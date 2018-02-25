package com.example.android.popularmovies.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
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



 /*   // Provide a suitable constructor (depends on the kind of dataset)
    public TrailerAdapter(String[] myDataset) {
        mDataset = myDataset;
    }*/

    public TrailerAdapter(Context context, ArrayList<TrailerParcelable> trailerlist) {
        this.context = context;
        this.trailerList = trailerlist;
    }



    // Create new views (invoked by the layout manager)
    @Override
    public TrailerAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
 /*       // create a new view
        TextView view = (TextView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.movie_trailers, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
*/

        Context context = parent.getContext();
        int layoutForTrailerItem = R.layout.movie_trailers;
        LayoutInflater inflater = LayoutInflater.from(context);
        //boolean attachParent = false;

        View view = inflater.inflate(layoutForTrailerItem, parent, false);
        return new TrailerAdapterViewHolder(view);
    }


    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class TrailerAdapterViewHolder extends RecyclerView.ViewHolder implements OnClickListener {

        //TODO consider delegating click listner to activity
        //https://www.youtube.com/watch?v=iEcMJE4KK-c

        // each data item is just a string in this case
        final ImageButton mImageButton;
        final TextView mTextView;
        String trailer_id;

        public TrailerAdapterViewHolder(View v) {
            super(v);
            mImageButton = (ImageButton) itemView.findViewById(R.id.ib_play_button);
            mTextView = (TextView) itemView.findViewById(R.id.tv_movie_trailer);

            mImageButton.setOnClickListener(this);

        }


        /**
         * Called when a view has been clicked.
         *
         * @param v The view that was clicked.
         */
        @Override
        public void onClick(View v) {

            int adapterPosition = getAdapterPosition();
            Log.d("TrailerAdpterViewHolder", "onClick: " + Integer.toString(adapterPosition));
            Log.d("TrailerAdpterVariable", "onClick: " + trailer_id);


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
    public void onBindViewHolder(TrailerAdapterViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.mImageButton.setImageResource(R.drawable.ic_player);
        holder.mTextView.setText(trailerList.get(position).getTrailer_name());
        holder.trailer_id = trailerList.get(position).getTrailer_key();

        //movieList.get(position).getImage_poster()

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {return trailerList.size();}
}