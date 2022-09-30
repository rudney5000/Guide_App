package com.dedy.parcguide.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.dedy.parcguide.R;
import com.dedy.parcguide.about.Photo;
import com.dedy.parcguide.util.ImageOptionsBuilder;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

/**
 * Created by dedy ngueko on 12/3/2020.
 */
public class AdapterAbout extends ArrayAdapter<Photo> {

    private final List<Photo> mAdapterData;
    private final Context mContext;
    private final DisplayImageOptions mDisplayImageOptions;
    private final ImageLoader mImageLoader;

    public AdapterAbout(List<Photo> adapterData, Context context) {
        super(context, 0, adapterData);
        this.mAdapterData = adapterData;
        this.mContext = context;

        this.mDisplayImageOptions = ImageOptionsBuilder.buildGeneralImageOptions(true, 0, false);
        this.mImageLoader = ImageLoader.getInstance();

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        AboutHolder holder = null;

        if (convertView == null) {
            holder = new AboutHolder();

            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_about, null);

            holder.imageView = (ImageView) convertView.findViewById(R.id.list_item_about_image);

            convertView.setTag(holder);

        } else {
            holder = (AboutHolder) convertView.getTag();
        }

        Photo currentItem = getItem(position);

        if (currentItem.getPath().startsWith("http")) {
            mImageLoader.displayImage(currentItem.getPath(), holder.imageView, mDisplayImageOptions);
        } else {
            holder.imageView.setImageDrawable(mContext.getResources()
                    .getDrawable(currentItem.getDrawableId(mContext)));
        }

        return convertView;
    }

    public static class AboutHolder {

        ImageView imageView;

    }

}
