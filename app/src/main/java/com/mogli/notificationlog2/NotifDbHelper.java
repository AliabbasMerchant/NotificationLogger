package com.mogli.notificationlog2;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class NotifDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "notif.db";
    private static final int DATABASE_VERSION = 1;

    public NotifDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_NOTIF_TABLE = "CREATE TABLE " + NotificationsContract.NotifEntry.TABLE_NAME + "("
                + NotificationsContract.NotifEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + NotificationsContract.NotifEntry.COLUMN_NOTIF_APP_NAME + " TEXT , "
                + NotificationsContract.NotifEntry.COLUMN_NOTIF_APP_DATA_TITLE + " TEXT, "
                + NotificationsContract.NotifEntry.COLUMN_NOTIF_APP_DATA_TEXT + " TEXT , "
                + NotificationsContract.NotifEntry.COLUMN_NOTIF_APP_DATA_PACKAGE_NAME + " TEXT , "
                + NotificationsContract.NotifEntry.COLUMN_NOTIF_APP_TIME_IN_MILLI + " INTEGER );";

        db.execSQL(SQL_CREATE_NOTIF_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
