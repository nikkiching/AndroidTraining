package com.ching_chang.piggydiary;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ReportService extends Service {
    public static final String TAG = "ReportService";
    public static final String ACTION_REMINDER = "com.ching_chang.piggydiary.REMINDER";
    public static final String ACTION_REPORT = "com.ching_chang.piggydiary.REPORT";
    public static final String KEY_PATH = "Path";
    private static final String MSG_PATH = "File saved to the path: ";
    private Notification.Builder mNotifyBuilder;
    private NotificationManager mManager;
    private Handler mHandler;

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        iniNotify();
        mHandler = new ReportHandler(this);
    }

    protected static class ReportHandler extends Handler {
        private Context mContext;
        protected ReportHandler(Context context){
            mContext = context;
        }
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Toast.makeText(mContext, MSG_PATH + msg.getData().getString(KEY_PATH,""),
                    Toast.LENGTH_LONG).show();
        }
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
        intent.setAction(ACTION_REPORT);
        PendingIntent p = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        mNotifyBuilder.setContentIntent(p);
        mManager.notify(0, mNotifyBuilder.build());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        switch (intent.getAction()){
            case ACTION_REMINDER:
                Log.d(TAG, "Polling");
                showNotify();
                Log.d(TAG, "New notify shows");
                stopSelf();
                break;
            case ACTION_REPORT:
                Thread saveReport = new RunBackup();
                saveReport.start();
                break;
        }
        return super.onStartCommand(intent, flags, startId);
    }
    private File createOutFile(){
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File storageDir = Environment.getExternalStorageDirectory();
        final String outFileName = ItemDbAdapter.DB_NAME + "_" + timeStamp;
        try {
            File outFile = File.createTempFile(
                    outFileName,  /* prefix */
                    ".csv",         /* suffix */
                    storageDir      /* directory */
            );
            Log.d(TAG, "File: " + outFile.getAbsolutePath());
            return outFile;
        } catch (IOException ex){
            Log.e(TAG, "Cannot access external storage.");
            return null;
        }
    }

    protected class RunBackup extends Thread {
        public void run() {
            Log.d(TAG, "Backup start.");
            ItemDbAdapter dbAdapter = new ItemDbAdapter(ReportService.this);
            try {
                File outFile = createOutFile();
                Log.d(TAG, "Output File: stream created");
                // Transfer bytes from the inputfile to the outputfile
                dbAdapter.dbOpen();
                Log.d(TAG, "DB open");
                dbAdapter.createCSV(outFile);
                Log.d(TAG, "Backup finish");
                Bundle fileInfo = new Bundle();
                fileInfo.putString(KEY_PATH, outFile.getPath());
                Message msg = new Message();
                msg.setData(fileInfo);
                mHandler.sendMessage(msg);
            } catch (IOException ex) {
                Log.e(TAG, "Problem happened when writing to csv file.");
            } catch (NullPointerException ex){
                Log.e(TAG, "Cannot find outFile.");
            }
            finally {
                dbAdapter.dbClose();
                Log.d(TAG, "DB close");
                Log.d(TAG, "Service finish");
                stopSelf();
            }
        }
    }
}
