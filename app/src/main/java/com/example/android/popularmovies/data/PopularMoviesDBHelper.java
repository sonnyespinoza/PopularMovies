package com.example.android.popularmovies.data;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.popularmovies.data.FavoritesContract.favoriteMovies;

/**
 * Created by sonny on 1/23/18.
 */

public class PopularMoviesDBHelper extends SQLiteOpenHelper {

    // Database Info
    private static final String DATABASE_NAME = "popularMovies.db";
    private static final int DATABASE_VERSION = 1;

    // Create Favorites Table SQL String
    private static final String CREATE_FAVORITES_TABLE ="CREATE TABLE " + favoriteMovies.TABLE_NAME + " (" +

            favoriteMovies._ID                  + " INTEGER PRIMARY KEY AUTOINCREMENT," +

            favoriteMovies.IMAGE_NAME           + " TEXT,"                    +
            favoriteMovies.IMAGE_POSTER         + " TEXT,"                    +
            favoriteMovies.RELEASE_DATE         + " TEXT,"                    +
            favoriteMovies.MOVIE_DESCRIPTION    + " TEXT,"                    +
            favoriteMovies.MOVIE_TITLE          + " TEXT,"                    +
            favoriteMovies.USER_RATING          + " TEXT,"                    +
            favoriteMovies.MOVIE_ID             + " TEXT,"                    +
            favoriteMovies.USER_FAVORITES       + " TEXT,"                    +

            " UNIQUE (" + favoriteMovies._ID + ") ON CONFLICT REPLACE);";


    PopularMoviesDBHelper(Context context) {
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {




        db.execSQL(CREATE_FAVORITES_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        if (oldVersion != newVersion) {
            // TEMP solution drop all old table and recreate
            db.execSQL("DROP TABLE IF EXISTS " + favoriteMovies.TABLE_NAME);
            onCreate(db);
        }

    }
}
