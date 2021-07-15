package com.mogli.notificationlog2;

import android.app.Notification;
import android.content.Context;
import android.os.Bundle;
import android.service.notification.StatusBarNotification;

import androidx.core.app.NotificationCompat;

class NotificationObject {
    private final String packageName;
    private final long postTime;
    private final boolean isOngoing;

    private final long when;
    private final String appName;
    private String title;
    private String text;
    private String extraText;
    private String textBig;

    public NotificationObject(StatusBarNotification sbn, Context context) {
        Notification notification = sbn.getNotification();
        packageName = sbn.getPackageName();
        postTime = sbn.getPostTime();
        isOngoing = sbn.isOngoing();
        when = notification.when;

        Bundle extras = NotificationCompat.getExtras(notification);
        appName = Utils.getAppNameFromPackage(context, packageName, false);

        if (extras != null) {
            title = Utils.nullToEmptyString(extras.getCharSequence(NotificationCompat.EXTRA_TITLE));
            text = Utils.nullToEmptyString(extras.getCharSequence(NotificationCompat.EXTRA_TEXT));
            extraText = Utils.nullToEmptyString(extras.getCharSequence(Notification.EXTRA_SUB_TEXT));
            textBig = Utils.nullToEmptyString(extras.getCharSequence(NotificationCompat.EXTRA_BIG_TEXT));

            int textlen = text.length();
            if (extraText.length() != 0)
                text += "\n" + extraText;

            if (textBig.length() != 0) {
//                Log.v("NotifObject","index: " +index + "   Modified extra text :" + textBig.substring(textlen + 1));
                text += textBig.substring(textlen);
            }
        }
    }

    public long getPostTime() {
        return postTime;
    }

    public String getAppName() {
        return appName;
    }

    public String getTitle() {
        return title;
    }

    public String getText() {
        return text;
    }

    public String getPackageName() {
        return packageName;
    }

    public boolean getIsOngoing() {
        return isOngoing;
    }
}
