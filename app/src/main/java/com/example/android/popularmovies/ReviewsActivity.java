package com.example.android.popularmovies;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.example.android.popularmovies.adapters.ReviewsAdapter;
import com.example.android.popularmovies.parcelables.ReviewsParcelable;
import com.example.android.popularmovies.utilities.JsonUtils;
import com.example.android.popularmovies.utilities.NetworkUtils;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class ReviewsActivity extends AppCompatActivity {

    private ReviewsAdapter mReviewAdapter;
    private RecyclerView mReviewRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;

    String movie_id;

    private static final int REVIEWS_READ_LOADER =99;

    private static final String ReviewDataKey = "reviews";

    private NetworkUtils networkUtils = new NetworkUtils(this);

    ArrayList<ReviewsParcelable> mReviewsData;

    private static final String CRUD_URL_EXTRA = "crud";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reviews);

        mReviewRecyclerView = (RecyclerView) findViewById(R.id.rv_movie_reviews);
        mReviewRecyclerView.setHasFixedSize(true);

        Log.d("mReviewRecyclerView", mReviewRecyclerView.toString());
        mLayoutManager = new LinearLayoutManager(this);
        Log.d("mLayoutManager", mLayoutManager.toString());
        mReviewRecyclerView.setLayoutManager(mLayoutManager);

        // Set Adapter
        mReviewAdapter = new ReviewsAdapter(this, new ArrayList() {

        });

        mReviewRecyclerView.setAdapter(mReviewAdapter);

        Intent intent = getIntent();

        movie_id = intent.getStringExtra(this.getString(R.string.movie_id));
        Log.i("onCreate: movie_id: ", movie_id);

        URL mReviewsUrl = NetworkUtils.buildUrl("review_list", movie_id, "1");
        Log.i("createReviewURL", mReviewsUrl.toString());


        if (networkUtils.isNetworkAvailable(this)) {
            Log.i("isNetworkAvailable", "true");
            if (savedInstanceState == null || !savedInstanceState.containsKey(ReviewDataKey)) {
                Log.i("savedInstance:isNetwork", "isNull");
                makeMovieReviewsQuery(mReviewsUrl, REVIEWS_READ_LOADER);
            } else {
                Log.i("isNetworkAvailable", "false");
                mReviewsData = savedInstanceState.getParcelableArrayList(ReviewDataKey);
                //mTrailerAdapter.
                mReviewAdapter.setTrailList(mReviewsData);
                mReviewAdapter.notifyDataSetChanged();
            }


        } else {
            Toast.makeText(this, "No Internet Connection",
                    Toast.LENGTH_LONG).show();
        }
    }


    @SuppressLint("StaticFieldLeak") //ignore Lint warning
    private LoaderManager.LoaderCallbacks<ArrayList> movieReviews = new LoaderManager.LoaderCallbacks<ArrayList>() {
        @Override
        public Loader<ArrayList> onCreateLoader(final int id, final Bundle args) {
            return new AsyncTaskLoader<ArrayList>(getApplicationContext()) {

                @Override
                protected void onStartLoading() {

                    super.onStartLoading();


                    if (args == null) {
                        Log.i("onStartLoading", "null args");

                        return;
                    }


                    if (mReviewsData != null) {
                        Log.i("onStartLoading", "mTrailerData Not Null");
                        deliverResult(mReviewsData);

                    } else {
                        Log.i("onStartLoading", "mTrailerData Null");
                        forceLoad();
                    }
                }

                @Override
                public ArrayList loadInBackground() {

                    String reviewQueryUrlString = args.getString(CRUD_URL_EXTRA);
                    Log.i("LoadInBackground", reviewQueryUrlString);
                    if (reviewQueryUrlString == null || TextUtils.isEmpty(reviewQueryUrlString)) {
                        return null;
                    }
                    String mSearchResults;

                    //URL mTrailerUrl = networkUtils.buildUrl("trailer_list", movie_id, "1");
                    //Log.i("createTrailerURL", mTrailerUrl.toString());

                    //fetch data from API
                    try {
                        URL searchUrl = new URL(reviewQueryUrlString);

                        mSearchResults = networkUtils.getResponseFromHttpUrl(searchUrl);
                        //parse json
                        mReviewsData = JsonUtils.getReviewDataFromJson(mSearchResults);

                        return mReviewsData;

                    } catch (IOException | JSONException e) {
                        Log.e("LoadInBackground", "Exception");
                        e.printStackTrace();
                        return null;
                    }
                }

                @Override
                public void deliverResult(ArrayList data) {
                    mReviewsData = data;
                    Log.i("deliverResults", data.toString());
                    super.deliverResult(data);
                }


            };
        }


        @Override
        public void onLoadFinished(Loader<ArrayList> loader, ArrayList data) {

            Log.i("onLoadFinish: arySize", String.valueOf(data.size()));
            if (data != null && !data.equals("")) {
                //onSaveInstanceState();

                mReviewAdapter.setTrailList(data);
                mReviewAdapter.notifyDataSetChanged();
            }

        }

        @Override
        public void onLoaderReset(Loader<ArrayList> loader) {

        }
    };


    private void makeMovieReviewsQuery(URL url, int loaderID) {

        // created bundle to store key:value for the URL
        Bundle bundle = new Bundle();
        bundle.putString(CRUD_URL_EXTRA, url.toString());

        //get library for loadermanager
        LoaderManager loaderManager = getSupportLoaderManager();

        switch (loaderID) {

            case REVIEWS_READ_LOADER:
                Loader<ArrayList> reviewsLoader = loaderManager.getLoader(loaderID);

                //If the Loader was null, initialize it otherwise restart it
                if (reviewsLoader == null) {
                    Log.i("movieReviews ", "reviewsLoader: " + "isNull");
                    loaderManager.initLoader(loaderID, bundle, movieReviews);
                } else {
                    Log.i("movieReviews ", "reviewsLoader: " + "notNull");
                    loaderManager.restartLoader(loaderID, bundle, movieReviews);
                }
                break;
        }

    }



}
