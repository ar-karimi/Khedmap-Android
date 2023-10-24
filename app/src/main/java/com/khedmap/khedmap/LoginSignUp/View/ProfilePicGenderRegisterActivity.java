package com.khedmap.khedmap.LoginSignUp.View;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.khedmap.khedmap.InitApplication;
import com.khedmap.khedmap.LoginSignUp.ProfilePicGenderRegisterContract;
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
import me.zhanghai.android.materialprogressbar.IndeterminateHorizontalProgressDrawable;

public class ProfilePicGenderRegisterActivity extends AppCompatActivity implements ProfilePicGenderRegisterContract.ViewCallBack {


    @Inject
    ProfilePicGenderRegisterContract.PresenterCallBack presenterCallBack;

    @Inject
    Context mContext;


    private ImageView profilePic;
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


//to change font
        changeFont();


        setContentView(R.layout.activity_profile_pic_gender_register);

//to change StatusBar color
        UtilitiesSingleton.getInstance().changeStatusColor(ProfilePicGenderRegisterActivity.this, false);

////////////////////////////////////////////////////////////////////


        //to get nameRegisterInfo from NameRegisterActivity
        Intent intentFromNameRegister = getIntent();
        String[] nameRegisterInfo = intentFromNameRegister.getStringArrayExtra("nameRegisterInfo");
        presenterCallBack.setNameRegisterInfo(nameRegisterInfo);


        profilePic = findViewById(R.id.profile_pic);
        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                presenterCallBack.profilePicClicked(ProfilePicGenderRegisterActivity.this, view);

            }
        });


//to handle gender switch
        findViewById(R.id.male_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenterCallBack.genderSwitchClicked("male");
            }
        });

        findViewById(R.id.female_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenterCallBack.genderSwitchClicked("female");
            }
        });

        findViewById(R.id.not_specified_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenterCallBack.genderSwitchClicked("");
            }
        });

//till here

        findViewById(R.id.submit_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                EditText reagentCodeEditText = findViewById(R.id.reagent_code_edit_text);

                presenterCallBack.submitButtonClicked(ProfilePicGenderRegisterActivity.this
                        , reagentCodeEditText.getText().toString().trim());
            }
        });


//to make a ProgressBar on Top
        setupProgressBar();


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

        new CustomSnackbar(message, ProfilePicGenderRegisterActivity.this, findViewById(R.id.constraint_layout)).snackbar.show();

    }

    @Override
    public void showProgressBar(boolean showProgressBar) {
        if (showProgressBar)
            progressBar.setVisibility(View.VISIBLE);
        else
            progressBar.setVisibility(View.INVISIBLE);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        Uri imageUri = presenterCallBack.getRetrievedImage(requestCode, resultCode, data);
        if (imageUri != null)
            profilePic.setImageURI(imageUri);

    }

}
