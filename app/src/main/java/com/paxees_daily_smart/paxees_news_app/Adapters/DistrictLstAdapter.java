package com.paxees_daily_smart.paxees_news_app.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.paxees_daily_smart.paxees_news_app.Models.DistrictModel;
import com.paxees_daily_smart.paxees_news_app.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DistrictLstAdapter extends RecyclerView.Adapter<DistrictLstAdapter.ViewHolder> implements Filterable {
    private Context mContext;
    private ArrayList<DistrictModel> districtList = new ArrayList<>();
    private DistrictClickEventInterface districtClickEventInterface;
    private List<DistrictModel> districtFilteredList;

    public DistrictLstAdapter(Context context, ArrayList<DistrictModel> districtList, DistrictClickEventInterface districtClickEventInterface) {
        this.mContext = context;
        this.districtList = districtList;
        this.districtClickEventInterface = districtClickEventInterface;
        districtFilteredList = new ArrayList<>(districtList);

    }

    @Override
    public Filter getFilter() {
        return categoriesListFilter;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View districtListItem = layoutInflater.inflate(R.layout.district_list_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(districtListItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
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
                if (districtClickEventInterface != null) {
                    districtClickEventInterface.districtClickEventMethod(districtModel.getId(), districtModel.getDistrictNameTe());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return districtList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView txtDistrictName, txtDistrictNameInitial;
        private CardView card_view_textView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            card_view_textView = itemView.findViewById(R.id.card_view_textView);
            txtDistrictNameInitial = itemView.findViewById(R.id.txtDistrictNameInitial);
            txtDistrictName = itemView.findViewById(R.id.txtDistrictName);
        }
    }

    public interface DistrictClickEventInterface {
        void districtClickEventMethod(String districtId, String districtName);
    }

    private Filter categoriesListFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<DistrictModel> filteredList = new ArrayList<>();
            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(districtFilteredList);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (DistrictModel item : districtFilteredList) {
                    if (item.getDistrictNameEn().toLowerCase().contains(filterPattern)) {
                        filteredList.add(item);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            districtList.clear();
            districtList.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };
}
