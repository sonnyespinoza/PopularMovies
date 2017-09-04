package com.example.android.popularmovies;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Callback;
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

        Log.d(TAG + ": Image", "#" + position + ": " + " https://image.tmdb.org/t/p/w185/" + movieList.get(position).get("backdrop_path").toString() ); //REMOVE
        String imageURL= "https://image.tmdb.org/t/p/w185/" + movieList.get(position).get("backdrop_path");

/*        //Picasso.with(context).load(imageURL).into(holder.movie_image);
        Picasso.with(context).load(imageURL).into(holder.movie_image, new Callback() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onError() {
                Log.e("Picasso","Image load failed");

            }


        });*/

        Picasso.Builder builder = new Picasso.Builder(context);
        builder.listener(new Picasso.Listener()
        {
            @Override
            public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception)
            {
                exception.printStackTrace();
            }
        });
        builder.build().load(imageURL).into(holder.movie_image);
    }

    @Override
    public int getItemCount() {
        return movieList.size();
    }

    public class RecyclerAdapterViewHolder extends RecyclerView.ViewHolder  {

        ImageView movie_image;



        public RecyclerAdapterViewHolder(View itemView) {
            super(itemView);
            movie_image = (ImageView) itemView.findViewById(R.id.iv_movies);

            //itemView.setOnClickListener(this);
        }
    }
}
