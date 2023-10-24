package com.khedmap.khedmap.Order.Presenter;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.widget.RadioGroup;

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
import com.khedmap.khedmap.Order.Model.QuestionsModel;
import com.khedmap.khedmap.Order.QuestionsContract;
import com.khedmap.khedmap.Order.View.GetOrderDateTimeActivity;
import com.khedmap.khedmap.Utilities.UtilitiesSingleton;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import id.zelory.compressor.Compressor;

import static android.app.Activity.RESULT_OK;

public class QuestionsPresenter implements QuestionsContract.PresenterCallBack {

    private QuestionsContract.ViewCallBack view;
    private QuestionsModel model;

    private boolean responseReceived = true;

    private String selectedItemId;

    private JSONArray properties;
    private List<List<String>> answersList = new ArrayList<>();

    private int currentStep = 0;

    private boolean isEnableSubmitButton = true;

    private String meta;


    public QuestionsPresenter(QuestionsContract.ViewCallBack view, QuestionsModel model) {
        this.model = model;
        this.view = view;
    }


    @Override
    public void setSelectedItemId(String selectedItemId) {

        this.selectedItemId = selectedItemId;
    }


    private void setResponseReceived() {

        responseReceived = true;
        view.showProgressBar(false);

    }


    @Override
    public void loadQuestions(Activity activity) {

        if (!responseReceived) {   //for Block requests till Response not received
            view.showProgressBar(false);
            return;
        }

        if (!UtilitiesSingleton.getInstance().isNetworkAvailable(activity)) {
            view.showSnackBar("اتصال اینترنت را بررسی کنید");
            view.showProgressBar(false);
            return;
        }

        view.showProgressBar(true);
        responseReceived = false;

        //to reset Questions
        currentStep = 0;

        //to get Questions from server
        sendGetQuestionsRequest(activity);
    }


    ///////////////////////////////////////
//to get Questions from server

    private void sendGetQuestionsRequest(final Activity activity) {


        //to buildRequestBody
        String requestBody = buildGetQuestionsRequestBody(activity);

        if (requestBody == null)
            return;

        Response.Listener responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {


                GetQuestionsFetchData(response);

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


        model.sendGetQuestionsRequest(activity, requestBody, responseListener, errorListener);

    }

    private String buildGetQuestionsRequestBody(Activity activity) {


        try {

            //to build detail parameter
            JSONObject detail = new JSONObject();
            detail.put("subcategory_id", selectedItemId);
            detail.put("city_id", "1");

            //to build jsonBody
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("Detail", detail);
            //get apiToken from Shared Preferences
            jsonBody.put("api_token", model.getApiTokenSharedPref(activity));

            return jsonBody.toString();


        } catch (JSONException e) {
            e.printStackTrace();

            return null;
        }

    }


    private void GetQuestionsFetchData(String response) {


        try {
            JSONObject jsonobject = new JSONObject(response);

            //to get status
            String status = jsonobject.getString("status");

            if (status.equals("success")) {

                JSONObject data = jsonobject.getJSONObject("data");
                properties = data.getJSONArray("properties");

                meta = data.getJSONObject("meta").toString();

                answersList.clear();
                for (int i = 0; i < properties.length(); i++) {

                    answersList.add(new ArrayList<String>()); //to initial answersList with count of steps
                }

                initializeForm();

            } else {
                //to get message
                String message = jsonobject.getString("message");
                view.showSnackBar(message);
            }

        } catch (JSONException e) {
            Log.e("ParsError", e.getMessage());
            view.showSnackBar("خطای شبکه");
        }

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
///////////////////////////////////////


    @Override
    public void submitButtonClicked(Activity activity) {

        if (!isEnableSubmitButton)
            return;

        view.closeKeyboard();

        if (!responseReceived)   //for Block requests till Response not received
            return;

        if (!UtilitiesSingleton.getInstance().isNetworkAvailable(activity)) {
            view.showSnackBar("اتصال اینترنت را بررسی کنید");
            return;
        }


        if (currentStep == properties.length() - 1) {

            Bundle args = new Bundle();
            args.putSerializable("answersList", (Serializable) finalizeAnswersList());

            activity.startActivity(new Intent(activity, GetOrderDateTimeActivity.class).putExtra("answersBundle", args)
                    .putExtra("meta", meta).putExtra("subCategoryId", selectedItemId));

        } else {
            currentStep++;
            initializeForm();
        }


    }

    @Override
    public boolean backButtonClicked() {

        if (currentStep == 0)
            return true;

        currentStep--;
        initializeForm();
        return false;
    }


    private void initializeForm() {

        view.setToolbarTitle(currentStep + 1 + " از " + properties.length());

        view.clearList();

        try {

            JSONObject property = properties.getJSONObject(currentStep);


            String type = property.getString("type");

            switch (type) {
                case "checkbox":
                    isCheckBoxProperty(property.getString("id"), property.getString("title"),
                            property.getString("hint"), property.getString("required"), property.getJSONArray("answers"), answersList.get(currentStep));
                    break;
                case "radio":
                    isRadioProperty(property.getString("id"), property.getString("title"),
                            property.getString("hint"), property.getString("required"), property.getJSONArray("answers"), answersList.get(currentStep));
                    break;
                case "textbox":
                    isTextBoxProperty(property.getString("id"), property.getString("title"),
                            property.getString("hint"), property.getString("required"), answersList.get(currentStep));
                    break;
                case "file":
                    isFileProperty(property.getString("id"), property.getString("title"),
                            property.getString("hint"), property.getString("required"), answersList.get(currentStep));
                    break;

                default:
                    view.generateUnknownItem();
            }

        } catch (JSONException e) {
            Log.e("ParsError", e.getMessage());
            view.showSnackBar("خطای شبکه");
        }

    }

    private void isCheckBoxProperty(String id, String title, String hint, String required, JSONArray answers, List<String> selectedAnswers) {

//to disable or enable submit button
        if (required.equals("true")) {
            isEnableSubmitButton = false;
            view.enableSubmitButton(false);
        } else {
            isEnableSubmitButton = true;
            view.enableSubmitButton(true);
        }
//till here

        view.generateQuestionTitle(title);

        try {
            for (int i = 0; i < answers.length(); i++) {
                JSONObject answer = answers.getJSONObject(i);

                String answerText = answer.getString("text");
                String answerHint = answer.getString("hint");
                String answerIdentification = answer.getString("identification");

                view.generateCheckBoxAnswer(answerText, answerHint, answerIdentification, selectedAnswers.contains(answerIdentification), required);
            }

        } catch (JSONException e) {
            Log.e("ParsError", e.getMessage());
            view.showSnackBar("خطای شبکه");
        }

        view.generateQuestionHint(hint);
    }

    @Override
    public void checkBoxOrRadioAnswerSelected(String answerIdentification, boolean isSelected, String required) {

        if (isSelected) {

            if (!answersList.get(currentStep).contains(answerIdentification)) //to be sure that id not exist in list
                answersList.get(currentStep).add(answerIdentification);

        } else {

            answersList.get(currentStep).remove(answerIdentification);
        }

//to enable or disable SubmitButton
        if (answersList.get(currentStep).isEmpty() && required.equals("true")) {
            view.enableSubmitButton(false);
            isEnableSubmitButton = false;
        } else {
            view.enableSubmitButton(true);
            isEnableSubmitButton = true;
        }
//till here
    }


    private void isRadioProperty(String id, String title, String hint, String required, JSONArray answers, List<String> selectedAnswers) {

//to disable or enable submit button
        if (required.equals("true")) {
            isEnableSubmitButton = false;
            view.enableSubmitButton(false);
        } else {
            isEnableSubmitButton = true;
            view.enableSubmitButton(true);
        }
//till here

        view.generateQuestionTitle(title);

        RadioGroup radioGroup = view.generateRadioGroup();

        try {
            for (int i = 0; i < answers.length(); i++) {
                JSONObject answer = answers.getJSONObject(i);

                String answerText = answer.getString("text");
                String answerHint = answer.getString("hint");
                String answerIdentification = answer.getString("identification");

                view.generateRadioButtonAnswer(radioGroup, answerText, answerHint, answerIdentification, selectedAnswers.contains(answerIdentification), required);
            }

        } catch (JSONException e) {
            Log.e("ParsError", e.getMessage());
            view.showSnackBar("خطای شبکه");
        }

        view.generateQuestionHint(hint);
    }


    private void isTextBoxProperty(String id, String title, String hint, String required, List<String> insertedDescription) {

//to disable or enable submit button
        if (required.equals("true")) {
            isEnableSubmitButton = false;
            view.enableSubmitButton(false);
        } else {
            isEnableSubmitButton = true;
            view.enableSubmitButton(true);
        }
//till here

        view.generateQuestionTitle(title);

        if (insertedDescription.isEmpty())
            view.generateTextBox("", required);
        else {
            view.generateTextBox(insertedDescription.get(0), required);

            if (!insertedDescription.get(0).isEmpty()) {
                view.enableSubmitButton(true);
                isEnableSubmitButton = true;
            }

        }

        view.generateQuestionHint(hint);

    }

    @Override
    public void textChanged(CharSequence insertedDescription, String required) {

        if (insertedDescription.length() == 0)
            answersList.get(currentStep).clear();

        else {

            if (answersList.get(currentStep).isEmpty())
                answersList.get(currentStep).add(0, insertedDescription.toString());
            else
                answersList.get(currentStep).set(0, insertedDescription.toString());
        }

//to disable or enable submit button
        if (insertedDescription.toString().trim().length() < 4 && required.equals("true")) {
            view.enableSubmitButton(false);
            isEnableSubmitButton = false;
        } else {
            view.enableSubmitButton(true);
            isEnableSubmitButton = true;
        }
//till here

    }


    private void isFileProperty(String id, String title, String hint, String required, List<String> imageUri) {

//to disable or enable submit button
        if (required.equals("true")) {
            isEnableSubmitButton = false;
            view.enableSubmitButton(false);
        } else {
            isEnableSubmitButton = true;
            view.enableSubmitButton(true);
        }
//till here

        view.generateQuestionTitle(title);


        if (imageUri.isEmpty())
            view.generateFilePicker("", required);
        else {
            view.generateFilePicker(imageUri.get(0), required);

            if (!imageUri.get(0).isEmpty()) {
                view.enableSubmitButton(true);
                isEnableSubmitButton = true;
            }

        }

        view.generateQuestionHint(hint);


    }


    @Override
    public void problemPicClicked(final Activity activity, View view) {


        //build mainListener for notify permission granted
        MultiplePermissionsListener mainMultiplePermissionsListener = new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport report) {
                if (report.areAllPermissionsGranted()) {

                    // start picker to get image for cropping and then use the image in cropping activity
                    CropImage.activity()
                            .setAutoZoomEnabled(true)
                            .setCropMenuCropButtonTitle("برش")
                            .setCropShape(CropImageView.CropShape.RECTANGLE)
                            .setGuidelines(CropImageView.Guidelines.OFF)
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


    //not for use
    @Override
    public Uri getRetrievedImage(int requestCode, int resultCode, Intent data) {

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                return result.getUri();

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                view.showSnackBar(error.toString());
            }
        }
        return null;
    }
//till here

    @Override
    public void imageSelected(Uri imageUri, Context context) {

        Uri compressedImage = getCompressedImage(imageUri, context);

        if (compressedImage == null)
            return;

        if (answersList.get(currentStep).isEmpty())
            answersList.get(currentStep).add(0, compressedImage.toString());
        else
            answersList.get(currentStep).set(0, compressedImage.toString());

//to enable submit button
        view.enableSubmitButton(true);
        isEnableSubmitButton = true;

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

    @Override
    public void imageRemoved() {

        answersList.get(currentStep).clear();

//to disable submit button
        try {
            if (properties.getJSONObject(currentStep).getString("required").equals("true")) {
                view.enableSubmitButton(false);
                isEnableSubmitButton = false;
            }
        } catch (JSONException e) {
            Log.e("ParsError", e.getMessage());
            view.showSnackBar("خطای شبکه");
        }

    }


    private List<List<String>> finalizeAnswersList() { //to put id of each Question in end of it's Answers List


        List<List<String>> finalAnswersList = new ArrayList<>();

//to initialize final List with saved values
        for (int i = 0; i < properties.length(); i++) {

            finalAnswersList.add(new ArrayList<String>()); //to initial answersList with count of steps
        }

        int i = 0;
        for (List<String> answers : answersList) {
            for (String s : answers) {

                finalAnswersList.get(i).add(s);
            }
            i++;
        }
//till here

        try {

            for (int step = 0; step < properties.length(); step++) {

                JSONObject property = properties.getJSONObject(step);
                String id = property.getString("id");

                if (!finalAnswersList.get(step).isEmpty())
                    finalAnswersList.get(step).add(id);

            }

        } catch (JSONException e) {
            Log.e("ParsError", e.getMessage());
            view.showSnackBar("خطای شبکه");
        }

        return finalAnswersList;
    }
}
