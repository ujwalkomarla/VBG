package com.fsaduk.vbg;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.vbg.guide.Point;
import com.vbg.guide.Segment;

import java.util.HashSet;
import java.util.List;
import java.util.Set;


/*
REFERENCES:
	1.https://www.firebase.com/tutorial/#gettingstarted
	2.https://github.com/firebase/AndroidDrawing
	3.https://www.firebase.com/docs/android/examples.html
*/


// This class describes what to draw on the client's screen on top of the video stream
public class ClientDrawingView extends View {
	
	
	static int max = 10000;
    private static final int PIXEL_SIZE = 8;  // set the pixel size to draw the gesture on the screen
    
    
    public int width;
    public int height;
    
    private Paint mPaint;
    private Canvas mBuffer;
    private Bitmap mBitmap;
    private Paint mBitmapPaint;
    private Firebase mFirebaseRef;
    private ChildEventListener mListener;
    
    private Path mPath;
    private Set<String> mOutstandingSegments;
    private Path mChildPath = new Path();
    
    // this sets the screen resolution 
    static  int screeWidth ;
    static int screenHeight;

    public ClientDrawingView(Context context, Firebase ref) {
        super(context);

        
        
        // Get the actual screen resolution of the phone
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        android.graphics.Point size =new android.graphics.Point();
        display.getSize(size);
      ClientDrawingView.screeWidth  = size.x;
      ClientDrawingView.screenHeight = size.y;
      
        mOutstandingSegments = new HashSet<String>();
        mPath = new Path();
        this.mFirebaseRef = ref;

        
        /*add the firebase event listener, 
        so that whenever a guide draws on the screen, it gets updated at the client's phone
         */        
        
        mListener = ref.addChildEventListener(new ChildEventListener() {
       
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                String name = dataSnapshot.getKey();
                // To prevent lag, we draw our own segments as they are created. As a result, we need to check to make
                // sure this event is a segment drawn by another user before we draw it
                if (!mOutstandingSegments.contains(name)) {
                    // Deserialize the data into our Segment class
                    Segment segment = dataSnapshot.getValue(Segment.class);
                    drawSegment(segment, paintFromColor(segment.getColor()));
                
                    invalidate();
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                // No-operation
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
            	invalidate();
                // No-operaiotn
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                // No-operation
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                // No-operation
            }
        });

// set the canvas parameters
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(0xFFFF0000);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(40);
        mBitmapPaint = new Paint(Paint.DITHER_FLAG);
    }

    
 /*This removes the event listener to the Firebase URL, 
    so the client stops listening to ht eURL for changes when this method is called
    */
	public void cleanup() {
        mFirebaseRef.removeEventListener(mListener);
    }

    public void setColor(int color) {
        mPaint.setColor(color);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldW, int oldH) {
        super.onSizeChanged(w, h, oldW, oldH);
width = w;
height = h;

// Set the parameters of the image that is to be drawn on the screen of the client
        mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        mBitmap.eraseColor(Color.TRANSPARENT);
        mBuffer = new Canvas(mBitmap);  
    }

    
    // set the path and the color of the stroke when a new new child is added to the firebase URL
    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(0x00AAAAAA);
      canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);
        canvas.drawPath(mPath, mPaint);
        
    }

    private Paint paintFromColor(int color) {
        Paint p = new Paint();
        p.setAntiAlias(true);
        p.setDither(true);
        p.setColor(color);
        p.setStyle(Paint.Style.STROKE);
        return p;
    }

    
     /*This method draws on the canvas.It selects the appropriate pixel values and adjusts it to the screen 
    resolution of the phone and draws on the screen*/
    
    private void drawSegment(Segment segment, Paint paint) {
        mChildPath.reset();
        
        // get all the changes in Points from the firebase URL
        List<Point> points = segment.getPoints();
        Point current = points.get(0);
        mChildPath.moveTo((current.x *(ClientDrawingView.screeWidth)* PIXEL_SIZE)/ClientDrawingView.max, (current.y *ClientDrawingView.screenHeight* PIXEL_SIZE)/ClientDrawingView.max);
        Point next = null;
        for (int i = 1; i < points.size(); ++i) {
            next = points.get(i);
            mChildPath.quadTo((current.x *ClientDrawingView.screeWidth* PIXEL_SIZE)/ClientDrawingView.max, (current.y *ClientDrawingView.screenHeight* PIXEL_SIZE)/ClientDrawingView.max, (((next.x + current.x) *ClientDrawingView.screeWidth* PIXEL_SIZE) / 2)/ClientDrawingView.max, (((next.y + current.y)*ClientDrawingView.screenHeight* PIXEL_SIZE) / 2)/ClientDrawingView.max);
            current = next;
        }
        if (next != null) {
            mChildPath.lineTo(next.x*ClientDrawingView.screeWidth * PIXEL_SIZE/ClientDrawingView.max, next.y *ClientDrawingView.screenHeight* PIXEL_SIZE/ClientDrawingView.max);
        }
        mBuffer.drawPath(mChildPath, paint);
    }
}
