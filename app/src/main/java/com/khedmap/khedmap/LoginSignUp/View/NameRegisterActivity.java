package com.khedmap.khedmap.LoginSignUp.View;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.khedmap.khedmap.InitApplication;
import com.khedmap.khedmap.LoginSignUp.NameRegisterContract;
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

public class NameRegisterActivity extends AppCompatActivity implements NameRegisterContract.ViewCallBack {

    @Inject
    NameRegisterContract.PresenterCallBack presenterCallBack;

    @Inject
    Context mContext;


    private String apiToken;
    private String mobileNumber;


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


        setContentView(R.layout.activity_name_register);

//to change StatusBar color
        UtilitiesSingleton.getInstance().changeStatusColor(NameRegisterActivity.this, false);

////////////////////////////////////////////////////////////////////


//Receive validateMobileInfo from ValidateMobileActivity
        Intent intentFromValidateMobile = getIntent();
        apiToken = intentFromValidateMobile.getStringExtra("apiToken");
        mobileNumber = intentFromValidateMobile.getStringExtra("mobileNumber");


//to open keyboard automatically
        EditText nameEditText = findViewById(R.id.name_edit_text);
        nameEditText.requestFocus();
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


//to open keyboard by click on personIcon
        findViewById(R.id.ic_person1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openKeyboard();
            }
        });

        findViewById(R.id.ic_person2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openKeyboard();
            }
        });
//till here


        findViewById(R.id.submit_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                closeKeyboard();

                EditText nameEditText = findViewById(R.id.name_edit_text);
                EditText familyEditText = findViewById(R.id.family_edit_text);

                presenterCallBack.submitButtonClicked(NameRegisterActivity.this, apiToken, mobileNumber,
                        nameEditText.getText().toString().trim(), familyEditText.getText().toString().trim());

            }
        });


        findViewById(R.id.rules_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenterCallBack.rulesButtonClicked(NameRegisterActivity.this);
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


    @Override
    public void showSnackBar(String message) {

        new CustomSnackbar(message, NameRegisterActivity.this, findViewById(R.id.constraint_layout)).snackbar.show();

    }


}
