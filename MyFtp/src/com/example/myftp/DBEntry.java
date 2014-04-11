/*
 * DBEntry: stores informtaion from a database entry. Allows for easy storage of an entry and its' data within the FTP client.
 */
package com.example.myftp;

public class DBEntry {
	int ID;
	String USER;
	String PASS;
	String SERVERADDR;
	String SERVERNAME;
	String DATE;
	int PORT;
	int SAVE;
	
	//constructor with params.
	DBEntry(int i, String u, String pass, String addr, String n, String d, int p){
		ID = i;
		USER = u;
		PASS = pass;
		SERVERADDR = addr;
		SERVERNAME = n;
		DATE = d;
		PORT = p;
	}
	
	//constructor w/o params.
	DBEntry(){
		ID = 0;
		USER = null;
		PASS = null;
		SERVERADDR = null;
		SERVERNAME = null;
		DATE = null;
		PORT = 0;
		SAVE = 0;
	}

}
