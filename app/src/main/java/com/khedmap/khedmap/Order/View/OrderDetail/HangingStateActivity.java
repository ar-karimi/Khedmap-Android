package com.khedmap.khedmap.Order.View.OrderDetail;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.khedmap.khedmap.InitApplication;
import com.khedmap.khedmap.Order.Adapters.DisproofListAdapter;
import com.khedmap.khedmap.Order.DataModels.Disproof;
import com.khedmap.khedmap.Order.HangingStateContract;
import com.khedmap.khedmap.R;
import com.khedmap.khedmap.Utilities.CustomSnackbar;
import com.khedmap.khedmap.di.component.DaggerActivityComponent;
import com.khedmap.khedmap.di.module.MvpModule;
import com.squareup.picasso.Picasso;

import java.util.List;

import javax.inject.Inject;

import io.github.inflationx.calligraphy3.CalligraphyConfig;
import io.github.inflationx.calligraphy3.CalligraphyInterceptor;
import io.github.inflationx.viewpump.ViewPump;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import ir.hugenet.hugenetdialog.HugeNetAlertDialog;
import ir.hugenet.hugenetdialog.OnClickListener;

public class HangingStateActivity extends AppCompatActivity implements HangingStateContract.ViewCallBack {


    @Inject
    HangingStateContract.PresenterCallBack presenterCallBack;

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


        setContentView(R.layout.activity_hanging_state);

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


        findViewById(R.id.select_expert_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                presenterCallBack.selectExpertButtonClicked(HangingStateActivity.this);
            }
        });


        findViewById(R.id.cancel_order_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                presenterCallBack.cancelOrderButtonClicked(HangingStateActivity.this);
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

        new CustomSnackbar(message, HangingStateActivity.this, findViewById(R.id.coordinator_layout)).snackbar.show();

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


    //to show Dialogs
    @Override
    public void showDisproofsDialog(List<Disproof> disproofs) {


//to generate disproofsRecyclerView
        RecyclerView disproofsRecyclerView = new RecyclerView(HangingStateActivity.this);
        RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        disproofsRecyclerView.setLayoutParams(params);
        disproofsRecyclerView.setPadding(30, 0, 30, 35);
//till here


//to build AlertDialog
        final HugeNetAlertDialog alertDialog = new HugeNetAlertDialog.Builder()
                .setTitle("لطفاً دلیل لغو سفارش را انتخاب کنید")
                .setViews(disproofsRecyclerView)
                .setCancellable(true)
                .build();

        alertDialog.show(getSupportFragmentManager(), null);
//till here


        DisproofListAdapter.OnItemClickListener listener = new DisproofListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Disproof item) {

                presenterCallBack.disproofItemSelected(item, HangingStateActivity.this);
                alertDialog.dismiss();
            }
        };

        disproofsRecyclerView.setLayoutManager(new GridLayoutManager(this, 2, LinearLayoutManager.VERTICAL, false));
        disproofsRecyclerView.setAdapter(new DisproofListAdapter(this, disproofs, listener));
    }


    @Override
    public void showConfirmationDialog() {


        final HugeNetAlertDialog alertDialog = new HugeNetAlertDialog.Builder()
                .setTitle("آیا اطمینان دارید؟")
                .setPositiveText("بله")
                .setNegativeText("انصراف")
                .setCancellable(false)
                .build();

        alertDialog.setNegativeButtonClickListener(new OnClickListener.OnNegativeButtonClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        alertDialog.setPositiveButtonClickListener(new OnClickListener.OnPositiveButtonClickListener() {
            @Override
            public void onClick(View v) {

                presenterCallBack.rejectOrderClicked(HangingStateActivity.this);

                alertDialog.dismiss();

            }
        });

        alertDialog.show(getSupportFragmentManager(), null);

    }
//till here
}
