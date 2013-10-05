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
		
		bitmap = Bitmap.createScaledBitmap(bitmap, 1032, 774, true); //4128*3096 /4
		Log.i(TAG, "Image width after scaling is : " + bitmap.getWidth() + "Image height is : " + bitmap.getHeight());
		
		bitmap = rotateBitMap(bitmap, 90);
		Log.i(TAG, "Image width after rotating is : " + bitmap.getWidth() + "Image height is : " + bitmap.getHeight());
		
		bitmap= bitmap.copy(Bitmap.Config.ARGB_8888, true);
		
		return bitmap;
	}

	private Bitmap rotateBitMap(Bitmap bitmap, float angle) {
		Matrix matrix = new Matrix();
		matrix.postRotate(angle);
		Bitmap rotatedBitmap = Bitmap.createBitmap(bitmap , 0, 0, bitmap .getWidth(), bitmap .getHeight(), matrix, true);
		
		return rotatedBitmap;
	}
}
	