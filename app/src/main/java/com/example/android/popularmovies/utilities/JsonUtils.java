/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.popularmovies.utilities;

import android.content.ContentValues;
import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Utility functions to handle tmDB NMovie data JSON data.
 */
public final class JsonUtils {


    /**
     * This method parses JSON from a web response and returns an array of Strings
     * describing the each movie returned in the query.
     *
     * @param movieJsonStr JSON response from server
     * @return Array of Strings describing weather data
     * @throws JSONException If JSON data cannot be properly parsed
     */
    public static ArrayList<HashMap<String, String>> getMovieDataFromJson(String movieJsonStr)
            throws JSONException {

        /* The "list" array of movie results*/
        final String RESULTS = "results";

        /* Name of the image file */
        final String IMAGE_NAME = "backdrop_path";
        final String IMAGE_POSTER= "poster_path";

        /* Date the movei was released */
        final String RELEASE_DATE = "release_date";

        //Movie title and overview
        final String MOVIE_DESCRIPTION = "overview";
        final String MOVIE_TITLE = "title";

        //return status code
        final String MOVIE_STATUS_CODE = "status_code";

        //Array List to load Movie JSON Data
        ArrayList<HashMap<String, String>> movieData;
        movieData = new ArrayList<>();


        if (movieJsonStr != null) {

            try {

                JSONObject movieJson = new JSONObject(movieJsonStr);

                //JSON Array node
                JSONArray movieJSONArray = movieJson.getJSONArray(RESULTS);



                /* Is there an error? */
                if (movieJson.has(MOVIE_STATUS_CODE)) {
                    int errorCode = movieJson.getInt(MOVIE_STATUS_CODE);

                    //TODO update with actual code from https://www.themoviedb.org/documentation/api/status-codes
                    //TODO add code description as a log in the case statements
                    switch (errorCode) {
                        case HttpURLConnection.HTTP_OK:
                            break;
                        case HttpURLConnection.HTTP_NOT_FOUND:
                    /* Location invalid */
                            return null;
                        default:
                    /* Server probably down */
                            return null;
                    }
                } else {

                    //Load movies into movie data array

                    for (int i = 0; i < movieJSONArray.length(); i++) {

                        JSONObject movieInfo = movieJSONArray.getJSONObject(i);

                        String image = movieInfo.getString(IMAGE_NAME);
                        Log.d("JSON: getStringBckDrp", movieInfo.getString(IMAGE_NAME)); //REMOVE
                        String image_poster = movieInfo.getString(IMAGE_POSTER);
                        Log.d("JSON: getStringPoster", movieInfo.getString(IMAGE_POSTER)); //REMOVE
                        String title = movieInfo.getString(MOVIE_TITLE);
                        String description = movieInfo.getString(MOVIE_DESCRIPTION);
                        String releaseDate = movieInfo.getString(RELEASE_DATE);

                        //hash map for pre load of movie info
                        HashMap<String, String> mInfo = new HashMap<>();

                        //Load movie info into hash map
                        mInfo.put(IMAGE_NAME, image);
                        mInfo.put(IMAGE_POSTER, image_poster);
                        mInfo.put(MOVIE_TITLE, title);
                        mInfo.put(MOVIE_DESCRIPTION, description);
                        mInfo.put(RELEASE_DATE, releaseDate);


                        //add movie info to movie data array
                        movieData.add(mInfo);

                        //destory hashmap tmp object
                        mInfo=null;


                    }

                }
            } catch (final JSONException e) {
                e.printStackTrace();
            }
        }

        //return movie array data
        return movieData;
    }
}