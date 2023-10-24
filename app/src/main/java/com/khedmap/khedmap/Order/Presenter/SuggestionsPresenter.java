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
import com.khedmap.khedmap.Order.DataModels.Suggestion;
import com.khedmap.khedmap.Order.Model.SuggestionsModel;
import com.khedmap.khedmap.Order.SuggestionsContract;
import com.khedmap.khedmap.Order.View.SuggestionDetailActivity;
import com.khedmap.khedmap.Utilities.UtilitiesSingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SuggestionsPresenter implements SuggestionsContract.PresenterCallBack {

    private SuggestionsContract.ViewCallBack view;
    private SuggestionsModel model;

    private String submittedOrderId;

    private boolean responseReceived = true;


    public SuggestionsPresenter(SuggestionsContract.ViewCallBack view, SuggestionsModel model) {
        this.model = model;
        this.view = view;
    }


    @Override
    public void setSubmittedOrderId(String submittedOrderId) {

        this.submittedOrderId = submittedOrderId;
    }


    private void setResponseReceived() {

        responseReceived = true;
        view.showProgressBar(false);

    }


    @Override
    public void initSuggestionsRecyclerView(Activity activity) {

        if (!responseReceived)  //for Block requests till Response not received
            return;

        if (!UtilitiesSingleton.getInstance().isNetworkAvailable(activity)) {
            view.showSnackBar("اتصال اینترنت را بررسی کنید");
            view.showProgressBar(false);
            return;
        }

        view.showProgressBar(true);
        responseReceived = false;

        //to get SuggestionItems from server
        sendGetSuggestionItemsRequest(activity);
    }


    ///////////////////////////////////////
//to get SuggestionItems from server

    private void sendGetSuggestionItemsRequest(final Activity activity) {


        //to buildRequestBody
        String requestBody = buildGetSuggestionItemsRequestBody(activity);

        if (requestBody == null)
            return;

        Response.Listener responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {


                List<Suggestion> suggestions = GetSuggestionItemsFetchData(response);

                if (suggestions != null) {
                    view.showSuggestionsRecyclerView(suggestions);

                    if (!suggestions.isEmpty())
                        view.showLoadingText(false);
                }


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


        model.sendGetSuggestionItemsRequest(activity, requestBody, responseListener, errorListener);

    }

    private String buildGetSuggestionItemsRequestBody(Activity activity) {


        try {

            //to build detail parameter
            JSONObject detail = new JSONObject();
            detail.put("order", submittedOrderId);

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


    private List<Suggestion> GetSuggestionItemsFetchData(String response) {


        try {
            JSONObject jsonobject = new JSONObject(response);

            //to get status
            String status = jsonobject.getString("status");

            if (status.equals("success")) {

                List<Suggestion> suggestions = new ArrayList<>();

                JSONArray data = jsonobject.getJSONArray("data");

                for (int i = 0; i < data.length(); i++) {
                    JSONObject suggestionItem = data.getJSONObject(i);

                    Suggestion suggestion = new Suggestion();
                    suggestion.setIdentification(suggestionItem.getString("identification"));
                    suggestion.setBasePrice(UtilitiesSingleton.getInstance().convertPrice(suggestionItem.getInt("base_price")));
                    suggestion.setExpertName(suggestionItem.getString("expert_name"));
                    suggestion.setExpertRate(suggestionItem.getDouble("expert_rate"));
                    suggestion.setExpertAvatar(suggestionItem.getString("expert_avatar"));
                    suggestion.setAds(suggestionItem.getBoolean("isAds"));
                    suggestion.setFavorite(suggestionItem.getBoolean("isFavorite"));


                    suggestions.add(suggestion);
                }

                return suggestions;

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
    public void suggestionItemClicked(Suggestion item, Activity activity) {


        if (!responseReceived)   //for Block requests till Response not received
            return;

        if (!UtilitiesSingleton.getInstance().isNetworkAvailable(activity)) {
            view.showSnackBar("اتصال اینترنت را بررسی کنید");
            return;
        }

        activity.startActivity(new Intent(activity, SuggestionDetailActivity.class).putExtra("SelectedItemId", item.getIdentification())
                .putExtra("submittedOrderId", submittedOrderId));

    }




}
