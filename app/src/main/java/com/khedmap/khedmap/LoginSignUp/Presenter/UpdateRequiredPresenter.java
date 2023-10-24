package com.khedmap.khedmap.LoginSignUp.Presenter;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

import com.khedmap.khedmap.LoginSignUp.Model.UpdateRequiredModel;
import com.khedmap.khedmap.LoginSignUp.UpdateRequiredContract;

public class UpdateRequiredPresenter implements UpdateRequiredContract.PresenterCallBack {

    private UpdateRequiredContract.ViewCallBack view;
    private UpdateRequiredModel model;

    public UpdateRequiredPresenter(UpdateRequiredContract.ViewCallBack view, UpdateRequiredModel model) {
        this.model = model;
        this.view = view;
    }

    @Override
    public void updateButtonClicked(Activity activity) {

        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://cafebazaar.ir/app/com.khedmap.khedmap/"));
        activity.startActivity(browserIntent);
    }


}
