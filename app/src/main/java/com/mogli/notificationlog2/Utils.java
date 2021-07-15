package com.mogli.notificationlog2;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Utils {
    public static String getAppNameFromPackage(Context context, String packageName, boolean returnNull) {
        final PackageManager pm = context.getApplicationContext().getPackageManager();
        ApplicationInfo ai;
        try {
            ai = pm.getApplicationInfo(packageName, 0);
        } catch (final PackageManager.NameNotFoundException e) {
            ai = null;
        }
        if (returnNull) {
            return ai == null ? null : pm.getApplicationLabel(ai).toString();
        }
        return (String) (ai != null ? pm.getApplicationLabel(ai) : packageName);
    }

    public static String nullToEmptyString(CharSequence charsequence) {
        if (charsequence == null) {
            return "";
        } else {
            return charsequence.toString();
        }
    }

    public static String getDateTime(Long currentTimeMillis) {
        DateFormat df = new SimpleDateFormat("d MMM yyyy HH:mm:ss");
        Date resultDate = new Date(currentTimeMillis);
        return df.format(resultDate);
    }
}
