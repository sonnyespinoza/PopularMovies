package com.example.android.popularmovies.utilities;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by sonny on 9/10/17.
 */
public class ParcelableUtils implements Parcelable{

    private String release_date;
    private String overview;
    private String title;
    private String image_name;
    private String image_poster;
    private String user_rating;
    private String id;


    public ParcelableUtils(String mRelease_date, String mOverview, String mTitle, String mImage_name, String mImage_poster, String mUser_rating, String mId  ){
        this.release_date = mRelease_date;
        this.overview=mOverview;
        this.title=mTitle;
        this.image_name=mImage_name;
        this.image_poster=mImage_poster;
        this.user_rating=mUser_rating;
        this.id=mId;


    }

    public ParcelableUtils(Parcel parcel) {
        this.release_date=parcel.readString();
        this.overview=parcel.readString();
        this.title=parcel.readString();
        this.image_name=parcel.readString();
        this.image_poster=parcel.readString();
        this.user_rating=parcel.readString();
        this.id=parcel.readString();

    }

    @Override
    public int describeContents() {
        return 0;
    }


    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(this.release_date);
        parcel.writeString(this.overview);
        parcel.writeString(this.title);
        parcel.writeString(this.image_name);
        parcel.writeString(this.image_poster);
        parcel.writeString(this.user_rating);
        parcel.writeString(this.id);

    }
    public static final Creator<ParcelableUtils> CREATOR=new Creator<ParcelableUtils>() {
        @Override
        public ParcelableUtils createFromParcel(Parcel parcel) {
            return new ParcelableUtils(parcel);
        }

        @Override
        public ParcelableUtils[] newArray(int i) {
            return new ParcelableUtils[i];
        }
    };

    public void setRelease_date(String release_date) {this.release_date = release_date;}
    public void setOverview(String overview) {this.overview = overview;}
    public void setTitle(String title) {this.title = title;}
    public void setImage_name(String image_name) {this.image_name = image_name;}
    public void setImage_poster(String image_poster) {this.image_poster = image_poster;}
    public void setUser_rating(String user_rating) {this.user_rating = user_rating;}
    public void setId(String id) {this.id = id;}

    public String getRelease_date(){return release_date;}
    public String getOverview(){return overview;}
    public String getTitle(){return  title;}
    public String getImage_name(){return image_name;}
    public String getImage_poster(){return image_poster;}
    public String getUser_rating(){return user_rating;}
    public String getId(){return id;}





}
