/*
 * MainActivity: first screen shown when application is started.
 */
package com.example.myftp;

import java.util.ArrayList;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.PopupMenu;
import android.widget.Toast;

public class MainActivity extends Activity {
	
	ListView recents;				//View for list of recent connections
	ArrayList<DBEntry> recentList;  //list of recent connections

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		//initiate recent list
		recents = (ListView)findViewById(R.id.RecentList);
		//set onClick for recent list
		recents.setOnItemClickListener(new OnItemClickListener()
		   {
			@Override
			public void onItemClick(AdapterView<?> adapter, View v, int position,
					long arg3) {
				//package information about recent entry into Extra to be sent to next activity
				DBEntry clicked = (DBEntry) adapter.getItemAtPosition(position);
				Intent intent = new Intent(MainActivity.this, newConnectionCreds.class);
				intent.putExtra("name", clicked.SERVERNAME);
				intent.putExtra("address", clicked.SERVERADDR);
				intent.putExtra("port", clicked.PORT);
				intent.putExtra("user", clicked.USER);
				intent.putExtra("pass", clicked.PASS);
				intent.putExtra("save", clicked.SAVE);
				intent.putExtra("ID", clicked.ID);
				startActivity(intent); //start activity (userCreds)
				
				}
			
		   });
		
		//sets onLongClickListener for recent list
		recents.setOnItemLongClickListener(new OnItemLongClickListener()
		{
			@Override
			public boolean onItemLongClick(AdapterView<?> adapter, View v,
				int position, long arg3) {
					//show menu
					DBEntry clicked = (DBEntry) adapter.getItemAtPosition(position);
					showOptionsMenu(v, clicked);
					return true;
				}	
		});
		
		//display recent list
		showRecents();
	}
//---------------------------------------------------------------------------------------
	/*
	 * (non-Javadoc)
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
//-------------------------------------------------------------------------------------
	/*
	 * onClick: onClick listener for newConnection button
	 */
	public void onClick(View view){
		startActivity(new Intent(this, newConnectionName.class));
	}
	//--------------------------------------------------------------------------
	/*
	 * savedOnClick: onClickListener for saved button; displays all saved connections
	 */
	public void savedOnClick(View v){
		startActivity(new Intent(this, SavedConnections.class));
	}
	//--------------------------------------------------------------------------
	/*
	 * showRecents: sets adapter for recent list, then displays entries in the view
	 */
	void showRecents(){
		
		recentList = getConnList();//get list of recent connections;
		RecentListAdapter lAdapt = new RecentListAdapter(this, recentList);
		recents.setAdapter(lAdapt);
	}
//-------------------------------------------------------------------------------
	/*
	 * showOptionsMenu: shows the options menu when clicked
	 */
	void showOptionsMenu(View v, final DBEntry entry){
		final PopupMenu popup = new PopupMenu(MainActivity.this, v);
		popup.getMenuInflater().inflate(R.menu.recent_options, popup.getMenu());
		popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
			   
			   @Override
			   public boolean onMenuItemClick(MenuItem item) {
				   String message = null;
				   if(item.getTitle().toString().compareTo("Delete") == 0){
					   DBAdapter db = new DBAdapter(MainActivity.this);
					   db.open();
					   db.deleteConnection(entry.ID);
					   db.close();
					   showRecents();
					   message = "Deleted Connection";
				   }
				   else{
					   message = "Canceled";
				   }
			    Toast.makeText(MainActivity.this,
			      message,
			      Toast.LENGTH_LONG).show();
			    popup.dismiss();
			    return true;
			   }
			  
			  });
			      popup.show();		  
	}
//----------------------------------------------------------------------------------------
	/*
	 * getConnList: gets a list of the 5 most recent connections (storing them as DBEntries in an ArrayList)
	 */
	ArrayList<DBEntry> getConnList(){
		//create new Database adapter
		DBAdapter db = new DBAdapter(this);
		//create new ArrayList to store connection entries
		ArrayList<DBEntry> list = new ArrayList<DBEntry>();
		//open database
		db.open();
		//get a list of all connections in the server sorted by last access date
		Cursor conn = db.getAllConnections();
		if(conn.moveToNext()){
			for(int i = 0; i < 5; i++){
				DBEntry entry = new DBEntry();
				entry.ID = conn.getInt(0);
				entry.USER = conn.getString(1);
				entry.PASS = conn.getString(3);
				entry.SERVERADDR = conn.getString(4);
				entry.PORT = conn.getInt(2);
				entry.SERVERNAME = conn.getString(6);
				entry.DATE = conn.getString(7);
				entry.SAVE = conn.getInt(5);
				list.add(entry);
				if(!conn.moveToNext()){
					break;
				}
			}
		}
		conn.close();
		db.close();
		return list;
		
		
		
	}

}
