package com.khedmap.khedmap.LoginSignUp.View;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;

import com.github.florent37.viewanimator.ViewAnimator;
import com.khedmap.khedmap.InitApplication;
import com.khedmap.khedmap.LoginSignUp.SplashContract;
import com.khedmap.khedmap.R;
import com.khedmap.khedmap.Utilities.CustomSnackbar;
import com.khedmap.khedmap.Utilities.UtilitiesSingleton;
import com.khedmap.khedmap.di.component.DaggerActivityComponent;
import com.khedmap.khedmap.di.module.MvpModule;

import javax.inject.Inject;

import io.github.inflationx.calligraphy3.CalligraphyConfig;
import io.github.inflationx.calligraphy3.CalligraphyInterceptor;
import io.github.inflationx.viewpump.ViewPump;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class SplashActivity extends AppCompatActivity implements SplashContract.ViewCallBack {


    @Inject
    SplashContract.PresenterCallBack presenterCallBack;

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

//to hide statusBar
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_splash);

//to change Status color to Transparent
        UtilitiesSingleton.getInstance().changeStatusColor(SplashActivity.this, true);

////////////////////////////////////////////////////////////////////


        //to set animation to logo and title
        ViewAnimator
                .animate(findViewById(R.id.khedmap_splash_logo))
                .newsPaper()
                .duration(2000)
                .start();
        ViewAnimator
                .animate(findViewById(R.id.khedmap_splash_title))
                .newsPaper()
                .duration(2000)
                .start();

        int SPLASH_DISPLAY_LENGTH = 2000;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                //to Check Internet Connection
                presenterCallBack.checkInternetConnection(SplashActivity.this);


                //to validation token and uuid
                presenterCallBack.sendValidateRequest(SplashActivity.this);
                //to check available update
                presenterCallBack.sendUpdateRequest(SplashActivity.this);


            }
        }, SPLASH_DISPLAY_LENGTH);

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


    @Override
    public void showSnackBar(String message) {

        new CustomSnackbar(message, SplashActivity.this, findViewById(R.id.constraint_layout)).snackbar.show();

    }


}
