package com.khedmap.khedmap.Order.Presenter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.khedmap.khedmap.Order.DataModels.Day;
import com.khedmap.khedmap.Order.DataModels.Hour;
import com.khedmap.khedmap.Order.GetOrderDateTimeContract;
import com.khedmap.khedmap.Order.Model.GetOrderDateTimeModel;
import com.khedmap.khedmap.Order.View.GetDistrictActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class GetOrderDateTimePresenter implements GetOrderDateTimeContract.PresenterCallBack {

    private GetOrderDateTimeContract.ViewCallBack view;
    private GetOrderDateTimeModel model;

    private List<List<String>> answersList;
    private String meta;
    private String subCategoryId;

    private String currentDate;
    private List<Day> days;

    private int selectedDayPosition = 0;
    private String selectedRangeStart;

    private List<Integer> ranges = new ArrayList<>();
    private List<Integer> activeRanges = new ArrayList<>();

    private boolean isFirstTime = true;

    public GetOrderDateTimePresenter(GetOrderDateTimeContract.ViewCallBack view, GetOrderDateTimeModel model) {
        this.model = model;
        this.view = view;
    }


    @Override
    public void setReceivedMetaAndAnswers(List<List<String>> answersList, String meta, String subCategoryId) {

        this.answersList = answersList;
        this.meta = meta;
        this.subCategoryId = subCategoryId;
    }


    private void initDaysRecyclerView(boolean todayEnded) {

        days = GetDaysItemsFetchData(meta);

        if (days == null)
            return;

        if (todayEnded && days.get(0).getGregorianDate().equals(currentDate)) {

            days.remove(0);
        }

        if (days.isEmpty()) {
            view.finishActivity();
            return;
        }
        view.showDaysRecyclerView(days, 0);

    }


    private List<Day> GetDaysItemsFetchData(String meta) {

        try {
            JSONObject jsonobject = new JSONObject(meta);

            //init received current date and time
            String[] currentDateAndTime = jsonobject.getString("today").split(" ");
            currentDate = currentDateAndTime[0];

            JSONArray daysJSONArray = jsonobject.getJSONArray("days");

            List<Day> days = new ArrayList<>();

            for (int i = 0; i < daysJSONArray.length(); i++) { //to get all of days to show

                JSONArray dayItem = daysJSONArray.getJSONArray(i);

                Day day = new Day();

                day.setName(dayItem.getString(0));
                day.setNumber(String.valueOf(dayItem.getInt(1)));
                day.setMonth(dayItem.getString(2));
                day.setGregorianDate(dayItem.getString(3));

                days.add(day);

            }

            return days;

        } catch (JSONException e) {
            Log.e("ParsError", e.getMessage());
            view.showSnackBar("خطای شبکه");
        }

        return null;

    }

    @Override
    public void dayItemClicked(int selectedItemPosition) {

        selectedDayPosition = selectedItemPosition;

        //to check that selected day is today or not
        if (days.get(selectedItemPosition).getGregorianDate().equals(currentDate)) {

            generateHoursItems(true);

        } else
            generateHoursItems(false);

    }


    @Override
    public void initHoursRecyclerView() {

        generateHoursItems(GetHoursItemsFetchData(meta));

    }


    private boolean GetHoursItemsFetchData(String meta) {

        try {
            JSONObject jsonobject = new JSONObject(meta);

            //init received current date and time
            String[] currentDateAndTime = jsonobject.getString("today").split(" ");
            String[] currentTime = currentDateAndTime[1].split(":");

            int firstTimeAvailable = jsonobject.getInt("first_time_available");
            int lastTimeAvailable = jsonobject.getInt("last_time_available");
            int range = jsonobject.getInt("range");

            int rangesCount;
            if (range != 0) //to Prevent from divide by zero
                rangesCount = (lastTimeAvailable - firstTimeAvailable) / range;
            else
                rangesCount = 0;

            for (int i = 0; i < rangesCount; i++) {
                int endOfRange = firstTimeAvailable + range;

                ranges.add(firstTimeAvailable); //0 index is start and 1 index is end of range, and so on ....
                ranges.add(endOfRange);

                firstTimeAvailable = endOfRange;
            }


            if (jsonobject.getInt("first_day_available") != 0) //haven't today to show
                return false;

            else {

                int currentTimeTotal = Integer.parseInt(currentTime[0]) * 60 + Integer.parseInt(currentTime[1]); //get current Time in minute

                for (int i = 0; i < ranges.size(); i += 2) {

                    double middleOfRange = (ranges.get(i) + ranges.get(i + 1)) / 2.0;

                    if (currentTimeTotal < (middleOfRange * 60)) {
                        activeRanges.add(ranges.get(i));
                        activeRanges.add(ranges.get(i + 1));
                    }

                }

                return true;
            }

        } catch (JSONException e) {
            Log.e("ParsError", e.getMessage());
            view.showSnackBar("خطای شبکه");
        }

        return false;

    }


    private void generateHoursItems(boolean isToday) {

        if (ranges.isEmpty()) {
            view.finishActivity();
            return;
        }


        List<Hour> hours = new ArrayList<>();

        if (isToday && !activeRanges.isEmpty())
            for (int i = 0; i < activeRanges.size(); i += 2) {
                Hour hour = new Hour();
                hour.setStart(String.valueOf(activeRanges.get(i)));
                hour.setEnd(String.valueOf(activeRanges.get(i + 1)));
                hours.add(hour);
            }
        else
            for (int i = 0; i < ranges.size(); i += 2) {
                Hour hour = new Hour();
                hour.setStart(String.valueOf(ranges.get(i)));
                hour.setEnd(String.valueOf(ranges.get(i + 1)));
                hours.add(hour);
            }

        selectedRangeStart = hours.get(0).getStart(); //to save start of first range

        view.showHoursRecyclerView(hours, 0);

        if (isFirstTime) {
            initDaysRecyclerView(activeRanges.isEmpty());
            isFirstTime = false;
        }
    }

    @Override
    public void hourItemClicked(String selectedItemStart) {

        selectedRangeStart = selectedItemStart;
    }

    @Override
    public void submitButtonClicked(Activity activity) {

        Bundle args = new Bundle();
        args.putSerializable("answersList", (Serializable) answersList);

        activity.startActivity(new Intent(activity, GetDistrictActivity.class).putExtra("answersBundle", args)
                .putExtra("subCategoryId", subCategoryId)
                .putExtra("selectedDate", days.get(selectedDayPosition).getGregorianDate())
                .putExtra("selectedHour", selectedRangeStart));

    }

}
