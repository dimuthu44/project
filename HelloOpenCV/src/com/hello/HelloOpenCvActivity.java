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
    private Mat mRgba15;
    DetectSquares detectSquares = new DetectSquares();

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
		this.prepare(640, 480);
	}
	
	public void onCameraViewStopped() {
	}
	
	public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
		//mOpenCvCameraView.disableView();
//		Log.i(TAG, "Re-drawing image");
//		Mat mRgba = inputFrame.rgba();
//		Mat mGray = new Mat();
//        Imgproc.cvtColor(mRgba, mGray, Imgproc.COLOR_BGRA2GRAY);
//        Mat xyz = image.clone();
		//Utils.
		ArrayList<List<Point>> squares = new ArrayList<List<Point>>();
		detectSquares.find_squares(inputFrame.rgba(), squares);
		if (squares.size() > 0) {
			Log.i(TAG, "Square count is : " + squares.size());
			drawSquares(inputFrame.rgba(), squares);
		}
		
//		Mat blurred = new Mat(image.size(), CvType.CV_64FC4);
//		Imgproc.medianBlur(image, blurred, 9);
	        
		return inputFrame.rgba();
	}
	
	// the function draws all the squares in the image
	private void drawSquares(Mat image, ArrayList<List<Point>> squares)
	{
		List<MatOfPoint> polyline = null;
	    for(int i = 0; i < squares.size(); i++ )
	    {
	        Point point = squares.get(i).get(0);
	                
	        
	        polyline = new ArrayList<MatOfPoint>();
	        polyline.add(new MatOfPoint(point)); //TODO
	        
	        
	        
	        //polylines(image, &p, &n, 1, true, Scalar(0,255,0), 3, CV_AA);
	    }
	    
	    if (polyline != null) {
	    	Core.polylines(image, polyline, true, new Scalar(255, 0, 0));
	    	Imgproc.drawContours(image, polyline, -1, new Scalar(0,255,0), Core.FILLED);
	    }
	    
	    
//	    Highgui.im
//	    imshow(wndname, image);
	}
	
	private Bitmap ReadImage1(String fBitmap) {
//	    String root = Environment.getExternalStorageDirectory().toString();
//	    File myDir = new File(root + "/preprocessed");
	    File file = new File(fBitmap); //or any other format supported
	   // UIHelper.displayText(this, R.id.textView1, file.toString());
	    Bitmap bitmap = null; 

	    try {
	        BitmapFactory.Options options = new BitmapFactory.Options();
	        options.inPreferredConfig = Bitmap.Config.ARGB_8888;        
	        bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(),options); //This gets the image `       
	        return bitmap;

	        } catch (Exception e) {
	               e.printStackTrace();
	               //UIHelper.displayText(this, R.id.textView1, "Doesn't exist");
	        }

	        return bitmap;
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
	 
	 /* This method is to make the processor know the size of the frames that
	     * will be delivered via puzzleFrame.
	     * If the frames will be different size - then the result is unpredictable
	     */
	 public synchronized void prepare(int width, int height) {
		 mRgba15 = new Mat(height, width, CvType.CV_8UC4);
	 }
}
