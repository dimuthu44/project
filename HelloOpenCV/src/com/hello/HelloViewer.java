package com.hello;

import java.io.FileOutputStream;
import java.io.IOException;

import org.opencv.android.JavaCameraView;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.Size;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.util.Log;

public class HelloViewer extends JavaCameraView implements PictureCallback {

	protected static final String TAG = "DIMUTHU::";
	private String mPictureFileName;

	public HelloViewer(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public void setResolution(Size resolution) {
		disconnectCamera();
		mMaxHeight = 640; // resolution.height;
		mMaxWidth = 480; // resolution.width;
		connectCamera(getWidth(), getHeight());
	}

	public Size getResolution() {
		return mCamera.getParameters().getPreviewSize();
	}

	public void setAutoFocus() {
		Log.i(TAG, "Seting autofocus mode");
		Camera.Parameters parameters = mCamera.getParameters();
		parameters.setFocusMode("continuous-picture");
	}

	public void takePicture(final String fileName) {
		Log.i(TAG, "Taking picture");

		this.mPictureFileName = fileName;
		// Postview and jpeg are sent in the same buffers if the queue is not
		// empty when performing a capture.
		// Clear up buffers to avoid mCamera.takePicture to be stuck because of
		// a memory issue

		mCamera.setPreviewCallback(null);
		// mCamera.autoFocus(autoFocusCallback);

		// PictureCallback is implemented by the current class
		mCamera.takePicture(null, null, this);
		// mCamera.stopPreview();
	}

	AutoFocusCallback autoFocusCallback = new AutoFocusCallback() {
		@Override
		public void onAutoFocus(boolean success, Camera camera) {
			Log.i(TAG, "autofocus callback called.");
		}
	};

	@Override
	public void onPictureTaken(byte[] data, Camera camera) {
		Log.i(TAG, "Saving a bitmap to file");

		// Write the image in a file (in jpeg format)
		try {
			FileOutputStream fos = new FileOutputStream(mPictureFileName);
			fos.write(data);
			fos.close();

		} catch (IOException e) {
			Log.e(TAG, "Exception in photoCallback", e);
		} finally {

			// The camera preview was automatically stopped. Start it again.
			mCamera.startPreview();
			mCamera.setPreviewCallback(this);
		}

		processOCR(mPictureFileName);
	}

	public void processOCR(String imagePath) {
		try {
			Log.i(TAG, "Started processing OCR");
			// String imagePath =
			// Environment.getExternalStorageDirectory().getPath() +
			// "/project/111.jpg";
			BitmapWorkerTask task = new BitmapWorkerTask();
			AsyncTask<String, Void, Bitmap> aTask = task.execute(imagePath);

			OCRProcessor ocr = new OCRProcessor();
			// Bitmap bitmap = ocr.getBitmapImage(imagePath);
			// Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
			String text = ocr.getOCRText(aTask.get());

			// Write text to file
			Log.i(TAG, "TEXT \n" + text);
		} catch (Exception exc) {
			Log.e(TAG, "Error occured in processing OCR\n" + exc);
		}
	}
}
