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
import com.khedmap.khedmap.Order.DataModels.InfiniteCategory;
import com.khedmap.khedmap.Order.DataModels.Slide;
import com.khedmap.khedmap.Order.InfiniteCategoryContract;
import com.khedmap.khedmap.Order.Model.InfiniteCategoryModel;
import com.khedmap.khedmap.Order.View.InfiniteCategoryActivity;
import com.khedmap.khedmap.Order.View.OrderSpecificationActivity;
import com.khedmap.khedmap.Order.View.SubCategoryActivity;
import com.khedmap.khedmap.Utilities.UtilitiesSingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class InfiniteCategoryPresenter implements InfiniteCategoryContract.PresenterCallBack {

    private InfiniteCategoryContract.ViewCallBack view;
    private InfiniteCategoryModel model;

    private boolean responseReceived = true;

    private List<String> selectedItemId = new ArrayList<>();

    private List<List<Slide>> receivedSlides = new ArrayList<>();
    private List<List<InfiniteCategory>> receivedLists = new ArrayList<>();

    public InfiniteCategoryPresenter(InfiniteCategoryContract.ViewCallBack view, InfiniteCategoryModel model) {
        this.model = model;
        this.view = view;
    }

    @Override
    public void setSelectedItemId(String selectedItemId) {

        this.selectedItemId.add(selectedItemId);

    }


    private void setResponseReceived() {

        responseReceived = true;
        view.showProgressBar(false);

    }

    @Override
    public void setupNavigationView(Context context) {

        String fullName = model.getNameSharedPref(context) + " " + model.getFamilySharedPref(context);
        int credit = Integer.parseInt(model.getCreditSharedPref(context));
        String avatarUrl = model.getAvatarSharedPref(context);

        view.setNavigationHeaderValues(fullName, UtilitiesSingleton.getInstance().convertPrice(credit), avatarUrl);
    }

    @Override
    public void callToSupportButtonClicked(Context context) {

        context.startActivity(new Intent(Intent.ACTION_DIAL)
                .setData(Uri.parse("tel:" + model.getSupportPhoneSharedPref(context))));
    }

    @Override
    public void logOutButtonClicked(Context context) {

//clear apiToken from sharedPref
        model.setApiTokenSharedPref("", context);

        Intent intent = new Intent(context, EnterMobileActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }


    @Override
    public void initSliderAndInfiniteCategories(Activity activity, boolean sameListRefreshed) {

        if (!responseReceived)  //for Block requests till Response not received
            return;

        if (!UtilitiesSingleton.getInstance().isNetworkAvailable(activity)) {
            view.showSnackBar("اتصال اینترنت را بررسی کنید");
            view.showProgressBar(false);
            return;
        }

        view.showProgressBar(true);
        responseReceived = false;

        //to get InfiniteCategoryItems from server
        sendGetInfiniteCategoryItemsRequest(activity, sameListRefreshed);
    }


    ///////////////////////////////////////
//to get InfiniteCategoryItems from server

    private void sendGetInfiniteCategoryItemsRequest(final Activity activity, final boolean sameListRefreshed) {


        //to buildRequestBody
        String requestBody = buildGetInfiniteCategoryItemsRequestBody(activity);

        if (requestBody == null)
            return;

        Response.Listener responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {


                GetInfiniteCategoryItemsFetchData(response, sameListRefreshed);

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


        model.sendGetInfiniteCategoryItemsRequest(activity, requestBody, responseListener, errorListener);

    }


    private String buildGetInfiniteCategoryItemsRequestBody(Activity activity) {


        try {

            //to build detail parameter
            JSONObject detail = new JSONObject();
            detail.put("parent", selectedItemId.get(selectedItemId.size() - 1)); //to get the last value in selectedItemId

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


    private void GetInfiniteCategoryItemsFetchData(String response, boolean sameListRefreshed) {


        try {
            JSONObject jsonobject = new JSONObject(response);

            //to get status
            String status = jsonobject.getString("status");

            if (status.equals("success")) {

                //to get slides
                List<Slide> slides = new ArrayList<>();

                JSONObject data = jsonobject.getJSONObject("data");

                JSONArray slidersJSONArray = data.getJSONArray("sliders");

                for (int i = 0; i < slidersJSONArray.length(); i++) {
                    JSONObject slideItem = slidersJSONArray.getJSONObject(i);

                    Slide slide = new Slide();
                    slide.setPicture(slideItem.getString("picture"));
                    slide.setTarget(slideItem.getString("target"));

                    if (slide.getTarget().equals("web")) //check target is web or application
                        slide.setLink(slideItem.getString("link"));
                    else {
                        slide.setType(slideItem.getString("type"));
                        slide.setIdentification(slideItem.getString("id"));
                        slide.setTitle(slideItem.getString("title"));
                    }

                    slides.add(slide);
                }


                if (slides.isEmpty())
                    view.showSlides(null);
                else if (slides.size() == 1) // if only one slide received add it 2 time in slider for Beauty (and Slider onClickListener have Bug in 1 slide, for that use must fix it with position=0; in onClick)
                {
                    slides.add(slides.get(0));
                    view.showSlides(slides);
                } else
                    view.showSlides(slides);


                if (sameListRefreshed)
                    receivedSlides.set(receivedSlides.size() - 1, slides); //to update last list in stack
                else
                    receivedSlides.add(slides);

                //till here


                //to get categories
                List<InfiniteCategory> infiniteCategories = new ArrayList<>();

                JSONArray infiniteCategoriesJSONArray = data.getJSONArray("categories");

                for (int i = 0; i < infiniteCategoriesJSONArray.length(); i++) {
                    JSONObject infiniteCategoryItem = infiniteCategoriesJSONArray.getJSONObject(i);

                    InfiniteCategory infiniteCategory = new InfiniteCategory();
                    infiniteCategory.setHasChild(infiniteCategoryItem.getBoolean("hasChild"));
                    infiniteCategory.setIcon(infiniteCategoryItem.getString("icon"));
                    infiniteCategory.setName(infiniteCategoryItem.getString("name"));
                    infiniteCategory.setServerId(infiniteCategoryItem.getString("id"));

                    infiniteCategories.add(infiniteCategory);
                }


                if (sameListRefreshed)
                    receivedLists.set(receivedLists.size() - 1, infiniteCategories); //to update last list in stack
                else
                    receivedLists.add(infiniteCategories);
                view.showInfiniteCategoriesRecyclerView(infiniteCategories);
                //till here

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
    public void slideItemClicked(Slide item, Activity activity) {


        if (!responseReceived)   //for Block requests till Response not received
            return;

        if (!UtilitiesSingleton.getInstance().isNetworkAvailable(activity)) {
            view.showSnackBar("اتصال اینترنت را بررسی کنید");
            return;
        }


        if (item.getTarget().equals("web")) //check target is web or application
        {
            if (!item.getLink().equals("null"))
                activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(item.getLink())));
        } else {

            if (item.getType().equals("category")) { //if has child must go to infiniteActivity, else subcategoryActivity

                view.showProgressBar(true);
                responseReceived = false;

                //to send checkCategoryChildren Request
                sendCheckCategoryChildrenRequest(item.getIdentification(), item.getTitle(), activity);
            } else {
                activity.startActivity(new Intent(activity, OrderSpecificationActivity.class).putExtra("itemId", item.getIdentification())
                        .putExtra("itemName", item.getTitle()));
            }
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


    @Override
    public void infiniteCategoryItemClicked(InfiniteCategory item, Activity activity) {


        if (!responseReceived)   //for Block requests till Response not received
            return;

        if (!UtilitiesSingleton.getInstance().isNetworkAvailable(activity)) {
            view.showSnackBar("اتصال اینترنت را بررسی کنید");
            return;
        }


        if (!item.getHasChild()) {
            activity.startActivity(new Intent(activity, SubCategoryActivity.class).putExtra("itemId", item.getServerId())
                    .putExtra("itemName", item.getName()));
            return;
        }


        view.showProgressBar(true);
        responseReceived = false;


        selectedItemId.add(item.getServerId());

        //to get InfiniteCategoryItems from server
        sendGetInfiniteCategoryItemsRequest(activity, false);

    }


    @Override
    public boolean backButtonClicked() {

        if (receivedLists == null || receivedSlides == null) //to Avoid of get null pointer Exception In effect clear list by Garbage Collector
            return true;

        if (receivedLists.size() < 2) //to check is first list or not
            return true;
        else {
            if (selectedItemId.size() == receivedSlides.size())
                receivedSlides.remove(receivedSlides.size() - 1);

            if (selectedItemId.size() == receivedLists.size())
                receivedLists.remove(receivedLists.size() - 1);

            selectedItemId.remove(selectedItemId.size() - 1); //to pop the last value from stack

            if (receivedSlides.get(receivedSlides.size() - 1).isEmpty())
                view.showSlides(null);
            else
                view.showSlides(receivedSlides.get(receivedSlides.size() - 1));

            view.showInfiniteCategoriesRecyclerView(receivedLists.get(receivedLists.size() - 1));

            return false;
        }
    }
}
