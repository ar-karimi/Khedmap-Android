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
import com.khedmap.khedmap.Order.Model.VerifyFinishOrderModel;
import com.khedmap.khedmap.Order.VerifyFinishOrderContract;
import com.khedmap.khedmap.Order.View.CommentActivity;
import com.khedmap.khedmap.Utilities.UtilitiesSingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class VerifyFinishOrderPresenter implements VerifyFinishOrderContract.PresenterCallBack {

    private VerifyFinishOrderContract.ViewCallBack view;
    private VerifyFinishOrderModel model;

    private boolean responseReceived = true;

    private String finishedOrderId;

    private int currentCredit = 0;
    private boolean haveCredit = false;

    private boolean finishedOrder = false; //to check finish order request successful done or not

    private List<String> expertDetails;

    private boolean isFavoriteLocation; //to check must show addFavoriteLocation Dialog or not


    public VerifyFinishOrderPresenter(VerifyFinishOrderContract.ViewCallBack view, VerifyFinishOrderModel model) {
        this.model = model;
        this.view = view;
    }


    @Override
    public void setFinishedOrderDetail(String orderId, String subcategory, int finalPrice, String factorPicture) {

        this.finishedOrderId = orderId;

        view.showPageItems(subcategory, UtilitiesSingleton.getInstance().convertPrice(finalPrice), factorPicture);
    }


    private void setResponseReceived() {

        responseReceived = true;
        view.showLoadingView(false);

    }

    private void setFinishedOrderFlag() {

        finishedOrder = true;

    }


    @Override
    public void noButtonClicked(Context context) {


        if (!responseReceived)   //for Block requests till Response not received
            return;

        if (!UtilitiesSingleton.getInstance().isNetworkAvailable(context)) {
            view.showSnackBar("اتصال اینترنت را بررسی کنید");
            return;
        }

        view.showNoButtonDialog();

    }


    @Override
    public void verifyFinishOrderClicked(String operation, Activity activity) {


        if (!responseReceived)   //for Block requests till Response not received
            return;

        if (!UtilitiesSingleton.getInstance().isNetworkAvailable(activity)) {
            view.showSnackBar("اتصال اینترنت را بررسی کنید");
            return;
        }

        view.showLoadingView(true);
        responseReceived = false;

        //to send VerifyFinishOrder Request
        sendVerifyFinishOrderRequest(operation, activity);

    }


    ///////////////////////////////////////
//to send VerifyFinishOrder Request
    private void sendVerifyFinishOrderRequest(final String operation, final Activity activity) {


        //to buildRequestBody
        String requestBody = buildSendVerifyFinishOrderRequestBody(operation, activity);

        if (requestBody == null)
            return;

        Response.Listener responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {


                if (verifyFinishOrderFetchData(response)) { //to check is success or not

                    if (operation.equals("accept")) //Check is "accept" or "decline" request
                    {
                        setFinishedOrderFlag();
                        view.showPaymentTypeDialog(currentCredit);
                    } else {

                        //User rejected Verifying finishOrder (no need to send Comment)
                        activity.finish();

                    }

                }

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


        model.sendVerifyFinishOrderRequest(activity, requestBody, responseListener, errorListener);

    }


    private String buildSendVerifyFinishOrderRequestBody(String operation, final Context context) {


        try {

            //to build detail parameter
            JSONObject detail = new JSONObject();
            detail.put("order", finishedOrderId);
            detail.put("operation", operation);

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


    private boolean verifyFinishOrderFetchData(String response) {


        try {
            JSONObject jsonobject = new JSONObject(response);

            //to get status
            String status = jsonobject.getString("status");

            if (status.equals("success")) {

                JSONObject data = jsonobject.getJSONObject("data");

                currentCredit = data.getInt("currentCredit");
                haveCredit = data.getBoolean("haveCredit"); //user have enough Credit for this order or not

                return true;

            } else {
                //to get message
                String message = jsonobject.getString("message");
                view.showToast(message);
            }

        } catch (JSONException e) {
            Log.e("ParsError", e.getMessage());
            //view.showSnackBar("خطای شبکه");
            view.showToast("خطای شبکه");
        }

        return false;
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
            //view.showSnackBar("خطای شبکه");
            view.showToast("خطای شبکه");
        }
    }

//till here
///////////////////////////////////////


    @Override
    public void paymentTypeButtonClicked(String paymentType, Activity activity) {


        if (!responseReceived)   //for Block requests till Response not received
            return;

        if (!UtilitiesSingleton.getInstance().isNetworkAvailable(activity)) {
            view.showToast("اتصال اینترنت را بررسی کنید");
            return;
        }


        if (paymentType.equals("online") && !haveCredit) //haven't enough credit for this order
        {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("https://khedmap.info/payOrder/" + finishedOrderId));
            activity.startActivity(intent);

            return;
        }


        view.dismissPaymentTypeDialog(); //because loading view shows back of dialog
        view.showLoadingView(true);
        responseReceived = false;

        //to send paymentType Request
        sendPaymentTypeRequest(paymentType, activity);

    }


    ///////////////////////////////////////
//to send paymentType Request
    private void sendPaymentTypeRequest(final String paymentType, final Activity activity) {


        //to buildRequestBody
        String requestBody = buildSendPaymentTypeRequestBody(paymentType, activity);

        if (requestBody == null)
            return;

        Response.Listener responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {


                if (paymentTypeFetchData(response, activity) != null) { //Check requestResponse Success or not

                    if (!isFavoriteLocation)
                        view.showFavoriteLocationDialog();
                    else {

                        activity.startActivity(new Intent(activity, CommentActivity.class)
                                .putExtra("orderId", finishedOrderId)
                                .putExtra("expertName", expertDetails.get(0))
                                .putExtra("expertPic", expertDetails.get(1))
                                .putExtra("expertId", expertDetails.get(2))
                                .putExtra("isFavorite", expertDetails.get(3))
                                .putExtra("rate", "null")
                                .putExtra("comment", "null"));
                        activity.finish();
                    }

                } else {
                    view.showPaymentTypeDialog(currentCredit); //Show Again PaymentType Dialog
                }

                setResponseReceived();
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                ///////Error Handling

                ErrorHandling(error, activity);

                setResponseReceived();

                view.showPaymentTypeDialog(currentCredit); //Show Again PaymentType Dialog

            }
        };


        model.sendPaymentTypeRequest(activity, requestBody, responseListener, errorListener);

    }


    private String buildSendPaymentTypeRequestBody(String paymentType, Context context) {


        try {

            //to build detail parameter
            JSONObject detail = new JSONObject();
            detail.put("order", finishedOrderId);
            detail.put("paymentType", paymentType);

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


    private List<String> paymentTypeFetchData(String response, Activity activity) {

        try {
            JSONObject jsonobject = new JSONObject(response);

            //to get status
            String status = jsonobject.getString("status");

            if (status.equals("success")) {

                JSONObject data = jsonobject.getJSONObject("data");

                isFavoriteLocation = data.getBoolean("favorite_location");

                //to get expert details to use in next page
                List<String> expertDetails = new ArrayList<>();

                JSONObject expert = data.getJSONObject("expert");

                expertDetails.add(expert.getString("name"));
                expertDetails.add(expert.getString("avatar"));
                expertDetails.add(expert.getString("identification"));
                expertDetails.add(expert.getString("isFavorite"));


                this.expertDetails = expertDetails;
                return expertDetails;

            } else { //a problem occurred

                int code = jsonobject.getInt("code"); //to get errorCode
                if (code == 20) {
                    //user don't have enough Credit for this order
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse("https://khedmap.info/payOrder/" + finishedOrderId));
                    activity.startActivity(intent);

                } else
                    activity.finish(); //something went wrong by user
            }

        } catch (JSONException e) {
            Log.e("ParsError", e.getMessage());
            //view.showSnackBar("خطای شبکه");
            view.showToast("خطای شبکه");
        }

        return null;
    }


//till here
///////////////////////////////////////


    @Override
    public void checkPayedOrder(Activity activity) {

        view.showLoadingView(true);
        responseReceived = false;

        //to send checkPayedOrder Request
        sendCheckPayedOrderRequest(activity);

    }


    ///////////////////////////////////////
//to send checkPayedOrder Request
    private void sendCheckPayedOrderRequest(final Activity activity) {


        //to buildRequestBody
        String requestBody = buildCheckPayedOrderRequestBody(activity);

        if (requestBody == null)
            return;

        Response.Listener responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {


                if (checkPayedOrderFetchData(response, activity) != null) { //Check requestResponse Success or not

                    if (!isFavoriteLocation)
                        view.showFavoriteLocationDialog();
                    else {

                        activity.startActivity(new Intent(activity, CommentActivity.class)
                                .putExtra("orderId", finishedOrderId)
                                .putExtra("expertName", expertDetails.get(0))
                                .putExtra("expertPic", expertDetails.get(1))
                                .putExtra("expertId", expertDetails.get(2))
                                .putExtra("isFavorite", expertDetails.get(3))
                                .putExtra("rate", "null")
                                .putExtra("comment", "null"));
                        activity.finish();
                    }

                } else {

                    if (finishedOrder) //check this because prevent showing PaymentTypeDialog before verifyFinishOrder
                        view.showPaymentTypeDialog(currentCredit);
                }

                setResponseReceived();
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                ///////Error Handling

                ErrorHandling(error, activity);

                setResponseReceived();


                if (finishedOrder) //check this because prevent showing PaymentTypeDialog before verifyFinishOrder
                    view.showPaymentTypeDialog(currentCredit); //Show Again PaymentType Dialog

            }
        };


        model.sendCheckPayedOrderRequest(activity, requestBody, responseListener, errorListener);

    }


    private String buildCheckPayedOrderRequestBody(Context context) {


        try {

            //to build detail parameter
            JSONObject detail = new JSONObject();
            detail.put("order", finishedOrderId);

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


    private List<String> checkPayedOrderFetchData(String response, Activity activity) {


        try {
            JSONObject jsonobject = new JSONObject(response);

            //to get status
            String status = jsonobject.getString("status");

            if (status.equals("success")) {

                JSONObject data = jsonobject.getJSONObject("data");


                if (data.getBoolean("payedOrder")) { //to check user payed this order or not

                    isFavoriteLocation = data.getBoolean("favorite_location");

                    JSONObject expert = data.getJSONObject("expert");

                    if (!expert.getBoolean("haveRate")) {  //to check user rated to expert or not

                        //to get expert details to use in next page
                        List<String> expertDetails = new ArrayList<>();

                        expertDetails.add(expert.getString("name"));
                        expertDetails.add(expert.getString("avatar"));
                        expertDetails.add(expert.getString("identification"));
                        expertDetails.add(expert.getString("isFavorite"));


                        this.expertDetails = expertDetails;
                        return expertDetails;

                    } else {

                        activity.finish();
                    }

                } else {

                    // to check user have enough Credit for this order or not (new haveCredit)
                    haveCredit = data.getBoolean("haveCredit");

                    //to Repeat procedure again because order not payed (dialog shows again)
                }


            } else {
                //to get message
                String message = jsonobject.getString("message");
                //view.showSnackBar(message);
                view.showToast(message);
            }

        } catch (JSONException e) {
            Log.e("ParsError", e.getMessage());
            //view.showSnackBar("خطای شبکه");
            view.showToast("خطای شبکه");
        }

        return null;
    }


//till here
///////////////////////////////////////


    @Override
    public void addFavoriteLocationClicked(Activity activity, String favoriteLocationName) {

        if (favoriteLocationName == null) { //to find yes button or no clicked

            activity.startActivity(new Intent(activity, CommentActivity.class)
                    .putExtra("orderId", finishedOrderId)
                    .putExtra("expertName", expertDetails.get(0))
                    .putExtra("expertPic", expertDetails.get(1))
                    .putExtra("expertId", expertDetails.get(2))
                    .putExtra("isFavorite", expertDetails.get(3))
                    .putExtra("rate", "null")
                    .putExtra("comment", "null"));
            activity.finish();
            return;
        }

        if (favoriteLocationName.isEmpty()) {
            view.showToast("لطفاً نام را وارد کنید");
            return;
        }

        if (!responseReceived)   //for Block requests till Response not received
            return;

        if (!UtilitiesSingleton.getInstance().isNetworkAvailable(activity)) {
            view.showSnackBar("اتصال اینترنت را بررسی کنید");
            return;
        }

        view.dismissFavoriteLocationDialog(); //because loading view shows back of dialog
        view.showLoadingView(true);
        responseReceived = false;

        //to send addFavoriteLocation Request
        sendAddFavoriteLocationRequest(activity, favoriteLocationName);

    }


    ///////////////////////////////////////
//to send addFavoriteLocation Request
    private void sendAddFavoriteLocationRequest(final Activity activity, String favoriteLocationName) {


        //to buildRequestBody
        String requestBody = buildAddFavoriteLocationRequestBody(favoriteLocationName, activity);

        if (requestBody == null)
            return;

        Response.Listener responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {


                if (addFavoriteLocationFetchData(response)) { //Check requestResponse Success or not

                    activity.startActivity(new Intent(activity, CommentActivity.class)
                            .putExtra("orderId", finishedOrderId)
                            .putExtra("expertName", expertDetails.get(0))
                            .putExtra("expertPic", expertDetails.get(1))
                            .putExtra("expertId", expertDetails.get(2))
                            .putExtra("isFavorite", expertDetails.get(3))
                            .putExtra("rate", "null")
                            .putExtra("comment", "null"));
                    activity.finish();

                } else {
                    view.showFavoriteLocationDialog(); //Show Again FavoriteLocation Dialog
                }

                setResponseReceived();
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                ///////Error Handling

                ErrorHandling(error, activity);

                setResponseReceived();

                view.showFavoriteLocationDialog(); //Show Again FavoriteLocation Dialog

            }
        };


        model.sendAddFavoriteLocationRequest(activity, requestBody, responseListener, errorListener);

    }


    private String buildAddFavoriteLocationRequestBody(String favoriteLocationName, Context context) {


        try {

            //to build detail parameter
            JSONObject detail = new JSONObject();
            detail.put("order", finishedOrderId);
            detail.put("name", favoriteLocationName);

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


    private boolean addFavoriteLocationFetchData(String response) {

        try {
            JSONObject jsonobject = new JSONObject(response);

            //to get status
            String status = jsonobject.getString("status");

            if (status.equals("success")) {

                return true;

            } else {
                //to get message
                String message = jsonobject.getString("message");
                //view.showSnackBar(message);
                view.showToast(message);
            }


        } catch (JSONException e) {
            Log.e("ParsError", e.getMessage());
            //view.showSnackBar("خطای شبکه");
            view.showToast("خطای شبکه");
        }

        return false;
    }


//till here
///////////////////////////////////////


}
