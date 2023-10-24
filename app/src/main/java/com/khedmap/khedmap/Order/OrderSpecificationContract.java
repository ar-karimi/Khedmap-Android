package com.khedmap.khedmap.Order;

import android.app.Activity;
import android.content.Context;

import com.android.volley.Response;

public interface OrderSpecificationContract {

    interface ViewCallBack {

        void showSnackBar(String message);

        void showProgressBar(boolean showProgressBar);

        void generateTitleItem(String value);

        void generateNormalTextItem(String value);

        void generateBoldTextItem(String value);

        void generateWarningItem(String value);

        void generateBorderedItem(String value);

        void generateBorderedTextItem(String value);

        void generateHintItem(String value);

        void generateListItem(String value);

        void generateImageItem(String url);

        void generateUnknownItem();

        void clearList();
    }

    interface ModelCallBack {

        void setApiTokenSharedPref(String apiToken, Context context);

        String getApiTokenSharedPref(Context context);

        void sendGetSpecificationRequest(Context context, final String requestBody, Response.Listener responseListener, Response.ErrorListener errorListener);

        String getNameSharedPref(Context context);

        String getFamilySharedPref(Context context);

        String getAvatarSharedPref(Context context);

        String getCreditSharedPref(Context context);

    }

    interface PresenterCallBack {

        void setSelectedItemId(String selectedItemId);

        void initSpecificationList(Activity activity);

        void submitButtonClicked(Activity activity);
    }

}
