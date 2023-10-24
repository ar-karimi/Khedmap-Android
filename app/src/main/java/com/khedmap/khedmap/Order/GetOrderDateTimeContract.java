package com.khedmap.khedmap.Order;

import android.app.Activity;
import android.content.Context;

import com.khedmap.khedmap.Order.DataModels.Day;
import com.khedmap.khedmap.Order.DataModels.Hour;

import java.util.List;

public interface GetOrderDateTimeContract {

    interface ViewCallBack {

        void showSnackBar(String message);

        void showDaysRecyclerView(List<Day> days, int itemPosition);

        void showHoursRecyclerView(List<Hour> hours, int itemPosition);

        void finishActivity();
    }

    interface ModelCallBack {

        void setApiTokenSharedPref(String apiToken, Context context);

        String getApiTokenSharedPref(Context context);

        String getNameSharedPref(Context context);

        String getFamilySharedPref(Context context);

        String getAvatarSharedPref(Context context);

        String getCreditSharedPref(Context context);

    }

    interface PresenterCallBack {

        void setReceivedMetaAndAnswers(List<List<String>> answersList, String meta, String subCategoryId);

        void dayItemClicked(int selectedItemPosition);

        void initHoursRecyclerView();

        void hourItemClicked(String selectedItemPosition);

        void submitButtonClicked(Activity activity);

    }

}
