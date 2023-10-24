package com.khedmap.khedmap.Order.DataModels;


import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class Slide {

    private int id;
    @NonNull
    private String picture;
    @NonNull
    private String target;
    @Nullable
    private String link;
    @Nullable
    private String type;
    @Nullable
    private String identification;
    @Nullable
    private String title;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    @NonNull
    public String getPicture() {
        return picture;
    }

    public void setPicture(@NonNull String picture) {
        this.picture = picture;
    }

    @NonNull
    public String getTarget() {
        return target;
    }

    public void setTarget(@NonNull String target) {
        this.target = target;
    }

    @Nullable
    public String getLink() {
        return link;
    }

    public void setLink(@Nullable String link) {
        this.link = link;
    }

    @Nullable
    public String getType() {
        return type;
    }

    public void setType(@Nullable String type) {
        this.type = type;
    }

    @Nullable
    public String getIdentification() {
        return identification;
    }

    public void setIdentification(@Nullable String identification) {
        this.identification = identification;
    }

    @Nullable
    public String getTitle() {
        return title;
    }

    public void setTitle(@Nullable String title) {
        this.title = title;
    }
}
