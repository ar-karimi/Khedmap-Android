package com.khedmap.khedmap.Order.Presenter.OrderDetail;

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
import com.khedmap.khedmap.Order.DataModels.Disproof;
import com.khedmap.khedmap.Order.DataModels.Teammate;
import com.khedmap.khedmap.Order.Model.OrderDetail.WaitingForStartStateModel;
import com.khedmap.khedmap.Order.WaitingForStartStateContract;
import com.khedmap.khedmap.Utilities.UtilitiesSingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class WaitingForStartStatePresenter implements WaitingForStartStateContract.PresenterCallBack {

    private WaitingForStartStateContract.ViewCallBack view;
    private WaitingForStartStateModel model;

    private boolean responseReceived = true;

    private String submittedOrderId;
    private String selectedDisproofId;

    private String expertPhone;

    public WaitingForStartStatePresenter(WaitingForStartStateContract.ViewCallBack view, WaitingForStartStateModel model) {
        this.model = model;
        this.view = view;
    }


    @Override
    public void setSubmittedOrderId(String submittedOrderId) {

        this.submittedOrderId = submittedOrderId;
    }


    private void setResponseReceived() {

        responseReceived = true;
        view.showLoadingView(false);

    }

    @Override
    public void fetchDataAndGenerateOrderDetailItems(String data) {


        try {
            JSONObject jsonobject = new JSONObject(data);


            //to show static part
            List<String> orderDetail = new ArrayList<>();

            orderDetail.add(jsonobject.getString("title"));
            orderDetail.add(jsonobject.getString("service_time"));
            orderDetail.add(jsonobject.getString("address"));
            orderDetail.add(jsonobject.getString("status"));
            orderDetail.add(UtilitiesSingleton.getInstance().convertPrice(jsonobject.getInt("price")));


            view.setStaticPartValues(orderDetail);
            //till here


            //to show dynamic part (order detail)
            JSONArray properties = jsonobject.getJSONArray("properties");

            if (properties.length() == 0)
                view.setNoDetailItemToShow();


            for (int i = 0; i < properties.length(); i++) {
                JSONObject questionItem = properties.getJSONObject(i);

                view.generateTitleItem(questionItem.getString("title"));

                if (questionItem.getString("type").equals("file"))
                    view.generateImageAnswerItem(questionItem.getJSONArray("values").getString(0));

                else {

                    JSONArray answers = questionItem.getJSONArray("values");
                    for (int j = 0; j < answers.length(); j++)
                        view.generateAnswerItem(answers.getString(j));
                }

            }
            //till here


            //to show static part (expert detail)
            JSONObject expert = jsonobject.getJSONObject("expert");

            List<String> expertDetail = new ArrayList<>();

            expertDetail.add(expert.getString("avatar"));
            expertDetail.add(expert.getString("name"));
            expertPhone = expert.getString("phone");

            view.setExpertDetailValues(expertDetail);
            //till here


            //to show teammates
            if (expert.getString("teammate").equals("null"))
                view.setNoTeammateToShow();
            else {

                List<Teammate> teammates = new ArrayList<>();

                JSONArray teammateArray = expert.getJSONArray("teammate");

                for (int i = 0; i < teammateArray.length(); i++) {
                    JSONObject teammateItem = teammateArray.getJSONObject(i);

                    Teammate teammate = new Teammate();
                    teammate.setName(teammateItem.getString("name"));
                    teammate.setAvatar(teammateItem.getString("avatar"));

                    teammates.add(teammate);
                }

                view.showTeammatesRecyclerView(teammates);
            }
            //till here

        } catch (JSONException e) {
            Log.e("ParsError", e.getMessage());
            view.showSnackBar("خطای شبکه");
        }
    }


    @Override
    public void callExpertButtonClicked(Context context) {

        if (!responseReceived)   //for Block requests till Response not received
            return;

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("tel:" + expertPhone));
        context.startActivity(intent);
    }


    @Override
    public void cancelOrderButtonClicked(Context context) {

        if (!responseReceived)   //for Block requests till Response not received
            return;

        if (!UtilitiesSingleton.getInstance().isNetworkAvailable(context)) {
            view.showSnackBar("اتصال اینترنت را بررسی کنید");
            return;
        }

        view.showLoadingView(true);
        responseReceived = false;

        //to send VerifyFinishOrder Request
        sendGetDisproofItemsRequest(context);

    }


    ///////////////////////////////////////
//to get DisproofItems from server
    private void sendGetDisproofItemsRequest(final Context context) {


        //to buildRequestBody
        String requestBody = buildGetDisproofItemsRequestBody(context);

        if (requestBody == null)
            return;

        Response.Listener responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {


                List<Disproof> disproofs = GetDisproofItemsFetchData(response);

                if (disproofs != null)
                    view.showDisproofsDialog(disproofs);

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


        model.sendGetDisproofItemsRequest(context, requestBody, responseListener, errorListener);

    }


    private String buildGetDisproofItemsRequestBody(Context context) {


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


    private List<Disproof> GetDisproofItemsFetchData(String response) {


        try {
            JSONObject jsonobject = new JSONObject(response);

            //to get status
            String status = jsonobject.getString("status");

            if (status.equals("success")) {

                List<Disproof> disproofs = new ArrayList<>();

                JSONArray data = jsonobject.getJSONArray("data");

                for (int i = 0; i < data.length(); i++) {
                    JSONObject disproofItem = data.getJSONObject(i);

                    Disproof disproof = new Disproof();
                    disproof.setIdentification(disproofItem.getString("identification"));
                    disproof.setTitle(disproofItem.getString("disproof"));


                    disproofs.add(disproof);
                }

                return disproofs;

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
    public void disproofItemSelected(Disproof selectedDisproof, Context context) {

        selectedDisproofId = selectedDisproof.getIdentification();

        view.showConfirmationDialog();
    }


    @Override
    public void rejectOrderClicked(Activity activity) {

        if (!responseReceived)   //for Block requests till Response not received
            return;

        if (!UtilitiesSingleton.getInstance().isNetworkAvailable(activity)) {
            view.showSnackBar("اتصال اینترنت را بررسی کنید");
            return;
        }

        view.showLoadingView(true);
        responseReceived = false;

        //to send RejectOrder Request
        sendRejectOrderRequest(activity);

    }


    ///////////////////////////////////////
//to send RejectOrder request to server
    private void sendRejectOrderRequest(final Activity activity) {


        //to buildRequestBody
        String requestBody = buildRejectOrderRequestBody(activity);

        if (requestBody == null)
            return;

        Response.Listener responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                if (rejectOrderFetchData(response))
                    activity.finish();

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


        model.sendRejectOrderRequest(activity, requestBody, responseListener, errorListener);

    }


    private String buildRejectOrderRequestBody(Context context) {


        try {

            //to build detail parameter
            JSONObject detail = new JSONObject();
            detail.put("order", submittedOrderId);
            detail.put("disproof", selectedDisproofId);

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


    private boolean rejectOrderFetchData(String response) {


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
