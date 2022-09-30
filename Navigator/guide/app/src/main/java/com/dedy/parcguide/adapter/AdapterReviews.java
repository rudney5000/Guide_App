package com.dedy.parcguide.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dedy.parcguide.R;
import com.dedy.parcguide.googleplaces.Constants;
import com.dedy.parcguide.googleplaces.models.PlaceReview;
import com.dedy.parcguide.util.UtilDate;
import com.dedy.parcguide.util.ImageOptionsBuilder;
import com.dedy.parcguide.util.UtilView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.Date;
import java.util.List;

/**
 * Created by dedy ngueko on 12/3/2020.
 */
public class AdapterReviews extends RecyclerView.Adapter<AdapterReviews.ViewHolder> {

    private final DisplayImageOptions mDisplayImageOptions;
    private final ImageLoader mImageLoader;
    private final Context mContext;
    private final Constants.PLACE_TYPES mType;
    private LayoutInflater mLayoutInflater;
    private List<PlaceReview> mAdapterData;

    public AdapterReviews(LayoutInflater layoutInflater, List<PlaceReview> adapterData, Context context, Constants.PLACE_TYPES type) {
        mLayoutInflater = layoutInflater;
        mAdapterData = adapterData;

        this.mDisplayImageOptions = ImageOptionsBuilder.buildGeneralImageOptions(true, 0, true);
        this.mImageLoader = ImageLoader.getInstance();
        this.mContext = context;
        this.mType = type;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = mLayoutInflater
                .inflate(R.layout.list_item_review, null);
        ViewHolder vh = new ViewHolder(v);

        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int i) {

        final PlaceReview currentPlaceReview = mAdapterData.get(i);

        if(currentPlaceReview.getAuthorPhotoUrl().isEmpty()){
            //mImageLoader.displayImage("drawable://" + Constants.PLACE_TYPES.getEmptyReviewImage(mType), viewHolder.avatar, mDisplayImageOptions);
            viewHolder.avatar.setBackgroundResource(Constants.PLACE_TYPES.getEmptyReviewImage(mType));
        }else{
            mImageLoader.displayImage(currentPlaceReview.getAuthorPhotoUrl(), viewHolder.avatar, mDisplayImageOptions);
        }

        viewHolder.name.setText(currentPlaceReview.getAuthorName());

        String formattedDate = UtilDate.REVIEW_DATE.format(new Date(currentPlaceReview.getDate()));
        viewHolder.date.setText(formattedDate);

        viewHolder.text.setText(currentPlaceReview.getText());

        if (currentPlaceReview.getRating() > 0) {
            viewHolder.rateValue.setText("" + currentPlaceReview.getRating());
        }

        viewHolder.rateValue.setTextColor(mContext.getResources().getColor(mType.getColourId()));

        viewHolder.rateContainer.removeAllViews();
        if (currentPlaceReview.getRating() > 0) {
            UtilView.getStarsByValue(currentPlaceReview.getRating(), viewHolder.rateContainer, getLayoutInflater(), mType);
        }

    }

    @Override
    public int getItemCount() {
        return mAdapterData.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView avatar;
        private TextView name;
        private TextView date;
        private TextView text;
        private LinearLayout rateContainer;
        private TextView rateValue;

        public ViewHolder(View itemView) {
            super(itemView);

            avatar = (ImageView) itemView.findViewById(R.id.list_item_review_avatar);
            name = (TextView) itemView.findViewById(R.id.list_item_review_name);
            date = (TextView) itemView.findViewById(R.id.list_item_review_date);
            text = (TextView) itemView.findViewById(R.id.list_item_review_text);
            rateContainer = (LinearLayout) itemView.findViewById(R.id.list_item_review_rate_container);
            rateValue = (TextView) itemView.findViewById(R.id.list_item_review_rate_value);
        }
    }

    private LayoutInflater getLayoutInflater() {
        return LayoutInflater.from(mContext);
    }


}
