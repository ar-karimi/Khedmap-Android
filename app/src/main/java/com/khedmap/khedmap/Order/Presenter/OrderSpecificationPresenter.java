package com.khedmap.khedmap.Order.Presenter;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.error.NoConnectionError;
import com.android.volley.error.TimeoutError;
import com.android.volley.error.VolleyError;
import com.khedmap.khedmap.LoginSignUp.View.EnterMobileActivity;
import com.khedmap.khedmap.Order.Model.OrderSpecificationModel;
import com.khedmap.khedmap.Order.OrderSpecificationContract;
import com.khedmap.khedmap.Order.View.QuestionsActivity;
import com.khedmap.khedmap.Utilities.UtilitiesSingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class OrderSpecificationPresenter implements OrderSpecificationContract.PresenterCallBack {

    private OrderSpecificationContract.ViewCallBack view;
    private OrderSpecificationModel model;

    private String selectedItemId;

    private boolean responseReceived = true;


    public OrderSpecificationPresenter(OrderSpecificationContract.ViewCallBack view, OrderSpecificationModel model) {
        this.model = model;
        this.view = view;
    }


    @Override
    public void setSelectedItemId(String selectedItemId) {

        this.selectedItemId = selectedItemId;
    }

    private void setResponseReceived() {

        responseReceived = true;
        view.showProgressBar(false);

    }

    @Override
    public void initSpecificationList(Activity activity) {

        if (!responseReceived) {   //for Block requests till Response not received
            view.showProgressBar(false);
            return;
        }

        if (!UtilitiesSingleton.getInstance().isNetworkAvailable(activity)) {
            view.showSnackBar("اتصال اینترنت را بررسی کنید");
            view.showProgressBar(false);
            return;
        }

        view.showProgressBar(true);
        responseReceived = false;

        //to get CategoryItems from server
        sendGetSpecificationRequest(activity);
    }


    ///////////////////////////////////////
//to get Specification from server

    private void sendGetSpecificationRequest(final Activity activity) {


        //to buildRequestBody
        String requestBody = buildGetSpecificationRequestBody(activity);

        if (requestBody == null)
            return;

        Response.Listener responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                view.clearList();

                GetSubCategoryItemsFetchData(response);

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


        model.sendGetSpecificationRequest(activity, requestBody, responseListener, errorListener);

    }

    private String buildGetSpecificationRequestBody(Activity activity) {


        try {

            //to build detail parameter
            JSONObject detail = new JSONObject();
            detail.put("subcategory_id", selectedItemId);

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


    private void GetSubCategoryItemsFetchData(String response) {


        try {
            JSONObject jsonobject = new JSONObject(response);

            //to get status
            String status = jsonobject.getString("status");

            if (status.equals("success")) {

                JSONArray data = jsonobject.getJSONArray("data");

                for (int i = 0; i < data.length(); i++) {
                    JSONObject SpecificationItem = data.getJSONObject(i);

                    String section = SpecificationItem.getString("section");
                    String value = SpecificationItem.getString("value");

                    switch (section) {
                        case "title":
                            view.generateTitleItem(value);
                            break;
                        case "normalText":
                            view.generateNormalTextItem(value);
                            break;
                        case "boldText":
                            view.generateBoldTextItem(value);
                            break;
                        case "warning":
                            view.generateWarningItem(value);
                            break;
                        case "bordered":
                            view.generateBorderedItem(value);
                            break;
                        case "borderedText":
                            view.generateBorderedTextItem(value);
                            break;
                        case "hint":
                            view.generateHintItem(value);
                            break;
                        case "list":
                            view.generateListItem(value);
                            break;
                        case "image":
                            view.generateImageItem(value);
                            break;

                        default:
                            view.generateUnknownItem();
                    }


                }

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

        activity.startActivity(new Intent(activity, QuestionsActivity.class).putExtra("itemId", selectedItemId));

    }


}
