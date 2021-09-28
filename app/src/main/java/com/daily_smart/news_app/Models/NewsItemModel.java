package com.daily_smart.news_app.Models;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class NewsItemModel implements Parcelable {

    private String news_id;
    private String PostTitleTe;
    private String PostTitleEn;
    private String PostDetailsTe;
    private String PostDetailsEn;
    private String PostImage;
    private String video;
    private String youtube_link;
    private String views_count;
    private String comments_count;
    private String district_id;
    private String category_id;
    private String user_id;
    private String status;
    private String name;
    private String favorites_id;
    private String id;
    private String PostingDate;
    private String ScheduleDate;

    public String getScheduleDate() {
        return ScheduleDate;
    }

    public void setScheduleDate(String scheduleDate) {
        ScheduleDate = scheduleDate;
    }

    private String profile_pic;
    private String DistrictNameTe;
    private String DistrictNameEn;
    private String fullscreen;
    private String is_fav;
    private String is_followers;
    private String is_fav_id;
    private String is_followers_id;

    public String getIs_fav_id() {
        return is_fav_id;
    }

    public void setIs_fav_id(String is_fav_id) {
        this.is_fav_id = is_fav_id;
    }

    public String getIs_followers_id() {
        return is_followers_id;
    }

    public void setIs_followers_id(String is_followers_id) {
        this.is_followers_id = is_followers_id;
    }

    public String getIs_fav() {
        return is_fav;
    }

    public void setIs_fav(String is_fav) {
        this.is_fav = is_fav;
    }

    public String getIs_followers() {
        return is_followers;
    }

    public void setIs_followers(String is_followers) {
        this.is_followers = is_followers;
    }

    public String getPostVideo() {
        return PostVideo;
    }

    public void setPostVideo(String postVideo) {
        PostVideo = postVideo;
    }

    private String PostVideo;

    public String getViewType() {
        return viewType;
    }

    public void setViewType(String viewType) {
        this.viewType = viewType;
    }

    private String viewType;
    private List<PostImagesModel> postImages = new ArrayList<PostImagesModel>();

    public List<PostImagesModel> getPostImages() {
        return postImages;
    }

    public void setPostImages(List<PostImagesModel> postImages) {
        this.postImages = postImages;
    }

    public String getFullscreen() {
        return fullscreen;
    }

    public void setFullscreen(String fullscreen) {
        this.fullscreen = fullscreen;
    }

    public String getDistrictNameEn() {
        return DistrictNameEn;
    }

    public void setDistrictNameEn(String districtNameEn) {
        DistrictNameEn = districtNameEn;
    }

    public String getDistrictNameTe() {
        return DistrictNameTe;
    }

    public void setDistrictNameTe(String districtNameTe) {
        DistrictNameTe = districtNameTe;
    }

    public String getProfile_pic() {
        return profile_pic;
    }

    public void setProfile_pic(String profile_pic) {
        this.profile_pic = profile_pic;
    }

    public String getPostingDate() {
        return PostingDate;
    }

    public void setPostingDate(String created_at) {
        this.PostingDate = created_at;
    }

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

    public static Creator<NewsItemModel> getCREATOR() {
        return CREATOR;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public NewsItemModel() {

    }

    protected NewsItemModel(Parcel in) {
        news_id = in.readString();
        id = in.readString();
        PostTitleTe = in.readString();
        PostTitleEn = in.readString();
        PostDetailsTe = in.readString();
        PostDetailsEn = in.readString();
        PostImage = in.readString();
        video = in.readString();
        youtube_link = in.readString();
        views_count = in.readString();
        comments_count = in.readString();
        district_id = in.readString();
        category_id = in.readString();
        user_id = in.readString();
        status = in.readString();
        name = in.readString();
        favorites_id = in.readString();
        PostingDate = in.readString();
        profile_pic = in.readString();
        DistrictNameTe = in.readString();
        DistrictNameEn = in.readString();
        fullscreen = in.readString();
        PostVideo = in.readString();
        is_fav = in.readString();
        is_followers = in.readString();
        is_fav_id = in.readString();
        is_followers_id = in.readString();
        ScheduleDate = in.readString();
        in.readList(postImages, PostImagesModel.class.getClassLoader());
    }

    public static final Creator<NewsItemModel> CREATOR = new Creator<NewsItemModel>() {
        @Override
        public NewsItemModel createFromParcel(Parcel in) {
            return new NewsItemModel(in);
        }

        @Override
        public NewsItemModel[] newArray(int size) {
            return new NewsItemModel[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String _id) {
        this.id = _id;
    }

    public String getPostTitleTe() {
        return PostTitleTe;
    }

    public void setPostTitleTe(String title_te) {
        this.PostTitleTe = title_te;
    }

    public String getPostTitleEn() {
        return PostTitleEn;
    }

    public void setPostTitleEn(String title_en) {
        this.PostTitleEn = title_en;
    }

    public String getPostDetailsTe() {
        return PostDetailsTe;
    }

    public void setPostDetailsTe(String description_te) {
        this.PostDetailsTe = description_te;
    }

    public String getPostDetailsEn() {
        return PostDetailsEn;
    }

    public void setPostDetailsEn(String description_en) {
        this.PostDetailsEn = description_en;
    }

    public String getPostImage() {
        return PostImage;
    }

    public void setPostImage(String image) {
        this.PostImage = image;
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

    public String getViews_count() {
        return views_count;
    }

    public void setViews_count(String views_count) {
        this.views_count = views_count;
    }

    public String getComments_count() {
        return comments_count;
    }

    public void setComments_count(String comments_count) {
        this.comments_count = comments_count;
    }

    public String getDistrict_id() {
        return district_id;
    }

    public void setDistrict_id(String district_id) {
        this.district_id = district_id;
    }

    public String getCategory_id() {
        return category_id;
    }

    public void setCategory_id(String category_id) {
        this.category_id = category_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public static NewsItemModel fromJson(JSONObject jsonObject) {
        NewsItemModel newsItemModel = new NewsItemModel();
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
            if (jsonObject.has("PostTitleEn")) {
                newsItemModel.PostTitleEn = jsonObject.getString("PostTitleEn");
            }
            if (jsonObject.has("PostDetailsTe")) {
                newsItemModel.PostDetailsTe = jsonObject.getString("PostDetailsTe");
            }
            if (jsonObject.has("PostDetailsEn")) {
                newsItemModel.PostDetailsEn = jsonObject.getString("PostDetailsEn");
            }
            if (jsonObject.has("PostImage")) {
                newsItemModel.PostImage = jsonObject.getString("PostImage");
            }
            if (jsonObject.has("video")) {
                newsItemModel.video = jsonObject.getString("video");
            }
            if (jsonObject.has("youtube_link")) {
                newsItemModel.youtube_link = jsonObject.getString("youtube_link");
            }
            if (jsonObject.has("views_count")) {
                newsItemModel.views_count = jsonObject.getString("views_count");
            }
            if (jsonObject.has("comments_count")) {
                newsItemModel.comments_count = jsonObject.getString("comments_count");
            }
            if (jsonObject.has("district_id")) {
                newsItemModel.district_id = jsonObject.getString("district_id");
            }
            if (jsonObject.has("category_id")) {
                newsItemModel.category_id = jsonObject.getString("category_id");
            }
            if (jsonObject.has("user_id")) {
                newsItemModel.user_id = jsonObject.getString("user_id");
            }
            if (jsonObject.has("status")) {
                newsItemModel.status = jsonObject.getString("status");
            }
            if (jsonObject.has("name")) {
                newsItemModel.name = jsonObject.getString("name");
            }
            if (jsonObject.has("favorites_id")) {
                newsItemModel.favorites_id = jsonObject.getString("favorites_id");
            }
            if (jsonObject.has("PostingDate")) {
                newsItemModel.PostingDate = jsonObject.getString("PostingDate");
            }
            if (jsonObject.has("profile_pic")) {
                newsItemModel.profile_pic = jsonObject.getString("profile_pic");
            }
            if (jsonObject.has("DistrictNameTe")) {
                newsItemModel.DistrictNameTe = jsonObject.getString("DistrictNameTe");
            }
            if (jsonObject.has("DistrictNameEn")) {
                newsItemModel.DistrictNameEn = jsonObject.getString("DistrictNameEn");
            }
            if (jsonObject.has("fullscreen")) {
                newsItemModel.fullscreen = jsonObject.getString("fullscreen");
            }
            if (jsonObject.has("PostVideo")) {
                newsItemModel.PostVideo = jsonObject.getString("PostVideo");
            }
            if (jsonObject.has("is_fav")) {
                newsItemModel.is_fav = jsonObject.getString("is_fav");
            }
            if (jsonObject.has("is_followers")) {
                newsItemModel.is_followers = jsonObject.getString("is_followers");
            }
            if (jsonObject.has("is_fav_id")) {
                newsItemModel.is_fav_id = jsonObject.getString("is_fav_id");
            }
            if (jsonObject.has("is_followers_id")) {
                newsItemModel.is_followers_id = jsonObject.getString("is_followers_id");
            }
            if (jsonObject.has("ScheduleDate")) {
                newsItemModel.ScheduleDate = jsonObject.getString("ScheduleDate");
            }
            if (jsonObject.has("PostImage")) {
                newsItemModel.postImages = PostImagesModel.fromJson(jsonObject.getJSONArray("PostImage"));
            }

        } catch (JSONException e) {
            //e.printStackTrace();
            return null;
        }
        return newsItemModel;
    }

    public static ArrayList<NewsItemModel> fromJson(JSONArray jsonArray) {
        ArrayList<NewsItemModel> newsItemModelArrayList = new ArrayList<NewsItemModel>(jsonArray.length());
        // Process each result in json array, decode and convert to business object
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject newsItemListJson = null;
            try {
                newsItemListJson = jsonArray.getJSONObject(i);
            } catch (Exception e) {
                //e.printStackTrace();
                continue;
            }

            NewsItemModel newsItemModelResponse = NewsItemModel.fromJson(newsItemListJson);
            if (newsItemModelResponse != null) {
                newsItemModelArrayList.add(newsItemModelResponse);
            }
        }
        return newsItemModelArrayList;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(news_id);
        parcel.writeString(id);
        parcel.writeString(PostTitleTe);
        parcel.writeString(PostTitleEn);
        parcel.writeString(PostDetailsTe);
        parcel.writeString(PostDetailsEn);
        parcel.writeString(PostImage);
        parcel.writeString(video);
        parcel.writeString(youtube_link);
        parcel.writeString(views_count);
        parcel.writeString(comments_count);
        parcel.writeString(district_id);
        parcel.writeString(category_id);
        parcel.writeString(user_id);
        parcel.writeString(status);
        parcel.writeString(PostingDate);
        parcel.writeString(name);
        parcel.writeString(favorites_id);
        parcel.writeString(DistrictNameTe);
        parcel.writeString(DistrictNameEn);
        parcel.writeString(fullscreen);
        parcel.writeString(PostVideo);
        parcel.writeString(is_fav);
        parcel.writeString(is_followers);
        parcel.writeString(is_fav_id);
        parcel.writeString(is_followers_id);
        parcel.writeString(ScheduleDate);
        parcel.writeTypedList(postImages);
    }
}
