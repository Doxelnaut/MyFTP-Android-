/*
 * DBAdapter: adapter class for use with ftp client. Allows the client to save and restore account information.
 */
package com.example.myftp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBAdapter {
	//Column definitions
	static final String KEY_ROWID = "_id";
	static final String KEY_USER = "user";
	static final String KEY_PASS = "pass";
	static final String KEY_SERVERADDR = "address";
	static final String KEY_SERVERNAME = "nickname";
	static final String KEY_SAVE = "save";
	static final String KEY_DATE = "date";
	static final String KEY_PORT = "port";
	
	//Global variables
	static final String TAG = "DBAdapter";
	static final String DATABASE_NAME = "myFTPDB.db";
	static final String DATABASE_TABLE = "connections";
	static final int DATABASE_VERSION = 1;
	
	//string used when defining the database table
	static final String DATABASE_CREATE = "create table connections (_id integer primary key autoincrement, " + "user text not null, pass text, address text not null, port integer not null, nickname text not null, date text, save integer not null);";
	
	//context sent from android activity
	final Context context;
	
	//global databaseHelper instance
	DatabaseHelper DBHelper;
	//global database instance
	SQLiteDatabase db;

	//constructor
	public DBAdapter(Context ctx){
		this.context = ctx;
		DBHelper = new DatabaseHelper(context);
	}
	
/*
 * DatabaseHelper class: class that communicates with SQL database so the FTP client doesn't need to do so directly
 */
	private static class DatabaseHelper extends SQLiteOpenHelper{
		
		//constructor
		DatabaseHelper(Context context){
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}
		
		//called when database is first created
		public void onCreate(SQLiteDatabase db){
			try{
				db.execSQL(DATABASE_CREATE);
			} catch(SQLException e){
				e.printStackTrace();
			}
		}
		
		//allows for upgrading the database
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
			Log.w(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");
			db.execSQL("DROP TABLE IF EXISTS connections");
			onCreate(db);
		}
	}
	
	//opens the database
	public DBAdapter open() throws SQLException{
		db = DBHelper.getWritableDatabase();
		return this;
	}
	
	//closes the database
	public void close(){
		DBHelper.close();
	}
	
	//insert a connection into the database
	public long insertConnection(String user, String pass, String address, String nickname, int save, String date, int port){
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_USER, user);
		initialValues.put(KEY_PASS, pass);
		initialValues.put(KEY_SERVERADDR, address);
		initialValues.put(KEY_SAVE, save);
		initialValues.put(KEY_SERVERNAME, nickname);
		initialValues.put(KEY_DATE, date);
		initialValues.put(KEY_PORT, port);
		return db.insert(DATABASE_TABLE, null,initialValues);
	}
	
	//delete connection
	public boolean deleteConnection(long rowId){
		return db.delete(DATABASE_TABLE, KEY_ROWID + "=" + rowId, null) > 0;
	}
	
	//retrieve all connections ordered by date
	public Cursor getAllConnections(){
		return db.query(DATABASE_TABLE, new String[] {KEY_ROWID, KEY_USER, KEY_PORT, KEY_PASS, KEY_SERVERADDR, KEY_SAVE, KEY_SERVERNAME, KEY_DATE}, null, null, null, null, KEY_DATE+" DESC");
	}
	
	//retrieve a particular connection
	public Cursor getConnnection(long rowId) throws SQLException{
		Cursor mCursor = 
				db.query(true, DATABASE_TABLE, new String[] {KEY_ROWID, KEY_USER, KEY_PORT, KEY_PASS, KEY_SERVERADDR, KEY_SERVERNAME, KEY_SAVE, KEY_DATE}, KEY_ROWID + "=" + rowId, null, null, null, null, null);
		if(mCursor != null){
			mCursor.moveToFirst();
		}
		return mCursor;
	}
	
	//update a connection
	public boolean updateConnection(long rowId, String user, String pass, String address, String nickname, int port, int save, String date){
		ContentValues args = new ContentValues();
		args.put(KEY_USER, user);
		args.put(KEY_PASS, pass);
		args.put(KEY_SERVERADDR, address);
		args.put(KEY_SAVE, save);
		args.put(KEY_SERVERNAME, nickname);
		args.put(KEY_DATE, date);
		args.put(KEY_PORT, port);
		return db.update(DATABASE_TABLE, args, KEY_ROWID + "=" + rowId, null) > 0;
	}
}
