package com.khedmap.khedmap.LoginSignUp.Presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.crashlytics.android.Crashlytics;
import com.khedmap.khedmap.LoginSignUp.Model.NameRegisterModel;
import com.khedmap.khedmap.LoginSignUp.NameRegisterContract;
import com.khedmap.khedmap.LoginSignUp.View.ProfilePicGenderRegisterActivity;

import java.util.regex.Pattern;

public class NameRegisterPresenter implements NameRegisterContract.PresenterCallBack {

    private NameRegisterContract.ViewCallBack view;
    private NameRegisterModel model;

    public NameRegisterPresenter(NameRegisterContract.ViewCallBack view, NameRegisterModel model) {
        this.model = model;
        this.view = view;
    }


    @Override
    public void submitButtonClicked(Activity activity, String apiToken, String mobileNumber
            , String name, String family) {


        //name Checking
        if (!nameRegexChecking(name, "name"))
            return;

        //family Checking
        if (!nameRegexChecking(family, "family"))
            return;

        String[] nameRegisterInfo = {apiToken, mobileNumber, name, family};
        activity.startActivity(new Intent(activity, ProfilePicGenderRegisterActivity.class)
                .putExtra("nameRegisterInfo", nameRegisterInfo));
        activity.finish();

    }

    private boolean nameRegexChecking(String name, String type) {

        if (name.length() < 2) {
            switch (type) {
                case "name":
                    view.showSnackBar("نام باید حداقل 2 حرف باشد");
                    break;
                case "family":
                    view.showSnackBar("نام خانوادگی باید حداقل 2 حرف باشد");
                    break;
            }
            return false;
        }

        if (!Pattern.matches("^[\u0629\u0643\u0649\u064A\u064B\u064D\u06D5\u0020\u0621-\u0628\u062A-\u063A\u0641-\u0642\u0644-\u0648\u064E-\u0651\u0655\u067E\u0686\u0698\u06A9-\u06AF\u06BE\u06CC]+$", name)) {
            switch (type) {
                case "name":
                    view.showSnackBar("لطفاً نام را به صورت صحیح و فارسی وارد کنید");
                    break;
                case "family":
                    view.showSnackBar("لطفاً نام خانوادگی را به صورت صحیح و فارسی وارد کنید");
                    break;
            }
            return false;
        }
        return true;

    }

    @Override
    public void rulesButtonClicked(Context context) {

        String termsUrl = model.getTermsSharedPref(context);

        try {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(termsUrl)));
        } catch (Exception e) {
            Crashlytics.logException(e);
            Crashlytics.setString("url-terms", termsUrl);
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://khedmap.co")));
        }

    }

}
