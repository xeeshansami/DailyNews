package com.paxees_daily_smart.paxees_news_app.Utilities;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class TimeAgo {

    public String covertTimeToText(String dataDate) {
        String convTime = null;
        String suffix = "ago";
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date pasTime = null;
        try {
            pasTime = dateFormat.parse(dataDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Date nowTime = new Date();
        long dateDiff = nowTime.getTime() - pasTime.getTime();

        long second = TimeUnit.MILLISECONDS.toSeconds(dateDiff);
        long minute = TimeUnit.MILLISECONDS.toMinutes(dateDiff);
        long hour = TimeUnit.MILLISECONDS.toHours(dateDiff);
        long day = TimeUnit.MILLISECONDS.toDays(dateDiff);

        if (second < 60) {
            convTime = second + " sec " + suffix;
        } else if (minute < 60) {
            convTime = minute + " min " + suffix;
        } else if (hour < 24) {
            convTime = hour + " hours " + suffix;
        } else if (day >= 7) {
            if (day > 360) {
                convTime = (day / 360) + " years " + suffix;
            } else if (day > 30) {
                convTime = (day / 30) + " months " + suffix;
            } else {
                convTime = (day / 7) + " week " + suffix;
            }
        } else if (day < 7) {
            convTime = day + " days " + suffix;
        }

        return convTime;
    }
}
