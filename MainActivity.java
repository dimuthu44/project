package com.example.myfirstapp;

import java.io.File;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;
import org.opencv.highgui.Highgui;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.view.Menu;

public class MainActivity extends Activity {
	private static final String  TAG = "Sample::Dimuthu::Activity";
	final Context context = this;
	
//	static {
//	    if (!OpenCVLoader.initDebug()) {
//	        // Handle initialization error
//	    }
//	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (!OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_6, this, mOpenCVCallBack))
		{
		    Log.e(TAG, "Cannot connect to OpenCV Manager");
		}
		else {
			Log.i(TAG, "Libraries loaded async");
		}
	}
	
	private BaseLoaderCallback  mOpenCVCallBack = new BaseLoaderCallback(this) {
	    @Override
	    public void onManagerConnected(int status) {
	        switch (status) {
	                case LoaderCallbackInterface.SUCCESS:
	                {
					   Mat Image = Highgui.imread("/mnt/sdcard/car.bmp");
					   if (Image == null) {
					       AlertDialog ad = new AlertDialog.Builder(context).create(); 
					       ad.setMessage("Fatal error: can't open /image.jpg!");  
						   
					   }
					   else {
						   Log.w(TAG, "1234##################### PARTY #################*&^#!*^*!#&^*&^");
					   }
	                } break;
	                default:
	                {
	                    super.onManagerConnected(status);
	                } break;
	            }
	    }
	    };
	
	@Override
	protected void onStop() {
	    Log.w(TAG, "App stopped");
	    super.onStop();
	}

	@Override
	protected void onDestroy() {
	    Log.w(TAG, "App destoryed");
	    super.onDestroy();
	}
	
	

//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		// Inflate the menu; this adds items to the action bar if it is present.
////		getMenuInflater().inflate(R.menu.main, menu);
//		return true;
//	}

}
