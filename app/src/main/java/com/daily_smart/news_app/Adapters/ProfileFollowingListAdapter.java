package com.daily_smart.news_app.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.daily_smart.news_app.CustomViews.CircleImageView;
import com.daily_smart.news_app.Models.NewsCommentsModel;
import com.daily_smart.news_app.R;
import com.daily_smart.news_app.Utilities.Config;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ProfileFollowingListAdapter extends RecyclerView.Adapter<ProfileFollowingListAdapter.ViewHolder> {
    private Context mContext;
    private ArrayList<NewsCommentsModel> newsItemModelArrayList = new ArrayList<>();
    private ProfileFollowingListUnFollowInterface profileFollowingListUnFollowInterface;
    private String follower = Config.EMPTY_STRING;

    public ProfileFollowingListAdapter(Context context, String follower, ArrayList<NewsCommentsModel> newsItemModelArrayList, ProfileFollowingListUnFollowInterface profileFollowingListUnFollowInterface) {
        this.mContext = context;
        this.follower = follower;
        this.newsItemModelArrayList = newsItemModelArrayList;
        this.profileFollowingListUnFollowInterface = profileFollowingListUnFollowInterface;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.profile_following_list_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final NewsCommentsModel newsItemModel = newsItemModelArrayList.get(position);
        holder.txtFollowingName.setText(newsItemModel.getName());
        Picasso.get()
                .load(Config.PROFILE_IMAGE_URL + newsItemModel.getProfile_pic())
                .placeholder(R.drawable.defult_profile)
                .error(R.drawable.defult_profile)
                .into(holder.ivFollowingProfileImage);

        if (follower.equalsIgnoreCase("Following")) {
            holder.txtUnFollow.setVisibility(View.VISIBLE);
        } else {
            holder.txtUnFollow.setVisibility(View.GONE);
        }

        holder.txtUnFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (profileFollowingListUnFollowInterface != null) {
                    profileFollowingListUnFollowInterface.profileFollowingListUnFollowMethod(newsItemModel.getFollower_id());
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return newsItemModelArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView txtFollowingName, txtUnFollow;
        private CircleImageView ivFollowingProfileImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtFollowingName = itemView.findViewById(R.id.txtFollowingName);
            txtUnFollow = itemView.findViewById(R.id.txtUnFollow);
            ivFollowingProfileImage = itemView.findViewById(R.id.ivFollowingProfileImage);
        }
    }

    public interface ProfileFollowingListUnFollowInterface {
        void profileFollowingListUnFollowMethod(String followingId);
    }

}
