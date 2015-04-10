package com.ching_chang.piggydiary;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.Button;
import android.widget.Toast;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
         public PlaceholderFragment() {

        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }
    }
    public void onClickMain(View v){
        int id = v.getId();
        Intent intent = new Intent();
        switch (id) {
            case R.id.payment_btn:
                intent.setAction(UpdateActivity.ADD_PAYMENT);
                startActivity(intent);
                break;
            case R.id.income_btn:
                intent.setAction(UpdateActivity.ADD_INCOME);
                startActivity(intent);
                break;
            case R.id.report_btn:

                break;
            case R.id.daily_view:
                intent.setAction(ItemListActivity.SHOW_DAY);
                startActivity(intent);
                break;
            case R.id.weekly_view:
                intent.setAction(ItemListActivity.SHOW_WEEK);
                startActivity(intent);
                break;
            case R.id.monthly_view:
                intent.setAction(ItemListActivity.SHOW_MONTH);
                startActivity(intent);
                break;
            case R.id.all_view:
                intent.setAction(ItemListActivity.SHOW_ALL);
                startActivity(intent);
                break;
        }

    }
    public static void message(Context context, String msg){
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }
}
