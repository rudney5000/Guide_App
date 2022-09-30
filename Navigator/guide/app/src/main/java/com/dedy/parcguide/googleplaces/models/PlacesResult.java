package com.dedy.parcguide.googleplaces.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashSet;
import java.util.Set;

public class PlacesResult extends Result {

    private String mNextPageToken;
    private Set<Place> mPlaces = new HashSet<Place>();

    public PlacesResult(JSONObject jsonResponse) throws JSONException {
        super(jsonResponse);


        JSONArray results = jsonResponse.optJSONArray("results");

        if (results != null) {
            for (int i = 0; i < results.length(); i++) {
                Place place = new Place(results.getJSONObject(i));
                mPlaces.add(place);
            }
        }

        mNextPageToken = jsonResponse.optString("next_page_token");
    }

    public Set<Place> getPlaces() {
        return mPlaces;
    }

    public String getNextPageToken() {
        return mNextPageToken;
    }
}
