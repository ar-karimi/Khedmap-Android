package com.khedmap.khedmap.Order;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;

import com.android.volley.Response;

public interface IncreaseCreditContract {

    interface ViewCallBack {

        void showSnackBar(String message);

        void showLoadingView(boolean showLoadingView);

        void setCurrentCredit(String credit);
    }


    interface ModelCallBack {

        void setApiTokenSharedPref(String apiToken, Context context);

        String getApiTokenSharedPref(Context context);

        void sendGetCreditRequest(Context context, String requestBody, Response.Listener responseListener, Response.ErrorListener errorListener);
    }


    interface PresenterCallBack {


        void initCurrentCredit(Activity activity);

        void submitButtonClicked(Activity activity, String credit);

        void handlePaymentResult(Uri uri, Activity activity);
    }

}
