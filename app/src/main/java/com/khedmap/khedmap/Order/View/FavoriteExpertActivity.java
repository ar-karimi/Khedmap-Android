package com.khedmap.khedmap.Order.View;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.FrameLayout;

import com.khedmap.khedmap.InitApplication;
import com.khedmap.khedmap.Order.Adapters.FavoriteExpertListAdapter;
import com.khedmap.khedmap.Order.DataModels.FavoriteExpert;
import com.khedmap.khedmap.Order.FavoriteExpertContract;
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
import ir.hugenet.hugenetdialog.HugeNetAlertDialog;
import ir.hugenet.hugenetdialog.OnClickListener;

public class FavoriteExpertActivity extends AppCompatActivity implements FavoriteExpertContract.ViewCallBack {


    @Inject
    FavoriteExpertContract.PresenterCallBack presenterCallBack;

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


        setContentView(R.layout.activity_favorite_expert);

////////////////////////////////////////////////////////////////////


        findViewById(R.id.ic_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                onBackPressed();
            }
        });


        //init FavoriteExperts RecyclerView
        presenterCallBack.initFavoriteExpertsRecyclerView(this);

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
    public void showFavoriteExpertsRecyclerView(List<FavoriteExpert> favoriteExperts) {

        RecyclerView recyclerView = findViewById(R.id.recycler_view_favorite_experts);

        if (!favoriteExperts.isEmpty()) {

            findViewById(R.id.no_item_exist).setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);

            FavoriteExpertListAdapter.OnRemoveItemClickListener listener = new FavoriteExpertListAdapter.OnRemoveItemClickListener() {
                @Override
                public void onRemoveItemClick(String selectedItemId) {

                    showVerifyRemoveDialog(selectedItemId);
                }
            };

            //to set Animation on Items
            LayoutAnimationController animationController = AnimationUtils.loadLayoutAnimation(this, R.anim.layout_animation_slide_right);
            recyclerView.setLayoutAnimation(animationController);

            recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
            recyclerView.setAdapter(new FavoriteExpertListAdapter(this, favoriteExperts, listener));
        } else {
            recyclerView.setVisibility(View.GONE);
            findViewById(R.id.no_item_exist).setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void showSnackBar(String message) {

        new CustomSnackbar(message, FavoriteExpertActivity.this, findViewById(R.id.coordinator_layout)).snackbar.show();

    }

    @Override
    public void showLoadingView(boolean showLoadingView) {
        FrameLayout loadingView = findViewById(R.id.loading_view);
        if (showLoadingView)
            loadingView.setVisibility(View.VISIBLE);
        else
            loadingView.setVisibility(View.GONE);

    }

    public void showVerifyRemoveDialog(final String selectedItemId) {

        final HugeNetAlertDialog alertDialog = new HugeNetAlertDialog.Builder()
                .setTitle("آیا برای حذف اطمینان دارید؟")
                .setPositiveText("بله")
                .setNegativeText("انصراف")
                .setCancellable(true)
                .build();

        alertDialog.setNegativeButtonClickListener(new OnClickListener.OnNegativeButtonClickListener() {
            @Override
            public void onClick(View v) {

                alertDialog.dismiss();
            }
        });

        alertDialog.setPositiveButtonClickListener(new OnClickListener.OnPositiveButtonClickListener() {
            @Override
            public void onClick(View v) {

                alertDialog.dismiss();

                presenterCallBack.removeButtonClicked(selectedItemId, FavoriteExpertActivity.this);
            }
        });

        alertDialog.show(getSupportFragmentManager(), null);
    }

}
