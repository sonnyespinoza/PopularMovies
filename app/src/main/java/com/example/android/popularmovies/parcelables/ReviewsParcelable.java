package com.example.android.popularmovies.parcelables;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by sonny on 9/10/17.
 */
public class ReviewsParcelable implements Parcelable{


    private String review_id;
    private String review_author;
    private String review_content;

    public ReviewsParcelable(String mReviewId, String mReviewAuthor, String mReviewContent){
        this.review_id = mReviewId;
        this.review_author=mReviewAuthor;
        this.review_content=mReviewContent;
    }

    public ReviewsParcelable(Parcel parcel) {
        this.review_id=parcel.readString();
        this.review_author=parcel.readString();
        this.review_content=parcel.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }


    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(this.review_id);
        parcel.writeString(this.review_author);
        parcel.writeString(this.review_content);
    }
    public static final Creator<ReviewsParcelable> CREATOR=new Creator<ReviewsParcelable>() {
        @Override
        public ReviewsParcelable createFromParcel(Parcel parcel) {
            return new ReviewsParcelable(parcel);
        }

        @Override
        public ReviewsParcelable[] newArray(int i) {
            return new ReviewsParcelable[i];
        }
    };

    public void setReview_id(String review_id) {this.review_id = review_id;}
    public void setReview_author(String review_author) {this.review_author = review_author;}
    public void setReview_content(String review_content) {this.review_content = review_content;}



    public String getReview_id(){return review_id;}
    public String getReview_author(){return review_author;}
    public String getReview_content(){return  review_content;}

}
