package com.khedmap.khedmap.Order;

import android.content.Context;

import com.android.volley.Response;
import com.khedmap.khedmap.Order.DataModels.District;
import com.khedmap.khedmap.Order.DataModels.FavoriteLocation;
import com.khedmap.khedmap.Order.DataModels.Zone;

import java.util.List;

public interface GetDistrictContract {

    interface ViewCallBack {

        void showSnackBar(String message);

        void showFavoriteLocationsRecyclerView(List<FavoriteLocation> favoriteLocations);

        void setNoItemTextVisible();

        void showZonesDialog(List<Zone> zones);

        void showDistrictsDialog(List<District> districts);

        void enableSubmitButton(boolean isEnable);

        void showLoadingView(boolean isShow);

        void showSubmitOrderLoadingView(boolean isShow);

        void showCancelFavoriteButton(boolean isShow);

        void enableZoneButton(boolean isEnable);

        void enableDistrictButton(boolean isEnable);

        void showDistrictButtonProgressBar(boolean isShow);

        void setZoneButtonTitle(String title);

        void setDistrictButtonTitle(String title);

        //to show discountCode Dialog
        void showDiscountCodeDialog(String currentCode);

        void setChangedCode(String input);
    }

    interface ModelCallBack {

        void sendSubmitOrderRequest(Context context, Response.Listener responseListener, Response.ErrorListener errorListener
                , String subCategoryId, List<List<String>> answersList, String selectedDate, String selectedHour
                , String favoriteLocationId, String districtId);

        void setApiTokenSharedPref(String apiToken, Context context);

        String getApiTokenSharedPref(Context context);

        String getNameSharedPref(Context context);

        String getFamilySharedPref(Context context);

        String getAvatarSharedPref(Context context);

        String getCreditSharedPref(Context context);

        void sendGetUserFavoriteLocationsRequest(Context context, String requestBody, Response.Listener responseListener, Response.ErrorListener errorListener);

        void sendGetZonesRequest(Context context, String requestBody, Response.Listener responseListener, Response.ErrorListener errorListener);

        void sendGetDistrictsRequest(Context context, String requestBody, Response.Listener responseListener, Response.ErrorListener errorListener);


    }

    interface PresenterCallBack {

        void setReceivedMetaAndAnswers(List<List<String>> answersList, String subCategoryId,
                                       String selectedDate, String selectedHour);

        void initPageLists(Context context);

        void favoriteLocationItemSelected(FavoriteLocation selectedFavoriteLocation);

        void cancelFavoriteButtonClicked();

        void selectZoneButtonClicked(Context context);

        void zoneItemSelected(Zone selectedZone, Context context);

        void getDistrictButtonClicked(Context context);

        void districtItemSelected(District selectedDistrict);

        void discountCodeButtonClicked();

        void submitDiscountCodeClicked(String input);

        void submitButtonClicked(Context context);
    }

}
