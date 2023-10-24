package com.khedmap.khedmap.Utilities;


import android.content.Context;
import android.content.SharedPreferences;


public class SharedPrefManager {

    private static final String SHARED_PREF = "shared_pref";

    private static final String KEY_API_TOKEN = "api_token";
    private static final String KEY_NAME = "name";
    private static final String KEY_FAMILY = "family";
    private static final String KEY_AVATAR = "avatar";
    private static final String KEY_CREDIT = "credit";
    private static final String KEY_SUPPORT_PHONE = "supportPhone";
    private static final String KEY_TERMS = "terms";
    private static final String KEY_GUIDE_TOUR_SHOWED = "guideTourShowed";

    private SharedPreferences sharedPreferences;


    public SharedPrefManager(Context context) {
        sharedPreferences = context.getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE);
    }



    public void saveApiToken(String apiToken) {
        if (apiToken != null) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(KEY_API_TOKEN, apiToken);
            editor.apply();
        }
    }

    public void saveName(String name) {
        if (name != null) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(KEY_NAME, name);
            editor.apply();
        }
    }

    public void saveFamily(String family) {
        if (family != null) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(KEY_FAMILY, family);
            editor.apply();
        }
    }

    public void saveAvatar(String avatar) {
        if (avatar != null) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(KEY_AVATAR, avatar);
            editor.apply();
        }
    }

    public void saveCredit(String credit) {
        if (credit != null) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(KEY_CREDIT, credit);
            editor.apply();
        }
    }

    public void saveSupportPhone(String supportPhone) {
        if (supportPhone != null) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(KEY_SUPPORT_PHONE, supportPhone);
            editor.apply();
        }
    }

    public void saveTerms(String terms) {
        if (terms != null) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(KEY_TERMS, terms);
            editor.apply();
        }
    }

    public void saveGuideTourShowed() {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(KEY_GUIDE_TOUR_SHOWED, true);
            editor.apply();
    }


    public String getApiToken() {
        return sharedPreferences.getString(KEY_API_TOKEN, "");
    }

    public String getName() {
        return sharedPreferences.getString(KEY_NAME, "");
    }

    public String getFamily() {
        return sharedPreferences.getString(KEY_FAMILY, "");
    }

    public String getAvatar() {
        return sharedPreferences.getString(KEY_AVATAR, "");
    }

    public String getCredit() {
        return sharedPreferences.getString(KEY_CREDIT, "");
    }

    public String getSupportPhone() {
        return sharedPreferences.getString(KEY_SUPPORT_PHONE, "");
    }

    public String getTerms() {
        return sharedPreferences.getString(KEY_TERMS, "");
    }

    public boolean getGuideTourShowed() {
        return sharedPreferences.getBoolean(KEY_GUIDE_TOUR_SHOWED, false);
    }

}

