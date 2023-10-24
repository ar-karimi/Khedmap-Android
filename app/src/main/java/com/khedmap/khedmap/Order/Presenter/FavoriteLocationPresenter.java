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
import com.khedmap.khedmap.Order.DataModels.FavoriteLocation;
import com.khedmap.khedmap.Order.FavoriteLocationContract;
import com.khedmap.khedmap.Order.Model.FavoriteLocationModel;
import com.khedmap.khedmap.Order.View.EditFavoriteLocationActivity;
import com.khedmap.khedmap.Utilities.UtilitiesSingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class FavoriteLocationPresenter implements FavoriteLocationContract.PresenterCallBack {

    private FavoriteLocationContract.ViewCallBack view;
    private FavoriteLocationModel model;

    private boolean responseReceived = true;


    public FavoriteLocationPresenter(FavoriteLocationContract.ViewCallBack view, FavoriteLocationModel model) {
        this.model = model;
        this.view = view;
    }


    private void setResponseReceived() {

        responseReceived = true;
        view.showLoadingView(false);
    }

    @Override
    public void initFavoriteLocationsRecyclerView(Context context) {

        if (!responseReceived)  //for Block requests till Response not received
            return;

        if (!UtilitiesSingleton.getInstance().isNetworkAvailable(context)) {
            view.showSnackBar("اتصال اینترنت را بررسی کنید");
            return;
        }

        view.showLoadingView(true);
        responseReceived = false;

        //to get FavoriteLocations
        sendGetFavoriteLocationsRequest(context, false);
    }


    ///////////////////////////////////////
//to get FavoriteLocations
    private void sendGetFavoriteLocationsRequest(final Context context, final boolean comeFromRemove) {


        //to buildRequestBody
        String requestBody = buildGetFavoriteLocationsRequestBody(context);

        if (requestBody == null)
            return;

        Response.Listener responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {


                List<FavoriteLocation> favoriteLocations = GetFavoriteLocationsFetchData(response);

                if (favoriteLocations != null) {
                    if (comeFromRemove)
                        view.showSnackBar("با موفقیت انجام شد");

                    view.showFavoriteLocationsRecyclerView(favoriteLocations);
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


        model.sendGetFavoriteLocationsRequest(context, requestBody, responseListener, errorListener);

    }


    private String buildGetFavoriteLocationsRequestBody(Context context) {


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


    private List<FavoriteLocation> GetFavoriteLocationsFetchData(String response) {


        try {
            JSONObject jsonobject = new JSONObject(response);

            //to get status
            String status = jsonobject.getString("status");

            if (status.equals("success")) {

                JSONArray data = jsonobject.getJSONArray("data");


                List<FavoriteLocation> favoriteLocationsList = new ArrayList<>();

                for (int i = 0; i < data.length(); i++) {

                    JSONObject favoriteLocationJSONObject = data.getJSONObject(i);

                    FavoriteLocation favoriteLocation = new FavoriteLocation();
                    favoriteLocation.setName(favoriteLocationJSONObject.getString("name"));
                    favoriteLocation.setDistrict(favoriteLocationJSONObject.getString("district"));
                    favoriteLocation.setIdentification(favoriteLocationJSONObject.getString("identification"));

                    favoriteLocationsList.add(favoriteLocation);

                }


                return favoriteLocationsList;

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

        //to send RemoveLocationRequest
        sendRemoveLocationRequest(selectedItemId, context);

    }


    ///////////////////////////////////////
//to send RemoveLocationRequest
    private void sendRemoveLocationRequest(String selectedItemId, final Context context) {


        //to buildRequestBody
        String requestBody = buildRemoveLocationRequestBody(selectedItemId, context);

        if (requestBody == null)
            return;

        Response.Listener responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {


                if (removeLocationFetchData(response))
                    sendGetFavoriteLocationsRequest(context, true); //set response received in this request
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


        model.sendRemoveLocationRequest(context, requestBody, responseListener, errorListener);

    }


    private String buildRemoveLocationRequestBody(String selectedItemId, Context context) {


        try {

            //to build detail parameter
            JSONObject detail = new JSONObject();
            detail.put("favorite_location", selectedItemId);

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


    private boolean removeLocationFetchData(String response) {


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


    @Override
    public void editButtonClicked(String selectedItemId, Context context) {

        if (!responseReceived)  //for Block requests till Response not received
            return;

        if (!UtilitiesSingleton.getInstance().isNetworkAvailable(context)) {
            view.showSnackBar("اتصال اینترنت را بررسی کنید");
            return;
        }

        context.startActivity(new Intent(context, EditFavoriteLocationActivity.class)
                .putExtra(EditFavoriteLocationActivity.KEY_FAVORITE_LOCATION_ID, selectedItemId));

    }

}
