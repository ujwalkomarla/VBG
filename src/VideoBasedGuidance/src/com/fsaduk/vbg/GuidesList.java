package com.fsaduk.vbg;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
 
/*
References:
1. http://windrealm.org/tutorials/android/android-listview.php
2. http://developer.android.com/guide/topics/ui/layout/listview.html
	*/
	
//THis activity is used by the client to get the list of active guides

public class GuidesList extends Activity {
  
	
	// Creates a list to display the list of guides
  private ListView mainListView ;
  private ArrayAdapter<String> listAdapter ;
  
  // used to store the user name of the guide which is selected
  public  static String  guideUsername ;
  
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.guidelist);
    
    // Find the ListView resource. 
    mainListView = (ListView) findViewById( R.id.mainListView );
    listAdapter = new ArrayAdapter<String>(this, R.layout.simplerow);
    

    // Gets the list of all guides from the server
    for(int i=0;i<GuideOrClient.listOfGuides.size();i++){
    	
    	listAdapter.add(GuideOrClient.listOfGuides.get(i));
    }
    
    // Set the ArrayAdapter as the ListView's adapter.
    mainListView.setAdapter( listAdapter );      
  mainListView.setOnItemClickListener(new OnItemClickListener() {
	  
      @Override
      public void onItemClick(AdapterView<?> parent, View view,
        int position, long id) {
         
       guideUsername    = (String) mainListView.getItemAtPosition(position);     
       connectToguide();
       Intent i = new Intent(GuidesList.this, ClientStreaming.class);
       finish();
       
       // starts the streaming activity to the selected guide
       startActivity(i);
      }

      
      // sends a JSON message to the server to connect to the selected guide
	private void connectToguide() {
		new AsyncTask<Void, Void, String>(){
			@Override
			protected String doInBackground(Void... params) {
				 JSONParser jsonParser = new JSONParser();
		    	   List<NameValuePair> params1 = new ArrayList<NameValuePair>();
		           params1.add(new BasicNameValuePair("username", Login.username));
		           params1.add(new BasicNameValuePair("guidename",guideUsername ));
		           params1.add(new BasicNameValuePair("ip",GCMmsgHandler.hostIP ));
		           
				// sends a HTTP request to the server with parameters required to connect to guide
				JSONObject json = jsonParser.makeHttpRequest(
		                   URL.CONNECT_TO_GUIDE , "POST", params1);
			
				return 	"done" ;
			}
			
		}.execute(null,null,null);
		
	}
});    
  
  
  }
}