package com.khedmap.khedmap.Utilities;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.TextView;

import com.khedmap.khedmap.R;


public class CustomSnackbar {
    public Snackbar snackbar;
    public TextView snkTextView;

    public CustomSnackbar(String message, Context context, View view) {
        snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG);
        snkTextView = snackbar.getView().findViewById(android.support.design.R.id.snackbar_text);
        snkTextView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        snkTextView.setTextColor(context.getResources().getColor(R.color.colorPrimary));
    }

}
