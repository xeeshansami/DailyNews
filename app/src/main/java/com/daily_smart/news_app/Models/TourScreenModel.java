package com.daily_smart.news_app.Models;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class TourScreenModel implements Parcelable {

    private String id;
    private String description;
    private String image;
    private String created_at;

    public TourScreenModel(){

    }

    protected TourScreenModel(Parcel in) {
        id = in.readString();
        description = in.readString();
        image = in.readString();
        created_at = in.readString();
    }

    public static final Creator<TourScreenModel> CREATOR = new Creator<TourScreenModel>() {
        @Override
        public TourScreenModel createFromParcel(Parcel in) {
            return new TourScreenModel(in);
        }

        @Override
        public TourScreenModel[] newArray(int size) {
            return new TourScreenModel[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public static TourScreenModel fromJson(JSONObject jsonObject) {
        TourScreenModel tourScreenModel = new TourScreenModel();
        // Deserialize json into object fields
        try {
            if (jsonObject.has("id")) {
                tourScreenModel.id = jsonObject.getString("id");
            }
            if (jsonObject.has("description")) {
                tourScreenModel.description = jsonObject.getString("description");
            }
            if (jsonObject.has("image")) {
                tourScreenModel.image = jsonObject.getString("image");
            }
            if (jsonObject.has("created_at")) {
                tourScreenModel.created_at = jsonObject.getString("created_at");
            }

        } catch (JSONException e) {
            //e.printStackTrace();
            return null;
        }
        return tourScreenModel;
    }

    public static ArrayList<TourScreenModel> fromJson(JSONArray jsonArray) {
        ArrayList<TourScreenModel> tourScreenModelArrayList = new ArrayList<TourScreenModel>(jsonArray.length());
        // Process each result in json array, decode and convert to business object
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject DistrictListJson = null;
            try {
                DistrictListJson = jsonArray.getJSONObject(i);
            } catch (Exception e) {
                //e.printStackTrace();
                continue;
            }

            TourScreenModel districtModelResponse = TourScreenModel.fromJson(DistrictListJson);
            if (districtModelResponse != null) {
                tourScreenModelArrayList.add(districtModelResponse);
            }
        }
        return tourScreenModelArrayList;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(description);
        parcel.writeString(image);
        parcel.writeString(created_at);
    }
}
