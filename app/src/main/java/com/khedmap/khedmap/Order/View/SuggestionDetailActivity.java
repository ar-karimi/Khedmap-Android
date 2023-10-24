package com.khedmap.khedmap.Order.View;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.khedmap.khedmap.InitApplication;
import com.khedmap.khedmap.Order.SuggestionDetailContract;
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

public class SuggestionDetailActivity extends AppCompatActivity implements SuggestionDetailContract.ViewCallBack {


    @Inject
    SuggestionDetailContract.PresenterCallBack presenterCallBack;

    @Inject
    Context mContext;

    private LinearLayout additionalServicesLinearLayout;


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


        setContentView(R.layout.activity_suggestion_detail);

////////////////////////////////////////////////////////////////////


        //to get selected itemId and submittedOrderId from prev page
        presenterCallBack.setSelectedItemId(getIntent().getStringExtra("SelectedItemId"),
                getIntent().getStringExtra("submittedOrderId"));


        //init dynamic part of page (additionalServices)
        additionalServicesLinearLayout = findViewById(R.id.additional_services);


//init Specification List
        presenterCallBack.initPageItems(SuggestionDetailActivity.this);


//toolbar back icon
        findViewById(R.id.ic_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
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

        new CustomSnackbar(message, SuggestionDetailActivity.this, findViewById(R.id.constraint_layout)).snackbar.show();

    }


    @Override
    public void showProgressBar(boolean showProgressBar) {
        ProgressBar progressBar = findViewById(R.id.progress_bar);
        LinearLayout mainLinearLayout = findViewById(R.id.main_linear_layout);

        if (showProgressBar) {
            mainLinearLayout.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
        } else {
            progressBar.setVisibility(View.GONE);
            mainLinearLayout.setVisibility(View.VISIBLE);
        }
    }


    @Override
    public void setStaticPartValues(List<String> suggestionDetail) {


        //set clickListener here to sure that response is success
        findViewById(R.id.submit_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenterCallBack.submitButtonClicked(SuggestionDetailActivity.this);
            }
        });



        ImageView expertAvatar = findViewById(R.id.expert_avatar);
        final SkeletonGroup skeletonGroup = findViewById(R.id.skeletonGroup);
        TextView expertName = findViewById(R.id.expert_name);
        TextView expertRate = findViewById(R.id.expert_rate);
        TextView basePrice = findViewById(R.id.base_price);

        TextView descriptionTitle = findViewById(R.id.description_title);
        TextView description = findViewById(R.id.description);

        TextView teammate = findViewById(R.id.teammate);


        if (!suggestionDetail.get(0).equals("null"))
            Picasso.get().load(suggestionDetail.get(0)).into(expertAvatar, new Callback.EmptyCallback() {
                @Override
                public void onSuccess() {

                    skeletonGroup.setShowSkeleton(false);
                    skeletonGroup.finishAnimation();
                }
            });

        expertName.setText(suggestionDetail.get(1));
        expertRate.setText(suggestionDetail.get(2));
        basePrice.setText(suggestionDetail.get(3));

        //description (is optional)
        if (suggestionDetail.get(4).equals("")) {
            descriptionTitle.setVisibility(View.GONE);
            description.setVisibility(View.GONE);
        } else {
            description.setText(suggestionDetail.get(4));
        }

        teammate.setText(suggestionDetail.get(5));


    }


    ///////////////////////////////////////
//to add Dynamic items to view

    @SuppressLint({"ResourceType", "SetTextI18n"})
    @Override
    public void generateAdditionalServiceItem(int id, String title, String price) {

        @SuppressLint("InflateParams") LinearLayout additionalService = (LinearLayout) getLayoutInflater().inflate(R.layout.layout_additional_service, null, false);
        additionalServicesLinearLayout.addView(additionalService);

        //get textView from generated view
        TextView additionalServiceTitle = findViewById(R.id.additional_service_title);
        additionalServiceTitle.setId(id); //to set generated textView unique to avoid of conflict
        additionalServiceTitle.setText(title + " : ");

        //get textView from generated view
        TextView additionalServicePrice = findViewById(R.id.additional_service_price);
        additionalServicePrice.setId(id + 100); //to set generated textView unique to avoid of conflict
        additionalServicePrice.setText(price);

    }

    @Override
    public void setNoAdditionalService() {

        findViewById(R.id.additional_services_divider).setVisibility(View.GONE);
        findViewById(R.id.additional_services_title).setVisibility(View.GONE);
        findViewById(R.id.additional_services).setVisibility(View.GONE);

    }
}
