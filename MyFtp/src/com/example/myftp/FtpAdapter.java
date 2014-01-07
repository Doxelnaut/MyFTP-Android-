package com.example.myftp;

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

	String address;
	String user;
	String pass;
	int port;
	FTPClient client;
	final Context context;
	String TAG = "FTPClient";
	String localDir = Environment.getExternalStorageDirectory().toString();
	
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
	int ftpConnect() throws SocketException, IOException{
		client.connect(address, port);
		client.enterLocalPassiveMode();
		client.login(user, pass);
		
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
	void ftpDisconnect() throws IOException{
		client.logout();
		client.disconnect();
	}
//---------------------------------------------------------------------------------------------
	
	String getWD() {
		String dir = null;
		try {
			dir = client.printWorkingDirectory();
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Error retreiving working directory");
			return null;
		}
		return dir;
	}
//--------------------------------------------------------------------------------------------
	
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

	int readReply(){
		
		int reply = 0;
		reply = client.getReplyCode();
		return reply;
	}
	
//------------------------------------------------------------------------------------------------
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
	int pullFile(String src, String dest){
		FileOutputStream desFileStream;
		try {
			desFileStream = new FileOutputStream(dest);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("Error creating output stream");
			return 1;
		};  
		
	      try {
			client.retrieveFile(src, desFileStream);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("Error retrieving file");
			return 2;
		}  
	      try {
			desFileStream.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("Error closing file stream");
			return 3;
		}  
	      return 0;
	}
//---------------------------------------------------------------------------------------------
	
	int putFile(String src, String dest){
		   FileInputStream srcFileStream;
		try {
			srcFileStream = context.openFileInput(src);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("Error opening file for put");
			return 1;
		}  
		   try {
			client.storeFile(dest, srcFileStream);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("Error writing file to server");
			return 2;
		}  
		   try {
			srcFileStream.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("Error closing stream");
			return 3;
		} 
		   return 0;
	}
//------------------------------------------------------------------------------------------------
	
	public ArrayList<ListEntry> ftpPrintFilesList()  
    {  
		ArrayList<ListEntry> list = new ArrayList<ListEntry>();
		 ListEntry current = new ListEntry(".",true);
	        list.add(current); 
	        ListEntry parent = new ListEntry("..",true);
	        list.add(parent);
      try {  
        FTPFile[] ftpFiles = client.listFiles(".");  
        int length = ftpFiles.length;  
        for (int i = 0; i < length; i++) {  
          String name = ftpFiles[i].getName();  
          boolean isFile = ftpFiles[i].isFile();  
          if (isFile) {  
            ListEntry e = new ListEntry(name,false);
            list.add(e);
          }  
          else {  
        	  ListEntry e = new ListEntry(name,true);
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
