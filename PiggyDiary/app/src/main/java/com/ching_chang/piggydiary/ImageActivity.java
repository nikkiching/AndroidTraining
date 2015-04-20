package com.ching_chang.piggydiary;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * Created by Ching_Chang on 2015/4/13.
 */
public class ImageActivity extends Activity {
    public static final String TAG = "ImageActivity";
    private ImageView mImageView;
    private Button mOK, mNew;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    public static final String ACT_SHOW_IMAGE = "com.ching_chang.piggydiary.ACT_SHOW_IMAGE";
    public static final String ACT_TAKE_IMAGE = "com.ching_chang.piggydiary.TAKE_IMAGE";
    public static final String KEY_IMAGE = "IMAGE";
    public static final String KEY_IMAGE_PATH = "IMAGE_PATH";

    private String mPicPath = null;
    boolean mIsTakeNewPicture = false;
    private static final String IMAGE_PRE = "P_";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        findView();
        setListener();
        Intent intent = getIntent();
        switch (intent.getAction()){
            case ACT_SHOW_IMAGE:
                mPicPath =  intent.getExtras().getString(KEY_IMAGE_PATH);
                if (! TextUtils.isEmpty(mPicPath)) {
                    Bitmap bmp = Image.getScalePicAsView(mPicPath, mImageView.getWidth(), mImageView.getHeight());
                    if (bmp != null) {
                        Matrix matrix = Image.getRotateMatrix(mPicPath);
                        Bitmap rotateBmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);
                        mImageView.setImageBitmap(rotateBmp);
                        bmp.recycle();
                    }else {
                        mImageView.setImageBitmap(null);
                        Toast.makeText(this, R.string.image_not_found, Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case ACT_TAKE_IMAGE:
                dispatchTakePictureIntent();
                break;
            default:
                break;
        }
    }
    private void setListener(){
        mOK.setOnClickListener(mOkListen);
        mNew.setOnClickListener(mNewListen);
    }

    private Button.OnClickListener mOkListen = new Button.OnClickListener(){
        @Override
        public void onClick(View v) {
            if (mIsTakeNewPicture) {
                // Pass mImageData to UpdateActivity
                Intent result = getIntent();
                result.putExtra(KEY_IMAGE_PATH, mPicPath);
                Log.d(TAG, "ok return to UpdateAct");
                setResult(Activity.RESULT_OK, result);
            }else{
                setResult(Activity.RESULT_CANCELED, getIntent());
            }
            Log.d(TAG, "ok finish activity");
            finish();
        }
    };

    private Button.OnClickListener mNewListen = new Button.OnClickListener(){
        @Override
        public void onClick(View v) {
            dispatchTakePictureIntent();
        }
    };

    private void findView(){
        mImageView = (ImageView) findViewById(R.id.image_view);
        mOK = (Button) findViewById(R.id.image_ok);
        mNew = (Button) findViewById(R.id.image_new);
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Log.d(TAG, "take pic");
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File image = null;
            try {
                image = createImageFile();
            } catch (IOException ex) {
                Log.e(TAG, "Can not create Image File");
            }
            if (image != null) {
                Uri imageUri =  Uri.fromFile(image);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Matrix matrix = Image.getRotateMatrix(mPicPath);
            Bitmap imageBmp = Image.getScalePicAsView(mPicPath, mImageView.getWidth(), mImageView.getHeight());

            Bitmap rotateBmp = Bitmap.createBitmap(imageBmp, 0, 0, imageBmp.getWidth(), imageBmp.getHeight(), matrix, true);
            imageBmp.recycle();
            mImageView.setImageBitmap(rotateBmp);
            mIsTakeNewPicture = true;
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = IMAGE_PRE + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        mPicPath = image.getAbsolutePath();
//        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        return image;
    }
}
