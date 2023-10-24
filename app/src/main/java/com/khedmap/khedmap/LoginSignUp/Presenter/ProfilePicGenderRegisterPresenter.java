package com.khedmap.khedmap.LoginSignUp.Presenter;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;

import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.error.NoConnectionError;
import com.android.volley.error.TimeoutError;
import com.android.volley.error.VolleyError;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.CompositeMultiplePermissionsListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.karumi.dexter.listener.multi.SnackbarOnAnyDeniedMultiplePermissionsListener;
import com.khedmap.khedmap.LoginSignUp.Model.ProfilePicGenderRegisterModel;
import com.khedmap.khedmap.LoginSignUp.ProfilePicGenderRegisterContract;
import com.khedmap.khedmap.LoginSignUp.View.EnterMobileActivity;
import com.khedmap.khedmap.Order.View.HomeActivity;
import com.khedmap.khedmap.Utilities.UtilitiesSingleton;
import com.pusher.pushnotifications.PushNotifications;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.List;

import id.zelory.compressor.Compressor;

import static android.app.Activity.RESULT_OK;

public class ProfilePicGenderRegisterPresenter implements ProfilePicGenderRegisterContract.PresenterCallBack {

    private ProfilePicGenderRegisterContract.ViewCallBack view;
    private ProfilePicGenderRegisterModel model;

    private String[] nameRegisterInfo;
    private String gender = "";
    private boolean responseReceived = true;

    private Uri resultUri = null;

    public ProfilePicGenderRegisterPresenter(ProfilePicGenderRegisterContract.ViewCallBack view, ProfilePicGenderRegisterModel model) {
        this.model = model;
        this.view = view;
    }


    @Override
    public void setNameRegisterInfo(String[] nameRegisterInfo) {

        this.nameRegisterInfo = nameRegisterInfo;
    }


    @Override
    public void profilePicClicked(final Activity activity, View view) {


        //build mainListener for notify permission granted
        MultiplePermissionsListener mainMultiplePermissionsListener = new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport report) {
                if (report.areAllPermissionsGranted()) {

                    // start picker to get image for cropping and then use the image in cropping activity
                    CropImage.activity()
                            .setAspectRatio(1, 1)
                            .setAutoZoomEnabled(true)
                            .setCropMenuCropButtonTitle("برش")
                            .setCropShape(CropImageView.CropShape.OVAL)
                            .setGuidelines(CropImageView.Guidelines.ON)
                            .start(activity);

                }
            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {/* ... */}
        };
        //till here

        //build snackBarListener for handle rejecting permission by user
        MultiplePermissionsListener snackbarMultiplePermissionsListener =
                SnackbarOnAnyDeniedMultiplePermissionsListener.Builder
                        .with(view, "لطفاً دسترسی\u200Cهای مورد نیاز را در تنظیمات فعال کنید")
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
                        .withDuration(4000)
                        .build();
        //till here

        //Composite 2 Listener in one
        MultiplePermissionsListener compositePermissionsListener = new CompositeMultiplePermissionsListener(snackbarMultiplePermissionsListener,
                mainMultiplePermissionsListener);

        Dexter.withActivity(activity)
                .withPermissions(
                        Manifest.permission.CAMERA,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
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
    public void genderSwitchClicked(String gender) {

        this.gender = gender;

    }

    @Override
    public Uri getRetrievedImage(int requestCode, int resultCode, Intent data) {

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                resultUri = result.getUri();

                return resultUri;

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                view.showSnackBar(error.toString());
            }
        }
        return null;
    }


    //to Compress Image
    private Uri getCompressedImage(Uri imageUri, Context context) {
        try {
            File file = new Compressor(context)
                    .compressToFile(new File(imageUri.getPath()));

            return Uri.fromFile(file);

        } catch (IOException | NullPointerException e) {
            e.printStackTrace();
            return null;
        }
    }
//till here

    ///////////////////////////////////////////
//to send name,gender,avatar to server
    @Override
    public void submitButtonClicked(Activity activity, String reagentCode) {

        if (!responseReceived)   //for Block requests till Response not received
            return;


        if (!UtilitiesSingleton.getInstance().isNetworkAvailable(activity)) {
            view.showSnackBar("اتصال اینترنت را بررسی کنید");
            return;
        }


        view.showProgressBar(true);
        responseReceived = false;

        sendSubmitRequest(activity, reagentCode);
    }


    private void sendSubmitRequest(final Activity activity, String reagentCode) {


        Response.Listener responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {


                String[] responseResult = SubmitFetchData(response);

                if (responseResult != null) {

                    model.setApiTokenSharedPref(nameRegisterInfo[0], activity);

                    String name = responseResult[1];
                    String family = responseResult[2];
                    String avatar = responseResult[3];
                    String credit = responseResult[4];

                    model.setNameSharedPref(name, activity);
                    model.setFamilySharedPref(family, activity);
                    model.setAvatarSharedPref(avatar, activity);
                    model.setCreditSharedPref(credit, activity);


                    //Subscribe to Pusher interest for PushNotification
                    PushNotifications.clearDeviceInterests(); //clear if prev interest exist
                    PushNotifications.addDeviceInterest("all");
                    PushNotifications.addDeviceInterest("user-" + nameRegisterInfo[1]);

                    Intent intent = new Intent(activity, HomeActivity.class);
                    activity.startActivity(intent);
                    activity.finish();


                }

                setResponseReceived();
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                ///////Error Handling

                NetworkResponse networkResponse = error.networkResponse;
                String errorMessage = "Unknown error";
                if (networkResponse == null) {
                    if (error.getClass().equals(TimeoutError.class)) {
                        errorMessage = "Request timeout";
                    } else if (error.getClass().equals(NoConnectionError.class)) {
                        errorMessage = "Failed to connect server";
                    }
                } else {

                    if (networkResponse.statusCode == 404) {
                        errorMessage = "Resource not found";
                    } else if (networkResponse.statusCode == 401) {
                        errorMessage = "Please login again";
                    } else if (networkResponse.statusCode == 400) {
                        errorMessage = "Check your inputs";
                    } else if (networkResponse.statusCode == 500) {
                        errorMessage = "Something is getting wrong";
                    }
                }

                Log.e("ResponseError", errorMessage);
                error.printStackTrace();

                //till here

                ErrorHandling(errorMessage, activity);

                setResponseReceived();


            }
        };


        //to Compress image
        Uri compressedImage = getCompressedImage(resultUri, activity); //may be null

        model.sendSubmitRequest(activity, responseListener, errorListener, nameRegisterInfo, gender
                , compressedImage, reagentCode);
    }


    private void setResponseReceived() {
        responseReceived = true;
        view.showProgressBar(false);
    }

    private String[] SubmitFetchData(String response) {


        try {
            JSONObject jsonobject = new JSONObject(response);

            //to get status
            String status = jsonobject.getString("status");

            if (status.equals("success")) {

                JSONObject data = jsonobject.getJSONObject("data");

                String[] responseResult = new String[5];


                responseResult[0] = "success";

                responseResult[1] = data.getString("name");
                responseResult[2] = data.getString("family");
                responseResult[3] = data.getString("avatar");
                responseResult[4] = data.getString("credit");

                return responseResult;

            } else {
                //to get message
                String message = jsonobject.getString("message");
                view.showSnackBar(message);
            }

        } catch (JSONException e) {
            Log.e("ParsError", e.getMessage());
            view.showSnackBar("خطای شبکه");
        }

        return null;
    }


    private void ErrorHandling(String errorMessage, Activity activity) {

        if (errorMessage.equals("Please login again")) {
            //clear apiToken from sharedPref
            model.setApiTokenSharedPref("", activity);


            Intent intent = new Intent(activity, EnterMobileActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            activity.startActivity(intent);

        } else {
            view.showSnackBar("خطای شبکه");
        }
    }
//till here
///////////////////////////////////////////
}
