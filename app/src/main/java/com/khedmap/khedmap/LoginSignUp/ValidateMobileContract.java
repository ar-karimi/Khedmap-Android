package com.khedmap.khedmap.LoginSignUp;


import android.app.Activity;
import android.content.Context;
import android.widget.EditText;

import com.android.volley.Response;

import cn.iwgang.countdownview.CountdownView;

public interface ValidateMobileContract {

    interface ViewCallBack {

        void closeKeyboard();

        void showSnackBar(String message);

        void showProgressBar(boolean showProgressBar);

        void enableResendSmsButton();

        void disableResendSmsButton();

        Activity getActivity();

    }


    interface ModelCallBack {

        void resendSmsRequest(Context context, final String mobileNumber,
                              Response.Listener responseListener, Response.ErrorListener errorListener);

        void sendSubmitRequest(Context context, final String requestBody,
                               Response.Listener responseListener, Response.ErrorListener errorListener);


        void setApiTokenSharedPref(String apiToken, Context context);

        void setNameSharedPref(String name, Context context);

        void setFamilySharedPref(String family, Context context);

        void setAvatarSharedPref(String avatar, Context context);

        void setCreditSharedPref(String credit, Context context);
    }


    interface PresenterCallBack {

        void changePhoneButtonClicked(Activity activity);

        void setEnterMobileInfo(String[] enterMobileInfo);

        void textChanged(String number);

        void resendSmsButtonClicked(Activity activity);

        void setupCountDownView(CountdownView countDownView);

        void submitButtonClicked(Activity activity, String validateCode);

    }

}
