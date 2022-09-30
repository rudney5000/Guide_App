package com.dedy.parcguide.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.dedy.parcguide.R;
import com.dedy.parcguide.googleplaces.Constants;
import com.dedy.parcguide.googleplaces.models.Place;

import java.util.ArrayList;
import java.util.Set;

/**
 * Created by dedy ngueko on 12/3/2020.
 */
public class AdapterSearch extends ArrayAdapter<Place>{

    public AdapterSearch(Context context, Set<Place> adapterData) {
        super(context, 0, new ArrayList<Place>(adapterData));
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Place currentPlaceDetail = getItem(position);

        SearchViewHolder holder;

        if (convertView == null) {
            convertView = getInflater().inflate(R.layout.list_item_search, null);

            holder = new SearchViewHolder();

            holder.icon = (ImageView) convertView.findViewById(R.id.list_item_search_icon);

            if (currentPlaceDetail.getPlaceType() != null) {
                int iconId = Constants.PLACE_TYPES.getSearchIcon(currentPlaceDetail.getPlaceType());

                if (iconId > 0) {
                    holder.icon.setBackgroundResource(iconId);
                }
            }

            holder.label = (TextView) convertView.findViewById(R.id.list_item_search_label);
            holder.rating = (TextView) convertView.findViewById(R.id.list_item_search_rating);

            convertView.setTag(holder);
        } else {
            holder = (SearchViewHolder) convertView.getTag();
        }

        holder.label.setText(currentPlaceDetail.getName());
        holder.rating.setText("Rating: " + currentPlaceDetail.getRatingValue());

        return convertView;
    }

    private LayoutInflater getInflater() {
        return LayoutInflater.from(getContext());
    }

    public static class SearchViewHolder {
        ImageView icon;
        TextView label;
        TextView rating;

    }
}
