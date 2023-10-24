package com.khedmap.khedmap.Order;

import android.content.Context;

import com.android.volley.Response;
import com.khedmap.khedmap.Order.DataModels.FavoriteExpert;
import com.khedmap.khedmap.Order.DataModels.Order;

import java.util.List;

public interface FavoriteExpertContract {

    interface ViewCallBack {

        void showFavoriteExpertsRecyclerView(List<FavoriteExpert> favoriteExperts);

        void showSnackBar(String message);

        void showLoadingView(boolean showLoadingView);

    }


    interface ModelCallBack {

        void setApiTokenSharedPref(String apiToken, Context context);

        String getApiTokenSharedPref(Context context);

        void sendGetFavoriteExpertsRequest(Context context, final String requestBody, Response.Listener responseListener, Response.ErrorListener errorListener);

        void sendRemoveExpertRequest(Context context, final String requestBody, Response.Listener responseListener, Response.ErrorListener errorListener);

    }


    interface PresenterCallBack {

        void initFavoriteExpertsRecyclerView(Context context);


        void removeButtonClicked(String selectedItemId, Context context);
    }

}
