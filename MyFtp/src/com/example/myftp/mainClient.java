package com.example.myftp;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.commons.net.ftp.FTPFile;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.Builder;
import android.text.format.Time;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.Toast;
import android.widget.ToggleButton;

public class mainClient extends Activity {

	String TAG = "FTPClient";
	FtpAdapter ftpClient;
	DBAdapter db;
	ListView list;
	cdAsynchTask cdTask;
	downloadAsyncTask dwnldTask;
	boolean localView = false;
	ToggleButton server; 
	ToggleButton local; 
	String path =  "";
	File pwd = new File("/");
	File parentDir = new File("/");
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_client);
		
		 if (android.os.Build.VERSION.SDK_INT > 9) {
		      StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		      StrictMode.setThreadPolicy(policy);
		    }
		local = (ToggleButton)findViewById(R.id.Local);
		server = (ToggleButton) findViewById(R.id.Server);
		Intent oldIntent = getIntent();
		String name = oldIntent.getStringExtra("name");
		String address = oldIntent.getStringExtra("address");
		int port = oldIntent.getIntExtra("port",21);
		String user = oldIntent.getStringExtra("user");
		String pass = oldIntent.getStringExtra("pass");
		int save = oldIntent.getIntExtra("save", 0);
		int ID = oldIntent.getIntExtra("ID", -1);
		Time today = new Time(Time.getCurrentTimezone());
		today.setToNow();
		String date = today.toString();
		
		db = new DBAdapter(this);
		
		//if a new connection
		if(ID == -1){
			db.open();
			
			ftpClient = new FtpAdapter(address, user, pass,port, this);
			
			if(save == 0){
				pass = null;
			}
			
			long currentID = db.insertConnection(user, pass, address, name, save, date,port);
			
			if(currentID < 0){
				System.out.println("Error inserting new connection into database");
			}
			db.close();
			
		}
		
		//open previous connection
		else{
			
			ftpClient = new FtpAdapter(address, user, pass,port, this);
			
			db.open();
			Cursor conn = db.getConnnection(ID);
			String dbPass = conn.getString(3);
			
			//check if pass was not saved but user has asked to save now.
			if(save == 1 && dbPass == null){
				db.updateConnection(ID, user, pass, address, name, port, save, date);
			}
			
			//if pass was saved but requests not to save
			else if(save == 0 && dbPass != null){
				pass = null;
				db.updateConnection(ID, user, pass, address, name, port, save, date);
			}
			conn.close();
			db.close();
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
					if(!localView){
						cdTask = new cdAsynchTask(mainClient.this,ftpClient,clicked);
						cdTask.execute();
						try {
							Thread.sleep(100);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						updateClientServer();
					}
					else{
						changeLocalDir(clicked);
					}
						
				}
				else{
					showOptionsMenu(v,clicked);
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
					showOptionsMenu(v,clicked);
					return true;
				}
			
		});
		
		task.execute();
		
	
	}
//-------------------------------------------------------------------------
	public void serverOnClick(View v){
		if(localView){
			//load server view
			localView = false;
			server.setChecked(true);
			local.setChecked(false);
			updateClientServer();
			
		}
		
	}
//-------------------------------------------------------------------------
	public void localOnClick(View v){
		if(!localView){
			//load local view
			localView = true;
			local.setChecked(true);
			server.setChecked(false);
			updateClientLocal();
			
		}
		
	}
//-------------------------------------------------------------------------
	void updateClientServer(){
		
		String wd = ftpClient.getWD();
		if(path == null){
			System.out.print("Error getting path");
		}
		ArrayList<ListEntry> Flist = ftpClient.ftpPrintFilesList();
		
		
		listAdapter lAdapt = new listAdapter(this, Flist);
		list.setAdapter(lAdapt);
		setTitle("FTP Client:Server: " + wd);
		
		
	}
//--------------------------------------------------------------------------
	void updateClientLocal(){
		
		ArrayList<ListEntry> Flist = getLocalFileList();
		listAdapter lAdapt = new listAdapter(this, Flist);
		list.setAdapter(lAdapt);
		setTitle("FTP Client:Local: " + pwd.getName());
	}
//---------------------------------------------------------------------------------------------------
	ArrayList<ListEntry> getLocalFileList(){
		
		
		ArrayList<ListEntry> list = new ArrayList<ListEntry>(); 
	        ListEntry parent = new ListEntry("..",true,parentDir.getAbsolutePath());
	        list.add(parent);
      try {  
        File[] files = pwd.listFiles() ;  
        int length = files.length;  
        for (int i = 0; i < length; i++) {  
          String name = files[i].getName();  
          boolean isFile = files[i].isFile();  
          if (isFile) {  
            ListEntry e = new ListEntry(name,false,path.concat("/".concat(name)));
            list.add(e);
          }  
          else {  
        	  ListEntry e = new ListEntry(name,true,path.concat("/".concat(name)));
              list.add(e); 
          }  
        }
        
        return list;
      } catch(Exception e) {  
        e.printStackTrace();  
      }  
      return list;
	
}
//--------------------------------------------------------------------------------------------------
	void showOptionsMenu(View v, final ListEntry entry){
		final PopupMenu popup = new PopupMenu(mainClient.this, v);
		if(entry.dir){
			//display dir menu
			popup.getMenuInflater().inflate(R.menu.client_menu_dir, popup.getMenu());
		}
		else{
			popup.getMenuInflater().inflate(R.menu.client_menu_file, popup.getMenu());
		}
		popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
			   
			   @Override
			   public boolean onMenuItemClick(MenuItem item) {
				   String message = null;
				   if(item.getTitle().toString().compareTo("Delete") == 0){
					   //delete file
					   deleteFile(entry);
				   }
				   else if(item.getTitle().toString().compareTo("Download") == 0){
					   //download
					   dwnldTask = new downloadAsyncTask(mainClient.this,ftpClient,entry);
						dwnldTask.execute();
						message = "Downloading file";
				   }
				   else if(item.getTitle().toString().compareTo("Open") == 0){
					   //open dir
					   if(!localView){
						    cdTask = new cdAsynchTask(mainClient.this,ftpClient,entry);
							cdTask.execute();
							updateClientServer();
					   }
					   else{
						   changeLocalDir(entry);
					   }
						message = "Opened Directory";
				   }
				   
				   else{
					   message = "Canceled";
				   }
			    Toast.makeText(mainClient.this,
			      message,
			      Toast.LENGTH_LONG).show();
			    popup.dismiss();
			    return true;
			   }
			  
			  });
			      popup.show();		  
	}
//--------------------------------------------------------------------------------------------------
	void deleteFile(ListEntry entry){
		
	}
//--------------------------------------------------------------------------------------------------
	void changeLocalDir(ListEntry clicked){
		if(clicked.path.compareTo(parentDir.getAbsolutePath()) == 0){
			//change to parent
			path = parentDir.getAbsolutePath();
			pwd = parentDir;
			if(pwd.getAbsolutePath().compareTo("/") != 0){
				parentDir = pwd.getParentFile();
			}
		
		}
		else{
			path = path.concat("/".concat(clicked.name));
			pwd = new File(path);
			parentDir = pwd.getParentFile(); //returning null
		}
		updateClientLocal();
	}
//--------------------------------------------------------------------------------------------------
//*************************************************************************************************
//---------------------------------------------------------------------------------------------------
	class cdAsynchTask extends AsyncTask<Void,Void,Void>{

		private mainClient activity;
		private FtpAdapter ftpClient;
		private ListEntry clicked;
		
		cdAsynchTask(mainClient a, FtpAdapter ftp, ListEntry entry){
			activity = a;
			ftpClient = ftp;
			clicked = entry;
			
		}
		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			int reply = ftpClient.cd(clicked.name);
			if(reply == 550){
				permErr();
			}
			else if(reply == 1){
				cdErr();
			}
			
			return null;
		}
		
//------------------------------------------------------------------------

		void cdErr(){
			AlertDialog.Builder builder = new AlertDialog.Builder(activity);
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
			AlertDialog.Builder builder = new AlertDialog.Builder(activity);
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
		
	}
//--------------------------------------------------------------------------------------------------
//************************************************************************************************
//--------------------------------------------------------------------------------------------------
	class downloadAsyncTask extends AsyncTask<Void,Void,Void>{
		private mainClient activity;
		private FtpAdapter ftpClient;
		private ListEntry entry;
		NotificationManager mNotifyManager;
		Builder mBuilder;

		
		downloadAsyncTask(mainClient a, FtpAdapter ftp, ListEntry e){
			activity = a;
			ftpClient = ftp;
			entry = e;
			mNotifyManager = (NotificationManager) getSystemService(activity.NOTIFICATION_SERVICE);
			mBuilder = new NotificationCompat.Builder(activity);
			mBuilder.setContentTitle("File Download")
			    .setContentText("Download in progress")
			    .setSmallIcon(R.drawable.ic_launcher);
		}
//------------------------------------------------------------------------------------------------------
		@Override
		protected Void doInBackground(Void... params) {
		
			String root = Environment.getExternalStorageDirectory().toString();
			
			File dir = new File(root + "/Download");
			dir.mkdir();
			String dest = dir.toString() + "/" + entry.name;
			
				
			mBuilder.setProgress(100, 0, false);
	        // Displays the progress bar for the first time.
	        mNotifyManager.notify(0, mBuilder.build());
	       
	        
	        //need to keep track of progress while transferring
	            
	        int reply = ftpClient.pullFile(entry.name, dest);
			if(reply >= 300){
				printError();
			}
			else{
				//update progress to completed
				mBuilder.setProgress(100, 100, false);
		        mNotifyManager.notify(0, mBuilder.build());
			}

			return null;
		}
//---------------------------------------------------------------------------------------------------
		/*
		 * printError: displays error popup
		 */
		void printError(){
			AlertDialog.Builder builder = new AlertDialog.Builder(activity);
			builder.setTitle("Error Downloading");
			builder.setMessage("An error occured downloading the requested file");
			builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		               // User clicked OK button
		        	   dialog.dismiss();
		           }
		       });
			AlertDialog dialog = builder.create();
			dialog.show();
			
			 
		}
//---------------------------------------------------------------------------------------------------
		  @Override
	        protected void onPreExecute() {
	            super.onPreExecute();
	           // showDialog(DIALOG_DOWNLOAD_PROGRESS);

	        }
		
	}
//---------------------------------------------------------------------------------------------------
//**************************************************************************************************
//---------------------------------------------------------------------------------------------------
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
	        
	        activity.updateClientServer();
		
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
//*****************************************************************************************
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
	            updateClientServer();
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
		try {
			ftpClient.ftpDisconnect();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Intent intent = new Intent(mainClient.this, MainActivity.class);
		startActivity(intent);
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
//-----------------------------------------------------------------------------------

}		

	
