package com.paxees_daily_smart.paxees_news_app.Models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

public class SubCommentsModel implements Serializable {
    private String sub_comment_id;
    private String subcomment;
    private String created_at;
    private String name;
    private String user_id;
    private String sub_like_id;
    private String is_sub_like;
    private String profile_pic;

    public String getSub_comment_id() {
        return sub_comment_id;
    }

    public void setSub_comment_id(String sub_comment_id) {
        this.sub_comment_id = sub_comment_id;
    }

    public String getSubcomment() {
        return subcomment;
    }

    public void setSubcomment(String subcomment) {
        this.subcomment = subcomment;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getSub_like_id() {
        return sub_like_id;
    }

    public void setSub_like_id(String sub_like_id) {
        this.sub_like_id = sub_like_id;
    }

    public String getIs_sub_like() {
        return is_sub_like;
    }

    public void setIs_sub_like(String is_sub_like) {
        this.is_sub_like = is_sub_like;
    }

    public static SubCommentsModel fromJson(JSONObject jsonObject) {
        SubCommentsModel newsCommentsModel = new SubCommentsModel();
        // Deserialize json into object fields
        try {
            if (jsonObject.has("sub_comment_id")) {
                newsCommentsModel.sub_comment_id = jsonObject.getString("sub_comment_id");
            }
            if (jsonObject.has("subcomment")) {
                newsCommentsModel.subcomment = jsonObject.getString("subcomment");
            }
            if (jsonObject.has("created_at")) {
                newsCommentsModel.created_at = jsonObject.getString("created_at");
            }
            if (jsonObject.has("name")) {
                newsCommentsModel.name = jsonObject.getString("name");
            }
            if (jsonObject.has("user_id")) {
                newsCommentsModel.user_id = jsonObject.getString("user_id");
            }
            if (jsonObject.has("sub_like_id")) {
                newsCommentsModel.sub_like_id = jsonObject.getString("sub_like_id");
            }
            if (jsonObject.has("is_sub_like")) {
                newsCommentsModel.is_sub_like = jsonObject.getString("is_sub_like");
            }
            if (jsonObject.has("profile_pic")) {
                newsCommentsModel.profile_pic = jsonObject.getString("profile_pic");
            }

        } catch (JSONException e) {
            //e.printStackTrace();
            return null;
        }
        return newsCommentsModel;
    }

    public static ArrayList<SubCommentsModel> fromJson(JSONArray jsonArray) {
        ArrayList<SubCommentsModel> newsCommentsModelArrayList = new ArrayList<SubCommentsModel>(jsonArray.length());
        // Process each result in json array, decode and convert to business object
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject newsItemListJson = null;
            try {
                newsItemListJson = jsonArray.getJSONObject(i);
            } catch (Exception e) {
                //e.printStackTrace();
                continue;
            }

            SubCommentsModel newsItemModelResponse = SubCommentsModel.fromJson(newsItemListJson);
            if (newsItemModelResponse != null) {
                newsCommentsModelArrayList.add(newsItemModelResponse);
            }
        }
        return newsCommentsModelArrayList;
    }

    public String getProfile_pic() {
        return profile_pic;
    }

    public void setProfile_pic(String profile_pic) {
        this.profile_pic = profile_pic;
    }
}
