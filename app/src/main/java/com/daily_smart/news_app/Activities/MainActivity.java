package com.daily_smart.news_app.Activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.daily_smart.news_app.CustomViews.LoginDialog;
import com.daily_smart.news_app.CustomViews.PostNewsOptionsDialog;
import com.daily_smart.news_app.Fragments.HomeFragment;
import com.daily_smart.news_app.Fragments.ProfileFragment;
import com.daily_smart.news_app.Fragments.SettingsFragment;
import com.daily_smart.news_app.Fragments.TrendingFragment;
import com.daily_smart.news_app.R;
import com.daily_smart.news_app.Utilities.Config;
import com.daily_smart.news_app.Utilities.GeneralFunctions;
import com.daily_smart.news_app.Utilities.ShareData;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.analytics.FirebaseAnalytics;

public class MainActivity extends AppCompatActivity implements HomeFragment.PassViewsToActivityListener {
    private BottomNavigationView bottomNavigationView;
    private RecyclerView categoriesRecyclerView, newsListRecyclerView;
    private String districtId = Config.EMPTY_STRING;
    private ShareData shareData;
    private String userId = Config.EMPTY_STRING;
    private LoginDialog loginDialog;
    private PostNewsOptionsDialog postNewsOptionsDialog;
    private FloatingActionButton fabNewsPost;
    private View viewLineBottom;
    private String deviceId = Config.EMPTY_STRING;
    private boolean doubleBackToExitPressedOnce = false;
    private boolean reloadFrag = false;
    private HomeFragment homeFragment;
    private FirebaseAnalytics analytics;

    @SuppressLint("HardwareIds")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        shareData = new ShareData(this);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        fabNewsPost = findViewById(R.id.fabNewsPost);
        viewLineBottom = findViewById(R.id.viewLineBottom);
        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);
        userId = shareData.getCustomerId();
        if (getIntent().hasExtra("DistrictId")) {
            districtId = getIntent().getStringExtra("DistrictId");
        } else if (!userId.isEmpty()) {
            districtId = shareData.getDistrictId();
        }
        loginDialog = new LoginDialog(this);
        deviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        shareData.setAuthenticationDeviceId(deviceId);
        Log.e("deviceId", deviceId);

        Bundle bundle = new Bundle();
        bundle.putString("DistrictId", districtId);
        HomeFragment homeFragment = new HomeFragment();
        homeFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, homeFragment)
                .commit();
        fabNewsPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (userId.isEmpty() || userId.equalsIgnoreCase("0")) {
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    intent.putExtra("DistrictId", districtId);
                    startActivity(intent);
                } else if (shareData.getProfileStatus().equalsIgnoreCase("0")) {
                    Intent intent = new Intent(MainActivity.this, EditProfileActivity.class);
                    startActivity(intent);
                } else {
                    postNewsOptionsDialog = new PostNewsOptionsDialog(MainActivity.this, new PostNewsOptionsDialog.PostNewsDialogOptionsInterface() {
                        @Override
                        public void postNewsDialogOptionsMethod(String postNewsType) {
                            Intent intent = new Intent(MainActivity.this, PostNewsActivity.class);
                            intent.putExtra("PostNewsType", postNewsType);
                            intent.putExtra("EditNewsProfile", "home");
                            startActivity(intent);
                        }
                    });
                    postNewsOptionsDialog.showPostNewsDialog();

                }
            }
        });

        if (getIntent().hasExtra("EditNewsProfile")) {
            String reDirection = getIntent().getStringExtra("EditNewsProfile");
            if (reDirection.equalsIgnoreCase("EditNewsProfile")) {
                bottomNavigationView.setSelectedItemId(R.id.navigation_profile);
            } else if (reDirection.equalsIgnoreCase("home")) {
                bottomNavigationView.setSelectedItemId(R.id.navigation_home);
            } else if (reDirection.equalsIgnoreCase("RecentPostList")) {
                Intent intent = new Intent(this, CategoryActivity.class);
                startActivity(intent);
            }
        }
        if (getIntent().hasExtra("Followers")) {
            bottomNavigationView.setSelectedItemId(R.id.navigation_profile);
        }
        analytics = FirebaseAnalytics.getInstance(this);

    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment selectedFragment = null;
            FragmentManager fragmentManager = getSupportFragmentManager();
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    selectedFragment = new HomeFragment();
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, selectedFragment).addToBackStack("home")
                            .commit();
                    break;
                case R.id.navigation_trending:
                    selectedFragment = new TrendingFragment();
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, selectedFragment).addToBackStack("trending")
                            .commit();
                    break;
                case R.id.navigation_profile:
                    if (userId.isEmpty() || userId.equalsIgnoreCase("0")) {
                        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                        intent.putExtra("DistrictId", districtId);
                        startActivity(intent);
                    } else if (shareData.getProfileStatus().equalsIgnoreCase("0")) {
                        Intent intent = new Intent(MainActivity.this, EditProfileActivity.class);
                        startActivity(intent);
                    } else {
                        selectedFragment = new ProfileFragment();
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.fragment_container, selectedFragment).addToBackStack("profile")
                                .commit();
                    }
                    break;
                case R.id.navigation_settings:
                    selectedFragment = new SettingsFragment();
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, selectedFragment).addToBackStack("settings")
                            .commit();
                    break;
                default:
                    break;
            }
            return true;
        }
    };

    @Override
    public void passViewsToActivityListenerMethod(RecyclerView recyclerView, RecyclerView recyclerView1, String viewType) {
        categoriesRecyclerView = recyclerView;
        newsListRecyclerView = recyclerView1;
        if (categoriesRecyclerView.getVisibility() == View.VISIBLE) {
            categoriesRecyclerView.setVisibility(View.GONE);
            bottomNavigationView.setVisibility(View.GONE);
            viewLineBottom.setVisibility(View.GONE);
        } else {
            categoriesRecyclerView.setVisibility(View.VISIBLE);
            bottomNavigationView.setVisibility(View.VISIBLE);
            viewLineBottom.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void passViewsToActivityInitialMethod(RecyclerView recyclerView) {
        categoriesRecyclerView = recyclerView;
    }

    @Override
    public void onBackPressed() {
//        shareData.setScrolledPosition(shareData.getScrolledPosition());
        int selectedItemId = bottomNavigationView.getSelectedItemId();
        if (R.id.navigation_home == selectedItemId) {
            if (doubleBackToExitPressedOnce) {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                return;
            }
            this.doubleBackToExitPressedOnce = true;
            GeneralFunctions.showToast("Please double click to exit app", MainActivity.this);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
        } else {
            reloadFrag = true;
            bottomNavigationView.setSelectedItemId(R.id.navigation_home);
        }
    }

    private void defaultFontSize() {
        Configuration configuration = getResources().getConfiguration();
        configuration.fontScale = (float) 1; //0.85 small size, 1 normal size, 1,15 big etc
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        metrics.scaledDensity = configuration.fontScale * metrics.density;
        configuration.densityDpi = (int) getResources().getDisplayMetrics().xdpi;
        getBaseContext().getResources().updateConfiguration(configuration, metrics);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e("onResume", "onResume");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.e("onRestart", "onRestart");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e("onDestroy", "onDestroy");
    }

    @Override
    protected void onPause() {
        super.onPause();

    }
}