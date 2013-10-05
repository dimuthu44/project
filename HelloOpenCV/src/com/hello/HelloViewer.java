package com.hello;

import java.io.FileOutputStream;
import java.io.IOException;

import org.opencv.android.JavaCameraView;

import android.content.Context;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.hardware.Camera.Size;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.util.Log;

public class HelloViewer extends JavaCameraView implements PictureCallback, ShutterCallback {

//	protected static final String TAG = "DIMUTHU::";
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
		Log.i(Util.TAG, "Seting autofocus mode");
		Camera.Parameters parameters = mCamera.getParameters();
		parameters.setFocusMode("continuous-picture");
		mCamera.setParameters(parameters);
	}

	public void takePicture(final String fileName) {
		Log.i(Util.TAG, "Taking picture");

		this.mPictureFileName = fileName;
		// Postview and jpeg are sent in the same buffers if the queue is not
		// empty when performing a capture.
		// Clear up buffers to avoid mCamera.takePicture to be stuck because of
		// a memory issue

//		mCamera.setPreviewCallback(null);
		// mCamera.autoFocus(autoFocusCallback);

		// PictureCallback is implemented by the current class
		mCamera.takePicture(this, null, null, this);
		//TODO: We need to stop processing afterwards.
//		mCamera.stopPreview();
	}

	AutoFocusCallback autoFocusCallback = new AutoFocusCallback() {
		@Override
		public void onAutoFocus(boolean success, Camera camera) {
			Log.i(Util.TAG, "autofocus callback called.");
		}
	};

	@Override
	public void onPictureTaken(byte[] data, Camera camera) {
		Log.i(Util.TAG, "Saving a bitmap to file");

		// Write the image in a file (in jpeg format)
		try {
			FileOutputStream fos = new FileOutputStream(mPictureFileName);
			fos.write(data);
			fos.close();

		} catch (IOException e) {
			Log.e(Util.TAG, "Exception in photoCallback", e);
		} finally {
			// The camera preview was automatically stopped. Start it again.
			// TODO: Stop this recall and let the program terminate.
			mCamera.startPreview();
			mCamera.setPreviewCallback(this);
		}

		processOCR(mPictureFileName);
	}

	public void processOCR(String imagePath) {
		try {
			Log.i(Util.TAG, "Started processing OCR");
			
			BitmapWorkerTask task = new BitmapWorkerTask();
			AsyncTask<String, Void, Bitmap> aTask = task.execute(imagePath);

			OCRProcessor ocr = new OCRProcessor();
			String text = ocr.getOCRText(aTask.get());

			// Write text to file
			Log.i(Util.TAG, "TEXT \n" + text);
		} catch (Exception exc) {
			Log.e(Util.TAG, "Error occured in processing OCR\n" + exc);
		}
	}

	@Override
	public void onShutter() {
	}
}
