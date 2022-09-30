package com.dedy.parcguide.about;

/**
 * Created by dedy ngueko on 12/3/2020.
 */

import android.content.Context;
import android.util.Log;

import org.simpleframework.xml.core.Persister;

import java.io.InputStream;

/**
 * Created by dedy ngueko on 12/3/2020.
 */
public class AboutDataHolder {

    public static AboutDataHolder instance;
    private Persister mSerializer;
    private About mAbout;

    private AboutDataHolder() {

    }

    public static AboutDataHolder getInstance() {
        if (instance == null) {
            synchronized (AboutDataHolder.class) {
                if (instance == null) {
                    instance = new AboutDataHolder();
                }
            }
        }

        return instance;
    }

    public void parseData(Context c, InputStream inputStream) {
        try {
            mSerializer = new Persister();

            mAbout = mSerializer.read(About.class, inputStream);

            mAbout.getDescription().getContent();

        } catch (Exception e) {
            Log.i("AboutDataHolder", "Message - " + e.getMessage());
        }
    }

    public About getAbout() {
        return mAbout;
    }
}