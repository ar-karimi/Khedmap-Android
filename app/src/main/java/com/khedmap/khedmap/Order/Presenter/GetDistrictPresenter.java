package com.khedmap.khedmap.Order.Presenter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.error.NoConnectionError;
import com.android.volley.error.TimeoutError;
import com.android.volley.error.VolleyError;
import com.crashlytics.android.Crashlytics;
import com.khedmap.khedmap.LoginSignUp.View.EnterMobileActivity;
import com.khedmap.khedmap.Order.DataModels.District;
import com.khedmap.khedmap.Order.DataModels.FavoriteLocation;
import com.khedmap.khedmap.Order.DataModels.Zone;
import com.khedmap.khedmap.Order.Model.GetDistrictModel;
import com.khedmap.khedmap.Order.GetDistrictContract;
import com.khedmap.khedmap.Order.View.WaitingForSuggestionsActivity;
import com.khedmap.khedmap.Utilities.UtilitiesSingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class GetDistrictPresenter implements GetDistrictContract.PresenterCallBack {

    private GetDistrictContract.ViewCallBack view;
    private GetDistrictModel model;

    private List<List<String>> answersList;
    private String subCategoryId;
    private String selectedDate;
    private String selectedHour;

    private boolean responseReceived = true;

    private List<FavoriteLocation> favoriteLocationList = new ArrayList<>();
    private List<Zone> zoneList = new ArrayList<>();
    private List<District> districtList = new ArrayList<>();

    //to send to server
    private String selectedFavoriteLocationId;
    private String selectedDistrictId;
    //till here

    private boolean isEnableZoneButton = true;
    private boolean isEnableDistrictButton = false;
    private boolean isEnableSubmitButton = false;

    private String discountCode = "";


    public GetDistrictPresenter(GetDistrictContract.ViewCallBack view, GetDistrictModel model) {
        this.model = model;
        this.view = view;
    }


    @Override
    public void setReceivedMetaAndAnswers(List<List<String>> answersList, String subCategoryId,
                                          String selectedDate, String selectedHour) {

        this.answersList = answersList;
        this.subCategoryId = subCategoryId;
        this.selectedDate = selectedDate;
        this.selectedHour = selectedHour;
    }

    private void setResponseReceived(String loadingType) {

        responseReceived = true;

        switch (loadingType) {
            case "initPage":
                view.showLoadingView(false);
                break;
            case "getDistricts":
                view.showDistrictButtonProgressBar(false);
                break;
            case "submitOrder":
                view.showSubmitOrderLoadingView(false);
                break;
        }

    }


    @Override
    public void initPageLists(Context context) { //Calls One Time in onCreate


        view.showLoadingView(true);
        responseReceived = false;

        //to send GetUserFavoriteLocations Request
        sendGetUserFavoriteLocationsRequest(context);

        //getZonesRequest Called in GetUserFavoriteLocations onResponse

    }


    ///////////////////////////////////////
//to Get UserFavoriteLocations
    private void sendGetUserFavoriteLocationsRequest(final Context context) {


        //to buildRequestBody
        String requestBody = buildGetUserFavoriteLocationsRequestBody(context);

        if (requestBody == null)
            return;

        Response.Listener responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                List<FavoriteLocation> favoriteLocationsList = getUserFavoriteLocationsFetchData(response);

                if (favoriteLocationsList != null) {

                    if (favoriteLocationsList.isEmpty())
                        view.setNoItemTextVisible();
                    else
                        view.showFavoriteLocationsRecyclerView(favoriteLocationsList);
                }

                //send GetZonesRequest to show in dialog
                sendGetZonesRequest(context);

                //setResponseReceived("initPage"); //Call in getZonesRequest onResponse

            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                ///////Error Handling

                ErrorHandling(error, context, false);

                setResponseReceived("initPage");


            }
        };

        model.sendGetUserFavoriteLocationsRequest(context, requestBody, responseListener, errorListener);

    }


    private String buildGetUserFavoriteLocationsRequestBody(Context context) {

        try {

            //to build jsonBody
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("api_token", model.getApiTokenSharedPref(context));

            return jsonBody.toString();


        } catch (JSONException e) {
            e.printStackTrace();

            return null;
        }

    }

    private List<FavoriteLocation> getUserFavoriteLocationsFetchData(String response) {


        try {
            JSONObject jsonobject = new JSONObject(response);

            //to get status
            String status = jsonobject.getString("status");

            if (status.equals("success")) {

                JSONArray data = jsonobject.getJSONArray("data");


                List<FavoriteLocation> favoriteLocationsList = new ArrayList<>();

                for (int i = 0; i < data.length(); i++) {

                    JSONObject favoriteLocationJSONObject = data.getJSONObject(i);

                    FavoriteLocation favoriteLocation = new FavoriteLocation();
                    favoriteLocation.setName(favoriteLocationJSONObject.getString("name"));
                    favoriteLocation.setDistrict(favoriteLocationJSONObject.getString("district"));
                    favoriteLocation.setIdentification(favoriteLocationJSONObject.getString("identification"));

                    favoriteLocationsList.add(favoriteLocation);

                }

                this.favoriteLocationList = favoriteLocationsList;
                return favoriteLocationsList;


            } else {
                //to get message
                String message = jsonobject.getString("message");
                view.showSnackBar(message);
            }

        } catch (JSONException e) {
            Log.e("ParsError", e.getMessage());
            view.showSnackBar("خطای شبکه");
        }

        return null;
    }


    private void ErrorHandling(VolleyError error, Context context, boolean isErrorInSubmitOrderRequest) {


        NetworkResponse networkResponse = error.networkResponse;
        String errorMessage = "Unknown error";
        if (networkResponse == null) {
            if (error.getClass().equals(TimeoutError.class)) {
                errorMessage = "Request timeout";
            } else if (error.getClass().equals(NoConnectionError.class)) {
                errorMessage = "Failed to connect server";
            }
        } else {

            if (networkResponse.statusCode == 404) {
                errorMessage = "Resource not found";
            } else if (networkResponse.statusCode == 401) {
                errorMessage = "Please login again";
            } else if (networkResponse.statusCode == 400) {
                errorMessage = "Check your inputs";
            } else if (networkResponse.statusCode == 500) {
                errorMessage = "Something is getting wrong";
            }
        }

        Log.e("ResponseError", errorMessage);
        error.printStackTrace();


        if (errorMessage.equals("Please login again")) {
            //clear apiToken from sharedPref
            model.setApiTokenSharedPref("", context);


            Intent intent = new Intent(context, EnterMobileActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);

        } else {
            view.showSnackBar("خطای شبکه");

            if (isErrorInSubmitOrderRequest){ //Report only SubmitOrder error

                //to send RequestError to fireBase
                Crashlytics.logException(error.getCause()); //this line needed to send log to fireBase
                Crashlytics.setString("request-error-in-submit-order-request", error.getMessage());
            }

        }
    }

//till here
///////////////////////////////////////


    ///////////////////////////////////////
//to Get Zones
    private void sendGetZonesRequest(final Context context) {


        //to buildRequestBody
        String requestBody = buildGetZonesRequestBody(context);

        if (requestBody == null)
            return;

        Response.Listener responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                getZonesFetchData(response);

                setResponseReceived("initPage");

            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                ///////Error Handling

                ErrorHandling(error, context, false);

                setResponseReceived("initPage");


            }
        };

        model.sendGetZonesRequest(context, requestBody, responseListener, errorListener);

    }


    private String buildGetZonesRequestBody(Context context) {

        try {

            //to build detail parameter
            JSONObject detail = new JSONObject();
            detail.put("city_id", 1);

            //to build jsonBody
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("Detail", detail);
            //get apiToken from Shared Preferences
            jsonBody.put("api_token", model.getApiTokenSharedPref(context));

            return jsonBody.toString();


        } catch (JSONException e) {
            e.printStackTrace();

            return null;
        }

    }

    private void getZonesFetchData(String response) {


        try {
            JSONObject jsonobject = new JSONObject(response);

            //to get status
            String status = jsonobject.getString("status");

            if (status.equals("success")) {

                JSONArray data = jsonobject.getJSONArray("data");


                List<Zone> zoneList = new ArrayList<>();

                for (int i = 0; i < data.length(); i++) {

                    JSONObject zoneJSONObject = data.getJSONObject(i);

                    Zone zone = new Zone();
                    zone.setTitle(zoneJSONObject.getString("title"));
                    zone.setServerId(zoneJSONObject.getString("id"));


                    zoneList.add(zone);

                }

                this.zoneList = zoneList; //save to show in dialog

            } else {
                //to get message
                String message = jsonobject.getString("message");
                view.showSnackBar(message);
            }

        } catch (JSONException e) {
            Log.e("ParsError", e.getMessage());
            view.showSnackBar("خطای شبکه");
        }

    }

//till here
///////////////////////////////////////


    @Override
    public void favoriteLocationItemSelected(FavoriteLocation selectedFavoriteLocation) {


        if (!responseReceived)   //for Block requests till Response not received
            return;

        //to save info of District to send to server
        selectedFavoriteLocationId = selectedFavoriteLocation.getIdentification();
        selectedDistrictId = null;

        //to enable cancelFavorite Button
        view.showCancelFavoriteButton(true);

        //to disable Zone and District Button
        isEnableZoneButton = false;
        view.enableZoneButton(false);
        view.setZoneButtonTitle("انتخاب منطقه");
        isEnableDistrictButton = false;
        view.enableDistrictButton(false);
        view.setDistrictButtonTitle("انتخاب محله");

        //to enable Submit Button
        isEnableSubmitButton = true;
        view.enableSubmitButton(true);

    }


    @Override
    public void cancelFavoriteButtonClicked() {

        view.showFavoriteLocationsRecyclerView(favoriteLocationList);

        //to enable Zone Button
        isEnableZoneButton = true;
        view.enableZoneButton(true);

        //to disable Submit Button
        isEnableSubmitButton = false;
        view.enableSubmitButton(false);

        view.showCancelFavoriteButton(false);

    }


    @Override
    public void selectZoneButtonClicked(Context context) {

        if (!responseReceived)   //for Block requests till Response not received
            return;

        if (!isEnableZoneButton) {
            view.showSnackBar("انتخاب از مکان\u200Cهای مورد علاقه را لغو کنید");
            return;
        }

        if (!UtilitiesSingleton.getInstance().isNetworkAvailable(context)) {
            view.showSnackBar("اتصال اینترنت را بررسی کنید");
            return;
        }

        view.showZonesDialog(zoneList);
    }

    @Override
    public void zoneItemSelected(Zone selectedZone, Context context) {

        if (!UtilitiesSingleton.getInstance().isNetworkAvailable(context)) {
            view.showSnackBar("اتصال اینترنت را بررسی کنید");
            return;
        }

        //to set Title of ZoneButton
        view.setZoneButtonTitle(selectedZone.getTitle());

        //to disable District and submit Button if enabled in past
        isEnableDistrictButton = false;
        view.enableDistrictButton(false);
        view.setDistrictButtonTitle("انتخاب محله");
        isEnableSubmitButton = false;
        view.enableSubmitButton(false);

        //to show loading in DistrictButton
        view.showDistrictButtonProgressBar(true);
        responseReceived = false;

        //to send Districts Request
        sendGetDistrictsRequest(selectedZone.getServerId(), context);

    }


    ///////////////////////////////////////
//to Get Districts
    private void sendGetDistrictsRequest(String selectedZoneId, final Context context) {


        //to buildRequestBody
        String requestBody = buildGetDistrictsRequestBody(selectedZoneId, context);

        if (requestBody == null)
            return;

        Response.Listener responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                setResponseReceived("getDistricts");

                getDistrictsFetchData(response);

            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                ///////Error Handling

                ErrorHandling(error, context, false);

                setResponseReceived("getDistricts");


            }
        };

        model.sendGetDistrictsRequest(context, requestBody, responseListener, errorListener);

    }


    private String buildGetDistrictsRequestBody(String selectedZoneId, Context context) {

        try {

            //to build detail parameter
            JSONObject detail = new JSONObject();
            detail.put("city_id", 1);
            detail.put("zone", Integer.parseInt(selectedZoneId));

            //to build jsonBody
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("Detail", detail);
            //get apiToken from Shared Preferences
            jsonBody.put("api_token", model.getApiTokenSharedPref(context));

            return jsonBody.toString();


        } catch (JSONException e) {
            e.printStackTrace();

            return null;
        }

    }

    private void getDistrictsFetchData(String response) {


        try {
            JSONObject jsonobject = new JSONObject(response);

            //to get status
            String status = jsonobject.getString("status");

            if (status.equals("success")) {

                JSONArray data = jsonobject.getJSONArray("data");


                List<District> districtList = new ArrayList<>();

                for (int i = 0; i < data.length(); i++) {

                    JSONObject districtJSONObject = data.getJSONObject(i);

                    District district = new District();
                    district.setDistrictId(districtJSONObject.getString("district_id"));
                    district.setName(districtJSONObject.getString("name"));


                    districtList.add(district);

                }

                this.districtList = districtList; //save to show in dialog


                //to enable District Button
                isEnableDistrictButton = true;
                view.enableDistrictButton(true);


            } else {
                //to get message
                String message = jsonobject.getString("message");
                view.showSnackBar(message);
            }

        } catch (JSONException e) {
            Log.e("ParsError", e.getMessage());
            view.showSnackBar("خطای شبکه");
        }

    }

//till here
///////////////////////////////////////


    @Override
    public void getDistrictButtonClicked(Context context) {

        if (!responseReceived)   //for Block requests till Response not received
            return;

        if (!isEnableDistrictButton) {
            view.showSnackBar("لطفاً ابتدا منطقه خود را مشخص کنید");
            return;
        }

        view.showDistrictsDialog(districtList);
    }

    @Override
    public void districtItemSelected(District selectedDistrict) {

        //to save info of District to send to server
        selectedDistrictId = selectedDistrict.getDistrictId();
        selectedFavoriteLocationId = null;

        //to set Title of DistrictButton
        view.setDistrictButtonTitle(selectedDistrict.getName());

        //to enable Submit Button
        isEnableSubmitButton = true;
        view.enableSubmitButton(true);

    }


 //to save discount Code
    @Override
    public void discountCodeButtonClicked() {

        if (!responseReceived)
            return;

        view.showDiscountCodeDialog(discountCode);
    }

    @Override
    public void submitDiscountCodeClicked(String input) {

        discountCode = input;

        view.setChangedCode(input);
    }
//till here

        @Override
    public void submitButtonClicked(Context context) {

        if (!isEnableSubmitButton)
            return;

        if (!responseReceived)
            return;

        if (!UtilitiesSingleton.getInstance().isNetworkAvailable(context)) {
            view.showSnackBar("اتصال اینترنت را بررسی کنید");
            return;
        }

        view.showSubmitOrderLoadingView(true);
        responseReceived = false;

        //to Submit Order to server
        sendSubmitOrderRequest(context);
    }


///////////////////////////////////////
//to submit order and get Tracking Code

    private void sendSubmitOrderRequest(final Context context) {


        Response.Listener responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                //setResponseReceived("submitOrder"); //don't need to setResponseReceived if Successful because activity finished


                List<String> submittedOrderInfo = SubmitOrderFetchData(response);

                if (submittedOrderInfo != null) {
                    Intent intent = new Intent(context, WaitingForSuggestionsActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("trackingCode", submittedOrderInfo.get(0));
                    intent.putExtra("submittedOrderId", submittedOrderInfo.get(1));
                    context.startActivity(intent);
                }
                else
                    setResponseReceived("submitOrder");

            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                ///////Error Handling

                ErrorHandling(error, context, true);

                setResponseReceived("submitOrder");


            }
        };


        model.sendSubmitOrderRequest(context, responseListener, errorListener, subCategoryId, answersList,
                selectedDate, selectedHour, selectedFavoriteLocationId, selectedDistrictId);

    }

    private List<String> SubmitOrderFetchData(String response) {


        try {
            JSONObject jsonobject = new JSONObject(response);

            //to get status
            String status = jsonobject.getString("status");

            if (status.equals("success")) {

                JSONObject data = jsonobject.getJSONObject("data");

                List<String> submittedOrderInfo = new ArrayList<>();
                submittedOrderInfo.add(data.getString("order_tracking"));
                submittedOrderInfo.add(data.getString("identification"));

                return submittedOrderInfo;

            } else {
                //to get message
                String message = jsonobject.getString("message");
                view.showSnackBar(message);
            }

        } catch (JSONException e) {
            Log.e("ParsError", e.getMessage());
            view.showSnackBar("خطای شبکه");

            //to send RequestError to fireBase
            Crashlytics.logException(e.getCause()); //this line needed to send log to fireBase
            Crashlytics.setString("server-error-in-submit-order-request"
                    , e.getMessage() + "    And request response is: " + response);
        }

        return null;
    }

//till here
///////////////////////////////////////

}
