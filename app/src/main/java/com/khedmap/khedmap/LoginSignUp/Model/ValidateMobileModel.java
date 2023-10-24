package com.khedmap.khedmap.LoginSignUp.Model;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyLog;
import com.android.volley.error.AuthFailureError;
import com.android.volley.request.StringRequest;
import com.android.volley.toolbox.Volley;
import com.khedmap.khedmap.LoginSignUp.ValidateMobileContract;
import com.khedmap.khedmap.Utilities.SharedPrefManager;
import com.khedmap.khedmap.Utilities.UtilitiesSingleton;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class ValidateMobileModel implements ValidateMobileContract.ModelCallBack {

    private SharedPrefManager sharedPrefManager;

    @Override
    public void resendSmsRequest(Context context, final String mobileNumber,
                                 Response.Listener responseListener, Response.ErrorListener errorListener) {

        RequestQueue queue = Volley.newRequestQueue(context.getApplicationContext());

        String REGISTER_URL = UtilitiesSingleton.getInstance().getBaseURL() + "login";


        StringRequest postRequest = new StringRequest(Request.Method.POST, REGISTER_URL, responseListener, errorListener) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Accept", "application/json");
                params.put("phone", mobileNumber);

                return params;
            }
        };

        queue.add(postRequest);

    }


    @Override
    public void sendSubmitRequest(Context context, final String requestBody, Response.Listener responseListener, Response.ErrorListener errorListener) {

        RequestQueue queue = Volley.newRequestQueue(context.getApplicationContext());

        String VALIDATE_REGISTER_URL = UtilitiesSingleton.getInstance().getBaseURL() + "verifyCode";


        StringRequest postRequest = new StringRequest(Request.Method.POST, VALIDATE_REGISTER_URL, responseListener, errorListener) {
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Accept", "application/json");

                return params;
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                try {
                    return requestBody == null ? null : requestBody.getBytes("utf-8");
                } catch (UnsupportedEncodingException uee) {
                    VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", requestBody, "utf-8");
                    return null;
                }
            }


        };
        queue.add(postRequest);
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
