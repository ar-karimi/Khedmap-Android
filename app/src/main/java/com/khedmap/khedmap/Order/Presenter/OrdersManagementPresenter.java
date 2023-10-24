package com.khedmap.khedmap.Order.Presenter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.error.NoConnectionError;
import com.android.volley.error.TimeoutError;
import com.android.volley.error.VolleyError;
import com.khedmap.khedmap.LoginSignUp.View.EnterMobileActivity;
import com.khedmap.khedmap.Order.DataModels.Order;
import com.khedmap.khedmap.Order.Model.OrdersManagementModel;
import com.khedmap.khedmap.Order.OrdersManagementContract;
import com.khedmap.khedmap.Order.View.OrderDetail.CanceledStateActivity;
import com.khedmap.khedmap.Order.View.OrderDetail.CompletedStateActivity;
import com.khedmap.khedmap.Order.View.OrderDetail.DoingStateActivity;
import com.khedmap.khedmap.Order.View.OrderDetail.HangingStateActivity;
import com.khedmap.khedmap.Order.View.OrderDetail.WaitingForStartStateActivity;
import com.khedmap.khedmap.Utilities.UtilitiesSingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class OrdersManagementPresenter implements OrdersManagementContract.PresenterCallBack {

    private OrdersManagementContract.ViewCallBack view;
    private OrdersManagementModel model;

    private boolean responseReceived = true;

    private List<Order> orders;

    private Order selectedOrderItem;


    public OrdersManagementPresenter(OrdersManagementContract.ViewCallBack view, OrdersManagementModel model) {
        this.model = model;
        this.view = view;
    }


    private void setResponseReceived() {

        responseReceived = true;
        view.showPullToRefresh(false);

    }

    @Override
    public void initOrdersRecyclerView(Context context) {

        if (!responseReceived)  //for Block requests till Response not received
            return;

        if (!UtilitiesSingleton.getInstance().isNetworkAvailable(context)) {
            view.showSnackBar("اتصال اینترنت را بررسی کنید");
            view.showPullToRefresh(false);
            return;
        }

        view.showPullToRefresh(true);
        responseReceived = false;

        //to get OrderItems from server
        sendGetOrderItemsRequest(context);
    }


    ///////////////////////////////////////
//to get OrderItems from server
    private void sendGetOrderItemsRequest(final Context context) {


        //to buildRequestBody
        String requestBody = buildGetOrderItemsRequestBody(context);

        if (requestBody == null)
            return;

        Response.Listener responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {


                List<Order> orders = GetOrderItemsFetchData(response);

                if (orders != null)
                    view.showOrdersRecyclerView(orders);


                setResponseReceived();
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                ///////Error Handling

                ErrorHandling(error, context);

                setResponseReceived();


            }
        };


        model.sendGetOrderItemsRequest(context, requestBody, responseListener, errorListener);

    }


    private String buildGetOrderItemsRequestBody(Context context) {


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


    private List<Order> GetOrderItemsFetchData(String response) {


        try {
            JSONObject jsonobject = new JSONObject(response);

            //to get status
            String status = jsonobject.getString("status");

            if (status.equals("success")) {

                List<Order> orders = new ArrayList<>();

                JSONArray data = jsonobject.getJSONArray("data");

                for (int i = 0; i < data.length(); i++) {
                    JSONObject orderItem = data.getJSONObject(i);

                    Order order = new Order();
                    order.setIdentification(orderItem.getString("identification"));
                    order.setSubcategory(orderItem.getString("subcategory"));
                    order.setCreate(orderItem.getString("create"));
                    order.setServiceTime(orderItem.getString("service_time"));
                    order.setAddress(orderItem.getString("address"));
                    order.setNumberOfSuggestions(orderItem.getString("numberOfSuggestions"));
                    order.setStatus(orderItem.getString("status"));
                    order.setStatusId(orderItem.getInt("status_id"));


                    orders.add(order);
                }

                this.orders = orders;
                return orders;

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
    public void orderItemClicked(Order item, Context context) {


        if (!responseReceived)   //for Block requests till Response not received
            return;

        if (!UtilitiesSingleton.getInstance().isNetworkAvailable(context)) {
            view.showSnackBar("اتصال اینترنت را بررسی کنید");
            return;
        }

        //to get selected item
        selectedOrderItem = item;

        view.showPullToRefresh(true);
        responseReceived = false;

        //to get OrderDetail from server
        sendGetOrderDetailRequest(context);

    }


    ///////////////////////////////////////
//to get OrderDetail from server
    private void sendGetOrderDetailRequest(final Context context) {


        //to buildRequestBody
        String requestBody = buildGetOrderDetailRequestBody(context);

        if (requestBody == null)
            return;

        Response.Listener responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                getOrderDetailFetchData(response, context);

                setResponseReceived();
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                ///////Error Handling

                ErrorHandling(error, context);

                setResponseReceived();


            }
        };


        model.sendGetOrderDetailRequest(context, requestBody, responseListener, errorListener);

    }


    private String buildGetOrderDetailRequestBody(Context context) {


        try {

            //to build detail parameter
            JSONObject detail = new JSONObject();
            detail.put("order", selectedOrderItem.getIdentification());

            //to build jsonBody
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("Detail", detail);
            //get apiToken from Shared Preferences
            jsonBody.put("api_token", model.getApiTokenSharedPref(context));

            return jsonBody.toString();


        } catch (JSONException e) {
            e.printStackTrace();

            return null;
        }

    }


    private void getOrderDetailFetchData(String response, Context context) {


        try {
            JSONObject jsonobject = new JSONObject(response);

            //to get status
            String status = jsonobject.getString("status");

            if (status.equals("success")) {

                switch (selectedOrderItem.getStatusId()) {
                    case 0:
                        context.startActivity(new Intent(context, HangingStateActivity.class)
                                .putExtra("data", jsonobject.getJSONObject("data").toString())
                                .putExtra("submittedOrderId", selectedOrderItem.getIdentification()));
                        break;
                    case 1:
                        context.startActivity(new Intent(context, WaitingForStartStateActivity.class)
                                .putExtra("data", jsonobject.getJSONObject("data").toString())
                                .putExtra("submittedOrderId", selectedOrderItem.getIdentification()));
                        break;
                    case 2:
                        context.startActivity(new Intent(context, CanceledStateActivity.class)
                                .putExtra("data", jsonobject.getJSONObject("data").toString())
                                .putExtra("submittedOrderId", selectedOrderItem.getIdentification()));
                        break;
                    case 3:
                        context.startActivity(new Intent(context, CanceledStateActivity.class)
                                .putExtra("data", jsonobject.getJSONObject("data").toString())
                                .putExtra("submittedOrderId", selectedOrderItem.getIdentification()));
                        break;
                    case -1:
                        context.startActivity(new Intent(context, CanceledStateActivity.class)
                                .putExtra("data", jsonobject.getJSONObject("data").toString())
                                .putExtra("submittedOrderId", selectedOrderItem.getIdentification()));
                        break;
                    case 4:
                        context.startActivity(new Intent(context, DoingStateActivity.class)
                                .putExtra("data", jsonobject.getJSONObject("data").toString()));
                        break;
                    case 5:
                        context.startActivity(new Intent(context, CompletedStateActivity.class)
                                .putExtra("data", jsonobject.getJSONObject("data").toString())
                                .putExtra("submittedOrderId", selectedOrderItem.getIdentification()));
                        break;

                    default:
                        view.showSnackBar("خطای شبکه");
                }


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
//till here
///////////////////////////////////////


}
