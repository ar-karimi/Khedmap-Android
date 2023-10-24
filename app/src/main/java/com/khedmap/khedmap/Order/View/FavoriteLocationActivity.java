package com.khedmap.khedmap.Order.View;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.FrameLayout;

import com.khedmap.khedmap.InitApplication;
import com.khedmap.khedmap.Order.Adapters.MainFavoriteLocationListAdapter;
import com.khedmap.khedmap.Order.DataModels.FavoriteLocation;
import com.khedmap.khedmap.Order.FavoriteLocationContract;
import com.khedmap.khedmap.R;
import com.khedmap.khedmap.Utilities.CustomSnackbar;
import com.khedmap.khedmap.Utilities.GridRecyclerView;
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

public class FavoriteLocationActivity extends AppCompatActivity implements FavoriteLocationContract.ViewCallBack {


    @Inject
    FavoriteLocationContract.PresenterCallBack presenterCallBack;

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


        setContentView(R.layout.activity_favorite_location);

////////////////////////////////////////////////////////////////////


        findViewById(R.id.ic_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                onBackPressed();
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
    protected void onStart() {

        //init FavoriteLocations RecyclerView
        presenterCallBack.initFavoriteLocationsRecyclerView(this);

        super.onStart();
    }

    @Override
    public void showFavoriteLocationsRecyclerView(List<FavoriteLocation> favoriteLocations) {

        GridRecyclerView recyclerView = findViewById(R.id.grid_recycler_view_favorite_locations);

        if (!favoriteLocations.isEmpty()) {

            findViewById(R.id.no_item_exist).setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);


            MainFavoriteLocationListAdapter.OnEditItemClickListener editListener = new MainFavoriteLocationListAdapter.OnEditItemClickListener() {
                @Override
                public void onEditItemClick(String identification) {

                    presenterCallBack.editButtonClicked(identification, FavoriteLocationActivity.this);
                }
            };

            MainFavoriteLocationListAdapter.OnRemoveItemClickListener removeListener = new MainFavoriteLocationListAdapter.OnRemoveItemClickListener() {
                @Override
                public void onRemoveItemClick(String identification) {

                    showVerifyRemoveDialog(identification);
                }
            };

            //to set Animation on Items
            LayoutAnimationController animationController = AnimationUtils.loadLayoutAnimation(this, R.anim.layout_animation_slide_top);
            recyclerView.setLayoutAnimation(animationController);

            recyclerView.setLayoutManager(new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false));
            recyclerView.setAdapter(new MainFavoriteLocationListAdapter(this, favoriteLocations, editListener, removeListener));

        } else {

            recyclerView.setVisibility(View.GONE);
            findViewById(R.id.no_item_exist).setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void showSnackBar(String message) {

        new CustomSnackbar(message, FavoriteLocationActivity.this, findViewById(R.id.coordinator_layout)).snackbar.show();

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

                presenterCallBack.removeButtonClicked(selectedItemId, FavoriteLocationActivity.this);
            }
        });

        alertDialog.show(getSupportFragmentManager(), null);
    }

}
