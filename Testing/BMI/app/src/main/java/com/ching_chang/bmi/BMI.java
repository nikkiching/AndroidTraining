package com.ching_chang.bmi;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RemoteController;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;


public class BMI extends ActionBarActivity {
    private static final String TAG = "BMI";
    public static final String PREFER = "BMI_Pref";
    public static final String PREFER_HEIGHT = "BMI_height";
    protected static final String KEY_HEIGHT = "KEY_height";
    protected static final String KEY_WEIGHT = "KEY_weight";

    private Button mCalBtn;
    private EditText mHeightField;
    private EditText mWeightField;

    //protected static final int Menu_About = Menu.FIRST;
    //protected static final int Menu_Quit = Menu.FIRST+1;
    //  Called when the activity is first created.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate()...");
        setContentView(R.layout.activity_bmi);

        openWelcomePopup();
        //Listen for btn clicks
        findViews();
        restorePreferValue();
        setListeners();
    }

    private void findViews() {
        mCalBtn = (Button) findViewById(R.id.submit);
        mHeightField = (EditText)findViewById(R.id.height);
        mWeightField = (EditText)findViewById(R.id.weight);
    }
    private void setListeners() {
        mCalBtn.setOnClickListener(calcBMI);
    }

    private Button.OnClickListener calcBMI = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
            String height = mHeightField.getText().toString();
            String weight = mWeightField.getText().toString();
            if (TextUtils.isEmpty(height) || TextUtils.isEmpty(weight)) {
                //TextUtils.isEmpty(str_height)
                openErrorPopup();
            } else {
                // Switch to Report page
                Intent intent = new Intent();
                intent.setClass(BMI.this, Report.class);
                Bundle bundle = new Bundle();
                bundle.putString(KEY_HEIGHT, height);
                bundle.putString(KEY_WEIGHT, weight);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        }
    };
    // Restore preferences
    private void restorePreferValue(){
        SharedPreferences settings = getSharedPreferences(PREFER, Context.MODE_PRIVATE);
        String preferHeight = settings.getString(PREFER_HEIGHT, "");
        if (! TextUtils.isEmpty(preferHeight)){
            mHeightField.setText(preferHeight);
            mWeightField.requestFocus();
        }
    }
    private void openErrorPopup(){
        Toast.makeText(BMI.this, R.string.input_error_msg,Toast.LENGTH_SHORT).show();
    }
    private void openWelcomePopup(){
        // Using Toast to show about
        Toast popup = Toast.makeText(BMI.this, R.string.bmi_title, Toast.LENGTH_SHORT);
        popup.show();
    }
    private void openOptionsDialog(){
    // Using Dialog to show about
        AlertDialog.Builder dialog = new AlertDialog.Builder(BMI.this);
        dialog.setTitle(R.string.about_title);
        dialog.setMessage(R.string.about_content);

        dialog.setPositiveButton(R.string.about_ok_label, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // close the dialog and return to the app
            }
        });
        dialog.setNegativeButton(R.string.about_homepage_label, new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int which){
                //go to url
                Uri uri = Uri.parse(getString(R.string.about_homepage_uri));
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });
        dialog.show();
    }

    private void bmi_notify(){
        // Build  notification
        Notification.Builder notifyBuilder = new Notification.Builder(this);
        notifyBuilder.setSmallIcon(R.mipmap.ic_launcher);
        notifyBuilder.setContentTitle(getString(R.string.bmi_title));
        notifyBuilder.setContentText(getString(R.string.bmi_notify_text));
        notifyBuilder.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);
        notifyBuilder.setAutoCancel(true);

        // Setting the activity triggered by the notification
        Intent notifyIntent = new Intent(this, BMI.class);
        notifyIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        notifyBuilder.setContentIntent(pendingIntent);

        // Get Notification service
        final int notifyID = 0;
        NotificationManager nManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        nManager.notify(notifyID, notifyBuilder.build());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_bmi, menu);
        super.onCreateOptionsMenu(menu);
        //menu.add(0, Menu_About, 0, "BMI");
        //menu.add(0, Menu_Quit, 0, "Quit");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        super.onOptionsItemSelected(item);
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        switch(id){
            //case Menu_About:
            case R.id.menu_about:
                openOptionsDialog();
                break;
            //case Menu_Quit:
            case R.id.menu_quit:
                finish();
                break;
            case R.id.menu_notify:
                bmi_notify();
                break;
            case R.id.action_settings:
                break;
        }
        return true;
    }

    @Override
    protected void onPause(){
        super.onPause();
        Log.d(TAG, "onPause()...");
        // Save user preferences. Use editor to make changes.
        SharedPreferences settings = getSharedPreferences(PREFER, 0);
        settings.edit().putString(PREFER_HEIGHT, mHeightField.getText().toString()).commit();
    }
}
