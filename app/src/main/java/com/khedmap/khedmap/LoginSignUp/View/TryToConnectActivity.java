package com.khedmap.khedmap.LoginSignUp.View;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.airbnb.lottie.LottieAnimationView;
import com.khedmap.khedmap.InitApplication;
import com.khedmap.khedmap.LoginSignUp.TryToConnectContract;
import com.khedmap.khedmap.R;
import com.khedmap.khedmap.Utilities.UtilitiesSingleton;
import com.khedmap.khedmap.di.component.DaggerActivityComponent;
import com.khedmap.khedmap.di.module.MvpModule;

import javax.inject.Inject;

import io.github.inflationx.calligraphy3.CalligraphyConfig;
import io.github.inflationx.calligraphy3.CalligraphyInterceptor;
import io.github.inflationx.viewpump.ViewPump;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class TryToConnectActivity extends AppCompatActivity implements TryToConnectContract.ViewCallBack {


    @Inject
    TryToConnectContract.PresenterCallBack presenterCallBack;

    @Inject
    Context mContext;


    //to change font
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }
    //till here, continues ...

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


//to configure Dagger
        DaggerActivityComponent.builder()
                .appComponent(InitApplication.get(this).component())
                .mvpModule(new MvpModule(this))
                .build()
                .inject(this);


//to change font
        changeFont();


        setContentView(R.layout.activity_try_to_connect);

//to change Status color to Transparent
        UtilitiesSingleton.getInstance().changeStatusColor(TryToConnectActivity.this, false);

////////////////////////////////////////////////////////////////////


        findViewById(R.id.retry_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                presenterCallBack.retryButtonClicked(TryToConnectActivity.this);

            }
        });

        findViewById(R.id.settings_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                presenterCallBack.settingsButtonClicked(TryToConnectActivity.this);
            }
        });
    }


    public void changeFont() {

        ViewPump.init(ViewPump.builder()
                .addInterceptor(new CalligraphyInterceptor(
                        new CalligraphyConfig.Builder()
                                .setDefaultFontPath("IRANSansMobileFonts/IRANSansMobile(FaNum).ttf")
                                .setFontAttrId(R.attr.fontPath)
                                .build()))
                .build());

    }

}
