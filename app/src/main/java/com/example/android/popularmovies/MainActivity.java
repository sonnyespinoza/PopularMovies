package com.example.android.popularmovies;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.android.popularmovies.utilities.JsonUtils;
import com.example.android.popularmovies.utilities.NetworkUtils;

import org.json.JSONException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private ProgressBar mLoadingIndicator;

    private final String byMostPopular = "popularity.desc";


    private RecyclerAdapter mAdapter;

    RecyclerView mMovieImage;
    GridLayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //progress bar indicator for loading movie images.
        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);

        //reference to recyclerview for displaying movie in a grid
        mMovieImage = (RecyclerView) findViewById(R.id.rv_movies);
        mMovieImage.setHasFixedSize(true);

        layoutManager = new GridLayoutManager(this, 2);
        mMovieImage.setLayoutManager(layoutManager);

        URL mSearchUrl = createSearchURL(byMostPopular, "1");



        if (isNetworkAvailable()){

            /**
             *Pass url to query and fires off an AsyncTask
             *to perform the GET request using
             * {@link MovieQueryTask}
             */
            new MovieQueryTask().execute(mSearchUrl);

        } else {
            Toast.makeText(this, "No Internet Connection",
                    Toast.LENGTH_LONG).show();

        }


        // Set Adapter
        mAdapter = new RecyclerAdapter(this, new ArrayList());
        mMovieImage.setAdapter(mAdapter);






    }



    /**
     * This method creates the movie search URL
     * (using {@link NetworkUtils}) for the tmDB  movie repository
     */
    private URL createSearchURL(String sortby, String page) {
        String sortBy = sortby;
        String Page = page;

        //Create url
        URL movieSearchUrl = NetworkUtils.buildUrl(sortBy, Page);
        Log.i("createSearchQuery", movieSearchUrl.toString());

        return movieSearchUrl;
    }


    public class MovieQueryTask extends AsyncTask<URL, Void, ArrayList> {

        ArrayList<HashMap<String, String>> mParsedData; //Array to hold parsed data from tmdb

        private Context context;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoadingIndicator.setVisibility(View.VISIBLE);
        }


        // perform the query. Return the results.
        @Override
        protected ArrayList doInBackground(URL... urls) {
            URL searchUrl = urls[0];
            String mSearchResults = null;

            try {
                //get search results
                mSearchResults = NetworkUtils.getResponseFromHttpUrl(searchUrl);

                //parse json
                mParsedData = JsonUtils.getMovieDataFromJson(mSearchResults);

            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return mParsedData;
        }


        @Override
        protected void onPostExecute(ArrayList m_parsed_data) {
            mLoadingIndicator.setVisibility(View.INVISIBLE);

            Log.d("onPostExecute: arySize", String.valueOf(m_parsed_data.size()) );
            if (m_parsed_data != null && !m_parsed_data.equals("")) {

                mAdapter.setMovieList( m_parsed_data);
                mAdapter.notifyDataSetChanged();
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        final String byTopRated = "vote_average.desc";
        int menuItemClicked = item.getItemId();

        if (menuItemClicked == R.id.action_sortby_popular) {
            createSearchURL(byMostPopular, "1");

            URL mSearchUrl = createSearchURL(byMostPopular, "1");


            if (isNetworkAvailable()){

                /**
                 *Pass url to query and fires off an AsyncTask
                 *to perform the GET request using
                 * {@link MovieQueryTask}
                 */
                new MovieQueryTask().execute(mSearchUrl);

            } else {
                Toast.makeText(this, "No Internet Connection",
                        Toast.LENGTH_LONG).show();

            }
            return true;
        } else if (menuItemClicked == R.id.action_sortby_rating) {

            URL mSearchUrl = createSearchURL(byTopRated, "1");

            if (isNetworkAvailable()){

                /**
                 *Pass url to query and fires off an AsyncTask
                 *to perform the GET request using
                 * {@link MovieQueryTask}
                 */
                new MovieQueryTask().execute(mSearchUrl);

            } else {
                Toast.makeText(this, "No Internet Connection",
                        Toast.LENGTH_LONG).show();

            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //check for network connection
    public boolean isNetworkAvailable() {
        boolean isConnected = false;
        try{

            ConnectivityManager cm =
                    (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            isConnected = activeNetwork != null &&
                    activeNetwork.isConnectedOrConnecting();


        }catch(Exception e){
            e.printStackTrace();

        }
        return isConnected;

    }
}
