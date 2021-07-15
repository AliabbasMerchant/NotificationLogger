package com.mogli.notificationlog2;

import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class NotifCursorAdaptor extends CursorAdapter {

    public NotifCursorAdaptor(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView appName = view.findViewById(R.id.app_name);
        TextView time = view.findViewById(R.id.time);
        TextView date = view.findViewById(R.id.date);
        TextView appTitle = view.findViewById(R.id.app_title);
        TextView appText = view.findViewById(R.id.app_text);
        ImageView imageView = view.findViewById(R.id.appIcon);

//        int notifIdColumnIndex = cursor.getColumnIndex(NotificationsContract.NotifEntry.COLUMN_NOTIF_ID);
        int appNameColumnIndex = cursor.getColumnIndex(NotificationsContract.NotifEntry.COLUMN_NOTIF_APP_NAME);
        int appTimeColumnIndex = cursor.getColumnIndex(NotificationsContract.NotifEntry.COLUMN_NOTIF_APP_DATA_TIME);
        int appDateColumnIndex = cursor.getColumnIndex(NotificationsContract.NotifEntry.COLUMN_NOTIF_APP_DATA_DATE);
        int appTitleColumnIndex = cursor.getColumnIndex(NotificationsContract.NotifEntry.COLUMN_NOTIF_APP_DATA_TITLE);
        int appTextColumnIndex = cursor.getColumnIndex(NotificationsContract.NotifEntry.COLUMN_NOTIF_APP_DATA_TEXT);
        int appPackageNameColumnIndex = cursor.getColumnIndex(NotificationsContract.NotifEntry.COLUMN_NOTIF_APP_DATA_PACKAGE_NAME);

        String appPackageName = cursor.getString(appPackageNameColumnIndex);
        try {
            Drawable icon = context.getPackageManager().getApplicationIcon(appPackageName);
            imageView.setImageDrawable(icon);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        appName.setText(cursor.getString(appNameColumnIndex));
        time.setText(cursor.getString(appTimeColumnIndex));
        date.setText(cursor.getString(appDateColumnIndex));
        appTitle.setText(cursor.getString(appTitleColumnIndex));
        appText.setText(cursor.getString(appTextColumnIndex));
    }
}
