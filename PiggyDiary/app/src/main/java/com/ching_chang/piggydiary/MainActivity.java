package com.ching_chang.piggydiary;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;


public class MainActivity extends ActionBarActivity {

    public static final String TAG = "Main";
    public static final String PREFER = "MainPrefer";
    public static final String PREFER_TIME_INTERVAL = "Timer";
    private static int mTimer;
    private static final int MINUT_IN_MILLIS = 60000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
        Intent intent = getIntent();
        if (ReportService.ACTION_REPORT.equals(intent.getAction())){
            reportDialog(this);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    public void reportDialog(final Context context){
        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setTitle(R.string.backup_title);
        dialog.setMessage(R.string.backup_text);
        dialog.setPositiveButton(android.R.string.yes, new
                DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent backup = new Intent(context, ReportService.class);
                        backup.setAction(ReportService.ACTION_REPORT);
                        startService(backup);
                    }
                });
        dialog.setNegativeButton(android.R.string.no, null);
        dialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setTitle(R.string.default_notify)
                .setSingleChoiceItems(R.array.notify_minutes_array, mTimer,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mTimer = which;
                            }
                        })
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        SharedPreferences settings = getSharedPreferences(PREFER, Context.MODE_PRIVATE);
                        int oldTimer = settings.getInt(PREFER_TIME_INTERVAL, 0);
                        if (mTimer != oldTimer) {
                            settings.edit().putInt(PREFER_TIME_INTERVAL, mTimer).apply();
                            cancelAlarm();
                            if (mTimer != 0) setAlarm(mTimer);
                        }
                    }
                })
                .setNegativeButton(R.string.cancel, null);
            dialog.show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static class PlaceholderFragment extends Fragment {

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            restorePreferValue();
            return rootView;
        }

        private void restorePreferValue(){
            SharedPreferences settings = getActivity().getSharedPreferences(PREFER, Context.MODE_PRIVATE);
            mTimer = settings.getInt(PREFER_TIME_INTERVAL, 0);
        }

        @Override
        public void onPause() {
            super.onPause();
        }
    }

    public void onClickMain(View v){
        int id = v.getId();
        Intent intent = new Intent();
        switch (id) {
            case R.id.payment_btn:
                intent.setClass(this,UpdateActivity.class);
                intent.setAction(UpdateActivity.ADD_PAYMENT);
                break;
            case R.id.income_btn:
                intent.setClass(this,UpdateActivity.class);
                intent.setAction(UpdateActivity.ADD_INCOME);
                break;
            case R.id.report_btn:
                reportDialog(this);
                cancelAlarm();
                if (mTimer != 0) setAlarm(mTimer);
                return;
            case R.id.daily_view:
                intent.setClass(this,ItemListActivity.class);
                intent.setAction(ItemListActivity.SHOW_DAY);
                break;
            case R.id.weekly_view:
                intent.setClass(this,ItemListActivity.class);
                intent.setAction(ItemListActivity.SHOW_WEEK);
                break;
            case R.id.monthly_view:
                intent.setClass(this,ItemListActivity.class);
                intent.setAction(ItemListActivity.SHOW_MONTH);
                break;
        }
        startActivity(intent);
    }

    public void setAlarm(int choice){
        String[] timeChoice = getResources().getStringArray(R.array.notify_minutes_value_array);
        ReportUtils.startRemind(this, ReportService.class,
                Integer.parseInt(timeChoice[choice]) * MINUT_IN_MILLIS,
                ReportService.ACTION_REMINDER);
        Log.d(TAG, "Start the alarm....");
    }
    public void cancelAlarm(){
        Log.d(TAG, "Cancel the alarm....");
        ReportUtils.stopRemind(this, ReportService.class, ReportService.ACTION_REMINDER);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
