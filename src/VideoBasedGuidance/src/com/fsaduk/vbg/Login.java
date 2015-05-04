package com.fsaduk.vbg;
/*
 References:
1.  http://www.mybringback.com/android-sdk/12924/android-tutorial-using-remote-databases-php-and-mysql-part-1/
2.  http://www.javacodegeeks.com/2013/10/android-json-tutorial-create-and-parse-json-data.html
3.  http://www.tutorialspoint.com/json/json_php_example.htm
4.  http://www.survivingwithandroid.com/2013/05/build-weather-app-json-http-android.html
*/

  import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.gcm.GoogleCloudMessaging;

        import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Login extends Activity implements OnClickListener{
public static String username;
public static String addrOfClient;
	
	    GoogleCloudMessaging gcm;
	    String regid;
	    
	// Variables related to the GUI
    private EditText user, pass;
    private Button mSubmit, mRegister;
    Button btnRegId;
    EditText etRegId;

    // Progress Dialog
    private ProgressDialog pDialog;

    
    // JSON parser class
    JSONParser jsonParser = new JSONParser();
    
    //JSON element ids from repsonse of php script:
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        //setup input fields
        user = (EditText)findViewById(R.id.username);
        pass = (EditText)findViewById(R.id.password);

        //setup buttons
        mSubmit = (Button)findViewById(R.id.login);
        mRegister = (Button)findViewById(R.id.register);

        //register listeners
        mSubmit.setOnClickListener(this);
        mRegister.setOnClickListener(this);

    }
    
    
    //When the application is closed, the onDestroy method is called
    @Override
    protected void onDestroy() {
    	super.onDestroy();
    	sendStop();
    };

    
   // sendStop sends a notification to the GCM to remove from the database
    private void sendStop() {

		new AsyncTask<Void, Void, String>(){
	
			@Override
			protected String doInBackground(Void... params) {
				
				 JSONParser jsonParser = new JSONParser();
		    	   List<NameValuePair> params1 = new ArrayList<NameValuePair>();
		           params1.add(new BasicNameValuePair("username", Login.username));
		           
				
				JSONObject json = jsonParser.makeHttpRequest(
		                  URL.STOP_URL , "POST", params1);
			 
				return 	"done	";
				
				
			}
			
		}.execute(null,null,null);
 		
				
	
		
	}
	@Override
    public void onClick(View v) {
     
        switch (v.getId()) {
            case R.id.login:
                new AttemptLogin().execute();
                break;
            case R.id.register:
                Intent i = new Intent(this, Register.class);
                startActivity(i);
                break;

            default:
                break;
        }
    }
    
    
    
// Attempts the login when username and password is entered
    class AttemptLogin extends AsyncTask<String, String, String> {

        boolean failure = false;

        
        
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(Login.this);
            pDialog.setMessage("Attempting login...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... args) {
            int success;
            username = user.getText().toString();
            String password = pass.getText().toString();
            String status = "ACTIVE";
            try {
                // Building Parameters
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("username", username));
                params.add(new BasicNameValuePair("password", password));
                params.add(new BasicNameValuePair("status",status));
                
                Log.d("request!", "starting");
                JSONObject json = jsonParser.makeHttpRequest(
                        URL.LOGIN_URL, "POST", params);

                // check your log for json response
                Log.d("Login attempt", json.toString());

                // json success tag
                success = json.getInt(TAG_SUCCESS);
                if (success == 0) {
                    Log.d("Login Successful!", json.toString());

                    Intent i = new Intent(Login.this, GuideOrClient.class);              
                    getRegId();
                    // start the next activity, to select if the user wants to be guide or client
                    startActivity(i);
                    return json.getString(TAG_MESSAGE);
                }else{
                    Log.d("Login Failure!", json.getString(TAG_MESSAGE));
                    return json.getString(TAG_MESSAGE);

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;

        }
        
        // this function will get a GCM ID for this user
        public void getRegId(){
            new AsyncTask<Void, Void, String>() {
                @Override
                protected String doInBackground(Void... params) {
                    String msg = "";
                    try {
                        if (gcm == null) {
                            gcm = GoogleCloudMessaging.getInstance(getApplicationContext());
                        }
                        regid = gcm.register(URL.PROJECT_NUMBER);
                        msg = "Device registered, registration ID=" + regid;
                        Log.i("GCM",  msg);
                        
                        JSONParser jsonParser = new JSONParser();
                 	   List<NameValuePair> params1 = new ArrayList<NameValuePair>();
                        params1.add(new BasicNameValuePair("username", Login.username));
                        params1.add(new BasicNameValuePair("RegID", regid));
                 	   JSONObject json = jsonParser.makeHttpRequest(
                                URL.REGISTER_GCM, "POST", params1);

                    } catch (IOException ex) {
                        msg = "Error :" + ex.getMessage();

                    }
                    Log.d("GCM ErrorMESSAGE",  msg);
                    return msg; 
                }

                @Override
                protected void onPostExecute(String msg) {
                	 Log.d("GCM ErrorMESSAGE",  msg);
                }
            }.execute(null, null, null);
        }
     
        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog once product deleted
            pDialog.dismiss();
            if (file_url != null){
                Toast.makeText(Login.this, file_url, Toast.LENGTH_LONG).show();
            }

        }

    }

}
