package com.daily_smart.news_app.Models;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class FavoritesModel implements Parcelable {

    private String news_id;
    private String favorites_id;
    private String user_id;
    private String PostTitleTe;
    private String PostDetailsTe;
    private String video;
    private String youtube_link;
    private String comments_count;
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public FavoritesModel() {

    }

    protected FavoritesModel(Parcel in) {
        news_id = in.readString();
        favorites_id = in.readString();
        user_id = in.readString();
        PostTitleTe = in.readString();
        PostDetailsTe = in.readString();
        video = in.readString();
        youtube_link = in.readString();
        comments_count = in.readString();
        id = in.readString();
    }

    public static final Creator<FavoritesModel> CREATOR = new Creator<FavoritesModel>() {
        @Override
        public FavoritesModel createFromParcel(Parcel in) {
            return new FavoritesModel(in);
        }

        @Override
        public FavoritesModel[] newArray(int size) {
            return new FavoritesModel[size];
        }
    };

    public String getNews_id() {
        return news_id;
    }

    public void setNews_id(String news_id) {
        this.news_id = news_id;
    }

    public String getFavorites_id() {
        return favorites_id;
    }

    public void setFavorites_id(String favorites_id) {
        this.favorites_id = favorites_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getPostTitleTe() {
        return PostTitleTe;
    }

    public void setPostTitleTe(String postTitleTe) {
        PostTitleTe = postTitleTe;
    }

    public String getPostDetailsTe() {
        return PostDetailsTe;
    }

    public void setPostDetailsTe(String postDetailsTe) {
        PostDetailsTe = postDetailsTe;
    }

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }

    public String getYoutube_link() {
        return youtube_link;
    }

    public void setYoutube_link(String youtube_link) {
        this.youtube_link = youtube_link;
    }

    public String getComments_count() {
        return comments_count;
    }

    public void setComments_count(String comments_count) {
        this.comments_count = comments_count;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(news_id);
        parcel.writeString(favorites_id);
        parcel.writeString(user_id);
        parcel.writeString(PostTitleTe);
        parcel.writeString(PostDetailsTe);
        parcel.writeString(video);
        parcel.writeString(youtube_link);
        parcel.writeString(comments_count);
        parcel.writeString(id);
    }

    public static FavoritesModel fromJson(JSONObject jsonObject) {
        FavoritesModel newsItemModel = new FavoritesModel();
        // Deserialize json into object fields
        try {
            if (jsonObject.has("news_id")) {
                newsItemModel.news_id = jsonObject.getString("news_id");
            }
            if (jsonObject.has("id")) {
                newsItemModel.id = jsonObject.getString("id");
            }

            if (jsonObject.has("PostTitleTe")) {
                newsItemModel.PostTitleTe = jsonObject.getString("PostTitleTe");
            }

            if (jsonObject.has("PostDetailsTe")) {
                newsItemModel.PostDetailsTe = jsonObject.getString("PostDetailsTe");
            }

            if (jsonObject.has("video")) {
                newsItemModel.video = jsonObject.getString("video");
            }
            if (jsonObject.has("youtube_link")) {
                newsItemModel.youtube_link = jsonObject.getString("youtube_link");
            }

            if (jsonObject.has("comments_count")) {
                newsItemModel.comments_count = jsonObject.getString("comments_count");
            }

            if (jsonObject.has("user_id")) {
                newsItemModel.user_id = jsonObject.getString("user_id");
            }

            if (jsonObject.has("favorites_id")) {
                newsItemModel.favorites_id = jsonObject.getString("favorites_id");
            }


        } catch (JSONException e) {
            //e.printStackTrace();
            return null;
        }
        return newsItemModel;
    }
    public static ArrayList<FavoritesModel> fromJson(JSONArray jsonArray) {
        ArrayList<FavoritesModel> newsItemModelArrayList = new ArrayList<FavoritesModel>(jsonArray.length());
        // Process each result in json array, decode and convert to business object
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject newsItemListJson = null;
            try {
                newsItemListJson = jsonArray.getJSONObject(i);
            } catch (Exception e) {
                //e.printStackTrace();
                continue;
            }

            FavoritesModel newsItemModelResponse = FavoritesModel.fromJson(newsItemListJson);
            if (newsItemModelResponse != null) {
                newsItemModelArrayList.add(newsItemModelResponse);
            }
        }
        return newsItemModelArrayList;
    }
}
