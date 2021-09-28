package com.daily_smart.news_app.CustomViews;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.daily_smart.news_app.Adapters.DistrictListDialogAdapter;
import com.daily_smart.news_app.Adapters.DistrictLstAdapter;
import com.daily_smart.news_app.Models.DistrictModel;
import com.daily_smart.news_app.R;
import com.daily_smart.news_app.Utilities.Config;
import com.daily_smart.news_app.Utilities.ShareData;

import java.util.ArrayList;

public class DistrictListDialog {

    private Context mContext;
    private Dialog dialog;
    private RecyclerView recyclerViewDistrictListDialog;
    private ShareData shareData;
    private ImageView ivCloseEditProfile, ivDoneEditProfile;
    private DistrictListDialogAdapter districtListDialogAdapter;
    private String districtIdString = Config.EMPTY_STRING;
    private String districtNameString = Config.EMPTY_STRING;
    private DistrictListDialogInterface districtListDialogInterface;

    public DistrictListDialog(Context context, DistrictListDialogInterface districtListDialogInterface) {
        this.mContext = context;
        this.districtListDialogInterface = districtListDialogInterface;
    }

    public void showDistrictDialog(ArrayList<DistrictModel> arrayList) {
        dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.district_list_dialog_layout);
        WindowManager.LayoutParams windowManager = dialog.getWindow().getAttributes();
        windowManager.width = android.view.WindowManager.LayoutParams.MATCH_PARENT;
        windowManager.height = WindowManager.LayoutParams.MATCH_PARENT;
        recyclerViewDistrictListDialog = dialog.findViewById(R.id.recyclerViewDistrictListDialog);
        ivCloseEditProfile = dialog.findViewById(R.id.ivCloseEditProfile);
        ivDoneEditProfile = dialog.findViewById(R.id.ivDoneEditProfile);
        recyclerViewDistrictListDialog.setHasFixedSize(true);
        recyclerViewDistrictListDialog.setLayoutManager(new LinearLayoutManager(mContext));
        districtListDialogAdapter = new DistrictListDialogAdapter(mContext, arrayList, new DistrictLstAdapter.DistrictClickEventInterface() {
            @Override
            public void districtClickEventMethod(String districtId, String districtName) {
                districtIdString = districtId;
                districtNameString = districtName;
            }
        });
        recyclerViewDistrictListDialog.setAdapter(districtListDialogAdapter);
        shareData = new ShareData(mContext);
        ivCloseEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        ivDoneEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                if (districtListDialogInterface != null) {
                    districtListDialogInterface.districtListDialogMethod(districtIdString, districtNameString);
                }
            }
        });

        dialog.setCancelable(false);
        dialog.show();
    }

    public interface DistrictListDialogInterface {
        void districtListDialogMethod(String districtId, String districtName);
    }
}
