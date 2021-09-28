package com.daily_smart.news_app.Models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

public class DistrictModel implements Serializable {
     private String id;
     private String DistrictNameTe;
     private String DistrictNameEn;
    private String state_id;
    private String state_name_te;
    private String state_name_en;

    public void setDistrictNameEn(String districtNameEn) {
        DistrictNameEn = districtNameEn;
    }

    public String getState_name_te() {
        return state_name_te;
    }

    public void setState_name_te(String state_name_te) {
        this.state_name_te = state_name_te;
    }

    public String getState_name_en() {
        return state_name_en;
    }

    public void setState_name_en(String state_name_en) {
        this.state_name_en = state_name_en;
    }

    public String getId() {
        return id;
    }

    public void setId(String district_id) {
        this.id = district_id;
    }

    public String getDistrictNameTe() {
        return DistrictNameTe;
    }

    public void setDistrictNameTe(String district_name_te) {
        this.DistrictNameTe = district_name_te;
    }

    public String getDistrictNameEn() {
        return DistrictNameEn;
    }

    public void setDistrict_name_en(String district_name_en) {
        this.DistrictNameEn = district_name_en;
    }

    public String getState_id() {
        return state_id;
    }

    public void setState_id(String state_id) {
        this.state_id = state_id;
    }

    public static DistrictModel fromJson(JSONObject jsonObject) {
        DistrictModel districtModel = new DistrictModel();
        // Deserialize json into object fields
        try {
            if (jsonObject.has("id")) {
                districtModel.id = jsonObject.getString("id");
            }
            if (jsonObject.has("DistrictNameTe")) {
                districtModel.DistrictNameTe = jsonObject.getString("DistrictNameTe");
            }
            if (jsonObject.has("DistrictNameEn")) {
                districtModel.DistrictNameEn = jsonObject.getString("DistrictNameEn");
            }
            if (jsonObject.has("state_id")) {
                districtModel.state_id = jsonObject.getString("state_id");
            }
            if (jsonObject.has("state_name_en")) {
                districtModel.state_name_en = jsonObject.getString("state_name_en");
            }
            if (jsonObject.has("state_name_te")) {
                districtModel.state_name_te = jsonObject.getString("state_name_te");
            }

        } catch (JSONException e) {
            //e.printStackTrace();
            return null;
        }
        return districtModel;
    }

    public static ArrayList<DistrictModel> fromJson(JSONArray jsonArray) {
        ArrayList<DistrictModel> districtModelArrayList = new ArrayList<DistrictModel>(jsonArray.length());
        // Process each result in json array, decode and convert to business object
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject DistrictListJson = null;
            try {
                DistrictListJson = jsonArray.getJSONObject(i);
            } catch (Exception e) {
                //e.printStackTrace();
                continue;
            }

            DistrictModel districtModelResponse = DistrictModel.fromJson(DistrictListJson);
            if (districtModelResponse != null) {
                districtModelArrayList.add(districtModelResponse);
            }
        }
        return districtModelArrayList;
    }
}
