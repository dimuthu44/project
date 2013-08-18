package com.hello;

import java.io.IOException;

import com.googlecode.tesseract.android.TessBaseAPI;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Environment;

public class OCRProcessor {
	
	public static Bitmap getBitmapImage(String path) throws IOException {
		Bitmap bitmap = BitmapFactory.decodeFile(path);
		// _path = path to the image to be OCRed
		ExifInterface exif = new ExifInterface(path);
		int exifOrientation = exif.getAttributeInt(
		        ExifInterface.TAG_ORIENTATION,
		        ExifInterface.ORIENTATION_NORMAL);

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

		if (rotate != 0) {
		    int w = bitmap.getWidth();
		    int h = bitmap.getHeight();

		    // Setting pre rotate
		    Matrix mtx = new Matrix();
		    mtx.preRotate(rotate);

		    // Rotating Bitmap & convert to ARGB_8888, required by tess
		    bitmap = Bitmap.createBitmap(bitmap, 0, 0, w, h, mtx, false);
		}
		bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
		
		return bitmap;
	}
	
	public static String getOCRText(Bitmap bitmap) {
		TessBaseAPI baseApi = new TessBaseAPI();
		//Environment.getExternalStorageDirectory().getPath();
		//./Android/data/com.datumdroid.app/files/mounted/tessdata/eng.traineddata
//		String DATA_PATH = Environment.getExternalStorageDirectory().getPath() + "/Android/data/com.datumdroid.app/files/mounted/tessdata/eng.traineddata";
		
		String DATA_PATH = Environment.getExternalStorageDirectory().getPath() + "/project";
		String lang = "eng";
		baseApi.init(DATA_PATH, lang);
		baseApi.setImage(bitmap);
		String recognizedText = baseApi.getUTF8Text();
		baseApi.end();
		
		return recognizedText;
	}
}
