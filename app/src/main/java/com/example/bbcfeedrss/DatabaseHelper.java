package com.example.bbcfeedrss;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "news.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_FAVORITES = "favorites";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_TITLE = "title";
    private static final String COLUMN_DESCRIPTION = "description";
    private static final String COLUMN_PUB_DATE = "pub_date";
    private static final String COLUMN_MEDIA_URL = "media_url";
    private static final String COLUMN_LINK = "link";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableQuery = "CREATE TABLE " + TABLE_FAVORITES + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_TITLE + " TEXT, " +
                COLUMN_DESCRIPTION + " TEXT, " +
                COLUMN_PUB_DATE + " TEXT, " +
                COLUMN_MEDIA_URL + " TEXT, " +
                COLUMN_LINK + " TEXT)";
        db.execSQL(createTableQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FAVORITES);
        onCreate(db);
    }

    public boolean addFavorite(String title, String description, String pubDate, String mediaUrl, String link) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_TITLE, title);
        contentValues.put(COLUMN_DESCRIPTION, description);
        contentValues.put(COLUMN_PUB_DATE, pubDate);
        contentValues.put(COLUMN_MEDIA_URL, mediaUrl);
        contentValues.put(COLUMN_LINK, link);
        long result = db.insert(TABLE_FAVORITES, null, contentValues);
        return result != -1;
    }

    public boolean deleteFavorite(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_FAVORITES, COLUMN_ID + "=?", new String[]{String.valueOf(id)}) > 0;
    }

    public ArrayList<NewsItem> getAllFavorites() {
        ArrayList<NewsItem> favoriteList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_FAVORITES, null);
        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID));
                String title = cursor.getString(cursor.getColumnIndex(COLUMN_TITLE));
                String description = cursor.getString(cursor.getColumnIndex(COLUMN_DESCRIPTION));
                String pubDate = cursor.getString(cursor.getColumnIndex(COLUMN_PUB_DATE));
                String mediaUrl = cursor.getString(cursor.getColumnIndex(COLUMN_MEDIA_URL));
                String link = cursor.getString(cursor.getColumnIndex(COLUMN_LINK));
                NewsItem newsItem = new NewsItem(id, title, description, pubDate, mediaUrl, link);
                favoriteList.add(newsItem);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return favoriteList;
    }

    public boolean isFavorite(NewsItem newsItem) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {COLUMN_ID};
        String selection = COLUMN_TITLE + " = ? AND " + COLUMN_DESCRIPTION + " = ? AND " +
                COLUMN_PUB_DATE + " = ? AND " + COLUMN_MEDIA_URL + " = ? AND " + COLUMN_LINK + " = ?";
        String[] selectionArgs = {newsItem.getTitle(), newsItem.getDescription(), newsItem.getPubDate(),
                newsItem.getMediaUrl(), newsItem.getLink()};
        Cursor cursor = db.query(TABLE_FAVORITES, columns, selection, selectionArgs, null, null, null);
        boolean isFavorite = cursor.getCount() > 0;
        cursor.close();
        return isFavorite;
    }
}

