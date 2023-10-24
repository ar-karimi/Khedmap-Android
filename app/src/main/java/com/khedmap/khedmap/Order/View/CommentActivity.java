package com.khedmap.khedmap.Order.View;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.khedmap.khedmap.InitApplication;
import com.khedmap.khedmap.Order.CommentContract;
import com.khedmap.khedmap.R;
import com.khedmap.khedmap.Utilities.CustomSnackbar;
import com.khedmap.khedmap.di.component.DaggerActivityComponent;
import com.khedmap.khedmap.di.module.MvpModule;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import javax.inject.Inject;

import io.github.inflationx.calligraphy3.CalligraphyConfig;
import io.github.inflationx.calligraphy3.CalligraphyInterceptor;
import io.github.inflationx.viewpump.ViewPump;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import io.rmiri.skeleton.SkeletonGroup;
import ir.hugenet.hugenetdialog.HugeNetAlertDialog;
import ir.hugenet.hugenetdialog.OnClickListener;

public class CommentActivity extends AppCompatActivity implements CommentContract.ViewCallBack {


    @Inject
    CommentContract.PresenterCallBack presenterCallBack;

    @Inject
    Context mContext;


    private RatingBar ratingBar;
    private EditText commentEditText;

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


        setContentView(R.layout.activity_comment);

////////////////////////////////////////////////////////////////////


        ratingBar = findViewById(R.id.rating_bar);
        commentEditText = findViewById(R.id.comment_edit_text);


        //to get finished order detail from prev page
        presenterCallBack.setFinishedOrderDetail(getIntent().getStringExtra("orderId")
                , getIntent().getStringExtra("expertName")
                , getIntent().getStringExtra("expertPic")
                , getIntent().getStringExtra("expertId")
                , getIntent().getStringExtra("isFavorite")
                , getIntent().getStringExtra("rate")
                , getIntent().getStringExtra("comment"));


        findViewById(R.id.ic_skip).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                setResult(Activity.RESULT_CANCELED);
                finish();
            }
        });

        findViewById(R.id.submit_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                presenterCallBack.submitButtonClicked(CommentActivity.this, (int) ratingBar.getRating(),
                        commentEditText.getText().toString().trim());

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

        new CustomSnackbar(message, CommentActivity.this, findViewById(R.id.coordinator_layout)).snackbar.show();

    }


    @Override
    public void showLoadingView(boolean showLoadingView) {
        FrameLayout loadingView = findViewById(R.id.loading_view);
        if (showLoadingView)
            loadingView.setVisibility(View.VISIBLE);
        else
            loadingView.setVisibility(View.GONE);

    }


    //to setExpertDetails in page
    @Override
    public void showExpertDetails(String name, String avatar) {

        ImageView expertAvatar = findViewById(R.id.expert_avatar);
        final SkeletonGroup skeletonGroup = findViewById(R.id.skeletonGroup);
        TextView expertName = findViewById(R.id.expert_name);


        if (!avatar.equals("null"))
            Picasso.get().load(avatar).into(expertAvatar, new Callback.EmptyCallback() {
                @Override
                public void onSuccess() {

                    skeletonGroup.setShowSkeleton(false);
                    skeletonGroup.finishAnimation();
                }
            });

        expertName.setText(name);

    }

    @Override
    public void setPrevComment(String rate, String comment) {

        ratingBar.setRating(Integer.parseInt(rate));

        if (!comment.equals("null")) //comment is nullable
            commentEditText.setText(comment);
    }

    @Override
    public void showFavoriteExpertDialog() {

        final HugeNetAlertDialog alertDialog = new HugeNetAlertDialog.Builder()
                .setTitle("آیا مایل به افزودن کارشناس به موارد مورد علاقه\u200Cی خود هستید؟")
                .setPositiveText("بله")
                .setNegativeText("تمایلی ندارم")
                .setCancellable(false)
                .build();

        alertDialog.setNegativeButtonClickListener(new OnClickListener.OnNegativeButtonClickListener() {
            @Override
            public void onClick(View v) {

                presenterCallBack.addFavoriteExpertClicked(CommentActivity.this, false);
            }
        });

        alertDialog.setPositiveButtonClickListener(new OnClickListener.OnPositiveButtonClickListener() {
            @Override
            public void onClick(View v) {

                presenterCallBack.addFavoriteExpertClicked(CommentActivity.this, true);

                alertDialog.dismiss();

            }
        });

        alertDialog.show(getSupportFragmentManager(), null);
    }


    @Override
    public void showToast(String message) {

        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onBackPressed() {

        setResult(Activity.RESULT_CANCELED);
        finish();
    }
}
