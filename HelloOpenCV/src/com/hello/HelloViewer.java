package com.hello;

import java.io.FileOutputStream;

import org.opencv.android.JavaCameraView;

import android.content.Context;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.Size;
import android.util.AttributeSet;
import android.util.Log;

public class HelloViewer extends JavaCameraView implements PictureCallback {

	protected static final String TAG = "DIMUTHU::";
	private String mPictureFileName;

	public HelloViewer(Context context, AttributeSet attrs) {
		super(context, attrs);
//		Camera.Parameters parameters = mCamera.getParameters();
//		parameters.set("orientation", "portrait");
//		parameters.set("rotation", 90);
//		mCamera.setParameters(parameters);
		// TODO Auto-generated constructor stub
		//if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) { mCamera.setDisplayOrientation(90); } 
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
	
	public void takePicture(final String fileName) {
		Log.i(TAG, "Taking picture");
        this.mPictureFileName = fileName;
        // Postview and jpeg are sent in the same buffers if the queue is not empty when performing a capture.
        // Clear up buffers to avoid mCamera.takePicture to be stuck because of a memory issue
        mCamera.setPreviewCallback(null);

        // PictureCallback is implemented by the current class
        mCamera.takePicture(null, null, this);
    }
	
	@Override
    public void onPictureTaken(byte[] data, Camera camera) {
        Log.i(TAG, "Saving a bitmap to file");
        // The camera preview was automatically stopped. Start it again.
        mCamera.startPreview();
        mCamera.setPreviewCallback(this);

        // Write the image in a file (in jpeg format)
        try {
            FileOutputStream fos = new FileOutputStream(mPictureFileName);

            fos.write(data);
            fos.close();

        } catch (java.io.IOException e) {
            Log.e(TAG, "Exception in photoCallback", e);
        }

    }


}
