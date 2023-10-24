package com.khedmap.khedmap.Order;

import android.app.Activity;
import android.content.Context;

import com.android.volley.Response;

import java.util.List;

public interface SuggestionDetailContract {

    interface ViewCallBack {

        void showSnackBar(String message);

        void showProgressBar(boolean showProgressBar);

        void setStaticPartValues(List<String> suggestionDetail);

        void generateAdditionalServiceItem(int id, String title, String price);

        void setNoAdditionalService();

    }

    interface ModelCallBack {

        void setApiTokenSharedPref(String apiToken, Context context);

        String getApiTokenSharedPref(Context context);

        void sendGetSuggestionDetailItemsRequest(Context context, final String requestBody, Response.Listener responseListener, Response.ErrorListener errorListener);

        void sendAcceptSuggestionRequest(Context context, String requestBody, Response.Listener responseListener, Response.ErrorListener errorListener);
    }

    interface PresenterCallBack {

        void setSelectedItemId(String selectedItemId, String submittedOrderId);

        void initPageItems(Activity activity);

        void submitButtonClicked(Activity activity);
    }

}
