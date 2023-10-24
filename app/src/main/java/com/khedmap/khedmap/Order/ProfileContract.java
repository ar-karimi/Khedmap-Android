package com.khedmap.khedmap.Order;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;

import com.android.volley.Response;

import java.util.List;

public interface ProfileContract {

    interface ViewCallBack {

        void showSnackBar(String message);

        void showLoadingView(boolean showLoadingView);

        void showToast(String message);

        void setPageItems(List<String> profileDetails, boolean isFromEdit);

        void showEditNameDialog(boolean isName, String currentName);

        void setChangedName(String input, boolean isName);
    }


    interface ModelCallBack {

        void setApiTokenSharedPref(String apiToken, Context context);

        String getApiTokenSharedPref(Context context);

        void setNameSharedPref(String name, Context context);

        void setFamilySharedPref(String family, Context context);

        void setAvatarSharedPref(String avatar, Context context);

        void sendGetInformationRequest(Context context, String requestBody, Response.Listener responseListener, Response.ErrorListener errorListener);

        void sendUpdateProfileRequest(Context context, Response.Listener responseListener, Response.ErrorListener errorListener
                , List<String> profileInfo, Uri resultUri);
    }


    interface PresenterCallBack {

        void initPageItems(Activity activity, boolean isFromEdit);

        void genderSwitchClicked(String gender);

        void editProfilePicButtonClicked(Activity activity, View view);

        Uri getRetrievedImage(int requestCode, int resultCode, Intent data);

        void editNameButtonClicked(boolean isName);

        boolean confirmEditNameClicked(String input, boolean isName);

        void submitButtonClicked(Activity activity);
    }

}
