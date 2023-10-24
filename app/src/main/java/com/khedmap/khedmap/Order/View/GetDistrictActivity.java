package com.khedmap.khedmap.Order.View;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.khedmap.khedmap.InitApplication;
import com.khedmap.khedmap.Order.Adapters.DistrictListAdapter;
import com.khedmap.khedmap.Order.Adapters.FavoriteLocationListAdapter;
import com.khedmap.khedmap.Order.Adapters.ZoneListAdapter;
import com.khedmap.khedmap.Order.DataModels.District;
import com.khedmap.khedmap.Order.DataModels.FavoriteLocation;
import com.khedmap.khedmap.Order.DataModels.Zone;
import com.khedmap.khedmap.Order.GetDistrictContract;
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
import ir.hugenet.hugenetdialog.HugeNetAlertDialog;
import ir.hugenet.hugenetdialog.OnClickListener;

public class GetDistrictActivity extends AppCompatActivity implements GetDistrictContract.ViewCallBack {


    @Inject
    GetDistrictContract.PresenterCallBack presenterCallBack;

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


        setContentView(R.layout.activity_get_district);

////////////////////////////////////////////////////////////////////


        //to get Received Meta And Answers from prev page
        Bundle args = getIntent().getBundleExtra("answersBundle");
        presenterCallBack.setReceivedMetaAndAnswers((List<List<String>>) args.getSerializable("answersList"),
                getIntent().getStringExtra("subCategoryId")
                , getIntent().getStringExtra("selectedDate"),
                getIntent().getStringExtra("selectedHour"));


        //init FavoriteLocations RecyclerView and Zones List
        presenterCallBack.initPageLists(this);


        findViewById(R.id.ic_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                onBackPressed();
            }
        });

        findViewById(R.id.cancel_favorite_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                presenterCallBack.cancelFavoriteButtonClicked();
            }
        });

        findViewById(R.id.select_zone_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                presenterCallBack.selectZoneButtonClicked(GetDistrictActivity.this);
            }
        });

        findViewById(R.id.select_district_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                presenterCallBack.getDistrictButtonClicked(GetDistrictActivity.this);
            }
        });

        findViewById(R.id.submit_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                presenterCallBack.submitButtonClicked(GetDistrictActivity.this);
            }
        });

        findViewById(R.id.discount_code).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                presenterCallBack.discountCodeButtonClicked();
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

        new CustomSnackbar(message, GetDistrictActivity.this, findViewById(R.id.constraint_layout)).snackbar.show();

    }


    @Override
    public void showFavoriteLocationsRecyclerView(List<FavoriteLocation> favoriteLocations) {

        RecyclerView recyclerView = findViewById(R.id.recycler_view_favorite_locations);
        FavoriteLocationListAdapter.OnItemClickListener listener = new FavoriteLocationListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(FavoriteLocation item) {

                presenterCallBack.favoriteLocationItemSelected(item);
            }
        };

        recyclerView.setLayoutManager(new GridLayoutManager(this, 3, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(new FavoriteLocationListAdapter(this, favoriteLocations, listener));
    }


    @Override
    public void setNoItemTextVisible() {

        findViewById(R.id.no_item_exist).setVisibility(View.VISIBLE);
    }


    ///////////////
//to show Dialogs
    @Override
    public void showZonesDialog(List<Zone> zones) {


//to generate zonesRecyclerView
        RecyclerView zonesRecyclerView = new RecyclerView(this);
        RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        zonesRecyclerView.setLayoutParams(params);
        zonesRecyclerView.setPadding(30, 0, 30, 35);
//till here


//to build AlertDialog
        final HugeNetAlertDialog alertDialog = new HugeNetAlertDialog.Builder()
                .setTitle("لطفاً منطقه خود را انتخاب کنید")
                .setViews(zonesRecyclerView)
                .setCancellable(true)
                .build();

        alertDialog.show(getSupportFragmentManager(), null);
//till here


        ZoneListAdapter.OnItemClickListener listener = new ZoneListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Zone item) {

                presenterCallBack.zoneItemSelected(item, GetDistrictActivity.this);
                alertDialog.dismiss();
            }
        };

        zonesRecyclerView.setLayoutManager(new GridLayoutManager(this, 3, LinearLayoutManager.VERTICAL, false));
        zonesRecyclerView.setAdapter(new ZoneListAdapter(this, zones, listener));
    }

    @Override
    public void showDistrictsDialog(List<District> districts) {


//to generate districtsRecyclerView
        RecyclerView districtsRecyclerView = new RecyclerView(this);
        RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        districtsRecyclerView.setLayoutParams(params);
        districtsRecyclerView.setPadding(30, 0, 30, 35);
//till here


//to build AlertDialog
        final HugeNetAlertDialog alertDialog = new HugeNetAlertDialog.Builder()
                .setTitle("لطفاً محله خود را انتخاب کنید")
                .setViews(districtsRecyclerView)
                .setCancellable(true)
                .build();

        alertDialog.show(getSupportFragmentManager(), null);
//till here


        DistrictListAdapter.OnItemClickListener listener = new DistrictListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(District item) {

                presenterCallBack.districtItemSelected(item);
                alertDialog.dismiss();
            }
        };

        districtsRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        districtsRecyclerView.setAdapter(new DistrictListAdapter(this, districts, listener));
    }

////////////
//till here


    @Override
    public void enableSubmitButton(boolean isEnable) {
        if (isEnable)
            findViewById(R.id.submit_button).setBackground(ContextCompat.getDrawable(mContext, R.drawable.round_button_without_shadow));
        else
            findViewById(R.id.submit_button).setBackground(ContextCompat.getDrawable(mContext, R.drawable.round_button_without_shadow_disable));
    }


    @Override
    public void showLoadingView(boolean isShow) {
        if (isShow) {
            findViewById(R.id.loading_view).setVisibility(View.VISIBLE);
            findViewById(R.id.submit_button).setVisibility(View.GONE);
        } else {
            findViewById(R.id.loading_view).setVisibility(View.GONE);
            findViewById(R.id.submit_button).setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void showSubmitOrderLoadingView(boolean isShow) {
        if (isShow) {
            findViewById(R.id.loading_view_submit_order).setVisibility(View.VISIBLE);
            findViewById(R.id.submit_button).setVisibility(View.GONE);
        } else {
            findViewById(R.id.loading_view_submit_order).setVisibility(View.GONE);
            findViewById(R.id.submit_button).setVisibility(View.VISIBLE);
        }
    }


    @Override
    public void showCancelFavoriteButton(boolean isShow) {
        if (isShow)
            findViewById(R.id.cancel_favorite_button).setVisibility(View.VISIBLE);
        else
            findViewById(R.id.cancel_favorite_button).setVisibility(View.GONE);

    }

    @Override
    public void enableZoneButton(boolean isEnable) {
        if (isEnable)
            findViewById(R.id.select_zone_button).setBackground(ContextCompat.getDrawable(mContext, R.drawable.round_button_without_shadow));
        else
            findViewById(R.id.select_zone_button).setBackground(ContextCompat.getDrawable(mContext, R.drawable.round_button_without_shadow_disable));
    }

    @Override
    public void enableDistrictButton(boolean isEnable) {
        if (isEnable)
            findViewById(R.id.select_district_button).setBackground(ContextCompat.getDrawable(mContext, R.drawable.round_button_without_shadow));
        else
            findViewById(R.id.select_district_button).setBackground(ContextCompat.getDrawable(mContext, R.drawable.round_button_without_shadow_disable));
    }


    @Override
    public void showDistrictButtonProgressBar(boolean isShow) {
        if (isShow) {
            findViewById(R.id.district_button_title).setVisibility(View.GONE);
            findViewById(R.id.district_button_progressbar).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.district_button_progressbar).setVisibility(View.GONE);
            findViewById(R.id.district_button_title).setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void setZoneButtonTitle(String title) {

        TextView zoneButtonTitle = findViewById(R.id.zone_button_title);
        zoneButtonTitle.setText(title);
    }

    @Override
    public void setDistrictButtonTitle(String title) {

        TextView districtButtonTitle = findViewById(R.id.district_button_title);
        districtButtonTitle.setText(title);
    }


    //to show discountCode Dialog
    @Override
    public void showDiscountCodeDialog(String currentCode) {

//to generate codeEditText (generate in java because can set margins for it)
        final float scale = getResources().getDisplayMetrics().density;
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        int margin_left_right_in_dp = 20;
        int margin_left_right_in_px = (int) (margin_left_right_in_dp * scale + 0.5f);

        int margin_Bottom_in_dp = 16;
        int margin_Bottom_in_px = (int) (margin_Bottom_in_dp * scale + 0.5f);

        layoutParams.setMargins(margin_left_right_in_px, 0, margin_left_right_in_px, margin_Bottom_in_px);


        LinearLayout layoutCodeEditText = new LinearLayout(this);
        layoutCodeEditText.setOrientation(LinearLayout.VERTICAL);

        final EditText codeEditText = new EditText(this);
        layoutCodeEditText.addView(codeEditText, layoutParams);

        int padding_left_right_in_dp = 12;
        int padding_left_right_in_px = (int) (padding_left_right_in_dp * scale + 0.5f);

        int padding_Top_Bottom_in_dp = 8;
        int padding_Top_Bottom_in_px = (int) (padding_Top_Bottom_in_dp * scale + 0.5f);
        codeEditText.setPadding(padding_left_right_in_px, padding_Top_Bottom_in_px, padding_left_right_in_px, padding_Top_Bottom_in_px);

        codeEditText.setBackgroundResource(R.drawable.bordered_edit_text);
        codeEditText.setTypeface(Typeface.createFromAsset(getApplicationContext().getAssets(), "IRANSansMobileFonts/IRANSansMobile(FaNum).ttf"));
        codeEditText.setTextColor(Color.parseColor("#333333"));
        codeEditText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f);
        codeEditText.setHint("کد تخفیف");

        codeEditText.append(currentCode);
//till here


        final HugeNetAlertDialog alertDialog = new HugeNetAlertDialog.Builder()
                .setTitle("ثبت کد تخفیف")
                .setPositiveText("ثبت")
                .setViews(layoutCodeEditText)
                .setCancellable(true)
                .build();

        alertDialog.setPositiveButtonClickListener(new OnClickListener.OnPositiveButtonClickListener() {
            @Override
            public void onClick(View v) {

                closeKeyboard();

                presenterCallBack.submitDiscountCodeClicked(codeEditText.getText().toString().trim());

                alertDialog.dismiss();

            }
        });

        alertDialog.show(getSupportFragmentManager(), null);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void setChangedCode(String input) {

        if (input.isEmpty()) {
            TextView discountCode = findViewById(R.id.discount_code);
            discountCode.setText("کد تخفیف دارید؟");
        } else {
            TextView discountCode = findViewById(R.id.discount_code);
            discountCode.setText("کد تخفیف : " + input);
        }

    }

    public void closeKeyboard() {

        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (imm != null && findViewById(R.id.constraint_layout).getWindowToken() != null) {
            imm.hideSoftInputFromWindow(findViewById(R.id.constraint_layout).getWindowToken(), 0);
        }
    }
}
