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

import android.net.Uri;
import android.os.StrictMode;

import com.example.android.popularmovies.BuildConfig;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * These utilities will be used to communicate with the network.
 */
public class NetworkUtils {


    final static String API_VAR = "api_key";
    final static String API_KEY = BuildConfig.API_KEY;

    final static String BASE_URL = "https://api.themoviedb.org/3/movie/";
    final static String LANGUAGE_VAR = "language";
    final static String LANGUAGE = "en-US";
    final static String PAGE = "page";

    final static String TYPE_TRAILER = "trailer_list";
    final static String TYPE_REVIEWS = "review_list";

    /**
     * Builds the URL
     *
     * @param sortBy The sort criteria that will be queried for.
     * @param page   the page to return.
     * @return The URL to use to query.
     */
    public static URL buildUrl(String sortBy, String page) {
        Uri builtUri = Uri.parse(BASE_URL + sortBy).buildUpon()
                .appendQueryParameter(API_VAR, API_KEY)
                .appendQueryParameter(LANGUAGE_VAR, LANGUAGE)
                //.appendQueryParameter(PAGE, page)
                .build();

        //https://api.themoviedb.org/3/movie/popular?api_key=27870a6246d2191e0a996bb1e04e2cea&language=en-US

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    /**
     * Builds the URL
     *
     * @param id   used to specifiy which movie trailers
     * @param type the type of url to build (currently either trailer_list or review_list)
     *             * @param page   the page to return.
     * @return The URL to use to query.
     */

    public static URL buildUrl(String type, String id, String page) {

        Uri builtUri = null;
        URL url = null;
        switch (type) {
            case TYPE_TRAILER:
                builtUri = Uri.parse(BASE_URL + id + "/videos").buildUpon()
                        .appendQueryParameter(API_VAR, API_KEY)
                        .appendQueryParameter(LANGUAGE_VAR, LANGUAGE)
                        .build();
                try {
                    url = new URL(builtUri.toString());
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                break;

            case TYPE_REVIEWS:
                builtUri = Uri.parse(BASE_URL + id + "/reviews").buildUpon()
                        .appendQueryParameter(API_VAR, API_KEY)
                        .appendQueryParameter(LANGUAGE_VAR, LANGUAGE)
                        .build();
                try {
                    url = new URL(builtUri.toString());
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + builtUri);
        }
        return url;

        //examples
        //https://api.themoviedb.org/3/movie/254128/videos?api_key=27870a6246d2191e0a996bb1e04e2cea&language=en-US
        //https://api.themoviedb.org/3/movie/254128/reviews?api_key=27870a6246d2191e0a996bb1e04e2cea&language=en-US
    }

    /**
     * This method returns the entire result from the HTTP response.
     *
     * @param url The URL to fetch the HTTP response from.
     * @return The contents of the HTTP response.
     * @throws IOException Related to network and stream reading
     */
    public static String getResponseFromHttpUrl(URL url) throws IOException {

        //TODO - Re-move StrictMode once Trailer and reviews are moved off the main thread
        //added for testing of data retrieval on the main thread
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        //Log.d("getResponseFromHttpUrl:", "url: " +url.toString());
        try {
            InputStream in = urlConnection.getInputStream();
            //Log.d("getResponseFromHttpUrl:", in.toString());

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                //Log.d("getResponseFromHttpUrl:", "hasInput");
                return scanner.next();
            } else {
                //Log.d("getResponseFromHttpUrl:", "noInput");
                return null;
            }
        } finally {
            //Log.d("getResponseFromHttpUrl ", "finally");
            urlConnection.disconnect();
        }
    }
}