package com.dedy.parcguide.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.dedy.parcguide.R;
import com.dedy.parcguide.googleplaces.Constants;
import com.dedy.parcguide.util.ImageOptionsBuilder;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

/**
 * Created by dedy ngueko on 12/3/2020.
 */
public class AdapterSlider extends PagerAdapter {


    /**
     * The Context.
     */
    private Context mContext;

    /**
     * The Inflater.
     */
    private LayoutInflater mInflater;

    /**
     * The Adapter data.
     */
    private List<String> mAdapterData;

    /**
     * The Display image options.
     */
    private DisplayImageOptions mDisplayImageOptions;

    /**
     * The Image loader.
     */
    private ImageLoader mImageLoader;

    public AdapterSlider(Context context, List<String> adapterData, Constants.PLACE_TYPES type) {
        this.mContext = context;
        this.mInflater = LayoutInflater.from(this.mContext);

        this.mAdapterData = adapterData;

        this.mDisplayImageOptions = ImageOptionsBuilder.buildGeneralImageOptions(true, Constants.PLACE_TYPES.getEmptyImagePlaceHolder(type), false);

        this.mImageLoader = ImageLoader.getInstance();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        String currentItem = mAdapterData.get(position);

        RelativeLayout parentView = (RelativeLayout) mInflater.inflate(R.layout.pager_item_main, null);

        ImageView image = (ImageView) parentView.findViewById(R.id.pager_item_main_image);

        mImageLoader.displayImage(currentItem, image, mDisplayImageOptions);

        container.addView(parentView, 0);

        return parentView;
    }

    @Override
    public int getCount() {
        return mAdapterData.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return (view == object);
    }

    @Override
    public void destroyItem(ViewGroup collection, int position, Object view) {
        collection.removeView((RelativeLayout) view);
    }
}
