package com.dedy.parcguide.googleplaces.models;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

public class PlaceReview implements Parcelable {

    private static String GOOGLE_PLUS = "https://plus.google.com/";

    private long mDate;
    private String mAuthorPhotoUrl;
    private int mRating = 0;
    private String mAuthorName = "";
    private String mText = "";

    private PlaceReview(Parcel in) {
        mRating = in.readInt();
        mAuthorName = in.readString();
        mText = in.readString();
    }

    public PlaceReview(JSONObject jsonReview) {
        try {
            mRating = jsonReview.optJSONArray("aspects").getJSONObject(0).getInt("rating");
            mAuthorName = jsonReview.optString("author_name");
            mText = jsonReview.getString("text");
            mAuthorPhotoUrl = jsonReview.optString("author_url");
            removeGooglePlusUrl();
            mDate = jsonReview.optLong("time");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void removeGooglePlusUrl(){
        if(mAuthorPhotoUrl.startsWith(GOOGLE_PLUS)){
            mAuthorPhotoUrl = mAuthorPhotoUrl.replace(GOOGLE_PLUS, "");
        }
    }

    public int getRating() {
        return mRating;
    }

    public String getAuthorName() {
        return mAuthorName;
    }

    public String getText() {
        return mText;
    }

    public long getDate() {
        return mDate * 1000;
    }

    public String getAuthorPhotoUrl() {
        return mAuthorPhotoUrl;
    }

    public void setAuthorPhotoUrl(String authorPhotoUrl) {
        mAuthorPhotoUrl = authorPhotoUrl;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(mRating);
        out.writeString(mAuthorName);
        out.writeString(mText);
    }

    public static final Creator<PlaceReview> CREATOR = new Creator<PlaceReview>() {

        public PlaceReview createFromParcel(Parcel in) {
            return new PlaceReview(in);
        }

        public PlaceReview[] newArray(int size) {
            return new PlaceReview[size];
        }
    };
}
