package com.daily_smart.news_app.CustomViews;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.daily_smart.news_app.R;
import com.daily_smart.news_app.Utilities.GeneralFunctions;

public class PostNewsOptionsDialog implements View.OnClickListener {
    private Context mContext;
    private Dialog dialog;
    private ImageView ivClosePostNewsOptionsDialog;
    private LinearLayout layoutPostImage, layoutPostVideo, layoutPostSocialLinks;
    private PostNewsDialogOptionsInterface postNewsDialogOptionsInterface;
    private TextView txtOne,txtTwo,txtThree,txtFour;

    public PostNewsOptionsDialog(Context context, PostNewsDialogOptionsInterface postNewsDialogOptionsInterface) {
        this.mContext = context;
        this.postNewsDialogOptionsInterface = postNewsDialogOptionsInterface;
    }

    public void showPostNewsDialog() {
        dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.post_news_options_dialog);
        WindowManager.LayoutParams windowManager = dialog.getWindow().getAttributes();
        windowManager.width = android.view.WindowManager.LayoutParams.MATCH_PARENT;
        windowManager.height = WindowManager.LayoutParams.MATCH_PARENT;
        ivClosePostNewsOptionsDialog = dialog.findViewById(R.id.ivClosePostNewsOptionsDialog);
        layoutPostImage = dialog.findViewById(R.id.layoutPostImage);
        layoutPostVideo = dialog.findViewById(R.id.layoutPostVideo);
        layoutPostSocialLinks = dialog.findViewById(R.id.layoutPostSocialLinks);
        txtOne = dialog.findViewById(R.id.txtOne);
        txtTwo = dialog.findViewById(R.id.txtTwo);
        txtThree = dialog.findViewById(R.id.txtThree);
        txtFour = dialog.findViewById(R.id.txtFour);

        Typeface typeface = Typeface.createFromAsset(mContext.getAssets(), "ramabhadra_regular.ttf");
        txtOne.setTypeface(typeface);
        txtTwo.setTypeface(typeface);
        txtThree.setTypeface(typeface);
        txtFour.setTypeface(typeface);

        ivClosePostNewsOptionsDialog.setOnClickListener(this);
        layoutPostImage.setOnClickListener(this);
        layoutPostVideo.setOnClickListener(this);
        layoutPostSocialLinks.setOnClickListener(this);
        dialog.setCancelable(false);
        dialog.show();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ivClosePostNewsOptionsDialog:
                dialog.dismiss();
                break;
            case R.id.layoutPostImage:
                if (postNewsDialogOptionsInterface != null) {
                    postNewsDialogOptionsInterface.postNewsDialogOptionsMethod("Image");
                }
                dialog.dismiss();
                break;
            case R.id.layoutPostVideo:
                if (postNewsDialogOptionsInterface != null) {
                    postNewsDialogOptionsInterface.postNewsDialogOptionsMethod("Video");
                }
                dialog.dismiss();
                break;
            case R.id.layoutPostSocialLinks:
                if (postNewsDialogOptionsInterface != null) {
                    postNewsDialogOptionsInterface.postNewsDialogOptionsMethod("Links");
                }
                dialog.dismiss();
                break;
        }
    }

    public interface PostNewsDialogOptionsInterface {
        void postNewsDialogOptionsMethod(String postNewsType);
    }
}
