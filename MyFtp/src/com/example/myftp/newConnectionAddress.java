package com.example.myftp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;

public class newConnectionAddress extends Activity{

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.new_connection2);
		
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	public void onClickNext(View view){
		Intent oldIntent = getIntent();
		 String name = oldIntent.getStringExtra("name");
		 
		 int port = 21;
		EditText pEdit = (EditText) findViewById(R.id.portField);
		EditText mEdit   = (EditText)findViewById(R.id.addressField);
		String address = mEdit.getText().toString();
		if(pEdit.getText().length() <= 0){
			port = 21;
		}
		else{
			port = Integer.parseInt(pEdit.getText().toString());
		}
		Intent intent = new Intent(this, newConnectionCreds.class);
		intent.putExtra("name", name);
		intent.putExtra("address", address);
		intent.putExtra("port", port);
		startActivity(intent);
	}

}
