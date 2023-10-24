package com.khedmap.khedmap.Order.View;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.khedmap.khedmap.InitApplication;
import com.khedmap.khedmap.Order.VerifyFinishOrderContract;
import com.khedmap.khedmap.R;
import com.khedmap.khedmap.Utilities.CustomSnackbar;
import com.khedmap.khedmap.Utilities.UtilitiesSingleton;
import com.khedmap.khedmap.di.component.DaggerActivityComponent;
import com.khedmap.khedmap.di.module.MvpModule;
import com.squareup.picasso.Picasso;

import javax.inject.Inject;

import io.github.inflationx.calligraphy3.CalligraphyConfig;
import io.github.inflationx.calligraphy3.CalligraphyInterceptor;
import io.github.inflationx.viewpump.ViewPump;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import ir.hugenet.hugenetdialog.HugeNetAlertDialog;
import ir.hugenet.hugenetdialog.OnClickListener;

public class VerifyFinishOrderActivity extends AppCompatActivity implements VerifyFinishOrderContract.ViewCallBack {


    @Inject
    VerifyFinishOrderContract.PresenterCallBack presenterCallBack;

    @Inject
    Context mContext;

    private boolean isFirstTime = true;
    private HugeNetAlertDialog paymentTypeAlertDialog;
    private HugeNetAlertDialog favoriteLocationAlertDialog;


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


        setContentView(R.layout.activity_verify_finish_order);

////////////////////////////////////////////////////////////////////


        //to get finished order detail from notification
        presenterCallBack.setFinishedOrderDetail(getIntent().getStringExtra("orderId")
                , getIntent().getStringExtra("subcategory")
                , getIntent().getIntExtra("finalPrice", 0)
                , getIntent().getStringExtra("factorPicture"));


        findViewById(R.id.no_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                presenterCallBack.noButtonClicked(VerifyFinishOrderActivity.this);

            }
        });


        findViewById(R.id.yes_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                presenterCallBack.verifyFinishOrderClicked("accept", VerifyFinishOrderActivity.this);

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

        new CustomSnackbar(message, VerifyFinishOrderActivity.this, findViewById(R.id.coordinator_layout)).snackbar.show();

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
    public void showPageItems(String subcategory, String finalPrice, String factorPictureUrl) {


        TextView subcategoryTitle = findViewById(R.id.subcategory);
        TextView price = findViewById(R.id.final_price);
        ImageView factorImage = findViewById(R.id.factor_image);

        subcategoryTitle.setText(subcategory);
        price.setText(finalPrice);

        Picasso.get().load(factorPictureUrl).into(factorImage);

    }

    //to show Dialogs
    @Override
    public void showNoButtonDialog() {

        final HugeNetAlertDialog alertDialog = new HugeNetAlertDialog.Builder()
                .setTitle("آیا اطمینان دارید؟")
                .setPositiveText("بله")
                .setNegativeText("انصراف")
                .setCancellable(true)
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

                presenterCallBack.verifyFinishOrderClicked("decline", VerifyFinishOrderActivity.this);

                alertDialog.dismiss();

            }
        });

        alertDialog.show(getSupportFragmentManager(), null);
    }


    @Override
    public void showPaymentTypeDialog(int currentCredit) {

        paymentTypeAlertDialog = new HugeNetAlertDialog.Builder()
                .setTitle("لطفاً روش پرداخت خود را انتخاب کنید")
                .setSubtitle("اعتبار فعلی : " + UtilitiesSingleton.getInstance().convertPrice(currentCredit))
                .setPositiveText("آنلاین")
                .setNegativeText("نقدی")
                .setCancellable(false)
                .build();

        paymentTypeAlertDialog.setNegativeButtonClickListener(new OnClickListener.OnNegativeButtonClickListener() {
            @Override
            public void onClick(View v) {

                presenterCallBack.paymentTypeButtonClicked("offline", VerifyFinishOrderActivity.this);

            }
        });

        paymentTypeAlertDialog.setPositiveButtonClickListener(new OnClickListener.OnPositiveButtonClickListener() {
            @Override
            public void onClick(View v) {

                presenterCallBack.paymentTypeButtonClicked("online", VerifyFinishOrderActivity.this);

            }
        });

        paymentTypeAlertDialog.show(getSupportFragmentManager(), null);
    }

    @Override
    public void showFavoriteLocationDialog() {

        //can't set margins directly to Inflated Parent (only can set padding)
        @SuppressLint("InflateParams") LinearLayout linearLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.layout_add_to_favorite_location_dialog, null, false);
        final EditText favoriteLocationNameEditText = linearLayout.findViewById(R.id.favorite_location_name_edit_text);

        //to build AlertDialog
        favoriteLocationAlertDialog = new HugeNetAlertDialog.Builder()
                .setTitle("افزودن به مکان\u200Cهای مورد علاقه")
                .setSubtitle("اگر مایل به افزودن این مکان به موارد مورد علاقه\u200Cی خود هستید، یک نام (مثل خانه، شرکت یا ...) برای آن انتخاب کنید")
                .setPositiveText("ثبت")
                .setNegativeText("تمایلی ندارم")
                .setViews(linearLayout)
                .setCancellable(false)
                .build();

        favoriteLocationAlertDialog.setNegativeButtonClickListener(new OnClickListener.OnNegativeButtonClickListener() {
            @Override
            public void onClick(View v) {

                presenterCallBack.addFavoriteLocationClicked(VerifyFinishOrderActivity.this, null);
            }
        });

        favoriteLocationAlertDialog.setPositiveButtonClickListener(new OnClickListener.OnPositiveButtonClickListener() {
            @Override
            public void onClick(View v) {

                presenterCallBack.addFavoriteLocationClicked(VerifyFinishOrderActivity.this
                        , favoriteLocationNameEditText.getText().toString().trim());

            }
        });

        favoriteLocationAlertDialog.show(getSupportFragmentManager(), null);
    }

//till here

    @Override
    public void dismissPaymentTypeDialog() {

        paymentTypeAlertDialog.dismiss();
    }

    @Override
    public void dismissFavoriteLocationDialog() {

        favoriteLocationAlertDialog.dismiss();
    }

    @Override
    protected void onPause() {

        //to hide dialog if page minimized and at onStart called checkPayedOrder and show again if needed
        if (paymentTypeAlertDialog != null)
            dismissPaymentTypeDialog();

        super.onPause();
    }

    @Override
    protected void onStart() {

        //to send request after return from paymentWeb (hasn't problem if send even on first time just is useless)
        if (isFirstTime)
            isFirstTime = false;
        else
            presenterCallBack.checkPayedOrder(VerifyFinishOrderActivity.this);

        super.onStart();
    }

    @Override
    public void showToast(String message) {

        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}
