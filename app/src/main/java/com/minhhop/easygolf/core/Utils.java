package com.minhhop.easygolf.core;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import java.io.ByteArrayOutputStream;

/**
 * Created by pdypham on 3/30/18.
 */

public class Utils {

    public static double Standard_width = 1526;
    public static double Standard_height = 2048;

    public static double current_width;
    public static double current_height;

    public static double width;
    public static double height;

    public static int setLayoutHeight(double temp) {
        double var;
        var = height * temp;
        return (int) var;
    }

    public static int setLayoutWidth(double temp) {
        double var;
        var = width * temp;
        return (int) var;
    }

    public static String encodeImageToString(String imagePath){

        Bitmap imageBitmap = decodeBitmapFromFile(imagePath,1024,1024);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        imageBitmap.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream);
        byte[] byteArrayVar = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArrayVar, Base64.DEFAULT);
    }

    private static DisplayMetrics getDisplayMetrics(Context context) {
        Resources resources = context.getResources();
        return resources.getDisplayMetrics();
    }

    public static float convertDpToPixel(float dp, Context context) {
        return dp * (getDisplayMetrics(context).densityDpi / 160f);
    }


    public static Bitmap decodeBitmapFromFile(String filePath, int sizeW, int sizeH) {

//        Get the dimensions of the view
        int targetW = sizeW;
        int targetH = sizeH;

//         Get the dimensions of the bitmap

        BitmapFactory.Options bmOption = new BitmapFactory.Options();
        bmOption.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath,bmOption);
        int photoW = bmOption.outWidth;
        int photoH = bmOption.outHeight;

//         Determine how much to scale down the image 720 1280

        int scaleFactor = Math.min(photoW/1024,photoH/1024);


//        Decode the image file into a Bitmap sized to fill the View
        bmOption.inJustDecodeBounds = false;
        bmOption.inSampleSize = scaleFactor;

        return BitmapFactory.decodeFile(filePath,bmOption);

    }


    public static boolean hasCamera(Context context) {
        PackageManager pm = context.getPackageManager();
        return pm.hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }

    public static boolean isExternalStorageAvailable() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }



    public static void setViewHeight(View v, int height) {
        ViewGroup.LayoutParams params = v.getLayoutParams();
        params.height = height;
        v.requestLayout();
    }


    public static void setViewWidth(View v, int width) {
        final ViewGroup.LayoutParams params = v.getLayoutParams();
        params.width = width;
        v.requestLayout();
    }

    public static void setViewMarginTop(View v, int marginTop) {
        setViewMargin(v, 0, marginTop, 0, 0);
    }


    public static void setViewMargin(View v, int left, int top, int right, int bottom) {
        final ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
        params.setMargins(left, top, right, bottom);
        v.requestLayout();
    }

}
