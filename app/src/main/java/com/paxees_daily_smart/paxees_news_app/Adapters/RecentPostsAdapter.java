package com.paxees_daily_smart.paxees_news_app.Adapters;

import android.annotation.SuppressLint;
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

import com.paxees_daily_smart.paxees_news_app.Models.NewsItemModel;
import com.paxees_daily_smart.paxees_news_app.R;
import com.paxees_daily_smart.paxees_news_app.Utilities.Config;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class RecentPostsAdapter extends RecyclerView.Adapter<RecentPostsAdapter.ViewHolder> {
    private Context mContext;
    private ArrayList<NewsItemModel> newsItemModelArrayList;
    private DeleteRecentPostInterface deleteRecentPostInterface;

    public RecentPostsAdapter(Context context, ArrayList<NewsItemModel> newsItemModelArrayList, DeleteRecentPostInterface deleteRecentPostInterface) {
        this.mContext = context;
        this.newsItemModelArrayList = newsItemModelArrayList;
        this.deleteRecentPostInterface = deleteRecentPostInterface;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.recent_post_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(listItem);
        return viewHolder;
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final NewsItemModel newsItemModel = newsItemModelArrayList.get(position);
        holder.txtRecentPostNewsTitle.setText(newsItemModel.getPostTitleTe());
        switch (Integer.parseInt(newsItemModel.getStatus())) {
            case 4:
                holder.txtRecentPostNewsStatus.setText(mContext.getResources().getString(R.string.pending));
                holder.txtRecentPostNewsStatus.setTextColor(mContext.getResources().getColor(R.color.yellow));
                break;
            case 1:
                holder.txtRecentPostNewsStatus.setText(mContext.getResources().getString(R.string.published));
                holder.txtRecentPostNewsStatus.setTextColor(mContext.getResources().getColor(R.color.green));
                break;
            case 2:
                holder.txtRecentPostNewsStatus.setText(mContext.getResources().getString(R.string.rejected));
                holder.txtRecentPostNewsStatus.setTextColor(mContext.getResources().getColor(R.color.red));
                break;
            case 3:
                holder.txtRecentPostNewsStatus.setText(mContext.getResources().getString(R.string.hold));
                holder.txtRecentPostNewsStatus.setTextColor(mContext.getResources().getColor(R.color.red));
                break;
            default:
                break;
        }

        if (!newsItemModel.getComments_count().isEmpty()) {
            holder.txtCommentsRecentPost.setText(newsItemModel.getComments_count());
        } else {
            holder.txtCommentsRecentPost.setText("0");
        }
        if (!newsItemModel.getPostImages().get(0).getPostImage().isEmpty()) {
            Picasso.get()
                    .load(Config.NEWS_IMAGE_URL + newsItemModel.getPostImages().get(0).getPostImage())
                    .placeholder(R.drawable.default_placeholder)
                    .error(R.drawable.default_placeholder)
                    .into(holder.ivRecentPostNews);
        }

        holder.layoutDeleteRecentPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (deleteRecentPostInterface != null) {
                    deleteRecentPostInterface.deleteRecentPostMethod(newsItemModel.getNews_id());
                }
            }
        });
        holder.layoutRecentPostReadMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (deleteRecentPostInterface != null) {
                    deleteRecentPostInterface.readMoreRecentPostMethod(newsItemModel.getNews_id(),newsItemModel.getStatus());
                }
            }
        });
        holder.layoutEditRecentPost.setOnClickListener(new View.OnClickListener() {
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
        return newsItemModelArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView txtRecentPostNewsTitle, txtRecentPostNewsStatus, txtCommentsRecentPost;
        private ImageView ivRecentPostNews;
        private LinearLayout layoutDeleteRecentPost, layoutRecentPostReadMore, layoutEditRecentPost;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtRecentPostNewsTitle = itemView.findViewById(R.id.txtRecentPostNewsTitle);
            txtRecentPostNewsStatus = itemView.findViewById(R.id.txtRecentPostNewsStatus);
            ivRecentPostNews = itemView.findViewById(R.id.ivRecentPostNews);
            txtCommentsRecentPost = itemView.findViewById(R.id.txtCommentsRecentPost);
            layoutDeleteRecentPost = itemView.findViewById(R.id.layoutDeleteRecentPost);
            layoutRecentPostReadMore = itemView.findViewById(R.id.layoutRecentPostReadMore);
            layoutEditRecentPost = itemView.findViewById(R.id.layoutEditRecentPost);

            Typeface typeface = Typeface.createFromAsset(mContext.getAssets(), "ramabhadra_regular.ttf");
            txtRecentPostNewsTitle.setTypeface(typeface);
        }
    }

    public interface DeleteRecentPostInterface {
        void deleteRecentPostMethod(String newsId);

        void readMoreRecentPostMethod(String newsId,String status);

        void editRecentPostMethod(String newsId);
    }

}
