package com.khedmap.khedmap.Order.Model;

import android.content.Context;

import com.khedmap.khedmap.Order.GetOrderDateTimeContract;
import com.khedmap.khedmap.Utilities.SharedPrefManager;

public class GetOrderDateTimeModel  implements GetOrderDateTimeContract.ModelCallBack {

    private SharedPrefManager sharedPrefManager;


    @Override
    public void setApiTokenSharedPref(String apiToken, Context context) {

        //to make sharedPrefManager object
        if (sharedPrefManager == null)
            sharedPrefManager = new SharedPrefManager(context);

        sharedPrefManager.saveApiToken(apiToken);
    }


    @Override
    public String getApiTokenSharedPref(Context context) {

        //to make sharedPrefManager object
        if (sharedPrefManager == null)
            sharedPrefManager = new SharedPrefManager(context);

        return sharedPrefManager.getApiToken();
    }


    @Override
    public String getNameSharedPref(Context context) {

        //to make sharedPrefManager object
        if (sharedPrefManager == null)
            sharedPrefManager = new SharedPrefManager(context);

        return sharedPrefManager.getName();
    }


    @Override
    public String getFamilySharedPref(Context context) {

        //to make sharedPrefManager object
        if (sharedPrefManager == null)
            sharedPrefManager = new SharedPrefManager(context);

        return sharedPrefManager.getFamily();
    }


    @Override
    public String getAvatarSharedPref(Context context) {

        //to make sharedPrefManager object
        if (sharedPrefManager == null)
            sharedPrefManager = new SharedPrefManager(context);

        return sharedPrefManager.getAvatar();
    }


    @Override
    public String getCreditSharedPref(Context context) {

        //to make sharedPrefManager object
        if (sharedPrefManager == null)
            sharedPrefManager = new SharedPrefManager(context);

        return sharedPrefManager.getCredit();
    }

}
