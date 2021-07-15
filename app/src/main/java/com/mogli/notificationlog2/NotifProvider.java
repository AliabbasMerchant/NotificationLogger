package com.mogli.notificationlog2;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class NotifProvider extends ContentProvider {

    /**
     * URI matcher code for the content URI for the NOTIFS table
     */
    private static final int NOTIF = 100;
    /**
     * URI matcher code for the content URI for a single NOtIF in the NOTIFS table
     */
    private static final int NOTIF_ID = 101;
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    // Static initializer. This is run the first time anything is called from this class.
    static {
        // The calls to addURI() go here, for all of the content URI patterns that the provider
        // should recognize. All paths added to the UriMatcher have a corresponding code to return
        // when a match is found.

        sUriMatcher.addURI(NotificationsContract.CONTENT_AUTHORITY, NotificationsContract.PATH_NOTIFS, NOTIF);
        sUriMatcher.addURI(NotificationsContract.CONTENT_AUTHORITY, NotificationsContract.PATH_NOTIFS + "/#", NOTIF_ID);
    }

    private NotifDbHelper notifDbHelper;

    @Override
    public boolean onCreate() {
        notifDbHelper = new NotifDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteDatabase db = notifDbHelper.getReadableDatabase();
        Cursor cursor = db.query(NotificationsContract.NotifEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        SQLiteDatabase db = notifDbHelper.getWritableDatabase();
        long id = db.insert(NotificationsContract.NotifEntry.TABLE_NAME, null, values);
        getContext().getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase db = notifDbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        getContext().getContentResolver().notifyChange(uri, null);

        switch (match) {
            case NOTIF:
                return db.delete(NotificationsContract.NotifEntry.TABLE_NAME, selection, selectionArgs);
            case NOTIF_ID:
                selection = NotificationsContract.NotifEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return db.delete(NotificationsContract.NotifEntry.TABLE_NAME, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case NOTIF:
                return NotificationsContract.NotifEntry.CONTENT_LIST_TYPE;
            case NOTIF_ID:
                return NotificationsContract.NotifEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }
}
