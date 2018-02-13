package com.example.android.popularmovies;

import android.annotation.SuppressLint;
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
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.popularmovies.data.FavoritesContract;
import com.squareup.picasso.Picasso;

public class DetailsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    //TaskLoader unique identifier
    private int FAVORITES_LOADER;
    private static final int FAVORITES_READ_LOADER = 22;
    private static final int FAVORITES_DELETE_LOADER = 44;
    private static final int FAVORITES_CREATE_LOADER = 66;


    private static final String FAVORITES_CRUD_URL_EXTRA = "crud";
    //private static final String FAVORITES_READ_URL_EXTRA = "query";
    //private static final String FAVORITES_DELETE_URL_EXTRA = "delete";

    //private List<ParcelableUtils> movie;
    private Context context;
    Uri uri;
    boolean isFavorite = false;
    String title;
    String release_date;
    String user_rating;
    String movie_image;
    String movie_desc;
    String[] mSelectionArgs = {""};


    /*
     * onClickAddFavorite is called when the favorites button is click
     */
    public void onClickAddFavorite(View view) {

        //ImageButton ButtonStar = (ImageButton) findViewById(R.id.ib_favorite_button);
        //ContentValues contentValues = new ContentValues();


        if (isFavorite) { //if true re-move data from favorites

            //ButtonStar.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), android.R.drawable.btn_star));
            //getContentResolver().delete(FavoritesContract.favoriteMovies.CONTENT_IMAGE_URI, null, new String[]{String.valueOf(movie_image)});

            makeQuery(FavoritesContract.favoriteMovies.CONTENT_IMAGE_URI, FAVORITES_DELETE_LOADER);

        } else { //otherwise added the data to favorites
            //ButtonStar.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), android.R.drawable.btn_star_big_on));
/*            contentValues.put(FavoritesContract.favoriteMovies.MOVIE_TITLE, title);
            contentValues.put(FavoritesContract.favoriteMovies.RELEASE_DATE, release_date);
            contentValues.put(FavoritesContract.favoriteMovies.USER_RATING, user_rating);
            contentValues.put(FavoritesContract.favoriteMovies.USER_FAVORITES, "true");
            contentValues.put(FavoritesContract.favoriteMovies.MOVIE_DESCRIPTION, movie_desc);
            contentValues.put(FavoritesContract.favoriteMovies.IMAGE_POSTER, movie_image);*/

/*            try {
                uri = getContentResolver().insert(FavoritesContract.favoriteMovies.CONTENT_FAVORITES_URI, contentValues);
            } catch (Exception e) {

                Log.e("onClickAddFavorite", "insert failed: ", e);
            }*/

            //Query Favorites for movie
            makeQuery(FavoritesContract.favoriteMovies.CONTENT_IMAGE_URI, FAVORITES_CREATE_LOADER);
        }
        isFavorite = !isFavorite;

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        Intent intent = getIntent();

        title = intent.getStringExtra(this.getString(R.string.title));
        //this.setTitle(title);
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

        //Query Favorites for movie
        makeQuery(FavoritesContract.favoriteMovies.CONTENT_IMAGE_URI, FAVORITES_READ_LOADER);

    }

    @SuppressLint("StaticFieldLeak") //ignore Lint warning
    @Override
    public  Loader<Cursor> onCreateLoader(final int id, final Bundle args) {
        return new AsyncTaskLoader<Cursor>(this) {

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

                String favoritesQueryUrlString = args.getString(FAVORITES_CRUD_URL_EXTRA);
                Log.i("LoadInBackground", favoritesQueryUrlString);
                if (favoritesQueryUrlString == null || TextUtils.isEmpty(favoritesQueryUrlString)) {
                    return null;
                }

                switch(id) {
                    case FAVORITES_READ_LOADER:
                        //Query to determine if Movie is in favorites list;
                        String[] mProjection = {FavoritesContract.favoriteMovies._ID};
                        mSelectionArgs[0] = movie_image;
                        try {

                            Uri uri = Uri.parse(favoritesQueryUrlString);
                            return getContentResolver().query(uri,
                                    mProjection,
                                    null,
                                    mSelectionArgs,
                                    null);
                        } catch (Exception e) {
                            Log.e("LoadInBackground", "Exception");
                            e.printStackTrace();
                            return null;
                        }


                    case FAVORITES_DELETE_LOADER:

                        try {

                            getContentResolver().delete(FavoritesContract.favoriteMovies.CONTENT_IMAGE_URI, null, new String[]{String.valueOf(movie_image)});
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

                        try {

                            uri = getContentResolver().insert(FavoritesContract.favoriteMovies.CONTENT_FAVORITES_URI, contentValues);
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

        ImageButton ButtonStar = (ImageButton) findViewById(R.id.ib_favorite_button);
        switch(FAVORITES_LOADER) {
            case FAVORITES_READ_LOADER:

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

                ButtonStar.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), android.R.drawable.btn_star));
                break;

            case FAVORITES_CREATE_LOADER:

                ButtonStar.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), android.R.drawable.btn_star_big_on));
                break;
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    /* This method constructs the URL
         * and Request that an AsyncTaskLoader performs the GET request.
         */
    private void makeQuery(Uri uri, int loaderID) {

        FAVORITES_LOADER = loaderID;
        // created bundle movieQueryBundle to store key:value for the URL
        Bundle favoritesBundle = new Bundle();
        favoritesBundle.putString(FAVORITES_CRUD_URL_EXTRA, uri.toString());

        //get library for loadermanager
        LoaderManager loaderManager = getSupportLoaderManager();

        //call getLoader with loader id
        Loader<Cursor> favoritesLoader = loaderManager.getLoader(FAVORITES_LOADER);

        //If the Loader was null, initialize it otherwise restart it
        if (favoritesLoader == null) {
            Log.i("makeQuery", "favoritesLoader "+ "isNull");
            loaderManager.initLoader(FAVORITES_LOADER, favoritesBundle, this);
        } else {
            Log.i("makeQuery", "favoritesLoader "+"notNull");
            loaderManager.restartLoader(FAVORITES_LOADER, favoritesBundle, this);
        }

    }
}

