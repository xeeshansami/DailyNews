package com.daily_smart.news_app.Adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.daily_smart.news_app.Models.NewsItemModel;
import com.daily_smart.news_app.R;
import com.daily_smart.news_app.Utilities.Config;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class CategoryListAdapter extends RecyclerView.Adapter<CategoryListAdapter.ViewHolder> {
    private Context mContext;
    private ArrayList<NewsItemModel> recentNewsList = new ArrayList<>();
    private DeleteRecentPostInterface deleteRecentPostInterface;

    public CategoryListAdapter(Context context, ArrayList<NewsItemModel> recentNewsList, DeleteRecentPostInterface deleteRecentPostInterface) {
        this.mContext = context;
        this.recentNewsList = recentNewsList;
        this.deleteRecentPostInterface = deleteRecentPostInterface;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View districtListItem = layoutInflater.inflate(R.layout.category_list_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(districtListItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final NewsItemModel newsItemModel = recentNewsList.get(position);
        holder.txtRecentPostTitle.setText(newsItemModel.getPostTitleTe());
        if (!newsItemModel.getComments_count().isEmpty()) {
            holder.txtCommentsRecentPost.setText(newsItemModel.getComments_count());
        } else {
            holder.txtCommentsRecentPost.setText("0");
        }
        switch (Integer.parseInt(newsItemModel.getStatus())) {
            case 4:
                holder.txtRecentPostStatus.setText(mContext.getResources().getString(R.string.pending));
                holder.txtRecentPostStatus.setTextColor(mContext.getResources().getColor(R.color.yellow));
                break;
            case 1:
                holder.txtRecentPostStatus.setText(mContext.getResources().getString(R.string.published));
                holder.txtRecentPostStatus.setTextColor(mContext.getResources().getColor(R.color.green));
                break;
            case 2:
                holder.txtRecentPostStatus.setText(mContext.getResources().getString(R.string.rejected));
                holder.txtRecentPostStatus.setTextColor(mContext.getResources().getColor(R.color.red));
                break;
            case 3:
                holder.txtRecentPostStatus.setText(mContext.getResources().getString(R.string.hold));
                holder.txtRecentPostStatus.setTextColor(mContext.getResources().getColor(R.color.red));
                break;
            default:
                break;
        }
        if (!newsItemModel.getPostImages().get(0).getPostImage().isEmpty()) {
            Picasso.get()
                    .load(Config.NEWS_IMAGE_URL + newsItemModel.getPostImages().get(0).getPostImage())
                    .placeholder(R.drawable.default_placeholder)
                    .error(R.drawable.default_placeholder)
                    .into(holder.ivRecentPost);
        }

        holder.layoutDeleteRecentPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (deleteRecentPostInterface != null) {
                    deleteRecentPostInterface.deleteRecentPostMethod(newsItemModel.getNews_id());
                }
            }
        });
        holder.layoutReadMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (deleteRecentPostInterface != null) {
                    deleteRecentPostInterface.readMoreRecentPostMethod(newsItemModel.getNews_id(),newsItemModel.getStatus());
                }
            }
        });
        holder.layoutEditPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (deleteRecentPostInterface != null) {
                    deleteRecentPostInterface.editRecentPostMethod(newsItemModel.getNews_id());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return recentNewsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivRecentPost;
        private TextView txtRecentPostTitle, txtRecentPostStatus, txtCommentsRecentPost;
        private LinearLayout layoutDeleteRecentPost, layoutReadMore, layoutEditPost;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivRecentPost = itemView.findViewById(R.id.ivRecentPost);
            txtRecentPostTitle = itemView.findViewById(R.id.txtRecentPostTitle);
            txtRecentPostStatus = itemView.findViewById(R.id.txtRecentPostStatus);
            txtCommentsRecentPost = itemView.findViewById(R.id.txtCommentsRecentPost);
            layoutDeleteRecentPost = itemView.findViewById(R.id.layoutDeleteRecentPost);
            layoutReadMore = itemView.findViewById(R.id.layoutReadMore);
            layoutEditPost = itemView.findViewById(R.id.layoutEditPost);

            Typeface typeface = Typeface.createFromAsset(mContext.getAssets(), "ramabhadra_regular.ttf");
            txtRecentPostTitle.setTypeface(typeface);
        }
    }

    public interface DeleteRecentPostInterface {
        void deleteRecentPostMethod(String newsId);

        void readMoreRecentPostMethod(String newsId,String status);

        void editRecentPostMethod(String newsId);
    }
}
