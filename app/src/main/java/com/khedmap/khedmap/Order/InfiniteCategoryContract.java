package com.khedmap.khedmap.Order;

import android.app.Activity;
import android.content.Context;

import com.android.volley.Response;
import com.khedmap.khedmap.Order.DataModels.InfiniteCategory;
import com.khedmap.khedmap.Order.DataModels.Slide;

import java.util.List;

public interface InfiniteCategoryContract {

    interface ViewCallBack {

        void showSlides(List<Slide> slides);

        void setNavigationHeaderValues(String fullName, String credit, String avatar);

        void showSnackBar(String message);

        void showProgressBar(boolean showProgressBar);

        void showInfiniteCategoriesRecyclerView(List<InfiniteCategory> infiniteCategories);

    }

    interface ModelCallBack {

        void setApiTokenSharedPref(String apiToken, Context context);

        String getApiTokenSharedPref(Context context);

        String getNameSharedPref(Context context);

        String getFamilySharedPref(Context context);

        String getAvatarSharedPref(Context context);

        String getCreditSharedPref(Context context);

        String getSupportPhoneSharedPref(Context context);

        void sendGetInfiniteCategoryItemsRequest(Context context, final String requestBody, Response.Listener responseListener, Response.ErrorListener errorListener);

        void sendCheckCategoryChildrenRequest(Context context, String requestBody, Response.Listener responseListener, Response.ErrorListener errorListener);
    }

    interface PresenterCallBack {

        void setupNavigationView(Context context);

        void callToSupportButtonClicked(Context context);

        void logOutButtonClicked(Context context);

        void setSelectedItemId(String selectedItemId);

        void initSliderAndInfiniteCategories(Activity activity, boolean sameListRefreshed);

        void slideItemClicked(Slide item, Activity activity);

        void infiniteCategoryItemClicked(InfiniteCategory item, Activity activity);

        boolean backButtonClicked();
    }

}
