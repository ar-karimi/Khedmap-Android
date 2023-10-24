package com.khedmap.khedmap.Order.Presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.error.NoConnectionError;
import com.android.volley.error.TimeoutError;
import com.android.volley.error.VolleyError;
import com.khedmap.khedmap.LoginSignUp.View.EnterMobileActivity;
import com.khedmap.khedmap.Order.DataModels.District;
import com.khedmap.khedmap.Order.DataModels.Zone;
import com.khedmap.khedmap.Order.EditFavoriteLocationContract;
import com.khedmap.khedmap.Order.Model.EditFavoriteLocationModel;
import com.khedmap.khedmap.Order.View.EditFavoriteLocationActivity;
import com.khedmap.khedmap.Order.View.MapsActivity;
import com.khedmap.khedmap.Utilities.UtilitiesSingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class EditFavoriteLocationPresenter implements EditFavoriteLocationContract.PresenterCallBack {

    private EditFavoriteLocationContract.ViewCallBack view;
    private EditFavoriteLocationModel model;

    private boolean responseReceived = true;

    private String favoriteLocationId;

    private List<Zone> zoneList = new ArrayList<>();
    private List<District> districtList = new ArrayList<>();

    //to send to server
    private int selectedCity;
    private String selectedDistrictId;
    private String selectedLatitude;
    private String selectedLongitude;

    private boolean isEnableDistrictButton = true;
    private boolean isEnableSubmitButton = true;


    public EditFavoriteLocationPresenter(EditFavoriteLocationContract.ViewCallBack view, EditFavoriteLocationModel model) {
        this.model = model;
        this.view = view;
    }


    private void setResponseReceived(String loadingType) {

        responseReceived = true;

        switch (loadingType) {
            case "main":
                view.showLoadingView(false);
                break;
            case "districtButton":
                view.showDistrictButtonProgressBar(false);
                break;
        }

    }


    @Override
    public void setFavoriteLocationId(String favoriteLocationId) {

        this.favoriteLocationId = favoriteLocationId;
    }

    @Override
    public void initPageItems(Context context) {

        if (!responseReceived)  //for Block requests till Response not received
            return;

        if (!UtilitiesSingleton.getInstance().isNetworkAvailable(context)) {
            view.showSnackBar("اتصال اینترنت را بررسی کنید");
            return;
        }

        view.showLoadingView(true);
        responseReceived = false;

        //to get FavoriteLocationDetail
        sendFavoriteLocationDetailRequest(context);
    }


    ///////////////////////////////////////
//to get FavoriteLocationDetail
    private void sendFavoriteLocationDetailRequest(final Context context) {


        //to buildRequestBody
        String requestBody = buildFavoriteLocationDetailRequestBody(context);

        if (requestBody == null)
            return;

        Response.Listener responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                favoriteLocationDetailFetchData(response);

                setResponseReceived("main");

            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                ///////Error Handling

                ErrorHandling(error, context);

                setResponseReceived("main");


            }
        };

        model.sendFavoriteLocationDetailRequest(context, requestBody, responseListener, errorListener);

    }


    private String buildFavoriteLocationDetailRequestBody(Context context) {

        try {

            //to build detail parameter
            JSONObject detail = new JSONObject();
            detail.put("identification", favoriteLocationId);

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

    private void favoriteLocationDetailFetchData(String response) {


        try {
            JSONObject jsonobject = new JSONObject(response);

            //to get status
            String status = jsonobject.getString("status");

            if (status.equals("success")) {

                JSONObject data = jsonobject.getJSONObject("data");


                //to get zoneList
                JSONArray zones = data.getJSONObject("zones").getJSONArray("data");

                List<Zone> zoneList = new ArrayList<>();

                for (int i = 0; i < zones.length(); i++) {

                    JSONObject zoneJSONObject = zones.getJSONObject(i);

                    Zone zone = new Zone();
                    zone.setTitle(zoneJSONObject.getString("title"));
                    zone.setServerId(zoneJSONObject.getString("id"));


                    zoneList.add(zone);

                }

                this.zoneList = zoneList; //save to show in dialog
                //till here


                //to get districtList
                JSONArray districts = data.getJSONArray("districts");

                List<District> districtList = new ArrayList<>();

                for (int i = 0; i < districts.length(); i++) {

                    JSONObject districtJSONObject = districts.getJSONObject(i);

                    District district = new District();
                    district.setDistrictId(districtJSONObject.getString("district_id"));
                    district.setName(districtJSONObject.getString("name"));


                    districtList.add(district);

                }

                this.districtList = districtList; //save to show in dialog
                //till here


                //to get selectedItems
                JSONObject info = data.getJSONObject("info");

                selectedCity = info.getInt("city");
                selectedDistrictId = info.getString("district_id");
                selectedLatitude = info.getString("latitude");
                selectedLongitude = info.getString("longitude");

                view.setPageValues("منطقه " + info.getString("zone")
                        , info.getString("district"), info.getString("name")
                        , info.getString("detail_address"));
                //till here


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


    private void ErrorHandling(VolleyError error, Context context) {


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
        }
    }

//till here
///////////////////////////////////////


    @Override
    public void selectZoneButtonClicked(Context context) {

        if (!responseReceived)   //for Block requests till Response not received
            return;

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

                setResponseReceived("districtButton");

                getDistrictsFetchData(response);

            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                ///////Error Handling

                ErrorHandling(error, context);

                setResponseReceived("districtButton");


            }
        };

        model.sendGetDistrictsRequest(context, requestBody, responseListener, errorListener);

    }


    private String buildGetDistrictsRequestBody(String selectedZoneId, Context context) {

        try {

            //to build detail parameter
            JSONObject detail = new JSONObject();
            detail.put("city_id", selectedCity);
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

        //to set Title of DistrictButton
        view.setDistrictButtonTitle(selectedDistrict.getName());

        //to enable Submit Button
        isEnableSubmitButton = true;

    }


    @Override
    public void mapButtonClicked(Activity activity) {

        activity.startActivityForResult(new Intent(activity, MapsActivity.class)
                        .putExtra("prevLatitude", selectedLatitude)
                        .putExtra("prevLongitude", selectedLongitude)
                , EditFavoriteLocationActivity.REQUEST_CODE_MAP);
    }

    @Override
    public void setNewLatLng(String latitude, String longitude) {

        selectedLatitude = latitude;
        selectedLongitude = longitude;
    }

    @Override
    public void submitButtonClicked(Activity activity, String name, String address) {

        if (!responseReceived)
            return;


        if (!isEnableSubmitButton) {
            view.showSnackBar("لطفاً محله خود را مشخص کنید");
            return;
        }

        if (name.isEmpty()) {
            view.showSnackBar("لطفاً نام را مشخص کنید");
            return;
        }

        if (address.length() < 10) {
            view.showSnackBar("آدرس باید حداقل 10 حرف باشد");
            return;
        }


        if (!UtilitiesSingleton.getInstance().isNetworkAvailable(activity)) {
            view.showSnackBar("اتصال اینترنت را بررسی کنید");
            return;
        }


        view.showLoadingView(true);
        responseReceived = false;

        //to send EditFavoriteLocation Request
        sendEditFavoriteLocationRequest(activity, name, address);
    }


    ///////////////////////////////////////
//to send EditFavoriteLocation Request
    private void sendEditFavoriteLocationRequest(final Activity activity, String name, String address) {


        //to buildRequestBody
        String requestBody = buildEditFavoriteLocationRequestBody(activity, name, address);

        if (requestBody == null)
            return;

        Response.Listener responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {


                if (editFavoriteLocationFetchData(response)) {
                    view.showToast("با موفقیت انجام شد"); //no need to set response received
                    activity.finish();
                } else
                    setResponseReceived("main");
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                ///////Error Handling

                ErrorHandling(error, activity);

                setResponseReceived("main");


            }
        };


        model.sendEditFavoriteLocationRequest(activity, requestBody, responseListener, errorListener);

    }


    private String buildEditFavoriteLocationRequestBody(Context context, String name, String address) {


        try {

            //to build detail parameter
            JSONObject detail = new JSONObject();
            detail.put("favoriteLocation", favoriteLocationId);
            detail.put("name", name);
            detail.put("detailAddress", address);
            detail.put("district", selectedDistrictId);
            detail.put("lat", selectedLatitude);
            detail.put("lng", selectedLongitude);

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


    private boolean editFavoriteLocationFetchData(String response) {


        try {
            JSONObject jsonobject = new JSONObject(response);

            //to get status
            String status = jsonobject.getString("status");

            if (status.equals("success")) {

                return true;

            } else {
                //to get message
                String message = jsonobject.getString("message");
                view.showSnackBar(message);
            }

        } catch (JSONException e) {
            Log.e("ParsError", e.getMessage());
            view.showSnackBar("خطای شبکه");
        }

        return false;
    }
//till here
///////////////////////////////////////

}
