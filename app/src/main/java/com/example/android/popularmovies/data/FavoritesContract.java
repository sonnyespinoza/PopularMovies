package com.example.android.popularmovies.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by sonny on 1/22/18.
 */

public final class FavoritesContract {

    /* Content provider constants
    */
    // The authority, Content Provider to access
    public static final String AUTHORITY = "com.example.android.popularmovies";
    // The base content URI = "content://" + <authority>
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);
    // Path for accessing movie favorites data in this contract
    public static final String PATH_MOVIES = "favorites";


    private FavoritesContract(){

    }

    public static final class favoriteMovies implements BaseColumns {


            // favorites content URI = base content URI + path
            public static final Uri CONTENT_URI =
                    BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIES).build();

        /* Used internally as the name of our favorites movies table. */
        public static final String TABLE_NAME = "favorite_movies";

        /* Name of the image file */
        public static final String IMAGE_NAME = "backdrop_path";
        public static final String IMAGE_POSTER= "poster_path";

        /* Date the movie was released */
        public static final String RELEASE_DATE = "release_date";

        //Movie title and overview
        public static final String MOVIE_DESCRIPTION = "overview";
        public static final String MOVIE_TITLE = "title";

        //User movie rating
        public static final String USER_RATING = "vote_average";


    }
}
