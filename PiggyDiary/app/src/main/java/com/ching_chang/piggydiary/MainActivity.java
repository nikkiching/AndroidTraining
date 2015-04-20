package com.ching_chang.piggydiary;

import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceFragment;
import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import android.widget.ToggleButton;


public class MainActivity extends ActionBarActivity {

    public static final String TAG = "Main";
    public static final String PREFER = "MainPrefer";
    public static final String PREFER_BACKUP_REMINDER = "Backup";

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
        if (BackupService.ACTION_BACKUP.equals(intent.getAction())){
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setTitle(R.string.backup_title);
            dialog.setMessage(R.string.backup_text);
            dialog.setPositiveButton(android.R.string.yes, new
                    DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent backup = new Intent(MainActivity.this, BackupService.class);
                            backup.setAction(BackupService.ACTION_BACKUP);
                            startService(backup);
                        }
                    });
            dialog.setNegativeButton(android.R.string.no, null);
            dialog.show();
        }
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
//            PrefFragment prefFragment = new PrefFragment();
//            FragmentManager fragmentManager = getFragmentManager();
//            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//            fragmentTransaction.replace(android.R.id.content, prefFragment);
//            fragmentTransaction.commit();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public static class PrefFragment extends PreferenceFragment {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preference);
        }
    }

    public static class PlaceholderFragment extends Fragment {
        ToggleButton wantBackup;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            wantBackup = (ToggleButton) rootView.findViewById(R.id.backup_btn);
            restorePreferValue();
            return rootView;
        }

        private void restorePreferValue(){
            SharedPreferences settings = getActivity().getSharedPreferences(PREFER, Context.MODE_PRIVATE);
            Boolean reminder = settings.getBoolean(PREFER_BACKUP_REMINDER, false);
            wantBackup.setChecked(reminder);
            if(reminder) {
//                BackupUtils.startRemind(getActivity(), BackupService.class, 5000, BackupService.ACTION_REMINDER);
            } else {
//                BackupUtils.stopRemind(getActivity(), BackupService.class, BackupService.ACTION_REMINDER);
            }
        }

        @Override
        public void onPause() {
            super.onPause();
            // Save user backup reminder preferences. Use editor to make changes.
            SharedPreferences settings = getActivity().getSharedPreferences(PREFER, Context.MODE_PRIVATE);
            settings.edit().putBoolean(PREFER_BACKUP_REMINDER, wantBackup.isChecked()).apply();
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
                Intent backup = new Intent(MainActivity.this, BackupService.class);
                backup.setAction(BackupService.ACTION_BACKUP);
                startService(backup);
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
            case R.id.all_view:
                intent.setClass(this,ItemListActivity.class);
                intent.setAction(ItemListActivity.SHOW_ALL);
                break;
        }
        startActivity(intent);
    }

    public static void message(Context context, String msg){
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    public void onToggleClicked(View view) {
        boolean on = ((ToggleButton) view).isChecked();
        if (on) {
            // Start polling
            Log.d(TAG, "START service");
            BackupUtils.startRemind(this, BackupService.class, 5000, BackupService.ACTION_REMINDER);
        } else {
            //Stop polling service
            System.out.println("Stop polling service...");
            BackupUtils.stopRemind(this, BackupService.class, BackupService.ACTION_REMINDER);
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
