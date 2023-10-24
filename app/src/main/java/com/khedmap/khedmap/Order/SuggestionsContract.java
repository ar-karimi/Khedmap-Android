package com.khedmap.khedmap.Order;

import android.app.Activity;
import android.content.Context;

import com.android.volley.Response;
import com.khedmap.khedmap.Order.DataModels.Suggestion;

import java.util.List;

public interface SuggestionsContract {

    interface ViewCallBack {


        void showSnackBar(String message);

        void showProgressBar(boolean showProgressBar);

        void showLoadingText(boolean showLoadingText);

        void showSuggestionsRecyclerView(List<Suggestion> suggestions);


    }

    interface ModelCallBack {

        void setApiTokenSharedPref(String apiToken, Context context);

        String getApiTokenSharedPref(Context context);

        String getNameSharedPref(Context context);

        String getFamilySharedPref(Context context);

        String getAvatarSharedPref(Context context);

        String getCreditSharedPref(Context context);

        void sendGetSuggestionItemsRequest(Context context, final String requestBody, Response.Listener responseListener, Response.ErrorListener errorListener);

    }

    interface PresenterCallBack {


        void setSubmittedOrderId(String submittedOrderId);

        void initSuggestionsRecyclerView(Activity activity);

        void suggestionItemClicked(Suggestion item, Activity activity);

    }

}
