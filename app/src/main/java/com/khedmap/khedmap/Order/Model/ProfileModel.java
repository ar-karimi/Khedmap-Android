package com.khedmap.khedmap.Order.Model;

import android.content.Context;
import android.net.Uri;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyLog;
import com.android.volley.error.AuthFailureError;
import com.android.volley.request.SimpleMultiPartRequest;
import com.android.volley.request.StringRequest;
import com.android.volley.toolbox.Volley;
import com.khedmap.khedmap.Order.ProfileContract;
import com.khedmap.khedmap.Order.VerifyFinishOrderContract;
import com.khedmap.khedmap.Utilities.SharedPrefManager;
import com.khedmap.khedmap.Utilities.UtilitiesSingleton;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProfileModel  implements ProfileContract.ModelCallBack {

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
    public void sendGetInformationRequest(Context context, final String requestBody, Response.Listener responseListener, Response.ErrorListener errorListener) {


        RequestQueue queue = Volley.newRequestQueue(context.getApplicationContext());

        String URL = UtilitiesSingleton.getInstance().getBaseURL() + "getInformation";


        StringRequest postRequest = new StringRequest(Request.Method.POST, URL, responseListener, errorListener) {
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
    public void sendUpdateProfileRequest(Context context, Response.Listener responseListener, Response.ErrorListener errorListener
            , List<String> profileInfo, Uri resultUri) {


        String SUBMIT_REGISTER_URL = UtilitiesSingleton.getInstance().getBaseURL() + "updateProfile";

        SimpleMultiPartRequest smr = new SimpleMultiPartRequest(Request.Method.POST, SUBMIT_REGISTER_URL, responseListener, errorListener) {

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Accept", "application/json");

                return params;
            }

        };


        smr.addStringParam("api_token", profileInfo.get(0));

        smr.addStringParam("firstName", profileInfo.get(1));
        smr.addStringParam("lastName", profileInfo.get(2));
        smr.addStringParam("gender", profileInfo.get(3));

        if (resultUri != null) {
            File mFile = new File(resultUri.getPath());
            smr.addFile("avatar", mFile.getAbsolutePath());
        }

        RequestQueue mRequestQueue = Volley.newRequestQueue(context.getApplicationContext());
        mRequestQueue.add(smr);


    }

}
