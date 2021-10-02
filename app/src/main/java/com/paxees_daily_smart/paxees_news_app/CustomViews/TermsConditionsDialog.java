package com.paxees_daily_smart.paxees_news_app.CustomViews;

import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.text.Html;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.paxees_daily_smart.paxees_news_app.R;

public class TermsConditionsDialog {
    private Context mContext;
    private Dialog dialog;
    private ImageView ivCloseTermsDialog;
    private TextView txtTerms;

    public TermsConditionsDialog(Context context) {
        this.mContext = context;
    }

    public void showTermsCondDialog(String termsCondString) {
        dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.terms_cond_dialog_layout);
        WindowManager.LayoutParams windowManager = dialog.getWindow().getAttributes();
        windowManager.width = android.view.WindowManager.LayoutParams.MATCH_PARENT;
        windowManager.height = WindowManager.LayoutParams.MATCH_PARENT;
        ivCloseTermsDialog = dialog.findViewById(R.id.ivCloseTermsDialog);
        txtTerms = dialog.findViewById(R.id.txtTerms);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            txtTerms.setText(Html.fromHtml(termsCondString, Html.FROM_HTML_MODE_COMPACT));
        } else {
            txtTerms.setText(Html.fromHtml(termsCondString));
        }
        ivCloseTermsDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.setCancelable(false);
        dialog.show();
    }

}
