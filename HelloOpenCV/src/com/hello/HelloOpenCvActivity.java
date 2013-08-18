package com.hello;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Scalar;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera.Size;
import android.os.Bundle;
import android.os.Environment;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.Toast;

public class HelloOpenCvActivity extends Activity implements CvCameraViewListener2, OnTouchListener { 

	protected static final String TAG = "DIMUTHU::";
	HelloViewer mOpenCvCameraView;
	Mat square2;
	int counter = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.opencv_layout);
		
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
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

	public void onDestroy() {
		super.onDestroy();
		if (mOpenCvCameraView != null)
			mOpenCvCameraView.disableView();
	}

	public void onCameraViewStarted(int width, int height) {
		Size resolution = mOpenCvCameraView.getResolution();
		mOpenCvCameraView.setResolution(resolution);
	}

	public void onCameraViewStopped() {
	}

	public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
		Mat mRgba = inputFrame.rgba();

//		List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
//		try {
//			contours.add(DetectSquares.find(mRgba));
//			if (contours.get(0) != null) {
//				Imgproc.drawContours(mRgba, contours, -1/*TODO*/, new Scalar(0, 255, 0), 4);
//				// beep sound
//				// take picture
//			}
//		}
//		catch(Exception exc) {
//			Log.e(TAG, "Error occured" + exc.getMessage());
//		}

		return mRgba;
	}
	
	public void processOCR() {
		try {
			String imagePath = Environment.getExternalStorageDirectory().getPath() + "/project/111.jpg";
			Bitmap bitmap = OCRProcessor.getBitmapImage(imagePath);
//			Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
			String text = OCRProcessor.getOCRText(bitmap);
			Log.i(TAG, "TEXT \n" + text);
		}
		catch(Exception exc) {
			Log.e(TAG, "Error occured" + exc.getMessage());
		}
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
				Log.i(TAG, "OpenCV loaded successfully");
				mOpenCvCameraView.enableView();
				mOpenCvCameraView.setOnTouchListener(HelloOpenCvActivity.this);
				//square2 = Highgui.imread("/mnt/sdcard/square2.jpg");
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
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        Log.i(TAG,"onTouch event");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
        String currentDateandTime = sdf.format(new Date());
        String fileName = Environment.getExternalStorageDirectory().getPath() +
                               "/project/sample_picture_" + currentDateandTime + ".jpg";
        
        mOpenCvCameraView.takePicture(fileName);
        Toast.makeText(this, fileName + " saved", Toast.LENGTH_SHORT).show();
        return false;
    }
}
