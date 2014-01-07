package com.example.myftp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

public class newConnectionCreds extends Activity {
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.new_connection3);
		
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	public void onClickNext(View view){
		Intent oldIntent = getIntent();
		String name = oldIntent.getStringExtra("name");
		String address = oldIntent.getStringExtra("address");
		int port = oldIntent.getIntExtra("port",21);
		
		EditText uEdit = (EditText)findViewById(R.id.userField);
		EditText pEdit = (EditText)findViewById(R.id.passField);
		
		String user = uEdit.getText().toString();
		String pass = pEdit.getText().toString();
		
		int save = 0;
		CheckBox saveP = (CheckBox)findViewById(R.id.savePass);
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
		startActivity(intent);
		
	}
	
	/*public void onClickUserField(View view){
		EditText uEdit = (EditText)findViewById(R.id.userField);
		EditText pEdit = (EditText)findViewById(R.id.passField);
		
	}*/
	
	public void onClickShowPass(View view){
		CheckBox box = (CheckBox)findViewById(R.id.showPass);
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
