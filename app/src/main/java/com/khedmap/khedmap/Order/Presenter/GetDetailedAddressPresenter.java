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
import com.khedmap.khedmap.Order.GetDetailedAddressContract;
import com.khedmap.khedmap.Order.Model.GetDetailedAddressModel;
import com.khedmap.khedmap.Order.View.OrdersManagementActivity;
import com.khedmap.khedmap.Utilities.UtilitiesSingleton;

import org.json.JSONException;
import org.json.JSONObject;

public class GetDetailedAddressPresenter implements GetDetailedAddressContract.PresenterCallBack {

    private GetDetailedAddressContract.ViewCallBack view;
    private GetDetailedAddressModel model;

    private String submittedOrderId; //order id
    private String suggestionId; //suggestion id
    private String latitude; //selected location latitude
    private String longitude; //selected location longitude

    private boolean responseReceived = true;


    public GetDetailedAddressPresenter(GetDetailedAddressContract.ViewCallBack view, GetDetailedAddressModel model) {
        this.model = model;
        this.view = view;
    }

    @Override
    public void setSelectedItemIdAndLocation(String suggestionId, String submittedOrderId, String latitude, String longitude) {

        this.suggestionId = suggestionId;
        this.submittedOrderId = submittedOrderId;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    private void setResponseReceived() {

        responseReceived = true;
        view.showProgressBar(false);

    }


    @Override
    public void submitButtonClicked(Activity activity, String address) {

        if (!responseReceived)   //for Block requests till Response not received
            return;

        //address Checking
        if (address.length() < 10) {
            view.showSnackBar("آدرس باید حداقل 10 حرف باشد");
            return;
        }


        if (!UtilitiesSingleton.getInstance().isNetworkAvailable(activity)) {
            view.showSnackBar("اتصال اینترنت را بررسی کنید");
            return;
        }

        view.showProgressBar(true);
        responseReceived = false;

        //send SubmitAddress Request
        sendSubmitAddressRequest(activity, address);
    }


///////////////////////////////////////
//to send SubmitAddress Request

    private void sendSubmitAddressRequest(final Activity activity, String address) {


        //to buildRequestBody
        String requestBody = buildSubmitAddressRequestBody(activity, address);

        if (requestBody == null)
            return;

        Response.Listener responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {


                if (SubmitAddressFetchData(response))
                    //setResponseReceived();       //Set After Receive AcceptSuggestionRequest
                    sendAcceptSuggestionRequest(activity);
                else
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


        model.sendSubmitAddressRequest(activity, requestBody, responseListener, errorListener);

    }

    private String buildSubmitAddressRequestBody(Activity activity, String address) {


        try {

            //to build detail parameter
            JSONObject detail = new JSONObject();
            detail.put("order", submittedOrderId);
            detail.put("address", address);
            detail.put("latitude", latitude);
            detail.put("longitude", longitude);

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


    private boolean SubmitAddressFetchData(String response) {


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


///////////////////////////////////////
//to send acceptSuggestion Request (automatic after sendSubmitAddressRequest)

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
