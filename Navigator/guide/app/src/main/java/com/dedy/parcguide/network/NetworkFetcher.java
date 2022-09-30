package com.dedy.parcguide.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.dedy.parcguide.db.DatabaseManager;
import com.dedy.parcguide.db.cmn.DbNetwork;

import org.apache.commons.io.IOUtils;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;


public class NetworkFetcher {

    public static JSONObject executeRequest(String query, boolean useOffline) {

        InputStream in = null;
        HttpURLConnection conn = null;
        String response = null;
        try {

            DbNetwork dbNetwork = null;

            if (useOffline) {
                dbNetwork = DatabaseManager.getInstance().findNetworkQuery(query);
            }

            if (dbNetwork == null) {

                URL url = null;

                if (query.indexOf(";") > -1) {
                    url = new URL(query.split(";")[1]);
                } else {
                    url = new URL(query);

                }

                conn = (HttpURLConnection) url.openConnection();

                // read the response
                in = new BufferedInputStream(conn.getInputStream());
                response = IOUtils.toString(in, "UTF-8");
                //response = in.toString();

                DatabaseManager.getInstance().saveNetworkQuery(query, response);
            } else {
                response = dbNetwork.getValue();
            }

            return new JSONObject(response);
        } catch (Exception e) {
            Log.i("City_Guide_Error", "Error requesting places " + e.getMessage());
        } finally {
            try {
                conn.disconnect();
                in.close();
            } catch (Exception e) {
            }
        }

        return null;
    }

    /**
     * Checks if the device is connected to a network.
     *
     * @param context application context
     * @return true if the device is connected
     */
    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni == null) {
            // There are no active networks.
            return false;
        } else
            return true;
    }
}
