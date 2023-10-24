package com.khedmap.khedmap.LoginSignUp;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;

import com.android.volley.Response;

public interface ProfilePicGenderRegisterContract {

    interface ViewCallBack {

        void showSnackBar(String message);

        void showProgressBar(boolean showProgressBar);

    }


    interface ModelCallBack {

        void sendSubmitRequest(Context context, Response.Listener responseListener, Response.ErrorListener errorListener
                , String[] nameRegisterInfo, String gender, Uri resultUri, String reagentCode);


        void setApiTokenSharedPref(String apiToken, Context context);

        void setNameSharedPref(String name, Context context);

        void setFamilySharedPref(String family, Context context);

        void setAvatarSharedPref(String avatar, Context context);

        void setCreditSharedPref(String credit, Context context);

    }


    interface PresenterCallBack {

        void setNameRegisterInfo(String[] nameRegisterInfo);

        void profilePicClicked(Activity activity, View view);

        void genderSwitchClicked(String gender);

        Uri getRetrievedImage(int requestCode, int resultCode, Intent data);

        void submitButtonClicked(Activity activity, String reagentCode);

    }

}
