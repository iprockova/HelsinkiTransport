package com.example.helsinkitransport;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class Input extends Activity{
	private LinearLayout linearLayout;
	private EditText fromText;
	private EditText toText;
	private  Intent intent;
	AlertDialog alertDialog;
	
	 @Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.main);
	        
	        linearLayout = new LinearLayout(this);
	        linearLayout.setOrientation(LinearLayout.VERTICAL);
	        
	        TextView intro_text = new TextView(this);
	        intro_text.setText("Helsinki Region Transport");
	        intro_text.setPadding(0, 0, 0, 10);
	        linearLayout.addView(intro_text);
	        
	        
	        TextView label2 = new TextView(this);
	        label2.setText("Enter your route");
	        linearLayout.addView(label2);
	        
	        fromText = new EditText(this);
	    	fromText.setText("Timpurinkuja 3");
	    	linearLayout.addView(fromText);
	    	
	    	toText = new EditText(this);
	    	toText.setText("Otaniemi");
	    	linearLayout.addView(toText);
	        
	    	final Button button = new Button(this);
	    	button.setText("show routes");
	    	linearLayout.addView(button);
	    	
	    	intent = new Intent().setClass(this, Routes.class);
	    	alertDialog = new AlertDialog.Builder(this).create();
	    	button.setOnClickListener(new View.OnClickListener() {
	            public void onClick(View v) {
	            	String from = fromText.getText().toString();
	            	String to = toText.getText().toString();
	            	if(from.equalsIgnoreCase("") || from.equalsIgnoreCase(" ") || to.equalsIgnoreCase("") || to.equalsIgnoreCase(" ")){
	    	        	alertDialog.setTitle("Fill the From and To boxes.");
	    	        	alertDialog.show();
	            	} else {
	            		UserDataContainer.from = fromText.getText().toString();
		            	UserDataContainer.to = toText.getText().toString();
		            	Bundle b = new Bundle();
		            	b.putString("from", fromText.getText().toString());
		            	b.putString("to", toText.getText().toString());
		            	intent.putExtras(b);
		            	startActivity(intent);
	            	}
	            	
	    	     }
	        });

	    	setContentView(linearLayout);
	    }
}
