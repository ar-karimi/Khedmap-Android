package com.khedmap.khedmap.LoginSignUp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.android.volley.Response;
import com.khedmap.khedmap.LoginSignUp.View.EnterMobileActivity;


public interface EnterMobileContract {

    interface ViewCallBack {

        void closeKeyboard();

        void showSnackBar(String message);

        void showProgressBar(boolean showProgressBar);

        Activity getActivity();

    }


    interface ModelCallBack {

        String getRetrievedPhoneNumber(int requestCode, int resultCode, Intent data);

        void sendRequest(Context context, CharSequence mobileNumber,
                         Response.Listener responseListener, Response.ErrorListener errorListener);

        void setApiTokenSharedPref(String apiToken, Context context);
    }


    interface PresenterCallBack {

        void getPhoneNumber(Context context, EnterMobileActivity enterMobileActivity);

        String getRetrievedPhoneNumber(int requestCode, int resultCode, Intent data);

        void textChanged(String number);

        void sendSmsButtonClicked(Activity activity, CharSequence mobileNumber);


    }
}