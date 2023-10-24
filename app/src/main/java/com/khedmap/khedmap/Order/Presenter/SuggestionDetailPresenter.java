package com.khedmap.khedmap.Order.Presenter;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.error.NoConnectionError;
import com.android.volley.error.TimeoutError;
import com.android.volley.error.VolleyError;
import com.khedmap.khedmap.LoginSignUp.View.EnterMobileActivity;
import com.khedmap.khedmap.Order.Model.SuggestionDetailModel;
import com.khedmap.khedmap.Order.SuggestionDetailContract;
import com.khedmap.khedmap.Order.View.MapsActivity;
import com.khedmap.khedmap.Order.View.OrdersManagementActivity;
import com.khedmap.khedmap.Utilities.UtilitiesSingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SuggestionDetailPresenter implements SuggestionDetailContract.PresenterCallBack {

    private SuggestionDetailContract.ViewCallBack view;
    private SuggestionDetailModel model;

    private String suggestionId; //suggestion id
    private String submittedOrderId; //order id

    private String prevLatitude; //received Lat from request for use in map
    private String prevLongitude; //received Lng from request for use in map

    private boolean addressSelected; //if address got from favorite location no need to get detailed address again

    private boolean responseReceived = true;


    public SuggestionDetailPresenter(SuggestionDetailContract.ViewCallBack view, SuggestionDetailModel model) {
        this.model = model;
        this.view = view;
    }

    @Override
    public void setSelectedItemId(String selectedItemId, String submittedOrderId) {

        this.suggestionId = selectedItemId;
        this.submittedOrderId = submittedOrderId;
    }

    private void setResponseReceived() {

        responseReceived = true;
        view.showProgressBar(false);

    }

    @Override
    public void initPageItems(Activity activity) {

        view.showProgressBar(true);
        responseReceived = false;

        //to get SuggestionDetail items
        sendGetSuggestionDetailItemsRequest(activity);

    }


///////////////////////////////////////
//to get SuggestionDetail items from server

    private void sendGetSuggestionDetailItemsRequest(final Activity activity) {


        //to buildRequestBody
        String requestBody = buildGetSuggestionDetailItemsRequestBody(activity);

        if (requestBody == null)
            return;

        Response.Listener responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {


                GetSuggestionDetailItemsFetchData(response);

                setResponseReceived();
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                ///////Error Handling

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

                //till here

                ErrorHandling(errorMessage, activity);

                setResponseReceived();


            }
        };


        model.sendGetSuggestionDetailItemsRequest(activity, requestBody, responseListener, errorListener);

    }

    private String buildGetSuggestionDetailItemsRequestBody(Activity activity) {


        try {

            //to build detail parameter
            JSONObject detail = new JSONObject();
            detail.put("orderRequest", suggestionId);

            //to build jsonBody
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("Detail", detail);
            //get apiToken from Shared Preferences
            jsonBody.put("api_token", model.getApiTokenSharedPref(activity));

            return jsonBody.toString();


        } catch (JSONException e) {
            e.printStackTrace();

            return null;
        }

    }


    private void GetSuggestionDetailItemsFetchData(String response) {


        try {
            JSONObject jsonobject = new JSONObject(response);

            //to get status
            String status = jsonobject.getString("status");

            if (status.equals("success")) {

                List<String> suggestionDetail = new ArrayList<>();

                JSONObject data = jsonobject.getJSONObject("data");

                suggestionDetail.add(data.getString("avatar"));
                suggestionDetail.add(data.getString("expert_name"));
                suggestionDetail.add(data.getString("expert_rate"));
                suggestionDetail.add(UtilitiesSingleton.getInstance().convertPrice(data.getInt("base_price")));

                suggestionDetail.add(data.getString("description"));

                suggestionDetail.add(data.getString("teammate"));

                //show StaticPart items
                view.setStaticPartValues(suggestionDetail);


                //Dynamic items (additionalServices)
                JSONArray additionalServices = data.getJSONArray("service");

                if (additionalServices.length() == 0)
                    view.setNoAdditionalService();
                else
                    for (int i = 0; i < additionalServices.length(); i++) {
                        JSONObject additionalServiceItem = additionalServices.getJSONObject(i);

                        view.generateAdditionalServiceItem(100 + i, additionalServiceItem.getString("service_name"),
                                UtilitiesSingleton.getInstance().convertPrice(additionalServiceItem.getInt("price")));

                    }


                prevLatitude = data.getString("latitude");
                prevLongitude = data.getString("longitude");

                addressSelected = data.getBoolean("selectAddress");

            } else {
                //to get message
                String message = jsonobject.getString("message");
                view.showSnackBar(message);
            }

        } catch (JSONException e) {
            Log.e("ParsError", e.getMessage());
            view.showSnackBar("خطای شبکه");
        }
    }


    private void ErrorHandling(String errorMessage, Activity activity) {

        if (errorMessage.equals("Please login again")) {
            //clear apiToken from sharedPref
            model.setApiTokenSharedPref("", activity);


            Intent intent = new Intent(activity, EnterMobileActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            activity.startActivity(intent);

        } else {
            view.showSnackBar("خطای شبکه");
        }
    }
    //till here
    ///////////////////////////////////////


    @Override
    public void submitButtonClicked(Activity activity) {

        if (!responseReceived)   //for Block requests till Response not received
            return;

        if (!UtilitiesSingleton.getInstance().isNetworkAvailable(activity)) {
            view.showSnackBar("اتصال اینترنت را بررسی کنید");
            return;
        }

        if (addressSelected) //if address got from favorite location no need to get detailed address again
        {
            view.showProgressBar(true);
            responseReceived = false;

            sendAcceptSuggestionRequest(activity); //to send acceptSuggestion Request
        }

        else
            activity.startActivity(new Intent(activity, MapsActivity.class)
                    .putExtra("suggestionId", suggestionId).putExtra("submittedOrderId", submittedOrderId)
                    .putExtra("prevLatitude", prevLatitude).putExtra("prevLongitude", prevLongitude));
    }


///////////////////////////////////////
//to send acceptSuggestion Request

    private void sendAcceptSuggestionRequest(final Activity activity) {


        //to buildRequestBody
        String requestBody = buildAcceptSuggestionRequestBody(activity);

        if (requestBody == null)
            return;

        Response.Listener responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {


                if (acceptSuggestionFetchData(response)) {
                    //setResponseReceived();   //No Need to setResponseReceived if success

                    Toast.makeText(activity, "سفارش شما با موفقیت ثبت نهایی شد", Toast.LENGTH_LONG).show();

                    Intent intent = new Intent(activity, OrdersManagementActivity.class);
                    intent.putExtra("isFromSubmitOrder", "true");
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    activity.startActivity(intent);

                } else
                    setResponseReceived();   //No Need to setResponseReceived if success
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                ///////Error Handling

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

                //till here

                ErrorHandling(errorMessage, activity);

                setResponseReceived();


            }
        };


        model.sendAcceptSuggestionRequest(activity, requestBody, responseListener, errorListener);

    }

    private String buildAcceptSuggestionRequestBody(Activity activity) {


        try {

            //to build detail parameter
            JSONObject detail = new JSONObject();
            detail.put("orderRequest", suggestionId);

            //to build jsonBody
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("Detail", detail);
            //get apiToken from Shared Preferences
            jsonBody.put("api_token", model.getApiTokenSharedPref(activity));

            return jsonBody.toString();


        } catch (JSONException e) {
            e.printStackTrace();

            return null;
        }

    }


    private boolean acceptSuggestionFetchData(String response) {


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
