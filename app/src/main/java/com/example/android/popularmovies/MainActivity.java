package com.example.android.popularmovies;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.android.popularmovies.utilities.NetworkUtils;

import java.io.IOException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private final String byMostPopular = "popularity.desc";
    private final String byTopRated = "vote_average.desc";

    RecyclerAdapter mAdapter;

    RecyclerView mMovieImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
        //mSearchBoxEditText.getText().toString();
        URL movieSearchUrl = NetworkUtils.buildUrl(sortBy, Page);
        //mUrlDisplayTextView.setText(githubSearchUrl.toString());
        Log.i("makeTmdbSearchQuery", movieSearchUrl.toString());

        //Passing in the url to query
        new MovieQueryTask().execute(movieSearchUrl);
    }

    public class MovieQueryTask  extends AsyncTask<URL, Void, String> {

        // perform the query. Return the results.
        @Override
        protected String doInBackground(URL... urls) {
            URL searchUrl = urls[0];
            String movieSearchResults = null;
            try {
                movieSearchResults = NetworkUtils.getResponseFromHttpUrl(searchUrl);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return movieSearchResults;
        }

        @Override
        protected void onPostExecute(String s) {
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
