package com.mogli.notificationlog2;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public final class NotificationsContract {

    public static final String CONTENT_AUTHORITY = "com.mogli.notificationslog2";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_NOTIFS = "notifications";

    private NotificationsContract() {
    }

    public static final class NotifEntry implements BaseColumns {

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_NOTIFS);

        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_NOTIFS;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single notification.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_NOTIFS;

        public final static String TABLE_NAME = "notifications";

        public final static String _ID = BaseColumns._ID;

        public final static String COLUMN_NOTIF_TEXT = "text";
        public final static String COLUMN_NOTIF_TITLE = "title";
        public final static String COLUMN_NOTIF_POST_TIME = "postTime";
    }

}
