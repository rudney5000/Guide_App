package com.dedy.parcguide.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dedy.parcguide.R;
import com.dedy.parcguide.util.ImageOptionsBuilder;
import com.dedy.parcguide.util.UtilView;
import com.dedy.parcguide.weather.cmn.ForecastDay;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

/**
 * Created by dedy ngueko on 12/3/2020.
 */
public class AdapterForecast extends RecyclerView.Adapter<AdapterForecast.ViewHolder> {

    private final DisplayImageOptions mDisplayImageOptions;
    private final ImageLoader mImageLoader;
    private final Context mContext;
    private LayoutInflater mLayoutInflater;
    private List<ForecastDay> mAdapterData;

    public AdapterForecast(LayoutInflater layoutInflater, List<ForecastDay> adapterData, Context context) {
        mLayoutInflater = layoutInflater;
        mAdapterData = adapterData;

        this.mDisplayImageOptions = ImageOptionsBuilder.buildGeneralImageOptions(true, 0, false);
        this.mImageLoader = ImageLoader.getInstance();
        this.mContext = context;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = mLayoutInflater
                .inflate(R.layout.list_item_forecast, null);
        ViewHolder vh = new ViewHolder(v);

        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        ForecastDay currentForecastDay = mAdapterData.get(position);

        if(position % 2 != 0){
            holder.parentView.setGravity(Gravity.RIGHT);
        }


        holder.date.setText(currentForecastDay.getDate());
        holder.weather.setText(currentForecastDay.getWeatherDesc());
        holder.temperature.setText(currentForecastDay.getTemperature());

        holder.icon.setBackgroundResource(UtilView.getDrawableIdByString(mContext, currentForecastDay.getIconUrl()));

//        mImageLoader.displayImage(currentForecastDay.getIconUrl(), holder.icon, mDisplayImageOptions);

    }

    @Override
    public int getItemCount() {
        return mAdapterData.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        RelativeLayout parentView;
        TextView date;
        TextView temperature;
        TextView weather;
        ImageView icon;

        public ViewHolder(View itemView) {
            super(itemView);

            parentView = (RelativeLayout) itemView.findViewById(R.id.list_item_forecast_container);
            date = (TextView) itemView.findViewById(R.id.list_item_forecast_date);
            temperature = (TextView) itemView.findViewById(R.id.list_item_forecast_temp);
            weather = (TextView) itemView.findViewById(R.id.list_item_forecast_weather);
            icon = (ImageView) itemView.findViewById(R.id.list_item_forecast_icon);
        }
    }

    private LayoutInflater getLayoutInflater() {
        return LayoutInflater.from(mContext);
    }


}