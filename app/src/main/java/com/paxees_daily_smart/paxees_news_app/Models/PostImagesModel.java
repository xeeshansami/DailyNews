package com.paxees_daily_smart.paxees_news_app.Models;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

public class PostImagesModel implements Parcelable {

    private String postImage;
    public PostImagesModel() {

    }

    protected PostImagesModel(Parcel in) {
        postImage = in.readString();
    }

    public static final Creator<PostImagesModel> CREATOR = new Creator<PostImagesModel>() {
        @Override
        public PostImagesModel createFromParcel(Parcel in) {
            return new PostImagesModel(in);
        }

        @Override
        public PostImagesModel[] newArray(int size) {
            return new PostImagesModel[size];
        }
    };

    public String getPostImage() {
        return postImage;
    }

    public void setPostImage(String postImage) {
        this.postImage = postImage;
    }

    public static ArrayList<PostImagesModel> fromJson(JSONArray jsonArray) {
        ArrayList<PostImagesModel> postImagesModelArrayList = new ArrayList<PostImagesModel>(jsonArray.length());
        // Process each result in json array, decode and convert to business object
        for (int i = 0; i < jsonArray.length(); i++) {
            PostImagesModel postImagesModel = new PostImagesModel();
            try {
                postImagesModel.setPostImage(String.valueOf(jsonArray.get(i)));
                postImagesModelArrayList.add(postImagesModel);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
            } catch (Exception e) {
                //e.printStackTrace();
                continue;
            }

        }
        return postImagesModelArrayList;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(postImage);
    }
}
