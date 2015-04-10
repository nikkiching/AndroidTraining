package com.ching_chang.piggydiary;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

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
        mDbAdapter.dbOpen();
        Intent intent = getIntent();
        String action = intent.getAction();
        if (SHOW_DAY.equals(action)){
            mItems = mDbAdapter.fetchDay();
        }
        if (SHOW_WEEK.equals(action)){
            mItems = mDbAdapter.fetchWeek();
        }
        if (SHOW_MONTH.equals(action)){
            mItems = mDbAdapter.fetchMonth();
        }
        if (SHOW_ALL.equals(action)){
            mItems = mDbAdapter.fetchAll();
        }
        mItemAdapter = new ItemAdapter(this, R.layout.item, mItems);
        mItemList.setAdapter(mItemAdapter);
        setListener();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // If edit or delete return ok
        if (resultCode == Activity.RESULT_OK) {
            // Get Item
            Item item = (Item) data.getExtras().getSerializable("KEY_ITEM");

            switch (requestCode){
                case EDIT:
                    MainActivity.message(this,"EDIT Complete");
                    int position = data.getIntExtra(KEY_POS, -1);

                    if (position != -1) {
                        // reset the item
                        mItems.set(position, item);
                        mItemAdapter.notifyDataSetChanged();
                    }
                    break;
                case DELETE:
                    MainActivity.message(this, "Delete Complete");

                    break;
                default:
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
            Item item = mItemAdapter.getItem(position);
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

            Item item = mItemAdapter.getItem(position);
            mDbAdapter.delete(item.getID());
            mItemAdapter.remove(item);
            mItemAdapter.notifyDataSetChanged();
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
}
