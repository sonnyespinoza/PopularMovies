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
    final static String API_KEY = "27870a6246d2191e0a996bb1e04e2cea"; // TODO: Remove before posting to gethub

    final static String BASE_URL = "https://api.themoviedb.org/3/discover/movie";

    final static String SORT_VAR = "sort_by";
    final static String LANGUAGE_VAR = "language";
    final static String LANGUAGE = "en-US";
    final static String INCLUDE_ADULT_VAR = "include_adult";
    final static String INCLUDE_ADULT = "false";
    final static String INCLUDE_VIDEO_VAR = "include_video";
    final static String INCLUDE_VIDEO = "false";



    /**
     * Builds the URL used to query GitHub.
     *
     * @param sortBy The sort criteria that will be queried for.
     * @param page the page to return.
     * @return The URL to use to query the GitHub.
     */
    public static URL buildUrl(String sortBy, String page ) {
        Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                .appendQueryParameter(API_VAR, API_KEY)
                .appendQueryParameter(LANGUAGE_VAR, LANGUAGE)
                .appendQueryParameter(SORT_VAR, sortBy)
                .appendQueryParameter(INCLUDE_ADULT_VAR, INCLUDE_ADULT )
                .appendQueryParameter(INCLUDE_VIDEO_VAR, INCLUDE_VIDEO )
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    /**
     * This method returns the entire result from the HTTP response.
     *
     * @param url The URL to fetch the HTTP response from.
     * @return The contents of the HTTP response.
     * @throws IOException Related to network and stream reading
     */
    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }
}