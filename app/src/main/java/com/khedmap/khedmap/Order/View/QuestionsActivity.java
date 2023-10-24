package com.khedmap.khedmap.Order.View;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatCheckBox;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.khedmap.khedmap.InitApplication;
import com.khedmap.khedmap.Order.QuestionsContract;
import com.khedmap.khedmap.R;
import com.khedmap.khedmap.Utilities.CustomSnackbar;
import com.khedmap.khedmap.di.component.DaggerActivityComponent;
import com.khedmap.khedmap.di.module.MvpModule;

import javax.inject.Inject;

import io.github.inflationx.calligraphy3.CalligraphyConfig;
import io.github.inflationx.calligraphy3.CalligraphyInterceptor;
import io.github.inflationx.viewpump.ViewPump;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class QuestionsActivity extends AppCompatActivity implements QuestionsContract.ViewCallBack {


    @Inject
    QuestionsContract.PresenterCallBack presenterCallBack;

    @Inject
    Context mContext;


    private SwipeRefreshLayout pullToRefresh;

    private LinearLayout mainLinearLayout;

    private ImageView problemPic;
    private ImageView removeImageButton;


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


        setContentView(R.layout.activity_questions);

////////////////////////////////////////////////////////////////////


        //to get selected itemId from prev page
        presenterCallBack.setSelectedItemId(getIntent().getStringExtra("itemId"));


        findViewById(R.id.ic_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                onBackPressed();
            }
        });


        mainLinearLayout = findViewById(R.id.main_linear_layout);
        mainLinearLayout.setGravity(Gravity.CENTER);
        mainLinearLayout.removeAllViews();


        //init pullToRefresh
        pullToRefresh = findViewById(R.id.pullToRefresh);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                //Refresh Questions
                presenterCallBack.loadQuestions(QuestionsActivity.this);

            }
        });


//to load Questions
        presenterCallBack.loadQuestions(QuestionsActivity.this);


        findViewById(R.id.submit_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                presenterCallBack.submitButtonClicked(QuestionsActivity.this);
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

    @Override
    public void closeKeyboard() {

        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (imm != null && mainLinearLayout.getWindowToken() != null) {
            imm.hideSoftInputFromWindow(mainLinearLayout.getWindowToken(), 0);
        }
    }


    @Override
    public void showSnackBar(String message) {

        new CustomSnackbar(message, QuestionsActivity.this, findViewById(R.id.coordinator_layout)).snackbar.show();

    }


    @Override
    public void showProgressBar(boolean showProgressBar) {
        if (showProgressBar)
            pullToRefresh.setRefreshing(true);
        else
            pullToRefresh.setRefreshing(false);

    }


    @Override
    public void setToolbarTitle(String toolbarTitle) {

        TextView toolbarTitleTextView = findViewById(R.id.toolbar_title);
        toolbarTitleTextView.setText(toolbarTitle);
    }

///////////////////////////////////////
//to add Specification items to view

    @Override
    public void generateQuestionTitle(String title) {

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        int margin_left_right_in_dp = 19;
        final float scale = getResources().getDisplayMetrics().density;
        int margin_left_right_in_px = (int) (margin_left_right_in_dp * scale + 0.5f);

        int margin_top_in_dp = 4;
        int margin_top_in_px = (int) (margin_top_in_dp * scale + 0.5f);

        layoutParams.setMargins(margin_left_right_in_px, margin_top_in_px, margin_left_right_in_px, 0);

        TextView titleItem = new TextView(this);
        titleItem.setLayoutParams(layoutParams);
        titleItem.setText(title);
        titleItem.setTypeface(Typeface.createFromAsset(getApplicationContext().getAssets(), "IRANSansMobileFonts/IRANSansMobile(FaNum)_Bold.ttf"));
        titleItem.setTextColor(Color.parseColor("#4D4D4D"));
        titleItem.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17.5f);
        titleItem.setLineSpacing(5, (float) 1.15);

        mainLinearLayout.addView(titleItem);

    }

    @Override
    public void generateQuestionHint(String hint) {

        if (hint.equals("null"))
            return;

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        int margin_left_right_in_dp = 19;
        final float scale = getResources().getDisplayMetrics().density;
        int margin_left_right_in_px = (int) (margin_left_right_in_dp * scale + 0.5f);

        int margin_top_in_dp = 45;
        int margin_top_in_px = (int) (margin_top_in_dp * scale + 0.5f);

        layoutParams.setMargins(margin_left_right_in_px, margin_top_in_px, margin_left_right_in_px, 0);

        TextView hintText = new TextView(this);
        hintText.setLayoutParams(layoutParams);
        hintText.setText(hint);
        hintText.setTypeface(Typeface.createFromAsset(getApplicationContext().getAssets(), "IRANSansMobileFonts/IRANSansMobile(FaNum).ttf"));
        hintText.setTextColor(Color.parseColor("#888888"));
        hintText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13f);
        hintText.setLineSpacing(0, (float) 1.15);

        mainLinearLayout.addView(hintText);
    }


    @SuppressLint("RestrictedApi") //to use setSupportButtonTintList for change checkBox color
    @Override
    public void generateCheckBoxAnswer(final String answerText, String answerHint, final String answerIdentification,
                                       boolean isChecked, final String required) {

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        int margin_left_right_in_dp = 30;
        final float scale = getResources().getDisplayMetrics().density;
        int margin_left_right_in_px = (int) (margin_left_right_in_dp * scale + 0.5f);

        int margin_top_in_dp = 16;
        int margin_top_in_px = (int) (margin_top_in_dp * scale + 0.5f);

        layoutParams.setMargins(margin_left_right_in_px, margin_top_in_px, margin_left_right_in_px, 0);

        CheckBox checkBoxAnswer = new AppCompatCheckBox(this);
        checkBoxAnswer.setLayoutParams(layoutParams);
        checkBoxAnswer.setText(answerText);
        checkBoxAnswer.setTypeface(Typeface.createFromAsset(getApplicationContext().getAssets(), "IRANSansMobileFonts/IRANSansMobile(FaNum).ttf"));
        checkBoxAnswer.setTextColor(Color.parseColor("#333333"));
        checkBoxAnswer.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f);
        checkBoxAnswer.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);


        checkBoxAnswer.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isSelected) {

                presenterCallBack.checkBoxOrRadioAnswerSelected(answerIdentification, isSelected, required);

            }
        });

        checkBoxAnswer.setChecked(isChecked);
        mainLinearLayout.addView(checkBoxAnswer);


        if (answerHint.equals("null"))
            return;

        //define answer hint
        LinearLayout.LayoutParams hintLayoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        int hint_margin_left_right_in_dp = 62;
        int hint_margin_left_right_in_px = (int) (hint_margin_left_right_in_dp * scale + 0.5f);

        int hint_margin_top_in_dp = 0;
        int hint_margin_top_in_px = (int) (hint_margin_top_in_dp * scale + 0.5f);

        hintLayoutParams.setMargins(hint_margin_left_right_in_px, hint_margin_top_in_px, hint_margin_left_right_in_px, 0);

        TextView hintText = new TextView(this);
        hintText.setLayoutParams(hintLayoutParams);
        hintText.setText(answerHint);
        hintText.setTypeface(Typeface.createFromAsset(getApplicationContext().getAssets(), "IRANSansMobileFonts/IRANSansMobile(FaNum).ttf"));
        hintText.setTextColor(Color.parseColor("#888888"));
        hintText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13f);
        hintText.setLineSpacing(0, (float) 1.15);

        mainLinearLayout.addView(hintText);
        //till here

    }


    @Override
    public RadioGroup generateRadioGroup() {

        RadioGroup radioGroup = new RadioGroup(this);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        radioGroup.setLayoutParams(layoutParams);

        mainLinearLayout.addView(radioGroup);

        return radioGroup;

    }

    @Override
    public void generateRadioButtonAnswer(RadioGroup radioGroup, String answerText, String answerHint,
                                          final String answerIdentification, boolean isChecked, final String required) {

        RadioGroup.LayoutParams layoutParams = new RadioGroup.LayoutParams(
                RadioGroup.LayoutParams.MATCH_PARENT, RadioGroup.LayoutParams.WRAP_CONTENT);

        int margin_left_right_in_dp = 30;
        final float scale = getResources().getDisplayMetrics().density;
        int margin_left_right_in_px = (int) (margin_left_right_in_dp * scale + 0.5f);

        int margin_top_in_dp = 16;
        int margin_top_in_px = (int) (margin_top_in_dp * scale + 0.5f);

        layoutParams.setMargins(margin_left_right_in_px, margin_top_in_px, margin_left_right_in_px, 0);

        RadioButton radioButtonAnswer = new RadioButton(this);
        radioButtonAnswer.setLayoutParams(layoutParams);
        radioButtonAnswer.setText(answerText);
        radioButtonAnswer.setTypeface(Typeface.createFromAsset(getApplicationContext().getAssets(), "IRANSansMobileFonts/IRANSansMobile(FaNum).ttf"));
        radioButtonAnswer.setTextColor(Color.parseColor("#333333"));
        radioButtonAnswer.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f);
        radioButtonAnswer.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);


        radioButtonAnswer.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isSelected) {

                presenterCallBack.checkBoxOrRadioAnswerSelected(answerIdentification, isSelected, required);

            }
        });

        radioGroup.addView(radioButtonAnswer);

        radioButtonAnswer.setChecked(isChecked);


        if (answerHint.equals("null"))
            return;

        //define answer hint
        RadioGroup.LayoutParams hintLayoutParams = new RadioGroup.LayoutParams(
                RadioGroup.LayoutParams.MATCH_PARENT, RadioGroup.LayoutParams.WRAP_CONTENT);

        int hint_margin_left_right_in_dp = 62;
        int hint_margin_left_right_in_px = (int) (hint_margin_left_right_in_dp * scale + 0.5f);

        int hint_margin_top_in_dp = 0;
        int hint_margin_top_in_px = (int) (hint_margin_top_in_dp * scale + 0.5f);

        hintLayoutParams.setMargins(hint_margin_left_right_in_px, hint_margin_top_in_px, hint_margin_left_right_in_px, 0);

        TextView hintText = new TextView(this);
        hintText.setLayoutParams(hintLayoutParams);
        hintText.setText(answerHint);
        hintText.setTypeface(Typeface.createFromAsset(getApplicationContext().getAssets(), "IRANSansMobileFonts/IRANSansMobile(FaNum).ttf"));
        hintText.setTextColor(Color.parseColor("#888888"));
        hintText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13f);
        hintText.setLineSpacing(0, (float) 1.15);

        radioGroup.addView(hintText);
        //till here
    }


    @Override
    public void generateTextBox(String insertedDescription, final String required) {

        int height_in_dp = 250;
        final float scale = getResources().getDisplayMetrics().density;
        int height_in_px = (int) (height_in_dp * scale + 0.5f);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, height_in_px);

        int margin_left_right_in_dp = 20;
        int margin_left_right_in_px = (int) (margin_left_right_in_dp * scale + 0.5f);

        int margin_top_in_dp = 16;
        int margin_top_in_px = (int) (margin_top_in_dp * scale + 0.5f);

        layoutParams.setMargins(margin_left_right_in_px, margin_top_in_px, margin_left_right_in_px, 0);

        @SuppressLint("InflateParams") LinearLayout layoutDescriptionEditText = (LinearLayout) getLayoutInflater().inflate(R.layout.layout_description_edit_text, null, false);
        layoutDescriptionEditText.setLayoutParams(layoutParams);
        EditText descriptionEditText = layoutDescriptionEditText.findViewById(R.id.description_edit_text);
        descriptionEditText.setText(insertedDescription);

        //to set TextWatcher to editText
        descriptionEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable insertedDescription) {

                presenterCallBack.textChanged(insertedDescription.toString(), required);
            }
        });
        //till here

        mainLinearLayout.addView(layoutDescriptionEditText);
    }


    @Override
    public void generateFilePicker(String selectedImageUri, final String required) {


        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);


        @SuppressLint("InflateParams") ConstraintLayout uploadFileLayout = (ConstraintLayout) getLayoutInflater().inflate(R.layout.layout_upload_file, null, false);
        uploadFileLayout.setLayoutParams(layoutParams);

        mainLinearLayout.addView(uploadFileLayout);

        problemPic = uploadFileLayout.findViewById(R.id.problem_pic);
        problemPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                presenterCallBack.problemPicClicked(QuestionsActivity.this, view);
            }
        });


        removeImageButton = uploadFileLayout.findViewById(R.id.remove_image_button);
        removeImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                presenterCallBack.imageRemoved();
                problemPic.setImageResource(R.drawable.add_image_file);
                removeImageButton.setVisibility(View.GONE);
            }
        });


        if (!selectedImageUri.isEmpty()) {
            problemPic.setImageURI(Uri.parse(selectedImageUri));
            removeImageButton.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        Uri imageUri = presenterCallBack.getRetrievedImage(requestCode, resultCode, data);
        if (imageUri == null)
            return;

        problemPic.setImageURI(imageUri);
        presenterCallBack.imageSelected(imageUri, this);
        removeImageButton.setVisibility(View.VISIBLE);

    }


    @Override
    public void generateUnknownItem() {

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        TextView unknownItem = new TextView(this);
        unknownItem.setLayoutParams(layoutParams);
        unknownItem.setText("آیتم دریافتی ناشناس!");
        mainLinearLayout.addView(unknownItem);
    }


    @Override
    public void clearList() {

        mainLinearLayout.removeAllViews();
    }

//till here
///////////////////////////////////////


    @Override
    public void onBackPressed() {

        if (presenterCallBack.backButtonClicked()) //to check is first step or not
            super.onBackPressed();
    }

    @Override
    public void enableSubmitButton(boolean isEnable) {
        if (isEnable)
            findViewById(R.id.submit_button).setBackground(ContextCompat.getDrawable(mContext, R.drawable.round_button_without_shadow));
        else
            findViewById(R.id.submit_button).setBackground(ContextCompat.getDrawable(mContext, R.drawable.round_button_without_shadow_disable));
    }
}
