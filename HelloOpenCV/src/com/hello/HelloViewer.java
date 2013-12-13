package com.hello;

import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.opencv.android.JavaCameraView;

import android.content.Context;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.hardware.Camera.Size;
import android.os.AsyncTask;
import android.speech.tts.TextToSpeech;
import android.util.AttributeSet;
import android.util.Log;

public class HelloViewer extends JavaCameraView implements PictureCallback, ShutterCallback {

	private String mPictureFileName;

	public HelloViewer(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public void setResolution(Size resolution) {
		disconnectCamera();
		
		mMaxHeight = 480; // resolution.width;
		mMaxWidth = 640; // resolution.height;
//		connectCamera(getWidth(), getHeight());
		// Sets the preview size of camera.
		
		connectCamera(mMaxWidth, mMaxHeight);
	}

	public Size getResolution() {
		List<Camera.Size> sizes  = mCamera.getParameters().getSupportedPictureSizes();
		for (Camera.Size size : sizes) {
			Log.i(Util.TAG, "Picture sizes height : " + size.height + ", width : " + size.width);
		}
		
		return mCamera.getParameters().getPreviewSize();
	}

	public void setAutoFocus() {
		Log.i(Util.TAG, "Seting autofocus mode to Continuous picture.");
		Camera.Parameters parameters = mCamera.getParameters();
		parameters.setFocusMode("continuous-picture");
		//TODO: Enable in nexus 3
		parameters.setPictureSize(2592, 1944);
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
//
//	AutoFocusCallback autoFocusCallback = new AutoFocusCallback() {
//		@Override
//		public void onAutoFocus(boolean success, Camera camera) {
//			Log.i(Util.TAG, "autofocus callback called.");
//		}
//	};

	@Override
	public void onPictureTaken(byte[] data, Camera camera) {
		Log.i(Util.TAG, "Saving a bitmap to file");
		//TODO: TTS say OCR in progress

		// Write the image in a file (in jpeg format)
		try {
			FileOutputStream fos = new FileOutputStream(mPictureFileName + ".jpg");
			fos.write(data);
			fos.close();

		} catch (IOException e) {
			Log.e(Util.TAG, "Exception in photoCallback", e);
		} finally {
			processOCR(mPictureFileName);
			// The camera preview was automatically stopped. Start it again.
			// TODO: Stop this recall and let the program terminate.
			mCamera.startPreview();
			mCamera.setPreviewCallback(this);
		}
	}

	public void processOCR(String imagePath) {
		try {
			Log.i(Util.TAG, "Started processing OCR");
			
			BitmapWorkerTask task = new BitmapWorkerTask();
			AsyncTask<String, Void, Bitmap> aTask = task.execute(mPictureFileName + ".jpg");

			OCRProcessor ocr = new OCRProcessor();
			String text = ocr.getOCRText(aTask.get());
			
			// Write text to file
			HelloOpenCvActivity.mTts.speak(text, TextToSpeech.QUEUE_ADD, null);
			Log.i(Util.TAG, "TEXT \n" + text);
			writeToFile(text, mPictureFileName + ".txt");
		} 
		catch (Exception exc) {
			Log.e(Util.TAG, "Error occured in processing OCR", exc);
		}
	}

	private void writeToFile(String text, String fileName) {
	    try {
	    	FileWriter writer = new FileWriter(fileName);
	    	writer.write(text);
	    	writer.close();
	    }
	    catch (Exception e) {
	        Log.e(Util.TAG, "File write failed: " + e.toString());
	    } 
	}
	
	@Override
	public void onShutter() {
	}
	
	public Camera getCamera() {
		return mCamera;
	}
	
	public void setCamera(Camera camera) {
		this.mCamera = camera;
	}
}
