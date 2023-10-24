package com.khedmap.khedmap.LoginSignUp.Model;

import android.content.Context;

import com.khedmap.khedmap.LoginSignUp.NameRegisterContract;
import com.khedmap.khedmap.Utilities.SharedPrefManager;

public class NameRegisterModel implements NameRegisterContract.ModelCallBack {

    private SharedPrefManager sharedPrefManager;

    @Override
    public String getTermsSharedPref(Context context) {

        //to make sharedPrefManager object
        if (sharedPrefManager == null)
            sharedPrefManager = new SharedPrefManager(context);

        return sharedPrefManager.getTerms();
    }

}
