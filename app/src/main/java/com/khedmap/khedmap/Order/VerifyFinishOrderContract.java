package com.khedmap.khedmap.Order;

import android.app.Activity;
import android.content.Context;

import com.android.volley.Response;

public interface VerifyFinishOrderContract {

    interface ViewCallBack {

        void showSnackBar(String message);

        void showLoadingView(boolean showLoadingView);

        void showPageItems(String subcategory, String finalPrice, String factorPictureUrl);

        void showNoButtonDialog();

        void showPaymentTypeDialog(int currentCredit);

        void showFavoriteLocationDialog();

        void dismissPaymentTypeDialog();

        void dismissFavoriteLocationDialog();

        void showToast(String message);
    }


    interface ModelCallBack {

        void setApiTokenSharedPref(String apiToken, Context context);

        String getApiTokenSharedPref(Context context);

        void sendVerifyFinishOrderRequest(Context context, final String requestBody, Response.Listener responseListener, Response.ErrorListener errorListener);

        void sendPaymentTypeRequest(Context context, final String requestBody, Response.Listener responseListener, Response.ErrorListener errorListener);

        void sendCheckPayedOrderRequest(Context context, String requestBody, Response.Listener responseListener, Response.ErrorListener errorListener);

        void sendAddFavoriteLocationRequest(Context context, String requestBody, Response.Listener responseListener, Response.ErrorListener errorListener);
    }


    interface PresenterCallBack {

        void setFinishedOrderDetail(String orderId, String subcategory, int finalPrice, String factorPicture);

        void noButtonClicked(Context context);

        void verifyFinishOrderClicked(String operation, Activity activity);

        void paymentTypeButtonClicked(String paymentType, Activity activity);

        void checkPayedOrder(Activity activity);

        void addFavoriteLocationClicked(Activity activity, String favoriteLocationName);
    }

}
