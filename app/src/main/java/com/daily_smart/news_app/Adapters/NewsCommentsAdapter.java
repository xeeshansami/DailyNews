package com.daily_smart.news_app.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.daily_smart.news_app.Models.NewsCommentsModel;
import com.daily_smart.news_app.R;
import com.daily_smart.news_app.Utilities.Config;
import com.daily_smart.news_app.Utilities.ShareData;
import com.daily_smart.news_app.Utilities.TimeAgo;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class NewsCommentsAdapter extends RecyclerView.Adapter<NewsCommentsAdapter.ViewHolder> {
    private Context mContext;
    private ArrayList<NewsCommentsModel> newsItemModelArrayList = new ArrayList<>();
    private SubCommentsAdapter subCommentsAdapter;
    public CommentsLikeInterface commentsLikeInterface;
    private ShareData shareData;

    public NewsCommentsAdapter(Context context, ArrayList<NewsCommentsModel> newsItemModelArrayList, CommentsLikeInterface commentsLikeInterface) {
        this.mContext = context;
        this.newsItemModelArrayList = newsItemModelArrayList;
        this.commentsLikeInterface = commentsLikeInterface;
        shareData = new ShareData(mContext);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.news_comments_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(listItem);
        return viewHolder;
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final NewsCommentsModel newsItemModel = newsItemModelArrayList.get(position);
        holder.txtCommentDescription.setText(newsItemModel.getComment());
        holder.txtCommentUserName.setText(newsItemModel.getName());
        TimeAgo timeAgo = new TimeAgo();
//        String timeAgoString = timeAgo.covertTimeToText(newsItemModel.getCreated_at());
//        holder.txtCommentTime.setText(timeAgoString);

        Picasso.get()
                .load(Config.PROFILE_IMAGE_URL + newsItemModel.getProfile_pic())
                .placeholder(R.drawable.defult_profile)
                .error(R.drawable.defult_profile)
                .into(holder.ivCommentProfileImage);

        if (newsItemModel.getIs_like().equalsIgnoreCase("0")) {
            holder.ivCommentLike.setBackgroundResource(R.drawable.thumb_down);
        } else {
            holder.ivCommentLike.setBackgroundResource(R.drawable.thumb_up);
        }

        holder.ivCommentLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (newsItemModel.getIs_like().equalsIgnoreCase("0")) {
                    if (commentsLikeInterface != null) {
                        commentsLikeInterface.commentsLiked(shareData.getCustomerId(), newsItemModel.getNews_id(), newsItemModel.getComment_id(),"Comment");
                    }
                } else {
                    if (commentsLikeInterface != null) {
                        commentsLikeInterface.commentsUnLiked(shareData.getCustomerId(), newsItemModel.getNews_id(), newsItemModel.getLike_id(),"Comment");
                    }
                }
            }
        });

        holder.txtCommentReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (commentsLikeInterface != null) {
                    commentsLikeInterface.replyComments(shareData.getCustomerId(), newsItemModel.getNews_id(), newsItemModel.getComment_id());
                }
            }
        });

        subCommentsAdapter = new SubCommentsAdapter(mContext, newsItemModel.getNews_id(), newsItemModel.getSubCommentsModelArrayList(), new SubCommentsAdapter.SubCommentLikeInterface() {
            @Override
            public void subCommentsLiked(String userId, String newsId, String commentId,String SubComment) {
                if (commentsLikeInterface != null) {
                    commentsLikeInterface.commentsLiked(userId, newsId, commentId,SubComment);
                }
            }

            @Override
            public void subCommentsUnLiked(String userId, String newsId, String likeId,String SubComment) {
                if (commentsLikeInterface != null) {
                    commentsLikeInterface.commentsUnLiked(userId, newsId, likeId,SubComment);
                }
            }
        });
        holder.rvSubComments.setLayoutManager(new LinearLayoutManager(mContext));
        holder.rvSubComments.setHasFixedSize(true);
        holder.rvSubComments.setAdapter(subCommentsAdapter);
    }

    @Override
    public int getItemCount() {
        return newsItemModelArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView txtCommentUserName, txtCommentDescription, txtCommentTime, txtCommentReply;
        private ImageView ivCommentProfileImage, ivCommentLike;
        private RecyclerView rvSubComments;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtCommentUserName = itemView.findViewById(R.id.txtCommentUserName);
            txtCommentDescription = itemView.findViewById(R.id.txtCommentDescription);
            ivCommentProfileImage = itemView.findViewById(R.id.ivCommentProfileImage);
            txtCommentTime = itemView.findViewById(R.id.txtCommentTime);
            ivCommentLike = itemView.findViewById(R.id.ivCommentLike);
            txtCommentReply = itemView.findViewById(R.id.txtCommentReply);
            rvSubComments = itemView.findViewById(R.id.rvSubComments);
        }
    }

    public interface CommentsLikeInterface {
        void commentsLiked(String userId, String newsId, String commentId,String Comment);

        void commentsUnLiked(String userId, String newsId, String likeId,String Comment);

        void replyComments(String userId, String newsId, String commentId);
    }
}
