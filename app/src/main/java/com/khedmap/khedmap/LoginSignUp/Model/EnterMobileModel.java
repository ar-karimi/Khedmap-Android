package com.khedmap.khedmap.LoginSignUp.Model;

import android.content.Context;
import android.content.Intent;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.request.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.auth.api.credentials.Credential;
import com.khedmap.khedmap.LoginSignUp.EnterMobileContract;
import com.khedmap.khedmap.Utilities.SharedPrefManager;
import com.khedmap.khedmap.Utilities.UtilitiesSingleton;

import java.util.HashMap;
import java.util.Map;

import static android.app.Activity.RESULT_OK;


public class EnterMobileModel implements EnterMobileContract.ModelCallBack {

    private static final int RC_HINT = 1000;
    private SharedPrefManager sharedPrefManager;


    //to get Phone Number from google api
    @Override
    public String getRetrievedPhoneNumber(int requestCode, int resultCode, Intent data) {

        if (requestCode == RC_HINT) {
            if (resultCode == RESULT_OK) {
                Credential credential = data.getParcelableExtra(Credential.EXTRA_KEY);
                // credential.getId(); <-- E.164 format phone number on 10.2.+ devices
                return credential.getId(); //return Phone Number
            }
        }
        return null;

    }
//till here


    @Override
    public void sendRequest(Context context, CharSequence mobileNumber,
                            Response.Listener responseListener, Response.ErrorListener errorListener) {

        RequestQueue queue = Volley.newRequestQueue(context.getApplicationContext());

        String REGISTER_URL = UtilitiesSingleton.getInstance().getBaseURL() + "login";

        final String finalMobileNumber = mobileNumber.toString();


        StringRequest postRequest = new StringRequest(Request.Method.POST, REGISTER_URL, responseListener, errorListener) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Accept", "application/json");
                params.put("phone", finalMobileNumber);

                return params;
            }
        };

        queue.add(postRequest);

    }


    @Override
    public void setApiTokenSharedPref(String apiToken, Context context) {

        //to make sharedPrefManager object
        sharedPrefManager = new SharedPrefManager(context);

        sharedPrefManager.saveApiToken(apiToken);
    }

}
