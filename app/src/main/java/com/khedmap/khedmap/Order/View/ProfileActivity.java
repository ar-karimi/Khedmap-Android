package com.khedmap.khedmap.Order.View;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.util.TypedValue;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.khedmap.khedmap.InitApplication;
import com.khedmap.khedmap.Order.ProfileContract;
import com.khedmap.khedmap.R;
import com.khedmap.khedmap.Utilities.CustomSnackbar;
import com.khedmap.khedmap.di.component.DaggerActivityComponent;
import com.khedmap.khedmap.di.module.MvpModule;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

import javax.inject.Inject;

import io.github.inflationx.calligraphy3.CalligraphyConfig;
import io.github.inflationx.calligraphy3.CalligraphyInterceptor;
import io.github.inflationx.viewpump.ViewPump;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import io.rmiri.skeleton.SkeletonGroup;
import ir.hugenet.hugenetdialog.HugeNetAlertDialog;
import ir.hugenet.hugenetdialog.OnClickListener;

public class ProfileActivity extends AppCompatActivity implements ProfileContract.ViewCallBack {


    @Inject
    ProfileContract.PresenterCallBack presenterCallBack;

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


        setContentView(R.layout.activity_profile);

////////////////////////////////////////////////////////////////////

        presenterCallBack.initPageItems(this, false);


        findViewById(R.id.ic_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                onBackPressed();
            }
        });

        findViewById(R.id.edit_profile_pic_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                presenterCallBack.editProfilePicButtonClicked(ProfileActivity.this, view);
            }
        });

        findViewById(R.id.ic_edit_profile_name).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                presenterCallBack.editNameButtonClicked(true);
            }
        });

        findViewById(R.id.ic_edit_profile_family).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                presenterCallBack.editNameButtonClicked(false);
            }
        });

        findViewById(R.id.add_credit_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(ProfileActivity.this, IncreaseCreditActivity.class));
                finish();
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
        if (imm != null && findViewById(R.id.coordinator_layout).getWindowToken() != null) {
            imm.hideSoftInputFromWindow(findViewById(R.id.coordinator_layout).getWindowToken(), 0);
        }
    }


    @Override
    public void showSnackBar(String message) {

        new CustomSnackbar(message, ProfileActivity.this, findViewById(R.id.coordinator_layout)).snackbar.show();

    }


    @Override
    public void showLoadingView(boolean showLoadingView) {
        FrameLayout loadingView = findViewById(R.id.loading_view);
        Button submitButton = findViewById(R.id.submit_button);
        if (showLoadingView) {
            submitButton.setVisibility(View.GONE);
            loadingView.setVisibility(View.VISIBLE);
        } else {
            submitButton.setVisibility(View.VISIBLE);
            loadingView.setVisibility(View.GONE);
        }

    }


    @Override
    public void showToast(String message) {

        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }


    @Override
    public void setPageItems(List<String> profileDetails, boolean isFromEdit) {


        //set clickListener here to sure that response is success
        findViewById(R.id.submit_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                presenterCallBack.submitButtonClicked(ProfileActivity.this);
            }
        });



        ImageView profilePic = findViewById(R.id.profile_pic);
        final SkeletonGroup skeletonGroup = findViewById(R.id.skeletonGroup);
        TextView profileName = findViewById(R.id.profile_name);
        TextView profileFamily = findViewById(R.id.profile_family);
        TextView mobile = findViewById(R.id.mobile);
        TextView credit = findViewById(R.id.credit);


        if (!isFromEdit) { //to check is first time load or not
            if (!profileDetails.get(0).isEmpty())
                Picasso.get().load(profileDetails.get(0)).into(profilePic, new Callback.EmptyCallback() {
                    @Override
                    public void onSuccess() {

                        skeletonGroup.setShowSkeleton(false);
                        skeletonGroup.finishAnimation();
                    }
                });
            else {

                skeletonGroup.setShowSkeleton(false);
                skeletonGroup.finishAnimation();
            }
        }

        profileName.setText(profileDetails.get(1));
        profileFamily.setText(profileDetails.get(2));
        mobile.setText(profileDetails.get(3));
        credit.setText(profileDetails.get(4));


//to handle gender switch

        RadioButton maleButton = findViewById(R.id.male_button);
        RadioButton femaleButton = findViewById(R.id.female_button);
        RadioButton notSpecifiedButton = findViewById(R.id.not_specified_button);

        //to set current gender
        switch (profileDetails.get(5)) {
            case "male":
                maleButton.setChecked(true);
                break;
            case "female":
                femaleButton.setChecked(true);
                break;
            case "":
                notSpecifiedButton.setChecked(true);
                break;
            default:
                notSpecifiedButton.setChecked(true);
        }

        maleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenterCallBack.genderSwitchClicked("male");
            }
        });

        femaleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenterCallBack.genderSwitchClicked("female");
            }
        });

        notSpecifiedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenterCallBack.genderSwitchClicked("");
            }
        });

//till here

    }


    //to show Dialogs
    @Override
    public void showEditNameDialog(final boolean isName, String currentName) {

//to generate nameEditText (generate in java because can set margins for it)
        final float scale = getResources().getDisplayMetrics().density;
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        int margin_left_right_in_dp = 20;
        int margin_left_right_in_px = (int) (margin_left_right_in_dp * scale + 0.5f);

        int margin_Bottom_in_dp = 16;
        int margin_Bottom_in_px = (int) (margin_Bottom_in_dp * scale + 0.5f);

        layoutParams.setMargins(margin_left_right_in_px, 0, margin_left_right_in_px, margin_Bottom_in_px);


        LinearLayout layoutNameEditText = new LinearLayout(this);
        layoutNameEditText.setOrientation(LinearLayout.VERTICAL);

        final EditText nameEditText = new EditText(this);
        layoutNameEditText.addView(nameEditText, layoutParams);

        int padding_left_right_in_dp = 12;
        int padding_left_right_in_px = (int) (padding_left_right_in_dp * scale + 0.5f);

        int padding_Top_Bottom_in_dp = 8;
        int padding_Top_Bottom_in_px = (int) (padding_Top_Bottom_in_dp * scale + 0.5f);
        nameEditText.setPadding(padding_left_right_in_px, padding_Top_Bottom_in_px, padding_left_right_in_px, padding_Top_Bottom_in_px);

        nameEditText.setBackgroundResource(R.drawable.bordered_edit_text);
        nameEditText.setTypeface(Typeface.createFromAsset(getApplicationContext().getAssets(), "IRANSansMobileFonts/IRANSansMobile(FaNum).ttf"));
        nameEditText.setTextColor(Color.parseColor("#333333"));
        nameEditText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f);

        //to set maxLength for EditText
        InputFilter[] filterArray = new InputFilter[1];
        filterArray[0] = new InputFilter.LengthFilter(50);
        nameEditText.setFilters(filterArray);
        //till here

        nameEditText.append(currentName);
//till here

        //to find that is name or family edit
        String dialogTitle;
        if (isName) {
            dialogTitle = "ویرایش نام";
            nameEditText.setHint("نام شما");
        } else {
            dialogTitle = "ویرایش نام خانوادگی";
            nameEditText.setHint("نام خانوادگی شما");
        }


        final HugeNetAlertDialog alertDialog = new HugeNetAlertDialog.Builder()
                .setTitle(dialogTitle)
                .setPositiveText("ویرایش")
                .setViews(layoutNameEditText)
                .setCancellable(true)
                .build();

        alertDialog.setPositiveButtonClickListener(new OnClickListener.OnPositiveButtonClickListener() {
            @Override
            public void onClick(View v) {

                closeKeyboard();

                if (presenterCallBack.confirmEditNameClicked(nameEditText.getText().toString().trim(), isName))
                    alertDialog.dismiss();

            }
        });

        alertDialog.show(getSupportFragmentManager(), null);
    }
    //till here

    @Override
    public void setChangedName(String input, boolean isName) {

        if (isName) {
            TextView profileName = findViewById(R.id.profile_name);
            profileName.setText(input);
        } else {
            TextView profileFamily = findViewById(R.id.profile_family);
            profileFamily.setText(input);
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        Uri imageUri = presenterCallBack.getRetrievedImage(requestCode, resultCode, data);
        if (imageUri != null) {

            ImageView profilePic = findViewById(R.id.profile_pic);
            profilePic.setImageURI(imageUri);
        }

    }

}
