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

       public listAdapter(Context context, ArrayList<ListEntry> modelsArrayList) {

           super(context, R.layout.list_item, modelsArrayList);

           this.context = context;
           this.modelsArrayList = modelsArrayList;
       }
 //------------------------------------------------------------------------------------------
       
       public View getView(int position, View convertView, ViewGroup parent) {
    	   
           // 1. Create inflater
           LayoutInflater inflater = (LayoutInflater) context
               .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

           // 2. Get rowView from inflater

           View rowView = null;
           if(modelsArrayList.get(position).dir){
        	   
               rowView = inflater.inflate(R.layout.list_item, parent, false);

               // 3. Get icon,title & counter views from the rowView
               ImageView imgView = (ImageView) rowView.findViewById(R.id.icon);
               TextView titleView = (TextView) rowView.findViewById(R.id.title);

               // 4. Set the text for textView
               imgView.setImageResource(R.drawable.folder);
               titleView.setText(modelsArrayList.get(position).name);
           }
           else{
                   rowView = inflater.inflate(R.layout.list_item, parent, false);
                   ImageView imgView = (ImageView) rowView.findViewById(R.id.icon);
                   TextView titleView = (TextView) rowView.findViewById(R.id.title);

                   // 4. Set the text for textView
                   imgView.setImageResource(R.drawable.file1);
                   titleView.setText(modelsArrayList.get(position).name);
           }

           // 5. retrn rowView
           return rowView;
       }

}
