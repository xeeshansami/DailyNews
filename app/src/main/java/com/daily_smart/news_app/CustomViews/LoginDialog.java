package com.daily_smart.news_app.CustomViews;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.daily_smart.news_app.Activities.LoginActivity;
import com.daily_smart.news_app.R;
import com.daily_smart.news_app.Utilities.ShareData;


public class LoginDialog {

    private Context mContext;
    private Dialog dialog;
    private TextView txtLoginClose, txtLogin;
    private ShareData shareData;

    public LoginDialog(Context context) {
        this.mContext = context;
    }

    public void showLoginDialog() {
        dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.login_dialog);
        WindowManager.LayoutParams windowManager = dialog.getWindow().getAttributes();
        windowManager.width = android.view.WindowManager.LayoutParams.MATCH_PARENT;
        windowManager.height = android.view.WindowManager.LayoutParams.WRAP_CONTENT;
        txtLoginClose = dialog.findViewById(R.id.txtLoginClose);
        shareData = new ShareData(mContext);
        txtLogin = dialog.findViewById(R.id.txtLogin);
        dialog.setCancelable(false);
        dialog.show();
        txtLoginClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        txtLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                shareData.setShareDataEmpty();
                mContext.startActivity(new Intent(mContext, LoginActivity.class));
            }
        });
    }
}
