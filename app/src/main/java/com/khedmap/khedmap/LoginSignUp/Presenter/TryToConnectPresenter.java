package com.khedmap.khedmap.LoginSignUp.Presenter;

import android.app.Activity;
import android.content.Intent;

import com.khedmap.khedmap.LoginSignUp.Model.TryToConnectModel;
import com.khedmap.khedmap.LoginSignUp.TryToConnectContract;
import com.khedmap.khedmap.LoginSignUp.View.SplashActivity;
import com.khedmap.khedmap.LoginSignUp.View.TryToConnectActivity;
import com.khedmap.khedmap.R;
import com.khedmap.khedmap.Utilities.UtilitiesSingleton;

import io.github.inflationx.calligraphy3.CalligraphyConfig;
import io.github.inflationx.calligraphy3.CalligraphyInterceptor;
import io.github.inflationx.viewpump.ViewPump;

public class TryToConnectPresenter implements TryToConnectContract.PresenterCallBack {

    private TryToConnectContract.ViewCallBack view;
    private TryToConnectModel model;

    public TryToConnectPresenter(TryToConnectContract.ViewCallBack view, TryToConnectModel model) {
        this.model = model;
        this.view = view;
    }


    @Override
    public void retryButtonClicked(Activity activity) {

        if (UtilitiesSingleton.getInstance().isNetworkAvailable(activity)) {
            Intent intent = new Intent(activity, SplashActivity.class);
            activity.startActivity(intent);
        } else {
            Intent intent = new Intent(activity, TryToConnectActivity.class);
            activity.startActivity(intent);
        }

        activity.finish();
    }

    @Override
    public void settingsButtonClicked(Activity activity) {

        activity.startActivityForResult(new Intent(android.provider.Settings.ACTION_SETTINGS), 0);
    }


}
