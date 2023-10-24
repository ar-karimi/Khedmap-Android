package com.khedmap.khedmap.LoginSignUp.Model;

import android.content.Context;
import android.net.Uri;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.request.SimpleMultiPartRequest;
import com.android.volley.toolbox.Volley;
import com.khedmap.khedmap.LoginSignUp.ProfilePicGenderRegisterContract;
import com.khedmap.khedmap.Utilities.SharedPrefManager;
import com.khedmap.khedmap.Utilities.UtilitiesSingleton;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class ProfilePicGenderRegisterModel implements ProfilePicGenderRegisterContract.ModelCallBack {


    private SharedPrefManager sharedPrefManager;

    @Override
    public void sendSubmitRequest(Context context, Response.Listener responseListener, Response.ErrorListener errorListener
            , String[] nameRegisterInfo, String gender, Uri resultUri, String reagentCode) {


        String SUBMIT_REGISTER_URL = UtilitiesSingleton.getInstance().getBaseURL() + "register";

        SimpleMultiPartRequest smr = new SimpleMultiPartRequest(Request.Method.POST, SUBMIT_REGISTER_URL, responseListener, errorListener) {

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Accept", "application/json");

                return params;
            }

        };


        smr.addStringParam("api_token", nameRegisterInfo[0]);

        smr.addStringParam("name", nameRegisterInfo[2]);
        smr.addStringParam("family", nameRegisterInfo[3]);

        smr.addStringParam("gender", gender);

        if (resultUri != null) {
            File mFile = new File(resultUri.getPath());
            smr.addFile("avatar", mFile.getAbsolutePath());
        }

        if (!reagentCode.isEmpty()) //because is optional
            smr.addStringParam("reagent_code", reagentCode);

        RequestQueue mRequestQueue = Volley.newRequestQueue(context.getApplicationContext());
        mRequestQueue.add(smr);


    }


    @Override
    public void setApiTokenSharedPref(String apiToken, Context context) {

        //to make sharedPrefManager object
        if (sharedPrefManager == null)
            sharedPrefManager = new SharedPrefManager(context);

        sharedPrefManager.saveApiToken(apiToken);
    }

    @Override
    public void setNameSharedPref(String name, Context context) {

        //to make sharedPrefManager object
        if (sharedPrefManager == null)
            sharedPrefManager = new SharedPrefManager(context);

        sharedPrefManager.saveName(name);
    }

    @Override
    public void setFamilySharedPref(String family, Context context) {

        //to make sharedPrefManager object
        if (sharedPrefManager == null)
            sharedPrefManager = new SharedPrefManager(context);

        sharedPrefManager.saveFamily(family);
    }

    @Override
    public void setAvatarSharedPref(String avatar, Context context) {

        //to make sharedPrefManager object
        if (sharedPrefManager == null)
            sharedPrefManager = new SharedPrefManager(context);

        sharedPrefManager.saveAvatar(avatar);
    }

    @Override
    public void setCreditSharedPref(String credit, Context context) {

        //to make sharedPrefManager object
        if (sharedPrefManager == null)
            sharedPrefManager = new SharedPrefManager(context);

        sharedPrefManager.saveCredit(credit);
    }

}
