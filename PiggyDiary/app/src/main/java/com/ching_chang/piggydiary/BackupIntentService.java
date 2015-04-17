package com.ching_chang.piggydiary;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.Context;
import android.util.Log;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class BackupIntentService extends IntentService {
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    private static final String ACTION_BACKUP = "com.ching_chang.piggydiary.action.BACKUP";
    private static final String ACTION_RESTORE = "com.ching_chang.piggydiary.action.RESTORE";
    private static final String EXTRA_PARAM1 = "com.ching_chang.piggydiary.extra.PARAM1";
    private static final String EXTRA_PARAM2 = "com.ching_chang.piggydiary.extra.PARAM2";
    private static final String TAG = "BackupIntentService";
    private NotificationManager mManager;
    private Notification.Builder mNotifyBuilder;

    public static void startActionBackup(Context context, String param1, String param2) {
        Intent intent = new Intent(context, BackupIntentService.class);
        intent.setAction(ACTION_BACKUP);
        intent.putExtra(EXTRA_PARAM1, param1);
        intent.putExtra(EXTRA_PARAM2, param2);
        context.startService(intent);
    }

    public static void startActionRestore(Context context, String param1, String param2) {
        Intent intent = new Intent(context, BackupIntentService.class);
        intent.setAction(ACTION_RESTORE);
        intent.putExtra(EXTRA_PARAM1, param1);
        intent.putExtra(EXTRA_PARAM2, param2);
        context.startService(intent);
    }

    public BackupIntentService() {
        super("BackupIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_BACKUP.equals(action)) {
                final String param1 = intent.getStringExtra(EXTRA_PARAM1);
                final String param2 = intent.getStringExtra(EXTRA_PARAM2);
                handleActionBackup(param1, param2);
            } else if (ACTION_RESTORE.equals(action)) {
                final String param1 = intent.getStringExtra(EXTRA_PARAM1);
                final String param2 = intent.getStringExtra(EXTRA_PARAM2);
                handleActionRestore(param1, param2);
            }
        }
    }
int count = 0;
    private void handleActionBackup(String param1, String param2) {
        count ++;
        if (count %5 ==0){
            showNotify();
            Log.d(TAG, "New notify shows");
        }
    }

    private void handleActionRestore(String param1, String param2) {

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
