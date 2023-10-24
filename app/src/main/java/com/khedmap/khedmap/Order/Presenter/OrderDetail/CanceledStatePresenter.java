package com.khedmap.khedmap.Order.Presenter.OrderDetail;

import android.util.Log;

import com.khedmap.khedmap.Order.CanceledStateContract;
import com.khedmap.khedmap.Order.DataModels.Teammate;
import com.khedmap.khedmap.Order.Model.OrderDetail.CanceledStateModel;
import com.khedmap.khedmap.Utilities.UtilitiesSingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CanceledStatePresenter implements CanceledStateContract.PresenterCallBack {

    private CanceledStateContract.ViewCallBack view;
    private CanceledStateModel model;

    private boolean responseReceived = true;

    private String submittedOrderId;

    public CanceledStatePresenter(CanceledStateContract.ViewCallBack view, CanceledStateModel model) {
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


            if (jsonobject.getString("expert").equals("null")) //to find that price and expert detail exist or not
            {
                view.setPrice(null); //Price doesn't exist
                view.setExpertDetailValues(null); //Expert doesn't exist
                return;
            }

            //set Price when exist
            view.setPrice(UtilitiesSingleton.getInstance().convertPrice(jsonobject.getInt("price")));

            //to show static part (expert detail)
            JSONObject expert = jsonobject.getJSONObject("expert");

            List<String> expertDetail = new ArrayList<>();

            expertDetail.add(expert.getString("avatar"));
            expertDetail.add(expert.getString("name"));

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

}
