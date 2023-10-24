package com.khedmap.khedmap.LoginSignUp.Presenter;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.util.Log;

import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.error.NoConnectionError;
import com.android.volley.error.TimeoutError;
import com.android.volley.error.VolleyError;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.credentials.HintRequest;
import com.google.android.gms.common.api.GoogleApiClient;
import com.khedmap.khedmap.LoginSignUp.EnterMobileContract;
import com.khedmap.khedmap.LoginSignUp.Model.EnterMobileModel;
import com.khedmap.khedmap.LoginSignUp.View.EnterMobileActivity;
import com.khedmap.khedmap.LoginSignUp.View.ValidateMobileActivity;
import com.khedmap.khedmap.R;
import com.khedmap.khedmap.Utilities.UtilitiesSingleton;
import com.shashank.sony.fancygifdialoglib.FancyGifDialog;
import com.shashank.sony.fancygifdialoglib.FancyGifDialogListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Pattern;

public class EnterMobilePresenter implements EnterMobileContract.PresenterCallBack {

    private EnterMobileContract.ViewCallBack view;
    private EnterMobileModel model;

    private GoogleApiClient mCredentialsApiClient = null;
    private static final int RC_HINT = 1000;

    private boolean ResponseReceived = true;


    public EnterMobilePresenter(EnterMobileContract.ViewCallBack view, EnterMobileModel model) {
        this.model = model;
        this.view = view;
    }


    ///////////////////////////////////////////
//to get Phone Number from google api
    @Override
    public void getPhoneNumber(Context context, EnterMobileActivity enterMobileActivity) {

        if (mCredentialsApiClient == null)
            mCredentialsApiClient = new GoogleApiClient.Builder(context)
                    .addConnectionCallbacks(enterMobileActivity)
                    .enableAutoManage(enterMobileActivity, enterMobileActivity)
                    .addApi(Auth.CREDENTIALS_API)
                    .build();


// Construct a request for phone numbers and show the picker
        HintRequest hintRequest = new HintRequest.Builder()
                .setPhoneNumberIdentifierSupported(true)
                .build();

        PendingIntent intent = Auth.CredentialsApi.getHintPickerIntent(
                mCredentialsApiClient, hintRequest);
        try {
            enterMobileActivity.startIntentSenderForResult(intent.getIntentSender(),
                    RC_HINT, null, 0, 0, 0);
        } catch (IntentSender.SendIntentException e) {
            e.printStackTrace();
        }

    }


    @Override
    public String getRetrievedPhoneNumber(int requestCode, int resultCode, Intent data) {
        String mobileNumber = model.getRetrievedPhoneNumber(requestCode, resultCode, data);

        if (mobileNumber != null)
            return mobileNumber.replace("+98", "0");
        return null;
    }
//till here
///////////////////////////////////////////


    @Override
    public void textChanged(String number) {

        if (number.length() == 11)
            sendSmsButtonClicked(view.getActivity(), number);


    }


    private boolean mobileRegexChecking(CharSequence mobileNumber) {

        if (mobileNumber.length() == 0) {
            view.showSnackBar("لطفا شماره موبایل را وارد کنید");
            return false;
        }
        if (mobileNumber.charAt(0) != '0') {
            mobileNumber = "0" + mobileNumber;
        }
        if (!Pattern.matches("^09[0-9]{9}$", mobileNumber)) {
            view.showSnackBar("لطفا شماره موبایل را به صورت صحیح وارد کنید");
            return false;
        }
        return true;

    }


    ///////////////////////////////////////////
//to send phone number to server and get Token
    @Override
    public void sendSmsButtonClicked(Activity activity, CharSequence mobileNumber) {

        if (!ResponseReceived)
            return;

        //to close keyboard by click on sendSmsButton
        view.closeKeyboard();

        //Regex Checking
        if (!mobileRegexChecking(mobileNumber))
            return;

        //to be Sure 0 is in first of mobileNumber
        if (mobileNumber.charAt(0) != '0') {
            mobileNumber = "0" + mobileNumber;
        }

        showValidateDialog(activity, mobileNumber);

    }


    private void showValidateDialog(final Activity activity, final CharSequence mobileNumber) {

        new FancyGifDialog.Builder(activity)
                .setTitle(mobileNumber.toString())
                .setMessage("آیا از شماره موبایل وارد شده اطمینان دارید؟")
                .setNegativeBtnText("ویرایش")
                .setPositiveBtnBackground("#FF5B00")
                .setPositiveBtnText("بله")
                .setNegativeBtnBackground("#333333")
                .setGifResource(R.drawable.validate_phone)   //Pass your Gif here
                .isCancellable(false)
                .OnPositiveClicked(new FancyGifDialogListener() {
                    @Override
                    public void OnClick() {


                        if (!UtilitiesSingleton.getInstance().isNetworkAvailable(activity)) {
                            view.showSnackBar("اتصال اینترنت را بررسی کنید");
                            return;
                        }

                        view.showProgressBar(true);
                        ResponseReceived = false;

                        sendRequest(activity, mobileNumber);


                    }
                })
                .OnNegativeClicked(new FancyGifDialogListener() {
                    @Override
                    public void OnClick() {
                        //dismissed
                    }
                })
                .build();
    }


    private void sendRequest(final Activity activity, final CharSequence mobileNumber) {


        Response.Listener responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                String[] enterMobileInfo = enterMobileFetchData(response, mobileNumber);

                if (enterMobileInfo != null) {
                    Intent intent = new Intent(activity, ValidateMobileActivity.class);
                    intent.putExtra("enterMobileInfo", enterMobileInfo);
                    activity.startActivity(intent);
                    activity.finish();
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


        model.sendRequest(activity, mobileNumber, responseListener, errorListener);

    }


    private String[] enterMobileFetchData(String response, CharSequence mobileNumber) {

        // response
        try {
            JSONObject jsonobject = new JSONObject(response);

            //to get status
            String status = jsonobject.getString("status");

            if (status.equals("success")) {
                //to get Api Token
                JSONObject data = jsonobject.getJSONObject("data");
                String apiToken = data.getString("api_token");

                //send apiToken & mobileNumber to next activity
                return new String[]{apiToken, mobileNumber.toString()};

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


    private void setResponseReceived() {
        ResponseReceived = true;
        view.showProgressBar(false);
    }

//till here
///////////////////////////////////////////

}
