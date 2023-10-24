package com.khedmap.khedmap.Order;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.RadioGroup;

import com.android.volley.Response;

public interface QuestionsContract {

    interface ViewCallBack {

        void showSnackBar(String message);

        void showProgressBar(boolean showProgressBar);

        void setToolbarTitle(String toolbarTitle);

        void closeKeyboard();

        void generateQuestionTitle(String title);

        void generateQuestionHint(String hint);

        void generateCheckBoxAnswer(String answerText, String answerHint, String answerIdentification, boolean isChecked,
                                    String required);

        RadioGroup generateRadioGroup();

        void generateRadioButtonAnswer(RadioGroup radioGroup, String answerText, String answerHint, String answerIdentification,
                                       boolean isChecked, String required);

        void generateTextBox(String insertedDescription, String required);

        void generateFilePicker(String selectedImageUri, final String required);

        void generateUnknownItem();

        void clearList();

        void enableSubmitButton(boolean isEnable);

    }

    interface ModelCallBack {

        void setApiTokenSharedPref(String apiToken, Context context);

        String getApiTokenSharedPref(Context context);

        String getNameSharedPref(Context context);

        String getFamilySharedPref(Context context);

        String getAvatarSharedPref(Context context);

        String getCreditSharedPref(Context context);

        void sendGetQuestionsRequest(Context context, final String requestBody, Response.Listener responseListener,
                                     Response.ErrorListener errorListener);

    }

    interface PresenterCallBack {

        void setSelectedItemId(String selectedItemId);

        void loadQuestions(Activity activity);

        void submitButtonClicked(Activity activity);

        boolean backButtonClicked();

        void checkBoxOrRadioAnswerSelected(String answerIdentification, boolean isSelected, String required);

        void textChanged(CharSequence insertedDescription, String required);

        Uri getRetrievedImage(int requestCode, int resultCode, Intent data);

        void problemPicClicked(final Activity activity, View view);

        void imageSelected(Uri imageUri, Context context);

        void imageRemoved();

    }

}
