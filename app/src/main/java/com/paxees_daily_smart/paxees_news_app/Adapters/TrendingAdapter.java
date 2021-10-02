package com.paxees_daily_smart.paxees_news_app.Adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.paxees_daily_smart.paxees_news_app.Models.NewsItemModel;
import com.paxees_daily_smart.paxees_news_app.R;
import com.paxees_daily_smart.paxees_news_app.Utilities.Config;
import com.paxees_daily_smart.paxees_news_app.Utilities.TimeAgo;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class TrendingAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private TrendingNewsInterface trendingNewsInterface;
    private List<NewsItemModel> newsItemModelArrayList = new ArrayList<>();
    private static final int TRENDING_VIEW = 0;
    private static final int BANNER_VIEW = 1;
    //    private AdView adView;
    private String mGoogleBannerId = Config.EMPTY_STRING;

    public TrendingAdapter(Context context, List<NewsItemModel> newsItemModelArrayList, String googleBannerId, TrendingNewsInterface trendingNewsInterface) {
        this.mContext = context;
        this.trendingNewsInterface = trendingNewsInterface;
        this.newsItemModelArrayList = newsItemModelArrayList;
        this.mGoogleBannerId = googleBannerId;
//        adView = new AdView(mContext, "1209804396141394_1209806279474539", AdSize.BANNER_HEIGHT_50);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case TRENDING_VIEW:
                View trendingView = LayoutInflater.from(parent.getContext()).inflate(R.layout.trending_list_item, parent, false);
                return new TrendingViewHolder(trendingView);
            default:
                View trendingView1 = LayoutInflater.from(parent.getContext()).inflate(R.layout.facebook_banner_view, parent, false);
                return new BannerAdsViewHolder(trendingView1);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        switch (viewType) {
            case TRENDING_VIEW:
                TrendingViewHolder trendingViewHolder = (TrendingViewHolder) holder;
                final NewsItemModel newsItemModel = newsItemModelArrayList.get(position);
                trendingViewHolder.txtTrendingNewsTitle.setText(newsItemModel.getPostTitleTe());
                TimeAgo timeAgo = new TimeAgo();
                String timeAgoString = timeAgo.covertTimeToText(newsItemModel.getPostingDate());
                trendingViewHolder.txtTrendingNewsTime.setText(timeAgoString);
                Typeface typeface = Typeface.createFromAsset(mContext.getAssets(), "ramabhadra_regular.ttf");
                trendingViewHolder.txtTrendingNewsTitle.setTypeface(typeface);
                try {
                    if (!newsItemModel.getPostImages().get(0).getPostImage().isEmpty()) {
                        Picasso.get()
                                .load(Config.NEWS_IMAGE_URL + newsItemModel.getPostImages().get(0).getPostImage())
                                .placeholder(R.drawable.default_placeholder)
                                .error(R.drawable.default_placeholder)
                                .into(trendingViewHolder.ivTrendingNews);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                trendingViewHolder.cvTrendingNews.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (trendingNewsInterface != null) {
                            trendingNewsInterface.trendingNewsMethod(newsItemModel.getNews_id());
                        }
                    }
                });
                break;
            case BANNER_VIEW:
               /* Log.e("BANNER_VIEW", "BANNER_VIEW");
                BannerAdsViewHolder bannerAdsViewHolder = (BannerAdsViewHolder) holder;
                ViewGroup parent = (ViewGroup) bannerAdsViewHolder.layoutAds.getParent();
                if (parent != null) {
                    parent.removeView(bannerAdsViewHolder.layoutAds);
                }
//                bannerAdsViewHolder.layoutBannerAds.addView(adView);
                adView.loadAd();*/
//                BannerAdsViewHolder bannerAdsViewHolder = (BannerAdsViewHolder) holder;
//                AdView mAdView = new AdView(mContext);
//                mAdView.setAdSize(AdSize.SMART_BANNER);
//                mAdView.setAdUnitId("ca-app-pub-3940256099942544/6300978111");
//                bannerAdsViewHolder.adMobView.addView(mAdView);
//                AdRequest adRequest = new AdRequest.Builder().build();
//                mAdView.loadAd(adRequest);

//                AdRequest adRequest = new AdRequest.Builder().build();
//                bannerAdsViewHolder.googleAdView.loadAd(adRequest);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return newsItemModelArrayList.size();
    }

    public class TrendingViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivTrendingNews, ivTrendingRightArrow;
        private TextView txtTrendingNewsTitle, txtTrendingNewsTime;
        private CardView cvTrendingNews;

        public TrendingViewHolder(@NonNull View itemView) {
            super(itemView);
            ivTrendingNews = itemView.findViewById(R.id.ivTrendingNews);
            txtTrendingNewsTitle = itemView.findViewById(R.id.txtTrendingNewsTitle);
            txtTrendingNewsTime = itemView.findViewById(R.id.txtTrendingNewsTime);
            ivTrendingRightArrow = itemView.findViewById(R.id.ivTrendingRightArrow);
            cvTrendingNews = itemView.findViewById(R.id.cvTrendingNews);

        }
    }

    public class BannerAdsViewHolder extends RecyclerView.ViewHolder {
        private com.google.android.gms.ads.AdView googleAdView;
        private RelativeLayout adMobView;

        public BannerAdsViewHolder(@NonNull View itemView) {
            super(itemView);
            adMobView = itemView.findViewById(R.id.adMobView);
//            googleAdView = itemView.findViewById(R.id.googleAdView);
        }
    }

    public interface TrendingNewsInterface {
        void trendingNewsMethod(String newsId);
    }

    @Override
    public int getItemViewType(int position) {
        if (position > 1 && (position + 1) % 4 == 0) {
            return BANNER_VIEW;
        }
        return TRENDING_VIEW;
    }
}
