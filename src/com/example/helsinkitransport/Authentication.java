package com.example.helsinkitransport;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class Authentication extends Activity{
	private LinearLayout linearLayout;
	private EditText userText;
	private EditText passText;
	private  Intent intent;
	private  TextView label2;
	
	private String url = "http://api.reittiopas.fi/public-ytv/fi/api/?";
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.authentication);   
        
       linearLayout = new LinearLayout(this);
       linearLayout.setOrientation(LinearLayout.VERTICAL);
       
       TextView label1 = new TextView(this);
       label1.setText("Enter your reittiopas username and password:");
       linearLayout.addView(label1);
       
       userText = new EditText(this);
   	   userText.setHint("Username");
   	   linearLayout.addView(userText);
   	
	   	passText = new EditText(this);
	   	passText.setTransformationMethod(PasswordTransformationMethod.getInstance());
	   	passText.setHint("Password");
	   	linearLayout.addView(passText);
	   	
	    
	   	final Button button = new Button(this);
    	button.setText("Log in");
    	linearLayout.addView(button);
    	
    	label2 = new TextView(this);
	    label2.setText("You have no rights to access the API, " + "please request an account at " + "http://developer.reittiopas.fi/pages/en/account-request.php:");
	    label2.setVisibility(View.INVISIBLE);
	    linearLayout.addView(label2);
	    
    	intent = new Intent().setClass(this, Input.class);
    	
    	button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	new authenticateTask().execute(url + "user=" + userText.getText().toString() + "&pass=" + passText.getText().toString());
    	     }
        });
    	setContentView(linearLayout);
	}
	
	private class authenticateTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
              
        	String line = null;
	   	     try 
	   	     {
	   	         DefaultHttpClient httpClient = new DefaultHttpClient();
	   	         HttpGet httpPost = new HttpGet(urls[0]);
	
	   	         HttpResponse httpResponse = httpClient.execute(httpPost);
	   	         HttpEntity httpEntity = httpResponse.getEntity();
	   	         line = EntityUtils.toString(httpEntity);
	
	   	     } catch (UnsupportedEncodingException e) {
	   	         e.printStackTrace();
	   	     } catch (MalformedURLException e) {
	   	         e.printStackTrace();
	   	     } catch (IOException e) {
	   	         e.printStackTrace();
	   	     }
	   	  return line;
	        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
        	if(result.contains("No rights to access API"))
        		label2.setVisibility(View.VISIBLE);
        	else {
        		UserDataContainer.username = userText.getText().toString();
        		UserDataContainer.password = passText.getText().toString();
        		startActivity(intent);
        	}
       }
    }
}
