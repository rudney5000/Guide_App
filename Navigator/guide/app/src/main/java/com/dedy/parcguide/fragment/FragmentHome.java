package com.dedy.parcguide.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.ContextThemeWrapper;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.dedy.parcguide.CityGuideApp;
import com.dedy.parcguide.MainActivity;
import com.dedy.parcguide.R;
import com.dedy.parcguide.adapter.AdapterSearch;
import com.dedy.parcguide.callbacks.CallbackForecastLoaded;
import com.dedy.parcguide.callbacks.CallbackSearchDone;
import com.dedy.parcguide.googleplaces.Constants;
import com.dedy.parcguide.googleplaces.models.Place;
import com.dedy.parcguide.settings.AppSettings;
import com.dedy.parcguide.tasks.LoadForecast;
import com.dedy.parcguide.tasks.OnSearchEditTextFetching;
import com.dedy.parcguide.util.UtilView;
import com.dedy.parcguide.weather.cmn.ForecastDay;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by dedy ngueko on 28/3/2020..
 */
public class FragmentHome extends Fragment implements CallbackSearchDone, CallbackForecastLoaded, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    public static final String TAG = FragmentHome.class.getSimpleName();

    private static final int TOWN_RANGE = 20 * 1000;

    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;
    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = UPDATE_INTERVAL_IN_MILLISECONDS / 2;

    private View mBtnNearby;
    private View mBtnSleep;
    private View mBtnEat;
    private View mBtnEnjoy;

    private OnSearchEditTextFetching mSearchTask;
    private AutoCompleteTextView mAutoCompleteSearch;
    private View mWeatherContainer;
    private ImageView mNoWeatherIcon;
    private TextView mNoWeather;
    private ImageView mWeatherIcon;
    private TextView mWeatherTemp;
    private TextView mWeatherType;
    private View mAboutContainer;
    private TextView mTownName;
    private TextView mCountryName;
    private View mOverflowContainer;
    private View mOverflowBtnFavs;
    private View mOverflowBtnRate;
    private AdapterSearch adapterSearch;
    private View mOverflowIcon;
    private ProgressBar mSearchProgress;

    private static double currentLatitude;
    private static double currentLongitude;

    private Location predefinedLocation;

    protected GoogleApiClient mGoogleApiClient;
    protected LocationRequest mLocationRequest;

    TextView.OnEditorActionListener mOnEditorActionListener = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                if (mSearchTask != null) {
                    mSearchTask.cancel(true);
                }

                mSearchTask = new OnSearchEditTextFetching(mAutoCompleteSearch.getText().toString(), Constants.PLACE_TYPES.getAllTypesAsList(), FragmentHome.this);

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

    public static FragmentHome newInstance() {

        FragmentHome fragmentHome = new FragmentHome();

        return fragmentHome;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final Context contextThemeWrapper = new ContextThemeWrapper(getActivity(), R.style.AppThemeTranslucent);

        // clone the inflater using the ContextThemeWrapper
        LayoutInflater localInflater = inflater.cloneInContext(contextThemeWrapper);

        predefinedLocation = new Location("");
        predefinedLocation.setLatitude(AppSettings.LATITUDE);
        predefinedLocation.setLongitude(AppSettings.LONGITUDE);

        buildGoogleApiClient();

        return localInflater.inflate(R.layout.fra_home, null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        View mainContainer = getView().findViewById(R.id.fra_home_main_container);
        UtilView.setNoLollipopPadding(mainContainer, 0, 20);

        mBtnNearby = getView().findViewById(R.id.fra_home_btn_nearby);

        mBtnSleep = getView().findViewById(R.id.fra_home_sleep_btn);
        mBtnEat = getView().findViewById(R.id.fra_home_eat_btn);
        mBtnEnjoy = getView().findViewById(R.id.fra_home_enjoy_btn);

        mAutoCompleteSearch = (AutoCompleteTextView) getView().findViewById(R.id.fra_home_search);
        mAutoCompleteSearch.setHint(getString(R.string.search_in) + " " + AppSettings.TOWN);

        mWeatherContainer = getView().findViewById(R.id.fra_home_weather);
        mWeatherContainer.setOnClickListener(new OnWeatherClickListener());

        mAboutContainer = getView().findViewById(R.id.fra_home_about_container);
        mAboutContainer.setOnClickListener(new AboutClickListener());

        mNoWeatherIcon = (ImageView) getView().findViewById(R.id.fra_home_no_weather_icon);
        mNoWeather = (TextView) getView().findViewById(R.id.fra_home_no_weather);

        mWeatherIcon = (ImageView) getView().findViewById(R.id.fra_home_weather_icon);
        mWeatherTemp = (TextView) getView().findViewById(R.id.fra_home_weather_temp);
        mWeatherType = (TextView) getView().findViewById(R.id.fra_home_weather_type);

        mTownName = (TextView) getView().findViewById(R.id.fra_home_town_name);
        mTownName.setText(AppSettings.TOWN.toUpperCase());

        mCountryName = (TextView) getView().findViewById(R.id.fra_home_country_name);
        mCountryName.setText(AppSettings.COUNTRY.toUpperCase());

        mOverflowContainer = getView().findViewById(R.id.fra_home_container_overflow);

        mOverflowIcon = getView().findViewById(R.id.fra_home_overflow_icon);
        mOverflowIcon.setOnClickListener(new OnOverflowIconClickListener());

        mOverflowBtnFavs = getView().findViewById(R.id.fra_home_favs_btn);
        mOverflowBtnFavs.setOnClickListener(new OnCategoryClickListener(Constants.PLACE_TYPES.GOOGLE_PLACES_FAV));

        mOverflowBtnRate = getView().findViewById(R.id.fra_home_rate_btn);
        mOverflowBtnRate.setOnClickListener(new RateAppClickListener());

        mSearchProgress = (ProgressBar) getView().findViewById(R.id.search_progress);

        setViewsListeners();

        new LoadForecast((MainActivity) getActivity(), this).execute();

    }

    @Override
    public void onResume() {
        super.onResume();
        CityGuideApp application = (CityGuideApp)getActivity().getApplication();
        final Tracker tracker = application.getTracker();
        if(tracker != null){
            tracker.setScreenName(getClass().getSimpleName());
            tracker.send(new HitBuilders.ScreenViewBuilder().build());
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
        createLocationRequest();
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        // Sets the desired interval for active location updates. This interval
        // is
        // inexact. You may not receive updates at all if no location sources
        // are available, or
        // you may receive them slower than requested. You may also receive
        // updates faster than
        // requested if other applications are requesting location at a faster
        // interval.
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        // Sets the fastest rate for active location updates. This interval is
        // exact, and your
        // application will never receive updates faster than this value.
        mLocationRequest
                .setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
    }


    private void rateApp() {
        final Uri uri = Uri.parse("market://details?id=" + getActivity().getApplicationContext().getPackageName());
        final Intent rateAppIntent = new Intent(Intent.ACTION_VIEW, uri);

        if (getActivity().getPackageManager().queryIntentActivities(rateAppIntent, 0).size() > 0) {
            startActivity(rateAppIntent);
        }
    }


    private void setViewsListeners() {

        mBtnNearby.setOnClickListener(new NearbyClickListener());

        mBtnSleep.setOnClickListener(new OnCategoryClickListener(Constants.PLACE_TYPES.GOOGLE_PLACES_SLEEP));
        mBtnEat.setOnClickListener(new OnCategoryClickListener(Constants.PLACE_TYPES.GOOGLE_PLACES_EAT));
        mBtnEnjoy.setOnClickListener(new OnCategoryClickListener(Constants.PLACE_TYPES.GOOGLE_PLACES_ENJOY));

        mAutoCompleteSearch.setOnEditorActionListener(mOnEditorActionListener);
    }

    @Override
    public void getSearchResult(final Set<Place> placesResult) {
        adapterSearch = new AdapterSearch(getActivity(), placesResult);

        mAutoCompleteSearch.setAdapter(adapterSearch);

        adapterSearch.notifyDataSetChanged();

        mAutoCompleteSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                List<Place> places = new ArrayList<>(placesResult);

                ((MainActivity) (getActivity())).showPlaceDetailFragment(places.get(position), places.get(position).getPlaceType());
            }
        });
    }

    @Override
    public void showSearchLoading() {
        mOverflowIcon.setVisibility(View.GONE);
        mSearchProgress.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideSearchLoading() {
        mSearchProgress.setVisibility(View.GONE);
        mOverflowIcon.setVisibility(View.VISIBLE);
    }

    @Override
    public void doneLoadingForecast(final List<ForecastDay> result) {
        if (result != null && result.size() > 0) {

            mNoWeatherIcon.setVisibility(View.GONE);
            mNoWeather.setVisibility(View.GONE);
            mWeatherIcon.setBackgroundResource(UtilView.getDrawableIdByString(getActivity(), result.get(0).getIconUrl()));
            mWeatherTemp.setVisibility(View.VISIBLE);
            mWeatherType.setVisibility(View.VISIBLE);
            mWeatherTemp.setText(result.get(0).getTemperature());
            mWeatherType.setText(result.get(0).getWeatherDesc());


        } else {
            mWeatherTemp.setVisibility(View.GONE);
            mWeatherType.setVisibility(View.GONE);
            mWeatherIcon.setVisibility(View.GONE);
            mNoWeatherIcon.setVisibility(View.VISIBLE);
            mNoWeather.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void showLoadingWeather() {
        mWeatherTemp.setVisibility(View.INVISIBLE);
        mWeatherType.setVisibility(View.INVISIBLE);
        mWeatherIcon.setBackgroundResource(R.drawable.loading_animation);

        // Get the background, which has been compiled to an AnimationDrawable object.
        AnimationDrawable frameAnimation = (AnimationDrawable) mWeatherIcon.getBackground();

        // Start the animation (looped playback by default).
        frameAnimation.start();

    }

    @Override
    public void onConnected(Bundle bundle) {
        ((MainActivity) getActivity()).hideLoadingDialog();

        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if (location != null) {
            currentLatitude = location.getLatitude();
            currentLongitude = location.getLongitude();

            if (!((MainActivity) getActivity()).isNearbyFragmentShowed()) {
                showNearbyFragment();
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        if (isAdded()) {
            ((MainActivity) getActivity()).hideLoadingDialog();
            Toast.makeText(getActivity(), getString(R.string.current_location_issue), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (isAdded()) {
            ((MainActivity) getActivity()).hideLoadingDialog();
            Toast.makeText(getActivity(), getString(R.string.current_location_issue), Toast.LENGTH_LONG).show();
        }
    }

    private class OnOverflowIconClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {

            if (mOverflowContainer.getVisibility() == View.VISIBLE) {
                mOverflowContainer.setVisibility(View.GONE);
            } else {
                mOverflowContainer.setVisibility(View.VISIBLE);
            }
        }
    }

    private class OnCategoryClickListener implements View.OnClickListener {

        Constants.PLACE_TYPES type;


        public OnCategoryClickListener(Constants.PLACE_TYPES type) {
            this.type = type;
        }

        @Override
        public void onClick(View v) {
            ((MainActivity) getActivity()).showListFragment(type);
        }
    }

    private class OnWeatherClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            ((MainActivity) getActivity()).showForecastFragment();
        }
    }

    private class AboutClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            ((MainActivity) getActivity()).showAboutFragment();
        }
    }

    private class RateAppClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            rateApp();
        }
    }

    private class NearbyClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {

            if (isGPSEnabled(getActivity())) {

                if (currentLatitude > 0 && currentLongitude > 0) {
                    showNearbyFragment();
                } else if (mGoogleApiClient != null) {
                    ((MainActivity) getActivity()).showLoadingDialog();

                    mGoogleApiClient.connect();
                }
            } else {
                Toast.makeText(getActivity(), getString(R.string.turn_on_gps), Toast.LENGTH_LONG).show();
            }
        }
    }

    private void showNearbyFragment() {

        Location currentLocation = new Location("");
        currentLocation.setLatitude(currentLatitude);
        currentLocation.setLongitude(currentLongitude);

        if (isLocationsInRange(currentLocation)) {

            AppSettings.LATITUDE = currentLatitude;
            AppSettings.LONGITUDE = currentLongitude;

            ((MainActivity) getActivity()).showNearbyFragment(null, null, true);
        } else {
            Toast.makeText(getActivity(), String.format(getString(R.string.town_range), AppSettings.TOWN), Toast.LENGTH_LONG).show();
        }
    }

    private boolean isLocationsInRange(Location currentLocation) {

        return predefinedLocation.distanceTo(currentLocation) <= TOWN_RANGE;
    }

    public boolean isGPSEnabled(Context mContext) {
        LocationManager locationManager = (LocationManager)
                mContext.getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }
}
