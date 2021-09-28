package com.daily_smart.news_app.Models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

public class NewsCommentsModel implements Serializable {
    private String id;
    private String user_id;
    private String news_id;
    private String comment;
    private String created_at;
    private String profile_pic;
    private String name;
    private String follower_id;
    private String comment_id;
    private String like_id;
    private String is_like;
    private ArrayList<SubCommentsModel> subCommentsModelArrayList = new ArrayList<>();

    public String getComment_id() {
        return comment_id;
    }

    public void setComment_id(String comment_id) {
        this.comment_id = comment_id;
    }

    public String getLike_id() {
        return like_id;
    }

    public void setLike_id(String like_id) {
        this.like_id = like_id;
    }

    public String getIs_like() {
        return is_like;
    }

    public void setIs_like(String is_like) {
        this.is_like = is_like;
    }

    public String getFollower_id() {
        return follower_id;
    }

    public void setFollower_id(String follower_id) {
        this.follower_id = follower_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfile_pic() {
        return profile_pic;
    }

    public void setProfile_pic(String profile_pic) {
        this.profile_pic = profile_pic;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getNews_id() {
        return news_id;
    }

    public void setNews_id(String news_id) {
        this.news_id = news_id;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public static NewsCommentsModel fromJson(JSONObject jsonObject) {
        NewsCommentsModel newsCommentsModel = new NewsCommentsModel();
        // Deserialize json into object fields
        try {
            if (jsonObject.has("id")) {
                newsCommentsModel.id = jsonObject.getString("id");
            }
            if (jsonObject.has("user_id")) {
                newsCommentsModel.user_id = jsonObject.getString("user_id");
            }
            if (jsonObject.has("news_id")) {
                newsCommentsModel.news_id = jsonObject.getString("news_id");
            }
            if (jsonObject.has("comment")) {
                newsCommentsModel.comment = jsonObject.getString("comment");
            }
            if (jsonObject.has("created_at")) {
                newsCommentsModel.created_at = jsonObject.getString("created_at");
            }
            if (jsonObject.has("name")) {
                newsCommentsModel.name = jsonObject.getString("name");
            }
            if (jsonObject.has("profile_pic")) {
                newsCommentsModel.profile_pic = jsonObject.getString("profile_pic");
            }
            if (jsonObject.has("follower_id")) {
                newsCommentsModel.follower_id = jsonObject.getString("follower_id");
            }
            if (jsonObject.has("comment_id")) {
                newsCommentsModel.comment_id = jsonObject.getString("comment_id");
            }
            if (jsonObject.has("like_id")) {
                newsCommentsModel.like_id = jsonObject.getString("like_id");
            }
            if (jsonObject.has("is_like")) {
                newsCommentsModel.is_like = jsonObject.getString("is_like");
            }
            if (jsonObject.has("subcomment")) {
                newsCommentsModel.subCommentsModelArrayList = SubCommentsModel.fromJson(jsonObject.getJSONArray("subcomment"));
            }

        } catch (JSONException e) {
            //e.printStackTrace();
            return null;
        }
        return newsCommentsModel;
    }

    public static ArrayList<NewsCommentsModel> fromJson(JSONArray jsonArray) {
        ArrayList<NewsCommentsModel> newsCommentsModelArrayList = new ArrayList<NewsCommentsModel>(jsonArray.length());
        // Process each result in json array, decode and convert to business object
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject newsItemListJson = null;
            try {
                newsItemListJson = jsonArray.getJSONObject(i);
            } catch (Exception e) {
                //e.printStackTrace();
                continue;
            }

            NewsCommentsModel newsItemModelResponse = NewsCommentsModel.fromJson(newsItemListJson);
            if (newsItemModelResponse != null) {
                newsCommentsModelArrayList.add(newsItemModelResponse);
            }
        }
        return newsCommentsModelArrayList;
    }

    public ArrayList<SubCommentsModel> getSubCommentsModelArrayList() {
        return subCommentsModelArrayList;
    }

    public void setSubCommentsModelArrayList(ArrayList<SubCommentsModel> subCommentsModelArrayList) {
        this.subCommentsModelArrayList = subCommentsModelArrayList;
    }
}
