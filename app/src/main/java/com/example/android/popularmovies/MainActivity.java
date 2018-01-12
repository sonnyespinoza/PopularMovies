package com.example.android.popularmovies;

import android.app.LoaderManager;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.android.popularmovies.utilities.JsonUtils;
import com.example.android.popularmovies.utilities.NetworkUtils;
import com.example.android.popularmovies.utilities.ParcelableUtils;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<ArrayList>{

    private ProgressBar mLoadingIndicator;

    private final String byMostPopular = "popular";

    //ArrayList<HashMap<String, String>> mParsedData; //Array to hold parsed data from tmdb
    ArrayList<ParcelableUtils> mParsedData; //Array to hold parsed data from tmdb

    //TaskLoader unique identifier
    private static final int MOVIE_QUERY_TASK = 22;
    private static final String MOVIE_QUERY_URL_EXTRA = "query";

    private RecyclerAdapter mAdapter;

    private RecyclerView mMovieImage;
    private GridLayoutManager layoutManager;

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putParcelableArrayList("movies", mParsedData);

    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        Log.i("onRestoreInstanceState", "onRestoreInstanceState");
        // Restore UI state from the savedInstanceState.
        // This bundle has also been passed to onCreate.
        super.onRestoreInstanceState(savedInstanceState);
        mParsedData = savedInstanceState.getParcelableArrayList("movies");

    }


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

        // Set Adapter
        mAdapter = new RecyclerAdapter(this, new ArrayList());
        mMovieImage.setAdapter(mAdapter);

        if (isNetworkAvailable()) {
            if (savedInstanceState == null || !savedInstanceState.containsKey("movies")) {

                // created bundle movieQueryBundle to store key:value for the URL
                Bundle movieQueryBundle = new Bundle();
                movieQueryBundle.putString(MOVIE_QUERY_URL_EXTRA, mSearchUrl.toString());

                //TODO 1. Clean up commented code
                //new MovieQueryTask().execute(mSearchUrl);
                //Toast.makeText(this, "mParsedData is null ", Toast.LENGTH_LONG).show();
            } else {
                mParsedData = savedInstanceState.getParcelableArrayList("movies");
                mAdapter.setMovieList(mParsedData);
                mAdapter.notifyDataSetChanged();
                //Toast.makeText(this, "mParsedData is NOT null " + mParsedData.get(0).getTitle(), Toast.LENGTH_LONG).show();
            }

        } else {
            Toast.makeText(this, "No Internet Connection",
                    Toast.LENGTH_LONG).show();

        }


    }


    /**
     * This method creates the movie search URL
     * (using {@link NetworkUtils}) for the tmDB  movie repository
     */
    private URL createSearchURL(String sortby, String page) {

        //Create url
        URL movieSearchUrl = NetworkUtils.buildUrl(sortby, page);
        Log.i("createSearchQuery", movieSearchUrl.toString());

        return movieSearchUrl;
    }

    /**
     * Instantiate and return a new Loader for the given ID.
     *
     * @param id   The ID whose loader is to be created.
     * @param args Any arguments supplied by the caller.
     * @return Return a new Loader instance that is ready to start loading.
     */
    @Override
    public Loader<ArrayList> onCreateLoader(int id, final Bundle args) {
        return new AsyncTaskLoader<ArrayList>(this) {

            @Override
            protected void onStartLoading() {
                super.onStartLoading();
                if (args == null){
                    return;
                }
                mLoadingIndicator.setVisibility(View.VISIBLE);
            }

            @Override
            public ArrayList loadInBackground() {
                String movieQueryUrlString = args.getString(MOVIE_QUERY_URL_EXTRA);
                if (movieQueryUrlString == null || TextUtils.isEmpty(movieQueryUrlString)){
                    return null;
                }
                String mSearchResults;

                try {

                    URL searchUrl = new URL(movieQueryUrlString);
                    //get search results
                    if (mParsedData == null) {
                        mSearchResults = NetworkUtils.getResponseFromHttpUrl(searchUrl);
                        //parse json
                        mParsedData = JsonUtils.getMovieDataFromJson(mSearchResults);
                    }
                    return mParsedData;


                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                    return null;
                }
            }
        };
    }

    /**
     * Called when a previously created loader has finished its load.
     * @param loader The Loader that has finished.
     * @param data   The data generated by the Loader.
     */
    @Override
    public void onLoadFinished(Loader<ArrayList> loader, ArrayList data) {

    }

    /**
     * Called when a previously created loader is being reset, and thus
     * making its data unavailable.  The application should at this point
     * re-move any references it has to the Loader's data.
     *
     * @param loader The Loader that is being reset.
     */
    @Override
    public void onLoaderReset(Loader<ArrayList> loader) {

    }


    public class MovieQueryTask extends AsyncTask<URL, Void, ArrayList> {


        @Override
        protected void onPreExecute() {
            //TODO 4. Clean up commented code
            //super.onPreExecute();
            //mLoadingIndicator.setVisibility(View.VISIBLE);
        }


        // perform the query. Return the results.
        @Override
        protected ArrayList doInBackground(URL... urls) {
            URL searchUrl = urls[0];
            String mSearchResults;

            try {


                //get search results
                if (mParsedData == null) {
                    mSearchResults = NetworkUtils.getResponseFromHttpUrl(searchUrl);
                    //parse json
                    mParsedData = JsonUtils.getMovieDataFromJson(mSearchResults);
                }


            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return mParsedData;
        }


        @Override
        protected void onPostExecute(ArrayList m_parsed_data) {
            mLoadingIndicator.setVisibility(View.INVISIBLE);

            Log.d("onPostExecute: arySize", String.valueOf(m_parsed_data.size()));
            if (m_parsed_data != null && !m_parsed_data.equals("")) {
                //onSaveInstanceState();

                mAdapter.setMovieList(m_parsed_data);
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

        final String byTopRated = "top_rated";
        int menuItemClicked = item.getItemId();

        if (menuItemClicked == R.id.action_sortby_popular) {
            //createSearchURL(byMostPopular, "1");

            mParsedData = null;
            URL mSearchUrl = createSearchURL(byMostPopular, "1");
            Log.i("menuByPopular", mSearchUrl.toString());


            if (isNetworkAvailable()) {

                /**
                 *Pass url to query and fires off an AsyncTask
                 *to perform the GET request using
                 * {@link MovieQueryTask}
                 */
                //TODO 2. Clean up commented code
                //new MovieQueryTask().execute(mSearchUrl);

            } else {
                Toast.makeText(this, "No Internet Connection",
                        Toast.LENGTH_LONG).show();

            }
            return true;
        } else if (menuItemClicked == R.id.action_sortby_rating) {

            mParsedData = null;
            URL mSearchUrl = createSearchURL(byTopRated, "1");
            Log.i("menuByRating", mSearchUrl.toString());

            if (isNetworkAvailable()) {

                /**
                 *Pass url to query and fires off an AsyncTask
                 *to perform the GET request using
                 * {@link MovieQueryTask}
                 */
                //TODO 3. Clean up commented code
                //new MovieQueryTask().execute(mSearchUrl);

            } else {
                Toast.makeText(this, "No Internet Connection",
                        Toast.LENGTH_LONG).show();

            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //check for network connection
    private boolean isNetworkAvailable() {
        boolean isConnected = false;
        try {

            ConnectivityManager cm =
                    (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            isConnected = activeNetwork != null &&
                    activeNetwork.isConnectedOrConnecting();


        } catch (Exception e) {
            e.printStackTrace();

        }
        return isConnected;

    }
}
