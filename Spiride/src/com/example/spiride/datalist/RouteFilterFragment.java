package com.example.spiride.datalist;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Fragment;
import android.bluetooth.BluetoothAdapter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.spiride.R;
import com.example.spiride.util.SpirideDBHelper;

public class RouteFilterFragment extends Fragment
{  
	// 得到BluetoothAdapter对象
	public BluetoothAdapter 	blAdapter;
	public Spinner 			sp_routeList;

	private Handler dataList_handler;

	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) 
	{  
        
		View view = inflater.inflate(R.layout.fragment_datalist_routefilter, null);  
		sp_routeList = (Spinner) view.findViewById(R.id.spinner_datalist_routefilter);
		
		SpirideDBHelper dbHelper = new SpirideDBHelper(this.getActivity());
		ArrayList<HashMap<String, Object>> listItem = new ArrayList<HashMap<String, Object>>();
		dbHelper.getAllRouteData(listItem);
		
        List<String> spinner_list = new ArrayList<String>();
        spinner_list.add("ALL");
        for(int i=0; i<listItem.size();i++)
        {
        	HashMap<String, Object> map = listItem.get(i);
        	String showRouteInfo = map.get(SpirideDBHelper.COLUMN_NAME_ROUTENAME).toString() 
        			+ ":" + map.get(SpirideDBHelper.COLUMN_NAME_ROUTESTART).toString() 
        			+ " - "+map.get(SpirideDBHelper.COLUMN_NAME_ROUTEEND).toString();
        	spinner_list.add(showRouteInfo);
        }

		//适配器
		ArrayAdapter<String> arr_adapter= new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_item, spinner_list);
	    //设置样式
	    arr_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	    //加载适配器
	    sp_routeList.setAdapter(arr_adapter);
		
	    sp_routeList.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){
	    	@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
			 int arg2, long arg3) 
	    	{
	    		String routeInfo = sp_routeList.getSelectedItem().toString();
	    		routeInfo = routeInfo.split(":")[0];
	    		
	    		
	    		DataListFragment dlFrag = (DataListFragment)getFragmentManager().findFragmentByTag("datalist");
	    		dataList_handler = dlFrag.getHandler();
	    		Message msg = new Message();
	    		msg.what=0;
	    		msg.obj=routeInfo;
	    		if(dataList_handler!= null)
	    		{
	    			dataList_handler.sendMessage(msg);
	    		}
	    	}
	    	
	    	@Override
			public void onNothingSelected(AdapterView<?> arg0) 
	    	{
	    		
	    	}
	    });
	    
        return view;  
    }  
  
	public void setHandler(Handler hd)
	{
		this.dataList_handler = hd;
	}
	
    @Override
    public void onHiddenChanged(boolean hidden)
    {
    	Toast.makeText(this.getActivity(),"HiddenChanged:"+hidden, Toast.LENGTH_SHORT).show();
    }
    
}  
