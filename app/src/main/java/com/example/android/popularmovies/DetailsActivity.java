package com.example.android.popularmovies;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
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
    Uri uri;


    boolean isFavorite= false;

    String title;
    String release_date;
    String user_rating;
    String movie_image;
    String movie_desc;
    int id;






    /*
     * onClickAddFavorite is called when the favorites button is click
     */
    public void onClickAddFavorite(View view) {

        ImageButton ButtonStar = (ImageButton) findViewById(R.id.ib_favorite_button);
        ContentValues contentValues = new ContentValues();


        if (isFavorite){ //if true re-move data from favorites
            ButtonStar.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),android.R.drawable.btn_star));

            getContentResolver().delete(FavoritesContract.favoriteMovies.CONTENT_URI,FavoritesContract.favoriteMovies._ID+"=?", new String[]{String.valueOf(id)});

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



                //id = Long.valueOf(uri.getLastPathSegment());


                id = Integer.valueOf(uri.getLastPathSegment());
                Log.d("onClickAddFav", "id: " + id);
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
        movie_image = intent.getStringExtra(this.getString(R.string.image_poster));
        ImageView iv_img_backdrop = (ImageView) findViewById(R.id.iv_details_poster) ;
        builder.build().load(movie_image).into(iv_img_backdrop);

        movie_desc = intent.getStringExtra(this.getString(R.string.overview));
        TextView tv_movie_desc = (TextView) findViewById(R.id.tv_detail_movie_description);
        tv_movie_desc.setText(movie_desc);

        //TODO #2 query favorites from intent.getStringExtra(this.getString(R.string.image_poster));
        //https://androidexample.com/Content_Provider_Basic/index.php?view=article_discription&aid=120
        //getcontentresolver().query android example
        String[] mProjection =  {FavoritesContract.favoriteMovies._ID, FavoritesContract.favoriteMovies.MOVIE_TITLE};
        String mSelection = FavoritesContract.favoriteMovies.IMAGE_POSTER + " = ?";
        String[] mSelectionArgs = {""};
        mSelectionArgs[0] = movie_image;

        Cursor mCursor = getContentResolver().query(FavoritesContract.favoriteMovies.CONTENT_URI,
                mProjection,
                mSelection,
                mSelectionArgs,
                null  );

        // null = error
        if (null == mCursor) {

            Log.e("DetailsActivty: mCursor", "Error ocurred");

            // Insert code here to handle the error. Be sure not to use the cursor! You may want to
            // call android.util.Log.e() to log this error.


            //  no matches
        } else if (mCursor.getCount() < 1) {

            // Insert code here to notify the user that the contact query was unsuccessful. This isn’t necessarily
            // an error. You may want to offer the user the option to insert a new row, or re-type the
            // search term.
            Log.e("DetailsActivty: mCursor", "No Record Found");


        }else {

            Log.e("DetailsActivty: mCursor", "Found Something");

            // Insert code here to do something with the results

            // Moves to the next row in the cursor. Before the first movement in the cursor, the
            // "row pointer" is -1, and if you try to retrieve data at that position you will get an
            // exception.

            //while (mCursor.moveToNext()) {

                // Gets the value from the column.

               // phoneNumber = mCursor.getString(index);

                // Show phone number in Logcat
                //Log.i("Phone"," Numbers : " + "");

                // end of while loop
            //}


        }

        //Needed for research
        //https://books.google.com/books?id=hI8sBQAAQBAJ&pg=PA57&lpg=PA57&dq=contentprovider+query+single+item+uri+match+string&source=bl&ots=IpKr_pWkf6&sig=RlIQX9_97dIN4WaW3KIfs-6aK4I&hl=en&sa=X&ved=0ahUKEwj_sKrpr4rZAhVN-mMKHUR8D0gQ6AEIbjAJ#v=onepage&q=contentprovider%20query%20single%20item%20uri%20match%20string&f=false

        //TODO #3 update boolean based on search result >0
        Boolean movie_favorite = false ;


        ImageButton ButtonStar = (ImageButton) findViewById(R.id.ib_favorite_button);

        if (movie_favorite){
            ButtonStar.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),android.R.drawable.btn_star_big_on));
            isFavorite = !isFavorite;
        }
    }
}

