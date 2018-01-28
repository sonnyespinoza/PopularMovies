package com.example.android.popularmovies.data;

import android.content.ContentValues;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class TestUtil {

    public static void insertFakeData(SQLiteDatabase db){
        if(db == null){
            return;
        }
        //create a list of fake guests
        List<ContentValues> list = new ArrayList<ContentValues>();

        ContentValues cv = new ContentValues();
        cv.put(FavoritesContract.favoriteMovies.IMAGE_NAME, "/xqjGKLwLZeujg4fiBTOqhZkoL31.jpg");
        cv.put(FavoritesContract.favoriteMovies.IMAGE_POSTER, "/zQsEi6096L7PvowV39dtdqdW16f.jpg");
        cv.put(FavoritesContract.favoriteMovies.MOVIE_DESCRIPTION, "Standalone version of the series pilot with an alternate, closed ending, produced for the European VHS market.");
        cv.put(FavoritesContract.favoriteMovies.MOVIE_TITLE, "Twin Peaks");
        cv.put(FavoritesContract.favoriteMovies.USER_RATING, "8.7");
        list.add(cv);



        cv = new ContentValues();
        cv.put(FavoritesContract.favoriteMovies.IMAGE_NAME, "/xqjGKLwLZeujg4fiBTOqhZkoL31.jpg");
        cv.put(FavoritesContract.favoriteMovies.IMAGE_POSTER, "/zQsEi6096L7PvowV39dtdqdW16f.jpg");
        cv.put(FavoritesContract.favoriteMovies.MOVIE_DESCRIPTION, "Standalone version of the series pilot with an alternate, closed ending, produced for the European VHS market.");
        cv.put(FavoritesContract.favoriteMovies.MOVIE_TITLE, "Twin Peaks2");
        cv.put(FavoritesContract.favoriteMovies.USER_RATING, "8.7");
        list.add(cv);

        cv = new ContentValues();
        cv.put(FavoritesContract.favoriteMovies.IMAGE_NAME, "/xqjGKLwLZeujg4fiBTOqhZkoL31.jpg");
        cv.put(FavoritesContract.favoriteMovies.IMAGE_POSTER, "/zQsEi6096L7PvowV39dtdqdW16f.jpg");
        cv.put(FavoritesContract.favoriteMovies.MOVIE_DESCRIPTION, "Standalone version of the series pilot with an alternate, closed ending, produced for the European VHS market.");
        cv.put(FavoritesContract.favoriteMovies.MOVIE_TITLE, "Twin Peaks3");
        cv.put(FavoritesContract.favoriteMovies.USER_RATING, "8.7");
        list.add(cv);

        cv = new ContentValues();
        cv.put(FavoritesContract.favoriteMovies.IMAGE_NAME, "/xqjGKLwLZeujg4fiBTOqhZkoL31.jpg");
        cv.put(FavoritesContract.favoriteMovies.IMAGE_POSTER, "/zQsEi6096L7PvowV39dtdqdW16f.jpg");
        cv.put(FavoritesContract.favoriteMovies.MOVIE_DESCRIPTION, "Standalone version of the series pilot with an alternate, closed ending, produced for the European VHS market.");
        cv.put(FavoritesContract.favoriteMovies.MOVIE_TITLE, "Twin Peaks4");
        cv.put(FavoritesContract.favoriteMovies.USER_RATING, "8.7");
        list.add(cv);

        cv = new ContentValues();
        cv.put(FavoritesContract.favoriteMovies.IMAGE_NAME, "/xqjGKLwLZeujg4fiBTOqhZkoL31.jpg");
        cv.put(FavoritesContract.favoriteMovies.IMAGE_POSTER, "/zQsEi6096L7PvowV39dtdqdW16f.jpg");
        cv.put(FavoritesContract.favoriteMovies.MOVIE_DESCRIPTION, "Standalone version of the series pilot with an alternate, closed ending, produced for the European VHS market.");
        cv.put(FavoritesContract.favoriteMovies.MOVIE_TITLE, "Twin Peaks5");
        cv.put(FavoritesContract.favoriteMovies.USER_RATING, "8.7");
        list.add(cv);

        //insert all guests in one transaction
        try
        {
            db.beginTransaction();
            //clear the table first
            db.delete (FavoritesContract.favoriteMovies.TABLE_NAME,null,null);
            //go through the list and add one by one
            for(ContentValues c:list){
                db.insert(FavoritesContract.favoriteMovies.TABLE_NAME, null, c);
            }
            db.setTransactionSuccessful();
        }
        catch (SQLException e) {
            //too bad :(
        }
        finally
        {
            db.endTransaction();
        }

    }
}