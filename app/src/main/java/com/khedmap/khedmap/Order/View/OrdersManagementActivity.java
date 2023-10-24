package com.khedmap.khedmap.Order.View;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;

import com.khedmap.khedmap.InitApplication;
import com.khedmap.khedmap.Order.Adapters.OrderListAdapter;
import com.khedmap.khedmap.Order.DataModels.Order;
import com.khedmap.khedmap.Order.OrdersManagementContract;
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

public class OrdersManagementActivity extends AppCompatActivity implements OrdersManagementContract.ViewCallBack {


    @Inject
    OrdersManagementContract.PresenterCallBack presenterCallBack;

    @Inject
    Context mContext;

    private SwipeRefreshLayout pullToRefresh;

    private Order rejectedOrder = null; //to save rejectedOrder details (received from notification)


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


        setContentView(R.layout.activity_orders_management);

////////////////////////////////////////////////////////////////////


        //init pullToRefresh
        pullToRefresh = findViewById(R.id.pullToRefresh);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                //Refresh InfiniteCategoriesRecyclerView
                presenterCallBack.initOrdersRecyclerView(OrdersManagementActivity.this);

            }
        });


        findViewById(R.id.ic_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                onBackPressed();
            }
        });


        //to get data from rejectedOrder By Expert Notification
        if (getIntent().getStringExtra("isFromRejectedNotification") != null) {
            rejectedOrder = new Order();
            rejectedOrder.setIdentification(getIntent().getStringExtra("orderId"));
            rejectedOrder.setStatusId(3); //Canceled By Expert
        }
        //till here

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
    protected void onStart() {
        super.onStart();

        if (rejectedOrder == null) {

            //init InfiniteCategoriesRecyclerView
            presenterCallBack.initOrdersRecyclerView(OrdersManagementActivity.this);

            //Call in onStart Because if an Order rejected after finish detail activity this list Refreshed
        } else {

            presenterCallBack.orderItemClicked(rejectedOrder, OrdersManagementActivity.this);
            rejectedOrder = null;
        }

    }

    @Override
    public void showOrdersRecyclerView(List<Order> orders) {

        RecyclerView recyclerView = findViewById(R.id.recycler_view_orders);

        if (!orders.isEmpty()) {

            findViewById(R.id.no_item_exist).setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);

            OrderListAdapter.OnItemClickListener listener = new OrderListAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(Order item) {

                    presenterCallBack.orderItemClicked(item, OrdersManagementActivity.this);
                }
            };

            //to set Animation on Items
            LayoutAnimationController animationController = AnimationUtils.loadLayoutAnimation(this, R.anim.layout_animation_slide_right);
            recyclerView.setLayoutAnimation(animationController);

            recyclerView.setLayoutManager(new GridLayoutManager(this, 1, LinearLayoutManager.VERTICAL, false));
            recyclerView.setAdapter(new OrderListAdapter(this, orders, listener));
        } else {
            recyclerView.setVisibility(View.GONE);
            findViewById(R.id.no_item_exist).setVisibility(View.VISIBLE);
        }

/*

        //to hide fab when list scrolling
        final FloatingActionButton fab = findViewById(R.id.fab);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) {
                    fab.hide();
                } else {
                    fab.show();
                }
                super.onScrolled(recyclerView, dx, dy);
            }
        });
        //till here
*/

    }

    @Override
    public void showSnackBar(String message) {

        new CustomSnackbar(message, OrdersManagementActivity.this, findViewById(R.id.coordinator_layout)).snackbar.show();

    }


    @Override
    public void showPullToRefresh(boolean showPullToRefresh) {
        if (showPullToRefresh)
            pullToRefresh.setRefreshing(true);
        else
            pullToRefresh.setRefreshing(false);

    }


    @Override
    public void onBackPressed() {

        if (getIntent().getStringExtra("isFromSubmitOrder") != null //to find that this activity comes from SubmitOrder Steps or navigation drawer
                || getIntent().getStringExtra("isFromRejectedNotification") != null) {
            Intent intent = new Intent(OrdersManagementActivity.this, HomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);

        } else
            super.onBackPressed();
    }


}
