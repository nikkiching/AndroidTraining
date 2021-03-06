package com.ching_chang.piggydiary;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.List;


public class ItemListActivity extends ActionBarActivity {
    private ListView mItemList;
    private ItemAdapter mItemAdapter;
    private List<Item> mItems;
    private static final int EDIT = 1, DELETE = 2;
    public static final String KEY_ITEM = "ITEM", KEY_POS = "POSITION";
    public static final String SHOW_DAY = "com.ching_chang.piggydiary.SHOW_DAY";
    public static final String SHOW_MONTH = "com.ching_chang.piggydiary.SHOW_MONTH";
    public static final String SHOW_WEEK = "com.ching_chang.piggydiary.SHOW_WEEK";
    public static final String SHOW_ALL = "com.ching_chang.piggydiary.SHOW_ALL";
    private static final String TAG = "ItemListActivity";
    private ItemDbAdapter mDbAdapter = new ItemDbAdapter(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_list);
        findView();
        mDbAdapter.dbOpenWrite();
        Intent intent = getIntent();
        String action = intent.getAction();
        Calendar c = Calendar.getInstance();
        int day = c.get(Calendar.DAY_OF_MONTH);
        int month = c.get(Calendar.MONTH);
        int year = c.get(Calendar.YEAR);
        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();
        switch (action){
            case SHOW_DAY:
                c1.set(year, month, day,0,0,0);
                c2.set(year, month, day,23,59,59);
                break;
            case SHOW_WEEK:
                c1.set(year, month, day,0,0,0);
                c1.set(Calendar.DAY_OF_WEEK, c.getFirstDayOfWeek());
                c2.set(year, month, day,23,59,59);
                c2.set(Calendar.DAY_OF_WEEK, c.getFirstDayOfWeek() + 6);
                break;
            case SHOW_MONTH:
                int dayMax = c.getActualMaximum(Calendar.DAY_OF_MONTH);
                int dayMin = c.getActualMinimum(Calendar.DAY_OF_MONTH);
                c1.set(year, month, dayMin,0,0,0);
                c2.set(year, month, dayMax,23,59,59);
                break;
        }
        if (! c1.equals(c2)){
            mItems = mDbAdapter.fetchAll(c1.getTimeInMillis(),c2.getTimeInMillis());
            if (mItems.isEmpty()){
                Log.d(TAG, "No list to show.");
            } else {
                mItemAdapter = new ItemAdapter(this, R.layout.item, mItems);
                mItemList.setAdapter(mItemAdapter);
                setListener();
                Log.d(TAG, "Show list from " + c1.getTime().toString() + "to " + c2.getTime().toString());
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // If edit or delete return ok
        if (resultCode == Activity.RESULT_OK) {
            // Get Item
            Item item = (Item) data.getExtras().getSerializable(KEY_ITEM);

            switch (requestCode){
                case EDIT:
                    Toast.makeText(this, R.string.msg_edit, Toast.LENGTH_SHORT).show();
                    int position = data.getIntExtra(KEY_POS, -1);
                    if (position != -1) {
                        // reset the item
                        mItemAdapter.set(position, item);
                    }
                    break;
            }
        }
    }

    private void findView(){
        mItemList = (ListView) findViewById(R.id.item_list);
    }

    private void setListener(){
        mItemList.setOnItemClickListener(mItemListen);
        mItemList.setOnItemLongClickListener(mItemLongListen);
    }

    private AdapterView.OnItemClickListener mItemListen = new AdapterView.OnItemClickListener(){
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id){
            Item item = mItemAdapter.get(position);
            Intent intent;
            if (item.getCategory() > 6) {
                intent = new Intent(UpdateActivity.EDIT_INCOME);
            }else{
                intent = new Intent(UpdateActivity.EDIT_PAYMENT);
            }
            intent.putExtra(KEY_POS, position);
            intent.putExtra(KEY_ITEM,item);
            startActivityForResult(intent, EDIT);
        }
    };
    private AdapterView.OnItemLongClickListener mItemLongListen = new AdapterView.OnItemLongClickListener(){
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(ItemListActivity.this);
            dialog.setTitle(R.string.delete_title);
            dialog.setMessage(R.string.delete_text);
            final int pos = position;
            dialog.setPositiveButton(android.R.string.yes, new
                    DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Item item = mItemAdapter.get(pos);
                            mDbAdapter.delete(item.getID());
                            mItemAdapter.remove(item);
                            mItemAdapter.notifyDataSetChanged();
                        }
                    });
            dialog.setNegativeButton(android.R.string.no, null);
            dialog.show();
            return false;
        }
    };
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_item_list, menu);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDbAdapter.dbClose();
    }
}
