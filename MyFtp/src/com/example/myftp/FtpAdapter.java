/*
 * FTPAdapter: an adapter for use with FTP client. Allows for easy communication between activity and apache FTP Client.
 */
package com.example.myftp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.SocketException;
import java.util.ArrayList;

import org.apache.commons.net.ftp.*;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

public class FtpAdapter {

	//global variables used when connecting to FTP server
	String address;
	String user;
	String pass;
	int port;
	FTPClient client;
	
	//context sent from android activity
	final Context context;
	
	//tag for logs
	String TAG = "FTPClient";
	
	//constructor 
	FtpAdapter(String a, String u, String p, int pt, Context ctx){
		address = a;
		user = u;
		pass = p;
		port = pt;
		client = new FTPClient();
		this.context = ctx;
		FTPClientConfig conf = new FTPClientConfig(FTPClientConfig.SYST_UNIX);
		client.configure(conf);
		
	}
//---------------------------------------------------------------------------------------------
	/*
	 * ftpConnect: connects the client to the server. Enters passive mode and sets transfer mode.
	 */
	int ftpConnect() throws SocketException, IOException{
		client.connect(address, port);
		client.enterLocalPassiveMode();
		client.login(user, pass);
		
		//test if connection was successful
		if(FTPReply.isPositiveCompletion(readReply())){
			client.setFileTransferMode(FTP.BINARY_FILE_TYPE);
			client.setFileType(FTP.ASCII_FILE_TYPE);
			
			return 0;
		}
		else{
			return 1;
		}
	}
//---------------------------------------------------------------------------------------------
	/*
	 * ftpDisconnect: disconnects the client from the server
	 */
	void ftpDisconnect() throws IOException{
		client.logout();
		client.disconnect();
	}
//---------------------------------------------------------------------------------------------
	/*
	 * getWD: requests the current working directory from the server.
	 */
	String getWD() {
		String dir = null;
		try {
			dir = client.printWorkingDirectory();
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Error retreiving working directory");
			return null;
		}
		int reply = readReply();
		return dir;
	}
//--------------------------------------------------------------------------------------------
	/*
	 * cd: attempts to change the working director on the server
	 */
	int cd(String path){
		try {
			client.changeWorkingDirectory(path);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("Error changing directory");
			return 1;
		}
		int reply = readReply();
		
		if(reply == 550){
			return 550;
		}
		return 0;
	}
//-----------------------------------------------------------------------------------------------
/*
 * readReply: reads the reply from the server
 */
	int readReply(){
		
		int reply = 0;
		reply = client.getReplyCode();
		return reply;
	}
	
//------------------------------------------------------------------------------------------------
	/*
	 * mkDir: makes a directory in the pwd on the server
	 */
	int mkDir(String name){
		try {
			client.makeDirectory(name);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int reply = readReply();
		if(FTPReply.isPositiveCompletion(reply)){
			return 0;
		}
		else{
			return 1;
		}
	}
//------------------------------------------------------------------------------------------------
	/*
	 * pullFile: pulls a file from the server onto the client.
	 */
	int pullFile(String src, String dest){
		//define file output stream in the pwd on the client
		FileOutputStream desFileStream;
		
		//open output stream
		try {
			desFileStream = new FileOutputStream(dest);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("Error creating output stream");
			return 1;
		}  
		
		//pull file from server
	      try {
			client.retrieveFile(src, desFileStream);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("Error retrieving file");
			return 2;
		}  
	     
	      //close output file stream
	      try {
			desFileStream.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("Error closing file stream");
			return 3;
		}  
	      
	      int reply = readReply();
	      return reply;
	}
//---------------------------------------------------------------------------------------------
	/*
	 * putFile: uploads a file to the server
	 */
	int putFile(String src, String dest){
		
		//define input stream
		   FileInputStream srcFileStream;
		   
		   //open input stream
		try {
			srcFileStream = context.openFileInput(src);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("Error opening file for put");
			return 1;
		}  
		
		//upload file
		   try {
			client.storeFile(dest, srcFileStream);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("Error writing file to server");
			return 2;
		}  
		   
		   //close input stream
		   try {
			srcFileStream.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("Error closing stream");
			return 3;
		} 
		   int reply = readReply();
		   return reply;
	}
//------------------------------------------------------------------------------------------------
	/*
	 * ftpPrintFilesList: gets all files and directories in pwd of server. Stores this information as an arrayList of List Entries.
	 */
	public ArrayList<ListEntry> ftpPrintFilesList()  
    {  
		//get pwd
		String wd = this.getWD();
		
		//define ArrayList to store listEntries
		ArrayList<ListEntry> list = new ArrayList<ListEntry>();
		
		//add the parent directory to the list (may not be necessary depending on the server)
	        ListEntry parent = new ListEntry("..",true,wd);
	        list.add(parent);
	        
	  //add the rest of the files and directories to the list      
      try {  
        FTPFile[] ftpFiles = client.listFiles(".");  
        int length = ftpFiles.length;  
        for (int i = 0; i < length; i++) {  
          String name = ftpFiles[i].getName();  
          boolean isFile = ftpFiles[i].isFile();
          //test if file or dir
          if (isFile) {  
            ListEntry e = new ListEntry(name,false,wd.concat("/".concat(name))); //add file (designated by 'false' value)
            list.add(e);
          }  
          else {  
        	  ListEntry e = new ListEntry(name,true,wd.concat("/".concat(name)));//add directory (deisgnated by 'true' value)
              list.add(e); 
          }  
        }
        
        return list;
      } catch(Exception e) {  
        e.printStackTrace();  
      }  
      return list;
    }  
}
