package com.khedmap.khedmap.Order.View;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.khedmap.khedmap.InitApplication;
import com.khedmap.khedmap.Order.IncreaseCreditContract;
import com.khedmap.khedmap.R;
import com.khedmap.khedmap.Utilities.CustomSnackbar;
import com.khedmap.khedmap.di.component.DaggerActivityComponent;
import com.khedmap.khedmap.di.module.MvpModule;

import javax.inject.Inject;

import io.github.inflationx.calligraphy3.CalligraphyConfig;
import io.github.inflationx.calligraphy3.CalligraphyInterceptor;
import io.github.inflationx.viewpump.ViewPump;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class IncreaseCreditActivity extends AppCompatActivity implements IncreaseCreditContract.ViewCallBack {


    @Inject
    IncreaseCreditContract.PresenterCallBack presenterCallBack;

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


        setContentView(R.layout.activity_increase_credit);

////////////////////////////////////////////////////////////////////

//to close keyboard by tap outside
        findViewById(R.id.main_linear_layout).setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                closeKeyboard();
                return true;
            }
        });

        findViewById(R.id.ic_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                onBackPressed();
            }
        });


        //to get Current Credit
        presenterCallBack.initCurrentCredit(this);


        //To Receive Payment Result
        Intent intent = getIntent();
        if (intent.getData() != null)
            presenterCallBack.handlePaymentResult(intent.getData(), this);

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

    public void closeKeyboard() {

        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (imm != null && findViewById(R.id.coordinator_layout).getWindowToken() != null) {
            imm.hideSoftInputFromWindow(findViewById(R.id.coordinator_layout).getWindowToken(), 0);
        }
    }


    @Override
    public void showSnackBar(String message) {

        new CustomSnackbar(message, IncreaseCreditActivity.this, findViewById(R.id.coordinator_layout)).snackbar.show();

    }


    @Override
    public void showLoadingView(boolean showLoadingView) {
        FrameLayout loadingView = findViewById(R.id.loading_view);
        if (showLoadingView)
            loadingView.setVisibility(View.VISIBLE);
        else
            loadingView.setVisibility(View.GONE);

    }


    @Override
    public void setCurrentCredit(String credit) {


        //set clickListener here to sure that response is success
        final EditText increaseCreditEditText = findViewById(R.id.increase_credit_edit_text);
        findViewById(R.id.submit_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                closeKeyboard();
                presenterCallBack.submitButtonClicked(IncreaseCreditActivity.this, increaseCreditEditText.getText().toString());
            }
        });


        TextView currentCredit = findViewById(R.id.current_credit);
        currentCredit.setText(credit);
    }

}
