package com.dedy.parcguide.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dedy.parcguide.CityGuideApp;
import com.dedy.parcguide.MainActivity;
import com.dedy.parcguide.R;
import com.dedy.parcguide.adapter.AdapterSlider;
import com.dedy.parcguide.db.DatabaseManager;
import com.dedy.parcguide.googleplaces.Constants;
import com.dedy.parcguide.googleplaces.GooglePlaces;
import com.dedy.parcguide.googleplaces.models.Place;
import com.dedy.parcguide.googleplaces.models.PlaceDetails;
import com.dedy.parcguide.util.UtilView;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.maps.model.LatLng;

/**
 * Created by dedy ngueko on 05/4/2020.
 */
public class FragmentPlaceDetail extends Fragment {

    public static final String TAG = FragmentPlaceDetail.class.getSimpleName();
    private Place mPlace;
    private ViewPager mPagerView;
    private TextView mPlaceNameTv;
    private TextView mPlacePhotoCount;
    private PlaceDetails placeDetails;
    private TextView mPlaceAddress;
    private TextView mPlacePhone;
    private LinearLayout mPlacePriceLevelContainer;
    private LinearLayout mPlaceRatingContainer;
    private Constants.PLACE_TYPES mType;
    private ImageView mFavIcon;
    private TextView mPlaceAddressLabel;
    private TextView mPlacePhoneLabel;
    private TextView mPlaceTagLabel;
    private TextView mPlacePriceLevelLabel;
    private TextView mPlaceRatingLevelLabel;
    private TextView mPlaceRatingValue;
    private TextView mPlacePriceValue;
    private ImageView mIconMap;
    private ImageView mIconPhone;
    private ImageView mIconEye;
    private View mBackArrow;
    private LinearLayout mPlaceTagContainer;

    public static FragmentPlaceDetail newInstance(Place place, Constants.PLACE_TYPES type) {

        FragmentPlaceDetail fragmentPlaceDetail = new FragmentPlaceDetail();
        fragmentPlaceDetail.mPlace = place;
        fragmentPlaceDetail.mType = type;

        return fragmentPlaceDetail;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fra_place_detail, null);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mPagerView = (ViewPager) getView().findViewById(R.id.fra_place_detail_pager);
        mFavIcon = (ImageView) getView().findViewById(R.id.fra_place_detail_fav);
        mFavIcon.setBackgroundResource(Constants.PLACE_TYPES.getFavIconEmptyId(mType));
        mFavIcon.setOnClickListener(new OnFavIconClickListener());

        mPlaceNameTv = (TextView) getView().findViewById(R.id.fra_place_detail_name);
        mPlaceNameTv.setSelected(true);

        mPlacePhotoCount = (TextView) getView().findViewById(R.id.fra_place_detail_photo_count);

        // Address label and value
        mPlaceAddressLabel = (TextView) getView().findViewById(R.id.fra_place_detail_address_label);
        mPlaceAddressLabel.setTextColor(getResources().getColor(mType.getColourId()));
        mPlaceAddress = (TextView) getView().findViewById(R.id.fra_place_detail_address_value);

        // Phone label and value
        mPlacePhoneLabel = (TextView) getView().findViewById(R.id.fra_place_detail_phone_label);
        mPlacePhoneLabel.setTextColor(getResources().getColor(mType.getColourId()));
        mPlacePhone = (TextView) getView().findViewById(R.id.fra_place_detail_phone_value);

        mPlaceTagContainer = (LinearLayout) getView().findViewById(R.id.fra_place_tags_container);

        // TAGS label
        mPlaceTagLabel = (TextView) getView().findViewById(R.id.fra_place_tags_label);
        mPlaceTagLabel.setTextColor(getResources().getColor(mType.getColourId()));

        // Price level label + value
        mPlacePriceLevelLabel = (TextView) getView().findViewById(R.id.fra_place_price_label);
        mPlacePriceLevelLabel.setTextColor(getResources().getColor(mType.getColourId()));
        mPlacePriceLevelContainer = (LinearLayout) getView().findViewById(R.id.fra_place_price_container);
        mPlacePriceValue = (TextView) getView().findViewById(R.id.fra_place_price_value);

        // Rating level label + value
        mPlaceRatingLevelLabel = (TextView) getView().findViewById(R.id.fra_place_rating_label);
        mPlaceRatingLevelLabel.setTextColor(getResources().getColor(mType.getColourId()));
        mPlaceRatingContainer = (LinearLayout) getView().findViewById(R.id.fra_place_rating_container);
        mPlaceRatingValue = (TextView) getView().findViewById(R.id.fra_place_rating_value);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            new LoadDetailInfoTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            new LoadDetailInfoTask().execute();
        }

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

    private void setViews() {
        mFavIcon.setSelected(DatabaseManager.getInstance().getState(placeDetails.getId()));

        mPlaceNameTv.setText(placeDetails.getName());

        if(placeDetails.getPhotosUrls().size() == 0){
            mPlacePhotoCount.setText("");
        }else{
            mPlacePhotoCount.setText("1 of " + placeDetails.getPhotosUrls().size());
        }

        mPlaceAddress.setText(placeDetails.getAddress());
        mPlacePhone.setText(placeDetails.getPhoneNumber());

        mIconMap = (ImageView) getView().findViewById(R.id.fra_place_detail_map);
        mIconMap.setBackgroundResource(Constants.PLACE_TYPES.getIconMap(mType));
        mIconMap.setOnClickListener(new OnMapClickListener());

        mIconPhone = (ImageView) getView().findViewById(R.id.fra_place_detail_phone);
        mIconPhone.setBackgroundResource(Constants.PLACE_TYPES.getIconPhone(mType));
        mIconPhone.setOnClickListener(new OnCallIntent());

        mIconEye = (ImageView) getView().findViewById(R.id.fra_place_detail_eye);
        mIconEye.setBackgroundResource(Constants.PLACE_TYPES.getIconEye(mType));
        mIconEye.setOnClickListener(new OnShowReviewsClickListener());

        setPlaceTags();

        mPlacePriceValue.setTextColor(getResources().getColor(mType.getColourId()));
        if (placeDetails.getPriceLevelValue() > 0) {
            mPlacePriceValue.setText("" + placeDetails.getPriceLevelValue());

            UtilView.getStarsByValue(mPlace.getPriceLevelValue(), mPlacePriceLevelContainer, getLayoutInflater(), mType);
        } else {
            mPlacePriceLevelContainer.setPadding(0, 0, 0, 0);
        }

        mPlaceRatingValue.setTextColor(getResources().getColor(mType.getColourId()));
        if (placeDetails.getRatingValue() > 0) {
            mPlaceRatingLevelLabel.setText(mPlaceRatingLevelLabel.getText() + " " + String.format(getString(R.string.rating_count), "" + placeDetails.getReviews().size()));

            mPlaceRatingValue.setText("" + placeDetails.getRatingValue());

            UtilView.getStarsByValue(placeDetails.getRatingValue(), mPlaceRatingContainer, getLayoutInflater(), mType);
        } else {
            mPlaceRatingContainer.setPadding(0, 0, 0, 0);
        }

        mBackArrow = getView().findViewById(R.id.fra_place_detail_back);

        UtilView.setNoLollipopPadding(mBackArrow, 0, 0);

        setOnBackListener();
    }

    private void setPlaceTags() {

        for (int i = 0; i < placeDetails.getTypesAsList().size(); i++) {
            LinearLayout textViewLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.tags_textview, null);
            TextView textView = (TextView) textViewLayout.getChildAt(0);

            textView.setTextColor(getResources().getColor(mType.getColourId()));

            textView.setBackgroundResource(Constants.PLACE_TYPES.getTagsBg(mType));

            textView.setText(placeDetails.getTypesAsList().get(i));

            mPlaceTagContainer.addView(textViewLayout);
        }

    }

    private void setCurrentImageCount() {

        mPlacePhotoCount.setText("1 of " + placeDetails.getPhotosUrls().size());
        mPagerView.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mPlacePhotoCount.setText(String.format(getResources().getString(R.string.image_count), "" + ++position, "" + placeDetails.getPhotosUrls().size()));

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void setPager() {

        AdapterSlider adapterSlider = new AdapterSlider(getActivity(), placeDetails.getPhotosUrls(), mType);
        mPagerView.setAdapter(adapterSlider);

        setCurrentImageCount();
    }

    private void setOnBackListener() {
        mBackArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }


    private LayoutInflater getLayoutInflater() {
        return LayoutInflater.from(getActivity());
    }

    private void onBackPressed() {
        getActivity().getSupportFragmentManager().popBackStack();
    }

    private void phoneCall() {
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", placeDetails.getPhoneNumber(), null));
        startActivity(intent);
    }

    private class LoadDetailInfoTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            ((MainActivity)getActivity()).showLoadingDialog();
        }

        @Override
        protected Void doInBackground(Void... params) {

            GooglePlaces googlePlaces = new GooglePlaces(new LatLng(mPlace.getLatitude(), mPlace.getLongitude()));

            try {
                placeDetails = googlePlaces.getPlaceDetails(mPlace.getId()).getDetails();
            } catch (Exception e) {
                Log.i("TAG_PLACE_DETAIL", "Problem loading place detail " + e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            if (placeDetails != null) {
                setViews();
                setPager();
            } else {
                Toast.makeText(getActivity(), "Problem occurred while loading detail data", Toast.LENGTH_LONG);
            }

            ((MainActivity)getActivity()).hideLoadingDialog();
        }
    }

    private class OnShowReviewsClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {

            ((MainActivity) getActivity()).showReviewsScreen(placeDetails, mType);
        }
    }

    private class OnCallIntent implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            phoneCall();
        }
    }

    private class OnMapClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            ((MainActivity) getActivity()).showNearbyFragment(mType, placeDetails, false);
        }
    }

    private class OnFavIconClickListener implements View.OnClickListener {


        @Override
        public void onClick(View v) {
            mFavIcon.setSelected(!mFavIcon.isSelected());

            DatabaseManager.getInstance().performFavAction(placeDetails);
        }
    }
}
