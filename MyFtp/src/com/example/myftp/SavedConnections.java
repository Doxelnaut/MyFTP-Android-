package com.example.myftp;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

public class SavedConnections extends Activity{
	
	ListView saved;
	ArrayList<DBEntry> savedList;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.saved_list);
		
		saved = (ListView)findViewById(R.id.SavedList);
		
		saved.setOnItemClickListener(new OnItemClickListener()
		   {
			@Override
			public void onItemClick(AdapterView<?> adapter, View v, int position,
					long arg3) {
	
				DBEntry clicked = (DBEntry) adapter.getItemAtPosition(position);
				Intent intent = new Intent(SavedConnections.this, newConnectionCreds.class);
				intent.putExtra("name", clicked.SERVERNAME);
				intent.putExtra("address", clicked.SERVERADDR);
				intent.putExtra("port", clicked.PORT);
				intent.putExtra("user", clicked.USER);
				intent.putExtra("pass", clicked.PASS);
				intent.putExtra("save", clicked.SAVE);
				intent.putExtra("ID", clicked.ID);
				startActivity(intent);
				
				}
			
		   });
		
		saved.setOnItemLongClickListener(new OnItemLongClickListener()
		{

			@Override
			public boolean onItemLongClick(AdapterView<?> adapter, View v,
				int position, long arg3) {
					//show menu
					DBEntry clicked = (DBEntry) adapter.getItemAtPosition(position);
					showOptionsMenu(v, clicked);
					return false;
				}
			
		});
		showSaved();
	}
	
//---------------------------------------------------------------------------------------------------
	public void homeOnClick(View v){
		startActivity(new Intent(this, MainActivity.class));
	}
//----------------------------------------------------------------------------------------------------
	
	void showOptionsMenu(View v, final DBEntry entry){
		final PopupMenu popup = new PopupMenu(SavedConnections.this, v);
		popup.getMenuInflater().inflate(R.menu.recent_options, popup.getMenu());
		popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
			   
			   @Override
			   public boolean onMenuItemClick(MenuItem item) {
				   String message = null;
				   if(item.getTitle().toString().compareTo("Delete") == 0){
					   DBAdapter db = new DBAdapter(SavedConnections.this);
					   db.open();
					   db.deleteConnection(entry.ID);
					   db.close();
					   showSaved();
					   message = "Deleted Connection";
				   }
				   else{
					   message = "Canceled";
				   }
			    Toast.makeText(SavedConnections.this,
			      message,
			      Toast.LENGTH_LONG).show();
			    popup.dismiss();
			    return true;
			   }
			  
			  });
			      popup.show();		  
	}
//----------------------------------------------------------------------------------------------------
	
	void showSaved(){
			
			savedList = getConnList();//get list of recent connections;
			RecentListAdapter lAdapt = new RecentListAdapter(this, savedList);
			saved.setAdapter(lAdapt);
	}
//------------------------------------------------------------------------------------------------

	ArrayList<DBEntry> getConnList(){
		DBAdapter db = new DBAdapter(this);
		ArrayList<DBEntry> list = new ArrayList<DBEntry>();
		db.open();
		Cursor conn = db.getAllConnections();
		if(conn.moveToNext()){
			for(int i = 0; i < conn.getCount(); i++){
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
