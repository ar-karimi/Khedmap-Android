package ir.hugenet.hugenetdialog;

import android.os.Parcelable;
import android.view.View;

public class OnClickListener {

    public interface OnPositiveButtonClickListener {
        void onClick(View v);
    }

    public interface OnNegativeButtonClickListener {
        void onClick(View v);
    }
}

