package com.khedmap.khedmap.LoginSignUp;


import android.app.Activity;
import android.content.Context;

public interface NameRegisterContract {

    interface ViewCallBack {

        void showSnackBar(String message);
    }


    interface ModelCallBack {

        String getTermsSharedPref(Context context);
    }


    interface PresenterCallBack {

        void submitButtonClicked(Activity activity, String apiToken, String mobileNumber
                , String name, String family);

        void rulesButtonClicked(Context context);
    }

}
