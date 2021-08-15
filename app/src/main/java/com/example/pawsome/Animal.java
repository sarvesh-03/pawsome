package com.example.pawsome;

import com.google.gson.JsonObject;

import java.io.Serializable;

public class Animal {
    private String id;
    private String name;
    private String temperament;
    private String life_span;
    private String bred_for;
    private String reference_image_id;
    private String origin;
    private String breed_group;
    private JsonObject weight;
    private JsonObject height;
    private JsonObject image;

    public JsonObject getImage() {
        return image;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getTemperament() {
        return temperament;
    }

    public String getLife_span() {
        return life_span;
    }

    public String getBred_for() {
        return bred_for;
    }

    public String getReference_image_id() {
        return reference_image_id;
    }

    public String getOrigin() {
        return origin;
    }

    public String getBreed_group() {
        return breed_group;
    }

    public JsonObject getWeight() {
        return weight;
    }

    public JsonObject getHeight() {
        return height;
    }

    public  void SetJsonObj(){
        image=new JsonObject();
    }
}
