package com.alpha.popularmoviesii.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;

/**
 * Created by Admin on 6/27/2016.
 */
public class MovieProvider extends ContentProvider {



    private static final String PROVIDER_NAME = "com.alpha.popularmoviesii";
    private static final String URL = "content://" + PROVIDER_NAME + "/movies";

    private MovieDbHelper mHelper;

    private SQLiteDatabase db;
    @Override
    public boolean onCreate() {
        mHelper = new MovieDbHelper(getContext());
        db = mHelper.getWritableDatabase();
        return (db!=null);
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        Cursor retCursor;
        retCursor = mHelper.getReadableDatabase().query(
                MovieDbHelper.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );

        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {

        //return MovieContract.MovieEntry.CONTENT_TYPE;
        return URL;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {

        Uri returnUri;

        long _id = db.insert(MovieDbHelper.TABLE_NAME, null, values);
        if ( _id > 0 )
            returnUri = MovieContract.MovieEntry.buildMovieUri(_id);
        else
            throw new android.database.SQLException("Failed to insert row into " + uri);

        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int rowsDeleted;
        if ( null == selection ) selection = "1";

        rowsDeleted = db.delete(
                MovieDbHelper.TABLE_NAME, selection, selectionArgs);

        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        int rowsUpdated;

        rowsUpdated = db.update(MovieDbHelper.TABLE_NAME, values, selection,
                selectionArgs);

        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;

    }
}
