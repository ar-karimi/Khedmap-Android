package com.khedmap.khedmap.Order.View.OrderDetail;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.khedmap.khedmap.InitApplication;
import com.khedmap.khedmap.Order.Adapters.TeammateListAdapter;
import com.khedmap.khedmap.Order.CanceledStateContract;
import com.khedmap.khedmap.Order.DataModels.Teammate;
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

public class CanceledStateActivity extends AppCompatActivity implements CanceledStateContract.ViewCallBack {


    @Inject
    CanceledStateContract.PresenterCallBack presenterCallBack;

    @Inject
    Context mContext;

    private LinearLayout orderDetailLinearLayout;

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


        setContentView(R.layout.activity_canceled_state);

////////////////////////////////////////////////////////////////////

//init dynamic part of detail page
        orderDetailLinearLayout = findViewById(R.id.order_detail_linear_layout);


        //toolbar back icon
        findViewById(R.id.ic_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


        //to get Submitted OrderId from prev page
        presenterCallBack.setSubmittedOrderId(getIntent().getStringExtra("submittedOrderId"));

        //to get OrderDetail data from prev page
        presenterCallBack.fetchDataAndGenerateOrderDetailItems(getIntent().getStringExtra("data"));


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

        new CustomSnackbar(message, CanceledStateActivity.this, findViewById(R.id.coordinator_layout)).snackbar.show();

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
    public void setStaticPartValues(List<String> orderDetail) {

        TextView subcategory = findViewById(R.id.subcategory);
        TextView serviceTime = findViewById(R.id.service_time);
        TextView address = findViewById(R.id.address);
        TextView status = findViewById(R.id.status);


        subcategory.setText(orderDetail.get(0));
        serviceTime.setText(orderDetail.get(1));

        address.setText(orderDetail.get(2));

        status.setText(orderDetail.get(3));

    }

    @Override
    public void setPrice(String receivedPrice) {

        if (receivedPrice == null) {
            findViewById(R.id.price_layout).setVisibility(View.GONE);
            return;
        }

        TextView price = findViewById(R.id.price);

        price.setText(receivedPrice);

    }

    ///////////////////////////////////////
//to add Dynamic Detail items to view

    @Override
    public void setNoDetailItemToShow() {

        findViewById(R.id.no_detail_item_to_show).setVisibility(View.VISIBLE);
    }


    @Override
    public void generateTitleItem(String value) {

        @SuppressLint("InflateParams") LinearLayout titleItem = (LinearLayout) getLayoutInflater().inflate(R.layout.layout_dynamic_title, null, false);
        TextView dynamicTitle = titleItem.findViewById(R.id.dynamic_title);
        dynamicTitle.setText(value);
        orderDetailLinearLayout.addView(titleItem);
    }


    @Override
    public void generateAnswerItem(String value) {

        @SuppressLint("InflateParams") TextView answerItem = (TextView) getLayoutInflater().inflate(R.layout.layout_dynamic_answer, null, false);
        answerItem.setText(value);
        orderDetailLinearLayout.addView(answerItem);
    }


    @Override
    public void generateImageAnswerItem(String url) {

        @SuppressLint("InflateParams") ImageView imageAnswerItem = (ImageView) getLayoutInflater().inflate(R.layout.layout_dynamic_image_answer, null, false);
        orderDetailLinearLayout.addView(imageAnswerItem);
        Picasso.get().load(url).into(imageAnswerItem);
    }

//till here
///////////////////////////////////////


    @Override
    public void setExpertDetailValues(List<String> expertDetail) {

        if (expertDetail == null) {
            findViewById(R.id.expert_layout).setVisibility(View.GONE);
            return;
        }

        ImageView profilePic = findViewById(R.id.profile_pic);
        final SkeletonGroup skeletonGroup = findViewById(R.id.skeletonGroup);
        TextView expertName = findViewById(R.id.expert_name);


        if (!expertDetail.get(0).equals("null"))
            Picasso.get().load(expertDetail.get(0)).into(profilePic, new Callback.EmptyCallback() {
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

        expertName.setText(expertDetail.get(1));


    }


    @Override
    public void showTeammatesRecyclerView(List<Teammate> teammates) {

        RecyclerView recyclerView = findViewById(R.id.recycler_view_teammates);

        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(new TeammateListAdapter(this, teammates));
    }


    @Override
    public void setNoTeammateToShow() {

        findViewById(R.id.teammates_title).setVisibility(View.GONE);
        findViewById(R.id.recycler_view_teammates).setVisibility(View.GONE);

    }

}
