package com.ching_chang.piggydiary;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.util.Log;
import android.widget.ImageView;

import java.io.IOException;

/**
 * Created by Ching_Chang on 2015/4/14.
 */
public class ImageUtils {
    public static final String TAG = "Image class";
    public static Bitmap getScalePicAsView(String absPath, int targetW, int targetH){
        // Get the dimensions of the bitmap
        BitmapFactory.Options bmpOptions = new BitmapFactory.Options();
        BitmapFactory.decodeFile(absPath, bmpOptions);
        int photoW = bmpOptions.outWidth;
        int photoH = bmpOptions.outHeight;

        // Decode the image file into a Bitmap sized to fill the View
        try {
            bmpOptions.inSampleSize = Math.min(photoW/targetW, photoH/targetH);
            Log.d(TAG, "targetH " + String.valueOf(targetH));
            Log.d(TAG, "targetW " + String.valueOf(targetW));
        } catch (ArithmeticException ex) {
            Log.d(TAG, "targetH " + String.valueOf(targetH));
            Log.d(TAG, "targetW " + String.valueOf(targetW));
        }
        return BitmapFactory.decodeFile(absPath, bmpOptions);
    }

    public static Matrix getRotateMatrix(String absPath){
        Matrix matrix = new Matrix();
        try {
            ExifInterface exif = new ExifInterface(absPath);
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);

            switch (orientation){
                case ExifInterface.ORIENTATION_ROTATE_90:
                    matrix.postRotate(90);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    matrix.postRotate(180);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    matrix.postRotate(270);
                    break;
                default:
                    break;
            }
        }
        catch (IOException ex){
            Log.e(TAG, "Can't load file: " + absPath);
        }
        return matrix;
    }
}
