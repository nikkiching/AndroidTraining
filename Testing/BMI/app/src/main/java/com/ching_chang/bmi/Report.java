package com.ching_chang.bmi;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.text.DecimalFormat;


public class Report extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        findViews();
        showResults();
        setListeners();
    }
    private Button btn_back;
    private TextView view_result;
    private TextView view_suggest;
    private void findViews(){
        btn_back = (Button) findViewById(R.id.report_back);
        view_result = (TextView) findViewById(R.id.result);
        view_suggest = (TextView) findViewById(R.id.suggest);
    }
    private void showResults(){
        DecimalFormat nf = new DecimalFormat("0.00");
        Bundle bundle = this.getIntent().getExtras();
        double height = bundle.getDouble("KEY_height")/100;
        double weight = bundle.getDouble("KEY_weight");
        double BMI = weight / (height *height);
        view_result.setText(getText(R.string.bmi_result) + nf.format(BMI));

        //Give health advice
        if(BMI >25){
            view_suggest.setText(R.string.advice_heavy);
        }else if (BMI<20){
            view_suggest.setText(R.string.advice_light);
        }else{
            view_suggest.setText(R.string.advice_average);
        }
    }
    // Listen for btn clicks
    private void setListeners(){
        btn_back.setOnClickListener(back_Main);
    }
    private Button.OnClickListener back_Main = new Button.OnClickListener(){
        public void onClick(View v){
            // Close this Activity
            Report.this.finish();
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_report, menu);
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
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
