package com.example.spiride.datalist;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.example.spiride.R;
import com.example.spiride.util.SpirideDBHelper;

public class DataListFragment extends Fragment 
{  
	private ArrayList<HashMap<String, Object>> listItem;
	private SimpleAdapter myAdapter;
	private ListView lv;
	private SpirideDBHelper dbHelper;
	
	private Handler handler;
	
    @Override  
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) 
    {  
        View view = inflater.inflate(R.layout.fragment_data_list, null);  
        
        dbHelper = new SpirideDBHelper(this.getActivity());
        
        //ListView lv= (ListView)view.findViewById(R.id.route_List);
        lv= (ListView)view.findViewById(R.id.list_data_List);
        
        /* 在数组中存放数据 */
		listItem = new ArrayList<HashMap<String, Object>>();

		//dbHelper.getAllSlopeData(listItem);
		dbHelper.getAllSlopeDataInDegree(listItem);
		//dbHelper.getSlopeDataByRouteName(listItem, routeName);
		
		
		//SimpleAdapter smAdapter = new SimpleAdapter(this.getActivity(), listItem,
		//		R.layout.fragment_route_list_item, new String[] { "id", "name", "start",
		//				"end","number" }, new int[] { R.id.route_list_item_routeID, R.id.route_list_item_routeName,
		//				R.id.route_list_item_routeStart, R.id.route_list_item_routeEnd, R.id.route_list_item_routeSampleNum});
		//lv.setAdapter(smAdapter);
		myAdapter = new SimpleAdapter(this.getActivity(), listItem, R.layout.fragment_data_list_item ,new String[] { "id", "name","degree" }, new int[] { R.id.data_list_item_dataID,
		R.id.data_list_item_routeName, R.id.data_list_item_degree });
		lv.setAdapter(myAdapter);
		
		setHandler();
        return view;
    }  
  
    
    @Override
    public void onHiddenChanged(boolean hidden)
    {
    	Toast.makeText(this.getActivity(),"HiddenChanged:"+hidden, Toast.LENGTH_SHORT).show();
    }
    
    private void setHandler()
    {
    	final Context ct = this.getActivity().getApplicationContext();
    	
    	handler = new Handler()
		{
			@Override
			public void handleMessage(Message msg)
			{
				
				switch (msg.what)
				{
					case 0: //更新列表，根据线路名词显示对应的线路数据
						String routeName = msg.obj.toString();
						if(!routeName.equalsIgnoreCase("ALL"))
						{
							listItem.clear();
	                    	//dbHelper.getSlopeDataByRouteName(listItem, routeName);
							dbHelper.getSlopeDataInDegreeByRouteName(listItem, routeName);
	                    	myAdapter.notifyDataSetChanged();
						}
						else
						{
							listItem.clear();
	                    	//dbHelper.getAllSlopeData(listItem);
							dbHelper.getAllSlopeDataInDegree(listItem);
	                    	myAdapter.notifyDataSetChanged();
						}
						
                    	Toast.makeText(ct,"列表已更新", Toast.LENGTH_SHORT).show();
						break;
				}
			}
		};
    }
    
    public Handler getHandler()
    {
    	return this.handler;
    }
}  
