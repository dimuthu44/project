package com.hello;

import org.opencv.android.JavaCameraView;

import android.content.Context;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.Size;
import android.util.AttributeSet;

public class HelloViewer extends JavaCameraView implements PictureCallback {

	public HelloViewer(Context context, AttributeSet attrs) {
		super(context, attrs);
//		Camera.Parameters parameters = mCamera.getParameters();
//		parameters.set("orientation", "portrait");
//		parameters.set("rotation", 90);
//		mCamera.setParameters(parameters);
		// TODO Auto-generated constructor stub
		//if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) { mCamera.setDisplayOrientation(90); } 
	}

	@Override
	public void onPictureTaken(byte[] data, Camera camera) {
		// TODO Auto-generated method stub
		
	}
	
	 public void setResolution(Size resolution) {
	        disconnectCamera();
	        
	        mMaxHeight = 480; //resolution.height;
	        mMaxWidth = 640; //resolution.width;
	        connectCamera(getWidth(), getHeight());
	    }

	public Size getResolution() {
		return mCamera.getParameters().getPreviewSize();
		
	}


}
