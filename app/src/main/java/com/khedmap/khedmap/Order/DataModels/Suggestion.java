package com.khedmap.khedmap.Order.DataModels;


public class Suggestion {

    private int id;
    private String expertName;
    private double expertRate;
    private String expertAvatar;
    private String basePrice;
    private String identification;
    private boolean isAds;
    private boolean isFavorite;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public String getExpertName() {
        return expertName;
    }

    public void setExpertName(String expertName) {
        this.expertName = expertName;
    }

    public double getExpertRate() {
        return expertRate;
    }

    public void setExpertRate(double expertRate) {
        this.expertRate = expertRate;
    }

    public String getExpertAvatar() {
        return expertAvatar;
    }

    public void setExpertAvatar(String expertAvatar) {
        this.expertAvatar = expertAvatar;
    }

    public String getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(String basePrice) {
        this.basePrice = basePrice;
    }

    public String getIdentification() {
        return identification;
    }

    public void setIdentification(String identification) {
        this.identification = identification;
    }


    public boolean isAds() {
        return isAds;
    }

    public void setAds(boolean ads) {
        isAds = ads;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }
}
