package com.dedy.parcguide.about;

import android.app.Activity;
import android.os.AsyncTask;

import com.dmbteam.cityguide.settings.AppSettings;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

/**
 * Created by dedy ngueko on 12/3/2020.
 */
public class AboutDataLoader {

    public void loadData(Activity context) {
        new XmlNetworkStreamReader(context).execute();
    }


    private class XmlNetworkStreamReader extends
            AsyncTask<Void, Void, Void> {

        /**
         * The context.
         */
        private Activity context;

        /**
         * Instantiates a new catalog xml network strem reader.
         *
         * @param c the c
         */
        public XmlNetworkStreamReader(Activity c) {
            this.context = c;
        }

        /* (non-Javadoc)
         * @see android.os.AsyncTask#doInBackground(java.lang.Object[])
         */
        @Override
        protected synchronized Void doInBackground(Void... params) {

            InputStream inputStream = null;

            try {

                if (AppSettings.XMLResourcePath.startsWith("http")) {

                    DefaultHttpClient client = new DefaultHttpClient();

                    HttpGet method = new HttpGet(new URI(
                            AppSettings.XMLResourcePath));

                    HttpResponse res = client.execute(method);
                    res = client.execute(method);

                    inputStream = res.getEntity().getContent();

                } else {
                    inputStream = context.getAssets().open(AppSettings.XMLResourcePath);
                }


                if (inputStream != null) {
                    AboutDataHolder.getInstance().parseData(context, inputStream);
                }

            } catch (Exception e) {

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

        }

        public long LastModified(String url) {
            try {
                HttpURLConnection.setFollowRedirects(false);
                HttpURLConnection con = null;
                con = (HttpURLConnection) new URL(url).openConnection();
                long date = con.getLastModified();

                return date;
            } catch (IOException e) {
                e.printStackTrace();
            }

            return 0;
        }
    }
}
