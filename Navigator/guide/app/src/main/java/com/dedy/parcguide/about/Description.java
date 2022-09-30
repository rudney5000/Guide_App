package com.dedy.parcguide.about;

import org.simpleframework.xml.Element;

/**
 * Created by dedy ngueko on 12/3/2020.
 */
public class Description {

    @Element
    private String content;

    public String getContent() {

        if (content == null) {
            content = "";
        }

        return content;
    }
}
