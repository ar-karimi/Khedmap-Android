package com.khedmap.khedmap.Order;

import android.app.Activity;
import android.content.Context;

import com.android.volley.Response;
import com.khedmap.khedmap.Order.DataModels.Teammate;

import java.util.List;

public interface CompletedStateContract {

    interface ViewCallBack {

        void showSnackBar(String message);

        void showLoadingView(boolean showLoadingView);

        void setStaticPartValues(List<String> orderDetail);

        void setNoDetailItemToShow();

        void generateTitleItem(String value);

        void generateAnswerItem(String value);

        void generateImageAnswerItem(String url);

        void setExpertDetailValues(List<String> expertDetail);

        void showTeammatesRecyclerView(List<Teammate> teammates);

        void setNoTeammateToShow();

        void setFactorImage(String url);

        void showAddToFavoriteButton(boolean showButton);

        void showComment(List<String> commentDetail);

        void showEditCommentButton(boolean showButton, boolean isEdit);
    }


    interface ModelCallBack {

        void setApiTokenSharedPref(String apiToken, Context context);

        String getApiTokenSharedPref(Context context);

        void sendAddFavoriteExpertRequest(Context context, String requestBody, Response.Listener responseListener, Response.ErrorListener errorListener);
    }


    interface PresenterCallBack {

        void setSubmittedOrderId(String submittedOrderId);

        void fetchDataAndGenerateOrderDetailItems(String data);

        void editCommentButtonClicked(Activity activity, boolean isEdit);

        void commentResultReceived(String rate, String comment);

        void addFavoriteExpertButtonClicked(Context context);
    }

}
