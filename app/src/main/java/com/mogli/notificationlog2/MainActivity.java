package com.mogli.notificationlog2;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>, SharedPreferences.OnSharedPreferenceChangeListener {
    private static final String TAG = "MainActivity";

    private static final int NOTIF_LOADER = 1;
    private NotifCursorAdaptor notifCursorAdaptor;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkNotifPermission();

        getNotifPreferences();

        doNotKillService();

        listView = findViewById(R.id.list_view_notif);
        notifCursorAdaptor = new NotifCursorAdaptor(this, null);

        View emptyView = findViewById(R.id.empty_subtitle_text);
        listView.setEmptyView(emptyView);

        listView.setAdapter(notifCursorAdaptor);
        registerForContextMenu(listView);
        listView.setOnItemClickListener((parent, view, position, id) -> {
            TextView textView = view.findViewById(R.id.text);
            if (textView.getMaxLines() == 3)
                textView.setMaxLines(Integer.MAX_VALUE);
            else
                textView.setMaxLines(3);
        });

        getLoaderManager().initLoader(NOTIF_LOADER, null, this);
    }

    private void getNotifPreferences() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

        String deleteAfter = preferences.getString("deletenotifafter", getResources().getString(R.string.pref_value_one_week));
        int deleteAfterInt = Integer.parseInt(deleteAfter);
        doDeleteNotifOlderThanXdays(deleteAfterInt);
        preferences.registerOnSharedPreferenceChangeListener(this);
    }

    private void doDeleteNotifOlderThanXdays(int deleteAfter) {
        long timeNow = System.currentTimeMillis();
        long inMilli30days = deleteAfter * 60 * 60 * 1000;
        long timeBefore30days = timeNow - inMilli30days;
        String where = " postTime < ? ";
        String[] selectionArgs = new String[]{Long.toString(timeBefore30days)};
        int rowsDeleted = getContentResolver().delete(NotificationsContract.NotifEntry.CONTENT_URI, where, selectionArgs);
//        Log.v("deleted : ", "" + rowsDeleted);
    }

    private void doNotKillService() {
        PackageManager pm = getPackageManager();
        pm.setComponentEnabledSetting(new ComponentName(this, com.mogli.notificationlog2.NotificationListener.class),
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
        pm.setComponentEnabledSetting(new ComponentName(this, com.mogli.notificationlog2.NotificationListener.class),
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
    }

    private void checkNotifPermission() {
        boolean isNotificationServiceRunning = isNotificationServiceRunning();
        if (!isNotificationServiceRunning) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder
                    .setTitle("Enable Notification Access")
                    .setMessage("Enable Notification Access or the Notifications won't be Logged")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                            startActivity(new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS));
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }
    }

    private boolean isNotificationServiceRunning() {
        ContentResolver contentResolver = getContentResolver();
        String enabledNotificationListeners =
                Settings.Secure.getString(contentResolver, "enabled_notification_listeners");
        String packageName = getPackageName();
        return enabledNotificationListeners != null && enabledNotificationListeners.contains(packageName);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                NotificationsContract.NotifEntry._ID,
                NotificationsContract.NotifEntry.COLUMN_NOTIF_TITLE,
                NotificationsContract.NotifEntry.COLUMN_NOTIF_TEXT,
                NotificationsContract.NotifEntry.COLUMN_NOTIF_PACKAGE_NAME,
                NotificationsContract.NotifEntry.COLUMN_NOTIF_POST_TIME
        };
        return new CursorLoader(this, NotificationsContract.NotifEntry.CONTENT_URI, projection, null, null, NotificationsContract.NotifEntry._ID + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        notifCursorAdaptor.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        notifCursorAdaptor.swapCursor(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.settings_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.delete_all) {
            alertForDeletingAllNotifications();
            return true;
        } else if (id == R.id.settings_button) {
            Intent settingsActivity = new Intent(this, SettingsActivity.class);
            startActivity(settingsActivity);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void alertForDeletingAllNotifications() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Delete all currently logged notifications?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        int rowsDeleted = getContentResolver().delete(NotificationsContract.NotifEntry.CONTENT_URI, null, null);
                        dialog.cancel();
                        if (rowsDeleted > 0)
                            Toast.makeText(getApplicationContext(), R.string.delted_all_successfully, Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {}
                });
        AlertDialog alert = builder.create();
        alert.setTitle("Delete All Notifications");
        alert.show();
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int menuItemId = item.getItemId();
        int index = info.position;
//        Log.v("index: ",""+index);
        Cursor cursor = (Cursor) listView.getItemAtPosition(index);
        long currNotifId = cursor.getInt(cursor.getColumnIndex(NotificationsContract.NotifEntry._ID));
//        Log.v("notif id : " , ""+currNotifId + " text: " + cursor.getString(cursor.getColumnIndex(NotificationsContract.NotifEntry.COLUMN_NOTIF_APP_DATA_TEXT)));

        if (menuItemId == R.id.delete_current) {
            deleteCurrentNotif(currNotifId);
            return true;
        }
        return super.onContextItemSelected(item);
    }

    private void deleteCurrentNotif(final long notifId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Do you really want to delete this notification ?").setTitle("Delete current Notification")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Uri currentNotifUri = ContentUris.withAppendedId(NotificationsContract.NotifEntry.CONTENT_URI, notifId);
                        int rowDeleted = getContentResolver().delete(currentNotifUri, null, null);
                        if (rowDeleted == 0) {
                            Toast.makeText(getApplicationContext(), "Notification Delete failed", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "Notification Deleted successfully", Toast.LENGTH_SHORT).show();
                        }
                        dialog.cancel();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Do nothing
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.setTitle("Delete selected notification");
        alert.show();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        if (v.getId() == R.id.list_view_notif) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.list_item_menu, menu);
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals("deletenotifafter")) {
            getNotifPreferences();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this);
    }
}


