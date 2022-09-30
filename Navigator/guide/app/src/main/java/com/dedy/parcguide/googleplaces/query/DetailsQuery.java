package com.dedy.parcguide.googleplaces.query;


public class DetailsQuery extends Query {

	public DetailsQuery(String reference) {
		setPlaceId(reference);
	}
	
	public void setPlaceId(String placeId) {
		mQueryBuilder.addParameter("placeid", placeId);
	}
	
	@Override
	public String getUrl() {
		return "https://maps.googleapis.com/maps/api/place/details/json";
	}

}