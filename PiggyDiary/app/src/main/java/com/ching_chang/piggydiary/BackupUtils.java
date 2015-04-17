package com.ching_chang.piggydiary;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;

/**
 * Created by Ching_Chang on 2015/4/16.
 */
public class BackupUtils {
    public static void startRemind(Context context, Class<?> cls, int millis, String action) {
        AlarmManager manager = (AlarmManager)  context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, cls);
        intent.setAction(action);
        PendingIntent pendingIntent = PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        long triggerStart = SystemClock.elapsedRealtime();
        manager.setRepeating(AlarmManager.ELAPSED_REALTIME, triggerStart, millis, pendingIntent);
    }

    public static void stopRemind(Context context, Class<?> cls, String action){
        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, cls);
        intent.setAction(action);
        PendingIntent pendingIntent = PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        manager.cancel(pendingIntent);
    }

}
