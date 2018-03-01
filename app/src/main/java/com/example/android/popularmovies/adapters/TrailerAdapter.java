package com.example.android.popularmovies.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.android.popularmovies.R;
import com.example.android.popularmovies.parcelables.TrailerParcelable;

import java.util.ArrayList;

/**
 * Created by sonny on 2/21/18.
 */

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.TrailerAdapterViewHolder> {
    private String[] mDataset;

    ArrayList<TrailerParcelable> trailerList = new ArrayList<TrailerParcelable>();

    final private Context context;
    private TrailerAdapterClickListener mListener;




    public interface TrailerAdapterClickListener{
        void onClickTrailItem(String trailerKey, int position); //callback
    }

    //Constructor
    public TrailerAdapter(Context context, ArrayList<TrailerParcelable> trailerlist, TrailerAdapterClickListener mListener) {
        this.context = context;
        this.trailerList = trailerlist;
        this.mListener = mListener; //receive listener from activity
    }



    // Create new views (invoked by the layout manager)
    @Override
    public TrailerAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        Context context = parent.getContext();
        int layoutForTrailerItem = R.layout.movie_trailers;
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(layoutForTrailerItem, parent, false);
        return new TrailerAdapterViewHolder(view);
    }


    // A reference to the views for each data item
    public static class TrailerAdapterViewHolder extends RecyclerView.ViewHolder  {

        // each data item is just a string in this case
        final ImageButton mImageButton;
        final TextView mTextView;
        String trailer_id;

        public TrailerAdapterViewHolder(View v) {
            super(v);
            mImageButton = (ImageButton) itemView.findViewById(R.id.ib_play_button);
            mTextView = (TextView) itemView.findViewById(R.id.tv_movie_trailer);

        }

    }


    public void setTrailList ( ArrayList<TrailerParcelable> trailerlist) {
        this.trailerList = trailerlist;
    }

    public void testMethod ( String str) {
        Log.i("test", "tst");
    }


    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(TrailerAdapterViewHolder holder, final int position) {

        holder.mImageButton.setImageResource(R.drawable.ic_player);
        holder.mTextView.setText(trailerList.get(position).getTrailer_name());
        holder.trailer_id = trailerList.get(position).getTrailer_key();

        holder.mImageButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                mListener.onClickTrailItem(trailerList.get(position).getTrailer_key(), position);
            }
        });
    }

    // Return the size of the trailerList (invoked by the layout manager)
    @Override
    public int getItemCount() {return trailerList.size();}

}