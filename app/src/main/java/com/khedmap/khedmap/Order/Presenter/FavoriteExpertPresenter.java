package com.khedmap.khedmap.Order.Presenter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.error.NoConnectionError;
import com.android.volley.error.TimeoutError;
import com.android.volley.error.VolleyError;
import com.khedmap.khedmap.LoginSignUp.View.EnterMobileActivity;
import com.khedmap.khedmap.Order.DataModels.FavoriteExpert;
import com.khedmap.khedmap.Order.FavoriteExpertContract;
import com.khedmap.khedmap.Order.Model.FavoriteExpertModel;
import com.khedmap.khedmap.Utilities.UtilitiesSingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class FavoriteExpertPresenter implements FavoriteExpertContract.PresenterCallBack {

    private FavoriteExpertContract.ViewCallBack view;
    private FavoriteExpertModel model;

    private boolean responseReceived = true;


    public FavoriteExpertPresenter(FavoriteExpertContract.ViewCallBack view, FavoriteExpertModel model) {
        this.model = model;
        this.view = view;
    }


    private void setResponseReceived() {

        responseReceived = true;
        view.showLoadingView(false);
    }

    @Override
    public void initFavoriteExpertsRecyclerView(Context context) {

        if (!responseReceived)  //for Block requests till Response not received
            return;

        if (!UtilitiesSingleton.getInstance().isNetworkAvailable(context)) {
            view.showSnackBar("اتصال اینترنت را بررسی کنید");
            return;
        }

        view.showLoadingView(true);
        responseReceived = false;

        //to get FavoriteExperts
        sendGetFavoriteExpertsRequest(context, false);
    }


    ///////////////////////////////////////
//to get FavoriteExperts
    private void sendGetFavoriteExpertsRequest(final Context context, final boolean comeFromRemove) {


        //to buildRequestBody
        String requestBody = buildGetFavoriteExpertsRequestBody(context);

        if (requestBody == null)
            return;

        Response.Listener responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {


                List<FavoriteExpert> favoriteExperts = GetFavoriteExpertsFetchData(response);

                if (favoriteExperts != null) {
                    if (comeFromRemove)
                        view.showSnackBar("با موفقیت انجام شد");

                    view.showFavoriteExpertsRecyclerView(favoriteExperts);
                }


                setResponseReceived();
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                ///////Error Handling

                ErrorHandling(error, context);

                setResponseReceived();


            }
        };


        model.sendGetFavoriteExpertsRequest(context, requestBody, responseListener, errorListener);

    }


    private String buildGetFavoriteExpertsRequestBody(Context context) {


        try {

            //to build jsonBody
            JSONObject jsonBody = new JSONObject();

            //get apiToken from Shared Preferences
            jsonBody.put("api_token", model.getApiTokenSharedPref(context));

            return jsonBody.toString();


        } catch (JSONException e) {
            e.printStackTrace();

            return null;
        }

    }


    private List<FavoriteExpert> GetFavoriteExpertsFetchData(String response) {


        try {
            JSONObject jsonobject = new JSONObject(response);

            //to get status
            String status = jsonobject.getString("status");

            if (status.equals("success")) {

                List<FavoriteExpert> favoriteExperts = new ArrayList<>();

                JSONArray data = jsonobject.getJSONArray("data");

                for (int i = 0; i < data.length(); i++) {
                    JSONObject favoriteExpertItem = data.getJSONObject(i);

                    FavoriteExpert favoriteExpert = new FavoriteExpert();
                    favoriteExpert.setIdentification(favoriteExpertItem.getString("identification"));
                    favoriteExpert.setName(favoriteExpertItem.getString("name"));
                    favoriteExpert.setAvatar(favoriteExpertItem.getString("avatar"));


                    favoriteExperts.add(favoriteExpert);
                }

                return favoriteExperts;

            } else {
                //to get message
                String message = jsonobject.getString("message");
                view.showSnackBar(message);
            }

        } catch (JSONException e) {
            Log.e("ParsError", e.getMessage());
            view.showSnackBar("خطای شبکه");
        }

        return null;
    }


    private void ErrorHandling(VolleyError error, Context context) {


        NetworkResponse networkResponse = error.networkResponse;
        String errorMessage = "Unknown error";
        if (networkResponse == null) {
            if (error.getClass().equals(TimeoutError.class)) {
                errorMessage = "Request timeout";
            } else if (error.getClass().equals(NoConnectionError.class)) {
                errorMessage = "Failed to connect server";
            }
        } else {

            if (networkResponse.statusCode == 404) {
                errorMessage = "Resource not found";
            } else if (networkResponse.statusCode == 401) {
                errorMessage = "Please login again";
            } else if (networkResponse.statusCode == 400) {
                errorMessage = "Check your inputs";
            } else if (networkResponse.statusCode == 500) {
                errorMessage = "Something is getting wrong";
            }
        }

        Log.e("ResponseError", errorMessage);
        error.printStackTrace();


        if (errorMessage.equals("Please login again")) {
            //clear apiToken from sharedPref
            model.setApiTokenSharedPref("", context);


            Intent intent = new Intent(context, EnterMobileActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);

        } else {
            view.showSnackBar("خطای شبکه");
        }
    }

//till here
///////////////////////////////////////

    @Override
    public void removeButtonClicked(String selectedItemId, Context context) {


        if (!responseReceived)   //for Block requests till Response not received
            return;

        if (!UtilitiesSingleton.getInstance().isNetworkAvailable(context)) {
            view.showSnackBar("اتصال اینترنت را بررسی کنید");
            return;
        }


        view.showLoadingView(true);
        responseReceived = false;

        //to send RemoveExpertRequest
        sendRemoveExpertRequest(selectedItemId, context);

    }


    ///////////////////////////////////////
//to send RemoveExpertRequest
    private void sendRemoveExpertRequest(String selectedItemId, final Context context) {


        //to buildRequestBody
        String requestBody = buildRemoveExpertRequestBody(selectedItemId, context);

        if (requestBody == null)
            return;

        Response.Listener responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {


                if (removeExpertFetchData(response))
                    sendGetFavoriteExpertsRequest(context, true); //set response received in this request
                else
                    setResponseReceived();
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                ///////Error Handling

                ErrorHandling(error, context);

                setResponseReceived();


            }
        };


        model.sendRemoveExpertRequest(context, requestBody, responseListener, errorListener);

    }


    private String buildRemoveExpertRequestBody(String selectedItemId, Context context) {


        try {

            //to build detail parameter
            JSONObject detail = new JSONObject();
            detail.put("expert", selectedItemId);

            //to build jsonBody
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("Detail", detail);
            //get apiToken from Shared Preferences
            jsonBody.put("api_token", model.getApiTokenSharedPref(context));

            return jsonBody.toString();


        } catch (JSONException e) {
            e.printStackTrace();

            return null;
        }

    }


    private boolean removeExpertFetchData(String response) {


        try {
            JSONObject jsonobject = new JSONObject(response);

            //to get status
            String status = jsonobject.getString("status");

            if (status.equals("success")) {

                return true;

            } else {
                //to get message
                String message = jsonobject.getString("message");
                view.showSnackBar(message);
            }

        } catch (JSONException e) {
            Log.e("ParsError", e.getMessage());
            view.showSnackBar("خطای شبکه");
        }

        return false;
    }
//till here
///////////////////////////////////////


}
