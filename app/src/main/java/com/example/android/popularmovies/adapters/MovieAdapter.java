package com.example.android.popularmovies.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.android.popularmovies.R;
import com.example.android.popularmovies.parcelables.MovieParcelable;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieAdapterViewHolder> {


    ArrayList<MovieParcelable> movieList = new ArrayList<MovieParcelable>();
    final private Context context;
    private movieAdapterClickListener mListener;

    public interface movieAdapterClickListener{
        void onClickMovieItem(HashMap<String,String> movieDetails, int position); //callback
    }



    public MovieAdapter(Context context, ArrayList<MovieParcelable> movielist, movieAdapterClickListener mListener) {
        this.context = context;
        this.movieList = movielist;
        this.mListener = mListener;
    }

    public void setMovieList ( ArrayList<MovieParcelable> movielist) {
        this.movieList = movielist;
    }

    public void addToMovieList(ArrayList<MovieParcelable> movieImage) {

        movieList.addAll(movieImage);
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

        String imageURL = context.getString(R.string.image_url) + movieList.get(position).getImage_poster();

        //Log.d("MovieAdapter", "onBindViewHolder: imageURL"+ imageURL);

        //Picasso:Listen for loading errors
/*        Picasso.Builder builder = new Picasso.Builder(context);
        builder.listener(new Picasso.Listener()
        {
            @Override
            public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception)
            {
                //Log.d("onImageLoadFailed", String.valueOf(exception));
                exception.printStackTrace();
            }
        });*/

        //Load images to image view
        //builder.build().load(imageURL).into(holder.movie_image);
        Picasso.with(context).load(imageURL).into(holder.movie_image);

        holder.movie_image.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {

                HashMap<String,String> movieDetails= new HashMap<String,String>();

                movieDetails.put(context.getString(R.string.title), movieList.get(holder.getAdapterPosition()).getTitle());
                movieDetails.put(context.getString(R.string.image_poster), movieList.get(holder.getAdapterPosition()).getImage_poster());
                movieDetails.put(context.getString(R.string.release_date), movieList.get(holder.getAdapterPosition()).getRelease_date());
                movieDetails.put(context.getString(R.string.user_rating), movieList.get(holder.getAdapterPosition()).getUser_rating());
                movieDetails.put(context.getString(R.string.overview), movieList.get(holder.getAdapterPosition()).getOverview());
                movieDetails.put(context.getString(R.string.movie_id), movieList.get(holder.getAdapterPosition()).getId());

                mListener.onClickMovieItem(movieDetails, holder.getAdapterPosition());

            }
        });

    }

    @Override
    public int getItemCount() {
        return movieList.size();
    }

    public class MovieAdapterViewHolder extends RecyclerView.ViewHolder   {

        final ImageView movie_image;

        public MovieAdapterViewHolder(View itemView) {
            super(itemView);
            movie_image = (ImageView) itemView.findViewById(R.id.iv_movies);
        }
    }


}
