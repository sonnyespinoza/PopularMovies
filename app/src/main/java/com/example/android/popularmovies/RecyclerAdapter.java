package com.example.android.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.RecyclerAdapterViewHolder> {

    //Logging variable for class name
    private static final String TAG = RecyclerAdapter.class.getSimpleName();

    private ArrayList<HashMap<String, String>> movieList;
    private Context context;





    public RecyclerAdapter(Context context, ArrayList movielist) {
        this.context = context;
        this.movieList = movielist;

    }

    public void setMovieList ( ArrayList movielist) {
        this.movieList = movielist;

    }



    @Override
    public RecyclerAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutForMovieItem = R.layout.movie_list;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean attachParent = false;

        View view = inflater.inflate(layoutForMovieItem, parent, attachParent);
        return new RecyclerAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerAdapterViewHolder holder, int position) {

        //String imageURL= "https://image.tmdb.org/t/p/w185/" + movieList.get(position).get("backdrop_path");
        String imageURL= "https://image.tmdb.org/t/p/w185/" + movieList.get(position).get("poster_path");


        String tTitle = movieList.get(position).get("title");
        Log.i("onBind:URL ", imageURL  );
        Log.i("onBind:TITLE ", tTitle );


        //Picasso:Listen for loading errors
        Picasso.Builder builder = new Picasso.Builder(context);
        builder.listener(new Picasso.Listener()
        {
            @Override
            public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception)
            {
                exception.printStackTrace();
            }
        });
        //Load images to image view
        builder.build().load(imageURL).into(holder.movie_image);
    }

    @Override
    public int getItemCount() {
        return movieList.size();
    }

    public class RecyclerAdapterViewHolder extends RecyclerView.ViewHolder  implements OnClickListener {

        ImageView movie_image;

        public RecyclerAdapterViewHolder(View itemView) {
            super(itemView);
            movie_image = (ImageView) itemView.findViewById(R.id.iv_movies);

            itemView.setOnClickListener(this);
        }

        /**
         * Called when a view has been clicked.
         *
         * @param v The view that was clicked.
         */
        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            Toast.makeText(context, "Clicked on "+  movieList.get(adapterPosition).get("title"), Toast.LENGTH_LONG).show();

            Class destinationClass = DetailsActivity.class;
            Intent intentToStartDetailActivity = new Intent(context, destinationClass);
            // DONE (1) Pass the weather to the DetailActivity
            //intentToStartDetailActivity.putExtra(EXTRA_MESSAGE, weatherForDay);
            context.startActivity(intentToStartDetailActivity);
            //Intent intent = new Intent(context, )

        }
    }
}
