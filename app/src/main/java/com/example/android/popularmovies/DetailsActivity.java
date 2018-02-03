package com.example.android.popularmovies;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.popularmovies.data.FavoritesContract;
import com.example.android.popularmovies.utilities.ParcelableUtils;
import com.squareup.picasso.Picasso;

import java.util.List;

public class DetailsActivity extends AppCompatActivity {


    private List<ParcelableUtils> movie;
    private Context context;
    boolean isFavorite= false;

    String title;
    String release_date;
    String user_rating;
    String movie_image;
    String movie_desc;
    String id;




    /*
     * onClickAddFavorite is called when the favorites button is click
     */
    public void onClickAddFavorite(View view) {

        ImageButton ButtonStar = (ImageButton) findViewById(R.id.ib_favorite_button);
        ContentValues contentValues = new ContentValues();
        Uri uri;

        if (isFavorite){ //if true re-move data from favorites
            ButtonStar.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),android.R.drawable.btn_star));

            //TODO WIP will need to match query method in content provider
            getContentResolver().delete(FavoritesContract.favoriteMovies.CONTENT_URI,FavoritesContract.favoriteMovies._ID+"=?", new String[]{id});

            Log.i("onClickAddFav", "Rec Deleted: " + id);

        }else{ //otherwise added the data to favorites
            ButtonStar.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),android.R.drawable.btn_star_big_on));
            contentValues.put(FavoritesContract.favoriteMovies.MOVIE_TITLE, title);
            contentValues.put(FavoritesContract.favoriteMovies.RELEASE_DATE, release_date);
            contentValues.put(FavoritesContract.favoriteMovies.USER_RATING, user_rating);
            contentValues.put(FavoritesContract.favoriteMovies.USER_FAVORITES, "true");
            contentValues.put(FavoritesContract.favoriteMovies.MOVIE_DESCRIPTION, movie_desc );
            contentValues.put(FavoritesContract.favoriteMovies.IMAGE_POSTER, movie_image );

            uri = getContentResolver().insert(FavoritesContract.favoriteMovies.CONTENT_URI, contentValues);

            if (uri != null){

                Log.d("onClickAddFav", "Uri " + uri);
            }

        }
        isFavorite = !isFavorite;

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);



        Intent intent = getIntent();
        //movie = intent.getParcelableArrayListExtra("movie");
        //;


        title = intent.getStringExtra(this.getString(R.string.title));
        //this.setTitle(title);
        TextView tv_title = (TextView) findViewById(R.id.tv_detail_movie_title);
        tv_title.setText(title);

        release_date = intent.getStringExtra(this.getString(R.string.release_date));
        TextView tv_release_date = (TextView) findViewById(R.id.tv_release_date);
        tv_release_date.setText(release_date);


        user_rating = intent.getStringExtra(this.getString(R.string.user_rating));
        user_rating = user_rating + this.getString(R.string.details_max_user_rating);

        TextView tv_user_rating = (TextView) findViewById(R.id.tv_user_rating);
        tv_user_rating.setText(user_rating );

        //TODO add record _id to String id from StringExtra

        //TODO clean up mock boolean once live data is plugged in stringExtras
        Boolean movie_favorite = false ;
                //intent.getStringExtra("movie_favorites");
        ImageButton ButtonStar = (ImageButton) findViewById(R.id.ib_favorite_button);

        if (movie_favorite){
            ButtonStar.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),android.R.drawable.btn_star_big_on));
            isFavorite = !isFavorite;
        }




        //Picasso:Listen for loading errors
        context = getApplicationContext();
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
        String movie_image = intent.getStringExtra(this.getString(R.string.image_poster));
        ImageView iv_img_backdrop = (ImageView) findViewById(R.id.iv_details_poster) ;
        builder.build().load(movie_image).into(iv_img_backdrop);

        movie_desc = intent.getStringExtra(this.getString(R.string.overview));
        TextView tv_movie_desc = (TextView) findViewById(R.id.tv_detail_movie_description);
        tv_movie_desc.setText(movie_desc);
    }
}

