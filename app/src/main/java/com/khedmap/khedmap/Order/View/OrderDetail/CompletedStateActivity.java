package com.khedmap.khedmap.Order.View.OrderDetail;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.khedmap.khedmap.InitApplication;
import com.khedmap.khedmap.Order.Adapters.TeammateListAdapter;
import com.khedmap.khedmap.Order.CompletedStateContract;
import com.khedmap.khedmap.Order.DataModels.Teammate;
import com.khedmap.khedmap.R;
import com.khedmap.khedmap.Utilities.CustomSnackbar;
import com.khedmap.khedmap.di.component.DaggerActivityComponent;
import com.khedmap.khedmap.di.module.MvpModule;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

import javax.inject.Inject;

import io.github.inflationx.calligraphy3.CalligraphyConfig;
import io.github.inflationx.calligraphy3.CalligraphyInterceptor;
import io.github.inflationx.viewpump.ViewPump;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import io.rmiri.skeleton.SkeletonGroup;

public class CompletedStateActivity extends AppCompatActivity implements CompletedStateContract.ViewCallBack {


    @Inject
    CompletedStateContract.PresenterCallBack presenterCallBack;

    @Inject
    Context mContext;

    private LinearLayout orderDetailLinearLayout;

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


        setContentView(R.layout.activity_completed_state);

////////////////////////////////////////////////////////////////////

//init dynamic part of detail page
        orderDetailLinearLayout = findViewById(R.id.order_detail_linear_layout);


        //toolbar back icon
        findViewById(R.id.ic_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


        //to get Submitted OrderId from prev page
        presenterCallBack.setSubmittedOrderId(getIntent().getStringExtra("submittedOrderId"));

        //to get OrderDetail data from prev page
        presenterCallBack.fetchDataAndGenerateOrderDetailItems(getIntent().getStringExtra("data"));

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

        new CustomSnackbar(message, CompletedStateActivity.this, findViewById(R.id.coordinator_layout)).snackbar.show();

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
    public void setStaticPartValues(List<String> orderDetail) {

        TextView subcategory = findViewById(R.id.subcategory);
        TextView serviceTime = findViewById(R.id.service_time);
        TextView address = findViewById(R.id.address);
        TextView status = findViewById(R.id.status);
        TextView price = findViewById(R.id.price);
        TextView startDate = findViewById(R.id.start_date);
        TextView startTime = findViewById(R.id.start_time);
        TextView finishDate = findViewById(R.id.finish_date);
        TextView finishTime = findViewById(R.id.finish_time);


        subcategory.setText(orderDetail.get(0));
        serviceTime.setText(orderDetail.get(1));

        address.setText(orderDetail.get(2));

        status.setText(orderDetail.get(3));

        price.setText(orderDetail.get(4));
        startDate.setText(orderDetail.get(5));
        startTime.setText(orderDetail.get(6));
        finishDate.setText(orderDetail.get(7));
        finishTime.setText(orderDetail.get(8));


    }

    ///////////////////////////////////////
//to add Dynamic Detail items to view

    @Override
    public void setNoDetailItemToShow() {

        findViewById(R.id.no_detail_item_to_show).setVisibility(View.VISIBLE);
    }


    @Override
    public void generateTitleItem(String value) {

        @SuppressLint("InflateParams") LinearLayout titleItem = (LinearLayout) getLayoutInflater().inflate(R.layout.layout_dynamic_title, null, false);
        TextView dynamicTitle = titleItem.findViewById(R.id.dynamic_title);
        dynamicTitle.setText(value);
        orderDetailLinearLayout.addView(titleItem);
    }


    @Override
    public void generateAnswerItem(String value) {

        @SuppressLint("InflateParams") TextView answerItem = (TextView) getLayoutInflater().inflate(R.layout.layout_dynamic_answer, null, false);
        answerItem.setText(value);
        orderDetailLinearLayout.addView(answerItem);
    }


    @Override
    public void generateImageAnswerItem(String url) {

        @SuppressLint("InflateParams") ImageView imageAnswerItem = (ImageView) getLayoutInflater().inflate(R.layout.layout_dynamic_image_answer, null, false);
        orderDetailLinearLayout.addView(imageAnswerItem);
        Picasso.get().load(url).into(imageAnswerItem);
    }

//till here
///////////////////////////////////////


    @Override
    public void setExpertDetailValues(List<String> expertDetail) {

        ImageView profilePic = findViewById(R.id.profile_pic);
        final SkeletonGroup skeletonGroup = findViewById(R.id.skeletonGroup);
        TextView expertName = findViewById(R.id.expert_name);


        if (!expertDetail.get(0).equals("null"))
            Picasso.get().load(expertDetail.get(0)).into(profilePic, new Callback.EmptyCallback() {
                @Override
                public void onSuccess() {

                    skeletonGroup.setShowSkeleton(false);
                    skeletonGroup.finishAnimation();
                }
            });
        else {
            skeletonGroup.setShowSkeleton(false);
            skeletonGroup.finishAnimation();
        }

        expertName.setText(expertDetail.get(1));


    }


    @Override
    public void showTeammatesRecyclerView(List<Teammate> teammates) {

        RecyclerView recyclerView = findViewById(R.id.recycler_view_teammates);

        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(new TeammateListAdapter(this, teammates));
    }


    @Override
    public void setNoTeammateToShow() {

        findViewById(R.id.teammates_title).setVisibility(View.GONE);
        findViewById(R.id.recycler_view_teammates).setVisibility(View.GONE);

    }

    @Override
    public void setFactorImage(String url) {

        ImageView factorImage = findViewById(R.id.factor_image);
        Picasso.get().load(url).into(factorImage);

    }

    //to show AddToFavoriteExpert Button
    @Override
    public void showAddToFavoriteButton(boolean showButton) {

        LinearLayout addToFavoriteExpertButton = findViewById(R.id.add_expert_to_favorite_button);

        if (showButton) {

            addToFavoriteExpertButton.setVisibility(View.VISIBLE);
            addToFavoriteExpertButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    presenterCallBack.addFavoriteExpertButtonClicked(CompletedStateActivity.this);
                }
            });
        } else {

            addToFavoriteExpertButton.setVisibility(View.GONE);
        }

    }
//till here


    @Override
    public void showComment(List<String> commentDetail) {

        if (commentDetail == null) {
            findViewById(R.id.rate_layout).setVisibility(View.GONE);
            findViewById(R.id.comment_layout).setVisibility(View.GONE);
            findViewById(R.id.no_comment_to_show).setVisibility(View.VISIBLE);
            return;
        }

        findViewById(R.id.rate_layout).setVisibility(View.VISIBLE);
        findViewById(R.id.comment_layout).setVisibility(View.VISIBLE);
        findViewById(R.id.no_comment_to_show).setVisibility(View.GONE);

        RatingBar expertRate = findViewById(R.id.expert_rate);
        expertRate.setRating(Integer.parseInt(commentDetail.get(0)));


        if (commentDetail.get(1).equals("null")) { //comment is nullable
            findViewById(R.id.comment_layout).setVisibility(View.GONE);
            return;
        }

        TextView expertComment = findViewById(R.id.expert_comment);
        expertComment.setText(commentDetail.get(1));
    }

    //to show EditComment Button
    @Override
    public void showEditCommentButton(boolean showButton, final boolean isEdit) {

        LinearLayout editCommentButton = findViewById(R.id.edit_comment_button);
        TextView editCommentTitle = findViewById(R.id.edit_comment_title);

        if (showButton) {

            editCommentButton.setVisibility(View.VISIBLE);

            if (isEdit)
                editCommentTitle.setText("ویرایش نظر");
            else
                editCommentTitle.setText("ثبت نظر");

            editCommentButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    presenterCallBack.editCommentButtonClicked(CompletedStateActivity.this, isEdit);
                }
            });
        } else {

            editCommentButton.setVisibility(View.GONE);
        }

    }
//till here


    //to receive new comment values from CommentActivity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    presenterCallBack.commentResultReceived(data.getStringExtra("rate"),
                            data.getStringExtra("comment"));
                }
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}
