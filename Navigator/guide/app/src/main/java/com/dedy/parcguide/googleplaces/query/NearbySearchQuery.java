package com.dedy.parcguide.googleplaces.query;

import android.location.Location;

import com.dedy.parcguide.googleplaces.Constants;

public class NearbySearchQuery extends SearchQuery {

	public enum Ranking { Prominence, Distance };

	private int pages;
	
	public NearbySearchQuery(Location location) {
		this(location.getLatitude(), location.getLongitude());
	}

	public NearbySearchQuery(double lat, double lon) {
		setLocation(lat, lon);
		//setRadius(2500); // Default
	}
	
	public void setRanking(Ranking ranking)	{
		mQueryBuilder.addParameter("rankby", ranking.toString());
	}
	
	public void setKeyword(String keyword) {
		mQueryBuilder.addParameter("keyword", keyword);
	}
	
	public void setName(String name) {
		mQueryBuilder.addParameter("name", name);	
	}
	
	public void setPageToken(String pageToken) {
		mQueryBuilder.addParameter("pagetoken", pageToken);
	}

	public int getPages() {
		return pages;
	}

	public void decrementPages(){
		pages--;
	}


	@Override
	public String getUrl() {
		return Constants.NEARBY_PLACES_URL;
	}

	public void setPages(int pages) {
		this.pages = pages;
	}
}
