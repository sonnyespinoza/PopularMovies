package com.example.android.popularmovies.data;

import android.provider.BaseColumns;

/**
 * Created by sonny on 1/22/18.
 */

public final class favoritesContract {

    private favoritesContract (){

    }

    public static final class favoriteMovies implements BaseColumns {

        /* Used internally as the name of our favorites movies table. */
        public static final String TABLE_NAME = "favoritemovies";

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
