package com.khedmap.khedmap.Order.Presenter;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.CompositeMultiplePermissionsListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.karumi.dexter.listener.multi.SnackbarOnAnyDeniedMultiplePermissionsListener;
import com.khedmap.khedmap.Order.MapsContract;
import com.khedmap.khedmap.Order.Model.MapsModel;
import com.khedmap.khedmap.Order.View.EditFavoriteLocationActivity;
import com.khedmap.khedmap.Order.View.GetDetailedAddressActivity;
import com.khedmap.khedmap.R;

import java.util.List;

public class MapsPresenter implements MapsContract.PresenterCallBack {

    private MapsContract.ViewCallBack view;
    private MapsModel model;


    private String submittedOrderId; //order id
    private String suggestionId; //suggestion id


    private LatLng selectedLatLng = null;

    public MapsPresenter(MapsContract.ViewCallBack view, MapsModel model) {
        this.model = model;
        this.view = view;
    }

    @Override
    public void setSelectedItemId(String suggestionId, String submittedOrderId) {

        this.suggestionId = suggestionId;
        this.submittedOrderId = submittedOrderId;
    }


    @Override
    public void getLocationPermission(Activity activity) {


        //build mainListener for notify permission granted
        MultiplePermissionsListener mainMultiplePermissionsListener = new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport report) {
                if (report.areAllPermissionsGranted()) {

                    //to enableMyLocationButton
                    view.enableMyLocationButton();

                }
            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {/* ... */}
        };
        //till here

        //build snackBarListener for handle rejecting permission by user
        MultiplePermissionsListener snackbarMultiplePermissionsListener =
                SnackbarOnAnyDeniedMultiplePermissionsListener.Builder
                        .with(activity.findViewById(R.id.map), "برای نمایش مکان فعلی لطفاً دسترسی لوکیشن را در تنظیمات فعال کنید")
                        .withOpenSettingsButton("تنظیمات")
                        .withCallback(new Snackbar.Callback() {
                            @Override
                            public void onShown(Snackbar snackbar) {
                                // Event handler for when the given Snackbar has been dismissed
                            }

                            @Override
                            public void onDismissed(Snackbar snackbar, int event) {
                                // Event handler for when the given Snackbar is visible
                            }
                        })
                        .withDuration(7000)
                        .build();
        //till here

        //Composite 2 Listener in one
        MultiplePermissionsListener compositePermissionsListener = new CompositeMultiplePermissionsListener(snackbarMultiplePermissionsListener,
                mainMultiplePermissionsListener);

        Dexter.withActivity(activity)
                .withPermissions(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                ).withListener(compositePermissionsListener)
                .withErrorListener(new PermissionRequestErrorListener() {
                    @Override
                    public void onError(DexterError error) {
                        Log.e("PermissionError", error.toString());
                    }
                })
                .check();


    }


    @Override
    public void setSelectedLatLng(LatLng selectedLatLng) {

        this.selectedLatLng = selectedLatLng;

        if (selectedLatLng != null)
            view.enableSubmitButton(true);
        else
            view.enableSubmitButton(false);
    }

    @Override
    public void submitButtonClicked(Activity activity) {

        if (selectedLatLng == null)
            return;


        if (submittedOrderId == null) { //to check come from EditFavoriteLocation or not
            activity.setResult(Activity.RESULT_OK, new Intent()
                    .putExtra(EditFavoriteLocationActivity.KEY_NEW_LATITUDE, String.valueOf(selectedLatLng.latitude))
                    .putExtra(EditFavoriteLocationActivity.KEY_NEW_LONGITUDE, String.valueOf(selectedLatLng.longitude)));
            activity.finish();
        } else
            activity.startActivity(new Intent(activity, GetDetailedAddressActivity.class)
                    .putExtra("suggestionId", suggestionId).putExtra("submittedOrderId", submittedOrderId)
                    .putExtra("latitude", String.valueOf(selectedLatLng.latitude))
                    .putExtra("longitude", String.valueOf(selectedLatLng.longitude)));


    }

}
