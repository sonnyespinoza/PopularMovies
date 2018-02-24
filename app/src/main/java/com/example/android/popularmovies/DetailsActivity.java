package com.example.android.popularmovies;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.popularmovies.adapters.TrailerAdapter;
import com.example.android.popularmovies.data.FavoritesContract;
import com.example.android.popularmovies.parcelables.TrailerParcelable;
import com.example.android.popularmovies.utilities.JsonUtils;
import com.example.android.popularmovies.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class DetailsActivity extends AppCompatActivity {

    //TODO clean up commented code prior to submission
    //TaskLoader unique identifier
    //private int FAVORITES_LOADER;
    private static final int FAVORITES_READ_LOADER = 22;
    private static final int FAVORITES_DELETE_LOADER = 44;
    private static final int FAVORITES_CREATE_LOADER = 66;
    private static final int TRAILER_READ_LOADER = 77;
    private static final int REVIEWS_READ_LOADER = 99;


    private static final String CRUD_URL_EXTRA = "crud";
    //private static final String FAVORITES_CRUD_URL_EXTRA = "crud";
    //private static final String FAVORITES_READ_URL_EXTRA = "query";
    //private static final String FAVORITES_DELETE_URL_EXTRA = "delete";

    //private List<MovieParcelable> movie;
    private Context context;
    //Uri uri;
    boolean isFavorite = false;
    String title;
    String release_date;
    String user_rating;
    String movie_image;
    String movie_desc;
    String movie_id;
    String[] mSelectionArgs = {""};

    ArrayList<TrailerParcelable> mTrailerData; //Array to hold parsed data from tmdb


    private TrailerAdapter mTrailerAdapter;
    private RecyclerView mTrailerRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private static final String TrailerDataKey = "trailer";


    //TODO refactor so that you ensure this obj is destroyed after use
    private NetworkUtils networkUtils = new NetworkUtils(this);


    /*
     * onClickAddFavorite is called when the favorites button is click
     */
    public void onClickAddFavorite(View view) {

        if (isFavorite) { //Re-move data from favorites

            makeFavoritesQuery(ContentUris.withAppendedId(
                    FavoritesContract.favoriteMovies.CONTENT_FAVORITES_URI, Integer.valueOf(movie_id)),
                    FAVORITES_DELETE_LOADER);

        } else { //Added the data to favorites

            makeFavoritesQuery(FavoritesContract.favoriteMovies.CONTENT_FAVORITES_URI, FAVORITES_CREATE_LOADER);
        }
        isFavorite = !isFavorite;

    }


    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {

        Log.i("onSaveInstanceState", "yes");
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putParcelableArrayList(TrailerDataKey, mTrailerData);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        Log.i("onRestoreInstanceState", "yes");
        // Restore UI state from the savedInstanceState.
        // This bundle has also been passed to onCreate.
        super.onRestoreInstanceState(savedInstanceState);
        mTrailerData = savedInstanceState.getParcelableArrayList(TrailerDataKey);
        //mLoadingIndicator.setVisibility(View.INVISIBLE);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_details);

        mTrailerRecyclerView = (RecyclerView) findViewById(R.id.rv_movie_trailers);
        mTrailerRecyclerView.setHasFixedSize(true);

        Log.d("mTrailerRecyclerView", mTrailerRecyclerView.toString());
        mLayoutManager = new LinearLayoutManager(this);
        Log.d("mLayoutManager", mLayoutManager.toString());
        mTrailerRecyclerView.setLayoutManager(mLayoutManager);

        // Set Adapter
        mTrailerAdapter = new TrailerAdapter(this, new ArrayList());
        mTrailerRecyclerView.setAdapter(mTrailerAdapter);


        Intent intent = getIntent();

        title = intent.getStringExtra(this.getString(R.string.title));
        TextView tv_title = (TextView) findViewById(R.id.tv_detail_movie_title);
        tv_title.setText(title);

        release_date = intent.getStringExtra(this.getString(R.string.release_date));
        TextView tv_release_date = (TextView) findViewById(R.id.tv_release_date);
        tv_release_date.setText(release_date);


        user_rating = intent.getStringExtra(this.getString(R.string.user_rating));
        user_rating = user_rating + this.getString(R.string.details_max_user_rating);

        TextView tv_user_rating = (TextView) findViewById(R.id.tv_user_rating);
        tv_user_rating.setText(user_rating);


        //Picasso:Listen for loading errors
        context = getApplicationContext();
        Picasso.Builder builder = new Picasso.Builder(context);
        builder.listener(new Picasso.Listener() {
            @Override
            public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
                exception.printStackTrace();
            }
        });
        //Load images to image view
        movie_image = intent.getStringExtra(this.getString(R.string.image_poster));
        ImageView iv_img_backdrop = (ImageView) findViewById(R.id.iv_details_poster);
        builder.build().load(getResources().getString(R.string.image_url) + movie_image).into(iv_img_backdrop);

        movie_desc = intent.getStringExtra(this.getString(R.string.overview));
        TextView tv_movie_desc = (TextView) findViewById(R.id.tv_detail_movie_description);
        tv_movie_desc.setText(movie_desc);

        movie_id = intent.getStringExtra(this.getString(R.string.movie_id));

        URL mTrailerUrl = NetworkUtils.buildUrl("trailer_list", movie_id, "1");
        URL mReviewsUrl = NetworkUtils.buildUrl("review_list", movie_id, "1");
        Log.i("createTrailerURL", mTrailerUrl.toString());
        Log.i("createReviewURL", mReviewsUrl.toString());

        if (networkUtils.isNetworkAvailable(this)) {
            Log.i("isNetworkAvailable", "true");
            if (savedInstanceState == null || !savedInstanceState.containsKey(TrailerDataKey)) {
                Log.i("savedInstance:isNetwork", "isNull");
                makeMovieExtrasQuery(mTrailerUrl, TRAILER_READ_LOADER);
                //makeMovieExtrasQuery(mReviewsUrl, REVIEWS_READ_LOADER);
            } else {
                Log.i("isNetworkAvailable", "false");
                mTrailerData = savedInstanceState.getParcelableArrayList(TrailerDataKey);
                //mTrailerAdapter.
                mTrailerAdapter.setTrailList(mTrailerData);
                mTrailerAdapter.notifyDataSetChanged();
            }


        } else {
            Toast.makeText(this, "No Internet Connection",
                    Toast.LENGTH_LONG).show();

        }


/*
        //TODO need to re-move this is a test call to validate correctness of the URL
        URL mReviewUrl = NetworkUtils.buildUrl("review_list", "254128", "1");
        Log.i("createReviewURL", mReviewUrl.toString());
*/


        //Query Favorites for movie
        makeFavoritesQuery(ContentUris.withAppendedId(
                FavoritesContract.favoriteMovies.CONTENT_FAVORITES_URI, Integer.valueOf(movie_id)),
                FAVORITES_READ_LOADER);


    }


    @SuppressLint("StaticFieldLeak") //ignore Lint warning
    private LoaderManager.LoaderCallbacks<ArrayList> movieDataExtras = new LoaderManager.LoaderCallbacks<ArrayList>() {
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


                    if (mTrailerData != null) {
                        Log.i("onStartLoading", "mTrailerData Not Null");
                        deliverResult(mTrailerData);

                    } else {
                        Log.i("onStartLoading", "mTrailerData Null");
                        forceLoad();
                    }
                }

                @Override
                public ArrayList loadInBackground() {

                    String trailerQueryUrlString = args.getString(CRUD_URL_EXTRA);
                    Log.i("LoadInBackground", trailerQueryUrlString);
                    if (trailerQueryUrlString == null || TextUtils.isEmpty(trailerQueryUrlString)) {
                        return null;
                    }
                    String mSearchResults;

                    URL mTrailerUrl = networkUtils.buildUrl("trailer_list", movie_id, "1");
                    //Log.i("createTrailerURL", mTrailerUrl.toString());

                    //fetch data from API
                    try {
                        URL searchUrl = new URL(trailerQueryUrlString);

                        mSearchResults = networkUtils.getResponseFromHttpUrl(searchUrl);
                        //parse json
                        mTrailerData = JsonUtils.getTrailerDataFromJson(mSearchResults);
                        //}
                        return mTrailerData;

                    } catch (IOException | JSONException e) {
                        Log.e("LoadInBackground", "Exception");
                        e.printStackTrace();
                        return null;
                    }
                }

                @Override
                public void deliverResult(ArrayList data) {
                    mTrailerData = data;
                    Log.i("deliverResults", data.toString());
                    super.deliverResult(data);
                }


            };
        }


        @Override
        public void onLoadFinished(Loader<ArrayList> loader, ArrayList data) {

            Log.i("onLoadFinish: arySize", String.valueOf(data.size()));
            if (data != null && !data.equals("")) {
                //onSaveInstanceState();

                mTrailerAdapter.setTrailList(data);
                mTrailerAdapter.notifyDataSetChanged();
            }

        }

        @Override
        public void onLoaderReset(Loader<ArrayList> loader) {

        }
    };

    @SuppressLint("StaticFieldLeak") //ignore Lint warning
    private LoaderManager.LoaderCallbacks<Cursor> favoritesData = new LoaderManager.LoaderCallbacks<Cursor>() {
        @Override
        public Loader<Cursor> onCreateLoader(final int id, final Bundle args) {
            return new AsyncTaskLoader<Cursor>(getApplicationContext()) {

                @Override
                protected void onStartLoading() {

                    super.onStartLoading();
                    if (args == null) {
                        Log.i("onStartLoading", "null args");

                    } else {
                        forceLoad();
                    }
                }

                @Override
                public Cursor loadInBackground() {

                    String favoritesQueryUrlString = args.getString(CRUD_URL_EXTRA);
                    Log.i("LoadInBackground", favoritesQueryUrlString);
                    if (favoritesQueryUrlString == null || TextUtils.isEmpty(favoritesQueryUrlString)) {
                        return null;
                    }

                    switch (id) {
                        case FAVORITES_READ_LOADER:
                            //Query to determine if Movie is in favorites list;
                            String[] mProjection = {FavoritesContract.favoriteMovies.MOVIE_ID};
                            //mSelectionArgs[0] = movie_image;
                            mSelectionArgs[0] = movie_id;
                            try {

                                Uri favorites_uri = Uri.parse(favoritesQueryUrlString);
                                return getContentResolver().query(favorites_uri,
                                        mProjection,
                                        null,
                                        mSelectionArgs,
                                        null);
                            } catch (Exception e) {
                                Log.e("LoadInBackground READ ", "Exception");
                                e.printStackTrace();
                                return null;
                            }


                        case FAVORITES_DELETE_LOADER:

                            try {

                                Uri favorites_uri = Uri.parse(favoritesQueryUrlString);
                                getContentResolver().delete(favorites_uri, null, new String[]{String.valueOf(movie_id)});
                                Log.i("LoadInBackground", "FAVORITES_DELETE_LOADER");
                            } catch (Exception e) {

                                Log.e("LoadInBackground", "Exception");
                                e.printStackTrace();
                            }

                            break;

                        case FAVORITES_CREATE_LOADER:
                            ContentValues contentValues = new ContentValues();
                            contentValues.put(FavoritesContract.favoriteMovies.MOVIE_TITLE, title);
                            contentValues.put(FavoritesContract.favoriteMovies.RELEASE_DATE, release_date);
                            contentValues.put(FavoritesContract.favoriteMovies.USER_RATING, user_rating);
                            contentValues.put(FavoritesContract.favoriteMovies.USER_FAVORITES, "true");
                            contentValues.put(FavoritesContract.favoriteMovies.MOVIE_DESCRIPTION, movie_desc);
                            contentValues.put(FavoritesContract.favoriteMovies.IMAGE_POSTER, movie_image);
                            contentValues.put(FavoritesContract.favoriteMovies.MOVIE_ID, movie_id);

                            try {
                                Uri uri = Uri.parse(favoritesQueryUrlString);
                                getContentResolver().insert(uri, contentValues);
                                Log.i("LoadInBackground", "FAVORITES_CREATE_LOADER");

                            } catch (Exception e) {

                                Log.e("LoadInBackground", "Exception");
                                e.printStackTrace();
                            }

                            break;
                    }


                    return null;
                }


            };
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {


            ImageButton ButtonStar;
            switch (loader.getId()) {
                case FAVORITES_READ_LOADER:
                    ButtonStar = (ImageButton) findViewById(R.id.ib_favorite_button);
                    if (null == cursor) { //null == error

                        Log.e("onLoadFinished: ", "cursor: Error Occurred");

                    } else if (cursor.getCount() < 1) { //No record found

                        Log.i("onLoadFinished ", "FAVORITES_READ_LOADER: No Record Found");
                        ButtonStar.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), android.R.drawable.btn_star));

                    } else { //Record found

                        Log.i("onLoadFinished: ", "FAVORITES_READ_LOADER: Record Found");
                        ButtonStar.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), android.R.drawable.btn_star_big_on));
                        isFavorite = !isFavorite; //found true
                    }

                    break;

                case FAVORITES_DELETE_LOADER:

                    ButtonStar = (ImageButton) findViewById(R.id.ib_favorite_button);
                    ButtonStar.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), android.R.drawable.btn_star));
                    break;

                case FAVORITES_CREATE_LOADER:
                    ButtonStar = (ImageButton) findViewById(R.id.ib_favorite_button);
                    ButtonStar.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), android.R.drawable.btn_star_big_on));
                    break;
            }

        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {

        }
    };

    /* This method constructs the URL
         * and Request that an AsyncTaskLoader performs the GET request.
         */
    private void makeFavoritesQuery(Uri uri, int loaderID) {

        // created bundle to store key:value for the URL
        Bundle bundle = new Bundle();
        bundle.putString(CRUD_URL_EXTRA, uri.toString());

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

    private void makeMovieExtrasQuery(URL url, int loaderID) {

        // created bundle to store key:value for the URL
        Bundle bundle = new Bundle();
        bundle.putString(CRUD_URL_EXTRA, url.toString());

        //get library for loadermanager
        LoaderManager loaderManager = getSupportLoaderManager();

        switch (loaderID) {
            case TRAILER_READ_LOADER:
                Loader<ArrayList> trailerLoader = loaderManager.getLoader(loaderID);

                //If the Loader was null, initialize it otherwise restart it
                if (trailerLoader == null) {
                    Log.i("makeMovieExtrasQuery", "trailerLoader " + "isNull");
                    loaderManager.initLoader(loaderID, bundle, movieDataExtras);
                } else {
                    Log.i("makeMovieExtrasQuery", "trailerLoader " + "notNull");
                    loaderManager.restartLoader(loaderID, bundle, movieDataExtras);
                }
                break;
            case REVIEWS_READ_LOADER:
                Loader<ArrayList> reviewsLoader = loaderManager.getLoader(loaderID);

                //If the Loader was null, initialize it otherwise restart it
                if (reviewsLoader == null) {
                    Log.i("makeWIPQuery", "reviewsLoader " + "isNull");
                    loaderManager.initLoader(loaderID, bundle, movieDataExtras);
                } else {
                    Log.i("makeWIPQuery", "reviewsLoader " + "notNull");
                    loaderManager.restartLoader(loaderID, bundle, movieDataExtras);
                }
                break;
        }

/*
        //call getLoader with loader id
        Loader<Cursor> favoritesLoader = loaderManager.getLoader(loaderID);

        //If the Loader was null, initialize it otherwise restart it
        if (favoritesLoader == null) {
            Log.i("makeQuery", "favoritesLoader "+ "isNull");
            loaderManager.initLoader(loaderID, favoritesBundle, favoritesData);
        } else {
            Log.i("makeQuery", "favoritesLoader "+"notNull");
            loaderManager.restartLoader(loaderID, favoritesBundle, favoritesData);
        }*/

    }
}

