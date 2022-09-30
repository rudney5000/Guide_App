package com.dedy.parcguide.fragment;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dedy.parcguide.ParcGuideApp;
import com.dedy.parcguide.MainActivity;
import com.dedy.parcguide.R;
import com.dedy.parcguide.adapter.AdapterReviews;
import com.dedy.parcguide.googleplaces.Constants;
import com.dedy.parcguide.googleplaces.GooglePlaces;
import com.dedy.parcguide.googleplaces.models.PlaceDetails;
import com.dedy.parcguide.googleplaces.models.PlaceReview;
import comdedy.parcguide.util.UtilView;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;

/**
 * Created by dedy ngueko on 08/4/2020.
 */
public class FragmentReviews extends Fragment {

    public static final String TAG = FragmentReviews.class.getSimpleName();
    private List<PlaceReview> mAdapterData;
    private Constants.PLACE_TYPES mPlacesType;
    private AdapterReviews mAdapter;
    private TextView abTitle;
    private TextView abSubTitle;
    private View abContainer;
    private View abArrow;
    private PlaceDetails mPlaceDetail;

    public static FragmentReviews newInstance(PlaceDetails placeDetails, Constants.PLACE_TYPES placeType) {
        FragmentReviews fragmentReviews = new FragmentReviews();
        fragmentReviews.mPlaceDetail = placeDetails;
        fragmentReviews.mPlacesType = placeType;

        return fragmentReviews;
    }

    private RecyclerView mRecyclerView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fra_reviews, null);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        abContainer = getView().findViewById(R.id.fra_reviews_ab_container);
        UtilView.setNoLollipopPadding(abContainer, 0, 0);

        if (!UtilView.isLollipop()) {
            UtilView.setViewHeight(abContainer, 50);
        }

        abContainer.setBackgroundColor(getResources().getColor(mPlacesType.getColourId()));

        abTitle = (TextView) getView().findViewById(R.id.fra_reviews_ab_title);
        abTitle.setSelected(true);

        abSubTitle = (TextView) getView().findViewById(R.id.fra_reviews_ab_subtitle);

        abArrow = getView().findViewById(R.id.fra_reviews_ab_back_arrow);
        abArrow.setOnClickListener(new OnBackClickListener());

        UtilView.setLollipopPadding(abArrow, 0, 10, 0, 0);

        mRecyclerView = (RecyclerView) getView().findViewById(R.id.fra_reviews_recyclerview);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(layoutManager);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            new LoadReviewsPhotos().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            new LoadReviewsPhotos().execute();
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

    private class OnBackClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            getActivity().getSupportFragmentManager().popBackStack();
        }
    }

    private class LoadReviewsPhotos extends AsyncTask<Void, Void, Void> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            ((MainActivity) getActivity()).showLoadingDialog();
        }

        @Override
        protected Void doInBackground(Void... params) {

            GooglePlaces googlePlaces = new GooglePlaces(new LatLng(mPlaceDetail.getLatitude(), mPlaceDetail.getLongitude()));

            try {
                googlePlaces.getPlaceDetailsReviews(mPlaceDetail);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            if (isAdded()) {
                ((MainActivity) getActivity()).hideLoadingDialog();

                mAdapterData = mPlaceDetail.getReviews();

                abTitle.setText(mPlaceDetail.getName());
                abSubTitle.setText(mAdapterData.size() + " Reviews - " + mPlaceDetail.getRatingValue());

                mAdapter = new AdapterReviews(getActivity().getLayoutInflater(), mAdapterData, getActivity(), mPlacesType);

                mRecyclerView.setAdapter(mAdapter);
            }
        }
    }
}
