package com.vbg.guide;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;
import android.app.Activity;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.fsaduk.vbg.JSONParser;
import com.fsaduk.vbg.Login;
import com.fsaduk.vbg.R;
import com.fsaduk.vbg.URL;


/*
REFERENCES:

Firebase Database
	1.https://www.firebase.com/tutorial/#gettingstarted
	2.https://github.com/firebase/AndroidDrawing
	3.https://www.firebase.com/docs/android/examples.html
	
Streaming Video display:
	4.	http://code.tutsplus.com/tutorials/streaming-video-in-android-apps--cms-19888
	5.	http://developer.android.com/reference/java/net/URI.html
	
Display from RTSP link
	6.	http://code.tutsplus.com/tutorials/streaming-video-in-android-apps--cms-19888
	7.	http://stackoverflow.com/questions/19247772/android-4-1-rtsp-using-videoview-and-med
*/


// THis class is used for the Guide side working
// This class sets up the media player objext to receive the stream from the client 
// also it sets up the canvas to draw gestures and update it to the firebase URL
public class DrawingActivity extends Activity implements SurfaceHolder.Callback, OnPreparedListener {

	MediaPlayer mediaPlayer;  // used to play the stream
	SurfaceHolder surfaceHolder1;  // used as a holder to hold the player and the canvas
	SurfaceView playerSurfaceView;

    public static Activity activity;
    private DrawingView mDrawingView; // canvas view to drw gestures
    private Firebase mFirebaseRef; // Firebase database reference
    private ValueEventListener mConnectedListener;
    int i = 0;

    // creates and sets up the GUI for the guide to listenin to the incoming stream and to draw gestures on screen
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;
        Firebase.setAndroidContext(this);
		setContentView(R.layout.main);
		playerSurfaceView = (SurfaceView)findViewById(R.id.playersurface);
		 mFirebaseRef = new Firebase(URL.FIREBASE_CLIENT_URL);
	        mDrawingView = new DrawingView(this, mFirebaseRef);
	        mFirebaseRef.removeValue();
	        RelativeLayout layout = (RelativeLayout) findViewById(R.id.relLayout);
	        layout.addView(mDrawingView);
	    
        surfaceHolder1 = playerSurfaceView.getHolder();
        surfaceHolder1.addCallback(this);
        i++;
        URL.FIREBASE_CLIENT_URL += i;
		 mFirebaseRef.removeValue();	 
		mFirebaseRef = new Firebase(URL.FIREBASE_CLIENT_URL);
		  updateFirebaseView();
	        layout.removeView(mDrawingView);   
	        mDrawingView = new DrawingView( getApplicationContext(), mFirebaseRef);
	    layout.addView(mDrawingView);
	    
	    
	    // GUI parameters
        final EditText textToSend =(EditText) findViewById(R.id.TextToSend);
        final LinearLayout linL =(LinearLayout) findViewById(R.id.testLayout);
        final Button SendButton = (Button) findViewById(R.id.SendText);
        final Button clear = (Button) findViewById(R.id.Clear);
        SendButton.bringToFront();
        linL.bringToFront();
        textToSend.bringToFront();
        clear.bringToFront();
        
        
        // On clear button click, the canvas should be erased both at client and guide
        // In order to do this, we change the URL of the drawing canvas itself
        clear.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {		
				i++;
				URL.FIREBASE_CLIENT_URL = URL.FIREBASE_CLIENT_URL+i;  // changing the URL
				 mFirebaseRef.removeValue(); // removing the old URL
				 updateFirebaseView(); //updating the firebase
				mFirebaseRef = new Firebase(URL.FIREBASE_CLIENT_URL);		
				 RelativeLayout layout = (RelativeLayout) findViewById(R.id.relLayout);
			        layout.removeView(mDrawingView);
			        mFirebaseRef.removeValue();		        
			        mDrawingView = new DrawingView( getApplicationContext(), mFirebaseRef);
			        layout.addView(mDrawingView);
			        
			        
			        // bring all the widgets to front so that they are visible
			        linL.bringToFront();
				clear.bringToFront();
				 SendButton.bringToFront();
				 textToSend.bringToFront();	
			}

			
			//This function sends the updated firebase URL to the backend server which in turn notifies the client to change the URL it listens to
			public void updateFirebaseView() {
					new AsyncTask<Void, Void, String>(){			
						@Override
						protected String doInBackground(Void... params) {						
							 JSONParser jsonParser = new JSONParser();
					    	   List<NameValuePair> params1 = new ArrayList<NameValuePair>();
					           params1.add(new BasicNameValuePair("username", Login.username));
					           params1.add(new BasicNameValuePair("url", URL.FIREBASE_CLIENT_URL));
							
							JSONObject json = jsonParser.makeHttpRequest(
					                   URL.CHANGE_FIREBASE , "POST", params1);			
							return 	"done";				
						}						
					}.execute(null,null,null);	
			}
		});
        
        
        
       
      // On send button click, The guide sends the text message entered to the client
 SendButton.setOnClickListener(new View.OnClickListener() {
	String text;
	String clearText = " ";
			@Override
			public void onClick(View v) {
				 text= textToSend.getText().toString();
				textToSend.setText(clearText);	
				if(!text.equals(clearText))
				 sendText();			
			}
			private void sendText() {	
				new AsyncTask<Void, Void, String>(){			
					@Override
					protected String doInBackground(Void... params) {					
						 JSONParser jsonParser = new JSONParser();
				    	   List<NameValuePair> params1 = new ArrayList<NameValuePair>();
				    	   // username of guide
				           params1.add(new BasicNameValuePair("username", Login.username));
				         // text to send
				           params1.add(new BasicNameValuePair("text",text ));
						
						JSONObject json = jsonParser.makeHttpRequest(
				                   URL.TEXT_TO_SEND , "POST", params1);			
						return 	"done	";	
					}
				}.execute(null,null,null);		
			}
		});
    }
    
// The view updates the firebase URL
    private void updateFirebaseView() {
		new AsyncTask<Void, Void, String>(){
			@Override
			protected String doInBackground(Void... params) {
				 JSONParser jsonParser = new JSONParser();
		    	   List<NameValuePair> params1 = new ArrayList<NameValuePair>();
		           params1.add(new BasicNameValuePair("username", Login.username));
		           params1.add(new BasicNameValuePair("url", URL.FIREBASE_CLIENT_URL));
			
				JSONObject json = jsonParser.makeHttpRequest(
		                   URL.UPDATE_FIREBASE , "POST", params1);
				return 	"done";
			}
		}.execute(null,null,null);	
}

    // Onstart is called when first the activity is initialised
	@Override
    public void onStart() {
        super.onStart();
        // Set up a notification to let us know when we're connected or disconnected from the Firebase servers
        mConnectedListener = mFirebaseRef.getRoot().child(".info/connected").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean connected = (Boolean) dataSnapshot.getValue();
              
               
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                // No-operation
            }
        });
    }

	
	// Stops listening to the URL
    @Override
    public void onStop() {
        super.onStop();
        
        notifyStop();
        mFirebaseRef.getRoot().child(".info/connected").removeEventListener(mConnectedListener);
        mDrawingView.cleanup();
    }

    
    // This method sends a message to the backend to inform the client to stop the stream
    private void notifyStop() {
		new AsyncTask<Void, Void, String>(){
			@Override
			protected String doInBackground(Void... params) {			
				 JSONParser jsonParser = new JSONParser();
		    	   List<NameValuePair> params1 = new ArrayList<NameValuePair>();
		           params1.add(new BasicNameValuePair("username", Login.username));			
				JSONObject json = jsonParser.makeHttpRequest(
		                   URL.STOP_TO_CLIENT , "POST", params1);
				return 	"done";
			}
		}.execute(null,null,null);
	}




    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        return true;
    }
	@Override
	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
	}

	// When surface is created, the media player is created and various parameters are set
	@Override
	public void surfaceCreated(SurfaceHolder arg0) {

        try {
        	mediaPlayer = new MediaPlayer();
            mediaPlayer.setDisplay(surfaceHolder1);
			mediaPlayer.setDataSource(URL.videoSrc); // This gives the URL for the stream
			mediaPlayer.prepare();
			mediaPlayer.setOnPreparedListener(this);
			
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	
	
	@Override
	public void surfaceDestroyed(SurfaceHolder arg0) {		
	}

	@Override
	public void onPrepared(MediaPlayer mp) {
		mediaPlayer.start();
	}
}
