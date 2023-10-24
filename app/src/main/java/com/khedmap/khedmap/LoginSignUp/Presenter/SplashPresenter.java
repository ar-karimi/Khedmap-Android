package com.khedmap.khedmap.LoginSignUp.Presenter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import android.util.Log;

import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.error.NoConnectionError;
import com.android.volley.error.TimeoutError;
import com.android.volley.error.VolleyError;
import com.crashlytics.android.Crashlytics;
import com.khedmap.khedmap.LoginSignUp.Model.SplashModel;
import com.khedmap.khedmap.LoginSignUp.SplashContract;
import com.khedmap.khedmap.LoginSignUp.View.EnterMobileActivity;
import com.khedmap.khedmap.LoginSignUp.View.IntroActivity;
import com.khedmap.khedmap.LoginSignUp.View.TryToConnectActivity;
import com.khedmap.khedmap.LoginSignUp.View.UpdateRequiredActivity;
import com.khedmap.khedmap.Order.View.HomeActivity;
import com.khedmap.khedmap.R;
import com.khedmap.khedmap.Utilities.UtilitiesSingleton;
import com.shashank.sony.fancygifdialoglib.FancyGifDialog;
import com.shashank.sony.fancygifdialoglib.FancyGifDialogListener;

import org.json.JSONException;
import org.json.JSONObject;

public class SplashPresenter implements SplashContract.PresenterCallBack {

    private SplashContract.ViewCallBack view;
    private SplashModel model;

    private boolean isDisconnected = false;

    public SplashPresenter(SplashContract.ViewCallBack view, SplashModel model) {
        this.model = model;
        this.view = view;
    }


    @Override
    public void checkInternetConnection(Activity activity) {

        if (!UtilitiesSingleton.getInstance().isNetworkAvailable(activity)) {
            Intent intent = new Intent(activity, TryToConnectActivity.class);
            activity.startActivity(intent);
            activity.finish();

            isDisconnected = true;
        }
    }


    ///////////////////////////////////////////
//to validation token and uuid
    @Override
    public void sendValidateRequest(final Activity activity) {

        if (isDisconnected)
            return;

        String requestBody = buildValidateRequestBody(activity);

        if (requestBody == null)
            return;

        Response.Listener responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {


                if (validateFetchData(response) != null) {
                    if (validateFetchData(response).equals("isValid")) {
                        Intent intent = new Intent(activity, HomeActivity.class);
                        activity.startActivity(intent);
                        activity.finish();
                    } else {
                        Intent intent = new Intent(activity, IntroActivity.class);
                        activity.startActivity(intent);
                        activity.finish();
                    }
                }

            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                ///////Error Handling

                ErrorHandling(error, activity);
            }
        };


        model.sendValidateRequest(activity, requestBody, responseListener, errorListener);

    }

    private String buildValidateRequestBody(Activity activity) {

        //to get Android ID
        @SuppressLint("HardwareIds")
        String androidId = Settings.Secure.getString(activity.getContentResolver(),
                Settings.Secure.ANDROID_ID);


        try {

            //to build detail parameter
            JSONObject detail = new JSONObject();

            detail.put("uuid", androidId);

            //to build jsonBody
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("Detail", detail);
            //get apiToken from sharedPref
            jsonBody.put("api_token", model.getApiTokenSharedPref(activity));


            return jsonBody.toString();

        } catch (JSONException e) {
            e.printStackTrace();

            return null;
        }


    }


    private String validateFetchData(String response) {


        try {
            JSONObject jsonobject = new JSONObject(response);

            //to get status
            String status = jsonobject.getString("status");

            if (status.equals("success")) {

                JSONObject data = jsonobject.getJSONObject("data");
                Boolean isValid = data.getBoolean("valid");

                if (isValid)
                    return "isValid";
                else
                    return "notValid";

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


    //till here
///////////////////////////////////////////


    ///////////////////////////////////////////
//to check available update
    @Override
    public void sendUpdateRequest(final Activity activity) {


        if (isDisconnected)
            return;

        //to get App Version
        int appVersion = 0;
        try {
            appVersion = activity.getPackageManager().getPackageInfo(activity.getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        final int finalAppVersion = appVersion;


        Response.Listener responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {


                String[] receivedVersion = checkVersionFetchData(response, activity);
                if (receivedVersion == null)
                    return;

                String lastVersion = receivedVersion[0];
                String stableVersion = receivedVersion[1];


                if (finalAppVersion < Integer.parseInt(lastVersion)) {
                    if (finalAppVersion < Integer.parseInt(stableVersion)) {
                        Intent intent = new Intent(activity, UpdateRequiredActivity.class);
                        activity.startActivity(intent);
                        activity.finish();
                    } else {


                        //to show update Dialog
                        showUpdateDialog(activity);

                    }
                } else {
                    if (model.getApiTokenSharedPref(activity).equals("")) {
                        Intent intent = new Intent(activity, IntroActivity.class);
                        activity.startActivity(intent);
                        activity.finish();

                    } else model.addToQueueCheckValidTokenRequest();
                }
            }


        };


        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                ///////Error Handling

                ErrorHandling(error, activity);
            }
        };


        model.sendUpdateRequest(responseListener, errorListener);

    }


    private String[] checkVersionFetchData(String response, Context context) {

        try {
            JSONObject jsonobject = new JSONObject(response);

            //to get status
            String status = jsonobject.getString("status");

            if (status.equals("success")) {
                //to get available version from server
                JSONObject data = jsonobject.getJSONObject("data");

                //get Dynamic Links from Server
                model.setSupportPhoneSharedPref(data.getString("supportPhone"), context);
                model.setTermsSharedPref(data.getString("terms"), context);


                String lastVersion = data.getString("lastVersion");
                //to remove .0.0 and get int version
                int index;
                for (index = 0; lastVersion.charAt(index) != '.'; index++) ;
                lastVersion = lastVersion.substring(0, index);

                String stableVersion = data.getString("stableVersion");
                for (index = 0; stableVersion.charAt(index) != '.'; index++) ;
                stableVersion = stableVersion.substring(0, index);


                return new String[]{lastVersion, stableVersion};


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


    private void showUpdateDialog(final Activity activity) {

        new FancyGifDialog.Builder(activity)
                .setTitle("نسخه جدید رسید")
                .setMessage("لطفاً آخرین نسخه را نصب نمایید")
                .setNegativeBtnText("بعدا")
                .setPositiveBtnBackground("#28b4f1")
                .setPositiveBtnText("به روزرسانی")
                .setNegativeBtnBackground("#FFA9A7A8")
                .setGifResource(R.drawable.update)   //Pass your Gif here
                .isCancellable(false)
                .OnPositiveClicked(new FancyGifDialogListener() {
                    @Override
                    public void OnClick() {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://cafebazaar.ir/"));
                        activity.startActivity(browserIntent);
                        activity.finish();
                    }
                })
                .OnNegativeClicked(new FancyGifDialogListener() {
                    @Override
                    public void OnClick() {
                        //dismissed
                        if (model.getApiTokenSharedPref(activity).equals("")) {
                            Intent intent = new Intent(activity, IntroActivity.class);
                            activity.startActivity(intent);
                            activity.finish();

                        } else model.addToQueueCheckValidTokenRequest();
                    }
                })
                .build();
    }

//till here
///////////////////////////////////////////


    private void ErrorHandling(VolleyError error, Activity activity) {

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
            model.setApiTokenSharedPref("", activity);


            Intent intent = new Intent(activity, EnterMobileActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            activity.startActivity(intent);

        } else {
            //to send RequestError to fireBase
            Crashlytics.logException(error.getCause()); //this line needed to send log to fireBase
            Crashlytics.setString("request-error-in-splash", error.getMessage());

            Intent intent = new Intent(activity, TryToConnectActivity.class);
            activity.startActivity(intent);
            activity.finish();
        }

    }


}
