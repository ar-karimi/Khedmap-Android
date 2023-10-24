package com.khedmap.khedmap.Order;

import android.content.Context;

import com.android.volley.Response;
import com.khedmap.khedmap.Order.DataModels.Order;

import java.util.List;

public interface OrdersManagementContract {

    interface ViewCallBack {

        void showSnackBar(String message);

        void showPullToRefresh(boolean showPullToRefresh);

        void showOrdersRecyclerView(List<Order> orders);

    }


    interface ModelCallBack {

        void setApiTokenSharedPref(String apiToken, Context context);

        String getApiTokenSharedPref(Context context);

        String getNameSharedPref(Context context);

        String getFamilySharedPref(Context context);

        String getAvatarSharedPref(Context context);

        String getCreditSharedPref(Context context);

        void sendGetOrderItemsRequest(Context context, final String requestBody, Response.Listener responseListener, Response.ErrorListener errorListener);

        void sendGetOrderDetailRequest(Context context, final String requestBody, Response.Listener responseListener, Response.ErrorListener errorListener);

    }


    interface PresenterCallBack {

        void initOrdersRecyclerView(Context context);

        void orderItemClicked(Order item, Context context);

    }

}
