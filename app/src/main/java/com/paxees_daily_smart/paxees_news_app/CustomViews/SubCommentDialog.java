package com.paxees_daily_smart.paxees_news_app.CustomViews;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.paxees_daily_smart.paxees_news_app.R;
import com.paxees_daily_smart.paxees_news_app.Utilities.GeneralFunctions;
import com.paxees_daily_smart.paxees_news_app.Utilities.ShareData;

public class SubCommentDialog {

    private Context mContext;
    private Dialog dialog;
    private TextView txtNewsTitle, txtCancel, txtSubmit;
    private EditText etComments;
    private ShareData shareData;
    public PostSubCommentsInterface postSubCommentsInterface;

    public SubCommentDialog(Context context, PostSubCommentsInterface postSubCommentsInterface) {
        this.mContext = context;
        this.postSubCommentsInterface = postSubCommentsInterface;
    }

    public void showLoginDialog(final String userId, final String newsId, final String commentId, String newsTitle) {
        dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.sub_comment_dialog);
        WindowManager.LayoutParams windowManager = dialog.getWindow().getAttributes();
        windowManager.width = android.view.WindowManager.LayoutParams.MATCH_PARENT;
        windowManager.height = android.view.WindowManager.LayoutParams.WRAP_CONTENT;
        txtNewsTitle = dialog.findViewById(R.id.txtNewsTitle);
        txtNewsTitle.setText(newsTitle);
        Typeface typeface = Typeface.createFromAsset(mContext.getAssets(), "lohit_telugu.ttf");
        txtNewsTitle.setTypeface(typeface);
        etComments = dialog.findViewById(R.id.etComments);
        shareData = new ShareData(mContext);
        txtCancel = dialog.findViewById(R.id.txtCancel);
        txtSubmit = dialog.findViewById(R.id.txtSubmit);
        dialog.setCancelable(false);
        dialog.show();
        txtCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        txtSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etComments.getText().toString().trim().isEmpty()) {
                    GeneralFunctions.showToast("Please write comment",mContext);
                } else {
                    if (postSubCommentsInterface != null) {
                        postSubCommentsInterface.postSubCommentsMethod(dialog, etComments.getText().toString().trim(), userId, newsId, commentId);
                    }
                }
            }
        });
    }

    public interface PostSubCommentsInterface {
        void postSubCommentsMethod(Dialog dialog, String subComments, String userId, String newsId, String commentId);
    }
}
