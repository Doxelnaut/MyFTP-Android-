/*
 * listAdapter: adapter used for displaying the list of files and directories in the FTP Client
 */
package com.example.myftp;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class listAdapter extends ArrayAdapter<ListEntry>{
	
	   private final Context context;
       private final ArrayList<ListEntry> modelsArrayList;

       //constructor
       public listAdapter(Context context, ArrayList<ListEntry> modelsArrayList) {
           super(context, R.layout.list_item, modelsArrayList);
           this.context = context;
           this.modelsArrayList = modelsArrayList;
       }
 //------------------------------------------------------------------------------------------
       /*
        * (non-Javadoc)
        * @see android.widget.ArrayAdapter#getView(int, android.view.View, android.view.ViewGroup)
        */
       public View getView(int position, View convertView, ViewGroup parent) {
    	   
           //Create inflater
           LayoutInflater inflater = (LayoutInflater) context
               .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

           //Get rowView from inflater
           View rowView = null;
           
           //test if current 'position' is a directory
           if(modelsArrayList.get(position).dir){
        	   
               rowView = inflater.inflate(R.layout.list_item, parent, false);

               //Get icon and title views from the rowView
               ImageView imgView = (ImageView) rowView.findViewById(R.id.icon);
               TextView titleView = (TextView) rowView.findViewById(R.id.title);

               //Set the text and icon for textView
               imgView.setImageResource(R.drawable.folder);  			//folder icon
               titleView.setText(modelsArrayList.get(position).name);
           }
           else{
        	   
        	   //inflate view
        	   rowView = inflater.inflate(R.layout.list_item, parent, false);
        	   
        	   //get icon and title views from the rowView
               ImageView imgView = (ImageView) rowView.findViewById(R.id.icon);
               TextView titleView = (TextView) rowView.findViewById(R.id.title);

               //Set the text and icon for textView
               imgView.setImageResource(R.drawable.file1);
               titleView.setText(modelsArrayList.get(position).name);
           }
           return rowView;
       }
}
