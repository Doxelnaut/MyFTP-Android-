package com.example.myftp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.TextView;

public class newConnectionConfirm extends Activity{

	String name;
	String address;
	int port;
	String user;
	String pass;
	int save;
	int ID;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.new_connection4);
		
		Intent oldIntent = getIntent();
		name = oldIntent.getStringExtra("name");
		address = oldIntent.getStringExtra("address");
		port = oldIntent.getIntExtra("port",21);
		user = oldIntent.getStringExtra("user");
		pass = oldIntent.getStringExtra("pass");
		save = oldIntent.getIntExtra("save", 0);
		ID = oldIntent.getIntExtra("ID", -1);
		
		TextView nameF = (TextView)findViewById(R.id.nameField);
		TextView addressF = (TextView)findViewById(R.id.addressField);
		TextView portF = (TextView)findViewById(R.id.portField);
		TextView userF = (TextView)findViewById(R.id.userField);
		TextView passF = (TextView)findViewById(R.id.passField);
		
		nameF.setText(name);
		addressF.setText(address);
		portF.setText(String.valueOf(port));
		userF.setText(user);
		passF.setText(pass);
		
	}
	
	public void onClickConnect(View view){
		
		Intent intent = new Intent(this, mainClient.class);
		intent.putExtra("name", name);
		intent.putExtra("address", address);
		intent.putExtra("port", port);
		intent.putExtra("user", user);
		intent.putExtra("pass", pass);
		intent.putExtra("save", save);
		intent.putExtra("ID", ID);
		startActivity(intent);
	}
	
}
