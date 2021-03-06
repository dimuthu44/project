package com.example.AndroidTest;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfInt;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DetectSquares {
    public static void main (String args[]) {
        File file = new File("L:\\Rc.jpg");
        if (file.exists()) {
            Mat m = Highgui.imread(file.getAbsolutePath());
        }

//        Mat image = OpenCVUtils.readImage("L:\\Rc.jpg", 0);

//        if (m.get)
//        {
//            showToast("Height: " + m.height() + " Width: " + m.width());
//        }
//        else
//        {
//            // print error and abort execution
//        }
//        image.s
    }

    int thresh = 50, N = 11;

    // helper function:
// finds a cosine of angle between vectors
// from pt0->pt1 and from pt0->pt2
    static double angle(Point pt1, Point pt2, Point pt0) {
        double dx1 = pt1.x - pt0.x;
        double dy1 = pt1.y - pt0.y;
        double dx2 = pt2.x - pt0.x;
        double dy2 = pt2.y - pt0.y;

        return (dx1 * dx2 + dy1 * dy2) / Math.sqrt((dx1 * dx1 + dy1 * dy1) * (dx2 * dx2 + dy2 * dy2) + 1e-10);
    }

    public void find_squares(Mat image, ArrayList<List<Point>> squares) {
        Imgproc img = new Imgproc();

        // blur will enhance edge detection
        org.opencv.core.Mat blurred = new org.opencv.core.Mat();
        Imgproc.medianBlur(image, blurred, 9);

        Mat gray0 = new Mat(blurred.size(), CvType.CV_8U);
        Mat gray = new Mat();
//        Vector<Vector<Point> > contours;
        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();

        // find squares in every color plane of the image
        for (int c = 0; c < 3; c++) {
            int ch[] = {c, 0};
//            Core.mixChannels(blurred, 1, gray0, 1, ch, 1);
            List<Mat> src = new ArrayList<Mat>();
            src.add(blurred);
            List<Mat> dest = new ArrayList<Mat>();
            dest.add(gray0);

            MatOfInt a = new MatOfInt(ch);
            Core.mixChannels(src, dest, a);

            // try several threshold levels
            final int threshold_level = 2;
            for (int l = 0; l < threshold_level; l++) {
                // Use Canny instead of zero threshold level!
                // Canny helps to catch squares with gradient shading
                if (l == 0) {
                    Imgproc.Canny(gray0, gray, 10, 20, 3, false);

                    // Dilate helps to remove potential holes between edge segments
                    Point point = new Point(-1, -1);
                    Imgproc.dilate(gray, gray, new Mat(), point, 1);
                } else {
                    // TODO
                    int i = (l + 1) * 255 / threshold_level;
//                    gray = gray0 >= i;

                }

                // Find contours and store them in a list. //TODO
                Imgproc.findContours(gray, contours, new Mat(), Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);

                // Test contours
                MatOfPoint2f approx = new MatOfPoint2f();
                for (int i = 0; i < contours.size(); i++) {
                    // approximate contour with accuracy proportional
                    // to the contour perimeter
                    double epilson = Imgproc.arcLength(new MatOfPoint2f(contours.get(i)), true);
                    epilson *= 0.02;
                    Imgproc.approxPolyDP(new MatOfPoint2f(contours.get(i)), approx, epilson, true);

                    // Note: absolute value of an area is used because
                    // area may be positive or negative - in accordance with the
                    // contour orientation
//                    Mat mmm = new Mat();
                    MatOfPoint approx_mapofpoint = new MatOfPoint(approx.toArray());

                    if (approx.size().area() == 4 &&
                            Math.abs(Imgproc.contourArea(approx)) > 1000 &&
                            Imgproc.isContourConvex(approx_mapofpoint)) {
                        double maxCosine = 0;

                        for (int j = 2; j < 5; j++) {
                            double cosine = Math.abs(angle(approx.toArray()[j % 4], approx.toArray()[j - 2], approx.toArray()[j - 1]));
                            maxCosine = Math.max(maxCosine, cosine);
                        }

                        if (maxCosine < 0.3) {
                            squares.add(approx.toList());
                        }
                    }
                }
            }
        }

    }
}
