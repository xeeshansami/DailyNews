package com.paxees_daily_smart.paxees_news_app.sqlite;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.paxees_daily_smart.paxees_news_app.Models.NewsItemModel;

public class NewsTableRepository {
    private NewsTable newsTable;

    public NewsTableRepository() {
    }

    public static String createTable() {
        return "CREATE TABLE " + NewsTable.TABLE +
                "(" + NewsTable.NEWS_ID + " TEXT, "
                + NewsTable.NEWS_TITLE + " TEXT, "
                + NewsTable.NEWS_DESCRIPTION + " TEXT, "
                + NewsTable.NEWS_IMAGE + " TEXT )";
    }

    public boolean checkForTables() {
        boolean hasTables = false;
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + NewsTable.TABLE, null);

        if (cursor != null && cursor.getCount() > 0) {
            hasTables = true;
            cursor.close();
        }

        return hasTables;
    }

    public void insert(NewsItemModel newsItemModel) {
        SQLiteDatabase sqLiteDatabase = DatabaseManager.getInstance().openDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(newsTable.NEWS_ID, newsItemModel.getNews_id());
        contentValues.put(newsTable.NEWS_TITLE, newsItemModel.getPostTitleTe());
        contentValues.put(newsTable.NEWS_DESCRIPTION, newsItemModel.getPostDetailsTe());
        contentValues.put(newsTable.NEWS_IMAGE, newsItemModel.getPostImage());

        // Inserting Row
        sqLiteDatabase.insert(NewsTable.TABLE, null, contentValues);
        Cursor cursor = null;
        String sql = "SELECT MAX( " + newsTable.NEWS_ID + " ) FROM " + NewsTable.TABLE;
        cursor = sqLiteDatabase.rawQuery(sql, null);
        cursor.moveToFirst();
        cursor.close();
        DatabaseManager.getInstance().closeDatabase();
    }
}
