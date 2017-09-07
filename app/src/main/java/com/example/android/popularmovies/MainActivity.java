package com.example.android.popularmovies;

import android.content.Context;
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

import com.example.android.popularmovies.utilities.JsonUtils;
import com.example.android.popularmovies.utilities.NetworkUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private ProgressBar mLoadingIndicator;

    private final String byMostPopular = "popularity.desc";
    private final String byTopRated = "vote_average.desc";

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

        /**
         *Pass url to query and fires off an AsyncTask
         *to perform the GET request using
         * {@link MovieQueryTask}
         */
        new MovieQueryTask().execute(mSearchUrl);

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

    //TODO need to create on click listener

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

            Log.d("onPostExecute: arySize", String.valueOf(m_parsed_data.size()) ) ; //REMOVE
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
        int menuItemClicked = item.getItemId();
        if (menuItemClicked == R.id.action_sortby_popular) {
            createSearchURL(byMostPopular, "1");

            URL mSearchUrl = createSearchURL(byMostPopular, "1");

            /**
             *Pass url to query and fires off an AsyncTask
             *to perform the GET request using
             * {@link MovieQueryTask}
             */
            new MovieQueryTask().execute(mSearchUrl);
            return true;
        } else if (menuItemClicked == R.id.action_sortby_rating) {

            //TODO need to understand why I have missing images
            URL mSearchUrl = createSearchURL(byTopRated, "1");

            /**
             *Pass url to query and fires off an AsyncTask
             *to perform the GET request using
             * {@link MovieQueryTask}
             */
            new MovieQueryTask().execute(mSearchUrl);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
