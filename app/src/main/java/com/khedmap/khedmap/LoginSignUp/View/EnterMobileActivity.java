package com.khedmap.khedmap.LoginSignUp.View;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.khedmap.khedmap.InitApplication;
import com.khedmap.khedmap.LoginSignUp.EnterMobileContract;
import com.khedmap.khedmap.R;
import com.khedmap.khedmap.Utilities.CustomSnackbar;
import com.khedmap.khedmap.Utilities.UtilitiesSingleton;
import com.khedmap.khedmap.di.component.DaggerActivityComponent;
import com.khedmap.khedmap.di.module.MvpModule;
import com.pusher.pushnotifications.PushNotifications;

import java.util.Locale;

import javax.inject.Inject;

import io.github.inflationx.calligraphy3.CalligraphyConfig;
import io.github.inflationx.calligraphy3.CalligraphyInterceptor;
import io.github.inflationx.viewpump.ViewPump;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import me.zhanghai.android.materialprogressbar.IndeterminateHorizontalProgressDrawable;

public class EnterMobileActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, EnterMobileContract.ViewCallBack {


    @Inject
    EnterMobileContract.PresenterCallBack presenterCallBack;

    @Inject
    Context mContext;


    private EditText mobileNumberEditText;
    private ProgressBar progressBar;


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

        //to keep layout Direction of activity ltr because don't shuffle in farsi lang
        Configuration configuration = getResources().getConfiguration();
        configuration.setLayoutDirection(new Locale("en"));
        getResources().updateConfiguration(configuration, getResources().getDisplayMetrics());

//to change font
        changeFont();

        setContentView(R.layout.activity_enter_mobile);

//to change StatusBar color
        UtilitiesSingleton.getInstance().changeStatusColor(EnterMobileActivity.this, false);

////////////////////////////////////////////////////////////////////


        //UnSubscribe from Pusher interest (user mobileNumber) for PushNotification
        PushNotifications.clearDeviceInterests();
        PushNotifications.addDeviceInterest("all");


//to open keyboard automatically
        mobileNumberEditText = findViewById(R.id.mobile_number_edit_text);
        mobileNumberEditText.requestFocus();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

//to close keyboard by tap outside
        findViewById(R.id.constraint_layout).setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                closeKeyboard();
                return true;
            }
        });
//till here


//to open keyboard by click on mobileIcon
        findViewById(R.id.ic_mobile).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                openKeyboard();
            }
        });
//till here

//to get Phone Number by click on simcardIcon
        findViewById(R.id.ic_simcard).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                presenterCallBack.getPhoneNumber(mContext, EnterMobileActivity.this);
            }
        });
//till here


        findViewById(R.id.send_sms_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CharSequence mobileNumber = mobileNumberEditText.getText().toString();
                presenterCallBack.sendSmsButtonClicked(EnterMobileActivity.this, mobileNumber);
            }
        });

//to make a ProgressBar on Top
        setupProgressBar();


        //to set TextWatcher to editText
        mobileNumberEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable number) {

                presenterCallBack.textChanged(number.toString());
            }
        });
        //till here

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
    public void closeKeyboard() {

        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (imm != null && findViewById(R.id.constraint_layout).getWindowToken() != null) {
            imm.hideSoftInputFromWindow(findViewById(R.id.constraint_layout).getWindowToken(), 0);
        }
    }

    public void openKeyboard() {
        InputMethodManager inputMethodManager =
                (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null) {
            inputMethodManager.toggleSoftInputFromWindow(
                    findViewById(R.id.constraint_layout).getApplicationWindowToken(),
                    InputMethodManager.SHOW_FORCED, 0);
        }
    }


    public void setupProgressBar() {

        progressBar = findViewById(R.id.indeterminate_horizontal_progress);
        IndeterminateHorizontalProgressDrawable progressDrawable = new IndeterminateHorizontalProgressDrawable(this);
        progressDrawable.setUseIntrinsicPadding(false);
        progressDrawable.setTint(getResources().getColor(R.color.colorPrimary));
        progressBar.setIndeterminateDrawable(progressDrawable);
        progressBar.setVisibility(View.INVISIBLE);
    }

    @Override
    public void showSnackBar(String message) {

        new CustomSnackbar(message, EnterMobileActivity.this, findViewById(R.id.constraint_layout)).snackbar.show();

    }

    @Override
    public void showProgressBar(boolean showProgressBar) {
        if (showProgressBar) {
            progressBar.setVisibility(View.VISIBLE);
            mobileNumberEditText.setEnabled(false);
        } else {
            progressBar.setVisibility(View.INVISIBLE);
            mobileNumberEditText.setEnabled(true);
        }
    }


    ///////////////////////////////////////////
//to get Phone Number from google api
// Obtain the phone number from the result
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        String mobileNumber = presenterCallBack.getRetrievedPhoneNumber(requestCode, resultCode, data);

        if (mobileNumber != null) {
            mobileNumberEditText = findViewById(R.id.mobile_number_edit_text);
            mobileNumberEditText.setText(mobileNumber);
        } else

            showSnackBar("لطفا شماره موبایل را وارد کنید");

    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }
//till here
//////////////////////////////////////////////

    @Override
    public Activity getActivity() {
        return this;
    }

}
