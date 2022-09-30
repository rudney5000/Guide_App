package com.dedy.parcguide.googleplaces.query;

public abstract class Query {

	protected QueryBuilder mQueryBuilder;
	
	public Query() {
		mQueryBuilder = new QueryBuilder();
		//setSensor(false); // Default
	}
	
	public void setSensor(boolean sensor) {
		mQueryBuilder.addParameter("sensor", Boolean.toString(sensor));
	}

	public void setKey(String key) {
		mQueryBuilder.addParameter("key", key);
	}

	public void setLanguage(String language) {
		mQueryBuilder.addParameter("language", language);
	}
	
	public abstract String getUrl();
	
	@Override
	public String toString() {
		return (getUrl() + mQueryBuilder.toString());
	}
}
