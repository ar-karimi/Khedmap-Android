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
import com.khedmap.khedmap.Order.DataModels.SearchResult;
import com.khedmap.khedmap.Order.Model.SearchModel;
import com.khedmap.khedmap.Order.SearchContract;
import com.khedmap.khedmap.Order.View.InfiniteCategoryActivity;
import com.khedmap.khedmap.Order.View.OrderSpecificationActivity;
import com.khedmap.khedmap.Order.View.SubCategoryActivity;
import com.khedmap.khedmap.R;
import com.khedmap.khedmap.Utilities.UtilitiesSingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class SearchPresenter implements SearchContract.PresenterCallBack {

    private SearchContract.ViewCallBack view;
    private SearchModel model;

    private boolean responseReceived = true;

    private Activity activity; //store Activity for use in requests and delay (get one time to better Performance)
    private Timer timer; //to use for search delay
    private long DELAY;


    public SearchPresenter(SearchContract.ViewCallBack view, SearchModel model) {
        this.model = model;
        this.view = view;
    }

    @Override
    public void setActivityContext(Activity activity) {

        this.activity = activity;

        timer = new Timer();
        DELAY = 1000; // milliseconds
    }

    private void setResponseReceived() {

        responseReceived = true;
        view.showSearchLoadingView(false);
        view.showLoadingView(false);

    }


    @Override
    public void textChanged(final String input) {

        //To reset delay timer
        timer.cancel();
        timer = new Timer();

        if (input.length() == 0)
            view.showClearIcon(false);
        else
            view.showClearIcon(true);

        //After 1000 ms send request
        if (input.trim().length() >= 2) {
            timer.schedule(
                    new TimerTask() {
                        @Override
                        public void run() {
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    searchPhraseTyped(input.trim());
                                }
                            });
                        }
                    },
                    DELAY
            );

        } else {
            model.cancelRequest(); //to cancel prev Request
            view.hideItems();
            view.showBackgroundImage();
        }


    }


    private void searchPhraseTyped(String input) {


        if (!UtilitiesSingleton.getInstance().isNetworkAvailable(activity)) {
            view.showSnackBar("اتصال اینترنت را بررسی کنید");
            return;
        }

        view.hideItems();
        view.showSearchLoadingView(true);
        responseReceived = false;

        //to send search Request
        model.cancelRequest(); //to cancel prev Request
        sendSearchRequest(input, activity);
    }


    ///////////////////////////////////////
//to send search Request
    private void sendSearchRequest(String input, final Context context) {


        //to buildRequestBody
        String requestBody = buildSearchRequestBody(input, context);

        if (requestBody == null)
            return;

        Response.Listener responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                List<SearchResult> searchResults = searchFetchData(response);

                if (searchResults != null) {

                    if (searchResults.size() != 0) {
                        view.setResultListVisible();
                        view.showSearchResultsRecyclerView(searchResults);
                    } else
                        view.setNoItemToShowVisible();

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


        model.sendSearchRequest(context, requestBody, responseListener, errorListener);

    }


    private String buildSearchRequestBody(String input, Context context) {


        try {

            //to build jsonBody
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("query", input);
            //get apiToken from Shared Preferences
            jsonBody.put("api_token", model.getApiTokenSharedPref(context));

            return jsonBody.toString();


        } catch (JSONException e) {
            e.printStackTrace();

            return null;
        }

    }


    private List<SearchResult> searchFetchData(String response) {


        try {
            JSONObject jsonobject = new JSONObject(response);

            //to get status
            String status = jsonobject.getString("status");

            if (status.equals("success")) {

                List<SearchResult> searchResults = new ArrayList<>();

                JSONArray data = jsonobject.getJSONArray("data");

                for (int i = 0; i < data.length(); i++) {
                    JSONObject searchResultItem = data.getJSONObject(i);

                    SearchResult searchResult = new SearchResult();

                    searchResult.setName(searchResultItem.getString("name"));
                    searchResult.setIdentification(searchResultItem.getString("identification"));
                    searchResult.setType(searchResultItem.getString("type"));

                    if (searchResultItem.getString("type").equals("category"))
                        searchResult.setIconRes(R.drawable.search_result_category);
                    else
                        searchResult.setIconRes(R.drawable.search_result_subcategory);

                    searchResults.add(searchResult);
                }

                return searchResults;

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
    public void searchResultItemClicked(SearchResult item, Activity activity) {


        if (!responseReceived)   //for Block requests till Response not received
            return;

        if (!UtilitiesSingleton.getInstance().isNetworkAvailable(activity)) {
            view.showSnackBar("اتصال اینترنت را بررسی کنید");
            return;
        }


        if (item.getType().equals("category")) { //if has child must go to infiniteActivity, else subcategoryActivity

            view.showLoadingView(true);
            responseReceived = false;

            //to send checkCategoryChildren Request
            sendCheckCategoryChildrenRequest(item.getIdentification(), item.getName(), activity);
        } else {
            activity.startActivity(new Intent(activity, OrderSpecificationActivity.class).putExtra("itemId", item.getIdentification())
                    .putExtra("itemName", item.getName()));
        }

    }


    ///////////////////////////////////////
//to send checkCategoryChildren Request
    private void sendCheckCategoryChildrenRequest(final String itemId, final String itemName, final Activity activity) {


        //to buildRequestBody
        String requestBody = buildCheckCategoryChildrenRequestBody(itemId, activity);

        if (requestBody == null)
            return;

        Response.Listener responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                String hasChild = checkCategoryChildrenFetchData(response);

                if (hasChild != null) {

                    if (hasChild.equals("true")) {

                        activity.startActivity(new Intent(activity, InfiniteCategoryActivity.class).putExtra("itemId", itemId)
                                .putExtra("itemName", itemName));
                    } else {

                        activity.startActivity(new Intent(activity, SubCategoryActivity.class).putExtra("itemId", itemId)
                                .putExtra("itemName", itemName));
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


        model.sendCheckCategoryChildrenRequest(activity, requestBody, responseListener, errorListener);

    }


    private String buildCheckCategoryChildrenRequestBody(String itemId, Context context) {


        try {

            //to build jsonBody
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("category", itemId);
            //get apiToken from Shared Preferences
            jsonBody.put("api_token", model.getApiTokenSharedPref(context));

            return jsonBody.toString();


        } catch (JSONException e) {
            e.printStackTrace();

            return null;
        }

    }


    private String checkCategoryChildrenFetchData(String response) {


        try {
            JSONObject jsonobject = new JSONObject(response);

            //to get status
            String status = jsonobject.getString("status");

            if (status.equals("success")) {

                JSONObject data = jsonobject.getJSONObject("data");

                return data.getString("hasChildren");

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

//till here
///////////////////////////////////////


}
