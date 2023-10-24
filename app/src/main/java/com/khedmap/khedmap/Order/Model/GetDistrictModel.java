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
import com.khedmap.khedmap.Order.GetDistrictContract;
import com.khedmap.khedmap.Utilities.SharedPrefManager;
import com.khedmap.khedmap.Utilities.UtilitiesSingleton;

import org.json.JSONArray;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GetDistrictModel implements GetDistrictContract.ModelCallBack {

    private SharedPrefManager sharedPrefManager;

    private RequestQueue queue;

    @Override
    public void sendGetUserFavoriteLocationsRequest(Context context, final String requestBody, Response.Listener responseListener, Response.ErrorListener errorListener) {


        queue = Volley.newRequestQueue(context.getApplicationContext());

        String URL = UtilitiesSingleton.getInstance().getBaseURL() + "fetchUserFavoriteLocation";


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
    public void sendGetZonesRequest(Context context, final String requestBody, Response.Listener responseListener, Response.ErrorListener errorListener) {

        String URL = UtilitiesSingleton.getInstance().getBaseURL() + "fetchZones";

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
    public void sendGetDistrictsRequest(Context context, final String requestBody, Response.Listener responseListener, Response.ErrorListener errorListener) {

        String URL = UtilitiesSingleton.getInstance().getBaseURL() + "fetchDistrict";

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
    public void sendSubmitOrderRequest(Context context, Response.Listener responseListener, Response.ErrorListener errorListener
            , String subCategoryId, List<List<String>> answersList, String selectedDate, String selectedHour
            , String favoriteLocationId, String districtId) {

        String SUBMIT_REGISTER_URL = UtilitiesSingleton.getInstance().getBaseURL() + "storeOrder";

        SimpleMultiPartRequest smr = new SimpleMultiPartRequest(Request.Method.POST, SUBMIT_REGISTER_URL, responseListener, errorListener) {

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Accept", "application/json");

                return params;
            }

        };


        smr.addStringParam("api_token", getApiTokenSharedPref(context));

        smr.addStringParam("subcategory_id", subCategoryId);
        smr.addStringParam("date", selectedDate);
        smr.addStringParam("time", selectedHour);
        if (favoriteLocationId == null)
            smr.addStringParam("district_id", districtId);
        else
            smr.addStringParam("favorite_location_id", favoriteLocationId);


        for (List<String> answers : answersList) {

            if (!answers.isEmpty()) {

                String questionId = answers.get(answers.size() - 1); //to get Question id

                String[] answersArray = new String[answers.size() - 1]; //to convert answers list to array
                for (int i = 0; i < answers.size() - 1; i++) {
                    answersArray[i] = answers.get(i);
                }

                if (answersArray[0].startsWith("file")) {

                    Uri imageUri = Uri.parse(answersArray[0]); //to convert string to uri
                    File mFile = new File(imageUri.getPath());
                    smr.addFile("question_" + questionId, mFile.getAbsolutePath());
                } else {

                    JSONArray answersJson = new JSONArray(Arrays.asList(answersArray)); //to convert list to jason array
                    smr.addStringParam("question_" + questionId, answersJson.toString());

                }

            }

        }

        queue.add(smr);
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
