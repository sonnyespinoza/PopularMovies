package com.example.android.popularmovies.data;

import android.content.ContentProvider;
import android.content.ContentUris;
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

        int match = sUriMatcher.match(uri);

        Uri returnUri;

        switch (match) {
            case FAVORITES:
                long id = db.insert(FavoritesContract.favoriteMovies.TABLE_NAME, null, values);
                if (id >0) {
                    //success
                    returnUri = ContentUris.withAppendedId(FavoritesContract.favoriteMovies.CONTENT_URI, id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri,null);

        return returnUri;

    }




    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {

        final SQLiteDatabase db = mPopularMoviesDbHelper.getReadableDatabase();

        int match = sUriMatcher.match(uri);

        Cursor rCursor;

        switch (match) {
            case FAVORITES:
                rCursor = db.query(FavoritesContract.favoriteMovies.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
                case FAVORITES_WITH_ID:
                String id = uri.getPathSegments().get(1);
                rCursor = db.query(FavoritesContract.favoriteMovies.TABLE_NAME,
                        projection,
                        selection,
                        new String[]{id},
                        null,
                        null,
                        sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        rCursor.setNotificationUri(getContext().getContentResolver(),uri);

        return rCursor;
    }


    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {

        final SQLiteDatabase db = mPopularMoviesDbHelper.getWritableDatabase();

        int match = sUriMatcher.match(uri);

        int favoritesDeleted;

        switch (match) {
            case FAVORITES_WITH_ID:


                String id = uri.getPathSegments().get(1);
                favoritesDeleted = db.delete(FavoritesContract.favoriteMovies.TABLE_NAME,
                        FavoritesContract.favoriteMovies._ID+"=?",new String[]{id});
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if(favoritesDeleted !=0){
            getContext().getContentResolver().notifyChange(uri, null);

        }

        return favoritesDeleted;
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
