package com.example.pawsome;

import android.preference.PreferenceManager;

public class Favs {

    private String image_id;
    private String sub_id;


    public Favs(String image_id, String sub_id) {
        this.image_id = image_id;
        this.sub_id = sub_id;
    }

    public String getImage_id() {
        return image_id;
    }

    public String getSub_id() {
        return sub_id;
    }

}
