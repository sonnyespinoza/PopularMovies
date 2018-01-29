package com.example.android.popularmovies.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;




public class PopularMoviesContentProvider extends ContentProvider {

    
    // Member variable
    private PopularMoviesDBHelper mPopularMoviesDbHelper;


    // Integer constants for the directory of favorite movies and a single favorite movie item.
    // 100 - for directory
    // 101 - items in 100 directory.
    public static final int FAVORITES = 100;
    public static final int FAVORITES_WITH_ID = 101;

    // Static variable for the Uri matcher
    private static final UriMatcher sUriMatcher = buildUriMatcher();

    public static UriMatcher buildUriMatcher() {

        // Initialize a UriMatcher with NO_MATCH
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        // UriMatcher for directory
        uriMatcher.addURI(FavoritesContract.AUTHORITY, FavoritesContract.PATH_MOVIES, FAVORITES);

        // UriMatcher for single favorites item by ID
        uriMatcher.addURI(FavoritesContract.AUTHORITY, FavoritesContract.PATH_MOVIES + "/#", FAVORITES_WITH_ID);

        return uriMatcher;
    }

    @Override
    public boolean onCreate() {

        //initialize a PopularMoviesDBHelper
        Context context = getContext();
        mPopularMoviesDbHelper = new PopularMoviesDBHelper(context);
        return true;
    }


    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {

        final SQLiteDatabase db = mPopularMoviesDbHelper.getWritableDatabase();

        throw new UnsupportedOperationException("Not yet implemented");
    }


    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {

        throw new UnsupportedOperationException("Not yet implemented");
    }


    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {

        throw new UnsupportedOperationException("Not yet implemented");
    }


    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {

        throw new UnsupportedOperationException("Not yet implemented");
    }


    @Override
    public String getType(@NonNull Uri uri) {

        throw new UnsupportedOperationException("Not yet implemented");
    }

}
