package com.example.bezeka.testtaskbezeka.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.bezeka.testtaskbezeka.model.Image;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bezeka on 19.01.2016.
 */
public class DatabaseHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "ImagesManager";

    // Contacts table name
    private static final String TABLE_IMAGES = "images";

    // Contacts Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_LAT = "lat";
    private static final String KEY_LNG = "lng";
    private static final String KEY_TAG = "tag";
    private static final String KEY_PATH = "path";
    private static final String KEY_DATE_TIME = "date_time";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_IMAGES + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_LAT + " TEXT,"
                + KEY_LNG + " TEXT,"
                + KEY_DATE_TIME + " TEXT,"
                + KEY_TAG + " TEXT,"
                + KEY_PATH + " TEXT"
                + ")";
        db.execSQL(CREATE_CONTACTS_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_IMAGES);

        // Create tables again
        onCreate(db);
    }

    public void addImage(Image image) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ID, image.getId());
        values.put(KEY_LAT, image.getLat());
        values.put(KEY_LNG, image.getLng());
        values.put(KEY_DATE_TIME, image.getDateTime());
        values.put(KEY_TAG, image.getTag());
        values.put(KEY_PATH, image.getPath());

        // Inserting Row
        db.insert(TABLE_IMAGES, null, values);
        db.close(); // Closing database connection
    }

    public Image getImage(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_IMAGES, new String[]{KEY_ID,
                        KEY_LAT, KEY_LNG, KEY_DATE_TIME, KEY_TAG, KEY_PATH}, KEY_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        Image image = new Image();
        image.setId(cursor.getInt(0));
        image.setLat(Double.parseDouble(cursor.getString(1)));
        image.setLng(Double.parseDouble(cursor.getString(2)));
        image.setDateTime(cursor.getString(3));
        image.setTag(cursor.getString(4));
        image.setPath(cursor.getString(4));
        // return contact
        return image;
    }

    public List<Image> getAllImages() {
        List<Image> imageList = new ArrayList<Image>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_IMAGES;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Image image = new Image();
                image.setId(cursor.getInt(0));
                image.setLat(Double.parseDouble(cursor.getString(1)));
                image.setLng(Double.parseDouble(cursor.getString(2)));
                image.setDateTime(cursor.getString(3));
                image.setTag(cursor.getString(4));
                image.setPath(cursor.getString(4));
                // Adding image to list
                imageList.add(image);
                Log.d("-----------------", "------------------------------------------");
                Log.d("SQLite -> ", image.getId() + ", " + image.getTag() + ", " + image.getDateTime() + ", " +
                        +image.getLat() + ", " + image.getLng());
            } while (cursor.moveToNext());
        }

        // return images list
        return imageList;
    }

    public int updateImage(Image image) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ID, image.getId());
        values.put(KEY_LAT, image.getLat());
        values.put(KEY_LNG, image.getLng());
        values.put(KEY_DATE_TIME, image.getDateTime());
        values.put(KEY_TAG, image.getTag());
        values.put(KEY_PATH, image.getPath());

        // updating row
        return db.update(TABLE_IMAGES, values, KEY_ID + " = ?",
                new String[]{String.valueOf(image.getId())});
    }

    public void deleteImage(Image image) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_IMAGES, KEY_ID + " = ?",
                new String[]{String.valueOf(image.getId())});
        db.close();
    }
}