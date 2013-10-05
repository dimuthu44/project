package com.hello;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.AsyncTask;

public class BitmapWorkerTask extends AsyncTask<String, Void, Bitmap> {

	@Override
	protected Bitmap doInBackground(String... arg0) {
		String path = arg0[0];
		
		Bitmap bitmap = BitmapFactory.decodeFile(path);
		bitmap = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth()/2, bitmap.getHeight()/2, true); //original : 4128*3096
		bitmap = rotateBitMap(bitmap, 90);
		bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
		
		return bitmap;
	}

	private Bitmap rotateBitMap(Bitmap bitmap, float angle) {
		Matrix matrix = new Matrix();
		matrix.postRotate(angle);
		Bitmap rotatedBitmap = Bitmap.createBitmap(bitmap , 0, 0, bitmap .getWidth(), bitmap .getHeight(), matrix, true);
		
		return rotatedBitmap;
	}
}
	