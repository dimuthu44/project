package com.hello;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfInt;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.imgproc.Imgproc;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class DetectSquares {
	
	private static final double PREDEFINED_AREA = 50000;

	/**
	 * http://stackoverflow.com/questions/18020455/java-opencv-tesseract-ocr-code-regocnition/18042054#18042054
	 * @param src
	 * @return
	 * @throws Exception
	 */
	public static MatOfPoint find(Mat src) {
        Mat blurred = src.clone();
        Imgproc.medianBlur(src, blurred, 9);

        Mat gray0 = new Mat(blurred.size(), CvType.CV_8U), gray = new Mat();

        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();

        List<Mat> blurredChannel = new ArrayList<Mat>();
        blurredChannel.add(blurred);
        List<Mat> gray0Channel = new ArrayList<Mat>();
        gray0Channel.add(gray0);

        MatOfPoint2f approxCurve;

        double maxArea = 0;
        int contourIdx = -1;

        // c=1 increase the performance, but doesn't catch A4 documents.
        for (int c = 0; c < 3; c++) { 
            int ch[] = {c, 0};
            Core.mixChannels(blurredChannel, gray0Channel, new MatOfInt(ch));

            int thresholdLevel = 1;
            for (int t = 0; t < thresholdLevel; t++) {
                if (t == 0) {
                    Imgproc.Canny(gray0, gray, 10, 20, 3, true); // true ?
                    Imgproc.dilate(gray, gray, new Mat(), new Point(-1, -1), 1); // 1 ?
                } else {
                    Imgproc.adaptiveThreshold(gray0, gray, thresholdLevel, Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C, Imgproc.THRESH_BINARY, (src.width() + src.height()) / 200, t);
                }

                Imgproc.findContours(gray, contours, new Mat(), Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);

                for (MatOfPoint contour : contours) {
                    
                    double area = Imgproc.contourArea(contour);
                    if (area < PREDEFINED_AREA)  {
                    	continue;
                    }

                    approxCurve = new MatOfPoint2f();
                    MatOfPoint2f temp = new MatOfPoint2f(contour.toArray());
                    Imgproc.approxPolyDP(temp, approxCurve, Imgproc.arcLength(temp, true) * 0.02, true);
                    
                    if (approxCurve.total() == 4 && area >= maxArea) {
                        double maxCosine = 0;

                        List<Point> curves = approxCurve.toList();
                        for (int j = 2; j < 5; j++)
                        {
                            double cosine = Math.abs(angle(curves.get(j % 4), curves.get(j - 2), curves.get(j - 1)));
                            maxCosine = Math.max(maxCosine, cosine);
                        }

                        if (maxCosine < 0.3) {
                            maxArea = area;
                            //Log.i("DIMUTHU::", "Contour area is " + maxArea);
                            contourIdx = contours.indexOf(contour);
                            //contours.set(maxId, getHull(contour));
                        }
                    }
                }
            }
        }

        if (contourIdx >= 0) {
        	return contours.get(contourIdx);
        }
        return null;
    }

	// helper function:
	// finds a cosine of angle between vectors
	// from pt0->pt1 and from pt0->pt2
	static double angle(Point pt1, Point pt2, Point pt0) {
		double dx1 = pt1.x - pt0.x;
		double dy1 = pt1.y - pt0.y;
		double dx2 = pt2.x - pt0.x;
		double dy2 = pt2.y - pt0.y;

		return (dx1 * dx2 + dy1 * dy2)
				/ Math.sqrt((dx1 * dx1 + dy1 * dy1) * (dx2 * dx2 + dy2 * dy2)
						+ 1e-10);
	}
}
