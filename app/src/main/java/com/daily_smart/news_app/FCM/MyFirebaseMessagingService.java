package com.daily_smart.news_app.FCM;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.daily_smart.news_app.Activities.NewsDetailsActivity;
import com.daily_smart.news_app.R;
import com.daily_smart.news_app.Utilities.Config;
import com.daily_smart.news_app.Utilities.ShareData;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private ShareData shareData;

    @Override
    public void onNewToken(String firebaseToken) {
        super.onNewToken(firebaseToken);
        shareData = new ShareData(getApplicationContext());
        shareData.setFcmToken(firebaseToken);
        Log.e("firebaseToken", firebaseToken);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        try {
            if (remoteMessage.getData().size() > 0) {
                for (String notificationResponse : remoteMessage.getData().values()) {
                    Log.e("notificationResponse", "--" + notificationResponse);
                    JSONObject jsonObject = new JSONObject(notificationResponse);
                    if (jsonObject.getString("image").equalsIgnoreCase("null") || jsonObject.getString("image").isEmpty()) {
                        GenerateNotification(jsonObject);
                    } else {
                        getBitmapAsyncAndDoWork(jsonObject);
                    }
                }
            }
        } catch (Exception e) {
        }
    }

    private void GenerateNotification(JSONObject remoteMessage) {
        Log.e("generate", "Generate");
        int num = (int) System.currentTimeMillis();

        RemoteViews remoteViews = new RemoteViews(getApplicationContext().getPackageName(),
                R.layout.custom_notification_layout);
        int color = Color.BLACK;
        Bitmap bitmap1 = null;
        try {
            bitmap1 = textAsBitmap(getApplicationContext(), remoteMessage.getString("title"), 30, color);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        remoteViews.setImageViewBitmap(R.id.ivNotificationMessage, bitmap1);

        PendingIntent pendingIntent = null;
        Intent intent = new Intent(this, NewsDetailsActivity.class);
        try {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("NewsId", remoteMessage.getString("news_id"));
            intent.putExtra("title", remoteMessage.getString("title"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        pendingIntent = PendingIntent.getActivity(this, num /* Request code */, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        String channelId = "dailyshortsid";
        String channel_name = "dailyshortschannel";
        CharSequence charSequence = "Dailyshorts";
        int importance = NotificationManager.IMPORTANCE_HIGH;
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = null;
        try {
            notificationBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.drawable.news_logo).setColor(getResources().getColor(R.color.red))
//                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.news_logo))
//                    .setContentTitle(remoteMessage.getString("title"))
//                    .setContentText(remoteMessage.getString("message"))
//                    .setStyle(new NotificationCompat.BigTextStyle().bigText(remoteMessage.getString("title")))
                    .setContent(remoteViews)
                    .setAutoCancel(true)
                    .setPriority(importance)
                    .setChannelId(channel_name)
                    .setSound(defaultSoundUri)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setContentIntent(pendingIntent);

        } catch (Exception e) {
            e.printStackTrace();
        }
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && notificationManager != null) {
            NotificationChannel notificationChannel = new NotificationChannel(channel_name, charSequence, importance);
            notificationManager.createNotificationChannel(notificationChannel);
            notificationManager.notify(num /* ID of notification */, notificationBuilder.build());
        } else {
            notificationManager.notify(num /* ID of notification */, notificationBuilder.build());
        }
    }

    private void GenerateNotificationWithImage(JSONObject remoteMessage, Bitmap bitmap) {
        Log.e("generateImage", "Generate");
        int num = (int) System.currentTimeMillis();
        RemoteViews remoteViews = new RemoteViews(getApplicationContext().getPackageName(),
                R.layout.custom_notification_layout);
        int color = Color.BLACK;
        Bitmap bitmap1 = null;
        try {
            bitmap1 = textAsBitmap(getApplicationContext(), remoteMessage.getString("title"), 30, color);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        remoteViews.setImageViewBitmap(R.id.ivNotificationMessage, bitmap1);

        PendingIntent pendingIntent = null;
        Intent intent = new Intent(this, NewsDetailsActivity.class);
        try {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("NewsId", remoteMessage.getString("news_id"));
            intent.putExtra("title", remoteMessage.getString("title"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        pendingIntent = PendingIntent.getActivity(this, num /* Request code */, intent, PendingIntent.FLAG_ONE_SHOT);

        String channelId = "dailyshortsid";
        String channel_name = "dailyshortschannel";
        CharSequence charSequence = "Dailyshorts";
        int importance = NotificationManager.IMPORTANCE_HIGH;
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = null;
        try {
            notificationBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.drawable.news_logo).setColor(getResources().getColor(R.color.red))
//                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.news_logo))
                    .setContentTitle(remoteMessage.getString("title"))
//                    .setContentText(remoteMessage.getString("message"))
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(remoteMessage.getString("title")))
                    .setAutoCancel(true)
                    .setPriority(importance)
                    .setChannelId(channel_name)
                    .setSound(defaultSoundUri)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setContentIntent(pendingIntent)
                    .setStyle(new NotificationCompat.BigPictureStyle().bigPicture(bitmap));

        } catch (JSONException e) {
            e.printStackTrace();
        }
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && notificationManager != null) {
            NotificationChannel notificationChannel = new NotificationChannel(channel_name, charSequence, importance);
            notificationManager.createNotificationChannel(notificationChannel);
            notificationManager.notify(num /* ID of notification */, notificationBuilder.build());
        } else {
            notificationManager.notify(num /* ID of notification */, notificationBuilder.build());
        }
    }

    private static int getNotificationId() {
        Random rnd = new Random();
        return 100 + rnd.nextInt(9000);
    }

    private void getBitmapAsyncAndDoWork(final JSONObject jsonObject) {

        final Bitmap[] bitmap = {null};
        try {
            Glide.with(getApplicationContext())
                    .asBitmap()
                    .load(Config.NEWS_IMAGE_URL + jsonObject.getString("image"))
                    .into(new CustomTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {

                            bitmap[0] = resource;
                            // TODO Do some work: pass this bitmap
                            GenerateNotificationWithImage(jsonObject, bitmap[0]);
                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {
                        }
                    });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static Bitmap textAsBitmap(Context context, String messageText, float textSize, int textColor) {
        Typeface typeface = Typeface.createFromAsset(context.getAssets(), "ramabhadra_regular.ttf");
        Paint paint = new Paint();
        paint.setTextSize(textSize);
        paint.setTypeface(typeface);
        paint.setColor(textColor);
        paint.setTextAlign(Paint.Align.LEFT);
        float baseline = -paint.ascent(); // ascent() is negative
        int width = (int) (paint.measureText(messageText) + 0.5f); // round
        int height = (int) (baseline + paint.descent() + 0.5f);
        Bitmap image = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(image);
        canvas.drawText(messageText, 0, baseline, paint);
        return image;
    }
}