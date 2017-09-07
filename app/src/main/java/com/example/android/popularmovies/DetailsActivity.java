package com.example.android.popularmovies;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class DetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        Intent intent = getIntent();

        String msg = intent.getStringExtra("title");

        this.setTitle(msg);

        //TextView textView = (TextView) findViewById(R.id.tv_title);
        //textView.setText(msg);
        TextView tView = (TextView) findViewById(R.id.movie_title);
        tView.setText(msg);
    }
}
