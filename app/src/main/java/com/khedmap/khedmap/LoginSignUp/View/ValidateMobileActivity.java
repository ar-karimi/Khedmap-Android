package com.khedmap.khedmap.LoginSignUp.View;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.khedmap.khedmap.InitApplication;
import com.khedmap.khedmap.LoginSignUp.ValidateMobileContract;
import com.khedmap.khedmap.R;
import com.khedmap.khedmap.Utilities.CustomSnackbar;
import com.khedmap.khedmap.Utilities.UtilitiesSingleton;
import com.khedmap.khedmap.di.component.DaggerActivityComponent;
import com.khedmap.khedmap.di.module.MvpModule;

import javax.inject.Inject;

import cn.iwgang.countdownview.CountdownView;
import io.github.inflationx.calligraphy3.CalligraphyConfig;
import io.github.inflationx.calligraphy3.CalligraphyInterceptor;
import io.github.inflationx.viewpump.ViewPump;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import me.zhanghai.android.materialprogressbar.IndeterminateHorizontalProgressDrawable;

public class ValidateMobileActivity extends AppCompatActivity implements ValidateMobileContract.ViewCallBack {


    @Inject
    ValidateMobileContract.PresenterCallBack presenterCallBack;

    @Inject
    Context mContext;


    private TextView resendSmsButton;
    private ProgressBar progressBar;

    private EditText validateCodeEditText;


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


        setContentView(R.layout.activity_validate_mobile);

//to change StatusBar color
        UtilitiesSingleton.getInstance().changeStatusColor(ValidateMobileActivity.this, false);

////////////////////////////////////////////////////////////////////

        //to get enterMobileInfo from EnterMobileActivity
        Intent intentFromEnterMobile = getIntent();
        String[] enterMobileInfo = intentFromEnterMobile.getStringArrayExtra("enterMobileInfo");
        presenterCallBack.setEnterMobileInfo(enterMobileInfo);

        resendSmsButton = findViewById(R.id.resend_sms_button);


//Set countDownView
        CountdownView countDownView = findViewById(R.id.countdown_view);
        presenterCallBack.setupCountDownView(countDownView);


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


//to open keyboard by click on mailIcon
        findViewById(R.id.ic_mail).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                openKeyboard();
            }
        });
//till here

        validateCodeEditText = findViewById(R.id.validate_code_edit_text);

        findViewById(R.id.submit_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                presenterCallBack.submitButtonClicked(ValidateMobileActivity.this, validateCodeEditText.getText().toString());
            }
        });


        findViewById(R.id.resend_sms_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                presenterCallBack.resendSmsButtonClicked(ValidateMobileActivity.this);
            }
        });


        findViewById(R.id.change_phone_number_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                presenterCallBack.changePhoneButtonClicked(ValidateMobileActivity.this);
            }
        });


//to make a ProgressBar on Top
        setupProgressBar();


        //to set TextWatcher to editText
        validateCodeEditText.addTextChangedListener(new TextWatcher() {
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

        new CustomSnackbar(message, ValidateMobileActivity.this, findViewById(R.id.constraint_layout)).snackbar.show();

    }

    @Override
    public void showProgressBar(boolean showProgressBar) {
        if (showProgressBar) {
            progressBar.setVisibility(View.VISIBLE);
            validateCodeEditText.setEnabled(false);
        } else {
            progressBar.setVisibility(View.INVISIBLE);
            validateCodeEditText.setEnabled(true);
        }
    }

    @Override
    public void enableResendSmsButton() {

        resendSmsButton.setTextColor(getResources().getColor(R.color.colorPrimary));
    }

    @Override
    public void disableResendSmsButton() {

        resendSmsButton.setTextColor(getResources().getColor(R.color.colorGrayText));
    }


    @Override
    public Activity getActivity() {
        return this;
    }
}
