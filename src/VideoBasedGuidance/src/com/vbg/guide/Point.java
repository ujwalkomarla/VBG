package com.vbg.guide;



// This class can be used to get the point at which to draw on the canvas 
public class Point {
    public int x;
    public int y;

    // Required default constructor for Firebase serialization / deserialization
    @SuppressWarnings("unused")
    private Point() {
    }
    
    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
