package com.khedmap.khedmap.Order.View;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextPaint;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.amlcurran.showcaseview.OnShowcaseEventListener;
import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
import com.khedmap.khedmap.InitApplication;
import com.khedmap.khedmap.Order.Adapters.BottomSheetCategoryListAdapter;
import com.khedmap.khedmap.Order.Adapters.CategoryListAdapter;
import com.khedmap.khedmap.Order.Adapters.MainSliderAdapter;
import com.khedmap.khedmap.Order.Adapters.MiddleCategoryListAdapter;
import com.khedmap.khedmap.Order.Adapters.PicassoImageLoadingService;
import com.khedmap.khedmap.Order.DataModels.BottomSheetCategory;
import com.khedmap.khedmap.Order.DataModels.Category;
import com.khedmap.khedmap.Order.DataModels.MiddleCategory;
import com.khedmap.khedmap.Order.DataModels.Slide;
import com.khedmap.khedmap.Order.HomeContract;
import com.khedmap.khedmap.R;
import com.khedmap.khedmap.Utilities.CustomSnackbar;
import com.khedmap.khedmap.di.component.DaggerActivityComponent;
import com.khedmap.khedmap.di.module.MvpModule;
import com.squareup.picasso.Picasso;

import org.michaelbel.bottomsheet.BottomSheet;

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

public class HomeActivity extends AppCompatActivity implements HomeContract.ViewCallBack {


    @Inject
    HomeContract.PresenterCallBack presenterCallBack;

    @Inject
    Context mContext;

    private NavigationView navigationView;

    private SwipeRefreshLayout pullToRefresh;

    private DrawerLayout drawerLayout;

    private BottomSheet.Builder builder;
    private View bottomSheetView;

    private boolean isOpenBottomSheet = false;

    private RecyclerView middleCategoriesRecyclerView;

    private boolean isExitTapped = false;

    private Slider slider;
    private MainSliderAdapter mainSliderAdapter;

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


        setContentView(R.layout.activity_home);

////////////////////////////////////////////////////////////////////


        setupToolbarAndNavigationDrawer();

//Search Icon
        findViewById(R.id.ic_search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Bundle transitionAnimation = //to set Transition Animation
                        ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.transition_animation1, R.anim.transition_animation2).toBundle();

                startActivity(new Intent(HomeActivity.this, SearchActivity.class), transitionAnimation);
            }
        });


//init pullToRefresh
        pullToRefresh = findViewById(R.id.pullToRefresh);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                //Refresh Slider and CategoriesRecyclerView
                presenterCallBack.initSliderAndCategories(HomeActivity.this);

            }
        });


        //to init slider with one slide to avoid crash
        initSlider();

        //init Slider and CategoriesRecyclerView
        presenterCallBack.initSliderAndCategories(this);

        //to show tour guides for first time
        presenterCallBack.showTourGuides(this);

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

        mainSliderAdapter = new MainSliderAdapter();

        List<String> slidesUrl = new ArrayList<>();
        slidesUrl.add("placeHolder");

        mainSliderAdapter.setSlides(slidesUrl);
        slider.setAdapter(mainSliderAdapter);
    }

    @Override
    public void showSlides(final List<Slide> slides) {

        List<String> slidesUrl = new ArrayList<>();

        if (slides != null) {
            slider.showIndicators();

            for (int i = 0; i < slides.size(); i++) {

                slidesUrl.add(slides.get(i).getPicture());
            }

            mainSliderAdapter.setSlides(slidesUrl);
            slider.setAdapter(mainSliderAdapter);

            slider.setOnSlideClickListener(new OnSlideClickListener() {
                @Override
                public void onSlideClick(int position) {

                    presenterCallBack.slideItemClicked(slides.get(position), HomeActivity.this);
                }
            });
        } else {
            slider.hideIndicators();

            slidesUrl.add("default");

            mainSliderAdapter.setSlides(slidesUrl);
            slider.setAdapter(mainSliderAdapter);
        }

    }

    @Override
    public void showCategoriesRecyclerView(List<Category> categories) {

        RecyclerView recyclerView = findViewById(R.id.recycler_view_categories);
        CategoryListAdapter.OnItemClickListener listener = new CategoryListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Category item) {

                presenterCallBack.categoryItemClicked(item, HomeActivity.this);
            }
        };

        recyclerView.setLayoutManager(new GridLayoutManager(this, 2, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(new CategoryListAdapter(this, categories, listener));
    }


    @Override
    public void initBottomSheetView() {

        builder = new BottomSheet.Builder(HomeActivity.this).setView(R.layout.layout_bottomsheet);
        bottomSheetView = builder.getView();

        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {

                isOpenBottomSheet = false;
            }
        });
    }

    @Override
    public void showBottomSheet() {

        if (!isOpenBottomSheet) {
            builder.show();
            isOpenBottomSheet = true;
        }
    }

    @Override
    public void showBottomSheetCategoriesRecyclerView(List<BottomSheetCategory> bottomSheetCategories, int itemPosition) {


        RecyclerView recyclerView = bottomSheetView.findViewById(R.id.recycler_view_bottom_sheet_categories);
        BottomSheetCategoryListAdapter.OnItemClickListener listener = new BottomSheetCategoryListAdapter.OnItemClickListener() {
            @Override
            public boolean onItemClick(BottomSheetCategory item) {

                return presenterCallBack.bottomSheetCategoryItemClicked(item, HomeActivity.this); //to verify that ResponseReceived
            }
        };
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setAdapter(new BottomSheetCategoryListAdapter(this, bottomSheetCategories, listener, itemPosition));


        recyclerView.scrollToPosition(itemPosition);

    }


    @Override
    public void showMiddleCategoriesRecyclerView(List<MiddleCategory> middleCategories) {

        middleCategoriesRecyclerView = bottomSheetView.findViewById(R.id.recycler_view_middle_categories);
        MiddleCategoryListAdapter.OnItemClickListener listener = new MiddleCategoryListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(MiddleCategory item) {

                presenterCallBack.middleCategoryItemClicked(item, HomeActivity.this);
            }
        };
        middleCategoriesRecyclerView.setLayoutManager(new GridLayoutManager(this, 3, LinearLayoutManager.VERTICAL, false));
        middleCategoriesRecyclerView.setAdapter(new MiddleCategoryListAdapter(this, middleCategories, listener));

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
        navigationView.setItemIconTintList(null); //set navigation icon colorful

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_menu_home:
                        drawerLayout.closeDrawers();
                        break;
                    case R.id.navigation_menu_profile:
                        startActivity(new Intent(HomeActivity.this, ProfileActivity.class));
                        drawerLayout.closeDrawers();
                        break;
                    case R.id.navigation_menu_order_management:
                        startActivity(new Intent(HomeActivity.this, OrdersManagementActivity.class));
                        drawerLayout.closeDrawers();
                        break;
                    case R.id.navigation_menu_increase_credit:
                        startActivity(new Intent(HomeActivity.this, IncreaseCreditActivity.class));
                        drawerLayout.closeDrawers();
                        break;
                    case R.id.navigation_menu_favorite_locations:
                        startActivity(new Intent(HomeActivity.this, FavoriteLocationActivity.class));
                        drawerLayout.closeDrawers();
                        break;
                    case R.id.navigation_menu_favorite_experts:
                        startActivity(new Intent(HomeActivity.this, FavoriteExpertActivity.class));
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
                        presenterCallBack.callToSupportButtonClicked(HomeActivity.this);
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
    public void showSnackBar(String message) {

        new CustomSnackbar(message, HomeActivity.this, findViewById(R.id.coordinator_layout)).snackbar.show();

    }

    @Override
    public void showProgressBar(boolean showProgressBar) {

        if (showProgressBar)
            pullToRefresh.setRefreshing(true);
        else
            pullToRefresh.setRefreshing(false);
    }

    @Override
    public void showBottomSheetProgressBar(boolean showProgressBar) {

        ProgressBar bottomSheetProgressBar = bottomSheetView.findViewById(R.id.bottom_sheet_progressbar);

        if (showProgressBar) {
            middleCategoriesRecyclerView.setVisibility(View.GONE);
            bottomSheetProgressBar.setVisibility(View.VISIBLE);
        } else {
            bottomSheetProgressBar.setVisibility(View.GONE);
            middleCategoriesRecyclerView.setVisibility(View.VISIBLE);
        }
    }


    @SuppressLint("RtlHardcoded")
    @Override
    public void onBackPressed() {

        //to close Drawer if is Open
        if (drawerLayout.isDrawerOpen(Gravity.RIGHT)) {
            drawerLayout.closeDrawers();
            return;
        }

        if (!isExitTapped) {
            Toast.makeText(this, "برای خروج دوباره کلید بازگشت را بزنید", Toast.LENGTH_LONG).show();
            isExitTapped = true;

            int EXIT_COUNTER_LENGTH = 4000;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    isExitTapped = false;
                }
            }, EXIT_COUNTER_LENGTH);
        } else
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

                presenterCallBack.logOutButtonClicked(HomeActivity.this);
            }
        });

        alertDialog.show(getSupportFragmentManager(), null);
    }

    @Override
    public void showSearchTourGuide() {

        Button tourGuideButton = new Button(this);
        tourGuideButton.setText("فهمیدم");
        tourGuideButton.setTextColor(getResources().getColor(R.color.colorWhite));
        tourGuideButton.setBackgroundResource(R.drawable.round_button_tour_guide);
        tourGuideButton.setTypeface(Typeface.createFromAsset(getAssets(), "IRANSansMobileFonts/IRANSansMobile(FaNum).ttf"));

        TextPaint titlePaint = new TextPaint();
        titlePaint.setTypeface(Typeface.createFromAsset(getAssets(), "IRANSansMobileFonts/IRANSansMobile(FaNum)_Medium.ttf"));
        TextPaint textPaint = new TextPaint();
        textPaint.setTypeface(Typeface.createFromAsset(getAssets(), "IRANSansMobileFonts/IRANSansMobile(FaNum).ttf"));


        new ShowcaseView.Builder(this)
                .setTarget(new ViewTarget(findViewById(R.id.ic_search)))
                .setContentTitle("جستجو")
                .setContentText("در این قسمت می\u200Cتوانید خدمت موردنظر خود را جستجو کنید.")
                .setContentTitlePaint(titlePaint)
                .setContentTextPaint(textPaint)
                .hideOnTouchOutside()
                .setStyle(R.style.CustomShowcaseTheme)
                .withNewStyleShowcase()
                .replaceEndButton(tourGuideButton)
                .setShowcaseEventListener(new OnShowcaseEventListener() {
                    @Override
                    public void onShowcaseViewHide(ShowcaseView showcaseView) {

                    }

                    @Override
                    public void onShowcaseViewDidHide(ShowcaseView showcaseView) {

                        showNavigationTourGuide();
                    }

                    @Override
                    public void onShowcaseViewShow(final ShowcaseView showcaseView) {

                        //to hide by click on targeted view
                        showcaseView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                showcaseView.hide();
                            }
                        });
                    }

                    @Override
                    public void onShowcaseViewTouchBlocked(MotionEvent motionEvent) {

                    }
                })
                .build();

    }

    private void showNavigationTourGuide() {

        Button tourGuideButton = new Button(this);
        tourGuideButton.setText("فهمیدم");
        tourGuideButton.setTextColor(getResources().getColor(R.color.colorWhite));
        tourGuideButton.setBackgroundResource(R.drawable.round_button_tour_guide);
        tourGuideButton.setTypeface(Typeface.createFromAsset(getAssets(), "IRANSansMobileFonts/IRANSansMobile(FaNum).ttf"));

        TextPaint titlePaint = new TextPaint();
        titlePaint.setTypeface(Typeface.createFromAsset(getAssets(), "IRANSansMobileFonts/IRANSansMobile(FaNum)_Medium.ttf"));
        TextPaint textPaint = new TextPaint();
        textPaint.setTypeface(Typeface.createFromAsset(getAssets(), "IRANSansMobileFonts/IRANSansMobile(FaNum).ttf"));

        new ShowcaseView.Builder(this)
                .setTarget(new ViewTarget(findViewById(R.id.ic_hamburger)))
                .setContentTitle("منوی گزینه\u200Cهای بیشتر")
                .setContentText("در این قسمت می\u200Cتوانید به بخش\u200Cهای مختلف مانند مدیریت سفارش\u200Cها ، افزایش اعتبار و ... دسترسی داشته باشید.")
                .setContentTitlePaint(titlePaint)
                .setContentTextPaint(textPaint)
                .hideOnTouchOutside()
                .setStyle(R.style.CustomShowcaseTheme)
                .withNewStyleShowcase()
                .replaceEndButton(tourGuideButton)
                .setShowcaseEventListener(new OnShowcaseEventListener() {
                    @Override
                    public void onShowcaseViewHide(ShowcaseView showcaseView) {

                    }

                    @Override
                    public void onShowcaseViewDidHide(ShowcaseView showcaseView) {

                    }

                    @Override
                    public void onShowcaseViewShow(final ShowcaseView showcaseView) {

                        //to hide by click on targeted view
                        showcaseView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                showcaseView.hide();
                            }
                        });
                    }

                    @Override
                    public void onShowcaseViewTouchBlocked(MotionEvent motionEvent) {

                    }
                })
                .build();
    }

}
