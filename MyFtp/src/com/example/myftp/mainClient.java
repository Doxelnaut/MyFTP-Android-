package com.example.myftp;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.Editable;
import android.text.format.Time;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ScrollView;

public class mainClient extends Activity {

	String TAG = "FTPClient";
	FtpAdapter ftpClient;
	DBAdapter db;
	ListView list;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_client);
		
		 if (android.os.Build.VERSION.SDK_INT > 9) {
		      StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		      StrictMode.setThreadPolicy(policy);
		    }
		 
		 
		 
		Intent oldIntent = getIntent();
		String name = oldIntent.getStringExtra("name");
		String address = oldIntent.getStringExtra("address");
		int port = oldIntent.getIntExtra("port",21);
		String user = oldIntent.getStringExtra("user");
		String pass = oldIntent.getStringExtra("pass");
		int save = oldIntent.getIntExtra("save", 0);
		Time today = new Time(Time.getCurrentTimezone());
		today.setToNow();
		String date = today.toString();
		
		
		db = new DBAdapter(this);
		db.open();
		
		ftpClient = new FtpAdapter(address, user, pass,port, this);
		
		if(save == 0){
			pass = null;
		}
		
		long currentID = db.insertConnection(user, pass, address, name, save, date,port);
		
		if(currentID < 0){
			System.out.println("Error inserting new connection into database");
		}

		connectAsyncTask task = new connectAsyncTask(this,ftpClient);
		list = (ListView)findViewById(R.id.ListView1);
		list.setOnItemClickListener(new OnItemClickListener()
		   {
			@Override
			public void onItemClick(AdapterView<?> adapter, View v, int position,
					long arg3) {
				// TODO Auto-generated method stub
				ListEntry clicked = (ListEntry)adapter.getItemAtPosition(position);
				if(clicked.dir){
					int reply = ftpClient.cd(clicked.name);
					if(reply == 550){
						permErr();
					}
					else if(reply == 1){
						cdErr();
					}
					else{
						updateClient();
					}
				}
				else{
					//show file menu
				}
			}
		   });
		
		
		list.setOnItemLongClickListener(new OnItemLongClickListener()
		{

			@Override
			public boolean onItemLongClick(AdapterView<?> adapter, View v,
				int position, long arg3) {
				// TODO Auto-generated method stub
					//show menu
					ListEntry clicked = (ListEntry) adapter.getItemAtPosition(position);
					if(clicked.dir){
						//show dir menu
					}
					else{
						//show file menu
					}
					return false;
				}
			
		});
		
		task.execute();
		
	
	}
//------------------------------------------------------------------------
	void cdErr(){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Error Changind Directory");
		builder.setMessage("Cannot change directory: undefined error");
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
	           public void onClick(DialogInterface dialog, int id) {
	               // User clicked OK button
	        	   dialog.dismiss();
	           }
	       });
		AlertDialog dialog = builder.create();
		dialog.show();
	}
//----------------------------------------------------------------------
	void permErr(){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Error Changind Directory");
		builder.setMessage("Cannot change directory: Permission Denied");
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
	           public void onClick(DialogInterface dialog, int id) {
	               // User clicked OK button
	        	   dialog.dismiss();
	           }
	       });
		AlertDialog dialog = builder.create();
		dialog.show();
		
		 
	}
//-------------------------------------------------------------------------
	void updateClient(){
		
		String path = ftpClient.getWD();
		if(path == null){
			System.out.print("Error getting path");
		}
		ArrayList<ListEntry> Flist = ftpClient.ftpPrintFilesList();
		
		
		listAdapter lAdapt = new listAdapter(this, Flist);
		list.setAdapter(lAdapt);
		setTitle("FTP Client:Server: " + path);
		
		
	}

//---------------------------------------------------------------------------------------------------
//--------------------------------------------------------------------------------------------------
	class connectAsyncTask extends AsyncTask<Void,Void,Boolean>{
		
		private mainClient activity;
		private ProgressDialog dialog;
		private FtpAdapter ftpClient;
		
		connectAsyncTask(mainClient  activity, FtpAdapter ftp) {
		    this.activity = activity;
		    dialog = new ProgressDialog(this.activity);
		    ftpClient = ftp;
		}

	    /** progress dialog to show user that the client is connecting. */
	    /** application context. */

	    protected void onPreExecute() {
	        this.dialog.setMessage("Connecting to FTP server");
	        this.dialog.show();
	    }

//----------------------------------------------------------------------------------------
	    
	    protected void onPostExecute(final Boolean success) {

	        if (dialog.isShowing()) {
	            dialog.dismiss();
	        }
	        
	        activity.updateClient();
		
	    }
//-----------------------------------------------------------------------------------------
		@Override
		protected Boolean doInBackground(Void...params) {
	        try {
        		ftpClient.ftpConnect();
        		return true;
	        } catch (Exception e) {
	        	Log.e("tag", "error", e);
	        	
	        	return false;
	        }
		}

	}
	
	
//-----------------------------------------------------------------------------------------
//------------------------------------------------------------------------------------
	
	
	void ShowCreateDir(){
		final EditText input = new EditText(this);

		new AlertDialog.Builder(this)
	    .setTitle("Create Directory")
	    .setMessage("Enter Directory Name:")
	    .setView(input)
	    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int whichButton) {
	            String value = input.getText().toString();
	            ftpClient.mkDir(value);
	            dialog.dismiss();
	            updateClient();
	        }
	    }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int whichButton) {
	            // Do nothing.
	        	dialog.dismiss();
	        }
	    }).show();
	}
	//-----------------------------------------------------------------------------------
	void ShowSettings(){
		
	}
//--------------------------------------------------------------------------------------
	void logOut(){
		
	}
//--------------------------------------------------------------------------------------
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

//----------------------------------------------------------------------------------
	public boolean onOptionsItemSelected(MenuItem item)
	{
	    // Handle item selection
	    switch (item.getItemId())
	    {

	    case R.id.MKdir:
	    {
	        ShowCreateDir();
	        return true;
	    }

	    case R.id.Settings:
	    {
	    	ShowSettings();
	    	return true;
	    }
	    
	    case R.id.logOut:
	    {
	    	logOut();
	    	return true;
	    }

	    default:
	        return super.onOptionsItemSelected(item);
	    }
	}
}		

	
