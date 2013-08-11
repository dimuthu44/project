package com.hello;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfInt;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DetectSquares {

	int thresh = 50, N = 11;
	static Mat blurred;
	static Mat gray;
	static Mat gray0;

	static MatOfInt fromTo;
	static Point point;
	static List<MatOfPoint> contours;
	static MatOfPoint2f approx;
	static int threshold_level = 2;

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

	public static void prepare() {
		blurred = new Mat();
		gray = new Mat();
		contours = new ArrayList<MatOfPoint>();
		point = new Point(-1, -1);
		approx = new MatOfPoint2f();

	}

	/**
	 * http://stackoverflow.com/questions/8667818/opencv-c-obj-c-detecting-a-sheet-of-paper-square-detection/8863060#8863060%29Regarding
	 * @param image
	 * @param squares
	 */
	public static void findSquares(Mat image, ArrayList<List<Point>> squares) {
		// blur will enhance edge detection
		Imgproc.medianBlur(image, blurred, 9);

		gray0 = new Mat(blurred.size(), CvType.CV_8U);

		// find squares in every color plane of the image
		for (int c = 0; c < 3; c++) {
			int ch[] = { c, 0 };

			List<Mat> src = new ArrayList<Mat>();
			src.add(blurred);
			List<Mat> dest = new ArrayList<Mat>();
			dest.add(gray0);

			fromTo = new MatOfInt(ch);
			Core.mixChannels(src, dest, fromTo);

			for (int l = 0; l < threshold_level; l++) {
				// Use Canny instead of zero threshold level! Canny helps to
				// catch squares with gradient shading
				if (l == 0) {
					Imgproc.Canny(gray0, gray, 10, 20, 3, false);

					// Dilate helps to remove potential holes between edge segments
					Imgproc.dilate(gray, gray, new Mat(), point, 1);
				} else {
					int i = (l + 1) * 255 / threshold_level;
					gray = gray0.rows() >= i ? gray0 : gray; // TODO
				}

				// Find contours and store them in a list.
				Imgproc.findContours(gray, contours, new Mat(),
						Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);

				// Test contours
				findAndPopulateSquares(squares, contours);

				// drawContours(image, contours);
			}
		}
	}
	
	/**
	 * http://stackoverflow.com/questions/18020455/java-opencv-tesseract-ocr-code-regocnition/18042054#18042054
	 * @param src
	 * @return
	 * @throws Exception
	 */
	public static MatOfPoint find(Mat src) throws Exception {
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
	        int maxId = -1;

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
	                	//Log.i("DIMUTHU::", "Contour size is : " + contours.size());
	                    MatOfPoint2f temp = new MatOfPoint2f(contour.toArray());

	                    double area = Imgproc.contourArea(contour);
	                    approxCurve = new MatOfPoint2f();
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
	                            maxId = contours.indexOf(contour);
	                            //contours.set(maxId, getHull(contour));
	                        }
	                    }
	                }
	            }
	        }

	        if (maxId >= 0) {
	        	MatOfPoint mmm = contours.get(maxId);
	            //Imgproc.drawContours(src, mmm, maxId, new Scalar(255, 0, 0, .8), 8);
	            
	            return mmm;
	        }
	        return null;
	    }

	private static void findAndPopulateSquares(ArrayList<List<Point>> squares, List<MatOfPoint> contours) {
		
		for (int i = 0; i < contours.size(); i++) {
			// approximate contour with accuracy proportional to the contour perimeter.
			MatOfPoint mapOfPoint = contours.get(i);
			MatOfPoint2f mapOfPoint2f = new MatOfPoint2f();

			mapOfPoint2f.fromArray(mapOfPoint.toArray());

			double epilson = Imgproc.arcLength(mapOfPoint2f, true);
			epilson *= 0.02;
			Imgproc.approxPolyDP(mapOfPoint2f, approx, epilson, true);

			// Note: absolute value of an area is used because
			// area may be positive or negative - in accordance with the
			// contour orientation

			MatOfPoint approx_mapofpoint = new MatOfPoint(approx.toArray());

			if (approx.size().area() == 4
					&& Math.abs(Imgproc.contourArea(approx)) > 1000
					&& Imgproc.isContourConvex(approx_mapofpoint)) {
				double maxCosine = 0;

				for (int j = 2; j < 5; j++) {
					double cosine = Math.abs(angle(approx.toArray()[j % 4],
							approx.toArray()[j - 2], approx.toArray()[j - 1]));
					maxCosine = Math.max(maxCosine, cosine);
				}

				if (maxCosine < 0.3) {
					squares.add(approx.toList());
				}
			}
		}
	}

	private void drawContours(Mat image, List<MatOfPoint> contours) {
		Imgproc.drawContours(image, contours, -1, new Scalar(0, 255, 0));
	}
}
