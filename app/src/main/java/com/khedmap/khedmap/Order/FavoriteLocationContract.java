package com.khedmap.khedmap.Order;

import android.content.Context;

import com.android.volley.Response;
import com.khedmap.khedmap.Order.DataModels.FavoriteExpert;
import com.khedmap.khedmap.Order.DataModels.FavoriteLocation;
import com.khedmap.khedmap.Order.DataModels.Order;

import java.util.List;

public interface FavoriteLocationContract {

    interface ViewCallBack {

        void showFavoriteLocationsRecyclerView(List<FavoriteLocation> favoriteLocations);

        void showSnackBar(String message);

        void showLoadingView(boolean showLoadingView);

    }


    interface ModelCallBack {

        void setApiTokenSharedPref(String apiToken, Context context);

        String getApiTokenSharedPref(Context context);

        void sendGetFavoriteLocationsRequest(Context context, final String requestBody, Response.Listener responseListener, Response.ErrorListener errorListener);

        void sendRemoveLocationRequest(Context context, final String requestBody, Response.Listener responseListener, Response.ErrorListener errorListener);

    }


    interface PresenterCallBack {

        void initFavoriteLocationsRecyclerView(Context context);

        void removeButtonClicked(String selectedItemId, Context context);

        void editButtonClicked(String selectedItemId, Context context);
    }

}
