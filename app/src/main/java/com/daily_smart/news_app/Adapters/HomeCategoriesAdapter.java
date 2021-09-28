package com.daily_smart.news_app.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.daily_smart.news_app.Activities.CategoryActivity;
import com.daily_smart.news_app.Models.CategoriesModel;
import com.daily_smart.news_app.R;

import java.util.ArrayList;

public class HomeCategoriesAdapter extends RecyclerView.Adapter<HomeCategoriesAdapter.ViewHolder> {
    private Context mContext;
    private ArrayList<CategoriesModel> categoriesList = new ArrayList<>();
    private CategoryClickedListener categoryClickedListener;
    private int selectedPosition = 0;

    public HomeCategoriesAdapter(Context context, ArrayList<CategoriesModel> categoriesList, CategoryClickedListener categoryClickedListener) {
        this.mContext = context;
        this.categoriesList = categoriesList;
        this.categoryClickedListener = categoryClickedListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.home_categories_list_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        final CategoriesModel categoriesModel = categoriesList.get(position);
        holder.txtCategoryName.setText(categoriesModel.getCategoryNameTe());
        Typeface typefaceDesc = Typeface.createFromAsset(mContext.getAssets(), "mallanna_regular.ttf");
        holder.txtCategoryName.setTypeface(typefaceDesc);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedPosition = position;
                notifyDataSetChanged();
                if (categoryClickedListener != null) {
                    categoryClickedListener.categoryClickedMethod(categoriesModel.getId());
                }
            }
        });
        if (selectedPosition == position) {
            holder.txtCategoryName.setBackground(mContext.getResources().getDrawable(R.drawable.selected_item_bg));
            holder.txtCategoryName.setTextColor(mContext.getResources().getColor(R.color.white));
        } else {
            holder.txtCategoryName.setBackground(mContext.getResources().getDrawable(R.drawable.unselected_item_bg));
            holder.txtCategoryName.setTextColor(mContext.getResources().getColor(R.color.black));
        }
    }

    @Override
    public int getItemCount() {
        return categoriesList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView txtCategoryName;
        private LinearLayout layoutCategoryItem;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtCategoryName = itemView.findViewById(R.id.txtCategoryName);
            layoutCategoryItem = itemView.findViewById(R.id.layoutCategoryItem);
        }
    }

    public interface CategoryClickedListener {
        void categoryClickedMethod(String categoryId);
    }
}
