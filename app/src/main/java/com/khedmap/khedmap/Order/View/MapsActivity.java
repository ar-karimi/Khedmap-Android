package com.khedmap.khedmap.Order.View;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.khedmap.khedmap.InitApplication;
import com.khedmap.khedmap.Order.MapsContract;
import com.khedmap.khedmap.R;
import com.khedmap.khedmap.Utilities.CustomSnackbar;
import com.khedmap.khedmap.di.component.DaggerActivityComponent;
import com.khedmap.khedmap.di.module.MvpModule;

import javax.inject.Inject;

import io.github.inflationx.calligraphy3.CalligraphyConfig;
import io.github.inflationx.calligraphy3.CalligraphyInterceptor;
import io.github.inflationx.viewpump.ViewPump;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, MapsContract.ViewCallBack {

    @Inject
    MapsContract.PresenterCallBack presenterCallBack;

    @Inject
    Context mContext;


    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationClient;

    private ImageView confirmButton;
    private Marker userMarker = null;

    private Button submitButton;

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


        setContentView(R.layout.activity_maps);

////////////////////////////////////////////////////////////////////


        //to get selected itemId and submittedOrderId from prev page (may be null -from EditFavoriteLocation Activity)
        presenterCallBack.setSelectedItemId(getIntent().getStringExtra("suggestionId"),
                getIntent().getStringExtra("submittedOrderId"));


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

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

        new CustomSnackbar(message, MapsActivity.this, findViewById(R.id.map)).snackbar.show();

    }


    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMinZoomPreference(8);
        mMap.setMaxZoomPreference(20);

        //to move Camera to prevLatLng (Received from server)
        LatLng prevLatLng = new LatLng(Double.parseDouble(getIntent().getStringExtra("prevLatitude")),
                Double.parseDouble(getIntent().getStringExtra("prevLongitude")));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(prevLatLng, 15));

        //to enableMyLocationButton
        presenterCallBack.getLocationPermission(MapsActivity.this);


        //confirm image marker
        confirmButton = findViewById(R.id.confirm_button);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final LatLng centerOfMap = mMap.getCameraPosition().target;

                userMarker = mMap.addMarker(new MarkerOptions().position(centerOfMap).icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_khedmap_logo)));
                confirmButton.setVisibility(View.GONE);

                presenterCallBack.setSelectedLatLng(centerOfMap);

                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(centerOfMap.latitude + 0.0001,
                        centerOfMap.longitude), mMap.getCameraPosition().zoom - (float) 0.5));

            }
        });


        //submit Button
        submitButton = findViewById(R.id.submit_button);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                presenterCallBack.submitButtonClicked(MapsActivity.this);

            }
        });


    }


    @Override
    public void enableMyLocationButton() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            return;

        mMap.setMyLocationEnabled(true);

/*

        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {

                            if (isFirstTime) {
                                mMap.animateCamera(CameraUpdateFactory
                                        .newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 17));
                                isFirstTime = false;
                            }
                        }
                    }
                });

*/
    }


    @Override
    public void onBackPressed() {


        if (userMarker != null) {
            confirmButton.setVisibility(View.VISIBLE);
            userMarker.remove();
            userMarker = null;
            presenterCallBack.setSelectedLatLng(null);
        } else {
            setResult(Activity.RESULT_CANCELED); //to EditFavoriteLocation Activity
            finish();
        }
    }


    @Override
    public void enableSubmitButton(boolean isEnable) {
        if (isEnable) {
            submitButton.setBackground(ContextCompat.getDrawable(mContext, R.drawable.round_button_without_shadow));
            submitButton.setText("تأیید مکان انتخاب شده");
        } else {
            submitButton.setBackground(ContextCompat.getDrawable(mContext, R.drawable.round_button_without_shadow_disable));
            submitButton.setText("مکان مورد نظر خود را مشخص کنید");
        }
    }


}

