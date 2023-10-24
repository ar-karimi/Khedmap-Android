package com.khedmap.khedmap.Order;


import android.app.Activity;
import android.content.Context;

import com.android.volley.Response;
import com.khedmap.khedmap.Order.DataModels.BottomSheetCategory;
import com.khedmap.khedmap.Order.DataModels.Category;
import com.khedmap.khedmap.Order.DataModels.MiddleCategory;
import com.khedmap.khedmap.Order.DataModels.Slide;

import java.util.List;

public interface HomeContract {

    interface ViewCallBack {

        void showSnackBar(String message);

        void showProgressBar(boolean showProgressBar);

        void setNavigationHeaderValues(String fullName, String credit, String avatar);

        void showSlides(List<Slide> slides);

        void showCategoriesRecyclerView(List<Category> categories);

        void initBottomSheetView ();

        void showBottomSheetCategoriesRecyclerView(List<BottomSheetCategory> bottomSheetCategories, int itemPosition);

        void showMiddleCategoriesRecyclerView(List<MiddleCategory> middleCategories);

        void showBottomSheet();

        void showBottomSheetProgressBar(boolean showProgressBar);

        void showSearchTourGuide();
    }


    interface ModelCallBack {

        void sendGetCategoryItemsRequest(Context context, final String requestBody,
                               Response.Listener responseListener, Response.ErrorListener errorListener);

        void sendGetMiddleCategoryItemsRequest(Context context, final String requestBody,
                               Response.Listener responseListener, Response.ErrorListener errorListener);

        void sendCheckCategoryChildrenRequest(Context context, String requestBody, Response.Listener responseListener, Response.ErrorListener errorListener);

        void setApiTokenSharedPref(String apiToken, Context context);

        String getApiTokenSharedPref(Context context);

        String getNameSharedPref(Context context);

        String getFamilySharedPref(Context context);

        String getAvatarSharedPref(Context context);

        String getCreditSharedPref(Context context);

        String getSupportPhoneSharedPref(Context context);

        boolean getGuideTourShowedSharedPref(Context context);

        void setGuideTourShowedSharedPref(Context context);
    }


    interface PresenterCallBack {

        void setupNavigationView(Context context);

        void callToSupportButtonClicked(Context context);

        void logOutButtonClicked(Context context);

        void initSliderAndCategories(Activity activity);

        void slideItemClicked(Slide item, Activity activity);

        void categoryItemClicked(Category item, Activity activity);

        boolean bottomSheetCategoryItemClicked(BottomSheetCategory item, Activity activity);

        void middleCategoryItemClicked(MiddleCategory item, Activity activity);

        void showTourGuides(Context context);
    }

}
