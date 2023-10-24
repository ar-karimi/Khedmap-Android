package com.khedmap.khedmap.Order.Presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.error.NoConnectionError;
import com.android.volley.error.TimeoutError;
import com.android.volley.error.VolleyError;
import com.khedmap.khedmap.LoginSignUp.View.EnterMobileActivity;
import com.khedmap.khedmap.Order.CommentContract;
import com.khedmap.khedmap.Order.Model.CommentModel;
import com.khedmap.khedmap.Utilities.UtilitiesSingleton;

import org.json.JSONException;
import org.json.JSONObject;

public class CommentPresenter implements CommentContract.PresenterCallBack {

    private CommentContract.ViewCallBack view;
    private CommentModel model;

    private boolean responseReceived = true;

    private String finishedOrderId;
    private String expertId;
    private boolean isFavorite;

    private String rate;
    private String comment;

    private boolean isEdit; //to find that is submitComment Request or editComment

    public CommentPresenter(CommentContract.ViewCallBack view, CommentModel model) {
        this.model = model;
        this.view = view;
    }


    @Override
    public void setFinishedOrderDetail(String orderId, String expertName, String expertPic, String expertId, String isFavorite,
                                       String rate, String comment) {

        this.finishedOrderId = orderId;
        this.expertId = expertId;
        this.isFavorite = isFavorite.equals("true");

        view.showExpertDetails(expertName, expertPic);

        if (!rate.equals("null")) { //to find that is submitComment or editComment
            isEdit = true;
            view.setPrevComment(rate, comment);
        } else
            isEdit = false;
    }


    private void setResponseReceived() {

        responseReceived = true;
        view.showLoadingView(false);

    }


    @Override
    public void submitButtonClicked(Activity activity, int rate, String comment) {


        if (!responseReceived)   //for Block requests till Response not received
            return;

        if (!UtilitiesSingleton.getInstance().isNetworkAvailable(activity)) {
            view.showSnackBar("اتصال اینترنت را بررسی کنید");
            return;
        }


        if (rate == 0) {
            view.showSnackBar("لطفاً امتیاز را وارد کنید");
            return;
        }

        if (comment.length() != 0 && comment.length() < 3) { //Comment is Optional but if exist must be At least 3 char
            view.showSnackBar("طول متن ارسالی کوتاه است");
            return;
        }

        view.showLoadingView(true);
        responseReceived = false;

        //to send Comment Request
        sendCommentRequest(activity, rate, comment);

    }


    ///////////////////////////////////////
//to send Comment Request
    private void sendCommentRequest(final Activity activity, final int rate, final String comment) {

        this.rate = String.valueOf(rate);
        this.comment = comment;

        //to buildRequestBody
        String requestBody = buildCommentRequestBody(activity, rate, comment);

        if (requestBody == null)
            return;

        Response.Listener responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                if (commentFetchData(response)) { //Check requestResponse Success or not

                    if (rate >= 3 && !isFavorite)
                        view.showFavoriteExpertDialog();
                    else {

                        activity.setResult(Activity.RESULT_OK, new Intent()
                                .putExtra("rate", String.valueOf(rate))
                                .putExtra("comment", comment));
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

        //because submitComment and editComment Request and response is the same but url is different
        String requestUrl;
        if (isEdit)
            requestUrl = "updateComment";
        else
            requestUrl = "order/comment/store";

        model.sendCommentRequest(activity, requestBody, responseListener, errorListener, requestUrl);

    }


    private String buildCommentRequestBody(final Context context, int rate, String comment) {


        try {

            //to build detail parameter
            JSONObject detail = new JSONObject();
            detail.put("order", finishedOrderId);
            detail.put("rate", rate);

            if (!comment.isEmpty()) //Comment is Optional
                detail.put("comment", comment);

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


    private boolean commentFetchData(String response) {


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
    public void addFavoriteExpertClicked(Activity activity, boolean isOk) {

        if (!isOk) { //to find yes button or no clicked

            activity.setResult(Activity.RESULT_OK, new Intent()
                    .putExtra("rate", rate)
                    .putExtra("comment", comment));
            activity.finish();
            return;
        }


        if (!responseReceived)   //for Block requests till Response not received
            return;

        if (!UtilitiesSingleton.getInstance().isNetworkAvailable(activity)) {
            view.showSnackBar("اتصال اینترنت را بررسی کنید");
            return;
        }

        view.showLoadingView(true);
        responseReceived = false;

        //to send AddFavoriteExpert Request
        sendAddFavoriteExpertRequest(activity);

    }


    ///////////////////////////////////////
//to send AddFavoriteExpert Request
    private void sendAddFavoriteExpertRequest(final Activity activity) {


        //to buildRequestBody
        String requestBody = buildAddFavoriteExpertRequestBody(activity);

        if (requestBody == null)
            return;

        Response.Listener responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                if (addFavoriteExpertFetchData(response)) { //Check requestResponse Success or not

                    activity.setResult(Activity.RESULT_OK, new Intent()
                            .putExtra("rate", rate)
                            .putExtra("comment", comment));
                    activity.finish();

                } else {
                    view.showFavoriteExpertDialog(); //Show Again FavoriteExpert Dialog
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


        model.sendAddFavoriteExpertRequest(activity, requestBody, responseListener, errorListener);

    }


    private String buildAddFavoriteExpertRequestBody(Context context) {


        try {

            //to build detail parameter
            JSONObject detail = new JSONObject();
            detail.put("expert", expertId);

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


    private boolean addFavoriteExpertFetchData(String response) {


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
