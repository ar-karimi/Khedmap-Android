package ir.hugenet.hugenetdialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

public class HugeNetAlertDialog extends DialogFragment {
    private static final String EXTRA_KEY_TITLE = "title";
    private static final String EXTRA_KEY_SUBTITLE = "subtitle";
    private static final String EXTRA_KEY_POSITIVE_TEXT = "positive_text";
    private static final String EXTRA_KEY_POSITIVE_BACKGROUND = "positive_background";
    private static final String EXTRA_KEY_NEGATIVE_TEXT = "negative_text";
    private static final String EXTRA_KEY_NEGATIVE_BACKGROUND = "negative_background";
    private static final String EXTRA_KEY_BACKGROUND_COLOR = "background_color";
    private static final String EXTRA_KEY_TITLE_TEXT_COLOR = "title_text_color";
    private static final String EXTRA_KEY_SUBTITLE_TEXT_COLOR = "subtitle_text_color";
    private static final String EXTRA_KEY_POSITIVE_TEXT_COLOR = "positive_text_color";
    private static final String EXTRA_KEY_NEGATIVE_TEXT_COLOR = "negative_text_color";

    private View btnContainer;
    private View rootView;
    private ImageView imageView;
    private TextView titleTextView;
    private TextView subTitleTextView;
    private Button positiveButton;
    private Button negativeButton;
    private FrameLayout frameLayout;

    private int icon;
    private Drawable image;
    private String title;
    private String subtitle;
    private String positiveText;
    private int positiveBackground;
    private String negativeText;
    private int negativeBackground;
    private int backgroundColor;
    private int titleTextColor;
    private int subtitleTextColor;
    private int positiveTextColor;
    private int negativeTextColor;
    private View views;

    private OnClickListener.OnPositiveButtonClickListener positiveButtonClickListener;
    private OnClickListener.OnNegativeButtonClickListener negativeButtonClickListener;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Bundle bundle = getArguments();

        if (bundle != null) {
            title = bundle.getString(EXTRA_KEY_TITLE);
            subtitle = bundle.getString(EXTRA_KEY_SUBTITLE);
            positiveText = bundle.getString(EXTRA_KEY_POSITIVE_TEXT);
            positiveBackground = bundle.getInt(EXTRA_KEY_POSITIVE_BACKGROUND);
            negativeText = bundle.getString(EXTRA_KEY_NEGATIVE_TEXT);
            negativeBackground = bundle.getInt(EXTRA_KEY_NEGATIVE_BACKGROUND);
            backgroundColor = bundle.getInt(EXTRA_KEY_BACKGROUND_COLOR);
            titleTextColor = bundle.getInt(EXTRA_KEY_TITLE_TEXT_COLOR);
            subtitleTextColor = bundle.getInt(EXTRA_KEY_SUBTITLE_TEXT_COLOR);
            positiveTextColor = bundle.getInt(EXTRA_KEY_POSITIVE_TEXT_COLOR);
            negativeTextColor = bundle.getInt(EXTRA_KEY_NEGATIVE_TEXT_COLOR);

        }
        super.onCreate(savedInstanceState);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        rootView = LayoutInflater.from(getContext()).inflate(R.layout.layout_dialog, null, false);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext())
                .setView(rootView);
        setupViews();
        bindData();
        return builder.create();
    }

    private void bindData() {
        if (image != null) {
            imageView.setVisibility(View.VISIBLE);
            imageView.setImageDrawable(image);
        }

        if (title != null) {
            titleTextView.setVisibility(View.VISIBLE);
            titleTextView.setText(title);
        }

        if (subtitle != null) {
            subTitleTextView.setVisibility(View.VISIBLE);
            subTitleTextView.setText(subtitle);
        }

        if (positiveText != null) {
            btnContainer.setVisibility(View.VISIBLE);
            positiveButton.setVisibility(View.VISIBLE);
            positiveButton.setText(positiveText);
        }

        if (positiveBackground != 0) {
            positiveButton.setBackgroundColor(positiveBackground);
        }

        if (negativeText != null) {
            btnContainer.setVisibility(View.VISIBLE);
            negativeButton.setVisibility(View.VISIBLE);
            negativeButton.setText(negativeText);
        }

        if (views != null) {
            if (views.getParent() != null)
                ((ViewGroup) views.getParent()).removeAllViews();
            frameLayout.addView(views);
        }

        if (negativeBackground != 0) {
            negativeButton.setBackgroundColor(negativeBackground);
        }

        if (backgroundColor != 0) {
            rootView.setBackgroundColor(backgroundColor);
        }

        if (titleTextColor != 0) {
            titleTextView.setTextColor(titleTextColor);
        }

        if (subtitleTextColor != 0) {
            subTitleTextView.setTextColor(subtitleTextColor);
        }

        if (positiveTextColor != 0) {
            positiveButton.setTextColor(positiveTextColor);
        }

        if (negativeTextColor != 0) {
            negativeButton.setTextColor(negativeTextColor);
        }

        positiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (positiveButtonClickListener != null)
                    positiveButtonClickListener.onClick(v);
            }
        });

        negativeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (negativeButtonClickListener != null)
                    negativeButtonClickListener.onClick(v);
            }
        });


    }

    private void setupViews() {
        btnContainer = rootView.findViewById(R.id.ll_btnContainer);
        imageView = rootView.findViewById(R.id.image);
        titleTextView = rootView.findViewById(R.id.title);
        subTitleTextView = rootView.findViewById(R.id.subtitle);
        positiveButton = rootView.findViewById(R.id.btn_positive);
        negativeButton = rootView.findViewById(R.id.btn_negative);
        frameLayout = rootView.findViewById(R.id.frame_container);

    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public void setImage(Drawable image) {
        this.image = image;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public void setPositiveText(String positiveText) {
        btnContainer.setVisibility(View.VISIBLE);
        positiveButton.setVisibility(View.VISIBLE);
        this.positiveText = positiveText;
    }

    public void setPositiveBackground(int positiveBackground) {
        this.positiveBackground = positiveBackground;
    }

    public void setNegativeText(String negativeText) {
        negativeButton.setVisibility(View.VISIBLE);
        btnContainer.setVisibility(View.VISIBLE);
        this.negativeText = negativeText;
    }

    public void setNegativeBackground(int negativeBackground) {
        this.negativeBackground = negativeBackground;
    }

    public void setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public void setTitleTextColor(int titleTextColor) {
        this.titleTextColor = titleTextColor;
    }

    public void setSubtitleTextColor(int subtitleTextColor) {
        this.subtitleTextColor = subtitleTextColor;
    }

    public void setPositiveTextColor(int positiveTextColor) {
        this.positiveTextColor = positiveTextColor;
    }

    public void setNegativeTextColor(int negativeTextColor) {
        this.negativeTextColor = negativeTextColor;
    }

    public void setViews(View views) {
        this.views = views;
    }

    public void setPositiveButtonClickListener(OnClickListener.OnPositiveButtonClickListener positiveButtonClickListener) {
        this.positiveButtonClickListener = positiveButtonClickListener;
    }

    public void setNegativeButtonClickListener(OnClickListener.OnNegativeButtonClickListener negativeButtonClickListener) {
        this.negativeButtonClickListener = negativeButtonClickListener;
    }

    public static HugeNetAlertDialog newInstance(Builder builder) {

        Bundle args = new Bundle();

        args.putString(EXTRA_KEY_TITLE, builder.getTitle());
        args.putString(EXTRA_KEY_SUBTITLE, builder.getSubtitle());
        args.putString(EXTRA_KEY_POSITIVE_TEXT, builder.getPositiveText());
        args.putInt(EXTRA_KEY_POSITIVE_BACKGROUND, builder.getPositiveBackground());
        args.putString(EXTRA_KEY_NEGATIVE_TEXT, builder.getNegativeText());
        args.putInt(EXTRA_KEY_NEGATIVE_BACKGROUND, builder.getNegativeBackground());
        args.putInt(EXTRA_KEY_BACKGROUND_COLOR, builder.getBackgroundColor());
        args.putInt(EXTRA_KEY_TITLE_TEXT_COLOR, builder.getTitleTextColor());
        args.putInt(EXTRA_KEY_SUBTITLE_TEXT_COLOR, builder.getSubtitleTextColor());
        args.putInt(EXTRA_KEY_POSITIVE_TEXT_COLOR, builder.getPositiveTextColor());
        args.putInt(EXTRA_KEY_NEGATIVE_TEXT_COLOR, builder.getNegativeTextColor());

        HugeNetAlertDialog fragment = new HugeNetAlertDialog();
        fragment.setArguments(args);
        fragment.setViews(builder.getViews());
        fragment.setPositiveButtonClickListener(builder.getPositiveButtonClickListener());
        fragment.setNegativeButtonClickListener(builder.getNegativeButtonClickListener());
        fragment.setCancelable(builder.isCancellable());
        fragment.setImage(builder.getImage());
        return fragment;
    }

    public static class Builder {
        private Drawable image;
        private String title;
        private String subtitle;
        private String positiveText;
        private int positiveBackground;
        private String negativeText;
        private int negativeBackground;
        private View views;

        private int backgroundColor;
        private int titleTextColor;
        private int subtitleTextColor;
        private int positiveTextColor;
        private int negativeTextColor;

        private boolean cancellable;

        public boolean isCancellable() {
            return cancellable;
        }

        public Builder setCancellable(boolean cancellable) {
            this.cancellable = cancellable;
            return this;
        }

        private OnClickListener.OnPositiveButtonClickListener positiveButtonClickListener;
        private OnClickListener.OnNegativeButtonClickListener negativeButtonClickListener;

        public Drawable getImage() {
            return image;
        }

        public Builder setImage(Drawable image) {
            this.image = image;
            return this;
        }

        public String getTitle() {
            return title;
        }

        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        public String getSubtitle() {
            return subtitle;
        }

        public Builder setSubtitle(String subtitle) {
            this.subtitle = subtitle;
            return this;
        }

        public String getPositiveText() {
            return positiveText;
        }

        public Builder setPositiveText(String positiveText) {
            this.positiveText = positiveText;
            return this;
        }

        public int getPositiveBackground() {
            return positiveBackground;
        }

        public Builder setPositiveBackground(int positiveBackground) {
            this.positiveBackground = positiveBackground;
            return this;
        }

        public String getNegativeText() {
            return negativeText;
        }

        public Builder setNegativeText(String negativeText) {
            this.negativeText = negativeText;
            return this;
        }

        public int getNegativeBackground() {
            return negativeBackground;
        }

        public Builder setNegativeBackground(int negativeBackground) {
            this.negativeBackground = negativeBackground;
            return this;
        }

        public View getViews() {
            return views;
        }

        public Builder setViews(View views) {
            this.views = views;
            return this;
        }

        public int getBackgroundColor() {
            return backgroundColor;
        }

        public Builder setBackgroundColor(int backgroundColor) {
            this.backgroundColor = backgroundColor;
            return this;
        }

        public int getTitleTextColor() {
            return titleTextColor;
        }

        public Builder setTitleTextColor(int titleTextColor) {
            this.titleTextColor = titleTextColor;
            return this;
        }

        public int getSubtitleTextColor() {
            return subtitleTextColor;
        }

        public Builder setSubtitleTextColor(int subtitleTextColor) {
            this.subtitleTextColor = subtitleTextColor;
            return this;
        }

        public int getPositiveTextColor() {
            return positiveTextColor;
        }

        public Builder setPositiveTextColor(int positiveTextColor) {
            this.positiveTextColor = positiveTextColor;
            return this;
        }

        public int getNegativeTextColor() {
            return negativeTextColor;
        }

        public Builder setNegativeTextColor(int negativeTextColor) {
            this.negativeTextColor = negativeTextColor;
            return this;
        }

        public OnClickListener.OnPositiveButtonClickListener getPositiveButtonClickListener() {
            return positiveButtonClickListener;
        }

        public Builder setPositiveButtonClickListener(OnClickListener.OnPositiveButtonClickListener positiveButtonClickListener) {
            this.positiveButtonClickListener = positiveButtonClickListener;
            return this;
        }

        public OnClickListener.OnNegativeButtonClickListener getNegativeButtonClickListener() {
            return negativeButtonClickListener;
        }

        public Builder setNegativeButtonClickListener(OnClickListener.OnNegativeButtonClickListener negativeButtonClickListener) {
            this.negativeButtonClickListener = negativeButtonClickListener;
            return this;
        }

        public HugeNetAlertDialog build() {
            return HugeNetAlertDialog.newInstance(this);
        }
    }
}
