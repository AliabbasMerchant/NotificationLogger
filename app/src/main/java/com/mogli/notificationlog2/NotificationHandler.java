package com.mogli.notificationlog2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.service.notification.StatusBarNotification;
import android.util.Log;

public class NotificationHandler {
    private static final String TAG = "NotificationHandler";

    public static final String LOCK = "lock";

    private final Context context;

    NotificationHandler(Context context) {
        this.context = context;
    }

    void handlePosted(StatusBarNotification sbn) {
//        if(sbn.isOngoing()){
//            return;
//        }
        NotificationObject no = new NotificationObject(sbn, context);
        if (no.getText().length() == 0)
            return;
        String appName = no.getAppName();
        String title = no.getTitle();
        String text = no.getText();
        long timeInMillis = no.getSystemTime();
        String packageName = no.getPackageName();
        String[] selectionArgs = new String[]{appName, text, title, String.valueOf(timeInMillis), packageName};
        String selection = "name = ? AND text = ? AND title = ? AND timeinmilli = ? AND packagename = ?";
        String[] projection = {
                NotificationsContract.NotifEntry._ID,
                NotificationsContract.NotifEntry.COLUMN_NOTIF_APP_NAME,
                NotificationsContract.NotifEntry.COLUMN_NOTIF_APP_DATA_TITLE,
                NotificationsContract.NotifEntry.COLUMN_NOTIF_APP_DATA_TEXT,
                NotificationsContract.NotifEntry.COLUMN_NOTIF_APP_TIME_IN_MILLI,
                NotificationsContract.NotifEntry.COLUMN_NOTIF_APP_DATA_PACKAGE_NAME};
        Cursor cursor = context.getContentResolver().query(NotificationsContract.NotifEntry.CONTENT_URI, projection, selection, selectionArgs, null);
        if (cursor != null && cursor.getCount() > 0) {
            Log.e(TAG, "handlePosted: Exists", null);
            return;
        }
        synchronized (LOCK) {
            ContentValues values = new ContentValues();
            values.put(NotificationsContract.NotifEntry.COLUMN_NOTIF_APP_NAME, appName);
            values.put(NotificationsContract.NotifEntry.COLUMN_NOTIF_APP_DATA_TITLE, title);
            values.put(NotificationsContract.NotifEntry.COLUMN_NOTIF_APP_DATA_TEXT, text);
            values.put(NotificationsContract.NotifEntry.COLUMN_NOTIF_APP_DATA_PACKAGE_NAME, packageName);
            values.put(NotificationsContract.NotifEntry.COLUMN_NOTIF_APP_TIME_IN_MILLI, timeInMillis);
            context.getContentResolver().insert(NotificationsContract.NotifEntry.CONTENT_URI, values);
        }
    }
}
