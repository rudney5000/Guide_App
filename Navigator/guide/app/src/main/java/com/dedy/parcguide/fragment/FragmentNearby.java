package com.dedy.parcguide.fragment;

import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dedy.parcguide.CityGuideApp;
import com.dedy.parcguide.MainActivity;
import com.dedy.parcguide.R;
import com.dedy.parcguide.adapter.AdapterSearch;
import com.dedy.parcguide.callbacks.CallbackNearbyPlaces;
import com.dedy.parcguide.callbacks.CallbackSearchDone;
import com.dedy.parcguide.db.DatabaseManager;
import com.dedy.parcguide.googleplaces.Constants;
import com.dedy.parcguide.googleplaces.NearbyPlacesManager;
import com.dedy.parcguide.googleplaces.models.Place;
import com.dedy.parcguide.settings.AppSettings;
import com.dedy.parcguide.tasks.OnSearchEditTextFetching;
import com.dedy.parcguide.util.UtilView;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;


/**
 * Created by dedy ngueko on 30/3/2020.
 */
public class FragmentNearby extends Fragment implements CallbackNearbyPlaces, CallbackSearchDone, GoogleMap.OnMyLocationChangeListener {

    public static final String TAG = FragmentNearby.class.getSimpleName();
    private GoogleMap mMap;

    private Place mPlaceDetail;

    private List<Marker> mMarkersEat;
    private List<Marker> mMarkersSleep;
    private List<Marker> mMarkersEnjoy;
    private List<Marker> mMarkersFav;

    private BitmapDescriptor bitmapDescriptorFav;
    private BitmapDescriptor bitmapDescriptorEat;
    private BitmapDescriptor bitmapDescriptorSleep;
    private BitmapDescriptor bitmapDescriptorEnjoy;
    private View mImageActivateFav;
    private View mImageActivateStay;
    private View mImageActivateEat;
    private View mImageActivateEnjoy;
    private View mImagesActivateContainer;
    private ProgressBar mSearchProgress;

    private AutoCompleteTextView mAutoCompleteTextView;

    private Constants.PLACE_TYPES mType;

    private int loadedMarkerCounter;

    private OnSearchEditTextFetching mSearchTask;

    private boolean isHaveToShowCurrentLocation;

    TextView.OnEditorActionListener mOnEditorActionListener = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                if (mSearchTask != null) {
                    mSearchTask.cancel(true);
                }

                mSearchTask = new OnSearchEditTextFetching(mAutoCompleteTextView.getText().toString(), Constants.PLACE_TYPES.getAllTypesAsList(), FragmentNearby.this);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    mSearchTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                } else {
                    mSearchTask.execute();
                }
                return true;
            }
            return false;
        }
    };


    private GoogleMap.OnCameraChangeListener mOnCameraChangeListener = new GoogleMap.OnCameraChangeListener() {
        @Override
        public void onCameraChange(CameraPosition cameraPosition) {

            ((MainActivity) getActivity()).showLoadingDialog();

            mMap.setOnCameraChangeListener(null);

            NearbyPlacesManager.getInstance().onLocationChanged(cameraPosition.target, FragmentNearby.this);
        }
    };

    public static FragmentNearby newInstance(Constants.PLACE_TYPES type, Place placeDetail, boolean isHaveToShowCurrentLocation) {
        FragmentNearby fragmentNearby = new FragmentNearby();
        fragmentNearby.mType = type;
        fragmentNearby.mPlaceDetail = placeDetail;
        fragmentNearby.isHaveToShowCurrentLocation = isHaveToShowCurrentLocation;

        return fragmentNearby;
    }

    protected MapView mMapView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mMarkersEat = new ArrayList<Marker>();
        mMarkersSleep = new ArrayList<Marker>();
        mMarkersEnjoy = new ArrayList<Marker>();
        mMarkersFav = new ArrayList<>();

        return inflater.inflate(R.layout.fra_nearby, null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mAutoCompleteTextView = (AutoCompleteTextView) getView().findViewById(R.id.fra_nearby_search);
        mAutoCompleteTextView.setHint(getString(R.string.search_in) + " " + AppSettings.TOWN);
        mAutoCompleteTextView.setOnEditorActionListener(mOnEditorActionListener);

        if (!UtilView.isLollipop()) {
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mAutoCompleteTextView.getLayoutParams();
            params.setMargins(0, (int) (MainActivity.density * 20), 0, 0);
        }

        mMapView = (MapView) getView().findViewById(R.id.nearby_mapView);

        mMapView.onCreate(null);
        mMap = mMapView.getMap();

        bitmapDescriptorFav = BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.map_pin_fav));
        bitmapDescriptorSleep = BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.map_pin_sleep));
        bitmapDescriptorEat = BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.map_pin_eat));
        bitmapDescriptorEnjoy = BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.map_pin_enjoy));

        mImagesActivateContainer = getView().findViewById(R.id.images_activate_container);

        mImageActivateFav = getView().findViewById(R.id.image_activate_fav);
        mImageActivateStay = getView().findViewById(R.id.image_activate_sleep);
        mImageActivateEat = getView().findViewById(R.id.image_activate_eat);
        mImageActivateEnjoy = getView().findViewById(R.id.image_activate_enjoy);

        mSearchProgress = (ProgressBar) getView().findViewById(R.id.search_progress);

        initCameraLocation();
    }

    @Override
    public void onResume() {
        super.onResume();

        mMapView.onResume();

        CityGuideApp application = (CityGuideApp)getActivity().getApplication();
        final Tracker tracker = application.getTracker();
        if(tracker != null){
            tracker.setScreenName(getClass().getSimpleName());
            tracker.send(new HitBuilders.ScreenViewBuilder().build());
        }
    }

    private void initActivateListeners() {

        mImageActivateFav.setOnClickListener(new ActivateClickListener());
        mImageActivateStay.setOnClickListener(new ActivateClickListener());
        mImageActivateEat.setOnClickListener(new ActivateClickListener());
        mImageActivateEnjoy.setOnClickListener(new ActivateClickListener());

        if (mType != null) {

            if (mType == Constants.PLACE_TYPES.GOOGLE_PLACES_SLEEP) {
                mImageActivateEat.performClick();
                mImageActivateEnjoy.performClick();
                mImageActivateFav.performClick();
            } else if (mType == Constants.PLACE_TYPES.GOOGLE_PLACES_EAT) {
                mImageActivateStay.performClick();
                mImageActivateEnjoy.performClick();
                mImageActivateFav.performClick();
            } else if (mType == Constants.PLACE_TYPES.GOOGLE_PLACES_ENJOY) {
                mImageActivateStay.performClick();
                mImageActivateEat.performClick();
                mImageActivateFav.performClick();
            } else if (mType == Constants.PLACE_TYPES.GOOGLE_PLACES_FAV) {
                mImageActivateEat.performClick();
                mImageActivateStay.performClick();
                mImageActivateEnjoy.performClick();
            }
        }
    }

    private void initCameraLocation() {

        if (isHaveToShowCurrentLocation) {
            mMap.getUiSettings().setMyLocationButtonEnabled(false);

            mMap.setMyLocationEnabled(true);
        }

        mMap.setOnCameraChangeListener(mOnCameraChangeListener);

        mMap.setOnMyLocationChangeListener(this);

        LatLng initialLatLng = null;

        if (mPlaceDetail != null) {
            initialLatLng = new LatLng(mPlaceDetail.getLatitude(), mPlaceDetail.getLongitude());
        } else {
            initialLatLng = new LatLng(AppSettings.LATITUDE, AppSettings.LONGITUDE);
        }

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(initialLatLng, AppSettings.MAP_INITIAL_ZOOM);
        mMap.moveCamera(cameraUpdate);

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {

                String name = NearbyPlacesManager.getInstance().findNameByLocation(marker.getPosition());

                marker.setTitle(name);

                marker.showInfoWindow();

                return false;
            }
        });

        initActivateListeners();

        if (mPlaceDetail != null) {
            List<Place> singlePlaceList = new ArrayList<Place>();
            singlePlaceList.add(mPlaceDetail);

            loadMarkers(singlePlaceList);
        } else {

            List<Place> allPlaces = NearbyPlacesManager.getInstance().getAllPlacesAsList();

            if (allPlaces.size() == 0) {
                ((MainActivity) getActivity()).showLoadingDialog();

                NearbyPlacesManager.getInstance().addCallback(this);
            } else {
                loadMarkers(allPlaces);

                loadMarkers(new ArrayList<Place>(DatabaseManager.getInstance().getRealFavPlace()));
            }
        }
    }

    private void loadMarkers(List<Place> currentPlacesToLoad) {

        if (currentPlacesToLoad.size() > 0) {
            mImagesActivateContainer.setVisibility(View.VISIBLE);
        }


        MarkerOptions markerOptions = new MarkerOptions();

        for (int i = 0; i < currentPlacesToLoad.size(); i++) {

            Log.i("Loaded_Markers", currentPlacesToLoad.get(i).getName() + " " + loadedMarkerCounter++);

            Place currentPlace = currentPlacesToLoad.get(i);

            markerOptions.position(new LatLng(currentPlace.getLatitude(), currentPlace.getLongitude()));

            BitmapDescriptor bitmapDescriptorToSet = null;

            if (currentPlace.isFav() && !mImageActivateFav.isSelected()) {
                bitmapDescriptorToSet = bitmapDescriptorFav;

                markerOptions.icon(bitmapDescriptorFav);
                mMarkersFav.add(mMap.addMarker(markerOptions));
            } else if (currentPlace.getPlaceType() == Constants.PLACE_TYPES.GOOGLE_PLACES_EAT && !mImageActivateEat.isSelected()) {
                bitmapDescriptorToSet = bitmapDescriptorEat;

                markerOptions.icon(bitmapDescriptorEat);
                mMarkersEat.add(mMap.addMarker(markerOptions));
            } else if (currentPlace.getPlaceType() == Constants.PLACE_TYPES.GOOGLE_PLACES_SLEEP && !mImageActivateStay.isSelected()) {
                bitmapDescriptorToSet = bitmapDescriptorSleep;

                markerOptions.icon(bitmapDescriptorSleep);
                mMarkersSleep.add(mMap.addMarker(markerOptions));
            } else if (currentPlace.getPlaceType() == Constants.PLACE_TYPES.GOOGLE_PLACES_ENJOY && !mImageActivateEnjoy.isSelected()) {
                bitmapDescriptorToSet = bitmapDescriptorEnjoy;

                markerOptions.icon(bitmapDescriptorEnjoy);
                mMarkersEnjoy.add(mMap.addMarker(markerOptions));
            }

            if (mPlaceDetail != null && checkPlaceIdentity(new LatLng(currentPlace.getLatitude(), currentPlace.getLongitude()), new LatLng(mPlaceDetail.getLatitude(), mPlaceDetail.getLongitude()))) {
                markerOptions.title(currentPlace.getName());
                markerOptions.icon(bitmapDescriptorToSet);
                mMap.addMarker(markerOptions).showInfoWindow();
            }
        }
    }

    private boolean checkPlaceIdentity(LatLng place1, LatLng place2) {
        if (place1.latitude == place2.latitude && place2.longitude == place2.longitude) {
            return true;
        }

        return false;
    }


    @Override
    public void onMyLocationChange(Location lastKnownLocation) {
        CameraUpdate myLoc = CameraUpdateFactory.newCameraPosition(
                new CameraPosition.Builder().target(new LatLng(lastKnownLocation.getLatitude(),
                        lastKnownLocation.getLongitude())).zoom(AppSettings.MAP_INITIAL_ZOOM).build());
        mMap.animateCamera(myLoc);
        mMap.setOnMyLocationChangeListener(null);
    }


    @Override
    public void getSearchResult(final Set<Place> placesResult) {

        AdapterSearch adapterSearch = new AdapterSearch(getActivity(), placesResult);

        mAutoCompleteTextView.setAdapter(adapterSearch);

        adapterSearch.notifyDataSetChanged();

        mAutoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                List<Place> places = new ArrayList<>(placesResult);

                List<Place> searchedPlaceToNavigate = new ArrayList<Place>();
                searchedPlaceToNavigate.add(places.get(position));

                loadMarkers(searchedPlaceToNavigate);

                LatLng latLng = new LatLng(searchedPlaceToNavigate.get(0).getLatitude(), searchedPlaceToNavigate.get(0).getLongitude());
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, AppSettings.MAP_INITIAL_ZOOM);
                mMap.animateCamera(cameraUpdate);
            }
        });


    }

    @Override
    public void showSearchLoading() {
        mSearchProgress.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideSearchLoading() {
        mSearchProgress.setVisibility(View.GONE);
    }


    private void hideLoadingDialog() {
        if (isAdded()) {
            ((MainActivity) getActivity()).hideLoadingDialog();
        }

    }

    @Override
    public void onPlaceClicked(Place place) {

    }

    @Override
    public void onPlacesLoaded(Set<Place> places, Constants.PLACE_TYPES type) {

        if (!isAdded()) {
            return;
        }
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mMap.setOnCameraChangeListener(mOnCameraChangeListener);

                ((MainActivity) getActivity()).hideLoadingDialog();

                loadMarkers(NearbyPlacesManager.getInstance().getAllNewestPlacesAsList());
            }
        });
    }

    private class ActivateClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {

            mMap.clear();

            v.setSelected(!v.isSelected());

            loadMarkers(NearbyPlacesManager.getInstance().getAllPlacesAsList());
        }
    }
}
