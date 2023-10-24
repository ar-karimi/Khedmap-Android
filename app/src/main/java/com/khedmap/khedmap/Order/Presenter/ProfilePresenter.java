package com.khedmap.khedmap.Order.Presenter;

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
import com.khedmap.khedmap.LoginSignUp.View.EnterMobileActivity;
import com.khedmap.khedmap.Order.Model.ProfileModel;
import com.khedmap.khedmap.Order.ProfileContract;
import com.khedmap.khedmap.Utilities.UtilitiesSingleton;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import id.zelory.compressor.Compressor;

import static android.app.Activity.RESULT_OK;

public class ProfilePresenter implements ProfileContract.PresenterCallBack {

    private ProfileContract.ViewCallBack view;
    private ProfileModel model;

    private boolean responseReceived = true;

    private String firstName;
    private String lastName;
    private String gender;

    private Uri resultUri = null;

    public ProfilePresenter(ProfileContract.ViewCallBack view, ProfileModel model) {
        this.model = model;
        this.view = view;
    }

    private void setResponseReceived() {

        responseReceived = true;
        view.showLoadingView(false);

    }

    private void resetResultUri() {

        resultUri = null;
    }

    @Override
    public void initPageItems(Activity activity, boolean isFromEdit) {

        if (!UtilitiesSingleton.getInstance().isNetworkAvailable(activity)) {
            view.showSnackBar("اتصال اینترنت را بررسی کنید");
            return;
        }

        view.showLoadingView(true);
        responseReceived = false;

        //to send getInformation Request
        sendGetInformationRequest(activity, isFromEdit);

    }


    ///////////////////////////////////////
//to send getInformation Request
    private void sendGetInformationRequest(final Activity activity, final boolean isFromEdit) {

        //to buildRequestBody
        String requestBody = buildGetInformationRequestBody(activity);

        if (requestBody == null)
            return;

        Response.Listener responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                List<String> profileDetails = GetInformationFetchData(response, isFromEdit);

                if (profileDetails != null) {

                    //to Update SharedPref
                    model.setAvatarSharedPref(profileDetails.get(0), activity);
                    model.setNameSharedPref(profileDetails.get(1), activity);
                    model.setFamilySharedPref(profileDetails.get(2), activity);

                    if (isFromEdit) {

                        view.showSnackBar("پروفایل با موفقیت به\u200Cروزرسانی شد");
                    }

                }

                setResponseReceived();
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                ///////Error Handling

                ErrorHandling(error, activity);

                setResponseReceived();


            }
        };


        model.sendGetInformationRequest(activity, requestBody, responseListener, errorListener);

    }


    private String buildGetInformationRequestBody(final Context context) {


        try {

            //to build jsonBody
            JSONObject jsonBody = new JSONObject();

            //get apiToken from Shared Preferences
            jsonBody.put("api_token", model.getApiTokenSharedPref(context));

            return jsonBody.toString();


        } catch (JSONException e) {
            e.printStackTrace();

            return null;
        }

    }


    private List<String> GetInformationFetchData(String response, boolean isFromEdit) {


        try {
            JSONObject jsonobject = new JSONObject(response);

            //to get status
            String status = jsonobject.getString("status");

            if (status.equals("success")) {

                JSONObject data = jsonobject.getJSONObject("data");

                List<String> profileDetails = new ArrayList<>();

                profileDetails.add(data.getString("avatar"));
                firstName = data.getString("firstName");
                profileDetails.add(firstName);
                lastName = data.getString("lastName");
                profileDetails.add(lastName);
                profileDetails.add(data.getString("phone"));
                profileDetails.add(UtilitiesSingleton.getInstance().convertPrice(data.getInt("credit")));
                gender = data.getString("gender");
                profileDetails.add(gender);

                view.setPageItems(profileDetails, isFromEdit);

                return profileDetails;

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


    private void ErrorHandling(VolleyError error, Context context) {


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


        if (errorMessage.equals("Please login again")) {
            //clear apiToken from sharedPref
            model.setApiTokenSharedPref("", context);


            Intent intent = new Intent(context, EnterMobileActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);

        } else {
            view.showSnackBar("خطای شبکه");
        }
    }

//till here
///////////////////////////////////////

    @Override
    public void genderSwitchClicked(String gender) {

        this.gender = gender;

    }


    //to Change profilePic
    @Override
    public void editProfilePicButtonClicked(final Activity activity, View view) {


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

//till here

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

    @Override
    public void editNameButtonClicked(boolean isName) {

        //to find that is name or family edit
        if (isName)
            view.showEditNameDialog(true, firstName);
        else
            view.showEditNameDialog(false, lastName);

    }

    @Override
    public boolean confirmEditNameClicked(String input, boolean isName) {

        if (input.length() < 2) {
            if (isName)
                view.showToast("نام باید حداقل 2 حرف باشد");
            else
                view.showToast("نام خانوادگی باید حداقل 2 حرف باشد");
            return false;
        }

        //Regex Checking
        if (!Pattern.matches("^[A-Za-z\u0629\u0643\u0649\u064A\u064B\u064D\u06D5\u0020\u0621-\u0628\u062A-\u063A\u0641-\u0642\u0644-\u0648\u064E-\u0651\u0655\u067E\u0686\u0698\u06A9-\u06AF\u06BE\u06CC]+$", input)) {
            if (isName)
                view.showToast("لطفاً نام را به صورت صحیح وارد کنید");
            else
                view.showToast("لطفاً نام خانوادگی را به صورت صحیح وارد کنید");
            return false;
        }

        if (isName) {
            firstName = input;
            view.setChangedName(input, true);
        } else {
            lastName = input;
            view.setChangedName(input, false);
        }

        return true;
    }

    @Override
    public void submitButtonClicked(Activity activity) {

        if (!responseReceived)   //for Block requests till Response not received
            return;


        if (!UtilitiesSingleton.getInstance().isNetworkAvailable(activity)) {
            view.showSnackBar("اتصال اینترنت را بررسی کنید");
            return;
        }


        view.showLoadingView(true);
        responseReceived = false;

        sendUpdateProfileRequest(activity);
    }

    ///////////////////////////////////////////
//to send name,gender,avatar(optional) to server
    private void sendUpdateProfileRequest(final Activity activity) {


        Response.Listener responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                if (UpdateProfileFetchData(response)) { //Check requestResponse Success or not

                    resetResultUri(); //to clear prev pic
                    initPageItems(activity, true);

                } else
                    setResponseReceived();
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                ///////Error Handling

                ErrorHandling(error, activity);

                setResponseReceived();


            }
        };

        List<String> profileInfo = new ArrayList<>();
        profileInfo.add(model.getApiTokenSharedPref(activity));
        profileInfo.add(firstName);
        profileInfo.add(lastName);
        profileInfo.add(gender);

        //to Compress image
        Uri compressedImage = getCompressedImage(resultUri, activity); //may be null

        model.sendUpdateProfileRequest(activity, responseListener, errorListener, profileInfo, compressedImage);

    }


    private boolean UpdateProfileFetchData(String response) {


        try {
            JSONObject jsonobject = new JSONObject(response);

            //to get status
            String status = jsonobject.getString("status");

            if (status.equals("success")) {

                return true;

            } else {
                //to get message
                String message = jsonobject.getString("message");
                view.showSnackBar(message);
            }

        } catch (JSONException e) {
            Log.e("ParsError", e.getMessage());
            view.showSnackBar("خطای شبکه");
        }

        return false;
    }

//till here
///////////////////////////////////////


}
