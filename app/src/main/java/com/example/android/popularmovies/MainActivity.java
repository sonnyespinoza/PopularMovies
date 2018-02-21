package com.example.android.popularmovies;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
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

import com.example.android.popularmovies.adapters.MovieAdapter;
import com.example.android.popularmovies.data.FavoritesContract;
import com.example.android.popularmovies.utilities.JsonUtils;
import com.example.android.popularmovies.parcelables.MovieParcelable;
import com.example.android.popularmovies.utilities.NetworkUtils;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<ArrayList> {


    //TODO Clean up Log.i statements
    private ProgressBar mLoadingIndicator;

    private final String byMostPopular = "popular";

    //ArrayList<HashMap<String, String>> mParsedData; //Array to hold parsed data from tmdb
    ArrayList<MovieParcelable> mParsedData; //Array to hold parsed data from tmdb

    SQLiteDatabase pmDB;
    //TaskLoader unique identifier
    private static final int MOVIE_QUERY_LOADER = 22;
    private static final String MOVIE_QUERY_URL_EXTRA = "query";

    private MovieAdapter mAdapter;

    private RecyclerView mMovieImage;
    private GridLayoutManager layoutManager;

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        Log.i("onSaveInstanceState", "yes");
        super.onSaveInstanceState(savedInstanceState);
        //TODO  add save instance state for sort menu selected see bookmark
        savedInstanceState.putParcelableArrayList("movies", mParsedData);

    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        Log.i("onRestoreInstanceState", "yes");
        // Restore UI state from the savedInstanceState.
        // This bundle has also been passed to onCreate.
        super.onRestoreInstanceState(savedInstanceState);
        mParsedData = savedInstanceState.getParcelableArrayList("movies");
        //mLoadingIndicator.setVisibility(View.INVISIBLE);

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

        URL mSearchUrl = NetworkUtils.buildUrl(byMostPopular, "1");
        Log.i("createSearchURL", mSearchUrl.toString());




        // Set Adapter
        mAdapter = new MovieAdapter(this, new ArrayList());
        mMovieImage.setAdapter(mAdapter);

        if (isNetworkAvailable()) {
            Log.i("isNetworkAvailable", "true");
            if (savedInstanceState == null || !savedInstanceState.containsKey("movies")) {
                Log.i("savedInstance:isNetwork", "isNull");
                makeSearchQuery(mSearchUrl.toString());
            } else {
                Log.i("isNetworkAvailable", "false");
                mParsedData = savedInstanceState.getParcelableArrayList("movies");
                mAdapter.setMovieList(mParsedData);
                mAdapter.notifyDataSetChanged();
            }

            //Initialize the loader with id
            //getSupportLoaderManager().initLoader(MOVIE_QUERY_LOADER, null, this);

        } else {
            Toast.makeText(this, "No Internet Connection",
                    Toast.LENGTH_LONG).show();

        }


    }

    /* This method constructs the URL
     * and Request that an AsyncTaskLoader performs the GET request.
     */
    private void makeSearchQuery(String url) {
        // created bundle movieQueryBundle to store key:value for the URL
        Bundle movieQueryBundle = new Bundle();
        movieQueryBundle.putString(MOVIE_QUERY_URL_EXTRA, url);

        //get library for loadermanager
        LoaderManager loaderManager = getSupportLoaderManager();

        //call getLoader with loader id
        Loader<ArrayList> movieSearchLoader = loaderManager.getLoader(MOVIE_QUERY_LOADER);

        //If the Loader was null, initialize it otherwise restart it
        if (movieSearchLoader == null) {
            Log.i("movieSearchLoader", "isNull");
            loaderManager.initLoader(MOVIE_QUERY_LOADER, movieQueryBundle, this);
        } else {
            Log.i("movieSearchLoader", "notNull");
            loaderManager.restartLoader(MOVIE_QUERY_LOADER, movieQueryBundle, this);
        }

    }


    /**
     * This method creates the movie search URL
     * (using {@link NetworkUtils}) for the tmDB  movie repository
     */
/*
    //DONE might need to move this method into the NetworkUtils so that you can reuse with trailer and reviews
    private URL createSearchURL(String sortby, String page) {

        //Create url
        URL movieSearchUrl = NetworkUtils.buildUrl(sortby, page);
        Log.i("createSearchURL", movieSearchUrl.toString());

        return movieSearchUrl;
    }
*/


    /**
     * Instantiate and return a new Loader for the given ID.
     *
     * @param id   The ID whose loader is to be created.
     * @param args Any arguments supplied by the caller.
     * @return Return a new Loader instance that is ready to start loading.
     */
    @SuppressLint("StaticFieldLeak")//ignore Lint warning
    @Override
    public Loader<ArrayList>  onCreateLoader(int id, final Bundle args) {
        return new AsyncTaskLoader<ArrayList>(this) {

            @Override
            protected void onStartLoading() {
                super.onStartLoading();
                if (args == null) {
                    Log.i("onStartLoading", "null args");

                    return;
                }
                Log.i("onStartLoading", "");



                if (mParsedData != null){
                    Log.i("onStartLoading", "parsedData Not Null");
                    //mLoadingIndicator.setVisibility(View.VISIBLE);
                    deliverResult(mParsedData);

                } else {
                    Log.i("onStartLoading", "parsed Data Null");
                    mLoadingIndicator.setVisibility(View.VISIBLE);
                   /* try {

                        //sleep 5 seconds
                        Thread.sleep(10000);

                        System.out.println("Sleeping...");

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }*/
                    forceLoad();
                }
            }

            @Override
            public ArrayList loadInBackground() {
                String movieQueryUrlString = args.getString(MOVIE_QUERY_URL_EXTRA);
                Log.i("LoadInBackground", movieQueryUrlString);
                if (movieQueryUrlString == null || TextUtils.isEmpty(movieQueryUrlString)) {
                    return null;
                }
                String mSearchResults;

                try {

                    URL searchUrl = new URL(movieQueryUrlString);
                    //get search results
                    //if (mParsedData == null) {
                    mSearchResults = NetworkUtils.getResponseFromHttpUrl(searchUrl);
                        //parse json
                    mParsedData = JsonUtils.getMovieDataFromJson(mSearchResults);
                    //}
                    return mParsedData;


                } catch (IOException | JSONException e) {
                    Log.e("LoadInBackground", "Exception");
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            public void deliverResult(ArrayList data) {
                mParsedData = data;
                Log.i("deliverResults", data.toString());
                super.deliverResult(data);
            }
        };
    }

    /**
     * Called when a previously created loader has finished its load.
     *
     * @param loader The Loader that has finished.
     * @param data   The data generated by the Loader.
     */
    @Override
    public void onLoadFinished(Loader<ArrayList> loader, ArrayList data) {

        mLoadingIndicator.setVisibility(View.INVISIBLE);

        Log.i("onLoadFinish: arySize", String.valueOf(data.size()));
        if (data != null && !data.equals("")) {
            //onSaveInstanceState();

            mAdapter.setMovieList(data);
            mAdapter.notifyDataSetChanged();
        }


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


    public void onGroupItemClick(MenuItem item) {

        final String byTopRated = "top_rated";
        final String byFavorites = "favorites";




        int groupMenuItemClicked = item.getItemId();


        if (groupMenuItemClicked == R.id.action_sortby_popular) {
            //createSearchURL(byMostPopular, "1");

            mParsedData = null;
            URL mSearchUrl = NetworkUtils.buildUrl(byMostPopular, "1");

            Log.i("menuByPopular", mSearchUrl.toString());


            if (isNetworkAvailable()) {

                item.setChecked(true);

                //Pass url to query and fires off an AsyncTaskLoader
                makeSearchQuery(mSearchUrl.toString());


            } else {

                Toast.makeText(this, "No Internet Connection",
                        Toast.LENGTH_LONG).show();

            }
            //return true;

        } else if (groupMenuItemClicked == R.id.action_sortby_rating) {

            mParsedData = null;
            URL mSearchUrl = NetworkUtils.buildUrl(byTopRated, "1");

            item.setChecked(true);
            Log.i("menuByRating", mSearchUrl.toString());

            if (isNetworkAvailable()) {

                //Pass url to query and fires off an AsyncTaskLoader
                makeSearchQuery(mSearchUrl.toString());

            } else {
                Toast.makeText(this, "No Internet Connection",
                        Toast.LENGTH_LONG).show();

            }
            //return true;
        } else if (groupMenuItemClicked == R.id.action_sortby_favorites) {

            mParsedData = null;



            //Remove test data
            //TestUtil.insertFakeData(pmDB);
            //Log.i("onGroupItemClick", " sort by favorites: insertFakeData");

            //TODO integrate with content provide once built
            Cursor cursor = getFavorites();
            //URL mSearchUrl = createSearchURL(byFavorites, "1");
            item.setChecked(true);
            //Log.i("menuByFavorites", mSearchUrl.toString());


            //TODO may need to get rid of network check since its pulling db data for favorites
            if (isNetworkAvailable()) {


                //Pass url to query and fires off an AsyncTaskLoader
                //REMOVE -- makeSearchQuery(mSearchUrl.toString());
                //TODO add call to search favorites either update makeSearchQuery or create new method using content provider to db

            } else {
                Toast.makeText(this, "No Internet Connection",
                        Toast.LENGTH_LONG).show();

            }
            //return true;
        }

    }

    private Cursor getFavorites (){

        return pmDB.query(FavoritesContract.favoriteMovies.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                FavoritesContract.favoriteMovies.USER_RATING);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int menuItemClicked = item.getItemId();
        if (menuItemClicked == R.id.action_settings) {
            Intent settingsActivity = new Intent(this, SettingsActivity.class);
            startActivity(settingsActivity);
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

            NetworkInfo activeNetwork = null;
            if (cm != null) {
                activeNetwork = cm.getActiveNetworkInfo();
            }
            isConnected = activeNetwork != null &&
                    activeNetwork.isConnectedOrConnecting();


        } catch (Exception e) {
            e.printStackTrace();

        }
        return isConnected;

    }
}
