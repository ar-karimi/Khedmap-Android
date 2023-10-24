package com.khedmap.khedmap.LoginSignUp.Presenter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.provider.Settings;
import android.util.Log;

import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.error.NoConnectionError;
import com.android.volley.error.TimeoutError;
import com.android.volley.error.VolleyError;
import com.khedmap.khedmap.LoginSignUp.Model.ValidateMobileModel;
import com.khedmap.khedmap.LoginSignUp.ValidateMobileContract;
import com.khedmap.khedmap.LoginSignUp.View.EnterMobileActivity;
import com.khedmap.khedmap.LoginSignUp.View.NameRegisterActivity;
import com.khedmap.khedmap.Order.View.HomeActivity;
import com.khedmap.khedmap.Utilities.UtilitiesSingleton;
import com.pusher.pushnotifications.PushNotifications;

import org.json.JSONException;
import org.json.JSONObject;

import cn.iwgang.countdownview.CountdownView;

public class ValidateMobilePresenter implements ValidateMobileContract.PresenterCallBack {

    private ValidateMobileContract.ViewCallBack view;
    private ValidateMobileModel model;

    private CountdownView countDownView;
    private boolean responseReceived = true;
    private boolean countDownEnded = false;
    private String[] enterMobileInfo;

    public ValidateMobilePresenter(ValidateMobileContract.ViewCallBack view, ValidateMobileModel model) {
        this.model = model;
        this.view = view;
    }


    @Override
    public void changePhoneButtonClicked(Activity activity) {

        Intent intent = new Intent(activity, EnterMobileActivity.class);
        activity.startActivity(intent);
        activity.finish();

    }


    @Override
    public void setEnterMobileInfo(String[] enterMobileInfo) {

        this.enterMobileInfo = enterMobileInfo;
    }


    @Override
    public void textChanged(String number) {

        if (number.length() == 6)
            submitButtonClicked(view.getActivity(), number);


    }


    ///////////////////////////////////////////
//to Request Resend verify code
    @Override
    public void resendSmsButtonClicked(Activity activity) {

        if (!responseReceived || !countDownEnded)   //for Block requests till Response not received
            return;


        if (!UtilitiesSingleton.getInstance().isNetworkAvailable(activity)) {
            view.showSnackBar("اتصال اینترنت را بررسی کنید");
            return;
        }


        view.showProgressBar(true);
        responseReceived = false;

        resendSmsRequest(activity, enterMobileInfo[1]);


    }


    @Override
    public void setupCountDownView(CountdownView countDownView) {

        this.countDownView = countDownView;
        countDownView.start(2 * 60 * 1000); // Millisecond

        countDownView.setOnCountdownEndListener(new CountdownView.OnCountdownEndListener() {
            @Override
            public void onEnd(CountdownView cv) {
                view.enableResendSmsButton();
                countDownEnded = true;
            }
        });
    }


    private void resendSmsRequest(final Activity activity, final String mobileNumber) {


        Response.Listener responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {


                countDownEnded = false;
                view.showSnackBar("کد تأیید مجدداً ارسال شد");
                view.disableResendSmsButton();
                countDownView.start(2 * 60 * 1000); // Millisecond

                responseReceived = true;
                view.showProgressBar(false);


                String apiToken = resendSmsFetchData(response);

                if (apiToken != null) {
                    //replace apiToken in enterMobileInfo
                    enterMobileInfo[0] = apiToken;
                }


                setResponseReceived();
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                ///////Error Handling

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

                //till here

                ErrorHandling(errorMessage, activity);

                setResponseReceived();


            }
        };


        model.resendSmsRequest(activity, mobileNumber, responseListener, errorListener);

    }


    private String resendSmsFetchData(String response) {

        // response
        try {
            JSONObject jsonobject = new JSONObject(response);

            //to get status
            String status = jsonobject.getString("status");

            if (status.equals("success")) {
                //to get Api Token
                JSONObject data = jsonobject.getJSONObject("data");

                //return new apiToken
                return data.getString("api_token");

            } else {
                //to get message
                String message = jsonobject.getString("message");
                view.showSnackBar(message);
            }

        } catch (JSONException e) {
            e.printStackTrace();
            view.showSnackBar("خطای شبکه");
        }

        return null;
    }


    private void ErrorHandling(String errorMessage, Activity activity) {

        if (errorMessage.equals("Please login again")) {
            //clear apiToken from sharedPref
            model.setApiTokenSharedPref("", activity);


            Intent intent = new Intent(activity, EnterMobileActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            activity.startActivity(intent);

        } else {
            view.showSnackBar("خطای شبکه");
        }
    }


//till here
///////////////////////////////////////////


    private void setResponseReceived() {
        responseReceived = true;
        view.showProgressBar(false);
    }


    ///////////////////////////////////////////
//to send verify code to server and get registered or not status
    @Override
    public void submitButtonClicked(Activity activity, String validateCode) {

        if (!responseReceived)   //for Block requests till Response not received
            return;

        //to close keyboard by click on submitButton
        view.closeKeyboard();

        if (validateCode.isEmpty()) {
            view.showSnackBar("لطفا کد دریافتی را وارد کنید");
            return;
        }

        if (!UtilitiesSingleton.getInstance().isNetworkAvailable(activity)) {
            view.showSnackBar("اتصال اینترنت را بررسی کنید");
            return;
        }


        view.showProgressBar(true);
        responseReceived = false;

        sendSubmitRequest(activity, validateCode);
    }


    private void sendSubmitRequest(final Activity activity, String validateCode) {


        //to buildRequestBody
        String requestBody = buildSubmitRequestBody(activity, validateCode);

        if (requestBody == null)
            return;

        Response.Listener responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {


                String[] responseResult = SubmitFetchData(response);

                if (responseResult != null) {

                    if (responseResult[0].equals("registered")) {
                        model.setApiTokenSharedPref(enterMobileInfo[0], activity);

                        String name = responseResult[1];
                        String family = responseResult[2];
                        String avatar = responseResult[3];
                        String credit = responseResult[4];

                        model.setNameSharedPref(name, activity);
                        model.setFamilySharedPref(family, activity);
                        model.setAvatarSharedPref(avatar, activity);
                        model.setCreditSharedPref(credit, activity);


                        //Subscribe to Pusher interest for PushNotification
                        PushNotifications.clearDeviceInterests(); //clear if prev interest exist
                        PushNotifications.addDeviceInterest("all");
                        PushNotifications.addDeviceInterest("user-" + enterMobileInfo[1]);
                        Intent intent = new Intent(activity, HomeActivity.class);
                        activity.startActivity(intent);
                        activity.finish();
                    } else {
                        Intent intent = new Intent(activity, NameRegisterActivity.class);
                        intent.putExtra("apiToken", enterMobileInfo[0]).putExtra("mobileNumber", enterMobileInfo[1]);
                        activity.startActivity(intent);
                        activity.finish();
                    }


                }

                setResponseReceived();
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                ///////Error Handling

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

                //till here

                ErrorHandling(errorMessage, activity);

                setResponseReceived();


            }
        };


        model.sendSubmitRequest(activity, requestBody, responseListener, errorListener);

    }


    private String buildSubmitRequestBody(Activity activity, String validateCode) {

        //to get App Version
        int appVersion = 0;
        try {
            appVersion = activity.getPackageManager().getPackageInfo(activity.getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }


        //to get Android ID
        @SuppressLint("HardwareIds")
        String androidId = Settings.Secure.getString(activity.getContentResolver(),
                Settings.Secure.ANDROID_ID);


        //to get OS Version
        String OsVersion = android.os.Build.VERSION.RELEASE;


        try {

            //to build detail parameter
            JSONObject detail = new JSONObject();
            detail.put("os", "android " + OsVersion);
            detail.put("appVersion", Integer.toString(appVersion) + ".0.0");
            detail.put("uuid", androidId);
            detail.put("code", validateCode);

            //to build jsonBody
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("Detail", detail);
            //get apiToken from prev activity
            jsonBody.put("api_token", enterMobileInfo[0]);

            return jsonBody.toString();


        } catch (JSONException e) {
            e.printStackTrace();

            return null;
        }

    }


    private String[] SubmitFetchData(String response) {


        try {
            JSONObject jsonobject = new JSONObject(response);

            //to get status
            String status = jsonobject.getString("status");

            if (status.equals("success")) {

                JSONObject data = jsonobject.getJSONObject("data");
                boolean registered = data.getBoolean("registered");

                String[] responseResult = new String[5];

                if (registered) {

                    responseResult[0] = "registered";

                    responseResult[1] = data.getString("name");
                    responseResult[2] = data.getString("family");
                    responseResult[3] = data.getString("avatar");
                    responseResult[4] = data.getString("credit");

                    return responseResult;

                } else {

                    responseResult[0] = "notRegistered";
                    return responseResult;
                }

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
}
