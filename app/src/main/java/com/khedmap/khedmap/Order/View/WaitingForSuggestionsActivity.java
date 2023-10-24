package com.khedmap.khedmap.Order.View;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.florent37.viewanimator.ViewAnimator;
import com.khedmap.khedmap.Order.Adapters.ViewPagerAdapter;
import com.khedmap.khedmap.R;
import com.khedmap.khedmap.Utilities.SharedPrefManager;
import com.khedmap.khedmap.Utilities.UtilitiesSingleton;

import io.github.inflationx.calligraphy3.CalligraphyConfig;
import io.github.inflationx.calligraphy3.CalligraphyInterceptor;
import io.github.inflationx.viewpump.ViewPump;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import ir.hugenet.hugenetdialog.HugeNetAlertDialog;
import ir.hugenet.hugenetdialog.OnClickListener;

public class WaitingForSuggestionsActivity extends AppCompatActivity {


    private SharedPrefManager sharedPrefManager;


    //to change font
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }
    //till here, continues ...

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //to change font
        ViewPump.init(ViewPump.builder()
                .addInterceptor(new CalligraphyInterceptor(
                        new CalligraphyConfig.Builder()
                                .setDefaultFontPath("IRANSansMobileFonts/IRANSansMobile(FaNum).ttf")
                                .setFontAttrId(R.attr.fontPath)
                                .build()))
                .build());


        setContentView(R.layout.activity_waiting_for_suggestions);

////////////////////////////////////////////////////////////////////


        //get Tracking Code
        if (getIntent().getStringExtra("trackingCode") != null)
            showSuccessDialog(getIntent().getStringExtra("trackingCode"));


        findViewById(R.id.ic_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                onBackPressed();
            }
        });

        TabLayout tabLayout = findViewById(R.id.tab_layout);
        ViewPager viewPager = findViewById(R.id.view_pager);

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        adapter.addFragment(new SuggestionsFragment(), getString(R.string.fragment_suggestions));
        adapter.addFragment(new DiscountsFragment(), getString(R.string.fragment_discounts));


        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);


//to set Custom View for tabs titles

        LinearLayout suggestionsLinearLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.tab_layout_title_suggestions, null, false);
        tabLayout.getTabAt(0).setCustomView(suggestionsLinearLayout);

        TextView titleSuggestions = findViewById(R.id.title_suggestions);
        Typeface customTypeface = Typeface.createFromAsset(this.getAssets(), "IRANSansMobileFonts/IRANSansMobile(FaNum)_Medium.ttf");
        titleSuggestions.setTypeface(customTypeface);

        LinearLayout discountsLinearLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.tab_layout_title_discounts, null, false);
        tabLayout.getTabAt(1).setCustomView(discountsLinearLayout);

        TextView titleDiscounts = findViewById(R.id.title_discounts);
        titleDiscounts.setTypeface(customTypeface);

//till here


        //to set animation to titleDiscounts
        ViewAnimator
                .animate(titleDiscounts)
                .pulse()
                .duration(1000)
                .repeatMode(ViewAnimator.RESTART)
                .repeatCount(ViewAnimator.INFINITE)
                .start();

    }


///////////////
//to show SuccessDialog
    public void showSuccessDialog(String trackingCode) {

        final HugeNetAlertDialog alertDialog = new HugeNetAlertDialog.Builder()
                .setTitle("سفارش شما با موفقیت ثبت شد")
                .setSubtitle("کد سفارش: " + trackingCode)
                .setPositiveText("باشه")
                .setCancellable(false)
                .build();

        alertDialog.setPositiveButtonClickListener(new OnClickListener.OnPositiveButtonClickListener() {
            @Override
            public void onClick(View v) {

                alertDialog.dismiss();
            }
        });

        alertDialog.show(getSupportFragmentManager(), null);
    }



    @Override
    public void onBackPressed() {

        if (getIntent().getStringExtra("trackingCode") != null) //to find that this activity comes from navigation drawer or not
        {
            startActivity(new Intent(WaitingForSuggestionsActivity.this, OrdersManagementActivity.class)
                    .putExtra("isFromSubmitOrder", "true"));
            finish();
        } else
            super.onBackPressed();
    }


    //model methods
    public String getNameSharedPref(Context context) {

        //to make sharedPrefManager object
        if (sharedPrefManager == null)
            sharedPrefManager = new SharedPrefManager(context);

        return sharedPrefManager.getName();
    }


    public String getFamilySharedPref(Context context) {

        //to make sharedPrefManager object
        if (sharedPrefManager == null)
            sharedPrefManager = new SharedPrefManager(context);

        return sharedPrefManager.getFamily();
    }


    public String getAvatarSharedPref(Context context) {

        //to make sharedPrefManager object
        if (sharedPrefManager == null)
            sharedPrefManager = new SharedPrefManager(context);

        return sharedPrefManager.getAvatar();
    }


    public String getCreditSharedPref(Context context) {

        //to make sharedPrefManager object
        if (sharedPrefManager == null)
            sharedPrefManager = new SharedPrefManager(context);

        return sharedPrefManager.getCredit();
    }
//till here


}
