package com.example.android.popularmovies.utilities;

import android.content.Context;
import android.util.DisplayMetrics;

/**
 * Created by sonny on 3/10/18.
 */

public class SpanColumns {
        public static int calculateNoOfColumns(Context context) {
            DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
            float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
            int noOfColumns = (int) (dpWidth / 140);
            return noOfColumns;
        }
    }

