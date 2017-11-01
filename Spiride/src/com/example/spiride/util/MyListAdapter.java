package com.example.spiride.util;

import java.util.ArrayList;
import java.util.HashMap;

import com.example.spiride.R;
import com.example.spiride.R.id;
import com.example.spiride.R.layout;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class MyListAdapter extends BaseAdapter
{
	private LayoutInflater mInflater = null;  
	ArrayList<HashMap<String, Object>> listItem;
	
    public MyListAdapter(Context context, ArrayList<HashMap<String, Object>> listItem)  
    {  
        this.mInflater = LayoutInflater.from(context);
        this.listItem = listItem;
    }
    
    @Override  
    public int getCount() {  
        // How many items are in the data set represented by this Adapter.(在此适配器中所代表的数据集中的条目数)  
        return listItem.size();  
    }  

    @Override  
    public Object getItem(int position) {  
        // Get the data item associated with the specified position in the data set.(获取数据集中与指定索引对应的数据项)  
        return listItem.get(position);  
    }  

    @Override  
    public long getItemId(int position) {  
        // Get the row id associated with the specified position in the list.(取在列表中与指定索引对应的行id)  
        return 0;  
    }  

    @Override  
    public View getView(int position, View convertView, ViewGroup parent) {  
        // Get a View that displays the data at the specified position in the data set.  
    	convertView = mInflater.inflate(R.layout.fragment_route_list_item, null);
    	HashMap<String, Object> map = listItem.get(position);
    	
    	TextView routeID = (TextView)convertView.findViewById(R.id.route_list_item_routeID);
    	TextView routeName = (TextView)convertView.findViewById(R.id.route_list_item_routeName);
    	TextView routeStart = (TextView)convertView.findViewById(R.id.route_list_item_routeStart);
    	TextView routeEnd = (TextView)convertView.findViewById(R.id.route_list_item_routeEnd);
    	TextView routeSampleNum = (TextView)convertView.findViewById(R.id.route_list_item_routeSampleNum);
    	
    	routeID.setText(map.get(SpirideDBHelper.COLUMN_NAME_ROUTEID).toString());
    	routeName.setText(map.get(SpirideDBHelper.COLUMN_NAME_ROUTENAME).toString());
    	routeStart.setText(map.get(SpirideDBHelper.COLUMN_NAME_ROUTESTART).toString());
    	routeEnd.setText(map.get(SpirideDBHelper.COLUMN_NAME_ROUTEEND).toString());
    	routeSampleNum.setText(map.get(SpirideDBHelper.COLUMN_NAME_ROUTESAMPLENUMBER).toString());
    	
    	return convertView;  
    }  
}
