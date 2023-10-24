package com.khedmap.khedmap.Order.View;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.Toast;

import com.github.florent37.viewanimator.ViewAnimator;
import com.khedmap.khedmap.InitApplication;
import com.khedmap.khedmap.Order.Adapters.SuggestionListAdapter;
import com.khedmap.khedmap.Order.DataModels.Suggestion;
import com.khedmap.khedmap.Order.SuggestionsContract;
import com.khedmap.khedmap.R;
import com.khedmap.khedmap.Utilities.CustomSnackbar;
import com.khedmap.khedmap.di.component.DaggerActivityComponent;
import com.khedmap.khedmap.di.module.MvpModule;

import java.util.List;

import javax.inject.Inject;


public class SuggestionsFragment extends Fragment implements SuggestionsContract.ViewCallBack {


    @Inject
    SuggestionsContract.PresenterCallBack presenterCallBack;

    @Inject
    Context mContext;

    private View mView;

    private Activity activity;

    private SwipeRefreshLayout pullToRefresh;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_suggestions, container, false);

        //to get parent Activity
        activity = getActivity();

//to configure Dagger
        DaggerActivityComponent.builder()
                .appComponent(InitApplication.get(activity).component())
                .mvpModule(new MvpModule(this))
                .build()
                .inject(this);


//to get Submitted OrderId from prev page
        presenterCallBack.setSubmittedOrderId(activity.getIntent().getStringExtra("submittedOrderId"));


        //init pullToRefresh
        pullToRefresh = mView.findViewById(R.id.pullToRefresh);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                //Refresh InfiniteCategoriesRecyclerView
                presenterCallBack.initSuggestionsRecyclerView(activity);

            }
        });
/*

        //init fab
        FloatingActionButton floatingActionButton = view.findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
*/


//init InfiniteCategoriesRecyclerView
        presenterCallBack.initSuggestionsRecyclerView(activity);


        //to set animation to titleDiscounts
        ViewAnimator
                .animate(mView.findViewById(R.id.loading_text))
                .fadeIn()
                .duration(1500)
                .repeatMode(ViewAnimator.REVERSE)
                .repeatCount(ViewAnimator.INFINITE)
                .start();


        return mView;
    }

    public static SuggestionsFragment newInstance() {

        Bundle args = new Bundle();

        SuggestionsFragment fragment = new SuggestionsFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void showSnackBar(String message) {

        if (mView != null) //to avoid of crash
            new CustomSnackbar(message, activity, mView).snackbar.show();
        else
            Toast.makeText(activity, message, Toast.LENGTH_LONG).show();

    }

    @Override
    public void showProgressBar(boolean showProgressBar) {
        if (showProgressBar)
            pullToRefresh.setRefreshing(true);
        else
            pullToRefresh.setRefreshing(false);

    }


    @Override
    public void showLoadingText(boolean showLoadingText) {
        if (showLoadingText)
            mView.findViewById(R.id.loading_text).setVisibility(View.VISIBLE);
        else
            mView.findViewById(R.id.loading_text).setVisibility(View.GONE);

    }


    @Override
    public void showSuggestionsRecyclerView(List<Suggestion> suggestions) {

        RecyclerView recyclerView = mView.findViewById(R.id.recycler_view_suggestions);
        SuggestionListAdapter.OnItemClickListener listener = new SuggestionListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Suggestion item) {

                presenterCallBack.suggestionItemClicked(item, activity);
            }
        };

        //to set Animation on Items
        LayoutAnimationController animationController = AnimationUtils.loadLayoutAnimation(getContext(), R.anim.layout_animation_slide_right);
        recyclerView.setLayoutAnimation(animationController);

        recyclerView.setLayoutManager(new GridLayoutManager(activity, 1, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(new SuggestionListAdapter(activity, suggestions, listener));
    }


}
