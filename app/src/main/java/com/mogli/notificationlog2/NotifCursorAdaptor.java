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
        TextView datetimeTV = view.findViewById(R.id.datetime);
        TextView titleTV = view.findViewById(R.id.title);
        TextView textTV = view.findViewById(R.id.text);

        int titleColumnIndex = cursor.getColumnIndex(NotificationsContract.NotifEntry.COLUMN_NOTIF_TITLE);
        int postTimeColumnIndex = cursor.getColumnIndex(NotificationsContract.NotifEntry.COLUMN_NOTIF_POST_TIME);
        int textColumnIndex = cursor.getColumnIndex(NotificationsContract.NotifEntry.COLUMN_NOTIF_TEXT);

        datetimeTV.setText(Utils.getDateTime(cursor.getLong(postTimeColumnIndex)));
        titleTV.setText(cursor.getString(titleColumnIndex));
        textTV.setText(cursor.getString(textColumnIndex));
    }
}
