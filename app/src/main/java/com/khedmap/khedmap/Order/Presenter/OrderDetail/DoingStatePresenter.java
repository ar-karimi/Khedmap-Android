package com.khedmap.khedmap.Order.Presenter.OrderDetail;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.khedmap.khedmap.Order.DataModels.Teammate;
import com.khedmap.khedmap.Order.DoingStateContract;
import com.khedmap.khedmap.Order.Model.OrderDetail.DoingStateModel;
import com.khedmap.khedmap.Utilities.UtilitiesSingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class DoingStatePresenter implements DoingStateContract.PresenterCallBack {

    private DoingStateContract.ViewCallBack view;
    private DoingStateModel model;

    private boolean responseReceived = true;

    private String submittedOrderId;
    private String expertPhone;

    public DoingStatePresenter(DoingStateContract.ViewCallBack view, DoingStateModel model) {
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

            expertDetail.add(expert.getString("avatar"));
            expertDetail.add(expert.getString("name"));
            expertPhone = expert.getString("phone");

            view.setExpertDetailValues(expertDetail);
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

        } catch (JSONException e) {
            Log.e("ParsError", e.getMessage());
            view.showSnackBar("خطای شبکه");
        }
    }


    @Override
    public void callExpertButtonClicked(Context context) {

        if (!responseReceived)   //for Block requests till Response not received
            return;

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("tel:" + expertPhone));
        context.startActivity(intent);
    }

}
