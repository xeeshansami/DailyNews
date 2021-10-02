package com.paxees_daily_smart.paxees_news_app.Adapters;

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

public class FavouritesListAdapter extends RecyclerView.Adapter<FavouritesListAdapter.ViewHolder> {
    private Context mContext;
    private ArrayList<NewsItemModel> recentNewsList = new ArrayList<>();
    private DeleteFavouriteInterface deleteFavouriteInterface;

    public FavouritesListAdapter(Context context, ArrayList<NewsItemModel> recentNewsList, DeleteFavouriteInterface deleteFavouriteInterface) {
        this.mContext = context;
        this.recentNewsList = recentNewsList;
        this.deleteFavouriteInterface = deleteFavouriteInterface;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View districtListItem = layoutInflater.inflate(R.layout.favourites_list_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(districtListItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final NewsItemModel newsItemModel = recentNewsList.get(position);
        holder.txtRecentPostTitle.setText(newsItemModel.getPostTitleTe());
        holder.txtRecentPostDesc.setText(newsItemModel.getPostDetailsTe());
        if (!newsItemModel.getComments_count().isEmpty()) {
            holder.txtFavouritesCommentsCount.setText(newsItemModel.getComments_count());
        } else {
            holder.txtFavouritesCommentsCount.setText("0");
        }
        if (!newsItemModel.getPostImages().get(0).getPostImage().isEmpty()) {
            Picasso.get()
                    .load(Config.NEWS_IMAGE_URL + newsItemModel.getPostImages().get(0).getPostImage())
                    .placeholder(R.drawable.default_placeholder)
                    .error(R.drawable.default_placeholder)
                    .into(holder.ivRecentPost);
        }

        holder.layoutFavourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (deleteFavouriteInterface != null) {
                    deleteFavouriteInterface.deleteFavouriteMethod(newsItemModel.getFavorites_id());
                }
            }
        });
        holder.layoutFavoriteReadMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (deleteFavouriteInterface != null) {
                    deleteFavouriteInterface.readMoreFavouriteMethod(newsItemModel.getNews_id());
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
        private TextView txtRecentPostTitle, txtRecentPostDesc, txtFavouritesCommentsCount;
        private LinearLayout layoutFavourite, layoutShareRecentPost, layoutFavoriteReadMore;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivRecentPost = itemView.findViewById(R.id.ivRecentPost);
            txtRecentPostTitle = itemView.findViewById(R.id.txtRecentPostTitle);
            txtRecentPostDesc = itemView.findViewById(R.id.txtRecentPostDesc);
            txtFavouritesCommentsCount = itemView.findViewById(R.id.txtFavouritesCommentsCount);
            layoutFavourite = itemView.findViewById(R.id.layoutFavourite);
            layoutShareRecentPost = itemView.findViewById(R.id.layoutShareRecentPost);
            layoutFavoriteReadMore = itemView.findViewById(R.id.layoutFavoriteReadMore);

            Typeface typeface = Typeface.createFromAsset(mContext.getAssets(), "ramabhadra_regular.ttf");
            txtRecentPostTitle.setTypeface(typeface);

            typeface = Typeface.createFromAsset(mContext.getAssets(), "mallanna_regular.ttf");
            txtRecentPostDesc.setTypeface(typeface);

        }
    }

    public interface DeleteFavouriteInterface {
        void deleteFavouriteMethod(String favouriteId);

        void readMoreFavouriteMethod(String newsId);
    }
}
