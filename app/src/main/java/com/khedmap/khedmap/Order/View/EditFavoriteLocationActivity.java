package com.khedmap.khedmap.Order.View;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.khedmap.khedmap.InitApplication;
import com.khedmap.khedmap.Order.Adapters.DistrictListAdapter;
import com.khedmap.khedmap.Order.Adapters.ZoneListAdapter;
import com.khedmap.khedmap.Order.DataModels.District;
import com.khedmap.khedmap.Order.DataModels.Zone;
import com.khedmap.khedmap.Order.EditFavoriteLocationContract;
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

public class EditFavoriteLocationActivity extends AppCompatActivity implements EditFavoriteLocationContract.ViewCallBack {


    @Inject
    EditFavoriteLocationContract.PresenterCallBack presenterCallBack;

    @Inject
    Context mContext;


    public static final String KEY_FAVORITE_LOCATION_ID = "favoriteLocationId";

    private final int REQUEST_CODE_SPEECH_RECOGNIZER = 3000;
    public static final int REQUEST_CODE_MAP = 5000;
    public static final String KEY_NEW_LATITUDE = "newLatitude";
    public static final String KEY_NEW_LONGITUDE = "newLongitude";

    private EditText nameEditText;
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


        setContentView(R.layout.activity_edit_favorite_location);

////////////////////////////////////////////////////////////////////


        findViewById(R.id.ic_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                onBackPressed();
            }
        });


        nameEditText = findViewById(R.id.name_edit_text);
        addressEditText = findViewById(R.id.address_edit_text);


        //to get favoriteLocationId from prev page
        presenterCallBack.setFavoriteLocationId(getIntent().getStringExtra(KEY_FAVORITE_LOCATION_ID));

        //init Page Items
        presenterCallBack.initPageItems(this);

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

        new CustomSnackbar(message, EditFavoriteLocationActivity.this, findViewById(R.id.coordinator_layout)).snackbar.show();

    }

    @Override
    public void showToast(String message) {

        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
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

                presenterCallBack.zoneItemSelected(item, EditFavoriteLocationActivity.this);
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
    public void enableDistrictButton(boolean isEnable) {
        if (isEnable)
            findViewById(R.id.select_district_button).setBackground(ContextCompat.getDrawable(mContext, R.drawable.round_button_primary_dark));
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

    @Override
    public void setPageValues(String zone, String district, String name, String address) {


        //set clickListeners here to sure that response is success
        findViewById(R.id.select_zone_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                presenterCallBack.selectZoneButtonClicked(EditFavoriteLocationActivity.this);
            }
        });

        findViewById(R.id.select_district_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                presenterCallBack.getDistrictButtonClicked(EditFavoriteLocationActivity.this);
            }
        });

        findViewById(R.id.submit_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                presenterCallBack.submitButtonClicked(EditFavoriteLocationActivity.this
                        , nameEditText.getText().toString().trim()
                        , addressEditText.getText().toString().trim());
            }
        });

        //Speech Recognition button
        findViewById(R.id.ic_mic).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startSpeechRecognizer();
            }
        });

        findViewById(R.id.map_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                presenterCallBack.mapButtonClicked(EditFavoriteLocationActivity.this);
            }
        });


        TextView zoneButtonTitle = findViewById(R.id.zone_button_title);
        TextView districtButtonTitle = findViewById(R.id.district_button_title);

        zoneButtonTitle.setText(zone);
        districtButtonTitle.setText(district);

        nameEditText.append(name);
        addressEditText.append(address);

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
        } else if (requestCode == REQUEST_CODE_MAP) {
            if (resultCode == RESULT_OK) {

                presenterCallBack.setNewLatLng(data.getStringExtra(KEY_NEW_LATITUDE), data.getStringExtra(KEY_NEW_LONGITUDE));
                showSnackBar("موقعیت جدید انتخاب شد");
            }
        }
    }

}
