package com.ching_chang.bmi;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RemoteController;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
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
    public static final String Prefer = "BMI_Pref";
    public static final String Prefer_height = "BMI_height";

    //  Called when the activity is first created.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bmi);

        openWelcomePopup();
        //Listen for btn clicks
        findViews();
        restorePreferValue();
        setListeners();
    }
    private Button calcbtn;
    private EditText fieldheight;
    private EditText fieldweight;

    protected static final int Menu_About = Menu.FIRST;
    protected static final int Menu_Quit = Menu.FIRST+1;
    private void findViews(){
        calcbtn = (Button) findViewById(R.id.submit);
        fieldheight = (EditText)findViewById(R.id.height);
        fieldweight = (EditText)findViewById(R.id.weight);
    }
    private void setListeners(){
        calcbtn.setOnClickListener(calcBMI);
    }
    private Button.OnClickListener calcBMI = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
            String str_height = fieldheight.getText().toString();
            String str_weight = fieldweight.getText().toString();
            if (str_height.equals("") || str_weight.equals("")){
                openErrorPopup();
            } else {
                double height = Double.parseDouble(str_height);
                double weight = Double.parseDouble(str_weight);
                // Switch to Report page
                Intent intent = new Intent();
                intent.setClass(BMI.this, Report.class);
                Bundle bundle = new Bundle();
                bundle.putDouble("KEY_height", height);
                bundle.putDouble("KEY_weight", weight);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        }
    };
    // Restore preferences
    private void restorePreferValue(){
        SharedPreferences settings = getSharedPreferences(Prefer, 0);
        String prefer_height = settings.getString(Prefer_height, "");
        if (! "".equals(prefer_height)){
            fieldheight.setText(prefer_height);
            fieldweight.requestFocus();
        }
    }
    private void openErrorPopup(){
        Toast popup = Toast.makeText(BMI.this, R.string.input_error_msg,Toast.LENGTH_SHORT);
        popup.show();
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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_bmi, menu);
        super.onCreateOptionsMenu(menu);
        menu.add(0, Menu_About, 0, "BMI");
        menu.add(0, Menu_Quit, 0, "Quit");
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
        if (id == R.id.action_settings) {
            return true;
        }
        switch(id){
            case Menu_About:
                openOptionsDialog();
                break;
            case Menu_Quit:
                finish();
                break;
            case R.id.action_settings:
                break;
        }
        return true;
    }

    @Override
    protected void onPause(){
        super.onPause();
        // Save user preferences. Use editor to make changes.
        SharedPreferences settings = getSharedPreferences(Prefer, 0);
        settings.edit().putString(Prefer_height, fieldheight.getText().toString()).commit();
    }
}
