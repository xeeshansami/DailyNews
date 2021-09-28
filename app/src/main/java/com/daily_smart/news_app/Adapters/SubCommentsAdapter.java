package com.daily_smart.news_app.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.daily_smart.news_app.Models.SubCommentsModel;
import com.daily_smart.news_app.R;
import com.daily_smart.news_app.Utilities.Config;
import com.daily_smart.news_app.Utilities.ShareData;
import com.daily_smart.news_app.Utilities.TimeAgo;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class SubCommentsAdapter extends RecyclerView.Adapter<SubCommentsAdapter.ViewHolder> {
    private Context mContext;
    private ArrayList<SubCommentsModel> newsItemModelArrayList = new ArrayList<>();
    private SubCommentLikeInterface subCommentLikeInterface;
    private String newsId = Config.EMPTY_STRING;
    private ShareData shareData;

    public SubCommentsAdapter(Context context, String newsId, ArrayList<SubCommentsModel> newsItemModelArrayList, SubCommentLikeInterface subCommentLikeInterface) {
        this.mContext = context;
        this.newsItemModelArrayList = newsItemModelArrayList;
        this.subCommentLikeInterface = subCommentLikeInterface;
        this.newsId = newsId;
        shareData = new ShareData(mContext);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.sub_comment_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final SubCommentsModel newsItemModel = newsItemModelArrayList.get(position);
        holder.txtCommentDescription.setText(newsItemModel.getSubcomment());
        holder.txtCommentUserName.setText(newsItemModel.getName());
        TimeAgo timeAgo = new TimeAgo();
//        String timeAgoString = timeAgo.covertTimeToText(newsItemModel.getCreated_at());
//        holder.txtCommentTime.setText(timeAgoString);

        Picasso.get()
                .load(Config.PROFILE_IMAGE_URL + newsItemModel.getProfile_pic())
                .placeholder(R.drawable.defult_profile)
                .error(R.drawable.defult_profile)
                .into(holder.ivCommentProfileImage);

        if (newsItemModel.getIs_sub_like().equalsIgnoreCase("0")) {
            holder.ivSubCommentLike.setBackgroundResource(R.drawable.thumb_down);
        } else {
            holder.ivSubCommentLike.setBackgroundResource(R.drawable.thumb_up);
        }

        holder.ivSubCommentLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (newsItemModel.getIs_sub_like().equalsIgnoreCase("0")) {
                    if (subCommentLikeInterface != null) {
                        subCommentLikeInterface.subCommentsLiked(shareData.getCustomerId(), newsId, newsItemModel.getSub_comment_id(), "SubComment");
                    }
                } else {
                    if (subCommentLikeInterface != null) {
                        subCommentLikeInterface.subCommentsUnLiked(shareData.getCustomerId(), newsId, newsItemModel.getSub_like_id(), "SubComment");
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return newsItemModelArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView txtCommentUserName, txtCommentDescription, txtCommentTime;
        private ImageView ivCommentProfileImage, ivSubCommentLike;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtCommentUserName = itemView.findViewById(R.id.txtCommentUserName);
            txtCommentDescription = itemView.findViewById(R.id.txtCommentDescription);
            ivCommentProfileImage = itemView.findViewById(R.id.ivCommentProfileImage);
            txtCommentTime = itemView.findViewById(R.id.txtCommentTime);
            ivSubCommentLike = itemView.findViewById(R.id.ivSubCommentLike);
        }
    }

    public interface SubCommentLikeInterface {
        void subCommentsLiked(String userId, String newsId, String commentId, String SubComment);

        void subCommentsUnLiked(String userId, String newsId, String likeId, String SubComment);
    }
}
