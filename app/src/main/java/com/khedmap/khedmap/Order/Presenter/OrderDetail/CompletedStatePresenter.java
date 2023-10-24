package com.khedmap.khedmap.Order.Presenter.OrderDetail;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.error.NoConnectionError;
import com.android.volley.error.TimeoutError;
import com.android.volley.error.VolleyError;
import com.khedmap.khedmap.LoginSignUp.View.EnterMobileActivity;
import com.khedmap.khedmap.Order.CompletedStateContract;
import com.khedmap.khedmap.Order.DataModels.Teammate;
import com.khedmap.khedmap.Order.Model.OrderDetail.CompletedStateModel;
import com.khedmap.khedmap.Order.View.CommentActivity;
import com.khedmap.khedmap.Utilities.UtilitiesSingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CompletedStatePresenter implements CompletedStateContract.PresenterCallBack {

    private CompletedStateContract.ViewCallBack view;
    private CompletedStateModel model;

    private boolean responseReceived = true;

    private String submittedOrderId;

    private String expertId;
    private String expertName;
    private String expertPic;
    private String expertIsFavorite;
    private String rate = "null"; //because from request "null" received not null
    private String comment = "null";


    public CompletedStatePresenter(CompletedStateContract.ViewCallBack view, CompletedStateModel model) {
        this.model = model;
        this.view = view;
    }


    @Override
    public void setSubmittedOrderId(String submittedOrderId) {

        this.submittedOrderId = submittedOrderId;
    }


    private void setResponseReceived() {

        responseReceived = true;
        view.showLoadingView(false);

    }

    private void setExpertIsFavorite() {

        expertIsFavorite = "true";

    }

    @Override
    public void fetchDataAndGenerateOrderDetailItems(String data) {

        try {
            JSONObject jsonobject = new JSONObject(data);


            //to show static part
            List<String> orderDetail = new ArrayList<>();

            orderDetail.add(jsonobject.getString("title"));
            orderDetail.add(jsonobject.getString("service_time"));
            orderDetail.add(jsonobject.getString("address"));
            orderDetail.add(jsonobject.getString("status"));
            orderDetail.add(UtilitiesSingleton.getInstance().convertPrice(jsonobject.getInt("price")));

            String[] startTime = jsonobject.getString("start_time").split(" ");
            orderDetail.add(startTime[0]); //startDate
            orderDetail.add(startTime[1]); //startTime

            String[] finishTime = jsonobject.getString("finish_time").split(" ");
            orderDetail.add(finishTime[0]); //finishDate
            orderDetail.add(finishTime[1]); //finishTime


            view.setStaticPartValues(orderDetail);
            //till here


            //to show dynamic part (order detail)
            JSONArray properties = jsonobject.getJSONArray("properties");

            if (properties.length() == 0)
                view.setNoDetailItemToShow();


            for (int i = 0; i < properties.length(); i++) {
                JSONObject questionItem = properties.getJSONObject(i);

                view.generateTitleItem(questionItem.getString("title"));

                if (questionItem.getString("type").equals("file"))
                    view.generateImageAnswerItem(questionItem.getJSONArray("values").getString(0));

                else {

                    JSONArray answers = questionItem.getJSONArray("values");
                    for (int j = 0; j < answers.length(); j++)
                        view.generateAnswerItem(answers.getString(j));
                }

            }
            //till here


            //to show static part (expert detail)
            JSONObject expert = jsonobject.getJSONObject("expert");

            List<String> expertDetail = new ArrayList<>();

            expertPic = expert.getString("avatar");
            expertDetail.add(expertPic);
            expertName = expert.getString("name");
            expertDetail.add(expertName);

            view.setExpertDetailValues(expertDetail);

            expertId = expert.getString("identification");
            expertIsFavorite = expert.getString("isFavorite");
            //till here


            //to show teammates
            if (expert.getString("teammate").equals("null"))
                view.setNoTeammateToShow();
            else {

                List<Teammate> teammates = new ArrayList<>();

                JSONArray teammateArray = expert.getJSONArray("teammate");

                for (int i = 0; i < teammateArray.length(); i++) {
                    JSONObject teammateItem = teammateArray.getJSONObject(i);

                    Teammate teammate = new Teammate();
                    teammate.setName(teammateItem.getString("name"));
                    teammate.setAvatar(teammateItem.getString("avatar"));

                    teammates.add(teammate);
                }

                view.showTeammatesRecyclerView(teammates);
            }
            //till here


            //to setFactorImage
            String factorImage = jsonobject.getString("factorPicture");
            if (!factorImage.equals("null"))
                view.setFactorImage(factorImage);


            //to show user Comment
            if (jsonobject.getString("comment").equals("null")) {
                view.showComment(null); //Comment doesn't exist

                if (jsonobject.getBoolean("createComment")) //to show submitComment Button
                    view.showEditCommentButton(true, false);

                return;
            }

            JSONObject Comment = jsonobject.getJSONObject("comment");

            List<String> commentDetail = new ArrayList<>();

            rate = Comment.getString("rate");
            commentDetail.add(rate);
            comment = Comment.getString("comment");
            commentDetail.add(comment);

            view.showComment(commentDetail);

            if (jsonobject.getBoolean("createComment")) //to show editComment Button
                view.showEditCommentButton(true, true);

            //till here


            //to show add to favorite button
            if (Comment.getInt("rate") >= 3 && !expert.getBoolean("isFavorite"))
                view.showAddToFavoriteButton(true);


        } catch (JSONException e) {
            Log.e("ParsError", e.getMessage());
            view.showSnackBar("خطای شبکه");
        }
    }


    @Override
    public void editCommentButtonClicked(Activity activity, boolean isEdit) {


        if (!responseReceived)   //for Block requests till Response not received
            return;

        if (!UtilitiesSingleton.getInstance().isNetworkAvailable(activity)) {
            view.showSnackBar("اتصال اینترنت را بررسی کنید");
            return;
        }

        activity.startActivityForResult(new Intent(activity, CommentActivity.class)
                .putExtra("orderId", submittedOrderId)
                .putExtra("expertName", expertName)
                .putExtra("expertPic", expertPic)
                .putExtra("expertId", expertId)
                .putExtra("isFavorite", expertIsFavorite)
                .putExtra("rate", rate)
                .putExtra("comment", comment), 1);

    }

    // onActivityResult
    @Override
    public void commentResultReceived(String rate, String comment) {

        List<String> commentDetail = new ArrayList<>();

        this.rate = rate;
        commentDetail.add(this.rate);

        if (comment.isEmpty())
            this.comment = "null";
        else
            this.comment = comment;
        commentDetail.add(this.comment);

        //show new comment
        view.showComment(commentDetail);

        //show edit comment
        view.showEditCommentButton(true, true);

    }


    @Override
    public void addFavoriteExpertButtonClicked(Context context) {


        if (!responseReceived)   //for Block requests till Response not received
            return;

        if (!UtilitiesSingleton.getInstance().isNetworkAvailable(context)) {
            view.showSnackBar("اتصال اینترنت را بررسی کنید");
            return;
        }

        view.showLoadingView(true);
        responseReceived = false;

        //to send AddFavoriteExpert Request
        sendAddFavoriteExpertRequest(context);

    }


    ///////////////////////////////////////
//to send AddFavoriteExpert Request
    private void sendAddFavoriteExpertRequest(final Context context) {


        //to buildRequestBody
        String requestBody = buildAddFavoriteExpertRequestBody(context);

        if (requestBody == null)
            return;

        Response.Listener responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                if (addFavoriteExpertFetchData(response)) { //Check requestResponse Success or not

                    view.showAddToFavoriteButton(false);
                    view.showSnackBar("با موفقیت به کارشناسان مورد علاقه اضافه شد");
                    setExpertIsFavorite();
                }

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


        model.sendAddFavoriteExpertRequest(context, requestBody, responseListener, errorListener);

    }


    private String buildAddFavoriteExpertRequestBody(Context context) {


        try {

            //to build detail parameter
            JSONObject detail = new JSONObject();
            detail.put("expert", expertId);

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


    private boolean addFavoriteExpertFetchData(String response) {


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

}
