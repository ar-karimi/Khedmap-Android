package com.khedmap.khedmap.Order.View;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.khedmap.khedmap.InitApplication;
import com.khedmap.khedmap.Order.Adapters.DayListAdapter;
import com.khedmap.khedmap.Order.Adapters.HourListAdapter;
import com.khedmap.khedmap.Order.DataModels.Day;
import com.khedmap.khedmap.Order.DataModels.Hour;
import com.khedmap.khedmap.Order.GetOrderDateTimeContract;
import com.khedmap.khedmap.R;
import com.khedmap.khedmap.Utilities.CustomSnackbar;
import com.khedmap.khedmap.di.component.DaggerActivityComponent;
import com.khedmap.khedmap.di.module.MvpModule;

import java.util.List;

import javax.inject.Inject;

import io.github.inflationx.calligraphy3.CalligraphyConfig;
import io.github.inflationx.calligraphy3.CalligraphyInterceptor;
import io.github.inflationx.viewpump.ViewPump;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class GetOrderDateTimeActivity extends AppCompatActivity implements GetOrderDateTimeContract.ViewCallBack {


    @Inject
    GetOrderDateTimeContract.PresenterCallBack presenterCallBack;

    @Inject
    Context mContext;


    //to change font
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }
    //till here, continues ...


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


//to configure Dagger
        DaggerActivityComponent.builder()
                .appComponent(InitApplication.get(this).component())
                .mvpModule(new MvpModule(this))
                .build()
                .inject(this);


//to change font
        changeFont();


        setContentView(R.layout.activity_get_order_date_time);

////////////////////////////////////////////////////////////////////


        //to get Received Meta And Answers from prev page
        Bundle args = getIntent().getBundleExtra("answersBundle");
        presenterCallBack.setReceivedMetaAndAnswers((List<List<String>>) args.getSerializable("answersList")
                , getIntent().getStringExtra("meta")
                , getIntent().getStringExtra("subCategoryId"));


        findViewById(R.id.ic_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                onBackPressed();
            }
        });


        //init Days RecyclerView
        // presenterCallBack.initDaysRecyclerView();  this method is called in generateHoursItems in presenter for check removing today

        //init Hours RecyclerView
        presenterCallBack.initHoursRecyclerView(); // and after it, initDaysRecyclerView


        findViewById(R.id.submit_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                presenterCallBack.submitButtonClicked(GetOrderDateTimeActivity.this);
            }
        });

    }


    public void changeFont() {

        ViewPump.init(ViewPump.builder()
                .addInterceptor(new CalligraphyInterceptor(
                        new CalligraphyConfig.Builder()
                                .setDefaultFontPath("IRANSansMobileFonts/IRANSansMobile(FaNum).ttf")
                                .setFontAttrId(R.attr.fontPath)
                                .build()))
                .build());

    }


    @Override
    public void showSnackBar(String message) {

        new CustomSnackbar(message, GetOrderDateTimeActivity.this, findViewById(R.id.constraint_layout)).snackbar.show();

    }


    @Override
    public void showDaysRecyclerView(List<Day> days, int itemPosition) {

        RecyclerView recyclerView = findViewById(R.id.recycler_view_days);
        DayListAdapter.OnItemClickListener listener = new DayListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int selectedItemPosition) {

                presenterCallBack.dayItemClicked(selectedItemPosition);
            }
        };

        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setAdapter(new DayListAdapter(this, days, listener, itemPosition));
    }

    @Override
    public void showHoursRecyclerView(List<Hour> hours, int itemPosition) {

        RecyclerView recyclerView = findViewById(R.id.recycler_view_hours);
        HourListAdapter.OnItemClickListener listener = new HourListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(String selectedItemStart) {

                presenterCallBack.hourItemClicked(selectedItemStart);
            }
        };

        recyclerView.setLayoutManager(new GridLayoutManager(this, 3, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(new HourListAdapter(this, hours, listener, itemPosition));
    }


    @Override
    public void finishActivity() {

        Toast.makeText(this, "زمان سفارش به پایان رسیده است", Toast.LENGTH_LONG).show();
        finish();
    }

}
