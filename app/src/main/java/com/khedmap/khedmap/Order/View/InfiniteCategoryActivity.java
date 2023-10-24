package com.khedmap.khedmap.Order.View;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ImageView;
import android.widget.TextView;

import com.khedmap.khedmap.InitApplication;
import com.khedmap.khedmap.Order.Adapters.InfiniteCategoryListAdapter;
import com.khedmap.khedmap.Order.Adapters.InfiniteCategorySliderAdapter;
import com.khedmap.khedmap.Order.Adapters.PicassoImageLoadingService;
import com.khedmap.khedmap.Order.DataModels.InfiniteCategory;
import com.khedmap.khedmap.Order.DataModels.Slide;
import com.khedmap.khedmap.Order.InfiniteCategoryContract;
import com.khedmap.khedmap.R;
import com.khedmap.khedmap.Utilities.CustomSnackbar;
import com.khedmap.khedmap.di.component.DaggerActivityComponent;
import com.khedmap.khedmap.di.module.MvpModule;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.github.inflationx.calligraphy3.CalligraphyConfig;
import io.github.inflationx.calligraphy3.CalligraphyInterceptor;
import io.github.inflationx.viewpump.ViewPump;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import ir.hugenet.hugenetdialog.HugeNetAlertDialog;
import ir.hugenet.hugenetdialog.OnClickListener;
import ss.com.bannerslider.Slider;
import ss.com.bannerslider.event.OnSlideClickListener;

public class InfiniteCategoryActivity extends AppCompatActivity implements InfiniteCategoryContract.ViewCallBack {


    @Inject
    InfiniteCategoryContract.PresenterCallBack presenterCallBack;

    @Inject
    Context mContext;


    private NavigationView navigationView;

    private SwipeRefreshLayout pullToRefresh;

    private DrawerLayout drawerLayout;

    private Slider slider;
    private InfiniteCategorySliderAdapter infiniteCategorySliderAdapter;


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


        setContentView(R.layout.activity_infinite_category);

////////////////////////////////////////////////////////////////////


        //to get selected itemId from prev page
        presenterCallBack.setSelectedItemId(getIntent().getStringExtra("itemId"));
        //to get selected itemName from prev page
        TextView toolbarTitle = findViewById(R.id.toolbar_title);
        toolbarTitle.setText(getIntent().getStringExtra("itemName"));


        setupToolbarAndNavigationDrawer();


//Search Icon
        findViewById(R.id.ic_search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Bundle transitionAnimation = //to set Transition Animation
                        ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.transition_animation1, R.anim.transition_animation2).toBundle();

                startActivity(new Intent(InfiniteCategoryActivity.this, SearchActivity.class), transitionAnimation);
            }
        });


        //init pullToRefresh
        pullToRefresh = findViewById(R.id.pullToRefresh);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                //Refresh Slider And InfiniteCategories
                presenterCallBack.initSliderAndInfiniteCategories(InfiniteCategoryActivity.this, true);

            }
        });


        //to init slider with one slide to avoid crash
        initSlider();

        //init Slider And InfiniteCategories
        presenterCallBack.initSliderAndInfiniteCategories(InfiniteCategoryActivity.this, false);

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



    public void initSlider() {

        PicassoImageLoadingService picassoImageLoadingService = new PicassoImageLoadingService(this);
        Slider.init(picassoImageLoadingService);

        slider = findViewById(R.id.banner_slider);

        infiniteCategorySliderAdapter = new InfiniteCategorySliderAdapter();

        List<String> slidesUrl = new ArrayList<>();
        slidesUrl.add("placeHolder");

        infiniteCategorySliderAdapter.setSlides(slidesUrl);
        slider.setAdapter(infiniteCategorySliderAdapter);
    }

    @Override
    public void showSlides(final List<Slide> slides) {

        List<String> slidesUrl = new ArrayList<>();

        if (slides != null) {
            slider.showIndicators();

            for (int i = 0; i < slides.size(); i++) {

                slidesUrl.add(slides.get(i).getPicture());
            }

            infiniteCategorySliderAdapter.setSlides(slidesUrl);
            slider.setAdapter(infiniteCategorySliderAdapter);

            slider.setOnSlideClickListener(new OnSlideClickListener() {
                @Override
                public void onSlideClick(int position) {

                    presenterCallBack.slideItemClicked(slides.get(position), InfiniteCategoryActivity.this);
                }
            });
        } else {
            slider.hideIndicators();

            slidesUrl.add("default");

            infiniteCategorySliderAdapter.setSlides(slidesUrl);
            slider.setAdapter(infiniteCategorySliderAdapter);
        }

    }

    private void setupToolbarAndNavigationDrawer() {

        drawerLayout = findViewById(R.id.drawer_layout);
        findViewById(R.id.ic_hamburger).setOnClickListener(new View.OnClickListener() {
            @SuppressLint("RtlHardcoded")
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(Gravity.RIGHT);
            }
        });
        setupNavigationView();
    }

    private void setupNavigationView() {
        navigationView = findViewById(R.id.navigation_view);
        navigationView.setItemIconTintList(null);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_menu_home:
                        Intent intent = new Intent(InfiniteCategoryActivity.this, HomeActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        break;
                    case R.id.navigation_menu_profile:
                        startActivity(new Intent(InfiniteCategoryActivity.this, ProfileActivity.class));
                        drawerLayout.closeDrawers();
                        break;
                    case R.id.navigation_menu_order_management:
                        startActivity(new Intent(InfiniteCategoryActivity.this, OrdersManagementActivity.class));
                        drawerLayout.closeDrawers();
                        break;
                    case R.id.navigation_menu_increase_credit:
                        startActivity(new Intent(InfiniteCategoryActivity.this, IncreaseCreditActivity.class));
                        drawerLayout.closeDrawers();
                        break;
                    case R.id.navigation_menu_favorite_locations:
                        startActivity(new Intent(InfiniteCategoryActivity.this, FavoriteLocationActivity.class));
                        drawerLayout.closeDrawers();
                        break;
                    case R.id.navigation_menu_favorite_experts:
                        startActivity(new Intent(InfiniteCategoryActivity.this, FavoriteExpertActivity.class));
                        drawerLayout.closeDrawers();
                        break;
                    case R.id.navigation_menu_invite_friends:
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://khedmap.co/%D8%AF%D8%B9%D9%88%D8%AA-%D8%AF%D9%88%D8%B3%D8%AA%D8%A7%D9%86/")));
                        drawerLayout.closeDrawers();
                        break;
                    case R.id.navigation_menu_im_expert:
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://khedmap.co/%D9%85%D9%86-%D9%85%D8%AA%D8%AE%D8%B5%D8%B5-%D9%87%D8%B3%D8%AA%D9%85/")));
                        drawerLayout.closeDrawers();
                        break;
                    case R.id.navigation_menu_support:
                        presenterCallBack.callToSupportButtonClicked(InfiniteCategoryActivity.this);
                        drawerLayout.closeDrawers();
                        break;
                    case R.id.navigation_menu_about_us:
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://khedmap.co/%D8%AF%D8%B1%D8%A8%D8%A7%D8%B1%D9%87-%D9%85%D8%A7/")));
                        drawerLayout.closeDrawers();
                        break;
                    case R.id.navigation_menu_logout:
                        drawerLayout.closeDrawers();
                        showLogoutDialog();
                        break;
                }
                return true;
            }
        });


    }


    @Override
    protected void onStart() {

        //to refresh navigationHeader values if Changed
        presenterCallBack.setupNavigationView(this);
        super.onStart();
    }

    @Override
    public void setNavigationHeaderValues(String fullName, String credit, String avatar) {

        View header = navigationView.getHeaderView(0);

        TextView profileName = header.findViewById(R.id.profile_name);
        TextView profileCredit = header.findViewById(R.id.credit);
        ImageView profilePic = header.findViewById(R.id.profile_pic);

        profileName.setText(fullName);
        profileCredit.setText(credit);
        if (!avatar.isEmpty())
            Picasso.get().load(avatar).into(profilePic);
    }


    @Override
    public void showInfiniteCategoriesRecyclerView(List<InfiniteCategory> infiniteCategories) {

        RecyclerView recyclerView = findViewById(R.id.recycler_view_infinite_categories);
        InfiniteCategoryListAdapter.OnItemClickListener listener = new InfiniteCategoryListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(InfiniteCategory item) {

                presenterCallBack.infiniteCategoryItemClicked(item, InfiniteCategoryActivity.this);
            }
        };

        //to set Animation on Items
        LayoutAnimationController animationController = AnimationUtils.loadLayoutAnimation(this, R.anim.layout_animation_slide_right);
        recyclerView.setLayoutAnimation(animationController);

        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(new InfiniteCategoryListAdapter(this, infiniteCategories, listener));
    }


    @Override
    public void showSnackBar(String message) {

        new CustomSnackbar(message, InfiniteCategoryActivity.this, findViewById(R.id.coordinator_layout)).snackbar.show();

    }


    @Override
    public void showProgressBar(boolean showProgressBar) {
        if (showProgressBar)
            pullToRefresh.setRefreshing(true);
        else
            pullToRefresh.setRefreshing(false);

    }


    @SuppressLint("RtlHardcoded")
    @Override
    public void onBackPressed() {

        //to close Drawer if is Open
        if (drawerLayout.isDrawerOpen(Gravity.RIGHT)) {
            drawerLayout.closeDrawers();
            return;
        }

        if (pullToRefresh.isRefreshing()) //To avoid Conflict Lists and crash
            finish();

        if (presenterCallBack.backButtonClicked()) //to check is first list or not
            super.onBackPressed();
    }


    public void showLogoutDialog() {

        final HugeNetAlertDialog alertDialog = new HugeNetAlertDialog.Builder()
                .setTitle("آیا برای خروج اطمینان دارید؟")
                .setPositiveText("بله")
                .setNegativeText("انصراف")
                .setCancellable(false)
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

                presenterCallBack.logOutButtonClicked(InfiniteCategoryActivity.this);
            }
        });

        alertDialog.show(getSupportFragmentManager(), null);
    }

}
