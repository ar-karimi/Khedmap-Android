package com.khedmap.khedmap.Order;

import android.app.Activity;
import android.content.Context;

import com.android.volley.Response;

public interface GetDetailedAddressContract {

    interface ViewCallBack {

        void showSnackBar(String message);

        void showProgressBar(boolean showProgressBar);

    }

    interface ModelCallBack {

        void setApiTokenSharedPref(String apiToken, Context context);

        String getApiTokenSharedPref(Context context);

        void sendSubmitAddressRequest(Context context, final String requestBody, Response.Listener responseListener, Response.ErrorListener errorListener);

        void sendAcceptSuggestionRequest(Context context, final String requestBody, Response.Listener responseListener, Response.ErrorListener errorListener);

    }

    interface PresenterCallBack {

        void setSelectedItemIdAndLocation(String suggestionId, String submittedOrderId, String latitude, String longitude);

        void submitButtonClicked(Activity activity, String address);
    }

}
