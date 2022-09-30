package com.dedy.parcguide.callbacks;

import com.dedy.parcguide.googleplaces.Constants;
import com.dedy.parcguide.googleplaces.models.Place;

import java.util.Set;

/**
 * Created by dedy ngueko on 12/3/2020.
 */
public interface CallbackNearbyPlaces {

    void onPlaceClicked(Place place);

    void onPlacesLoaded(Set<Place> places, Constants.PLACE_TYPES type);
}
