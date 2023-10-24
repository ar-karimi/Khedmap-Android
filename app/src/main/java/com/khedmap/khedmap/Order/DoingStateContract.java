package com.khedmap.khedmap.Order;

import android.content.Context;

import com.khedmap.khedmap.Order.DataModels.Teammate;

import java.util.List;

public interface DoingStateContract {

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

    }


    interface ModelCallBack {

        void setApiTokenSharedPref(String apiToken, Context context);

        String getApiTokenSharedPref(Context context);

    }


    interface PresenterCallBack {

        void setSubmittedOrderId(String submittedOrderId);

        void fetchDataAndGenerateOrderDetailItems(String data);

        void callExpertButtonClicked(Context context);

    }

}
