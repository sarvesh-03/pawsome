package com.example.pawsome;

import com.google.gson.JsonObject;

import java.io.Serializable;

public class Animal1 implements Serializable {
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

    public JsonObject getImage() {
        return image;
    }

    public Animal1(Animal animal){
        id=animal.getId();
        name=animal.getName();
        temperament=animal.getTemperament();
        life_span=animal.getLife_span();
        bred_for=animal.getBred_for();
        reference_image_id=animal.getReference_image_id();
        origin=animal.getOrigin();
        breed_group=animal.getBreed_group();
        weight=animal.getWeight();
        height=animal.getHeight();
        image=animal.getImage();
    }

}
