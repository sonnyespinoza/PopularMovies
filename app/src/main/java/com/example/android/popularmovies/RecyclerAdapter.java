package com.example.android.popularmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import android.view.View.OnClickListener;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.RecyclerAdapterViewHolder> {

    //Logging variable for class name
    private static final String TAG = RecyclerAdapter.class.getSimpleName();

    //Number of movies returned to display
    private int mNumberMovies;

    public RecyclerAdapter(int mNumberMovies) {
        this.mNumberMovies = mNumberMovies;
    }


    /**
     * Called when RecyclerView needs a new ViewHolder of the given type to represent
     * an item.
     *
     * @param parent   The ViewGroup into which the new View will be added after it is bound to
     *                 an adapter position.
     * @param viewType The view type of the new View.
     * @return A new ViewHolder that holds a View of the given view type.
     */
    @Override
    public RecyclerAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutForMovieItem = R.layout.movie_list;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean attachParent = false;

        View view = inflater.inflate(layoutForMovieItem, parent, attachParent);
        RecyclerAdapterViewHolder viewHolder = new RecyclerAdapterViewHolder(view);

        return viewHolder;
    }

    /**
     * OnBindViewHolder is called by the RecyclerView to display the data at the specified
     * position. In this method, we update the contents of the ViewHolder to display the correct
     * indices in the list for this particular position, using the "position" argument that is conveniently
     * passed into us.
     *
     * @param holder   The ViewHolder which should be updated to represent the contents of the
     *                 item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(RecyclerAdapterViewHolder holder, int position) {
        Log.d(TAG, "#" + position);
        holder.bind(position);
    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     *
     * @return The total number of items in this adapter.
     */
    @Override
    public int getItemCount() {
        return 0;
    }

    public class RecyclerAdapterViewHolder extends RecyclerView.ViewHolder  {

        public final GridView MovieGridView;

        public RecyclerAdapterViewHolder(View itemView) {
            super(itemView);
            MovieGridView = (GridView) itemView.findViewById(R.id.gv_movie_image);
            //itemView.setOnClickListener(this);
        }

        /**
         * this method will take an integer as input and
         * use that integer to display the appropriate image in the grid.
         *
         * @param listIndex Position of the item in the list
         */
        void bind(int listIndex) {

            //listItemNumberView.setText(String.valueOf(listIndex));
        }
    }
}
