package com.fsaduk.vbg;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.majorkernelpanic.streaming.rtsp.RtspServer;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.firebase.client.Firebase;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.vbg.guide.DrawingActivity;

import android.app.IntentService;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;


/*
REFERENCES:
1.	https://developer.android.com/google/gcm/index.html
2.	http://captechconsulting.com/blog/antoine-campbell/google-cloud-messaging-cloud-connection-server-tutorial
3.http://javapapers.com/android/google-cloud-messaging-gcm-for-android-and-push-notifications/
4.http://fryerblog.com/post/30057483199/implementing-push-notifications-with-gcm
5.http://hmkcode.com/android-google-cloud-messaging-tutorial/
6.https://github.com/mattg888/GCM-PHP-Server-Push-Message
*/

public class GCMmsgHandler extends IntentService {
	 private static Pattern VALID_IPV4_PATTERN = null;
	  public static String hostIP;
     String mes;
	 String status = null;
     private Handler handler;
     public GCMmsgHandler() {
     super("GCMmsgHandler");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        handler = new Handler();
    }
    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        String messageType = gcm.getMessageType(intent);
       mes = extras.getString("type");      
       	// for guide
       if(mes!=null){
    	   

    	  /* Ping is received by the server to the guide. 
    	   If the guide is active, it sends a reply to the server back . Now the server updates the active guides*/
       if(mes.equals("ping")){
			 hostIP= getLocalIpAddress(false);
    	   JSONParser jsonParser = new JSONParser();
    	   List<NameValuePair> params = new ArrayList<NameValuePair>();
           params.add(new BasicNameValuePair("username", Login.username));
    	   JSONObject json = jsonParser.makeHttpRequest(
                   URL.PING_URL, "POST", params);
    	   try {
    		    status=json.getString("message");
		} catch (JSONException e) {	
			e.printStackTrace();
		}   
    }
     
       
       
       
       if(mes.equals("connectTo")){
    	Login.addrOfClient=   extras.getString("IP");
    	sendguideIPaddress();
    	Intent i = new Intent(getApplicationContext(), DrawingActivity.class);
    	i.putExtras(intent.getExtras());
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i); // change the context name.
       }
       
       
       
       
       // This message gets the guide IP address
       if(mes.equals("guideIP")){
    	 String guideip=  extras.getString("IP");
       }

       
       
      /* THis message gets the text message which is sent by the guide to the client, 
       the client now toasts this message to the user*/
       if(mes.equals("text")){ 
    	   final String text=  extras.getString("text");
    	   handler.post(new Runnable() {
               public void run() {
                   Toast.makeText(getApplicationContext(),text , Toast.LENGTH_LONG).show();
               }
            });
    	   
       }
       
       
       
       
      /* THis message gets the URL of the firebase database, 
       now the cleint conects to this URL and listens to the server and updates its canvas based on the updates*/

 if(mes.equals("url")){ 
	 URL.FIREBASE_URL=  extras.getString("url");
	 handler.post(new Runnable() {
         public void run() {	 
        	 ClientStreaming.mFirebaseRef = new Firebase(URL.FIREBASE_URL);
        	 ClientStreaming. layout.removeView(ClientStreaming.mDrawingView);
        	 ClientStreaming.mDrawingView = new ClientDrawingView( getApplicationContext(), ClientStreaming.mFirebaseRef);
        	 ClientStreaming.layout.addView(ClientStreaming.mDrawingView);
         }
      });
  }
 
 
 
 /*  
 THis message is received by the guide when the client stops the streaming activity,
 thus now the guide closes the activity and stops listening to the stream
 */
 if(mes.equals("stopListening")){
	 handler.post(new Runnable() {
         public void run() {   	 
        	DrawingActivity.activity.finish();	 
         }
      });
 }
 
 
/* 
 this message is received by the client when the guide has stopped the activity,
thus now the client closes the stream and goes back to previous activity
*/
 if(mes.equals("stopStreaming")){
	 handler.post(new Runnable() {
         public void run() {  	 
        	 getApplicationContext().stopService(new Intent(getApplicationContext(),RtspServer.class));
        	 ClientStreaming.activity.finish();      	 
         }
      });
 }   
       }
        GCMbroadcastRecv.completeWakefulIntent(intent);
    }

    
    // this function is used to send the IP address of the guide to the server 
    //so that the connection with the client can be established
    private void sendguideIPaddress() {
		new AsyncTask<Void, Void, String>(){	
			@Override
			protected String doInBackground(Void... params) {
				 JSONParser jsonParser = new JSONParser();
		    	   List<NameValuePair> params1 = new ArrayList<NameValuePair>();
		           params1.add(new BasicNameValuePair("guidename", Login.username));
		           params1.add(new BasicNameValuePair("guideip",GCMmsgHandler.hostIP));
		           
				JSONObject json = jsonParser.makeHttpRequest(
						URL.GUIDE_IP, "POST", params1);
				return "done";
			}			
		}.execute(null,null,null);	
	}
    
    
	public void showToast(){
        handler.post(new Runnable() {
            public void run() {
                Toast.makeText(getApplicationContext(),mes , Toast.LENGTH_LONG).show();
            }
         });

    }
 
	
	
	// This function gets the IP address of the user's phone . This IP address is used for connecting to other users
	public static String getLocalIpAddress(boolean removeIPv6) {
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
					InetAddress inetAddress = enumIpAddr.nextElement();
					if (inetAddress.isSiteLocalAddress() &&
							!inetAddress.isAnyLocalAddress() &&
							(!removeIPv6 || isIpv4Address(inetAddress.getHostAddress().toString())) ) {
						return inetAddress.getHostAddress().toString();
					}
				}
			}
		} catch (SocketException ignore) {}
		return null;
	}
	public static boolean isIpv4Address(String ipAddress) {
		final String ipv4Pattern = "(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.){3}([01]?\\d\\d?|2[0-4]\\d|25[0-5])";
		VALID_IPV4_PATTERN = Pattern.compile(ipv4Pattern, Pattern.CASE_INSENSITIVE);
		Matcher m1 = VALID_IPV4_PATTERN.matcher(ipAddress);
		return m1.matches();
	}
    
    
}
