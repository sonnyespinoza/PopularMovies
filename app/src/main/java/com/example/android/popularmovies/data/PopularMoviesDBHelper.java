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

                /*
                 */
            favoriteMovies._ID               + " INTEGER PRIMARY KEY AUTOINCREMENT, INTEGER NOT NULL," +

            favoriteMovies.IMAGE_NAME        + " TEXT"                             +
            favoriteMovies.IMAGE_POSTER        + " TEXT NOT NULL"                             +
            favoriteMovies.RELEASE_DATE      + " INTEGER NOT NULL, "                 +

            favoriteMovies.MOVIE_DESCRIPTION        + " TEXT"                             +
            favoriteMovies.MOVIE_TITLE        + " TEXT NOT NULL"                             +
            favoriteMovies.USER_RATING        + " TEXT"                             +

            " UNIQUE (" + favoriteMovies._ID + ") ON CONFLICT REPLACE);";

    //Alter Favorites Table SQL String
    //TODO WIP
    //rename
    private static final String RENAME_FAVORITES_TABLE = "ALTER TABLE " + favoriteMovies.TABLE_NAME + "TO HOLD_FAVORITES";

    //restore favorites TODO WIP
    private static final String INSERT_SAVED_FAVORITES =
            "INSERT INTO " + favoriteMovies.TABLE_NAME +
            " (col1, col2) "+
                    "SELECT col1, col2, col2 " +
                    "FROM   sourceTable";

    //Alter table cols
    //TODO validate if this is actually needed most likely it is not
    private static final String ALTER_FAVORITES_TABLE = "ALTER TABLE " + favoriteMovies.TABLE_NAME + " ADD COLUMN ";


    public PopularMoviesDBHelper(Context context) {
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

            //TODO update existing schema and retain data WIP
            //db.execSQL(RENAME_FAVORITES_TABLE);
            //onCreate(db);
            //db.execSQL(INSERT_SAVED_FAVORITES);
        }

    }
}
