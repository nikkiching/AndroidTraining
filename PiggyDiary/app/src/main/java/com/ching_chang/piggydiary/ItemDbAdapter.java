package com.ching_chang.piggydiary;

import android.app.backup.BackupAgentHelper;
import android.app.backup.BackupDataInput;
import android.app.backup.BackupDataOutput;
import android.app.backup.FileBackupHelper;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

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
    private final static String KEY_IMAGE_PATH = "Path";
    protected final static String DB_NAME = "Item";
    private final static String DB_TABLE_RECORD = "Record";
    private final static int DB_VERSION = 3;
    private static final Object sDataLock = new Object();
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
                    "Image TEXT, Path TEXT); ";

    private static final String [] mCol = { KEY_ID, KEY_DATE, KEY_MONEY, KEY_LABEL,
            KEY_SUBLABEL, KEY_NOTE, KEY_IMAGE, KEY_IMAGE_PATH};
    private static final String DB_COUNT = "SELECT COUNT(*) FROM ";

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
        ContentValues record = putRecord(item);
        // 1: table name, 2: default value for columns without value, 3: content value
        long id = mDb.insert(DB_TABLE_RECORD, null, record);
        item.setId(id);
        return item;
    }

    // Method: Update
    public boolean updateItem(Item item) {
        ContentValues record = putRecord(item);
        String where = KEY_ID + "=" + item.getID();
        return mDb.update(DB_TABLE_RECORD, record, where, null) > 0 ;
    }
    private ContentValues putRecord(Item item){
        ContentValues record = new ContentValues();
        record.put(KEY_MONEY, item.getMoney());
        record.put(KEY_DATE, item.getDate());
        record.put(KEY_NOTE, item.getNote());
        record.put(KEY_LABEL, item.getCategory());
        record.put(KEY_SUBLABEL, item.getSubCategory());
        record.put(KEY_IMAGE, item.getImage());
        record.put(KEY_IMAGE_PATH, item.getImagePath());
        return record;
    }
    // Method: Delete
    public boolean delete(long id){
        String where = KEY_ID + "= ?";
        return mDb.delete(DB_TABLE_RECORD, where, new String[] {Long.toString(id)}) > 0;
    }

    // Method: Fetch
    public List<Item> fetchAll(){
        List<Item> result = new ArrayList<>();
        Cursor cursor = mDb.query(DB_TABLE_RECORD, mCol, null, null, null, null, null);
        while (cursor.moveToNext()){
            result.add(getRecord(cursor));
        }
        cursor.close();
        return result;
    }
    public List<Item> fetchAll(Long t1, Long t2){
        List<Item> result = new ArrayList<>();
        Cursor cursor = mDb.query(DB_TABLE_RECORD, mCol, KEY_DATE + " BETWEEN ? AND ?",
                new String[] {Long.toString(t1), Long.toString(t2)},
                null, null, KEY_DATE);
        while (cursor.moveToNext()){
            result.add(getRecord(cursor));
        }
        cursor.close();
        return result;
    }
    public List<Item> fetchDay(){
        Calendar c = Calendar.getInstance();
        int day = c.get(Calendar.DAY_OF_MONTH);
        int month = c.get(Calendar.MONTH);
        int year = c.get(Calendar.YEAR);
        Calendar c1 = Calendar.getInstance();
        c1.set(year, month, day,0,0,0);
        Calendar c2 = Calendar.getInstance();
        c2.set(year, month, day,23,59,59);
        return fetchAll(c1.getTimeInMillis(),c2.getTimeInMillis());
    }
    public List<Item> fetchWeek(){
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
        return fetchAll(c1.getTimeInMillis(),c2.getTimeInMillis());
    }
    public List<Item> fetchMonth(){
        Calendar c = Calendar.getInstance();
        int dayMax = c.getActualMaximum(Calendar.DAY_OF_MONTH);
        int dayMin = c.getActualMinimum(Calendar.DAY_OF_MONTH);
        int month = c.get(Calendar.MONTH);
        int year = c.get(Calendar.YEAR);
        Calendar c1 = Calendar.getInstance();
        c1.set(year, month, dayMin,0,0,0);
        Calendar c2 = Calendar.getInstance();
        c2.set(year, month, dayMax,23,59,59);
        return fetchAll(c1.getTimeInMillis(),c2.getTimeInMillis());
    }
    public Item getRecord(Cursor cursor) throws SQLException {
        return new Item(cursor.getLong(0), cursor.getLong(1), cursor.getInt(2), cursor.getInt(3),
                cursor.getInt(4), cursor.getString(5), cursor.getString(6), cursor.getString(7));
    }

    public void createCSV(File outFile) throws IOException{
        OutputStream outputStream = new FileOutputStream(outFile);

//        FileWriter outF = new FileWriter(outFile);
        BufferedWriter buffer = null;
                Cursor cursor = null;
        try {
            buffer = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
            String [] col = { KEY_DATE, KEY_MONEY, KEY_LABEL,KEY_NOTE };
            buffer.write(array2CSV(col));
            buffer.newLine();
            Log.d(TAG, "Write head.");

            cursor = mDb.query(DB_TABLE_RECORD, col, null, null, null, null, KEY_DATE);
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
            String[] labelPayment = mContext.getResources().getStringArray(R.array.labelPayment);
            String[] labelIncome = mContext.getResources().getStringArray(R.array.labelIncome);
            int payment = 0, income = 0, total;
            while (cursor.moveToNext()){
                int category = cursor.getInt(2);
                String label;
                if ( category < labelPayment.length){
                    label = labelPayment[category];
                    payment += cursor.getInt(1);
                } else {
                    label = labelIncome[category-labelPayment.length];
                    income += cursor.getInt(1);
                }
                String[] data = new String[] {
                        dateFormat.format(new Date(cursor.getLong(0))),
                        Integer.toString(cursor.getInt(1)),
                        label,
                        cursor.getString(3) };
                if (data.length != 0) {
                    buffer.write(array2CSV(data));
                    buffer.newLine();
                }
            }
            buffer.newLine();
            total = income - payment;
            buffer.write(mContext.getResources().getString(R.string.pay_title) + ", " + payment + ", "
                    + mContext.getResources().getString(R.string.income_title) + ", " + income + ", "
                    + mContext.getResources().getString(R.string.total_title) + ", " + total);
            buffer.newLine();
        } finally {
            if (buffer != null){
                buffer.flush();
                buffer.close();
            }
            if (cursor != null) cursor.close();
        }
    }

    public String array2CSV(Object[] array){
        if (array == null || array.length == 0) {
            return "null";
        }
        StringBuilder sb = new StringBuilder(array.length * 2);
        sb.append(array[0]);
        for (int i = 1; i < array.length; i++) {
            sb.append(", ");
            sb.append(array[i]);
        }
        return sb.toString();
    }
    public int getMoneySum(int label){
        Cursor cursor = null;
        try {
            cursor = mDb.rawQuery("SELECT SUM(" + KEY_MONEY + ") FROM " + DB_TABLE_RECORD
                            + "WHERE " + KEY_LABEL + "=?", new String[label]);
            if(cursor.moveToNext()) {
                return cursor.getInt(0);
            }
            return 0;
        }
        finally {
            if (cursor!=null) cursor.close();
        }
    }

    public int getMoneySum(long t1, long t2){
        Cursor cursor = null;
        try {
            cursor = mDb.rawQuery("SELECT SUM(" + KEY_MONEY + ") FROM " + DB_TABLE_RECORD
                            + "WHERE " + KEY_DATE + "BETWEEN ? AND ?" ,
                    new String[] {Long.toString(t1), Long.toString(t2)});
            if(cursor.moveToNext()) {
                return cursor.getInt(0);
            }
            return 0;
        }
        finally {
            if (cursor!=null) cursor.close();
        }
    }

    public int getCount(){
        int result = 0;
        Cursor cursor = mDb.rawQuery(DB_COUNT + DB_TABLE_RECORD, null);
        if (cursor.moveToNext()) {
            result = cursor.getInt(0);
        }
        cursor.close();
        return result;
    }

    public class BackupAgent extends BackupAgentHelper {

        @Override
        public void onCreate() {
            super.onCreate();
            FileBackupHelper hosts = new FileBackupHelper(this, DB_NAME);
            addHelper("db", hosts);
        }

        @Override
        public File getFilesDir(){
            File path = getDatabasePath(DB_NAME);
            return path.getParentFile();
        }

        @Override
        public void onBackup(ParcelFileDescriptor oldState, BackupDataOutput data, ParcelFileDescriptor newState) throws IOException {
            synchronized (sDataLock){
                Log.d(TAG, "Start backup");
                super.onBackup(oldState, data, newState);
            }
        }

        @Override
        public void onRestore(BackupDataInput data, int appVersionCode, ParcelFileDescriptor newState) throws IOException {
            synchronized (sDataLock) {
                Log.d(TAG, "Start restore");
                super.onRestore(data, appVersionCode, newState);
            }
        }
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
