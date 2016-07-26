package com.alpha.popularmoviesii.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Admin on 6/27/2016.
 */
public class MovieDbHelper extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_NAME = "MovieDB";

    public static final String TABLE_NAME = "movies";

    public static final String movie_id = "movie_id";
    public static final String movie_image = "movie_image";
    public static final String movie_poster = "movie_poster";
    public static final String movie_title = "movie_title";
    public static final String movie_overview = "movie_overview";
    public static final String movie_rating = "movie_rating";
    public static final String movie_popularity = "movie_popularity";
    public static final String movie_date = "movie_date";

    public MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        final String SQL_CREATE_MOVIE_TABLE = "CREATE TABLE " + TABLE_NAME +
                " (_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                movie_id + " TEXT NOT NULL, " +
                movie_image + " TEXT NOT NULL, " +
                movie_poster + " TEXT NOT NULL, " +
                movie_title + " TEXT NOT NULL," +
                movie_overview + " TEXT NOT NULL, " +
                movie_rating + " TEXT NOT NULL, " +
                movie_popularity + " TEXT NOT NULL, " +
                movie_date + " TEXT NOT NULL); ";

        sqLiteDatabase.execSQL(SQL_CREATE_MOVIE_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}