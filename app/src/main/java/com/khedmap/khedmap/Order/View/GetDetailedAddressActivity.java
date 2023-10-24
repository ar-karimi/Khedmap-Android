package com.khedmap.khedmap.Order.View;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.khedmap.khedmap.InitApplication;
import com.khedmap.khedmap.Order.GetDetailedAddressContract;
import com.khedmap.khedmap.R;
import com.khedmap.khedmap.Utilities.CustomSnackbar;
import com.khedmap.khedmap.di.component.DaggerActivityComponent;
import com.khedmap.khedmap.di.module.MvpModule;

import java.util.List;

import javax.inject.Inject;

import io.github.inflationx.calligraphy3.CalligraphyConfig;
import io.github.inflationx.calligraphy3.CalligraphyInterceptor;
import io.github.inflationx.viewpump.ViewPump;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class GetDetailedAddressActivity extends AppCompatActivity implements GetDetailedAddressContract.ViewCallBack {


    @Inject
    GetDetailedAddressContract.PresenterCallBack presenterCallBack;

    @Inject
    Context mContext;


    private final int REQUEST_CODE_SPEECH_RECOGNIZER = 3000;

    private EditText addressEditText;

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


        setContentView(R.layout.activity_get_detailed_address);

////////////////////////////////////////////////////////////////////


        //to get selected itemId and submittedOrderId from prev page
        presenterCallBack.setSelectedItemIdAndLocation(getIntent().getStringExtra("suggestionId"),
                getIntent().getStringExtra("submittedOrderId"),
                getIntent().getStringExtra("latitude"),
                getIntent().getStringExtra("longitude"));

        addressEditText = findViewById(R.id.address_edit_text);

//to close keyboard by tap outside
        findViewById(R.id.main_linear_layout).setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                closeKeyboard();
                return true;
            }
        });

//toolbar back icon
        findViewById(R.id.ic_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


        findViewById(R.id.submit_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                presenterCallBack.submitButtonClicked(GetDetailedAddressActivity.this,
                        addressEditText.getText().toString().trim());
            }
        });


        //Speech Recognition button
        findViewById(R.id.ic_mic).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startSpeechRecognizer();
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


    @Override
    public void showSnackBar(String message) {

        new CustomSnackbar(message, GetDetailedAddressActivity.this, findViewById(R.id.constraint_layout)).snackbar.show();

    }


    @Override
    public void showProgressBar(boolean showProgressBar) {
        ProgressBar progressBar = findViewById(R.id.progress_bar);
        LinearLayout mainLinearLayout = findViewById(R.id.main_linear_layout);
        Button submitButton = findViewById(R.id.submit_button);

        if (showProgressBar) {
            mainLinearLayout.setVisibility(View.GONE);
            submitButton.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
        } else {
            progressBar.setVisibility(View.GONE);
            mainLinearLayout.setVisibility(View.VISIBLE);
            submitButton.setVisibility(View.VISIBLE);
        }
    }

    private void startSpeechRecognizer() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "fa-IR");
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "آدرس دقیق خود را بگویید");
        startActivityForResult(intent, REQUEST_CODE_SPEECH_RECOGNIZER);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_SPEECH_RECOGNIZER) {
            if (resultCode == RESULT_OK) {
                List<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

                addressEditText.setText("");
                addressEditText.append(results.get(0));
            }
        }
    }

}
