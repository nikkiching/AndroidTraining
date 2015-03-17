package com.ching_chang.bmi;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.text.DecimalFormat;


public class Report extends BMI {
    private Button backBtn;
    private TextView resultView;
    private TextView suggestView;
    private int bmiNormalMin = 20;
    private int bmiNormalMax = 25;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        findViews();
        showResults();
        setListeners();
    }
    private void findViews(){
        backBtn = (Button) findViewById(R.id.report_back);
        resultView = (TextView) findViewById(R.id.result);
        suggestView = (TextView) findViewById(R.id.suggest);
    }
    private void showResults(){
        DecimalFormat nf = new DecimalFormat("0.00");
        Bundle bundle = this.getIntent().getExtras();

        double height = Double.parseDouble(bundle.getString(BMI.KEY_HEIGHT))/100;
        double weight = Double.parseDouble(bundle.getString(BMI.KEY_WEIGHT));
        double BMI = weight / (height *height);
        resultView.setText(getText(R.string.bmi_result) + nf.format(BMI));

        //Give health advice
        if(BMI > bmiNormalMax){
            suggestView.setText(R.string.advice_heavy);
        }else if (BMI< bmiNormalMin){
            suggestView.setText(R.string.advice_light);
        }else{
            suggestView.setText(R.string.advice_average);
        }
    }
    // Listen for btn clicks
    private void setListeners(){
        backBtn.setOnClickListener(back2Main);
    }
    private Button.OnClickListener back2Main = new Button.OnClickListener(){
        public void onClick(View v){
            // Close this Activity
            Report.this.finish();
        }
    };

}
