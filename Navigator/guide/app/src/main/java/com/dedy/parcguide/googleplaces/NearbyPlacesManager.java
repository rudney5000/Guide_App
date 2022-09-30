package com.dedy.parcguide.googleplaces;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;

import com.dedy.parcguide.callbacks.CallbackNearbyPlaces;
import com.dedy.parcguide.db.DatabaseManager;
import com.dedy.parcguide.googleplaces.models.Place;
import com.dedy.parcguide.settings.AppSettings;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class NearbyPlacesManager implements CallbackNearbyPlaces {

    private Set<Place> mPlacesSleep;
    private Set<Place> mPlacesEat;
    private Set<Place> mPlacesEnjoy;
    private Set<Place> mPlacesFav;

    private Set<Place> mPlacesSleepLatest;
    private Set<Place> mPlacesEatLatest;
    private Set<Place> mPlacesEnjoyLatest;
    private Set<Place> mPlacesFavLatest;


    private List<CallbackNearbyPlaces> mCallbacks;

    private static NearbyPlacesManager instance;

    public static NearbyPlacesManager getInstance() {

        if (instance == null) {
            synchronized (NearbyPlacesManager.class) {
                if (instance == null) {
                    instance = new NearbyPlacesManager();
                }
            }
        }

        return instance;
    }

    private NearbyPlacesManager() {
        mCallbacks = new ArrayList<>();

        mPlacesSleep = new HashSet<>();
        mPlacesEat = new HashSet<>();
        mPlacesEnjoy = new HashSet<>();
        mPlacesFav = new HashSet<>();

        mPlacesSleepLatest = new HashSet<>();
        mPlacesEatLatest = new HashSet<>();
        mPlacesEnjoyLatest = new HashSet<>();
        mPlacesFavLatest = new HashSet<>();

        final LatLng appSettingsLocation = new LatLng(AppSettings.LATITUDE, AppSettings.LONGITUDE);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                loadPlaces(appSettingsLocation);
            }
        }, 1000);
    }

    private void loadPlaces(LatLng location) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            new LoadNearbyPlacesTask(location, Constants.PLACE_TYPES.GOOGLE_PLACES_SLEEP).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            new LoadNearbyPlacesTask(location, Constants.PLACE_TYPES.GOOGLE_PLACES_EAT).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            new LoadNearbyPlacesTask(location, Constants.PLACE_TYPES.GOOGLE_PLACES_ENJOY).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            new LoadNearbyPlacesTask(location, Constants.PLACE_TYPES.GOOGLE_PLACES_SLEEP).execute();
            new LoadNearbyPlacesTask(location, Constants.PLACE_TYPES.GOOGLE_PLACES_EAT).execute();
            new LoadNearbyPlacesTask(location, Constants.PLACE_TYPES.GOOGLE_PLACES_ENJOY).execute();
        }
    }

    @Override
    public void onPlaceClicked(Place place) {

    }

    @Override
    public void onPlacesLoaded(Set<Place> places, Constants.PLACE_TYPES type) {

        if (type == Constants.PLACE_TYPES.GOOGLE_PLACES_SLEEP) {
            mPlacesSleep.addAll(places);

            mPlacesSleepLatest = places;
        }

        if (type == Constants.PLACE_TYPES.GOOGLE_PLACES_EAT) {
            mPlacesEat.addAll(places);

            mPlacesEatLatest = places;
        }

        if (type == Constants.PLACE_TYPES.GOOGLE_PLACES_ENJOY) {
            mPlacesEnjoy.addAll(places);

            mPlacesEnjoyLatest = places;
        }

        for (int i = 0; i < mCallbacks.size(); i++) {

            if (mCallbacks.get(i) != null) {
                mCallbacks.get(i).onPlacesLoaded(null, null);
            }
        }
    }

    public List<Place> getPlacesByType(Constants.PLACE_TYPES type) {
        if (type == Constants.PLACE_TYPES.GOOGLE_PLACES_SLEEP) {
            return new ArrayList<>(mPlacesSleep);
        } else if (type == Constants.PLACE_TYPES.GOOGLE_PLACES_EAT) {
            return new ArrayList<>(mPlacesEat);
        } else if (type == Constants.PLACE_TYPES.GOOGLE_PLACES_ENJOY) {
            return new ArrayList<>(mPlacesEnjoy);
        } else {
            mPlacesFav.addAll(DatabaseManager.getInstance().getRealFavPlace());

            return new ArrayList<>(mPlacesFav);
        }
    }

    public String findNameByLocation(LatLng position) {

        List<Place> allPlaces = getAllPlacesAsList();

        for (int i = 0; i < allPlaces.size(); i++) {
            boolean latitude = allPlaces.get(i).getLatitude() == position.latitude;
            boolean longitude = allPlaces.get(i).getLongitude() == position.longitude;

            if (latitude && longitude) {
                return allPlaces.get(i).getName();
            }
        }

        return "";
    }


    private class LoadNearbyPlacesTask extends AsyncTask<Void, Void, Void> {

        private LatLng location;
        private Constants.PLACE_TYPES type;

        public LoadNearbyPlacesTask(LatLng location, Constants.PLACE_TYPES type) {
            this.location = location;
            this.type = type;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            //((MainActivity) getActivity()).showLoadingDialog();
        }

        @Override
        protected Void doInBackground(Void... params) {

            try {

                GooglePlaces googlePlaces = new GooglePlaces(location);

                List<String> typeAsList = new ArrayList<>();
                typeAsList.add(type.getType());

                googlePlaces.getPlacesNearby(googlePlaces.getDefaultNearbySearchQuery(typeAsList), NearbyPlacesManager.this, type);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

        }
    }

    public List<Place> getAllPlacesAsList() {
        List<Place> allPlaces = new ArrayList<>();

        allPlaces.addAll(mPlacesSleep);
        allPlaces.addAll(mPlacesEat);
        allPlaces.addAll(mPlacesEnjoy);

        return allPlaces;
    }

    public List<Place> getAllNewestPlacesAsList() {
        List<Place> allPlaces = new ArrayList<>();

        allPlaces.addAll(mPlacesSleepLatest);
        allPlaces.addAll(mPlacesEatLatest);
        allPlaces.addAll(mPlacesEnjoyLatest);

        return allPlaces;
    }

    public void addCallback(CallbackNearbyPlaces callbackNearbyPlaces) {
        this.mCallbacks.clear();

        this.mCallbacks.add(callbackNearbyPlaces);
    }

    public void onLocationChanged(LatLng location, CallbackNearbyPlaces callbackNearbyPlaces) {
        this.mCallbacks.clear();

        mCallbacks.add(callbackNearbyPlaces);

        loadPlaces(location);
    }

    public List<Place> performSearchByName(String name, Constants.PLACE_TYPES type) {
        if (type == Constants.PLACE_TYPES.GOOGLE_PLACES_SLEEP) {
            return performSearchByName(new ArrayList<Place>(mPlacesSleep), name);
        } else if (type == Constants.PLACE_TYPES.GOOGLE_PLACES_ENJOY) {
            return performSearchByName(new ArrayList<Place>(mPlacesEnjoy), name);
        } else if (type == Constants.PLACE_TYPES.GOOGLE_PLACES_EAT) {
            return performSearchByName(new ArrayList<Place>(mPlacesEat), name);
        } else if (type == Constants.PLACE_TYPES.GOOGLE_PLACES_FAV) {
            return performSearchByName(new ArrayList<Place>(DatabaseManager.getInstance().getRealFavPlace()), name);
        }

        return new ArrayList<Place>();
    }

    private List<Place> performSearchByName(List<Place> places, String name) {
        List<Place> result = new ArrayList<>();

        for (int i = 0; i < places.size(); i++) {
            if (places.get(i).getName().toLowerCase().indexOf(name.toLowerCase()) > -1) {
                result.add(places.get(i));
            }
        }

        return result;
    }
}
