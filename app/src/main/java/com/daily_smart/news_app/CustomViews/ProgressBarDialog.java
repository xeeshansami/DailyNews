package com.daily_smart.news_app.CustomViews;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.view.Window;
import android.widget.ProgressBar;

import com.daily_smart.news_app.R;

public class ProgressBarDialog {

    private Dialog dialog;
    private ProgressBar progressBar;
    private Context mContext;

    public ProgressBarDialog(Context context) {
        this.mContext = context;
    }

    public void showProgressBar(Activity activity) {
        dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.progress_bar_dialog);
        progressBar = dialog.findViewById(R.id.progressBar);
        progressBar.getIndeterminateDrawable().setColorFilter(Color.RED, PorterDuff.Mode.MULTIPLY);
        dialog.setCancelable(false);
        if (dialog != null && !dialog.isShowing()) {
            dialog.show();
        }
    }

    public void dismissProgressBar(Activity activity) {
        dialog.dismiss();
    }
}
