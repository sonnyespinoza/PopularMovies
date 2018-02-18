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

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Utility functions to handle tmDB Movie data JSON data.
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
    public static ArrayList<ParcelableUtils> getMovieDataFromJson(String movieJsonStr)
            throws JSONException {

        /* The "list" array of movie results*/
        final String RESULTS = "results";

        /* Name of the image file */
        final String IMAGE_NAME = "backdrop_path";
        final String IMAGE_POSTER= "poster_path";

        /* Date the movie was released */
        final String RELEASE_DATE = "release_date";

        //Movie title and overview
        final String MOVIE_DESCRIPTION = "overview";
        final String MOVIE_TITLE = "title";

        //User movie rating
        final String USER_RATING = "vote_average";

        //return status code
        final String MOVIE_STATUS_CODE = "status_code";

        //movie id
        final String MOVIE_ID = "id";



        // INITIALIZE NEW ARRAYLIST AND POPULATE
        ArrayList<ParcelableUtils> movieData = new ArrayList<ParcelableUtils>();


        if (movieJsonStr != null) {

            try {


                JSONObject movieJson = new JSONObject(movieJsonStr);

                //JSON Array node
                JSONArray movieJSONArray = movieJson.getJSONArray(RESULTS);


                /* Is there an error? */
                if (movieJson.has(MOVIE_STATUS_CODE)) {

                    int errorCode = movieJson.getInt(MOVIE_STATUS_CODE);

                    Log.e("JSONUtil:Status Code", String.valueOf(MOVIE_STATUS_CODE) );
                    Log.e("JSONUtil:Message", movieJson.get("status_message").toString());
                    switch (errorCode) {
                        case 1:
                            break;
                        case 2:
                    /* Location invalid */
                            return null;
                        default:
                    /* Server probably down */
                            return null;
                    }
                } else {


                    for (int i = 0; i < movieJSONArray.length(); i++) {

                        JSONObject movieInfo = movieJSONArray.getJSONObject(i);

                        String image = movieInfo.getString(IMAGE_NAME);
                        //Log.i("backdrop_path", image );
                        String imagePoster = movieInfo.getString(IMAGE_POSTER);
                        String title = movieInfo.getString(MOVIE_TITLE);
                        String description = movieInfo.getString(MOVIE_DESCRIPTION);
                        String releaseDate = movieInfo.getString(RELEASE_DATE);
                        String userRating = movieInfo.getString(USER_RATING);
                        String id = movieInfo.getString(MOVIE_ID);


                        //hash map for pre load of movie info
/*
                        HashMap<String, String> mInfo = new HashMap<>();

                        //Load movie info into hash map
                        mInfo.put(IMAGE_NAME, image);
                        mInfo.put(IMAGE_POSTER, image_poster);
                        mInfo.put(MOVIE_TITLE, title);
                        mInfo.put(MOVIE_DESCRIPTION, description);
                        mInfo.put(RELEASE_DATE, releaseDate);
                        mInfo.put(USER_RATING, userRating);
*/


                        movieData.add(new ParcelableUtils(releaseDate, description, title, image, imagePoster, userRating, id));;

                        //add movie info to movie data array
                        //movieData.add(mInfo);





                    }

                }
            } catch (final JSONException e) {
                e.printStackTrace();

            }
        }
        Log.d("JsonUtils", "getMovieDataFromJson: " + movieData.size());

        //return movie array data
        return movieData;
    }

    public static ArrayList<ParcelableUtils> getTrailerDataFromJson(String trailerJsonStr)
            throws JSONException {

        /* The "list" array of trailer results*/
        final String RESULTS = "results";

        /* Name and Type of video  */
        final String TRAILER_NAME = "name";
        final String TRAILER_TYPE= "type";

        /* video key for call to youtube */
        final String TRAILER_KEY = "key";

        //what site is the video from
        // i.e. YouTube (will use to limit
            // to just YouTube for this project)
        final String TRAILER_SITE = "site";


        //trailer id
        final String TRAILER_ID = "id";

        //return status code
        final String TRAILER_STATUS_CODE = "status_code";



        // INITIALIZE NEW ARRAYLIST AND POPULATE
        ArrayList<ParcelableUtils> trailerData = new ArrayList<ParcelableUtils>();


        if (trailerJsonStr != null) {

            try {


                JSONObject movieJson = new JSONObject(trailerJsonStr);

                //JSON Array node
                JSONArray movieJSONArray = movieJson.getJSONArray(RESULTS);


                /* Is there an error? */
                if (movieJson.has(TRAILER_STATUS_CODE)) {

                    int errorCode = movieJson.getInt(TRAILER_STATUS_CODE);

                    Log.e("JSONUtil:Status Code", String.valueOf(TRAILER_STATUS_CODE) );
                    Log.e("JSONUtil:Message", movieJson.get("status_message").toString());
                    switch (errorCode) {
                        case 1:
                            break;
                        case 2:
                    /* Location invalid */
                            return null;
                        default:
                    /* Server probably down */
                            return null;
                    }
                } else {


                    for (int i = 0; i < movieJSONArray.length(); i++) {

                        JSONObject movieInfo = movieJSONArray.getJSONObject(i);

                        String trailerId = movieInfo.getString(TRAILER_ID);
                        Log.d("id", trailerId );
                        String trailerKey = movieInfo.getString(TRAILER_KEY);
                        String trailerName = movieInfo.getString(TRAILER_NAME);
                        String trailerSite = movieInfo.getString(TRAILER_SITE);
                        String trailerType = movieInfo.getString(TRAILER_TYPE);


                        //TODO #1 ParcelableUtils needs to have a constructor overload
                        // trailerData.add(new ParcelableUtils(trailerId, trailerKey, trailerName, trailerSite, trailerType));;
                    }

                }
            } catch (final JSONException e) {
                e.printStackTrace();

            }
        }
        Log.d("JsonUtils", "getTrailerDataFromJson: " + trailerData.size());

        //return trailer array data
        return trailerData;
    }
}