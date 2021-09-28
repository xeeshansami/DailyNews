package com.daily_smart.news_app.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.daily_smart.news_app.Models.DistrictModel;
import com.daily_smart.news_app.R;

import java.util.ArrayList;
import java.util.Random;

public class DistrictListDialogAdapter extends RecyclerView.Adapter<DistrictListDialogAdapter.ViewHolder> {
    private Context mContext;
    private ArrayList<DistrictModel> districtList = new ArrayList<>();
    private DistrictLstAdapter.DistrictClickEventInterface districtClickEventInterface;
    private int selectedPosition = 0;

    public DistrictListDialogAdapter(Context context, ArrayList<DistrictModel> districtList, DistrictLstAdapter.DistrictClickEventInterface districtClickEventInterface) {
        this.mContext = context;
        this.districtList = districtList;
        this.districtClickEventInterface = districtClickEventInterface;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View districtListItem = layoutInflater.inflate(R.layout.districtlist_dialog_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(districtListItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        final DistrictModel districtModel = districtList.get(position);
        Random random = new Random();
        int backgroundColor = Color.argb(255, random.nextInt(256), random.nextInt(256), random.nextInt(256));
        holder.card_view_textView.setBackgroundColor(backgroundColor);
        if (!districtModel.getDistrictNameEn().isEmpty() && districtModel.getDistrictNameEn() != null) {
            String districtName = districtModel.getDistrictNameEn();
            String districtInitial = String.valueOf(districtName.charAt(0));
            holder.txtDistrictNameInitial.setText(districtInitial);
        }
        holder.txtDistrictName.setText(districtModel.getDistrictNameTe());
        Typeface typeface = Typeface.createFromAsset(mContext.getAssets(), "ramabhadra_regular.ttf");
        holder.txtDistrictName.setTypeface(typeface);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedPosition = position;
                notifyDataSetChanged();
                if (districtClickEventInterface != null) {
                    districtClickEventInterface.districtClickEventMethod(districtModel.getId(), districtModel.getDistrictNameTe());
                }
            }
        });
        if (selectedPosition == position) {
            holder.ivDistrictSelected.setVisibility(View.VISIBLE);
        } else {
            holder.ivDistrictSelected.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return districtList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView txtDistrictName, txtDistrictNameInitial;
        private CardView card_view_textView;
        private ImageView ivDistrictSelected;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            card_view_textView = itemView.findViewById(R.id.card_view_textView);
            txtDistrictNameInitial = itemView.findViewById(R.id.txtDistrictNameInitial);
            txtDistrictName = itemView.findViewById(R.id.txtDistrictName);
            ivDistrictSelected = itemView.findViewById(R.id.ivDistrictSelected);
        }
    }

    public interface DistrictClickEventInterface {
        void districtClickEventMethod(String districtId, String districtName);
    }
}
