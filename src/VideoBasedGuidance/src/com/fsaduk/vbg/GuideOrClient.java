package com.fsaduk.vbg;

        import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.fsaduk.vbg.R;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;

public class GuideOrClient extends Activity{
 public static boolean guideListUpdated = false;
	 RadioButton clientButton;
	RadioButton guideButton;
	
	// create an Array to store the list of guides
	public static ArrayList<String> listOfGuides = new ArrayList<String>();
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.guideorclient);
        
      // buttons to select guide or client
        clientButton = (RadioButton) findViewById(R.id.Client);
        guideButton = (RadioButton) findViewById(R.id.Guide);
        
        
        // When the user selects thathe wants to be a client, the list of active guides is displayed
 clientButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				guideListUpdated = false;
				sendUpdateStatusClient();			  
				listOfGuides=getListGuides();
				while(!guideListUpdated);
				// get intent and set to next activity
				 Intent i = new Intent(GuideOrClient.this, GuidesList.class);
			// start the activity to show the list of active guides
				startActivity(i);
			}
			
			// This  methos gets the list of guides and stores in the arraylist
			private ArrayList<String> getListGuides() {
				new AsyncTask<Void, Void, String>(){
				@Override
				     protected String doInBackground(Void... params) {			
					 JSONParser jsonParser1 = new JSONParser();
			    	 List<NameValuePair> params1 = new ArrayList<NameValuePair>();
			         params1.add(new BasicNameValuePair("username", Login.username));
					JSONObject json1 = jsonParser1.makeHttpRequest(
							URL.LIST_OF_GUIDES, "POST", params1);
					int numGuides = 0;
					try {
						 numGuides = json1.getInt("list");
					} catch (JSONException e1) {
						e1.printStackTrace();
					}
					listOfGuides.clear();
					for(int i=0;i<numGuides;i++){
						try {
							listOfGuides.add(i, (String) json1.get(String.valueOf(i))) ;
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
					guideListUpdated = true;
					return "done";
					}
				}.execute(null,null,null);
				return listOfGuides;
			}
			
			
			// send a JSOn message to the server to update the status of this user as client
			private void sendUpdateStatusClient() {
				new AsyncTask<Void, Void, String>(){
					@Override
					protected String doInBackground(Void... params) {
						 JSONParser jsonParser = new JSONParser();
				    	   List<NameValuePair> params1 = new ArrayList<NameValuePair>();
				           params1.add(new BasicNameValuePair("username", Login.username));
				           params1.add(new BasicNameValuePair("status", "C"));
				    	   
						JSONObject json = jsonParser.makeHttpRequest(
				                   URL.UPDATE_STATUS, "POST", params1);
						
						return "done";
					}				
				}.execute(null,null,null);		
		    }
		});
 
 
//send a JSOn message to the server to update the status of this user as guide
 guideButton.setOnClickListener(new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {		
			sendUpdateStatusGuide();
		}

		
		// send a JSOn message to the server to update the status of this user as Guide
		private void sendUpdateStatusGuide() {
			new AsyncTask<Void, Void, String>(){
				@Override
				protected String doInBackground(Void... params) {
					 JSONParser jsonParser = new JSONParser();
			    	   List<NameValuePair> params1 = new ArrayList<NameValuePair>();
			           params1.add(new BasicNameValuePair("username", Login.username));
			           params1.add(new BasicNameValuePair("status", "G"));
					JSONObject json = jsonParser.makeHttpRequest(
			                   URL.UPDATE_STATUS, "POST", params1);
					return "done";				
				}			
			}.execute(null,null,null);
		
		}
	
	});
     
    }      
 }
