package com.hello;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.hardware.Camera.Size;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.SurfaceView;
import android.view.WindowManager;

public class HelloOpenCvActivity extends Activity implements CvCameraViewListener2 { 

	private HelloViewer mOpenCvCameraView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.opencv_layout);
		
//		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
		mOpenCvCameraView = (HelloViewer) findViewById(R.id.HelloOpenCvView);
		mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
		mOpenCvCameraView.setCvCameraViewListener(this);
	}

	@Override
	public void onPause() {
		super.onPause();
		if (mOpenCvCameraView != null)
			mOpenCvCameraView.disableView();
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		if (mOpenCvCameraView != null)
			mOpenCvCameraView.disableView();
	}

	@Override
	public void onCameraViewStarted(int width, int height) {
		Size resolution = mOpenCvCameraView.getResolution();
		mOpenCvCameraView.setResolution(resolution);
		mOpenCvCameraView.setAutoFocus();
	}

	@Override
	public void onCameraViewStopped() {
	}

	@Override
	public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
		Mat mRgba = inputFrame.rgba();

		List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
		try {
			contours.add(DetectSquares.find(mRgba));
			if (contours.get(0) != null) {
				Imgproc.drawContours(mRgba, contours, -1, new Scalar(0, 255, 0), 4);
				
				//TODO: Add a beep sound
				Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
			    Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
			    r.play();
			    				
				// take picture
			    Timer timer = new Timer();
			    timer.schedule(new TimerTask() {
			    	  @Override
			    	  public void run() {
			    		  captureImage();
			    	  }
			    }, 5*1000);
			}
			else {
				// kill the audio thread
			}
		}
		catch(Exception exc) {
			Log.e(Util.TAG, "Error occured" + exc.getMessage());
		}

		return mRgba;
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.hello_open_cv, menu);
		return true;
	}

	private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
		@Override
		public void onManagerConnected(int status) {
			switch (status) {
			case LoaderCallbackInterface.SUCCESS: {
				Log.i(Util.TAG, "OpenCV loaded successfully");
				mOpenCvCameraView.enableView();
			}
				break;
			default: {
				super.onManagerConnected(status);
			}
				break;
			}
		}
	};

	@Override
	public void onResume() {
		super.onResume();
		OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_6, this, mLoaderCallback);
	}

	@SuppressLint("SimpleDateFormat")
	private void captureImage() {
		try {
			Log.i(Util.TAG,"onTouch event");
	        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
	        String currentDateandTime = sdf.format(new Date());
	        String fileName = Environment.getExternalStorageDirectory().getPath() +
	                               "/project/" + currentDateandTime + ".jpg";
	        
	        mOpenCvCameraView.takePicture(fileName);
//	        Toast.makeText(this, fileName + " saved", Toast.LENGTH_SHORT).show();
		}
		catch (Exception exc) {
			Log.e(Util.TAG, "Error in capture image", exc);
		}
	}
}
