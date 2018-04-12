package com.team06.freehand.Utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by isabellepotvin on 2018-03-17.
 *
 * responsible for converting img url to bitmap
 *
 */

public class ImageManager {

    private static final String TAG = "ImageManager";


    //takes the image url and converts it to a bitmap
    public static Bitmap getBitmap(String imgUrl, Context mContext){
        File imageFile = new File(imgUrl);
        FileInputStream fis = null;
        Bitmap bitmap = null;

        try{
            fis = new FileInputStream(imageFile);

            //BitmapFactory.Options options = new BitmapFactory.Options();
            //options.inPreferredConfig = Bitmap.Config.RGB_565;
            //bitmap = BitmapFactory.decodeStream(fis, null, options);
            bitmap = BitmapFactory.decodeStream(fis);

        }catch (FileNotFoundException e){
            Log.e(TAG, "getBitmap: FileNotFoundException: " + e.getMessage() );
            Toast.makeText(mContext, "Sorry, we couldn't upload your image", Toast.LENGTH_SHORT).show();

        }catch (OutOfMemoryError e){
            Log.e(TAG, "getBitmap: OutOfMemoryError: " + e.getMessage() );
            Toast.makeText(mContext, "Sorry, we couldn't upload your image.  The file is too large.", Toast.LENGTH_SHORT).show();
        }

        finally {
            try{
                fis.close();
            }catch (IOException e) {
                Log.e(TAG, "getBitmap: IOException: " + e.getMessage());
            }
        }

        return bitmap;

    }

    /**
     * return byte array from a bitmap
     * quality is greater than 0 but less than 100
     * @param bm
     * @param quality
     * @return
     */
    public static byte[] getBytesFromBitmap(Bitmap bm, int quality, Context context){

        int mQuality = 0;
        boolean isFileTooLarge = false;

        //24424448
        //22990464
        //17915904 //1.78mb
        //203454
        //807716

        try {

            //gets byteSize of image
            int byteSize = bm.getRowBytes() * bm.getHeight();
            Log.d(TAG, "getBytesFromBitmap: byteSize: " + byteSize);

            //sets the quality of image depending on the byteSize
            if(byteSize > 100000000){
                isFileTooLarge = true;
            }
            else if(byteSize > 25000000) {
                mQuality = 20;
            }
            else if(byteSize > 20000000){
                mQuality = 30;
            }
            else if(byteSize > 15000000) {
                mQuality = 40;
            }
            else if(byteSize > 10000000){
                mQuality = 50;
            }
            else if(byteSize > 8000000){
                mQuality = 60;
            }
            else if(byteSize > 5000000){
                mQuality = 70;
            }
            else if(byteSize > 3000000){
                mQuality = 80;
            }
            else if(byteSize > 1000000){
                mQuality = 90;
            }
            else{
                mQuality = 100;
            }

            if(isFileTooLarge){
                Toast.makeText(context, "Sorry, we couldn't upload your image. The file is too large.", Toast.LENGTH_SHORT).show();
            }
            else{
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bm.compress(Bitmap.CompressFormat.JPEG, mQuality, stream);
                return stream.toByteArray();
            }

        } catch (OutOfMemoryError e){
            Log.e(TAG, "getBitmap: FileNotFoundException: " + e.getMessage() );
            Toast.makeText(context, "Sorry, we couldn't upload your image. The file is too large.", Toast.LENGTH_SHORT).show();
        }

        return null;
    }

}












