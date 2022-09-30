package com.dedy.parcguide.about;

import android.content.Context;

import org.simpleframework.xml.Element;

/**
 * Created by dedy ngueko on 12/3/2020.
 */
public class Photo {

    @Element
    private String path;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }



    public int getDrawableId(Context context) {
        return context.getResources().getIdentifier(path, "drawable",
                context.getPackageName());
    }
}
