package com.example.android.popularmovies.adapters;

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

import com.example.android.popularmovies.DetailsActivity;
import com.example.android.popularmovies.R;
import com.example.android.popularmovies.parcelables.MovieParcelable;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieAdapterViewHolder> {


    ArrayList<MovieParcelable> movieList = new ArrayList<MovieParcelable>();
    final private Context context;

    public MovieAdapter(Context context, ArrayList<MovieParcelable> movielist) {
        this.context = context;
        this.movieList = movielist;
    }

    public void setMovieList ( ArrayList<MovieParcelable> movielist) {
        this.movieList = movielist;
    }


    @Override
    public MovieAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutForMovieItem = R.layout.movie_list;
        LayoutInflater inflater = LayoutInflater.from(context);
        //boolean attachParent = false;

        View view = inflater.inflate(layoutForMovieItem, parent, false);
        return new MovieAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MovieAdapterViewHolder holder, int position) {

        //String imageURL= "https://image.tmdb.org/t/p/w185/"  + movieList.get(position).get("poster_path");
        String imageURL= "https://image.tmdb.org/t/p/w185/"  + movieList.get(position).getImage_poster();

        Log.d("MovieAdapter", "onBindViewHolder: imageURL"+ imageURL);

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

    public class MovieAdapterViewHolder extends RecyclerView.ViewHolder  implements OnClickListener {

        final ImageView movie_image;

        public MovieAdapterViewHolder(View itemView) {
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

            Class destinationClass = DetailsActivity.class;
            Intent intentDetailActivity = new Intent(context, destinationClass);


            //TODO add query to check if favorite
            movieList.get(adapterPosition).getImage_poster();
            //intentDetailActivity.putExtra("favorite", )


            // Pass the movie details to the DetailsActivity
            intentDetailActivity.putExtra(context.getString(R.string.title), movieList.get(adapterPosition).getTitle());
            intentDetailActivity.putExtra(context.getString(R.string.image_poster),  movieList.get(adapterPosition).getImage_poster());
            //intentDetailActivity.putExtra(IMAGE_NAME, "https://image.tmdb.org/t/p/w185/"  + movieList.get(adapterPosition).get(IMAGE_NAME));
            intentDetailActivity.putExtra(context.getString(R.string.release_date), movieList.get(adapterPosition).getRelease_date());
            intentDetailActivity.putExtra(context.getString(R.string.user_rating), movieList.get(adapterPosition).getUser_rating());
            intentDetailActivity.putExtra(context.getString(R.string.overview), movieList.get(adapterPosition).getOverview());
            intentDetailActivity.putExtra(context.getString(R.string.movie_id), movieList.get(adapterPosition).getId());
     //       intentDetailActivity.putParcelableArrayListExtra("movies", movieList);

            context.startActivity(intentDetailActivity);

        }


    }
}
