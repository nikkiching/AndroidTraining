package com.ching_chang.piggydiary;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class UpdateActivity extends ActionBarActivity {
    public static final String TAG = "Update";
    public static final String EDIT_PAYMENT = "com.ching_chang.piggydiary.EDIT_ITEM_PAYMENT";
    public static final String EDIT_INCOME = "com.ching_chang.piggydiary.EDIT_ITEM_INCOME";
    public static final String ADD_PAYMENT = "com.ching_chang.piggydiary.ADD_ITEM_PAYMENT";
    public static final String ADD_INCOME = "com.ching_chang.piggydiary.ADD_ITEM_INCOME";
    private EditText mMoney, mNote;
    private Button mOk, mCancel, mDate;
    private Spinner mLabel, mSubLabel;
    private DatePickerDialog mPickDate;
    private ImageButton mImageView;
    public static SimpleDateFormat mDateFormat = new SimpleDateFormat("yyyy/MM/dd");
    private long mTimeInput;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private ItemDbAdapter mDbAdapter = new ItemDbAdapter(this);
    private Item mItem = new Item();
    boolean mIsTakeNewPicture = false;
    private final int THUMBNAIL_SIZE = 200;
    private byte[] mImageData = null;
//    enum LabelType {
//        FOOD, UTILITY, TRANSPORTATION, RECREATION, STUDY, MEDICATION, OTHER_PAY, WORK,
//        OTHER_INCOME;
//    }
    String mAction;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);
        findViews();
        Intent intent = getIntent();
        mAction = intent.getAction();
        ArrayAdapter<CharSequence> labelList;
        switch (mAction){
            case ADD_PAYMENT:
                labelList = ArrayAdapter.createFromResource(this, R.array.LabelList_payment, android.R.layout.simple_spinner_item);
                mLabel.setAdapter(labelList);
                break;
            case ADD_INCOME:
                labelList = ArrayAdapter.createFromResource(this, R.array.LabelList_income, android.R.layout.simple_spinner_item);
                mLabel.setAdapter(labelList);

                break;
            case EDIT_PAYMENT:
                MainActivity.message(this, EDIT_PAYMENT);
                mItem = (Item) intent.getExtras().getSerializable(ItemListActivity.KEY_ITEM);
                mMoney.setText(Double.toString(mItem.getMoney()));
                mTimeInput = mItem.getDate();
                mDate.setText(mDateFormat.format(new Date(mTimeInput)));
                mNote.setText(mItem.getNote());
                labelList = ArrayAdapter.createFromResource(this, R.array.LabelList_payment, android.R.layout.simple_spinner_item);
                mLabel.setAdapter(labelList);
                mLabel.setSelection(mItem.getCategory());

                break;
            case EDIT_INCOME:
                mItem = (Item) intent.getExtras().getSerializable(ItemListActivity.KEY_ITEM);
                mMoney.setText(Double.toString(mItem.getMoney()));
                mTimeInput = mItem.getDate();
                mDate.setText(mDateFormat.format(new Date(mTimeInput)));
                mNote.setText(mItem.getNote());
                labelList = ArrayAdapter.createFromResource(this, R.array.LabelList_income, android.R.layout.simple_spinner_item);
                mLabel.setAdapter(labelList);
                mLabel.setSelection(mItem.getCategory()-7);
                break;
            default:
                break;
        }
        String imageString = mItem.getImage();
        if (imageString != null && !TextUtils.isEmpty(imageString)) {
            byte imageData[] = Base64.decode(imageString, Base64.DEFAULT);
            Bitmap bmp = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
            mImageView.setImageBitmap(bmp);
        }
        mDbAdapter.dbOpen();
        setDateField();
        setListeners();
    }

    private void findViews(){
        mImageView = (ImageButton) findViewById(R.id.img_input);
        mOk = (Button) findViewById(R.id.ok_btn);
        mCancel = (Button) findViewById(R.id.cancel_btn);
        mMoney = (EditText) findViewById(R.id.input_money);
        mDate = (Button) findViewById(R.id.input_date);
        mNote = (EditText) findViewById(R.id.input_note);

        mLabel = (Spinner) findViewById(R.id.input_label);
        mSubLabel = (Spinner) findViewById(R.id.input_sub_label);

    }
    // Listen for btn clicks
    private void setListeners() {
        mDate.setOnClickListener(mPickDateListen);
        mLabel.setOnItemSelectedListener(mLabelListen);
        mImageView.setOnClickListener(mImageListen);
        mOk.setOnClickListener(mOnSubmit);
        mCancel.setOnClickListener(mBack2Main);
    }

    private Button.OnClickListener mOnSubmit = new Button.OnClickListener(){
        @Override
        public void onClick(View v) {
            saveField();
            if (mAction.equals(EDIT_PAYMENT)) {
                mItem.setCategory(mLabel.getSelectedItemPosition());
                mDbAdapter.updateItem(mItem);
                Intent result = getIntent();
                result.putExtra("KEY_ITEM", mItem);
                setResult(Activity.RESULT_OK, result);
            } else if (mAction.equals(ADD_PAYMENT)) {
                mItem.setCategory(mLabel.getSelectedItemPosition());
                mDbAdapter.insertItem(mItem);
            } else if (mAction.equals(EDIT_INCOME)){
                // Because Pay label already have 7 labels
                mItem.setCategory(mLabel.getSelectedItemPosition() + 7);
                mDbAdapter.updateItem(mItem);
                Intent result = getIntent();
                result.putExtra("KEY_ITEM", mItem);
                setResult(Activity.RESULT_OK, result);
            } else if (mAction.equals(ADD_INCOME)) {
                // Because Pay label already have 7 labels
                mItem.setCategory(mLabel.getSelectedItemPosition() + 7);
                mDbAdapter.insertItem(mItem);
            }

            finish();
        }
    };
    private void saveField(){
        mItem.setNote(mNote.getText().toString());
        if (TextUtils.isEmpty(mMoney.getText().toString())){
            mItem.setMoney(0);
        }else{
            mItem.setMoney(Double.parseDouble(mMoney.getText().toString()));
        }
        mItem.setDate(mTimeInput);
        mItem.setSubCategory(mSubLabel.getSelectedItemPosition());
        if (mIsTakeNewPicture) {
            mItem.setImage(Base64.encodeToString(mImageData, Base64.DEFAULT));
        }
    }
    private Button.OnClickListener mPickDateListen = new Button.OnClickListener(){
        public void onClick(View v) {
            mPickDate.show();
        }
    };

    private Spinner.OnItemSelectedListener mLabelListen = new Spinner.OnItemSelectedListener() {
        public void onItemSelected(AdapterView parent, View v, int position, long id){
            // String label = parent.getSelectedItem().toString();
            ArrayAdapter<CharSequence> sublabelList;
            switch (mAction) {
                case ADD_PAYMENT:
                case EDIT_PAYMENT:
                    sublabelList = subLabelOptionPay(position);
                    mSubLabel.setAdapter(sublabelList);
                    if (position == mItem.getCategory()) {
                        mSubLabel.setSelection(mItem.getSubCategory());
                    }
                    break;
                case ADD_INCOME:
                case EDIT_INCOME:
                    sublabelList = subLabelOptionIncome(position);
                    mSubLabel.setAdapter(sublabelList);
                    if (position == mItem.getCategory() -7) {
                        mSubLabel.setSelection(mItem.getSubCategory());
                    }
                    break;
                default:
                    break;
            }
        }
        public void onNothingSelected(AdapterView parent){
        }
    };
    private ArrayAdapter<CharSequence> subLabelOptionPay(int position){
        ArrayAdapter<CharSequence> sublabelList;
        switch (position){
            case 0:
                sublabelList = ArrayAdapter.createFromResource(UpdateActivity.this, R.array.SubLabel_food, android.R.layout.simple_spinner_item);
                break;
            case 1:
                sublabelList = ArrayAdapter.createFromResource(UpdateActivity.this, R.array.SubLabel_utility, android.R.layout.simple_spinner_item);
                break;
            case 2:
                sublabelList = ArrayAdapter.createFromResource(UpdateActivity.this, R.array.SubLabel_transportation, android.R.layout.simple_spinner_item);
                break;
            case 3:
                sublabelList = ArrayAdapter.createFromResource(UpdateActivity.this, R.array.SubLabel_recreation, android.R.layout.simple_spinner_item);
                break;
            case 4:
                sublabelList = ArrayAdapter.createFromResource(UpdateActivity.this, R.array.SubLabel_study, android.R.layout.simple_spinner_item);
                break;
            case 5:
                sublabelList = ArrayAdapter.createFromResource(UpdateActivity.this, R.array.SubLabel_medication, android.R.layout.simple_spinner_item);
                break;
            case 6:
                sublabelList = ArrayAdapter.createFromResource(UpdateActivity.this, R.array.SubLabel_other, android.R.layout.simple_spinner_item);
                break;
            default:
                sublabelList = ArrayAdapter.createFromResource(UpdateActivity.this, R.array.SubLabel_food, android.R.layout.simple_spinner_item);
                break;
        }
        return sublabelList;
    }

    private ArrayAdapter<CharSequence> subLabelOptionIncome(int position){
        ArrayAdapter<CharSequence> sublabelList;
        switch (position){
            case 0:
                sublabelList = ArrayAdapter.createFromResource(UpdateActivity.this, R.array.SubLabel_work, android.R.layout.simple_spinner_item);
                break;
            case 1:
                sublabelList = ArrayAdapter.createFromResource(UpdateActivity.this, R.array.SubLabel_otherIncome, android.R.layout.simple_spinner_item);
                break;

            default:
                sublabelList = ArrayAdapter.createFromResource(UpdateActivity.this, R.array.SubLabel_work, android.R.layout.simple_spinner_item);
                break;
        }
        return sublabelList;
    }

    private ImageButton.OnClickListener mImageListen = new ImageButton.OnClickListener(){
        @Override
        public void onClick(View v) {
            dispatchTakePictureIntent();
        }
    };

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            mImageView.setImageBitmap(Bitmap.createScaledBitmap(imageBitmap, THUMBNAIL_SIZE, THUMBNAIL_SIZE, false));
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            mImageData = stream.toByteArray();
            imageBitmap.recycle();
            mIsTakeNewPicture = true;
        }
    }

    private Button.OnClickListener mBack2Main = new Button.OnClickListener(){
        public void onClick(View v) {
            finish();
        }
    };

    private void setDateField() {
        Calendar newCalendar = Calendar.getInstance();
        mPickDate = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                mDate.setText(mDateFormat.format(newDate.getTime()));
                mTimeInput = newDate.getTimeInMillis();
            }
        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DATE));
        mDate.setText(mDateFormat.format(newCalendar.getTime()));
        mTimeInput = newCalendar.getTimeInMillis();
        mPickDate.setTitle(R.string.date_title);
        mPickDate.setCancelable(false);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_update, menu);
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
