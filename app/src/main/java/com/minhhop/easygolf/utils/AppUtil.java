package com.minhhop.easygolf.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Patterns;
import android.util.TypedValue;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.minhhop.easygolf.framework.common.Contains;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.TimeZone;

import kotlin.Deprecated;

public class AppUtil {
    private static final String ALLOWED_CHARACTERS = "0123456789qwertyuiopasdfghjklzxcvbnm";

    //Meter
    public final static double EARTH_RADIUS = 6378100.0;
    public static File mFile;

    private static String mCurrentPhotoPath;

    public static boolean IsGpsOn(Activity activity) {
        LocationManager manager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
        assert manager != null;
        return !manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    public static float dp2px(float dip,Context context){

        Resources r = context.getResources();
        return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dip,
                r.getDisplayMetrics()
        );
    }

    public static String getRandomString(int sizeOfRandomString) {
        Random random = new Random();
        StringBuilder sb = new StringBuilder(sizeOfRandomString);
        for (int i = 0; i < sizeOfRandomString;i ++) {
            sb.append(ALLOWED_CHARACTERS.charAt(random.nextInt(ALLOWED_CHARACTERS.length())));
        }
        return sb.toString() + Calendar.getInstance().getTimeInMillis();
    }


    public static boolean IsTextEmpty(String ... values){
        boolean result = false;
        for (String item : values) {
            if(TextUtils.isEmpty(item)){
                result = true;
                break;
            }
        }
        return !result;
    }



    private static void setImageDrawableAssets(Context context, String nameFile, ImageView imgView){
        try
        {
            InputStream ims = context.getAssets().open(nameFile);
            Drawable d = Drawable.createFromStream(ims, null);
            imgView.setImageDrawable(d);
            ims .close();
        }
        catch(IOException ex)
        {
            ex.printStackTrace();
        }
    }

    public static void setFlagDrawableAssets(Context context, String iso, ImageView imgView){
        iso = iso.toLowerCase();
        setImageDrawableAssets(context,iso + ".png",imgView);
    }

    public static int convertDpToPixel(int value,Context context){
        float density = context.getResources().getDisplayMetrics().density;
        return Math.round(value * density);

    }

    public static String getLastVersion(Context context){
        String version = null;
        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            version = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return version;
    }



    public static String getSizeInApp(){
        return "19.3 MB";
    }

    public static String getLastTimeUpdate(Context context,String patternFormat){

        PackageManager pm = context.getPackageManager();
        ApplicationInfo appInfo;
        try {
            appInfo = pm.getApplicationInfo(context.getPackageName(), 0);
            String appFile = appInfo.sourceDir;
            long timeMini = new File(appFile).lastModified();

            return new SimpleDateFormat(patternFormat,Locale.ENGLISH)
                        .format(new Date(timeMini));


        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return "";
    }


    public static String formatDateToIS08601UTC(Date target){
        TimeZone tz = TimeZone.getTimeZone("UTC");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'",Locale.getDefault());
        df.setTimeZone(tz);
        return df.format(target);
    }
    public static String fromISO8601UTC(String dateStr) {
        return fromISO8601UTC(dateStr,"MMMM dd,yyyy hh:mm:ss a");
    }

    public static Date fromISO8601UTCToDate(String dateStr) {
        TimeZone tz = TimeZone.getTimeZone("UTC");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'",Locale.getDefault());
        df.setTimeZone(tz);

        try {
            return df.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
    public static String formatStringDate(String dateStr,String format) {
        TimeZone tz = TimeZone.getTimeZone("UTC");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'",Locale.getDefault());
        df.setTimeZone(tz);

        try {
            Date result = df.parse(dateStr);
            SimpleDateFormat formatter = new SimpleDateFormat(format,Locale.getDefault());
            return formatter.format(result);
        } catch (ParseException e) {
            e.printStackTrace();
        }



        return null;
    }


    public static String fromISO8601UTC(String dateStr,String format) {
        TimeZone tz = TimeZone.getTimeZone("UTC");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'",Locale.getDefault());
        df.setTimeZone(tz);

        try {
            Date result = df.parse(dateStr);
            SimpleDateFormat formatter = new SimpleDateFormat(format,Locale.getDefault());
            return formatter.format(result);
        } catch (ParseException e) {
            e.printStackTrace();
        }



        return null;
    }

    public static String formatDate(Date target,String format){
        SimpleDateFormat formatter = new SimpleDateFormat(format,Locale.getDefault());
        return formatter.format(target);
    }


    public static String formatTagVideo(List<String> data){
        String result = "";
        if(data != null) {
            int size = data.size();
            for (int i = 0; i < size; i++) {
                result = data.get(i);
                if (i < size - 1) {
                    result += " - ";
                }
            }

            return result;
        }

        return null;
    }




    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    public static void pickImageGallery(Activity activity){

        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        activity.startActivityForResult(galleryIntent, Contains.REQUEST_CODE_PHOTO_GALLERY);
    }

    @Deprecated(message = "it not working in android 10")
    public static void dispatchTakePictureIntent(Activity activity) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(activity.getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile(activity);
            } catch (IOException ex) {
                // Error occurred while creating the File

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photoFile));
                activity.startActivityForResult(takePictureIntent, Contains.REQUEST_CODE_CAMERA);
            }
        }
    }

    private static File createImageFile(Activity activity) throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat(Contains.FORMAT_IMAGE, Locale.ENGLISH).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        mFile = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = mFile.getAbsolutePath();

        galleryAddPic(activity);
        return mFile;
    }


    private static void galleryAddPic(Activity activity) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        activity.sendBroadcast(mediaScanIntent);
    }


    private static Bitmap rotateBitmap(Bitmap bitmap, int orientation) {
        Matrix matrix = new Matrix();
        switch (orientation) {
            case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                matrix.setScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                matrix.setRotate(180);
                break;
            case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                matrix.setRotate(180);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_TRANSPOSE:
                matrix.setRotate(90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_90:
                matrix.setRotate(90);
                break;
            case ExifInterface.ORIENTATION_TRANSVERSE:
                matrix.setRotate(-90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                matrix.setRotate(-90);
                break;
            default:
                return bitmap;
        }
        try {
            Bitmap bmRotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            bitmap.recycle();

            return bmRotated;
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            return null;
        }
    }

    private static void saveBitmapToFile(Context context, Bitmap croppedImage, File targetFile) {
        Uri saveUri  = Uri.fromFile(targetFile);
        if (saveUri != null) {
            OutputStream outputStream = null;
            try {
                outputStream = context.getContentResolver().openOutputStream(saveUri);
                if (outputStream != null) {
                    croppedImage.compress(Bitmap.CompressFormat.JPEG, 90, outputStream);
                }
            } catch (IOException ignored) {

            } finally {
                closeSilently(outputStream);
                croppedImage.recycle();
            }
        }
    }

    private static void closeSilently(@Nullable Closeable c) {
        if (c == null) {
            return;
        }
        try {
            c.close();
        } catch (Throwable t) {
            // Do nothing
        }
    }

    public static int getWidthScreen(Activity activity){
        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.widthPixels;
    }

}
