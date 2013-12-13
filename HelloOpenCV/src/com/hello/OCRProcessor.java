package com.hello;

import android.graphics.Bitmap;
import android.os.Environment;

import com.googlecode.tesseract.android.TessBaseAPI;

public class OCRProcessor {
	
	public String getOCRText(Bitmap bitmap) {
		TessBaseAPI baseApi = new TessBaseAPI();
		
		String DATA_PATH = Environment.getExternalStorageDirectory().getPath() + "/project";
		String lang = HelloOpenCvActivity.lang;
		baseApi.init(DATA_PATH, lang);
		baseApi.setImage(bitmap);
		String recognizedText = baseApi.getUTF8Text();
		baseApi.end();
		
		return recognizedText;
	}
}
