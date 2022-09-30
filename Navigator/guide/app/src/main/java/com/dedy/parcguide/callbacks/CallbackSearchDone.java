package com.dedy.parcguide.callbacks;

import com.dmbteam.cityguide.googleplaces.models.Place;

import java.util.Set;

/**
 * Created by dedy ngueko on 12/3/2020.
 */
public interface CallbackSearchDone {

    void getSearchResult(Set<Place> places);

    void showSearchLoading();

    void hideSearchLoading();

}
