package com.example.android.popularmovies.parcelables;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by sonny on 9/10/17.
 */
public class TrailerParcelable implements Parcelable{

    //trailerData.add(new MovieParcelable(trailerId, trailerKey, trailerName, trailerSite, trailerType));;

    private String trailer_id;
    private String trailer_key;
    private String trailer_name;
    private String trailer_site;
    private String trailer_type;



    public TrailerParcelable(String mTrailerId, String mTrailerKey, String mTrailerName, String mTrailerSite, String mTrailerType  ){
        this.trailer_id = mTrailerId;
        this.trailer_key=mTrailerKey;
        this.trailer_name=mTrailerName;
        this.trailer_site=mTrailerSite;
        this.trailer_type=mTrailerType;
    }

    public TrailerParcelable(Parcel parcel) {
        this.trailer_id=parcel.readString();
        this.trailer_key=parcel.readString();
        this.trailer_name=parcel.readString();
        this.trailer_site=parcel.readString();
        this.trailer_type=parcel.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }


    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(this.trailer_id);
        parcel.writeString(this.trailer_key);
        parcel.writeString(this.trailer_name);
        parcel.writeString(this.trailer_site);
        parcel.writeString(this.trailer_type);
    }
    public static final Creator<TrailerParcelable> CREATOR=new Creator<TrailerParcelable>() {
        @Override
        public TrailerParcelable createFromParcel(Parcel parcel) {
            return new TrailerParcelable(parcel);
        }

        @Override
        public TrailerParcelable[] newArray(int i) {
            return new TrailerParcelable[i];
        }
    };

    public void setTrailer_id(String trailer_id) {this.trailer_id = trailer_id;}
    public void setTrailer_key(String trailer_key) {this.trailer_key = trailer_key;}
    public void setTrailer_name(String trailer_name) {this.trailer_name = trailer_name;}
    public void setTrailer_site(String trailer_site) {this.trailer_site = trailer_site;}
    public void setTrailer_type(String trailer_type) {this.trailer_type = trailer_type;}


    public String getTrailer_id(){return trailer_id;}
    public String getTrailer_key(){return trailer_key;}
    public String getTrailer_name(){return  trailer_name;}
    public String getTrailer_site(){return trailer_site;}
    public String getTrailer_type(){return trailer_type;}

}
