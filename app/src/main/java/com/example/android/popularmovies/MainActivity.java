package com.example.android.popularmovies;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
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
import com.example.android.popularmovies.parcelables.MovieParcelable;
import com.example.android.popularmovies.utilities.JsonUtils;
import com.example.android.popularmovies.utilities.NetworkUtils;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity  {


    //TODO Clean up Log.i statements
    private ProgressBar mLoadingIndicator;

    private final String byMostPopular = "popular";

    //ArrayList<HashMap<String, String>> mParsedData; //Array to hold parsed data from tmdb
    ArrayList<MovieParcelable> mParsedData; //Array to hold parsed data from tmdb

    SQLiteDatabase pmDB;
    //TaskLoader unique identifier

    private static final int MOVIE_QUERY_LOADER = 22;
    private static final int FAVORITES_READ_LOADER = 44;
    private static final String MOVIE_QUERY_URL_EXTRA = "query";

    private MovieAdapter mMovieAdapter;
    private RecyclerView mMovieImage;
    private GridLayoutManager layoutManager;

    private NetworkUtils networkUtils = new NetworkUtils(this);


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
        mMovieAdapter = new MovieAdapter(this, new ArrayList(), new MovieAdapter.movieAdapterClickListener() {
            @Override
            public void onClickMovieItem(HashMap<String, String> movieDetails, int position) {

                Class destinationClass = DetailsActivity.class;
                Intent intentDetailActivity = new Intent(MainActivity.this, destinationClass);

                // Pass the movie details to the intent extras
                for(Map.Entry m:movieDetails.entrySet()){
                    //System.out.println(m.getKey()+" "+m.getValue());
                    intentDetailActivity.putExtra(m.getKey().toString(),m.getValue().toString());
                }
                MainActivity.this.startActivity(intentDetailActivity);
            }
        });

        mMovieImage.setAdapter(mMovieAdapter);

        if (networkUtils.isNetworkAvailable(this)) {
            Log.i("isNetworkAvailable", "true");
            if (savedInstanceState == null || !savedInstanceState.containsKey("movies")) {
                Log.i("savedInstance:isNetwork", "isNull");
                makeMovieQuery(mSearchUrl.toString(), MOVIE_QUERY_LOADER);
            } else {
                Log.i("isNetworkAvailable", "false");
                mParsedData = savedInstanceState.getParcelableArrayList("movies");
                mMovieAdapter.setMovieList(mParsedData);
                mMovieAdapter.notifyDataSetChanged();
            }


        } else {
            Toast.makeText(this, "No Internet Connection",
                    Toast.LENGTH_LONG).show();

        }


    }



    /**
     * Instantiate and return a new Loader for the given ID.
     *
     * @param id   The ID whose loader is to be created.
     * @param args Any arguments supplied by the caller.
     * @return Return a new Loader instance that is ready to start loading.
     */

    @SuppressLint("StaticFieldLeak") //ignore Lint warning
    private LoaderManager.LoaderCallbacks<ArrayList> movieData = new LoaderManager.LoaderCallbacks<ArrayList>(){

        @Override
        public Loader<ArrayList> onCreateLoader(final int id, final Bundle args) {
            return new AsyncTaskLoader<ArrayList>(getApplicationContext()) {

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
                        Log.i("SearchResults: ", mSearchResults);
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


        @Override
        public void onLoadFinished(Loader<ArrayList> loader, ArrayList data) {
            mLoadingIndicator.setVisibility(View.INVISIBLE);

            Log.i("onLoadFinish: arySize", String.valueOf(data.size()));
            if (data != null && !data.equals("")) {
                //onSaveInstanceState();

                mMovieAdapter.setMovieList(data);
                mMovieAdapter.notifyDataSetChanged();


            }

        }


        @Override
        public void onLoaderReset(Loader<ArrayList> loader) {

        }
    };

    @SuppressLint("StaticFieldLeak") //ignore Lint warning
    private LoaderManager.LoaderCallbacks<ArrayList> favoritesData = new LoaderManager.LoaderCallbacks<ArrayList>() {
        @Override
        public Loader<ArrayList> onCreateLoader(final int id, final Bundle args) {
            return new AsyncTaskLoader<ArrayList>(getApplicationContext()) {

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

                        forceLoad();
                    }
                }

                @Override
                public ArrayList loadInBackground() {

                    String favoritesQueryUrlString = args.getString(MOVIE_QUERY_URL_EXTRA);
                    Log.i("LoadInBackground", favoritesQueryUrlString);
                    if (favoritesQueryUrlString == null || TextUtils.isEmpty(favoritesQueryUrlString)) {
                        return null;
                    }

                    switch (id) {
                        case FAVORITES_READ_LOADER:
                            //Query to determine if Movie is in favorites list;
                            String[] mProjection = {
                                    FavoritesContract.favoriteMovies.RELEASE_DATE,
                                    FavoritesContract.favoriteMovies.MOVIE_DESCRIPTION,
                                    FavoritesContract.favoriteMovies.MOVIE_TITLE,
                                    FavoritesContract.favoriteMovies.IMAGE_NAME,
                                    FavoritesContract.favoriteMovies.IMAGE_POSTER,
                                    FavoritesContract.favoriteMovies.USER_RATING,
                                    FavoritesContract.favoriteMovies.MOVIE_ID};
                            try {

                                Uri favorites_uri = Uri.parse(favoritesQueryUrlString);
                                Cursor favoriteMovies =  getContentResolver().query(favorites_uri,
                                        mProjection,
                                        null,
                                        null,
                                        null);


                                JSONArray favorite2Json = JsonUtils.favoritesJSON(favoriteMovies);
                                Log.i("favorite2Json: "," {\"page\":1,\"total_results\":"+ favoriteMovies.getCount() + ",\"total_pages\":1,\"results\":"
                                        + favorite2Json.toString()
                                        + "}");

                                String a = " {\"page\":1,\"total_results\":"+ favoriteMovies.getCount() + ",\"total_pages\":1,\"results\":"
                                        + favorite2Json.toString()
                                        + "}";

                                mParsedData = JsonUtils.getMovieDataFromJson(a);
                                return mParsedData;

                            } catch (Exception e) {
                                Log.e("LoadInBackground READ ", "Exception");
                                e.printStackTrace();
                                return null;
                            }

                    }


                    return null;
                }
                @Override
                public void deliverResult(ArrayList data) {
                    mParsedData = data;
                    Log.i("deliverResults", data.toString());
                    super.deliverResult(data);
                }


            };
        }

        @Override
        public void onLoadFinished(Loader<ArrayList> loader, ArrayList data) {
            mLoadingIndicator.setVisibility(View.INVISIBLE);

            Log.i("onLoadFinish: arySize", String.valueOf(data.size()));
            if (data != null && !data.equals("")) {
                //onSaveInstanceState();

                mMovieAdapter.setMovieList(data);
                mMovieAdapter.notifyDataSetChanged();



            }

        }

        @Override
        public void onLoaderReset(Loader<ArrayList> loader) {

        }
    };

    public void onGroupItemClick(MenuItem item) {

        final String byTopRated = "top_rated";




        int groupMenuItemClicked = item.getItemId();


        if (groupMenuItemClicked == R.id.action_sortby_popular) {
            //createSearchURL(byMostPopular, "1");

            mParsedData = null;
            URL mSearchUrl = NetworkUtils.buildUrl(byMostPopular, "1");

            Log.i("menuByPopular", mSearchUrl.toString());


            if (networkUtils.isNetworkAvailable(this)) {

                item.setChecked(true);

                //Pass url to query and fires off an AsyncTaskLoader
                makeMovieQuery(mSearchUrl.toString(), MOVIE_QUERY_LOADER);


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

            if (networkUtils.isNetworkAvailable(this)) {

                //Pass url to query and fires off an AsyncTaskLoader
                makeMovieQuery(mSearchUrl.toString(), MOVIE_QUERY_LOADER);

            } else {
                Toast.makeText(this, "No Internet Connection",
                        Toast.LENGTH_LONG).show();

            }
            //return true;
        } else if (groupMenuItemClicked == R.id.action_sortby_favorites) {

            mParsedData = null;

            item.setChecked(true);

            makeFavoritesQuery(FavoritesContract.favoriteMovies.CONTENT_FAVORITES_URI, FAVORITES_READ_LOADER);

            //DONE Get rid of network check since its pulling db data for favorites
            //if (networkUtils.isNetworkAvailable(this)) {


                //Pass url to query and fires off an AsyncTaskLoader
                //REMOVE -- makeSearchQuery(mSearchUrl.toString());
                //TODO add call to search favorites either update makeSearchQuery or create new method using content provider to db

                //IN LOADER
                //TODO integrate with content provide once built
                //TODO provider code needed in loader
                //Query Favorites for movie
/*            makeFavoritesQuery(ContentUris.withAppendedId(
                    FavoritesContract.favoriteMovies.CONTENT_FAVORITES_URI, Integer.valueOf(movie_id)),
                    FAVORITES_READ_LOADER);*/

                //TODO once data is retrieved from the provide, still in loader, convert to json
                //https://tech.sarathdr.com/convert-database-cursor-result-to-json-array-android-app-development-1b9702fc7bbb
                //**https://www.programcreek.com/java-api-examples/index.php?api=android.util.JsonWriter
                //***https://stackoverflow.com/questions/19277529/android-jsonwriter-can-not-write-to-a-file
                //***http://docs.huihoo.com/android/3.0/reference/android/util/JsonWriter.html
                //will need to add method to jsonutil to do the conversion

                //TODO Then process under normal json call to process favorites data
                // mParsedData = JsonUtils.getMovieDataFromJson(mSearchResults);


/*            } else {
                Toast.makeText(this, "No Internet Connection",
                        Toast.LENGTH_LONG).show();

            }*/
            //return true;
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
        if (menuItemClicked == R.id.action_settings) {
            Intent settingsActivity = new Intent(this, SettingsActivity.class);
            startActivity(settingsActivity);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /* This method constructs the URL
  * and Request that an AsyncTaskLoader performs the GET request.
  */
    private void makeMovieQuery(String url, int loaderID) {
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
            loaderManager.initLoader(loaderID, movieQueryBundle, movieData);
        } else {
            Log.i("movieSearchLoader", "notNull");
            loaderManager.restartLoader(loaderID, movieQueryBundle, movieData);
        }

    }

    private void makeFavoritesQuery(Uri uri, int loaderID) {

        // created bundle to store key:value for the URL
        Bundle bundle = new Bundle();
        bundle.putString(MOVIE_QUERY_URL_EXTRA, uri.toString());

        //get library for loadermanager
        LoaderManager loaderManager = getSupportLoaderManager();

        //call getLoader with loader id
        Loader<Cursor> favoritesLoader = loaderManager.getLoader(loaderID);

        //If the Loader was null, initialize it otherwise restart it
        if (favoritesLoader == null) {
            Log.i("makeFavoritesQuery", "favoritesLoader " + "isNull");
            loaderManager.initLoader(loaderID, bundle, favoritesData);
        } else {
            Log.i("makeFavoritesQuery", "favoritesLoader " + "notNull");
            loaderManager.restartLoader(loaderID, bundle, favoritesData);
        }


    }

}
