package com.vbg.guide;

import java.util.ArrayList;
import java.util.List;

/*REFERENCES:

Firebase Database
	1.https://www.firebase.com/tutorial/#gettingstarted
	2.https://github.com/firebase/AndroidDrawing
	3.https://www.firebase.com/docs/android/examples.html
	
*/ 
// This class is used to take the segment which is written on the canvas and set various parameters for drawing such as paint color and all points which the segment has
public class Segment {

    private List<Point> points = new ArrayList<Point>();
    private int color;

    // Required default constructor for Firebase serialization / deserialization
    @SuppressWarnings("unused")
    private Segment() {
    }

    public Segment(int color) {
        this.color = color;
    }

    public void addPoint(int x, int y) {
        Point p = new Point(x, y);
        points.add(p);
    }

    public List<Point> getPoints() {
        return points;
    }

    public int getColor() {
        return color;
    }
}
