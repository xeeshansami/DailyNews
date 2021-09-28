package com.daily_smart.news_app.Activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.daily_smart.news_app.CustomViews.ProgressBarDialog;
import com.daily_smart.news_app.Models.NewsItemModel;
import com.daily_smart.news_app.R;
import com.daily_smart.news_app.Utilities.Config;
import com.daily_smart.news_app.Utilities.GeneralFunctions;
import com.daily_smart.news_app.Utilities.ShareData;
import com.daily_smart.news_app.Volley.VideoUploadHelper;
import com.daily_smart.news_app.Volley.VolleyMultipartRequest;
import com.daily_smart.news_app.Volley.VolleySingleton;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PostNewsActivity extends AppCompatActivity implements View.OnClickListener {

    private ShareData shareData;
    private Uri mCropImageUri;
    private RelativeLayout idImagePicker;
    private CropImageView mCropImageView;
    private String imgBase64 = Config.EMPTY_STRING;
    private String imgBase64LogoURL = Config.EMPTY_STRING;
    private LinearLayout parentLayoutPostNews;
    private ImageView ivPostNewsImage, ivBackPostNews;
    private EditText etNewsTitle, etNewsDescription, etNewsYoutubeLink;
    private TextView txtSubmitNews, txtNewsDescriptionCount, txtNewsTitleCount;
    private String descriptionCount = Config.EMPTY_STRING;
    private String titleCountCount = Config.EMPTY_STRING;
    private String PostNewsType = Config.EMPTY_STRING;
    private ProgressBarDialog progressBarDialog;
    private FrameLayout layoutCamera;
    private String videoFileName = Config.EMPTY_STRING;
    private int GALLERY = 1, CAMERA = 2;
    private int  GALLERY_VIDEO = 3;
    private Uri videoURI = null;
    private static final String VIDEO_DIRECTORY = "/BharatShorts";
    private VideoView videoView;
    private Button btnUploadVideo;
    private ImageView ivUploadDone;
    private CardView cardViewYoutubeLink, cardViewPostNewsImage;
    private String newsType = "add";
    private String newsId = Config.EMPTY_STRING;
    private String districtId = Config.EMPTY_STRING;
    private ArrayList<NewsItemModel> newsItemModelArrayList = new ArrayList<>();
    private NewsItemModel newsItemModel = null;
    private String editNewsProfile = Config.EMPTY_STRING;
    private Bitmap logoBitmap = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_news);
        shareData = new ShareData(this);
        parentLayoutPostNews = findViewById(R.id.parentLayoutPostNews);
        layoutCamera = findViewById(R.id.layoutCamera);
        ivPostNewsImage = findViewById(R.id.ivPostNewsImage);
        etNewsTitle = findViewById(R.id.etNewsTitle);
        etNewsDescription = findViewById(R.id.etNewsDescription);
        etNewsYoutubeLink = findViewById(R.id.etNewsYoutubeLink);
        txtNewsDescriptionCount = findViewById(R.id.txtNewsDescriptionCount);
        txtNewsTitleCount = findViewById(R.id.txtNewsTitleCount);
        idImagePicker = findViewById(R.id.idImagePicker);
        txtSubmitNews = findViewById(R.id.txtSubmitNews);
        ivBackPostNews = findViewById(R.id.ivBackPostNews);
        videoView = findViewById(R.id.videoView);
        btnUploadVideo = findViewById(R.id.btnUploadVideo);
        ivUploadDone = findViewById(R.id.ivUploadDone);
        cardViewPostNewsImage = findViewById(R.id.cardViewPostNewsImage);
        cardViewYoutubeLink = findViewById(R.id.cardViewYoutubeLink);
        progressBarDialog = new ProgressBarDialog(this);

        if (!GeneralFunctions.isNetworkAvailable(this)) {
            GeneralFunctions.showToast(getResources().getString(R.string.no_internet), this);
        } else {
            getProfileDetails(shareData.getCustomerId());
        }

        if (getIntent().hasExtra("PostNewsType")) {
            PostNewsType = getIntent().getStringExtra("PostNewsType");
        }
        if (getIntent().hasExtra("NewsId")) {
            newsId = getIntent().getStringExtra("NewsId");
            newsType = "update";
            getNewsDetails(newsId);
        }
        if (getIntent().hasExtra("EditNewsProfile")) {
            editNewsProfile = getIntent().getStringExtra("EditNewsProfile");
        }
        if (newsType.equalsIgnoreCase("add")) {
            if (PostNewsType.equalsIgnoreCase("Image")) {
                cardViewPostNewsImage.setVisibility(View.VISIBLE);
                videoView.setVisibility(View.GONE);
                btnUploadVideo.setVisibility(View.GONE);
                cardViewYoutubeLink.setVisibility(View.GONE);
            } else if (PostNewsType.equalsIgnoreCase("Video")) {
                cardViewPostNewsImage.setVisibility(View.GONE);
                btnUploadVideo.setVisibility(View.VISIBLE);
                cardViewYoutubeLink.setVisibility(View.GONE);
            } else if (PostNewsType.equalsIgnoreCase("Links")) {
                cardViewYoutubeLink.setVisibility(View.VISIBLE);
                cardViewPostNewsImage.setVisibility(View.GONE);
                btnUploadVideo.setVisibility(View.GONE);
            }
        }
        logoBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.news_logo);
        layoutCamera.setOnClickListener(this);
        txtSubmitNews.setOnClickListener(this);
        ivBackPostNews.setOnClickListener(this);
        etNewsDescription.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                int length = etNewsDescription.length();
                descriptionCount = String.valueOf(length);
                txtNewsDescriptionCount.setText(descriptionCount + " / " + "500");
            }
        });
        etNewsTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                int length = etNewsTitle.length();
                titleCountCount = String.valueOf(length);
                txtNewsTitleCount.setText(titleCountCount + " / " + "75");
            }
        });

        imgBase64LogoURL = LogoBitMapToString(logoBitmap);
        btnUploadVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPictureDialog();
            }
        });
    }

    private void getNewsDetails(final String newsId) {
        try {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.NEWS_DETAILS,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                progressBarDialog.dismissProgressBar(PostNewsActivity.this);
                                JSONObject jsonObject = new JSONObject(response);
                                JSONArray jsonArray = jsonObject.getJSONArray("news_data");
                                newsItemModelArrayList = NewsItemModel.fromJson(jsonArray);
                                newsItemModel = newsItemModelArrayList.get(0);
                                etNewsTitle.setText(newsItemModel.getPostTitleTe());
                                etNewsDescription.setText(newsItemModel.getPostDetailsTe());
                                try {
                                    if (!newsItemModel.getYoutube_link().isEmpty() && !newsItemModel.getYoutube_link().equalsIgnoreCase("0")) {
                                        PostNewsType = "Links";
                                        etNewsYoutubeLink.setText(newsItemModel.getYoutube_link());
                                    } else if (!newsItemModel.getPostVideo().isEmpty()) {
                                        PostNewsType = "Video";
                                        ivUploadDone.setVisibility(View.VISIBLE);
                                        videoFileName = newsItemModel.getPostVideo();
                                    } else {
                                        PostNewsType = "Image";
                                        Picasso.get()
                                                .load(Config.NEWS_IMAGE_URL + newsItemModel.getPostImages().get(0).getPostImage())
                                                .placeholder(R.drawable.no_image)
                                                .error(R.drawable.no_image)
                                                .into(ivPostNewsImage);
                                        imgBase64 = newsItemModel.getPostImages().get(0).getPostImage();
                                    }

                                    if (PostNewsType.equalsIgnoreCase("Image")) {
                                        cardViewPostNewsImage.setVisibility(View.VISIBLE);
                                        videoView.setVisibility(View.GONE);
                                        btnUploadVideo.setVisibility(View.GONE);
                                        cardViewYoutubeLink.setVisibility(View.GONE);
                                    } else if (PostNewsType.equalsIgnoreCase("Video")) {
                                        cardViewPostNewsImage.setVisibility(View.GONE);
                                        btnUploadVideo.setVisibility(View.VISIBLE);
                                        cardViewYoutubeLink.setVisibility(View.GONE);
                                    } else if (PostNewsType.equalsIgnoreCase("Links")) {
                                        cardViewYoutubeLink.setVisibility(View.VISIBLE);
                                        cardViewPostNewsImage.setVisibility(View.GONE);
                                        btnUploadVideo.setVisibility(View.GONE);
                                    }

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            } catch (
                                    Exception ex) {
                                // Catch exception here
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            try {
                                progressBarDialog.dismissProgressBar(PostNewsActivity.this);
                            } catch (Exception e) {

                            }
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("news_id", newsId);
                    params.put("user_id", shareData.getCustomerId());
                    return params;
                }
            };
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(30 * 1000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            VolleySingleton.getInstance(PostNewsActivity.this).addToRequestQueue(stringRequest);
            progressBarDialog.showProgressBar(PostNewsActivity.this);
        } catch (Exception ex) {
            //todo
        }
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.layoutCamera:
//                startActivityForResult(getPickImageChooserIntent(), 200);

                if (ContextCompat.checkSelfPermission(PostNewsActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(PostNewsActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(PostNewsActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA},
                                400);
                    }
                } else {
                    selectImage();
                }
                break;
            case R.id.ivBackPostNews:
                onBackPressed();
                break;
            case R.id.txtSubmitNews:
                if (validateInputFields()) {
                    if (!GeneralFunctions.isNetworkAvailable(PostNewsActivity.this)) {
                        GeneralFunctions.showToast(getResources().getString(R.string.no_internet), PostNewsActivity.this);
                    } else {
                        AlertDialog.Builder builder;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            builder = new AlertDialog.Builder(PostNewsActivity.this, R.style.MyDialogTheme);
                        } else {
                            builder = new AlertDialog.Builder(PostNewsActivity.this);
                        }
                        builder.setTitle(getString(R.string.confirmation))
                                .setMessage(getString(R.string.news_post))
                                .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        postNews();
                                    }
                                }).setNegativeButton(getString(R.string.no), null);
                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();
                        alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.red));
                        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.black));
                    }
                }
                break;
        }
    }
    private void selectImage() {
        final CharSequence[] options = {"Take Photo", "Choose from Gallery", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(PostNewsActivity.this);
        builder.setTitle("Add Photo!");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Take Photo")) {
                    Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, 1);
                } else if (options[item].equals("Choose from Gallery")) {
                    Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, 2);
                } else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(PostNewsActivity.this, MainActivity.class);
        intent.putExtra("EditNewsProfile", editNewsProfile);
        startActivity(intent);
    }

    private void showPictureDialog() {
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(this);
        pictureDialog.setTitle("Select Action");
        String[] pictureDialogItems = {
                "Select video from gallery"}; // "Record video from camera"
        pictureDialog.setItems(pictureDialogItems,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                chooseVideoFromGallery();
                                break;
                         /*   case 1:
                                takeVideoFromCamera();
                                break;*/
                        }
                    }
                });
        pictureDialog.show();
    }

    public void chooseVideoFromGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(galleryIntent, GALLERY_VIDEO);
    }

    private void takeVideoFromCamera() {
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        startActivityForResult(intent, CAMERA);
    }

    // Create a chooser intent to select the source to get image from.
    public Intent getPickImageChooserIntent() {
        Intent chooserIntent = null;
        try {
            Uri outputFileUri = getCaptureImageOutputUri();
            List<Intent> allIntents = new ArrayList<>();
            PackageManager packageManager = getPackageManager();

            // collect all camera intents
            Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
            for (ResolveInfo res : listCam) {
                Intent intent = new Intent(captureIntent);
                intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
                intent.setPackage(res.activityInfo.packageName);
                if (outputFileUri != null) {
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
                }
                allIntents.add(intent);
            }
            // collect all gallery intents
            Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
            galleryIntent.setType("image/*");
            List<ResolveInfo> listGallery = packageManager.queryIntentActivities(galleryIntent, 0);
            for (ResolveInfo res : listGallery) {
                Intent intent = new Intent(galleryIntent);
                intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
                intent.setPackage(res.activityInfo.packageName);
                allIntents.add(intent);
            }
            // the main intent is the last in the list
            Intent mainIntent = allIntents.get(allIntents.size() - 1);
            for (Intent intent : allIntents) {
                if (intent.getComponent().getClassName().equals("com.android.documentsui.DocumentsActivity")) {
                    mainIntent = intent;
                    break;
                }
            }
            allIntents.remove(mainIntent);
            // Create a chooser from the main intent
            chooserIntent = Intent.createChooser(mainIntent, "Select Image");

            // Add all other intents
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, allIntents.toArray(new Parcelable[allIntents.size()]));
        } catch (Exception e) {
            // e.printStackTrace();
        }
        return chooserIntent;
    }

    //Get URI to image received from capture by camera.
    private Uri getCaptureImageOutputUri() {
        Uri outputFileUri = null;
        try {
            File getImage = getExternalCacheDir();
            if (getImage != null) {
                outputFileUri = Uri.fromFile(new File(getImage.getPath(), "pickImageResult.jpeg"));
            }
        } catch (Exception e) {
            // e.printStackTrace();
        }
        return outputFileUri;
    }

    @Override
    @SuppressLint("NewApi")
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
       /* if (requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE && resultCode == AppCompatActivity.RESULT_OK) {
            Uri imageUri = CropImage.getPickImageResultUri(PostNewsActivity.this, data);
            // For API >= 23 we need to check specifically that we have permissions to read external storage,
            // but we don't know if we need to for the URI so the simplest is to try open the stream and see if we get error.
            boolean requirePermissions = false;
            if (CropImage.isReadExternalStoragePermissionsRequired(PostNewsActivity.this, imageUri)) {
                // request permissions and handle the result in onRequestPermissionsResult()
                requirePermissions = true;
                mCropImageUri = imageUri;
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, CropImage.PICK_IMAGE_PERMISSIONS_REQUEST_CODE);
            } else {
                showCroppedImage(imageUri);
            }
        }*/

        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
                    Bitmap photo = (Bitmap) data.getExtras().get("data");
                    Uri tempUri = getImageUri(getApplicationContext(), photo);
                    Log.e("tempUri", "--" + tempUri);
                    mCropImageUri = tempUri;
                    showCroppedImage(tempUri);
                }
                break;
            case 2:
                if (resultCode == RESULT_OK) {
                    if (data != null) {
                        Uri selectedImage = data.getData();
                        Log.e("Uri", "--" + selectedImage);
                        mCropImageUri = selectedImage;
                        showCroppedImage(selectedImage);
                    }
                }
                break;
        }


        if (resultCode == RESULT_CANCELED) {
            return;
        }
        if (requestCode == GALLERY_VIDEO) {
            if (data != null) {
                Uri contentURI = data.getData();
                videoURI = Uri.parse(String.valueOf(contentURI));
                postNewsVideo(videoURI);
            }

        } else if (requestCode == CAMERA) {
            Uri contentURI = data.getData();
            String recordedVideoPath = getPath(contentURI);
            saveVideoToInternalStorage(recordedVideoPath);
            videoView.setVideoURI(contentURI);
            videoView.requestFocus();
            videoView.start();
        }
    }
    private Uri getImageUri(Context applicationContext, Bitmap photo) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        photo.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(PostNewsActivity.this.getContentResolver(), photo, "Title", null);
        return Uri.parse(path);
    }

    private void saveVideoToInternalStorage(String filePath) {
        File newfile;
        try {

            File currentFile = new File(filePath);
            File wallpaperDirectory = new File(Environment.getExternalStorageDirectory() + VIDEO_DIRECTORY);
            newfile = new File(wallpaperDirectory, Calendar.getInstance().getTimeInMillis() + ".mp4");

            if (!wallpaperDirectory.exists()) {
                wallpaperDirectory.mkdirs();
            }

            if (currentFile.exists()) {

                InputStream in = new FileInputStream(currentFile);
                OutputStream out = new FileOutputStream(newfile);

                // Copy the bits from instream to outstream
                byte[] buf = new byte[1024];
                int len;

                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                in.close();
                out.close();
                Log.v("vii", "Video file saved successfully.");
            } else {
                Log.v("vii", "Video saving failed. Source file missing.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public String getPath(Uri uri) {
        String[] projection = {MediaStore.Video.Media.DATA};
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            // HERE YOU WILL GET A NULLPOINTER IF CURSOR IS NULL
            // THIS CAN BE, IF YOU USED OI FILE MANAGER FOR PICKING THE MEDIA
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } else
            return null;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {

        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            selectImage();
        }

        /*if (requestCode == CropImage.CAMERA_CAPTURE_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                CropImage.startPickImageActivity(PostNewsActivity.this);
            }
        }
        if (requestCode == CropImage.PICK_IMAGE_PERMISSIONS_REQUEST_CODE) {
            if (mCropImageUri != null && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showCroppedImage(mCropImageUri);
            }
        }*/
    }

    public void showCroppedImage(Uri imageUri) {
        try {
            parentLayoutPostNews.setVisibility(View.GONE);
            idImagePicker.setVisibility(View.VISIBLE);
            final ImageView imgSelected = findViewById(R.id.imgSelected);
            //Manage image Crop
            mCropImageView = findViewById(R.id.imgCropImage);
            mCropImageView.setImageUriAsync(imageUri);
            ImageView btnCancel = findViewById(R.id.iBtnCancel);
            final ImageView btnSelect = findViewById(R.id.btnSelect);
            final ImageView btnRotate = findViewById(R.id.btnRotate);

            btnSelect.setVisibility(View.VISIBLE);
            imgSelected.setImageBitmap(null);
            imgSelected.setVisibility(View.GONE);
            mCropImageView.setVisibility(View.VISIBLE);
            mCropImageView.setAutoZoomEnabled(false);

            btnSelect.setOnClickListener(new View.OnClickListener() {
                public void onClick(View popupView) {
                    Bitmap cropped = mCropImageView.getCroppedImage(800, 800);
                    if (cropped != null) {
                        imgBase64 = BitMapToString(cropped);
                        imgSelected.setImageBitmap(cropped);
                        cropped = Bitmap.createScaledBitmap(cropped, 200, 200, true);
                        ivPostNewsImage.setImageBitmap(cropped);
                        cropped = null;
                        System.gc();
                        imgSelected.setVisibility(View.VISIBLE);
                        mCropImageView.setVisibility(View.GONE);
                    }
                    idImagePicker.setVisibility(View.GONE);
                    parentLayoutPostNews.setVisibility(View.VISIBLE);
                }

            });
            btnCancel.setOnClickListener(new View.OnClickListener() {
                public void onClick(View popupView) {
                    imgBase64 = "";
                    imgSelected.setVisibility(View.GONE);
                    idImagePicker.setVisibility(View.GONE);
                    parentLayoutPostNews.setVisibility(View.VISIBLE);
                }
            });
            btnRotate.setOnClickListener(new View.OnClickListener() {
                public void onClick(View popupView) {
                    mCropImageView.rotateImage(90);
                }
            });
        } catch (Exception ex) {
            //todo
        }
    }

    public String BitMapToString(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] byteArray = baos.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    public String LogoBitMapToString(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] byteArray = baos.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    private void getProfileDetails(final String userId) {
        try {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.GET_PROFILE_DETAILS,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                JSONObject profileDataObject = jsonObject.getJSONObject("profile_data");
                                districtId = profileDataObject.getString("district_id");

                            } catch (Exception ex) {
                                // Catch exception here
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            try {

                            } catch (Exception e) {

                            }
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("user_id", userId);
                    return params;
                }
            };
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(30 * 1000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            VolleySingleton.getInstance(PostNewsActivity.this).addToRequestQueue(stringRequest);
        } catch (Exception ex) {
            //todo
        }
    }

    private void postNewsVideo(final Uri videoURI) {
        try {
            progressBarDialog.showProgressBar(PostNewsActivity.this);
            VolleyMultipartRequest stringRequest = new VolleyMultipartRequest(Request.Method.POST, Config.UPLOAD_POST_VIDEO_URL,
                    new Response.Listener<NetworkResponse>() {
                        @Override
                        public void onResponse(NetworkResponse response) {
                            try {
                                progressBarDialog.dismissProgressBar(PostNewsActivity.this);
                                String result = new String(response.data);
                                JSONObject jsonObject = new JSONObject(result);
                                GeneralFunctions.showToast(jsonObject.getString("message"), PostNewsActivity.this);
                                Log.e("video", jsonObject.getString("video_name"));
                                videoFileName = jsonObject.getString("video_name");
                                ivUploadDone.setVisibility(View.VISIBLE);

                            } catch (Exception ex) {
                                // Catch exception here
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            try {
                                progressBarDialog.dismissProgressBar(PostNewsActivity.this);
                            } catch (Exception e) {

                            }
                        }
                    }) {

                @Override
                protected Map<String, DataPart> getByteData() {
                    Map<String, DataPart> params = new HashMap<>();
                    if (videoURI != null) {
                        params.put("video", new DataPart("file.mp4", VideoUploadHelper.getFileDataFromDrawable(PostNewsActivity.this, videoURI)));
                    }
                    return params;
                }
            };
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(30 * 1000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            VolleySingleton.getInstance(getBaseContext()).addToRequestQueue(stringRequest);
        } catch (Exception ex) {
            //todo
        }
    }

    private void postNews() {
        try {
            progressBarDialog.showProgressBar(PostNewsActivity.this);
            VolleyMultipartRequest stringRequest = new VolleyMultipartRequest(Request.Method.POST, Config.POST_NEWS,
                    new Response.Listener<NetworkResponse>() {
                        @Override
                        public void onResponse(NetworkResponse response) {
                            try {
                                progressBarDialog.dismissProgressBar(PostNewsActivity.this);
                                String result = new String(response.data);
                                JSONObject jsonObject = new JSONObject(result);
                                if (jsonObject.getString("status").equalsIgnoreCase("200")) {
                                    GeneralFunctions.showToast(jsonObject.getString("message"), PostNewsActivity.this);
                                    startActivity(new Intent(PostNewsActivity.this, MainActivity.class));
                                } else if (jsonObject.getString("status").equalsIgnoreCase("400")) {
                                    GeneralFunctions.showAlert(PostNewsActivity.this, jsonObject.getString("message"));
                                }
                            } catch (Exception ex) {
                                // Catch exception here
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            try {
                                progressBarDialog.dismissProgressBar(PostNewsActivity.this);
                            } catch (Exception e) {

                            }
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("title_te", etNewsTitle.getText().toString().trim());
                    params.put("title_en", etNewsTitle.getText().toString().trim());
                    params.put("description_te", etNewsDescription.getText().toString().trim());
                    params.put("description_en", etNewsDescription.getText().toString().trim());
                    params.put("youtube_link", etNewsYoutubeLink.getText().toString().trim());
                    params.put("district_id", districtId);
                    params.put("category_id", "1");
                    params.put("user_id", shareData.getCustomerId());
                    params.put("video", videoFileName);
                    if (imgBase64.isEmpty()) {
                        params.put("image", imgBase64LogoURL);
                    } else {
                        params.put("image", imgBase64);
                    }
                    params.put("news_id", newsId);
                    params.put("type", newsType);
                    return params;
                }
                /*@Override
                protected Map<String, DataPart> getByteData() {
                    Map<String, DataPart> params = new HashMap<>();
                    if (imageBytes != null) {
                        params.put("profile_pic", new DataPart("file_new.jpg", imageBytes, "image/jpg"));
                    }
                    return params;
                }*/
            };
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(30 * 1000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            VolleySingleton.getInstance(getBaseContext()).addToRequestQueue(stringRequest);
        } catch (Exception ex) {
            //todo
        }
    }

    private boolean validateInputFields() {
        boolean flag = true;
        if (etNewsYoutubeLink.getText().toString().trim().isEmpty() && PostNewsType.equalsIgnoreCase("Links")) {
            GeneralFunctions.showToast(getResources().getString(R.string.youtube_link_required), PostNewsActivity.this);
            flag = false;
        } else if (etNewsTitle.getText().toString().trim().isEmpty() || etNewsTitle.getText().toString().length() < 35) {
            GeneralFunctions.showToast(getResources().getString(R.string.news_title_length), PostNewsActivity.this);
            etNewsTitle.requestFocus();
            flag = false;
        } else if (etNewsDescription.getText().toString().trim().isEmpty() || etNewsDescription.getText().toString().length() < 300) {
            GeneralFunctions.showToast(getResources().getString(R.string.news_description_length), PostNewsActivity.this);
            etNewsDescription.requestFocus();
            flag = false;
        } else if (imgBase64.trim().isEmpty() && PostNewsType.equalsIgnoreCase("Image")) {
            GeneralFunctions.showToast(getResources().getString(R.string.image_required), PostNewsActivity.this);
            etNewsDescription.requestFocus();
            flag = false;
        } else if (videoFileName.isEmpty() && PostNewsType.equalsIgnoreCase("Video")) {
            GeneralFunctions.showToast(getResources().getString(R.string.video_required), PostNewsActivity.this);
            flag = false;
        }
        return flag;
    }
}