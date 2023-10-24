package com.khedmap.khedmap.Order;

import android.app.Activity;
import android.content.Context;

import com.google.android.gms.maps.model.LatLng;

public interface MapsContract {

    interface ViewCallBack {

        void showSnackBar(String message);

        void enableMyLocationButton();

        void enableSubmitButton(boolean isEnable);

    }


    interface ModelCallBack {

        void setApiTokenSharedPref(String apiToken, Context context);

        String getApiTokenSharedPref(Context context);

    }


    interface PresenterCallBack {

        void setSelectedItemId(String suggestionId, String submittedOrderId);

        void getLocationPermission(Activity activity);

        void setSelectedLatLng (LatLng selectedLatLng);

        void submitButtonClicked(Activity activity);

    }

}
