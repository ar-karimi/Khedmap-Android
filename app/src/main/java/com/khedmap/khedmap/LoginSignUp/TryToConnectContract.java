package com.khedmap.khedmap.LoginSignUp;


import android.app.Activity;

public interface TryToConnectContract {

    interface ViewCallBack {

    }


    interface ModelCallBack {

    }


    interface PresenterCallBack {

        void retryButtonClicked(Activity activity);

        void settingsButtonClicked(Activity activity);

    }

}
