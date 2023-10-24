package com.khedmap.khedmap.Order;

import android.app.Activity;
import android.content.Context;

import com.android.volley.Response;

public interface CommentContract {

    interface ViewCallBack {

        void showSnackBar(String message);

        void showLoadingView(boolean showLoadingView);

        void showExpertDetails(String expertName, String expertPic);

        void setPrevComment(String rate, String comment);

        void showFavoriteExpertDialog();

        void showToast(String message);
    }


    interface ModelCallBack {

        void setApiTokenSharedPref(String apiToken, Context context);

        String getApiTokenSharedPref(Context context);

        void sendCommentRequest(Context context, final String requestBody,
                                Response.Listener responseListener, Response.ErrorListener errorListener, String requestUrl);

        void sendAddFavoriteExpertRequest(Context context, final String requestBody, Response.Listener responseListener, Response.ErrorListener errorListener);

    }


    interface PresenterCallBack {

        void setFinishedOrderDetail(String orderId, String expertName, String expertPic, String expertId, String isFavorite,
                                    String rate, String comment);

        void submitButtonClicked(Activity activity, int rate, String comment);

        void addFavoriteExpertClicked(Activity activity, boolean isOk);

    }

}
