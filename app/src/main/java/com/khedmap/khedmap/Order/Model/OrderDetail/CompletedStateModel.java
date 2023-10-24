package com.khedmap.khedmap.Order.Model.OrderDetail;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyLog;
import com.android.volley.error.AuthFailureError;
import com.android.volley.request.StringRequest;
import com.android.volley.toolbox.Volley;
import com.khedmap.khedmap.Order.CompletedStateContract;
import com.khedmap.khedmap.Utilities.SharedPrefManager;
import com.khedmap.khedmap.Utilities.UtilitiesSingleton;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class CompletedStateModel  implements CompletedStateContract.ModelCallBack {

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
    public void sendAddFavoriteExpertRequest(Context context, final String requestBody, Response.Listener responseListener, Response.ErrorListener errorListener) {


        RequestQueue queue = Volley.newRequestQueue(context.getApplicationContext());

        String URL = UtilitiesSingleton.getInstance().getBaseURL() + "favoriteExpert/add";


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



}
