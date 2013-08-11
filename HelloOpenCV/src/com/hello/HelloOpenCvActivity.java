package com.hello;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

import android.os.Bundle;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.Menu;
import android.view.SurfaceView;
import android.view.WindowManager;

public class HelloOpenCvActivity extends Activity implements CvCameraViewListener2 {

    protected static final String TAG = "DIMUTHU::";
    CameraBridgeViewBase mOpenCvCameraView;
    Mat image;
    int counter = 0;

	@Override
    protected void onCreate(Bundle savedInstanceState) {
		Log.i(TAG, "called onCreate");
		super.onCreate(savedInstanceState);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.opencv_layout);
		mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.HelloOpenCvView);
		mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
		mOpenCvCameraView.setCvCameraViewListener(this);
    }
	
	@Override
	public void onPause()
	{
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
		DetectSquares.prepare();
	}
	
	public void onCameraViewStopped() {
	}
	
	public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
		//if (counter++ % 10 == 0) {
			
			Mat mRgba = inputFrame.rgba();
			
			ArrayList<List<Point>> squares = new ArrayList<List<Point>>();
			DetectSquares.fintSquares(mRgba, squares);
			if (squares.size() > 0) {
				Log.i(TAG, "Square count is : " + squares.size());
				drawSquares(mRgba, squares);
			}
			
			
		//}
		//else {
		//	return inputFrame.rgba();
		//}
	        
			//test 1
//		Mat mRgba = inputFrame.gray();
//		Core.rectangle(mRgba, new Point(3, 4), new Point(7, 8), new Scalar(0,255,0));
//        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
//        Imgproc.findContours(mRgba, contours, new Mat(), Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
//
//        Imgproc.drawContours(mRgba, contours, -1, new Scalar(255,0,0));
//        Log.i(TAG, "contour count " + contours.size());
			
			// test 2
//			Mat blurred = new Mat(mRgba.size(), mRgba.type());
//			Imgproc.medianBlur(mRgba, blurred, 9);
//			return blurred;
        return mRgba;
		
	}
	
	// the function draws all the squares in the image
	private void drawSquares(Mat image, ArrayList<List<Point>> squares)
	{
		List<MatOfPoint> polyline = null;
	    for(int i = 0; i < squares.size(); i++ )
	    {
	        Point point = squares.get(i).get(0);
	                
	        Log.i(TAG, "Points x : " + point.x + " y: " + point.y);
	        
	        polyline = new ArrayList<MatOfPoint>();
	        polyline.add(new MatOfPoint(point)); //TODO
	    }
	    
	    if (polyline != null) {
	    	Core.polylines(image, polyline, true, new Scalar(255, 0, 0));
	    	Imgproc.drawContours(image, polyline, -1, new Scalar(0,255,0), Core.FILLED);
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
    			case LoaderCallbackInterface.SUCCESS:
    			{
    				Log.i(TAG, "OpenCV loaded successfully");
    				mOpenCvCameraView.enableView();
    				image = Highgui.imread("/mnt/sdcard/square2.jpg");
    			} break;
    			default:
    			{
    				super.onManagerConnected(status);
    			} break;
    		}
    	}
    };
    	
	 @Override
	 public void onResume()
	 {
		 super.onResume();
		 OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_6, this, mLoaderCallback);
	 }
}
