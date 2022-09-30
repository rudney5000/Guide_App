package com.dmbteam.cityguide.googleplaces.query;

import android.location.Location;

import java.util.List;

public abstract class SearchQuery extends Query {

    private StringBuilder mTypes = new StringBuilder();

    public void setLocation(double latitude, double longitude) {
        String location = Double.toString(latitude) + "," + Double.toString(longitude);
        mQueryBuilder.addParameter("location", location);
    }

    public void setLocation(Location location) {
        setLocation(location.getLatitude(), location.getLongitude());
    }

    public void setRadius(int radius) {
        mQueryBuilder.addParameter("radius", Integer.toString(radius));
    }

    public void addTypes(List<String> types) {

        for (int i = 0; i < types.size(); i++) {
            mTypes.append(types.get(i));
            mTypes.append("|");
        }

        mTypes.deleteCharAt(mTypes.length() - 1);

        mQueryBuilder.addParameter("types", mTypes.toString());
    }

    @Override
    public String toString() {
        //mQueryBuilder.addParameter("types", mTypes.toString());
        return (getUrl() + mQueryBuilder.toString());
    }
}
