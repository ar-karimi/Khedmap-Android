package com.khedmap.khedmap.Order.View;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;


import com.khedmap.khedmap.R;

import io.github.inflationx.calligraphy3.CalligraphyConfig;
import io.github.inflationx.calligraphy3.CalligraphyInterceptor;
import io.github.inflationx.viewpump.ViewPump;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class AnnouncementActivity extends AppCompatActivity {
    public static final String KEY_TITLE = "title";
    public static final String KEY_TEXT = "text";

    private String title;
    private String text;


    //to change font
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }
    //till here, continues ...


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//to change font
        changeFont();


        setContentView(R.layout.activity_announcement);

        Intent intent = getIntent();
        title = intent.getStringExtra(KEY_TITLE);
        text = intent.getStringExtra(KEY_TEXT);

        setupViews();


    }

    private void setupViews() {
        TextView titleTv = findViewById(R.id.tv_announcement_title);
        TextView textTv = findViewById(R.id.tv_announcement_text);

        titleTv.setText(title);
        textTv.setText(text);
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

}
