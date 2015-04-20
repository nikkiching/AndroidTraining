package com.ching_chang.piggydiary;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.Context;
import android.util.Log;


public class ReportIntentService extends IntentService {
    public static final String TAG = "ReportIntentService";
    public static final String ACTION_REMINDER = "com.ching_chang.piggydiary.REMINDER";
    public static final String ACTION_REPORT = "com.ching_chang.piggydiary.REPORT";
    private Notification.Builder mNotifyBuilder;
    private NotificationManager mManager;

    public static void startActionBackup(Context context, String param1, String param2) {
        Intent intent = new Intent(context, ReportIntentService.class);

        context.startService(intent);
    }

    public static void startActionRestore(Context context, String param1, String param2) {
        Intent intent = new Intent(context, ReportIntentService.class);

        context.startService(intent);
    }

    public ReportIntentService() {
        super("BackupIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_REMINDER.equals(action)) {

                handleActionReminder();
            } else if (ACTION_REPORT.equals(action)) {
                handleActionRestore();
            }
        }
    }

    private void handleActionReminder() {
            showNotify();
            Log.d(TAG, "New notify shows");
    }

    private void handleActionRestore() {

    }

    private void iniNotify(){
        mManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mNotifyBuilder = new Notification.Builder(this);
        mNotifyBuilder.setSmallIcon(R.mipmap.ic_launcher);
        mNotifyBuilder.setContentTitle(getString(R.string.app_name));
        mNotifyBuilder.setContentText("New message");
        mNotifyBuilder.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);
        mNotifyBuilder.setAutoCancel(true);
    }

    private void showNotify(){
        // Setting the activity triggered by the notification
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent p = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        mNotifyBuilder.setContentIntent(p);
        //mNotification.
        mManager.notify(0, mNotifyBuilder.build());
    }
}
