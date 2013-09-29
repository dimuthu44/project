package com.hello;

import java.io.IOException;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.AsyncTask;
import android.util.Log;


public class BitmapWorkerTask extends AsyncTask<String, Void, Bitmap> {
	protected static final String TAG = "DIMUTHU::";

	@Override
	protected Bitmap doInBackground(String... arg0) {
		String path = arg0[0];

		ExifInterface exif = null;
		try {
			exif = new ExifInterface(path);
		} catch (IOException ioex) {
			Log.e(TAG, ioex.toString());
		}
		int exifOrientation = exif.getAttributeInt(
		        ExifInterface.TAG_ORIENTATION,
		        ExifInterface.ORIENTATION_NORMAL);

		Log.i(TAG, "Image EXIF orientation is : " + exifOrientation);
		
		int rotate = 0;

		switch (exifOrientation) {
		case ExifInterface.ORIENTATION_ROTATE_90:
		    rotate = 90;
		    break;
		case ExifInterface.ORIENTATION_ROTATE_180:
		    rotate = 180;
		    break;
		case ExifInterface.ORIENTATION_ROTATE_270:
		    rotate = 270;
		    break;
		}
		
		Bitmap bitmap = BitmapFactory.decodeFile(path);

		if (rotate != 0) {
		    int w = bitmap.getWidth();
		    int h = bitmap.getHeight();

		    // Setting pre rotate
		    Matrix mtx = new Matrix();
		    mtx.preRotate(rotate);

		    // Rotating Bitmap & convert to ARGB_8888, required by tess
		    bitmap = Bitmap.createBitmap(bitmap, 0, 0, w, h, mtx, false);
		}
		
		// TODO: Here the size is full size. Resize this.
		bitmap = Bitmap.createScaledBitmap(bitmap, 1032, 774, true); //4128*3096 /4
		Bitmap bbbb = bitmap.copy(Bitmap.Config.ARGB_8888, true);
		bitmap = null;
		
		return bbbb;
	}
}
	