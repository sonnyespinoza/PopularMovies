package com.example.android.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class DetailsActivity extends AppCompatActivity {

    final String RELEASE_DATE = "release_date";
    final String MOVIE_DESCRIPTION = "overview";
    final String MOVIE_TITLE = "title";
    //final String IMAGE_NAME = "backdrop_path";
    final String IMAGE_POSTER= "poster_path";
    final String USER_RATING = "vote_average";
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        Intent intent = getIntent();

        String title = intent.getStringExtra(MOVIE_TITLE);
        //this.setTitle(title);
        TextView tv_title = (TextView) findViewById(R.id.tv_detail_movie_title);
        tv_title.setText(title);

        String release_date = intent.getStringExtra(RELEASE_DATE);
        TextView tv_release_date = (TextView) findViewById(R.id.tv_release_date);
        tv_release_date.setText(release_date);


        String user_rating = intent.getStringExtra(USER_RATING);
        TextView tv_user_rating = (TextView) findViewById(R.id.tv_user_rating);
        tv_user_rating.setText(user_rating + "/10");



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
        String movie_image = intent.getStringExtra(IMAGE_POSTER);
        ImageView iv_img_backdrop = (ImageView) findViewById(R.id.iv_details_poster) ;
        builder.build().load(movie_image).into(iv_img_backdrop);

        String movie_desc = intent.getStringExtra(MOVIE_DESCRIPTION);
        TextView tv_movie_desc = (TextView) findViewById(R.id.tv_detail_movie_description);
        tv_movie_desc.setText(movie_desc);
    }
}
