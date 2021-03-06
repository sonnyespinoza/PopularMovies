package com.example.android.popularmovies.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.popularmovies.R;
import com.example.android.popularmovies.parcelables.ReviewsParcelable;

import java.util.ArrayList;

/**
 * Created by sonny on 2/21/18.
 */

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ReviewsAdapterViewHolder> {
    private String[] mDataset;

    ArrayList<ReviewsParcelable> reviewList = new ArrayList<ReviewsParcelable>();

    final private Context context;


    //Constructor
    public ReviewsAdapter(Context context, ArrayList<ReviewsParcelable> reviewlist) {
        this.context = context;
        this.reviewList = reviewlist;
    }


    // Create new views (invoked by the layout manager)
    @Override
    public ReviewsAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        Context context = parent.getContext();
        int layoutForTrailerItem = R.layout.movie_reviews;
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(layoutForTrailerItem, parent, false);
        return new ReviewsAdapterViewHolder(view);
    }


    // A reference to the views for each data item
    public static class ReviewsAdapterViewHolder extends RecyclerView.ViewHolder {

        // each data item is just a string in this case
        final TextView mTextView;
        String review_id;

        public ReviewsAdapterViewHolder(View v) {
            super(v);
            mTextView = (TextView) itemView.findViewById(R.id.tv_movie_review);

        }

    }


    public void setTrailList(ArrayList<ReviewsParcelable> reviewlist) {
        this.reviewList = reviewlist;
    }


    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ReviewsAdapterViewHolder holder, final int position) {

        holder.mTextView.setText(reviewList.get(position).getReview_content());
    }

    // Return the size of the reviewList (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return reviewList.size();
    }

}