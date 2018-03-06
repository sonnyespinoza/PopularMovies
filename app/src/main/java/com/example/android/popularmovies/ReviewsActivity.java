package com.example.android.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.example.android.popularmovies.adapters.ReviewsAdapter;
import com.example.android.popularmovies.utilities.NetworkUtils;

import java.util.ArrayList;

public class ReviewsActivity extends AppCompatActivity {

    private ReviewsAdapter mReviewAdapter;
    private RecyclerView mReviewRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;

    String movie_id;

    //private static final String ReviewDataKey = "reviews";

    private NetworkUtils networkUtils = new NetworkUtils(this);



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

    }
}
