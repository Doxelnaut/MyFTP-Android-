package com.example.myftp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

public class newConnectionCreds extends Activity {
	String name;
	String address;
	int port ;
	int ID;
	String user;
	String pass;
	int save;
	EditText uEdit;
	EditText pEdit;
	CheckBox saveP;
	CheckBox box;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.new_connection3);
		
		Intent oldIntent = getIntent();
		name = oldIntent.getStringExtra("name");
		address = oldIntent.getStringExtra("address");
		port = oldIntent.getIntExtra("port",21);
		ID = oldIntent.getIntExtra("ID", -1);
		user = oldIntent.getStringExtra("user");
		pass = oldIntent.getStringExtra("pass");
		save = oldIntent.getIntExtra("save", 0);
		
		uEdit = (EditText)findViewById(R.id.userField);
		pEdit = (EditText)findViewById(R.id.passField);
		saveP = (CheckBox)findViewById(R.id.savePass);
		box = (CheckBox)findViewById(R.id.showPass);
		
		if(user != null){
			uEdit.setText(user);
		}
		
		if(pass != null){
			pEdit.setText(pass);
			saveP.setChecked(true);
			box.setVisibility(View.INVISIBLE);
			
		}
		
	}

	
	public void onClickNext(View view){
		
		
		
		user = uEdit.getText().toString();
		pass = pEdit.getText().toString();
		
		if(saveP.isChecked()){
			save = 1;
		}
		Intent intent = new Intent(this, newConnectionConfirm.class);
		intent.putExtra("name", name);
		intent.putExtra("address", address);
		intent.putExtra("port", port);
		intent.putExtra("user", user);
		intent.putExtra("pass", pass);
		intent.putExtra("save", save);
		intent.putExtra("ID", ID);
		startActivity(intent);
		
	}

	
	public void onClickShowPass(View view){
		
		EditText pEdit = (EditText)findViewById(R.id.passField);
		
		if(box.isChecked()){
			pEdit.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
			pEdit.setSelection(pEdit.getText().length());
		}
		else{
			pEdit.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
			pEdit.setSelection(pEdit.getText().length());
		}
	}
	
}
