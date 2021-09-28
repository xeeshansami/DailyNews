package com.daily_smart.news_app.Models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

public class CategoriesModel implements Serializable {

    private String id;
    private String CategoryNameTe;
    private String CategoryNameEn;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCategoryNameTe() {
        return CategoryNameTe;
    }

    public void setCategoryNameTe(String name_te) {
        this.CategoryNameTe = name_te;
    }

    public String getCategoryNameEn() {
        return CategoryNameEn;
    }

    public void setCategoryNameEn(String name_en) {
        this.CategoryNameEn = name_en;
    }

    public static CategoriesModel fromJson(JSONObject jsonObject) {
        CategoriesModel categoriesModel = new CategoriesModel();
        // Deserialize json into object fields
        try {
            if (jsonObject.has("id")) {
                categoriesModel.id = jsonObject.getString("id");
            }
            if (jsonObject.has("CategoryNameTe")) {
                categoriesModel.CategoryNameTe = jsonObject.getString("CategoryNameTe");
            }
            if (jsonObject.has("CategoryNameEn")) {
                categoriesModel.CategoryNameEn = jsonObject.getString("CategoryNameEn");
            }

        } catch (JSONException e) {
            //e.printStackTrace();
            return null;
        }
        return categoriesModel;
    }

    public static ArrayList<CategoriesModel> fromJson(JSONArray jsonArray) {
        ArrayList<CategoriesModel> categoriesModelArrayList = new ArrayList<CategoriesModel>(jsonArray.length());
        // Process each result in json array, decode and convert to business object
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject categoriesListJson = null;
            try {
                categoriesListJson = jsonArray.getJSONObject(i);
            } catch (Exception e) {
                //e.printStackTrace();
                continue;
            }

            CategoriesModel categoryModelResponse = CategoriesModel.fromJson(categoriesListJson);
            if (categoryModelResponse != null) {
                categoriesModelArrayList.add(categoryModelResponse);
            }
        }
        return categoriesModelArrayList;
    }
}
