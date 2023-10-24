package com.khedmap.khedmap.LoginSignUp.Model;

import android.app.Activity;
import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyLog;
import com.android.volley.error.AuthFailureError;
import com.android.volley.request.StringRequest;
import com.android.volley.toolbox.Volley;
import com.khedmap.khedmap.LoginSignUp.SplashContract;
import com.khedmap.khedmap.Utilities.SharedPrefManager;
import com.khedmap.khedmap.Utilities.UtilitiesSingleton;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class SplashModel implements SplashContract.ModelCallBack {

    private SharedPrefManager sharedPrefManager;

    private RequestQueue queue;
    private StringRequest checkValidTokenPostRequest;


    @Override
    public void sendValidateRequest(Activity activity, final String requestBody,
                                    Response.Listener responseListener, Response.ErrorListener errorListener) {

        queue = Volley.newRequestQueue(activity);

        String CHECK_VALID_TOKEN_URL = UtilitiesSingleton.getInstance().getBaseURL() + "checkValidToken";

        checkValidTokenPostRequest = new StringRequest(Request.Method.POST, CHECK_VALID_TOKEN_URL, responseListener, errorListener) {

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


    }


    @Override
    public void sendUpdateRequest(Response.Listener responseListener, Response.ErrorListener errorListener) {


        String CHECK_VERSION_URL = UtilitiesSingleton.getInstance().getBaseURL() + "checkVersion";

        StringRequest checkVersionPostRequest = new StringRequest(Request.Method.POST, CHECK_VERSION_URL, responseListener, errorListener) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Accept", "application/json");
                params.put("os", "android");

                return params;
            }
        };


        queue.add(checkVersionPostRequest);

    }

    @Override
    public void addToQueueCheckValidTokenRequest () {

        if (checkValidTokenPostRequest != null) {
            queue.add(checkValidTokenPostRequest);
        }
    }

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
    public void setSupportPhoneSharedPref(String value, Context context) {

        //to make sharedPrefManager object
        if (sharedPrefManager == null)
            sharedPrefManager = new SharedPrefManager(context);

        sharedPrefManager.saveSupportPhone(value);
    }

    @Override
    public void setTermsSharedPref(String value, Context context) {

        //to make sharedPrefManager object
        if (sharedPrefManager == null)
            sharedPrefManager = new SharedPrefManager(context);

        sharedPrefManager.saveTerms(value);
    }

}
