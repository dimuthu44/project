package com.hello;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.hardware.Camera.Size;
import android.os.Bundle;
import android.os.Environment;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.Toast;

public class HelloOpenCvActivity extends Activity implements
		CvCameraViewListener2, OnTouchListener, TextToSpeech.OnInitListener {

	protected static final String TAG = "DIMUTHU::";
	HelloViewer mOpenCvCameraView;
	private TextToSpeech mTts;
	private static final int MY_DATA_CHECK_CODE = 1234;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.opencv_layout);

		// setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
		mOpenCvCameraView = (HelloViewer) findViewById(R.id.HelloOpenCvView);
		mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
		mOpenCvCameraView.setCvCameraViewListener(this);

		// Fire off an intent to check if a TTS engine is installed
		Intent checkIntent = new Intent();
		checkIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
		startActivityForResult(checkIntent, MY_DATA_CHECK_CODE);
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
		if (mTts != null)
        {
            mTts.stop();
            mTts.shutdown();
        }
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

		// List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
		// try {
		// contours.add(DetectSquares.find(mRgba));
		// if (contours.get(0) != null) {
		// Imgproc.drawContours(mRgba, contours, -1/*TODO*/, new Scalar(0, 255,
		// 0), 4);
		//
		// // Open in different audio thread,
		// try {
		// // beep sound
		// Uri notification =
		// RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
		// Ringtone r = RingtoneManager.getRingtone(getApplicationContext(),
		// notification);
		// r.play();
		// } catch (Exception e) {
		//
		// }
		//
		// // take picture
		// }
		// else {
		// // kill the audio thread
		// }
		// }
		// catch(Exception exc) {
		// Log.e(TAG, "Error occured" + exc.getMessage());
		// }

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
				Log.i(TAG, "OpenCV loaded successfully");
				mOpenCvCameraView.enableView();
				mOpenCvCameraView.setOnTouchListener(HelloOpenCvActivity.this);
			}
				break;
			default: {
				super.onManagerConnected(status);
			}
				break;
			}
		}
	};

	// protected void startCameraActivity() {
	// File file = new File(_path);
	// Uri outputFileUri = Uri.fromFile(file);
	//
	// final Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
	// intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
	//
	// startActivityForResult(intent, 0);
	// }
	//
	@Override
	public void onResume() {
		super.onResume();
		OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_6, this,
				mLoaderCallback);
	}

	@SuppressLint("SimpleDateFormat")
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		Log.i(TAG, "onTouch event");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
		String currentDateandTime = sdf.format(new Date());
		String fileName = Environment.getExternalStorageDirectory().getPath()
				+ "/project/sample_picture_" + currentDateandTime + ".jpg";

		mOpenCvCameraView.takePicture(fileName);
		Toast.makeText(this, fileName + " saved", Toast.LENGTH_SHORT).show();

		return false;
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == MY_DATA_CHECK_CODE)
        {
            if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS)
            {
                // success, create the TTS instance
                mTts = new TextToSpeech(this, this);
            }
            else
            {
                // missing data, install it
                Intent installIntent = new Intent();
                installIntent.setAction(
                        TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                startActivity(installIntent);
            }
        }
    }
	
	@Override
	public void onInit(int arg0) {
		mTts.speak("Hello folks, welcome to my little demo on Text To Speech.",
                TextToSpeech.QUEUE_FLUSH,  // Drop all pending entries in the playback queue.
                null);

	}
}
