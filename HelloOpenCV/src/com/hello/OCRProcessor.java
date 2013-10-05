package com.hello;

import java.io.IOException;

import com.googlecode.tesseract.android.TessBaseAPI;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Environment;

public class OCRProcessor {
	static TessBaseAPI baseApi;
	
	static {
		baseApi = new TessBaseAPI();
		//Environment.getExternalStorageDirectory().getPath();
		//./Android/data/com.datumdroid.app/files/mounted/tessdata/eng.traineddata
//		String DATA_PATH = Environment.getExternalStorageDirectory().getPath() + "/Android/data/com.datumdroid.app/files/mounted/tessdata/eng.traineddata";
		
		String DATA_PATH = Environment.getExternalStorageDirectory().getPath() + "/project";
		String lang = "eng";
		baseApi.init(DATA_PATH, lang);
	}
	
	public String getOCRText(Bitmap bitmap) {
		baseApi.setImage(bitmap);
		String recognizedText = baseApi.getUTF8Text();
		baseApi.end();
		
		return recognizedText;
	}
}
