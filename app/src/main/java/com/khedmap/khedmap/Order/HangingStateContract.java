package com.khedmap.khedmap.Order;

import android.app.Activity;
import android.content.Context;

import com.android.volley.Response;
import com.khedmap.khedmap.Order.DataModels.Disproof;

import java.util.List;

public interface HangingStateContract {

    interface ViewCallBack {

        void showSnackBar(String message);

        void showLoadingView(boolean showLoadingView);

        void setStaticPartValues(List<String> orderDetail);

        void setNoDetailItemToShow();

        void generateTitleItem(String value);

        void generateAnswerItem(String value);

        void generateImageAnswerItem(String url);

        void showDisproofsDialog(List<Disproof> disproofs);

        void showConfirmationDialog();
    }


    interface ModelCallBack {

        void setApiTokenSharedPref(String apiToken, Context context);

        String getApiTokenSharedPref(Context context);

        void sendGetDisproofItemsRequest(Context context, final String requestBody, Response.Listener responseListener, Response.ErrorListener errorListener);

        void sendRejectOrderRequest(Context context, final String requestBody, Response.Listener responseListener, Response.ErrorListener errorListener);

    }


    interface PresenterCallBack {

        void setSubmittedOrderId(String submittedOrderId);

        void fetchDataAndGenerateOrderDetailItems(String data);

        void selectExpertButtonClicked(Context context);

        void cancelOrderButtonClicked(Context context);

        void disproofItemSelected (Disproof selectedDisproof, Context context);

        void rejectOrderClicked(Activity activity);

    }

}
