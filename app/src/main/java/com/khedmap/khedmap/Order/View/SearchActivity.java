package com.khedmap.khedmap.Order.View;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.florent37.viewanimator.ViewAnimator;
import com.khedmap.khedmap.InitApplication;
import com.khedmap.khedmap.Order.Adapters.SearchResultListAdapter;
import com.khedmap.khedmap.Order.DataModels.SearchResult;
import com.khedmap.khedmap.Order.SearchContract;
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

public class SearchActivity extends AppCompatActivity implements SearchContract.ViewCallBack {


    @Inject
    SearchContract.PresenterCallBack presenterCallBack;

    @Inject
    Context mContext;


    private TextView noItemExist;
    private TextView searchResultsTitle;
    private RecyclerView recyclerView;
    private LinearLayout searchLoadingView;
    private ImageView backgroundImage;
    private ImageView searchIcon;

    private boolean isClearIcon = false;

    //to change font
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }
    //till here, continues ...


    @SuppressLint("ClickableViewAccessibility") //for recyclerView.setOnTouchListener
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

        setContentView(R.layout.activity_search);

////////////////////////////////////////////////////////////////////


        presenterCallBack.setActivityContext(this);


        //to get page Items
        noItemExist = findViewById(R.id.no_item_exist);
        recyclerView = findViewById(R.id.recycler_view_search_results);
        searchLoadingView = findViewById(R.id.search_loading_view);
        searchResultsTitle = findViewById(R.id.search_results_title);
        backgroundImage = findViewById(R.id.background_image);
        searchIcon = findViewById(R.id.ic_search);
        //till here


//to close keyboard by tap outside
        findViewById(R.id.coordinator_layout).setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                closeKeyboard();
                return false;
            }
        });
        recyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                closeKeyboard();
                return false;
            }
        });
//till here


        //to set TextWatcher to editText
        final EditText searchEditText = findViewById(R.id.search_edit_text);

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable input) {

                presenterCallBack.textChanged(input.toString());
            }
        });

        searchEditText.setImeOptions(EditorInfo.IME_ACTION_DONE);
        //till here

        searchIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                searchEditText.setText("");
            }
        });


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

    public void closeKeyboard() {

        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (imm != null && findViewById(R.id.coordinator_layout).getWindowToken() != null) {
            imm.hideSoftInputFromWindow(findViewById(R.id.coordinator_layout).getWindowToken(), 0);
        }
    }


    @Override
    public void showSnackBar(String message) {

        new CustomSnackbar(message, SearchActivity.this, findViewById(R.id.coordinator_layout)).snackbar.show();

    }


    @Override
    public void showSearchLoadingView(boolean showSearchLoadingView) {

        if (showSearchLoadingView)
            searchLoadingView.setVisibility(View.VISIBLE);
        else
            searchLoadingView.setVisibility(View.GONE);

    }


    @Override
    public void showLoadingView(boolean showLoadingView) {
        FrameLayout loadingView = findViewById(R.id.loading_view);
        if (showLoadingView)
            loadingView.setVisibility(View.VISIBLE);
        else
            loadingView.setVisibility(View.GONE);

    }


    @Override
    public void showSearchResultsRecyclerView(List<SearchResult> searchResults) {

        SearchResultListAdapter.OnItemClickListener listener = new SearchResultListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(SearchResult item) {

                presenterCallBack.searchResultItemClicked(item, SearchActivity.this);
            }
        };

        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(new SearchResultListAdapter(this, searchResults, listener));
    }


    @Override
    public void setNoItemToShowVisible() {

        noItemExist.setVisibility(View.VISIBLE);
    }

    @Override
    public void setResultListVisible() {

        searchResultsTitle.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.VISIBLE);
    }


    @Override
    public void hideItems() {

        searchResultsTitle.setVisibility(View.GONE);
        noItemExist.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);
        backgroundImage.setVisibility(View.GONE);
        searchLoadingView.setVisibility(View.GONE);
    }

    @Override
    public void showBackgroundImage() {

        backgroundImage.setVisibility(View.VISIBLE);
    }

    @Override
    public void showClearIcon(boolean showIcon) {

        if (showIcon) {
            searchIcon.setImageResource(R.drawable.ic_clear_text);

            //to set animation
            if (!isClearIcon) {
                ViewAnimator
                        .animate(searchIcon)
                        .fadeIn()
                        .duration(500)
                        .start();
                isClearIcon = true;
            }
        } else {
            searchIcon.setImageResource(R.drawable.ic_search_gray);

            //to set animation
            if (isClearIcon) {
                ViewAnimator
                        .animate(searchIcon)
                        .fadeIn()
                        .duration(500)
                        .start();
                isClearIcon = false;
            }
        }
    }
}
