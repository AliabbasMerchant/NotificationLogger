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
        Log.d(TAG, sbn.toString());
        NotificationObject no = new NotificationObject(sbn, context);
        if (no.getText().length() == 0)
            return;
        if (!no.getPackageName().equals("com.whatsapp"))
            return;
        if (no.getTitle().equals("WhatsApp"))
            return;
        if (no.getTitle().equals("WhatsApp Web"))
            return;
        String title = no.getTitle();
        String text = no.getText();
        long postTime = no.getPostTime();
        String packageName = no.getPackageName();
        String[] selectionArgs = new String[]{title, text, String.valueOf(postTime)};
        String selection = String.format("%s = ? AND %s = ? AND %s = ?",
                NotificationsContract.NotifEntry.COLUMN_NOTIF_TITLE,
                NotificationsContract.NotifEntry.COLUMN_NOTIF_TEXT,
                NotificationsContract.NotifEntry.COLUMN_NOTIF_POST_TIME);
        String[] projection = {
                NotificationsContract.NotifEntry._ID,
                NotificationsContract.NotifEntry.COLUMN_NOTIF_TITLE,
                NotificationsContract.NotifEntry.COLUMN_NOTIF_TEXT,
                NotificationsContract.NotifEntry.COLUMN_NOTIF_POST_TIME
        };
        Cursor cursor = context.getContentResolver().query(NotificationsContract.NotifEntry.CONTENT_URI, projection, selection, selectionArgs, null);
        if (cursor != null && cursor.getCount() > 0) {
            Log.e(TAG, "handlePosted: Exists", null);
            return;
        }
        synchronized (LOCK) {
            ContentValues values = new ContentValues();
            values.put(NotificationsContract.NotifEntry.COLUMN_NOTIF_TITLE, title);
            values.put(NotificationsContract.NotifEntry.COLUMN_NOTIF_TEXT, text);
            values.put(NotificationsContract.NotifEntry.COLUMN_NOTIF_POST_TIME, postTime);
            context.getContentResolver().insert(NotificationsContract.NotifEntry.CONTENT_URI, values);
        }
    }
}
