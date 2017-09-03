package com.example.android.popularmovies;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.example.android.popularmovies.utilities.JsonUtils;
import com.example.android.popularmovies.utilities.NetworkUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private ProgressBar mLoadingIndicator;

    private final String byMostPopular = "popularity.desc";
    private final String byTopRated = "vote_average.desc";

    RecyclerAdapter mAdapter;

    RecyclerView mMovieImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //progress bar indicator for loading movie images.
        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);

        //reference to recyclerview for displaying movie grid
        mMovieImage = (RecyclerView) findViewById(R.id.rv_movies);

        LinearLayoutManager layoutManager;

        mMovieImage.setLayoutManager(new LinearLayoutManager(this));

        mMovieImage.setHasFixedSize(true);

        mAdapter = new RecyclerAdapter(4);

        mMovieImage.setAdapter(mAdapter);




    }

    /**
     * This method retrieves the movie search by sort criteria, constructs the URL
     * (using {@link NetworkUtils}) for the tmDB  movie repository
     * and fires off an AsyncTask to perform the GET request using
     * {@link MovieQueryTask}
     */
    private void makeTmdbSearchQuery(String sortby, String page) {
        String sortBy = sortby;
        String Page = page;
        //mSearchBoxEditText.getText().toString(); //TODO Remove
        URL movieSearchUrl = NetworkUtils.buildUrl(sortBy, Page);
        //mUrlDisplayTextView.setText(githubSearchUrl.toString()); //TODO Remove
        Log.i("makeTmdbSearchQuery", movieSearchUrl.toString());

        //Passing in the url to query
        new MovieQueryTask().execute(movieSearchUrl);
    }

    public class MovieQueryTask  extends AsyncTask<URL, Void, String[]> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoadingIndicator.setVisibility(View.VISIBLE);
        }

        // perform the query. Return the results.
        @Override
        protected String[] doInBackground(URL... urls) {
            URL searchUrl = urls[0];
            String movieSearchResults = null;
            try {
                movieSearchResults = NetworkUtils.getResponseFromHttpUrl(searchUrl);

                Log.d("doInBackGround: ", movieSearchResults); // TODO remove before submission

            //TODO add call to jsonutil
                ArrayList<HashMap<String, String>> movieParsedData = JsonUtils.getMovieDataFromJson(movieSearchResults);


            } catch (IOException|JSONException e) {
                e.printStackTrace();
            }
            return null; //movieSearchResults;
        }

        //TODO
        @Override
        protected void onPostExecute(String[] s) {
            mLoadingIndicator.setVisibility(View.INVISIBLE);

            //Log.i("onPostExecute: ", s );
            if(s != null && !s.equals("")){
                //mSearchResultsTextView.setText(s);

            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemThatWasClickedId = item.getItemId();
        if (itemThatWasClickedId == R.id.action_sortby_popular) {
            makeTmdbSearchQuery(byMostPopular, "1");
            return true;
        } else if (itemThatWasClickedId == R.id.action_sortby_rating){
            makeTmdbSearchQuery(byTopRated, "1");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
