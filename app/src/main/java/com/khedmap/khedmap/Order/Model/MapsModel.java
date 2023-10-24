package com.khedmap.khedmap.Order.Model;

import android.content.Context;

import com.khedmap.khedmap.Order.MapsContract;
import com.khedmap.khedmap.Utilities.SharedPrefManager;

public class MapsModel  implements MapsContract.ModelCallBack {

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

}
