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
import com.khedmap.khedmap.Order.DataModels.BottomSheetCategory;
import com.khedmap.khedmap.Order.DataModels.Category;
import com.khedmap.khedmap.Order.DataModels.MiddleCategory;
import com.khedmap.khedmap.Order.DataModels.Slide;
import com.khedmap.khedmap.Order.HomeContract;
import com.khedmap.khedmap.Order.Model.HomeModel;
import com.khedmap.khedmap.Order.View.InfiniteCategoryActivity;
import com.khedmap.khedmap.Order.View.OrderSpecificationActivity;
import com.khedmap.khedmap.Order.View.SubCategoryActivity;
import com.khedmap.khedmap.Utilities.UtilitiesSingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class HomePresenter implements HomeContract.PresenterCallBack {

    private HomeContract.ViewCallBack view;
    private HomeModel model;

    private boolean responseReceived = true;

    private List<Category> categories;
    private List<MiddleCategory> middleCategories;

    private String selectedItemId;

    public HomePresenter(HomeContract.ViewCallBack view, HomeModel model) {
        this.model = model;
        this.view = view;
    }


    @Override
    public void setupNavigationView(Context context) {

        String fullName = model.getNameSharedPref(context) + " " + model.getFamilySharedPref(context);
        if (model.getCreditSharedPref(context).isEmpty()) //because crashed if credit be empty
            return;
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


    private void setResponseReceived(boolean isFirsTime) {
        responseReceived = true;

        if (isFirsTime)
            view.showProgressBar(false);
        else
            view.showBottomSheetProgressBar(false);
    }

    @Override
    public void initSliderAndCategories(Activity activity) {

        if (!responseReceived)  //for Block requests till Response not received
            return;

        if (!UtilitiesSingleton.getInstance().isNetworkAvailable(activity)) {
            view.showSnackBar("اتصال اینترنت را بررسی کنید");
            view.showProgressBar(false);
            return;
        }

        view.showProgressBar(true);
        responseReceived = false;

        //to get CategoryItems from server
        sendGetCategoryItemsRequest(activity);
    }


    ///////////////////////////////////////
//to get CategoryItems from server
    private void sendGetCategoryItemsRequest(final Activity activity) {


        //to buildRequestBody
        String requestBody = buildGetCategoryItemsRequestBody(activity);

        if (requestBody == null)
            return;

        Response.Listener responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {


                GetCategoryItemsFetchData(response);


                setResponseReceived(true);
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

                setResponseReceived(true);


            }
        };


        model.sendGetCategoryItemsRequest(activity, requestBody, responseListener, errorListener);

    }


    private String buildGetCategoryItemsRequestBody(Activity activity) {


        try {

            //to build detail parameter
            JSONObject detail = new JSONObject(); //detail value must be empty in this request

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


    private void GetCategoryItemsFetchData(String response) {


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

                //till here


                //to get categories
                List<Category> categories = new ArrayList<>();

                JSONArray categoriesJSONArray = data.getJSONArray("categories");

                for (int i = 0; i < categoriesJSONArray.length(); i++) {
                    JSONObject categoryItem = categoriesJSONArray.getJSONObject(i);

                    Category category = new Category();
                    category.setHasChild(categoryItem.getBoolean("hasChild"));
                    category.setIcon(categoryItem.getString("icon"));
                    category.setName(categoryItem.getString("name"));
                    category.setServerId(categoryItem.getString("id"));

                    categories.add(category);
                }

                this.categories = categories;
                view.showCategoriesRecyclerView(categories);
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

                setResponseReceived(true);
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

                setResponseReceived(true);


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
    public void categoryItemClicked(Category item, Activity activity) {


        if (!responseReceived)   //for Block requests till Response not received
            return;

        if (categories == null)
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


        //to build BottomSheetView
        view.initBottomSheetView();

        //init BottomSheetCategoriesRecyclerView
        int itemPosition;
        for (itemPosition = 0; !item.getServerId().equals(categories.get(itemPosition).getServerId()); itemPosition++)
            ;
        view.showBottomSheetCategoriesRecyclerView(generateBottomSheetCategoryItems(), itemPosition);

        //to get MiddleCategoryItems from server
        sendGetMiddleCategoryItemsRequest(activity, item.getServerId(), true);

    }

    private List<BottomSheetCategory> generateBottomSheetCategoryItems() {

        List<BottomSheetCategory> bottomSheetCategories = new ArrayList<>();
        for (int i = 0; i < categories.size(); i++) {
            BottomSheetCategory bottomSheetCategory = new BottomSheetCategory();

            bottomSheetCategory.setHasChild(categories.get(i).getHasChild());
            bottomSheetCategory.setName(categories.get(i).getName());
            bottomSheetCategory.setServerId(categories.get(i).getServerId());

            bottomSheetCategories.add(bottomSheetCategory);

        }

        return bottomSheetCategories;
    }


///////////////////////////////////////
//to get MiddleCategoryItems from server

    private void sendGetMiddleCategoryItemsRequest(final Activity activity, final String serverId, final boolean isFirstTime) {

        selectedItemId = serverId;

        //to buildRequestBody
        String requestBody = buildGetMiddleCategoryItemsRequestBody(activity, serverId);

        if (requestBody == null)
            return;

        Response.Listener responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {


                List<MiddleCategory> middleCategories = GetMiddleCategoryItemsFetchData(response);

                if (middleCategories != null) {
                    if (isFirstTime)
                        view.showBottomSheet();
                    view.showMiddleCategoriesRecyclerView(middleCategories);
                }


                setResponseReceived(isFirstTime);
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

                setResponseReceived(isFirstTime);


            }
        };


        model.sendGetMiddleCategoryItemsRequest(activity, requestBody, responseListener, errorListener);

    }


    private String buildGetMiddleCategoryItemsRequestBody(Activity activity, String serverId) {


        try {

            //to build detail parameter
            JSONObject detail = new JSONObject();
            detail.put("parent", serverId);

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


    private List<MiddleCategory> GetMiddleCategoryItemsFetchData(String response) {


        try {
            JSONObject jsonobject = new JSONObject(response);

            //to get status
            String status = jsonobject.getString("status");

            if (status.equals("success")) {

                List<MiddleCategory> middleCategories = new ArrayList<>();

                JSONObject data = jsonobject.getJSONObject("data");

                JSONArray middleCategoriesJSONArray = data.getJSONArray("categories");

                for (int i = 0; i < middleCategoriesJSONArray.length(); i++) {
                    JSONObject middleCategoryItem = middleCategoriesJSONArray.getJSONObject(i);

                    MiddleCategory middleCategory = new MiddleCategory();
                    middleCategory.setHasChild(middleCategoryItem.getBoolean("hasChild"));
                    middleCategory.setIcon(middleCategoryItem.getString("icon"));
                    middleCategory.setName(middleCategoryItem.getString("name"));
                    middleCategory.setServerId(middleCategoryItem.getString("id"));

                    middleCategories.add(middleCategory);
                }

                this.middleCategories = middleCategories;
                return middleCategories;

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
    public boolean bottomSheetCategoryItemClicked(BottomSheetCategory item, Activity activity) {

        if (selectedItemId.equals(item.getServerId()))
            return false;

        if (!responseReceived)   //for Block requests till Response not received
            return false;

        if (!UtilitiesSingleton.getInstance().isNetworkAvailable(activity)) {
            view.showSnackBar("اتصال اینترنت را بررسی کنید");
            return false;
        }


        if (!item.getHasChild()) {
            activity.startActivity(new Intent(activity, SubCategoryActivity.class).putExtra("itemId", item.getServerId())
                    .putExtra("itemName", item.getName()));
            return false;
        }

        view.showBottomSheetProgressBar(true);
        responseReceived = false;


        //to get MiddleCategoryItems from server
        sendGetMiddleCategoryItemsRequest(activity, item.getServerId(), false);

        return true;
    }


    @Override
    public void middleCategoryItemClicked(MiddleCategory item, Activity activity) {

        if (!responseReceived)   //for Block requests till Response not received
            return;


        if (!UtilitiesSingleton.getInstance().isNetworkAvailable(activity)) {
            view.showSnackBar("اتصال اینترنت را بررسی کنید");
            return;
        }

        if (item.getHasChild()) {
            activity.startActivity(new Intent(activity, InfiniteCategoryActivity.class).putExtra("itemId", item.getServerId())
                    .putExtra("itemName", item.getName()));
        } else {
            activity.startActivity(new Intent(activity, SubCategoryActivity.class).putExtra("itemId", item.getServerId())
                    .putExtra("itemName", item.getName()));
        }

    }

    @Override
    public void showTourGuides(Context context) {

        if (!model.getGuideTourShowedSharedPref(context)) { //check is first time app launch
            model.setGuideTourShowedSharedPref(context);
            view.showSearchTourGuide();
        }
    }

}
