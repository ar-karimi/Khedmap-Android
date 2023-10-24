package com.khedmap.khedmap.LoginSignUp;

import android.app.Activity;
import android.content.Context;

import com.android.volley.Response;

public interface SplashContract {

    interface ViewCallBack {

        void showSnackBar(String message);
    }


    interface ModelCallBack {

        void sendValidateRequest(Activity activity, final String requestBody,
                                 Response.Listener responseListener, Response.ErrorListener errorListener);

        void sendUpdateRequest(Response.Listener responseListener, Response.ErrorListener errorListener);

        void setApiTokenSharedPref(String apiToken, Context context);

        String getApiTokenSharedPref(Context context);

        void addToQueueCheckValidTokenRequest();

        void setSupportPhoneSharedPref(String value, Context context);

        void setTermsSharedPref(String value, Context context);
    }


    interface PresenterCallBack {

        void checkInternetConnection(Activity activity);

        void sendValidateRequest(final Activity activity);

        void sendUpdateRequest(final Activity activity);


    }

}
