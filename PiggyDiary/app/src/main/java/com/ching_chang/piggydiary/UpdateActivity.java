package com.ching_chang.piggydiary;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ThumbnailUtils;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class UpdateActivity extends ActionBarActivity {
    public static final String TAG = "Update";
    public static final String KEY_ITEM = "ITEM";
    public static final String EDIT_PAYMENT = "com.ching_chang.piggydiary.EDIT_ITEM_PAYMENT";
    public static final String EDIT_INCOME = "com.ching_chang.piggydiary.EDIT_ITEM_INCOME";
    public static final String ADD_PAYMENT = "com.ching_chang.piggydiary.ADD_ITEM_PAYMENT";
    public static final String ADD_INCOME = "com.ching_chang.piggydiary.ADD_ITEM_INCOME";
    public static final String UPDATE_PIC = "com.ching_chang.piggydiary.UPDATE_PIC";
    private EditText mMoney, mNote;
    private Button mOk, mCancel, mDate;
    private Spinner mLabel, mSubLabel;
    private DatePickerDialog mPickDate;
    private ImageView mImageView;
    public static SimpleDateFormat mDateFormat = new SimpleDateFormat("yyyy/MM/dd");
    private long mTimeInput;
    public static final int REQUEST_IMAGE_CAPTURE = 1, REQUEST_IMAGE_SHOW =2;
    private Item mItem = new Item();
    boolean mIsTakeNewPicture = false;
    private static final int THUMBNAIL_SIZE_X = 200, THUMBNAIL_SIZE_Y = 250;
    private byte[] mImageData = null;
    private String mImagePath;
    private String[] mLabelIncomeValue;
    private int[] mSubLabelPayResource = {R.array.subLabelFood, R.array.subLabelUtility,
                R.array.subLabelTransportation, R.array.subLabelRecreation, R.array.subLabelStudy,
                R.array.subLabelMedication, R.array.subLabelOther};
    private int[] mSubLabelIncomeResource = {R.array.subLabelWork, R.array.subLabelOtherIncome};
    String mAction;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);
        findViews();
        mLabelIncomeValue = getResources().getStringArray(R.array.labelIncomeValue);
        setDateField();
        setListeners();
        Intent intent = getIntent();
        mAction = intent.getAction();
        ArrayAdapter<CharSequence> labelList;
        switch (mAction){
            case ADD_PAYMENT:
                labelList = ArrayAdapter.createFromResource(this, R.array.labelPayment, android.R.layout.simple_spinner_item);
                mLabel.setAdapter(labelList);
                break;
            case ADD_INCOME:
                labelList = ArrayAdapter.createFromResource(this, R.array.labelIncome, android.R.layout.simple_spinner_item);
                mLabel.setAdapter(labelList);
                break;
            case EDIT_PAYMENT:
                mItem = (Item) intent.getExtras().getSerializable(ItemListActivity.KEY_ITEM);
                mMoney.setText(Integer.toString(mItem.getMoney()));
                mTimeInput = mItem.getDate();
                mDate.setText(mDateFormat.format(new Date(mTimeInput)));
                mNote.setText(mItem.getNote());
                labelList = ArrayAdapter.createFromResource(this, R.array.labelPayment, android.R.layout.simple_spinner_item);
                mLabel.setAdapter(labelList);
                mLabel.setSelection(mItem.getCategory());
                break;
            case EDIT_INCOME:
                mItem = (Item) intent.getExtras().getSerializable(ItemListActivity.KEY_ITEM);
                mMoney.setText(Integer.toString(mItem.getMoney()));
                mTimeInput = mItem.getDate();
                mDate.setText(mDateFormat.format(new Date(mTimeInput)));
                mNote.setText(mItem.getNote());
                labelList = ArrayAdapter.createFromResource(this, R.array.labelIncome, android.R.layout.simple_spinner_item);
                mLabel.setAdapter(labelList);
                mLabel.setSelection(mItem.getCategory()-Integer.parseInt(mLabelIncomeValue[0]));
                break;
            default:
                break;
        }
        String imageString = mItem.getImage();
        if (!TextUtils.isEmpty(imageString)) {
            mImageData = Base64.decode(imageString, Base64.DEFAULT);
            Bitmap bmp = BitmapFactory.decodeByteArray(mImageData, 0, mImageData.length);
            mImageView.setImageBitmap(bmp);
            mImagePath = mItem.getImagePath();
        }
    }

    private void findViews(){
        // mImageView = (ImageButton) findViewById(R.id.img_input);
        mImageView = (ImageView) findViewById(R.id.img_input);
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
            Intent result = getIntent();
            ItemDbAdapter dbAdapter = new ItemDbAdapter(UpdateActivity.this);
            dbAdapter.dbOpenWrite();
            switch (mAction){
                case EDIT_PAYMENT:
                    mItem.setCategory(mLabel.getSelectedItemPosition());
                    dbAdapter.updateItem(mItem);
                    result.putExtra(ItemListActivity.KEY_ITEM, mItem);
                    setResult(Activity.RESULT_OK, result);
                    break;
                case ADD_PAYMENT:
                    mItem.setCategory(mLabel.getSelectedItemPosition());
                    dbAdapter.insertItem(mItem);
                    break;
                case EDIT_INCOME:
                    mItem.setCategory(Integer.parseInt(mLabelIncomeValue[mLabel.getSelectedItemPosition()]));
                    dbAdapter.updateItem(mItem);
                    result.putExtra(ItemListActivity.KEY_ITEM, mItem);
                    setResult(Activity.RESULT_OK, result);
                    break;
                case ADD_INCOME:
                    mItem.setCategory(Integer.parseInt(mLabelIncomeValue[mLabel.getSelectedItemPosition()]));
                    dbAdapter.insertItem(mItem);
                    break;
            }
            dbAdapter.dbClose();
            finish();
        }
    };
    private void saveField(){
        mItem.setNote(mNote.getText().toString());
        if (TextUtils.isEmpty(mMoney.getText().toString())){
            mItem.setMoney(0);
        }else{
            mItem.setMoney(Integer.parseInt(mMoney.getText().toString()));
        }
        mItem.setDate(mTimeInput);
        mItem.setSubCategory(mSubLabel.getSelectedItemPosition());
        if (mIsTakeNewPicture) {
            mItem.setImage(Base64.encodeToString(mImageData, Base64.DEFAULT));
            mItem.setImagePath(mImagePath);
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
            switch (mAction) {
                case ADD_PAYMENT:
                case EDIT_PAYMENT:
                    mSubLabel.setAdapter(ArrayAdapter.createFromResource(UpdateActivity.this,
                            mSubLabelPayResource[position], android.R.layout.simple_spinner_item));
                    if (position == mItem.getCategory()) {
                        mSubLabel.setSelection(mItem.getSubCategory());
                    }
                    break;
                case ADD_INCOME:
                case EDIT_INCOME:
                    mSubLabel.setAdapter(ArrayAdapter.createFromResource(UpdateActivity.this,
                            mSubLabelIncomeResource[position], android.R.layout.simple_spinner_item));
                    if (position == mItem.getCategory() - Integer.parseInt(mLabelIncomeValue[0])) {
                        mSubLabel.setSelection(mItem.getSubCategory());
                    }
                    break;
            }
        }
        public void onNothingSelected(AdapterView parent){
        }
    };

    private ImageView.OnClickListener mImageListen = new ImageView.OnClickListener(){
//    private ImageButton.OnClickListener mImageListen = new ImageButton.OnClickListener(){
        @Override
        public void onClick(View v) {
            if (mImageData == null ) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(UpdateActivity.this);
                dialog.setTitle(R.string.image)
                        .setMessage(R.string.image_take_new);
                dialog.setPositiveButton(android.R.string.yes,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(UpdateActivity.this, ImageActivity.class);
                                intent.setAction(ImageActivity.ACT_TAKE_IMAGE);
                                startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
                            }
                        });
                dialog.setNegativeButton(android.R.string.no, null);
                dialog.show();
            }
            else {
                Intent intent = new Intent(UpdateActivity.this, ImageActivity.class);
                intent.setAction(ImageActivity.ACT_SHOW_IMAGE);
                intent.putExtra(ImageActivity.KEY_IMAGE_PATH, mImagePath);

                startActivityForResult(intent, REQUEST_IMAGE_SHOW);
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG,"onDestroy...........");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG,"Enter get result");
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
//            Bundle extras = data.getExtras();
//            Bitmap imageBitmap = (Bitmap) extras.get("data");
//            mImageView.setImageBitmap(Bitmap.createScaledBitmap(imageBitmap, THUMBNAIL_SIZE_X, THUMBNAIL_SIZE_Y, false));
//            ByteArrayOutputStream stream = new ByteArrayOutputStream();
//            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
//            mImageData = stream.toByteArray();
//            imageBitmap.recycle();
//            mIsTakeNewPicture = true;
            Bundle extras = data.getExtras();
            mImagePath = extras.getString(ImageActivity.KEY_IMAGE_PATH);
            Matrix matrix = ImageUtils.getRotateMatrix(mImagePath);
            Bitmap imageBmp = BitmapFactory.decodeFile(mImagePath);
            Bitmap rotateBmp = Bitmap.createBitmap(imageBmp, 0, 0, imageBmp.getWidth(), imageBmp.getHeight(), matrix, true);
            Bitmap thumbnail = ThumbnailUtils.extractThumbnail(rotateBmp, THUMBNAIL_SIZE_X, THUMBNAIL_SIZE_Y);
            rotateBmp.recycle();
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            thumbnail.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            mImageData = stream.toByteArray();
            imageBmp.recycle();
            mImageView.setImageBitmap(thumbnail);
            mIsTakeNewPicture = true;
        }
        else if (requestCode == REQUEST_IMAGE_SHOW && resultCode == RESULT_OK) {
            Log.d(TAG, "reset the pic");
            Bundle extras = data.getExtras();
            mImagePath = extras.getString(ImageActivity.KEY_IMAGE_PATH);
            Bitmap imageBmp = BitmapFactory.decodeFile(mImagePath);
            Matrix matrix = ImageUtils.getRotateMatrix(mImagePath);
//            Bitmap imageBmp = Image.getScalePicAsView(imagePath,mImageView);
    //        Bitmap thumbnail = Bitmap.createBitmap(imageBmp, 0, 0, imageBmp.getWidth(), imageBmp.getHeight(), matrix, true);
//            Bitmap thumbnail = Bitmap.createScaledBitmap(imageBmp, mImageView.getWidth(), mImageView.getHeight(), false);
            Bitmap rotateBmp = Bitmap.createBitmap(imageBmp, 0, 0, imageBmp.getWidth(), imageBmp.getHeight(), matrix, true);
            Bitmap thumbnail = ThumbnailUtils.extractThumbnail(rotateBmp, THUMBNAIL_SIZE_X, THUMBNAIL_SIZE_Y);

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            thumbnail.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            mImageData = stream.toByteArray();
            imageBmp.recycle();
            mImageView.setImageBitmap(thumbnail);
            mIsTakeNewPicture = true;
        }else{
            Log.d(TAG,"Result code not ok");
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

}
