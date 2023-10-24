package com.khedmap.khedmap.Order.View;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.khedmap.khedmap.InitApplication;
import com.khedmap.khedmap.Order.OrderSpecificationContract;
import com.khedmap.khedmap.R;
import com.khedmap.khedmap.Utilities.CustomSnackbar;
import com.khedmap.khedmap.di.component.DaggerActivityComponent;
import com.khedmap.khedmap.di.module.MvpModule;
import com.squareup.picasso.Picasso;

import javax.inject.Inject;

import io.github.inflationx.calligraphy3.CalligraphyConfig;
import io.github.inflationx.calligraphy3.CalligraphyInterceptor;
import io.github.inflationx.viewpump.ViewPump;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class OrderSpecificationActivity extends AppCompatActivity implements OrderSpecificationContract.ViewCallBack {


    @Inject
    OrderSpecificationContract.PresenterCallBack presenterCallBack;

    @Inject
    Context mContext;


    private SwipeRefreshLayout pullToRefresh;

    private LinearLayout mainLinearLayout;

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


        setContentView(R.layout.activity_order_specification);

////////////////////////////////////////////////////////////////////


        //to get selected itemId from prev page
        presenterCallBack.setSelectedItemId(getIntent().getStringExtra("itemId"));
        //to get selected itemName from prev page
        TextView toolbarTitle = findViewById(R.id.toolbar_title);
        toolbarTitle.setText(getIntent().getStringExtra("itemName"));


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

                //Refresh Specification List
                presenterCallBack.initSpecificationList(OrderSpecificationActivity.this);

            }
        });


//init Specification List
        presenterCallBack.initSpecificationList(OrderSpecificationActivity.this);


        findViewById(R.id.submit_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                presenterCallBack.submitButtonClicked(OrderSpecificationActivity.this);
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
    public void showSnackBar(String message) {

        new CustomSnackbar(message, OrderSpecificationActivity.this, findViewById(R.id.coordinator_layout)).snackbar.show();

    }


    @Override
    public void showProgressBar(boolean showProgressBar) {
        if (showProgressBar)
            pullToRefresh.setRefreshing(true);
        else
            pullToRefresh.setRefreshing(false);

    }


    ///////////////////////////////////////
//to add Specification items to view

    @Override
    public void generateTitleItem(String value) {

        //place in FrameLayout Because can't set margins directly to Inflated Parent (only can set padding)
        @SuppressLint("InflateParams") FrameLayout titleItem = (FrameLayout) getLayoutInflater().inflate(R.layout.layout_dynamic_order_spec_title, null, false);
        TextView titleTextView = titleItem.findViewById(R.id.title_text_view);
        titleTextView.setText(value);
        mainLinearLayout.addView(titleItem);
    }

    @Override
    public void generateNormalTextItem(String value) {

        //place in FrameLayout Because can't set margins directly to Inflated Parent (only can set padding)
        @SuppressLint("InflateParams") FrameLayout normalTextItem = (FrameLayout) getLayoutInflater().inflate(R.layout.layout_dynamic_order_spec_normal_text, null, false);
        TextView normalTextView = normalTextItem.findViewById(R.id.normal_text_view);
        normalTextView.setText(value);
        mainLinearLayout.addView(normalTextItem);
    }

    @Override
    public void generateBoldTextItem(String value) {

        //place in FrameLayout Because can't set margins directly to Inflated Parent (only can set padding)
        @SuppressLint("InflateParams") FrameLayout boldTextItem = (FrameLayout) getLayoutInflater().inflate(R.layout.layout_dynamic_order_spec_bold_text, null, false);
        TextView boldTextView = boldTextItem.findViewById(R.id.bold_text_view);
        boldTextView.setText(value);
        mainLinearLayout.addView(boldTextItem);
    }

    @Override
    public void generateWarningItem(String value) {

        //place in FrameLayout Because can't set margins directly to Inflated Parent (only can set padding)
        @SuppressLint("InflateParams") FrameLayout warningItem = (FrameLayout) getLayoutInflater().inflate(R.layout.layout_dynamic_order_spec_warning, null, false);
        TextView warningTextView = warningItem.findViewById(R.id.warning_text_view);
        warningTextView.setText(value);
        mainLinearLayout.addView(warningItem);
    }

    @Override
    public void generateBorderedItem(String value) { //Bordered Warning

        //place in FrameLayout Because can't set margins directly to Inflated Parent (only can set padding)
        @SuppressLint("InflateParams") FrameLayout borderedItem = (FrameLayout) getLayoutInflater().inflate(R.layout.layout_dynamic_order_spec_bordered, null, false);
        TextView borderedTextView = borderedItem.findViewById(R.id.bordered_text_view);
        borderedTextView.setText(value);
        mainLinearLayout.addView(borderedItem);
    }

    @Override
    public void generateBorderedTextItem(String value) {

        //place in FrameLayout Because can't set margins directly to Inflated Parent (only can set padding)
        @SuppressLint("InflateParams") FrameLayout borderedTextItem = (FrameLayout) getLayoutInflater().inflate(R.layout.layout_dynamic_order_spec_bordered_text, null, false);
        TextView borderedTextView = borderedTextItem.findViewById(R.id.bordered_text_view);
        borderedTextView.setText(value);
        mainLinearLayout.addView(borderedTextItem);
    }

    @Override
    public void generateHintItem(String value) {

        //place in FrameLayout Because can't set margins directly to Inflated Parent (only can set padding)
        @SuppressLint("InflateParams") FrameLayout hintItem = (FrameLayout) getLayoutInflater().inflate(R.layout.layout_dynamic_order_spec_hint, null, false);
        TextView hintTextView = hintItem.findViewById(R.id.hint_text_view);
        hintTextView.setText(value);
        mainLinearLayout.addView(hintItem);
    }

    @Override
    public void generateListItem(String value) {

        String[] listItems = value.split("\n");

        for (String listItemText : listItems) {

            //place in FrameLayout Because can't set margins directly to Inflated Parent (only can set padding)
            @SuppressLint("InflateParams") LinearLayout listItem = (LinearLayout) getLayoutInflater().inflate(R.layout.layout_dynamic_order_spec_list, null, false);
            TextView listTextView = listItem.findViewById(R.id.list_text_view);
            listTextView.setText(listItemText);
            mainLinearLayout.addView(listItem);
        }
    }

    @Override
    public void generateImageItem(String url) {

        //place in FrameLayout Because can't set margins directly to Inflated Parent (only can set padding)
        @SuppressLint("InflateParams") FrameLayout imageItem = (FrameLayout) getLayoutInflater().inflate(R.layout.layout_dynamic_order_spec_image, null, false);
        ImageView roundedImageView = imageItem.findViewById(R.id.rounded_image_view);
        mainLinearLayout.addView(imageItem);

        Picasso.get().load(url).into(roundedImageView);
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
}
