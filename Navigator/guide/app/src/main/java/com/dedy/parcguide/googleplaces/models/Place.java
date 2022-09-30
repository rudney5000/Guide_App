package com.dedy.parcguide.googleplaces.models;

import android.content.Context;
import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;

import com.dedy.parcguide.MainActivity;
import com.dedy.parcguide.R;
import com.dedy.parcguide.db.DatabaseManager;
import com.dedy.parcguide.googleplaces.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Place implements Parcelable {

    private String mId;
    private ArrayList<String> mTypesAsList;
    private String mName = "";
    protected String mAddress = "";
    private double mLatitude = 0;
    private double mLongitude = 0;
    private double mRating = 0;
    private int mPriceLevel;
    private String jsonString;

    private ArrayList<String> mPhotosUrls;

    protected Place() {
    }

    protected Place(Parcel in) {
        mName = in.readString();
        mAddress = in.readString();
        mLatitude = in.readDouble();
        mLongitude = in.readDouble();
        mRating = in.readDouble();
    }

    public Place(JSONObject jsonPlace) {
        jsonString = jsonPlace.toString();

        try {
            mId = jsonPlace.optString("place_id");
            mName = jsonPlace.getString("name");
            mLatitude = jsonPlace.getJSONObject("geometry").getJSONObject("location").getDouble("lat");
            mLongitude = jsonPlace.getJSONObject("geometry").getJSONObject("location").getDouble("lng");
            mRating = jsonPlace.optDouble("rating");

            if (jsonPlace.has("vicinity")) {
                mAddress = jsonPlace.getString("vicinity");
            } else {
                mAddress = jsonPlace.getString("formatted_address");
            }

            mPriceLevel = jsonPlace.optInt("price_level");


            JSONArray typesAsJsonArray = jsonPlace.optJSONArray("types");

            mTypesAsList = new ArrayList<String>();

            if (typesAsJsonArray != null) {
                for (int i = 0; i < typesAsJsonArray.length(); i++) {
                    mTypesAsList.add((typesAsJsonArray.getString(i)));
                }
            }

            setAllPhotoUrls(jsonPlace);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean equals(Object o) {
        return !super.equals(o);
    }

    @Override
    public int hashCode() {
        return getId().hashCode();
    }

    public void setAllPhotoUrls(JSONObject jsonPlace) throws JSONException {

        mPhotosUrls = new ArrayList<String>();

        JSONArray photos = jsonPlace.optJSONArray("photos");

        if (photos != null) {
            for (int i = 0; i < photos.length(); i++) {
                String currentReferenceString = photos.getJSONObject(i).getString("photo_reference");

                String currentPhotoUrl = String.format(Constants.PLACE_PHOTO_URL, Constants.DEFAULT_PHOTO_WIDTH, currentReferenceString, MainActivity.getApiKey());

                mPhotosUrls.add(currentPhotoUrl);
            }
        }

    }

    public ArrayList<String> getPhotosUrls() {

        if (mPhotosUrls == null) {
            mPhotosUrls = new ArrayList<>();
        }

        if (mPhotosUrls.size() == 0) {
            mPhotosUrls.add("");
        }

        return mPhotosUrls;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getAddress() {
        return mAddress;
    }

    public void setAddress(String address) {
        mAddress = address;
    }

    public double getLatitude() {
        return mLatitude;
    }

    public void setLatitude(double latitude) {
        mLatitude = latitude;
    }

    public double getLongitude() {
        return mLongitude;
    }

    public void setLongitude(double longitude) {
        mLongitude = longitude;
    }

    public double getDistanceTo(Location location) {
        Location source = new Location("");
        source.setLatitude(mLatitude);
        source.setLongitude(mLongitude);

        return source.distanceTo(location);
    }

    public String getRating() {
        return "Rating " + mRating;
    }

    public double getRatingValue() {
        return mRating;
    }

    public void setRating(float rating) {
        mRating = rating;
    }

    public String getPriceLevel(Context context) {


        if (mPriceLevel == 0) {
            return context.getString(R.string.place_detail_label_no_value);
        }

        return String.valueOf((double) mPriceLevel);
    }

    public double getPriceLevelValue() {

        return mPriceLevel;
    }

    public String getId() {
        return mId;
    }

    public Constants.PLACE_TYPES getPlaceType() {

        for (int i = 0; i < mTypesAsList.size(); i++) {

            String currentType = mTypesAsList.get(i);

            Constants.PLACE_TYPES findedType = Constants.PLACE_TYPES.isValueInAnyType(currentType);
            if (findedType != null) {
                return findedType;
            }
        }

        return null;
    }

    public boolean isFav() {

        List<Place> favPlaces = new ArrayList<>(DatabaseManager.getInstance().getRealFavPlace());

        for (int i = 0; i < favPlaces.size(); i++) {
            if (favPlaces.get(i).getId().equalsIgnoreCase(this.mId)) {
                return true;
            }
        }

        return false;
    }

    public ArrayList<String> getTypesAsList() {

        if (mTypesAsList == null) {
            mTypesAsList = new ArrayList<>();
        }

        return mTypesAsList;
    }

    public int describeContents() {
        return 0;
    }

    public String getJsonString() {
        return jsonString;
    }

    public void setJsonString(String jsonString) {
        this.jsonString = jsonString;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeString(mName);
        out.writeString(mAddress);
        out.writeDouble(mLatitude);
        out.writeDouble(mLongitude);
        out.writeDouble(mRating);
    }

    public static final Creator<Place> CREATOR = new Creator<Place>() {

        public Place createFromParcel(Parcel in) {
            return new Place(in);
        }

        public Place[] newArray(int size) {
            return new Place[size];
        }
    };

    @Override
    public String toString() {
        return "";
    }
}