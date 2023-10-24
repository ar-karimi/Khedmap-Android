package com.khedmap.khedmap.Order;

import android.app.Activity;
import android.content.Context;

import com.android.volley.Response;
import com.khedmap.khedmap.Order.DataModels.Slide;
import com.khedmap.khedmap.Order.DataModels.SubCategory;

import java.util.List;

public interface SubCategoryContract {

    interface ViewCallBack {

        void showSlides(List<Slide> slides);

        void setNavigationHeaderValues(String fullName, String credit, String avatar);

        void showSnackBar(String message);

        void showProgressBar(boolean showProgressBar);

        void showSubCategoriesRecyclerView(List<SubCategory> subCategories);

    }

    interface ModelCallBack {

        void setApiTokenSharedPref(String apiToken, Context context);

        String getApiTokenSharedPref(Context context);

        String getNameSharedPref(Context context);

        String getFamilySharedPref(Context context);

        String getAvatarSharedPref(Context context);

        String getCreditSharedPref(Context context);

        String getSupportPhoneSharedPref(Context context);

        void sendGetSubCategoryItemsRequest(Context context, final String requestBody, Response.Listener responseListener, Response.ErrorListener errorListener);

        void sendCheckCategoryChildrenRequest(Context context, String requestBody, Response.Listener responseListener, Response.ErrorListener errorListener);
    }

    interface PresenterCallBack {

        void setSelectedItemInfo(String selectedItemId, String selectedItemName);

        void setupNavigationView(Context context);

        void callToSupportButtonClicked(Context context);

        void logOutButtonClicked(Context context);

        void initSliderAndSubCategories(Activity activity);

        void slideItemClicked(Slide item, Activity activity);

        void subCategoryItemClicked(SubCategory item, boolean isJumped, Activity activity);
    }

}
