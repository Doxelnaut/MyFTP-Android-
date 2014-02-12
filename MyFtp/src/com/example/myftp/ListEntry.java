/*
 * ListEntry: stores information about file or dir entry, stored in an ArrayList to be used as a viewAdapter
 */
package com.example.myftp;

public class ListEntry {

	String name;
	boolean dir = false;
	String path;

	//constructor
	ListEntry(String n, boolean d, String p){
		name = n;
		dir = d;
		path = p;
	}
}

