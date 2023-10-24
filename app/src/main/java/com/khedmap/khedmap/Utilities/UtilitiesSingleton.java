package com.khedmap.khedmap.Utilities;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.view.Window;
import android.view.WindowManager;

import com.khedmap.khedmap.R;

import java.text.DecimalFormat;


public class UtilitiesSingleton {
    private static UtilitiesSingleton instance;

    private UtilitiesSingleton() {
    }

    public static synchronized UtilitiesSingleton getInstance() {
        if (instance == null)
            instance = new UtilitiesSingleton();

        return instance;
    }

    //Method to change Status color
    public void changeStatusColor(Activity activity, Boolean isSplash) { //by Default use color Primary Dark to change Status Color

        Window window = activity.getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            if (isSplash)
                window.setStatusBarColor(ContextCompat.getColor(activity, R.color.colorPrimary));
            else
                window.setStatusBarColor(ContextCompat.getColor(activity, R.color.colorStatusBar));
        }
    }

    //to check connection to internet
    public boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = null;
        if (connectivityManager != null) {
            activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        }
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    public String getBaseURL() {
        return "https://khedmap.info/api/v1/";
    }


    public String convertPrice(int price) {
        int toman = price / 10;
        DecimalFormat format=new DecimalFormat("#,###,###");
        String newPrice = format.format(toman);
        return newPrice + " تومان";
    }


}