package com.khedmap.khedmap.Order;

import android.app.Activity;
import android.content.Context;

import com.android.volley.Response;
import com.khedmap.khedmap.Order.DataModels.District;
import com.khedmap.khedmap.Order.DataModels.FavoriteExpert;
import com.khedmap.khedmap.Order.DataModels.FavoriteLocation;
import com.khedmap.khedmap.Order.DataModels.Order;
import com.khedmap.khedmap.Order.DataModels.Zone;

import java.util.List;

public interface EditFavoriteLocationContract {

    interface ViewCallBack {

        void showSnackBar(String message);

        void showLoadingView(boolean showLoadingView);

        void showToast(String message);

        void showZonesDialog(List<Zone> zones);

        void showDistrictsDialog(List<District> districts);

        void enableDistrictButton(boolean isEnable);

        void showDistrictButtonProgressBar(boolean isShow);

        void setZoneButtonTitle(String title);

        void setDistrictButtonTitle(String title);

        void setPageValues(String zone, String district, String name, String address);
    }


    interface ModelCallBack {

        void setApiTokenSharedPref(String apiToken, Context context);

        String getApiTokenSharedPref(Context context);

        void sendFavoriteLocationDetailRequest(Context context, final String requestBody, Response.Listener responseListener, Response.ErrorListener errorListener);

        void sendGetDistrictsRequest(Context context, String requestBody, Response.Listener responseListener, Response.ErrorListener errorListener);

        void sendEditFavoriteLocationRequest(Context context, String requestBody, Response.Listener responseListener, Response.ErrorListener errorListener);
    }


    interface PresenterCallBack {

        void setFavoriteLocationId(String favoriteLocationId);

        void initPageItems(Context context);

        void selectZoneButtonClicked(Context context);

        void zoneItemSelected(Zone selectedZone, Context context);

        void getDistrictButtonClicked(Context context);

        void districtItemSelected(District selectedDistrict);

        void mapButtonClicked(Activity activity);

        void setNewLatLng(String latitude, String longitude);

        void submitButtonClicked(Activity activity, String name, String address);
    }

}
