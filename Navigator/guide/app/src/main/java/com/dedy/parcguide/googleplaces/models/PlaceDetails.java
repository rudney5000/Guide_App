package com.dedy.parcguide.googleplaces.models;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class PlaceDetails extends Place implements Parcelable {

	private String mPhoneNumber = "";
	private String mWebsite = "";
	private List<PlaceReview> mReviews;
	private List<String> photoUrls;

	private PlaceDetails(Parcel in) {
		super(in);
		mPhoneNumber = in.readString();
		mWebsite = in.readString();
		in.readTypedList(mReviews, PlaceReview.CREATOR);
	}

	private PlaceDetails() {
		super();
		mReviews = new ArrayList<PlaceReview>();
	}
	
	public PlaceDetails(JSONObject jsonDetail) {
		super(jsonDetail);

		try {
			mReviews = new ArrayList<PlaceReview>();

			mPhoneNumber = jsonDetail.optString("formatted_phone_number");
			
			mAddress = jsonDetail.optString("formatted_address");

			mWebsite = jsonDetail.optString("website");


			JSONArray jsonReviews = jsonDetail.getJSONArray("reviews");
			
			for(int i =0;i<jsonReviews.length();i++) {
				PlaceReview review = new PlaceReview(jsonReviews.getJSONObject(i));
				mReviews.add(review);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean equals(Object o) {
		return super.equals(o);
	}

	public static PlaceDetails getEmpty() {
		return new PlaceDetails();
	}
	
	public String getPhoneNumber() {
		return mPhoneNumber;
	}
	
	public boolean phoneNumerIsValid() {
		return mPhoneNumber.matches("^\\(?([0-9]{3})\\)?[-. ]?([0-9]{3})[-. ]?([0-9]{4})$");
	}

	public String getWebsite() {
		return mWebsite;
	}
	
	public boolean websiteIsValid() {
		return (mWebsite != "");
	}
	
	public List<PlaceReview> getReviews() {
		return mReviews;
	}
	
	public boolean hasReviews() {
		return (mReviews.size() > 0);
	}
	
	public int describeContents() {
		return 0;
	}

	public void writeToParcel(Parcel out, int flags) {
		out.writeString(mPhoneNumber);
		out.writeString(mWebsite);
		out.writeTypedList(mReviews);
	}
	
	public static final Creator<PlaceDetails> CREATOR = new Creator<PlaceDetails>() {

		public PlaceDetails createFromParcel(Parcel in) {
			return new PlaceDetails(in);
		}

		public PlaceDetails[] newArray(int size) {
			return new PlaceDetails[size];
		}
	};
}
