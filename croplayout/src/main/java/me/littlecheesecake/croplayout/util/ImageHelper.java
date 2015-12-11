/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 ViSenze Pte. Ltd.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package me.littlecheesecake.croplayout.util;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by yulu on 29/1/15.
 *
 * Helper class to handle image related task such resize, rotate, save to local storage
 */
public class ImageHelper {
    private static final String IMAGE_HELPER = "image helper";

    /**
     * rotate image with degrees
     * @param bitmap bitmap
     * @param degree rotate degree
     * @return rotated bitmap
     */
    public static Bitmap rotateImage(Bitmap bitmap, int degree) {
        //bitmap = scaleBitmap(bitmap);

        Matrix matrix = new Matrix();
        matrix.postRotate(degree);

        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

        return bitmap;
    }


    /**
     * load bitmap from path
     * @param filePath path to the image
     * @return bitmap
     */
    public static Bitmap getBitmapFromPath(String filePath) {
        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();

        bitmapOptions.inScaled = false;
        bitmapOptions.inJustDecodeBounds = false;

        return BitmapFactory.decodeFile(filePath, bitmapOptions);

    }

    public static Bitmap getBitmapFromResource(Resources resource, int id) {
        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();

        bitmapOptions.inScaled = false;
        bitmapOptions.inJustDecodeBounds = false;

        return BitmapFactory.decodeResource(resource, id, bitmapOptions);
    }

    /**
     * save the bitmap to the local path
     * @param bitmap bitmap
     * @param imagePath path to save the image
     */
    public static void saveImageToPath(Bitmap bitmap, String imagePath) {

        //Save image to local path
        try {
            File imageFile = new File(imagePath);
            FileOutputStream fos = new FileOutputStream(imageFile);

            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);

            fos.flush();
            fos.close();

        } catch (IOException e) {
            Log.d(IMAGE_HELPER, e.toString());
        }
    }


    /**
     * crop image and save as thumbnail
     * @param bitmap original bitmap
     * @param x1 x1
     * @param y1 y1
     * @param x2 x2
     * @param y2 y2
     * @return local file path
     */
    public static String saveImageCropToPath(Bitmap bitmap, int x1, int y1, int x2, int y2,
                                             String directoryPath, String imageName) {
        if (y2 - y1 > bitmap.getHeight())
            y2 = bitmap.getHeight() + y1;
        if (x2 - x1 > bitmap.getWidth())
            x2 = bitmap.getWidth() + x1;

        Bitmap cropBitmap = Bitmap.createBitmap(bitmap, x1, y1, x2 - x1, y2 - y1);

        //Get the album local path
        String fullPath = null;

        //Save image to local path
        try {
            File fileDir = new File(directoryPath);
            if(!fileDir.exists())
                fileDir.mkdir();

            File imageFile = new File(directoryPath, imageName);
            FileOutputStream fos = new FileOutputStream(imageFile);

            cropBitmap.compress(Bitmap.CompressFormat.JPEG, 80, fos);

            fos.flush();
            fos.close();

            fullPath = imageFile.getPath();
        } catch (IOException e) {
            Log.d(IMAGE_HELPER, e.toString());
        }

        cropBitmap.recycle();

        return fullPath;
    }

}
