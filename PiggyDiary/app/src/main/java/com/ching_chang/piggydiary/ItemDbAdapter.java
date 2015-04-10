package com.ching_chang.piggydiary;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Ching_Chang on 2015/4/10.
 */
public class ItemDbAdapter {
    private static final String TAG = "ItemDbAdapter";
    private SQLiteDatabase mDb;
    private final static String KEY_ID = "_id";
    private final static String KEY_DATE = "Date";
    private final static String KEY_MONEY = "Money";
    private final static String KEY_NOTE = "Note";
    private final static String KEY_LABEL = "Label";
    private final static String KEY_SUBLABEL = "SubLabel";
    private final static String KEY_IMAGE = "Image";
    private final static String DB_NAME = "Item";
    private final static String DB_TABLE_RECORD = "Record";
    private final static int DB_VERSION = 2;

    public ItemDbHelper mDbHelper;

    private final Context mContext;
    // SQL Command: Create DB
    private static final String DB_CREATE =
            "CREATE TABLE " + DB_TABLE_RECORD + " (" +
                    "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "Date INTEGER NOT NULL," +
                    "Money TEXT NOT NULL," +
                    "Label INTEGER NOT NULL," +
                    "SubLabel INTEGER NOT NULL," +
                    "Note TEXT," +
                    "Image TEXT); ";

    private static final String DB_Count = "SELECT COUNT(*) FROM ";

    public ItemDbAdapter(Context context){
        this.mContext = context;
    }

    private static class ItemDbHelper extends SQLiteOpenHelper {
        ItemDbHelper(Context context){
            super(context, DB_NAME, null, DB_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db){
            db.execSQL(DB_CREATE);
        }
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("Drop TABLE IF EXISTS " + DB_TABLE_RECORD );
            onCreate(db);
        }
    }

    // Method: Insert
    public Item insertItem(Item item) {
        ContentValues record = new ContentValues();
        record.put(KEY_MONEY, item.getMoney());
        record.put(KEY_DATE, item.getDate());
        record.put(KEY_NOTE, item.getNote());
        record.put(KEY_LABEL, item.getCategory());
        record.put(KEY_SUBLABEL, item.getSubCategory());
        record.put(KEY_IMAGE, item.getImage());

        // 1: table name, 2: default value for columns without value, 3: content value
        long id = mDb.insert(DB_TABLE_RECORD, null, record);
        item.setId(id);
        return item;
    }

    // Method: Update
    public boolean updateItem(Item item) {
        ContentValues record = new ContentValues();
        record.put(KEY_MONEY, item.getMoney());
        record.put(KEY_DATE, item.getDate());
        record.put(KEY_NOTE, item.getNote());
        record.put(KEY_LABEL, item.getCategory());
        record.put(KEY_SUBLABEL, item.getSubCategory());
        record.put(KEY_IMAGE, item.getImage());
        String where = KEY_ID + "=" + item.getID();
        return mDb.update(DB_TABLE_RECORD, record, where, null) > 0 ;
    }

    // Method: Delete
    public boolean delete(long id){
        String where = KEY_ID + "=" + id;
        return mDb.delete(DB_TABLE_RECORD, where, null) > 0;
    }

    // Method: Fetch
    public List<Item> fetchAll(){
        List<Item> result = new ArrayList<>();
        String [] col = { KEY_ID, KEY_DATE, KEY_MONEY, KEY_LABEL, KEY_SUBLABEL, KEY_NOTE, KEY_IMAGE};
        Cursor cursor = mDb.query(DB_TABLE_RECORD, col, null, null, null, null, null);
        while (cursor.moveToNext()){
            result.add(getRecord(cursor));
        }
        cursor.close();
        return result;
    }
    public List<Item> fetchDay(){
        List<Item> result = new ArrayList<>();
        String [] col = { KEY_ID, KEY_DATE, KEY_MONEY, KEY_LABEL, KEY_SUBLABEL, KEY_NOTE, KEY_IMAGE};
        Calendar c = Calendar.getInstance();
        int day = c.get(Calendar.DAY_OF_MONTH);
        int month = c.get(Calendar.MONTH);
        int year = c.get(Calendar.YEAR);
        Calendar c1 = Calendar.getInstance();
        c1.set(year, month, day,0,0,0);
        Calendar c2 = Calendar.getInstance();
        c2.set(year, month, day,23,59,59);
        Cursor cursor = mDb.query(DB_TABLE_RECORD, col, KEY_DATE + " BETWEEN " + c1.getTimeInMillis() + " AND " + c2.getTimeInMillis(), null, null, null, KEY_DATE);
        while (cursor.moveToNext()){
            result.add(getRecord(cursor));
        }
        cursor.close();
        return result;
    }
    public List<Item> fetchWeek(){
        List<Item> result = new ArrayList<>();
        String [] col = { KEY_ID, KEY_DATE, KEY_MONEY, KEY_LABEL, KEY_SUBLABEL, KEY_NOTE, KEY_IMAGE};
        Calendar c = Calendar.getInstance();
        int day = c.get(Calendar.DAY_OF_MONTH);
        int month = c.get(Calendar.MONTH);
        int year = c.get(Calendar.YEAR);
        Calendar c1 = Calendar.getInstance();
        c1.set(year, month, day,0,0,0);
        c1.set(Calendar.DAY_OF_WEEK, c.getFirstDayOfWeek());
        Calendar c2 = Calendar.getInstance();
        c2.set(year, month, day,23,59,59);
        c2.set(Calendar.DAY_OF_WEEK, c.getFirstDayOfWeek() + 6);
        Cursor cursor = mDb.query(DB_TABLE_RECORD, col, KEY_DATE + " BETWEEN " + c1.getTimeInMillis() + " AND " + c2.getTimeInMillis(), null, null, null, KEY_DATE);
        while (cursor.moveToNext()){
            result.add(getRecord(cursor));
        }
        cursor.close();
        return result;
    }
    public List<Item> fetchMonth(){
        List<Item> result = new ArrayList<>();
        String [] col = { KEY_ID, KEY_DATE, KEY_MONEY, KEY_LABEL, KEY_SUBLABEL, KEY_NOTE, KEY_IMAGE};
        Calendar c = Calendar.getInstance();
        int dayMax = c.getActualMaximum(Calendar.DAY_OF_MONTH);
        int dayMin = c.getActualMinimum(Calendar.DAY_OF_MONTH);
        int month = c.get(Calendar.MONTH);
        int year = c.get(Calendar.YEAR);
        Calendar c1 = Calendar.getInstance();
        c1.set(year, month, dayMin,0,0,0);
        Calendar c2 = Calendar.getInstance();
        c2.set(year, month, dayMax,23,59,59);
        Cursor cursor = mDb.query(DB_TABLE_RECORD, col, KEY_DATE + " BETWEEN " + c1.getTimeInMillis() + " AND " + c2.getTimeInMillis(), null, null, null, KEY_DATE);
        while (cursor.moveToNext()){
            result.add(getRecord(cursor));
        }
        cursor.close();
        return result;
    }
    public Item getRecord(Cursor cursor) throws SQLException {
        Item result = new Item(cursor.getLong(0), cursor.getLong(1), cursor.getDouble(2), cursor.getInt(3),
                cursor.getInt(4), cursor.getString(5), cursor.getString(6));
        return result;
    }


    public int getCount(){
        int result = 0;
        Cursor cursor = mDb.rawQuery(DB_Count + DB_TABLE_RECORD, null);
        if (cursor.moveToNext()) {
            result = cursor.getInt(0);
        }
        cursor.close();
        return result;
    }
    /*
         * Open the database. If it cannot be opened, try to create a new instance
         * of the database. If it cannot be created, throw an exception to signal
         * the failure
         */
    public ItemDbAdapter dbOpen() throws SQLException {
        if (mDb == null || !mDb.isOpen()) {
            mDbHelper = new ItemDbHelper(mContext);
            mDb = mDbHelper.getWritableDatabase();
        }
        return this;
    }
    public void dbClose(){
        mDb.close();
    }
}
