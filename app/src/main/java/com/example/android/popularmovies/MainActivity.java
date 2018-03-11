package com.example.android.popularmovies;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
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
import com.example.android.popularmovies.utilities.SpanColumns;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity  {

    private ProgressBar mLoadingIndicator;
    private final String byMostPopular = "popular";
    final String byTopRated = "top_rated";
    private String SEARCH_TYPE = byMostPopular;

    private int page = 1;
    int currentPage = page;
    private boolean isLoading = true;
    private int pastVisibleItems, visiableItemCount, totalItemCount, previousTotal = 0;
    private int viewThreshold = 15;


    ArrayList<MovieParcelable> mParsedData; //Array to hold parsed data from tmdb

    //SQLiteDatabase pmDB;
    //TaskLoader unique identifier



    private static final int MOVIE_QUERY_LOADER = 22;
    private static final int FAVORITES_READ_LOADER = 44;
    private static final String MOVIE_QUERY_URL_EXTRA = "query";

    private MovieAdapter mMovieAdapter;
    private RecyclerView mMovieImage;
    private GridLayoutManager layoutManager;

    private NetworkUtils networkUtils = new NetworkUtils(this);

    private SpanColumns spanColumns = new SpanColumns();


    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        Log.i("onSaveInstanceState", "yes");
        savedInstanceState.putParcelableArrayList("movies", mParsedData);
        super.onSaveInstanceState(savedInstanceState);
        //TODO  add save instance state for sort menu selected see bookmark
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        Log.i("onRestoreInstanceState", "yes");
        // Restore UI state from the savedInstanceState.
        super.onRestoreInstanceState(savedInstanceState);
        mParsedData = savedInstanceState.getParcelableArrayList("movies") ;




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

        int mNoOfColumns = spanColumns.calculateNoOfColumns(getApplicationContext());

        layoutManager = new GridLayoutManager(this, mNoOfColumns);
        mMovieImage.setLayoutManager(layoutManager);


        // Set Adapter
        mMovieAdapter = new MovieAdapter(this, new ArrayList(), new MovieAdapter.movieAdapterClickListener() {
            @Override
            public void onClickMovieItem(HashMap<String, String> movieDetails, int position) {

                Class destinationClass = DetailsActivity.class;
                Intent intentDetailActivity = new Intent(MainActivity.this, destinationClass);

                // Pass the movie details to the intent extras
                for(Map.Entry m:movieDetails.entrySet()){
                    intentDetailActivity.putExtra(m.getKey().toString(),m.getValue().toString());
                }
                MainActivity.this.startActivity(intentDetailActivity);
            }
        });

        mMovieImage.setAdapter(mMovieAdapter);

        mMovieImage.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                visiableItemCount = layoutManager.getChildCount();
                totalItemCount = layoutManager.getItemCount();
                pastVisibleItems = layoutManager.findFirstVisibleItemPosition();

                if (dy > 0) {
                    if (isLoading) {
                        if(totalItemCount>previousTotal){
                            isLoading = false;
                            previousTotal = totalItemCount;
                        }
                    }
                    if(!isLoading && (totalItemCount-visiableItemCount)<=(pastVisibleItems+viewThreshold)){
                        page++;
                        pageMovieImages(page);
                        isLoading = true;
                    }
                }
            }
        });

        if (networkUtils.isNetworkAvailable(this)) {
            Log.i("isNetworkAvailable", "true");

            if (savedInstanceState == null || !savedInstanceState.containsKey("movies")) {
                URL mSearchUrl = NetworkUtils.buildUrl(SEARCH_TYPE, String.valueOf(page));
                Log.i("createSearchURL", mSearchUrl.toString());

                Log.i("savedInstance:isNetwork", "isNull");
                makeMovieQuery(mSearchUrl.toString(), MOVIE_QUERY_LOADER);
            } else {

                Log.i("savedInstance:isNetwork", "Not Null");
                mParsedData = savedInstanceState.getParcelableArrayList("movies");
                mMovieAdapter.setMovieList(mParsedData);
                mMovieAdapter.notifyDataSetChanged();
            }
        } else {
            Log.i("isNetworkAvailable", "false");
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
    private LoaderManager.LoaderCallbacks<ArrayList> movieData = new LoaderManager.LoaderCallbacks<ArrayList>() {
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
                        if (currentPage==page){
                            deliverResult(mParsedData);
                        } else {
                            mLoadingIndicator.setVisibility(View.VISIBLE);
                            forceLoad();
                        }

                    } else {
                        Log.i("onStartLoading", "parsed Data Null");
                        mLoadingIndicator.setVisibility(View.VISIBLE);
                        //currentPage=page;
                        forceLoad();
                    }
                }

                @Override
                public ArrayList loadInBackground() {

                    String QueryUrlString = args.getString(MOVIE_QUERY_URL_EXTRA);
                    Log.i("LoadInBackground", QueryUrlString);
                    if (QueryUrlString == null || TextUtils.isEmpty(QueryUrlString)) {
                        return null;
                    }

                    switch (id) {
                        case FAVORITES_READ_LOADER:
                            String[] mProjection = {
                                    FavoritesContract.favoriteMovies.RELEASE_DATE,
                                    FavoritesContract.favoriteMovies.MOVIE_DESCRIPTION,
                                    FavoritesContract.favoriteMovies.MOVIE_TITLE,
                                    FavoritesContract.favoriteMovies.IMAGE_NAME,
                                    FavoritesContract.favoriteMovies.IMAGE_POSTER,
                                    FavoritesContract.favoriteMovies.USER_RATING,
                                    FavoritesContract.favoriteMovies.MOVIE_ID};
                            try {

                                Uri favorites_uri = Uri.parse(QueryUrlString);
                                Cursor favoriteMovies =  getContentResolver().query(favorites_uri,
                                        mProjection,
                                        null,
                                        null,
                                        null);


                                JSONArray favorite2Json = JsonUtils.favoritesJSON(favoriteMovies);

                                if(favoriteMovies.getCount() > 0){
                                    String favs = " {\"page\":1,\"total_results\":"+ favoriteMovies.getCount() + ",\"total_pages\":1,\"results\":"
                                            + favorite2Json.toString()
                                            + "}";
                                    mParsedData = JsonUtils.getMovieDataFromJson(favs);
                                    return mParsedData;
                                } else{

                                    Toast.makeText(getApplicationContext(),
                                            "No Favorites to display",
                                            Toast.LENGTH_SHORT).show();


                                }



                            } catch (Exception e) {
                                Log.e("LoadInBackground READ ", "Exception");
                                e.printStackTrace();
                                return null;
                            }
                        case MOVIE_QUERY_LOADER:
                            String mSearchResults;

                            try {

                                URL searchUrl = new URL(QueryUrlString);
                                //get search results
                                //if (mParsedData == null) {
                                mSearchResults = NetworkUtils.getResponseFromHttpUrl(searchUrl);
                                Log.i("SearchResults: ", mSearchResults);
                                //parse json
                                return  JsonUtils.getMovieDataFromJson(mSearchResults);
                                //}
                                //return mParsedData;


                            } catch (IOException | JSONException e) {
                                Log.e("LoadInBackground", "Exception");
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


            Log.i("onLoadFinish: arySize", String.valueOf(data.size()));
            Log.i("onLoadFinish: Size", String.valueOf(mParsedData.size()));
            if (data != null && !data.equals("")) {
                //onSaveInstanceState();

                if(currentPage<page){
                    mMovieAdapter.addToMovieList(data);
                    currentPage = page;
/*                    Toast.makeText(getApplicationContext(),
                            "Added new page",
                            Toast.LENGTH_SHORT).show();*/
                } else {
                    mMovieAdapter.setMovieList(data);
                }
                mLoadingIndicator.setVisibility(View.INVISIBLE);
                mMovieAdapter.notifyDataSetChanged();



            }

        }

        @Override
        public void onLoaderReset(Loader<ArrayList> loader) {

        }
    };

    public void onGroupItemClick(MenuItem item) {






        int groupMenuItemClicked = item.getItemId();


        if (groupMenuItemClicked == R.id.action_sortby_popular) {
            //createSearchURL(byMostPopular, "1");

            mParsedData = null;
            SEARCH_TYPE = byMostPopular;
            page  = 1;
            URL mSearchUrl = NetworkUtils.buildUrl(SEARCH_TYPE, String.valueOf(page));
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
            SEARCH_TYPE = byTopRated;
            page = 1;
            URL mSearchUrl = NetworkUtils.buildUrl(SEARCH_TYPE, String.valueOf(page));

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
        }

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

/*    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int menuItemClicked = item.getItemId();
        if (menuItemClicked == R.id.action_settings) {
            Intent settingsActivity = new Intent(this, SettingsActivity.class);
            startActivity(settingsActivity);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }*/

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
            //loaderManager.initLoader(loaderID, movieQueryBundle, movieData);
            loaderManager.initLoader(loaderID, movieQueryBundle, movieData);
        } else {
            Log.i("movieSearchLoader", "notNull");
            //loaderManager.restartLoader(loaderID, movieQueryBundle, movieData);
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
            loaderManager.initLoader(loaderID, bundle, movieData);
        } else {
            Log.i("makeFavoritesQuery", "favoritesLoader " + "notNull");
            loaderManager.restartLoader(loaderID, bundle, movieData);
        }


    }

    private void pageMovieImages(Integer page ){

        //SEARCH_TYPE = searchType;
        //this.page = page;

        URL mSearchUrl = NetworkUtils.buildUrl(SEARCH_TYPE, String.valueOf(page));

        if (networkUtils.isNetworkAvailable(this)) {
            Log.i("isNetworkAvailable", "true");
            makeMovieQuery(mSearchUrl.toString(), MOVIE_QUERY_LOADER);
        } else {
            Log.i("isNetworkAvailable", "false");
            Toast.makeText(this, "No Internet Connection",
                    Toast.LENGTH_LONG).show();
        }

    }

}
