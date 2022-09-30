package com.dedy.parcguide.db;

import android.content.Context;
import android.util.Log;

import com.dedy.parcguide.db.cmn.DbFavPlace;
import com.dedy.parcguide.db.cmn.DbNetwork;
import com.dedy.parcguide.googleplaces.models.Place;
import com.dedy.parcguide.googleplaces.models.PlaceDetails;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by dedy ngueko on 12/3/2020.
 */
public class DatabaseManager {

    public static final String TAG = DatabaseManager.class.getSimpleName();

    public static DatabaseManager instance;
    private Context mContext;
    private DatabaseHelper mHelper;
    private List<DbFavPlace> mAllFavItems;

    public static DatabaseManager getInstance() {

        if (instance == null) {
            synchronized (DatabaseManager.class) {
                if (instance == null) {
                    instance = new DatabaseManager();
                }
            }
        }

        return instance;
    }

    public static void initWithContext(Context ctx) {

        getInstance().mContext = ctx;
        getInstance().mHelper = new DatabaseHelper(ctx);
        getInstance().requestAllFavPlaces();
    }

    private DatabaseManager() {
    }

    public void requestAllFavPlaces() {
        mAllFavItems = new ArrayList<DbFavPlace>();

        try {
            mAllFavItems = mHelper.getFavItemDao().queryForAll();

        } catch (java.sql.SQLException e) {
            Log.i(TAG, "Error fetching records");
        }
    }

    public Set<Place> getRealFavPlace() {
        Set<Place> realFavItems = new HashSet<>();

        for (int i = 0; i < mAllFavItems.size(); i++) {
            try {
                JSONObject currentPlace = new JSONObject(mAllFavItems.get(i).getPlaceJson());

                realFavItems.add(new PlaceDetails(currentPlace));
            } catch (JSONException e) {
                Log.i(TAG, "Error creating fav items");
            }
        }

        return realFavItems;

    }

    public int createFavPlace(DbFavPlace dbFavPlace) {
        try {
            mHelper.getFavItemDao().createOrUpdate(dbFavPlace);

            return dbFavPlace.getId();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1;
    }

    public void deleteFavPlace(long favPlaceId) {
        try {
            mHelper.getFavItemDao().deleteById(favPlaceId);

            requestAllFavPlaces();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void performFavAction(PlaceDetails placeDetails) {
        requestAllFavPlaces();

        // First we iterate. If find then delete it.
        for (int i = 0; i < mAllFavItems.size(); i++) {
            if (mAllFavItems.get(i).getPlaceId().equalsIgnoreCase(placeDetails.getId())) {

                deleteFavPlace(mAllFavItems.get(i).getId());

                return;
            }
        }

        // If we are so new item need to be inserted
        DbFavPlace newItem = new DbFavPlace();
        newItem.setPlaceId(placeDetails.getId());
        newItem.setPlaceJson(placeDetails.getJsonString());

        createFavPlace(newItem);

        requestAllFavPlaces();
    }

    public boolean getState(String id) {
        requestAllFavPlaces();

        for (int i = 0; i < mAllFavItems.size(); i++) {
            if (mAllFavItems.get(i).getPlaceId().equalsIgnoreCase(id)) {
                return true;
            }
        }

        return false;
    }

    public void saveNetworkQuery(String key, String value) {
        try {
            List<DbNetwork> findedResult = mHelper.getNetworkDao().queryForEq("key", key);
            if (findedResult.size() > 0) {
                mHelper.getNetworkDao().delete(findedResult.get(0));
            }

            DbNetwork dbNetwork = new DbNetwork();
            dbNetwork.setKey(key);
            dbNetwork.setValue(value);

            mHelper.getNetworkDao().createOrUpdate(dbNetwork);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public DbNetwork findNetworkQuery(String key) {

        try {
            List<DbNetwork> result = mHelper.getNetworkDao().queryForEq("key", key);

            if (result != null && result.size() > 0) {
                return result.get(0);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

}
