package com.fsaduk.vbg;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.fsaduk.vbg.R;
import net.majorkernelpanic.streaming.SessionBuilder;
import net.majorkernelpanic.streaming.gl.SurfaceView;
import net.majorkernelpanic.streaming.rtsp.RtspServer;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;


/*
REFERENCES
Streaming Video display:
	1.	http://code.tutsplus.com/tutorials/streaming-video-in-android-apps--cms-19888
	2.	http://developer.android.com/reference/java/net/URI.html
Display from RTSP link
	3.	http://code.tutsplus.com/tutorials/streaming-video-in-android-apps--cms-19888
	4.	http://stackoverflow.com/questions/19247772/android-4-1-rtsp-using-videoview-and-med
*/


public class ClientStreaming extends Activity implements SurfaceHolder.Callback, OnPreparedListener {

SurfaceHolder surfaceHolder1;
private SurfaceView mSurfaceView;
private static final int COLOR_MENU_ID = Menu.FIRST;
static ClientDrawingView mDrawingView;
static Firebase mFirebaseRef;
private ValueEventListener mConnectedListener;
static  RelativeLayout layout;
public static Activity activity;
@Override
protected void onCreate(Bundle savedInstanceState) {
super.onCreate(savedInstanceState);
activity = this;
Firebase.setAndroidContext(this);
getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
setContentView(R.layout.client);
setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
mSurfaceView = (SurfaceView) findViewById(R.id.surface);
mFirebaseRef = new Firebase(URL.FIREBASE_URL);
mDrawingView = new ClientDrawingView(this, mFirebaseRef); 
layout = (RelativeLayout) findViewById(R.id.clientLayout);
layout.addView(mDrawingView);  
surfaceHolder1 = mSurfaceView.getHolder();
surfaceHolder1.addCallback(this);
	        
		// Sets the port of the RTSP server to 1234
		Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
		editor.putString(RtspServer.KEY_PORT, String.valueOf(1234));
		editor.commit();
		// Configures the SessionBuilder
		SessionBuilder.getInstance()
		.setSurfaceView(mSurfaceView)
		.setPreviewOrientation(0)
		.setContext(getApplicationContext())
		.setAudioEncoder(SessionBuilder.AUDIO_NONE) // No Audio played
		.setVideoEncoder(SessionBuilder.VIDEO_H264);	// Video encoded with H264 format

		// Starts the RTSP server
		this.startService(new Intent(this,RtspServer.class));

		Button  stopStream = (Button)findViewById(R.id.StopStream);
		stopStream.bringToFront();
		
		
		
		stopStream.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {				
					notifyToGuide();
					getApplicationContext().stopService(new Intent(getApplicationContext(),RtspServer.class));
					finish();	
				}
						
				private void notifyToGuide() {
					new AsyncTask<Void, Void, String>(){
						@Override
						protected String doInBackground(Void... params) {						
							 JSONParser jsonParser = new JSONParser();
					    	   List<NameValuePair> params1 = new ArrayList<NameValuePair>();
					           params1.add(new BasicNameValuePair("username", Login.username));
							jsonParser.makeHttpRequest(
					                   URL.STOP_TO_GUIDE , "POST", params1);				
							return 	"done	";			
						}					
					}.execute(null,null,null);				
				}
		});	
	}
	
	
	
	
	   @Override
	    public void onStart() {
	        super.onStart();
	        // Set up a notification to let us know when we're connected or disconnected from the Firebase servers
	        mConnectedListener = mFirebaseRef.getRoot().child(".info/connected").addValueEventListener(new ValueEventListener() {
	            @Override
	            public void onDataChange(DataSnapshot dataSnapshot) {
	                dataSnapshot.getValue();
	            }
	            @Override
	            public void onCancelled(FirebaseError firebaseError) {
	            }
	        });
	    }

	    @Override
	    public void onStop() {
	        super.onStop();
	        // Clean up our listener so we don't have it attached twice.
	        //mFirebaseRef.onDisconnect().removeValue();
	        mFirebaseRef.getRoot().child(".info/connected").removeEventListener(mConnectedListener);
	        mDrawingView.cleanup();
	    }

	  
	    @Override
	    public boolean onCreateOptionsMenu(Menu menu) {
	        super.onCreateOptionsMenu(menu);

	        menu.add(0, COLOR_MENU_ID, 0, "Color").setShortcut('3', 'c');
	        return true;
	    }

	    @Override
	    public boolean onPrepareOptionsMenu(Menu menu) {
	        super.onPrepareOptionsMenu(menu);
	        return true;
	    }

	    @Override
	    public boolean onOptionsItemSelected(MenuItem item) {
	        if (item.getItemId() == COLOR_MENU_ID) {
	          
	            return true;
	        } else {
	            return super.onOptionsItemSelected(item);
	        }
	    }
	    
/*	
	When the child is added or deleted to the firebase URL,
	the client updates this changes to its canvas*/
	public void updateView() {
		mFirebaseRef = new Firebase(URL.FIREBASE_URL);
	       layout.removeView(mDrawingView);
	       mDrawingView = new ClientDrawingView( getApplicationContext(), mFirebaseRef);
	       layout.addView(mDrawingView);
	}
	
	
	
	
	//<------------------------Not implemented functions(Not used) from the interface--------------------->
	@Override
	public void onPrepared(MediaPlayer mp) {	// No operation
	}
	@Override
	public void surfaceCreated(SurfaceHolder holder) {	// No operation
	}
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {	// No operation
	}
	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {	// No operation
	}

	
}
