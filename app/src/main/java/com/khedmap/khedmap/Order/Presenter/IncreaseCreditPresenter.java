package com.khedmap.khedmap.Order.Presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.error.NoConnectionError;
import com.android.volley.error.TimeoutError;
import com.android.volley.error.VolleyError;
import com.khedmap.khedmap.LoginSignUp.View.EnterMobileActivity;
import com.khedmap.khedmap.Order.IncreaseCreditContract;
import com.khedmap.khedmap.Order.Model.IncreaseCreditModel;
import com.khedmap.khedmap.Order.View.ProfileActivity;
import com.khedmap.khedmap.Utilities.UtilitiesSingleton;

import org.json.JSONException;
import org.json.JSONObject;

public class IncreaseCreditPresenter implements IncreaseCreditContract.PresenterCallBack {

    private IncreaseCreditContract.ViewCallBack view;
    private IncreaseCreditModel model;

    private boolean responseReceived = true;

    public IncreaseCreditPresenter(IncreaseCreditContract.ViewCallBack view, IncreaseCreditModel model) {
        this.model = model;
        this.view = view;
    }

    private void setResponseReceived() {

        responseReceived = true;
        view.showLoadingView(false);

    }


    @Override
    public void initCurrentCredit(Activity activity) {

        if (!responseReceived)  //for Block requests till Response not received
            return;

        if (!UtilitiesSingleton.getInstance().isNetworkAvailable(activity)) {
            view.showSnackBar("اتصال اینترنت را بررسی کنید");
            return;
        }

        view.showLoadingView(true);
        responseReceived = false;

        //send profile request to get Credit from server
        sendGetCreditRequest(activity);

    }


    ///////////////////////////////////////
//to send GetCredit Request
    private void sendGetCreditRequest(final Activity activity) {

        //to buildRequestBody
        String requestBody = buildGetCreditRequestBody(activity);

        if (requestBody == null)
            return;

        Response.Listener responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                String currentCredit = getCreditFetchData(response);

                if (currentCredit != null)
                    view.setCurrentCredit(currentCredit);

                setResponseReceived();

            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                ///////Error Handling

                ErrorHandling(error, activity);

                setResponseReceived();


            }
        };


        model.sendGetCreditRequest(activity, requestBody, responseListener, errorListener);

    }


    private String buildGetCreditRequestBody(Context context) {


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


    private String getCreditFetchData(String response) {


        try {
            JSONObject jsonobject = new JSONObject(response);

            //to get status
            String status = jsonobject.getString("status");

            if (status.equals("success")) {

                JSONObject data = jsonobject.getJSONObject("data");

                return UtilitiesSingleton.getInstance().convertPrice(data.getInt("credit"));

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
    public void submitButtonClicked(Activity activity, String credit) {

        if (credit.isEmpty()) {
            view.showSnackBar("مقدار قابل شارژ نمیتواند خالی باشد");
            return;
        }

        int rial = Integer.parseInt(credit) * 10;

        if (rial < 10000) {
            view.showSnackBar("حداقل مبلغ ورودی ۱۰۰۰ تومان می باشد");
            return;
        }

        if (!UtilitiesSingleton.getInstance().isNetworkAvailable(activity)) {
            view.showSnackBar("اتصال اینترنت را بررسی کنید");
            return;
        }


        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("https://khedmap.info/user/charge/" + model.getApiTokenSharedPref(activity) + "/" + rial));
        activity.startActivity(intent);
        activity.finish();
    }


    //To handle Payment Result
    @Override
    public void handlePaymentResult(Uri uri, Activity activity) {

        String status = uri.getQueryParameter("status");
        if (status != null)
            if (status.equals("success")) {
                activity.startActivity(new Intent(activity, ProfileActivity.class));
                activity.finish();
            }
    }

}
