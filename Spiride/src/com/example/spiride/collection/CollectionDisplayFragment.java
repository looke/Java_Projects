package com.example.spiride.collection;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Fragment;
import android.app.FragmentManager;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.spiride.MainActivity;
import com.example.spiride.R;
import com.example.spiride.util.DegreeCalc;
import com.example.spiride.util.SpirideDBHelper;

public class CollectionDisplayFragment extends Fragment  implements OnClickListener
{  
	BluetoothSocket socketInUse = null;
	Button btn_collect;
	Button btn_stopCollect;
	
	TextView tx_RouteName;
	TextView tx_RouteStart;
	TextView tx_RouteEnd;
	TextView tx_RouteSampleNum;
	
	Handler handler;
	
	private TextView 			degreeNum;
    private TextView 			percentNum;
    
    CollectionThread dataThread;
    
    private int selectedRouteID;
	@Override  
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {  
        
		View view = inflater.inflate(R.layout.fragment_collect_datadisplay, null);  
		
		btn_collect = (Button)view.findViewById(R.id.btn_data_display_startupcollect);
        btn_stopCollect = (Button)view.findViewById(R.id.btn_data_display_stopcollect);
        btn_stopCollect.setEnabled(false);
        
        btn_collect.setOnClickListener(this);
        btn_stopCollect.setOnClickListener(this);
        
        degreeNum = (TextView)view.findViewById(R.id.text_data_display_slopeDegreeNum);
        percentNum = (TextView)view.findViewById(R.id.text_data_display_slopePercentNum);
        setHandler();
        
        
        if(((MainActivity)this.getActivity()).isInCollection())
        {
        	btn_collect.setEnabled(false);
        	btn_stopCollect.setEnabled(true);
        	this.dataThread = ((MainActivity)this.getActivity()).getCollectionThread();
        }
        
        tx_RouteName = (TextView)view.findViewById(R.id.text_collect_routeinfo_name);
    	tx_RouteStart = (TextView)view.findViewById(R.id.text_collect_routeinfo_start);
    	tx_RouteEnd = (TextView)view.findViewById(R.id.text_collect_routeinfo_end);
    	tx_RouteSampleNum = (TextView)view.findViewById(R.id.text_collect_routeinfo_samplenumber_title);
    	
    	
        selectedRouteID = ((MainActivity)this.getActivity()).getSelectedRouteID();
        
        initRouteInfo();
        
        return view;  
    }  
  
    @Override
    public void onHiddenChanged(boolean hidden)
    {
    	Toast.makeText(this.getActivity(),"HiddenChanged:"+hidden, Toast.LENGTH_SHORT).show();
    }
    
    @Override
	public void onClick(View v)
	{
    	int id = v.getId();
    	
    	switch (id)
    	{
    		case R.id.btn_data_display_startupcollect:
    			startCollection();
    			((MainActivity)this.getActivity()).startCollection(dataThread);
    			break;
    		case R.id.btn_data_display_stopcollect:
    			stopCollection();
    			((MainActivity)this.getActivity()).stopCollection();
    			break;
    	}
	}
    
    private void startCollection()
    {
    	BluetoothSocket socket = ((MainActivity)this.getActivity()).getBluetoothSocketInUse();
    	if(null == socket)
    	{
    		Toast.makeText(this.getActivity(), "无法启动采集，请先建立蓝牙连接", 1)
			.show();
    		return;
    	}
    	FragmentManager fragmentManager = ((MainActivity)this.getActivity()).getFragmentManager();
    	BluetoothFragment bluetoothFrag = (BluetoothFragment)(fragmentManager.findFragmentByTag("bluetooth"));
    	bluetoothFrag.setBluetoothDisconnectDisable();
    	dataThread = new CollectionThread(this.handler, ((MainActivity)this.getActivity()).getDBHelper(), tx_RouteName.getText().toString());
    	dataThread.setBlueToothSocket(socket);
    	dataThread.start();
    	btn_stopCollect.setEnabled(true);
    	btn_collect.setEnabled(false);
    	Toast.makeText(this.getActivity(), "数据采集已启动", 1).show();
    }
    
    private void stopCollection()
    {
    	dataThread.setStop();
    	FragmentManager fragmentManager = ((MainActivity)this.getActivity()).getFragmentManager();
    	BluetoothFragment bluetoothFrag = (BluetoothFragment)(fragmentManager.findFragmentByTag("bluetooth"));
    	bluetoothFrag.setBluetoothDisconnectEnable();
    	Toast.makeText(this.getActivity(), "数据采集已停止", 1).show();
    	dataThread = null;
    	btn_stopCollect.setEnabled(false);
    	btn_collect.setEnabled(true);
    	degreeNum.setText("--");
		percentNum.setText("--");
		degreeNum.setTextColor(Color.rgb(88, 88, 88));
		percentNum.setTextColor(Color.rgb(88, 88, 88));
    }
    
    private void setHandler()
    {
    	final Context ct = this.getActivity().getApplicationContext();
    	final MainActivity act = (MainActivity)this.getActivity();
    	handler = new Handler()
		{
			@Override
			public void handleMessage(Message msg)
			{
				
				switch (msg.what)
				{
					case 0: //采集数据成功，刷新界面数据取值，刷新数据颜色
						int value = Integer.parseInt((String)msg.obj);
						double degree = DegreeCalc.calcDegree(value);
						double percent = DegreeCalc.calcPercentage(value);
						setDisplayColor(degree);
						//负数取反，界面通过颜色确定正负，数据入库值不会取反
						if(degree < 0)
						{
							degree = 0-degree;
							percent = 0-percent;
						}
						degreeNum.setText(String.format("%.1f",degree));
						percentNum.setText(String.format("%.1f",percent) + "%");
						
						int currentNum = Integer.valueOf(tx_RouteSampleNum.getText().toString());
						currentNum++;
						tx_RouteSampleNum.setText(String.valueOf(currentNum));
						break;
					case 1://采集异常处理，清除mainActivity的蓝牙适配记录
						act.cleanBluetoothDeviceAndSocket();
						Toast.makeText(ct, "数据采集线程异常，采集已停止", 1).show();
						break;
				}
			}
		};
    }
    
    private void setDisplayColor(double degree)
    {
    	if(degree > 0)
    	{
    		//大于零表示上坡，显示橙色
    		degreeNum.setTextColor(Color.rgb(255,97,0));
    		percentNum.setTextColor(Color.rgb(255,97,0));
    	}
    	else if(degree < 0)
    	{
    		//小于零表示下坡，显示绿土色
    		degreeNum.setTextColor(Color.rgb(46,139,87));
    		percentNum.setTextColor(Color.rgb(46,139,87));
    	}
    }
    
    private void initRouteInfo()
    {
    	if(this.selectedRouteID == -1)
    	{
    		tx_RouteName.setText("N/A");
    		tx_RouteStart.setText("N/A");
    		tx_RouteEnd.setText("N/A");
    		tx_RouteSampleNum.setText("N/A");
    	}
    	else
    	{
    		SpirideDBHelper dbHelper = new SpirideDBHelper(this.getActivity());
    		SQLiteDatabase db = dbHelper.getReadableDatabase();
    		ArrayList<HashMap<String, Object>> listItem = new ArrayList<HashMap<String, Object>>();
    		dbHelper.getAllRouteData(listItem);
    		HashMap<String, Object> map = null;
    		for (int i=0; i< listItem.size(); i++)
    		{
    			map = listItem.get(i);
    			String id = map.get(SpirideDBHelper.COLUMN_NAME_ROUTEID).toString();
    			
    			if(selectedRouteID == Integer.valueOf(id))
    			{
    				break;
    			}
    		}
    		if(map != null)
    		{
    			int number = dbHelper.getSampleNumberByRouteName(map.get(SpirideDBHelper.COLUMN_NAME_ROUTENAME).toString());
    		
    			tx_RouteName.setText(map.get(SpirideDBHelper.COLUMN_NAME_ROUTENAME).toString());
        		tx_RouteStart.setText(map.get(SpirideDBHelper.COLUMN_NAME_ROUTESTART).toString());
        		tx_RouteEnd.setText(map.get(SpirideDBHelper.COLUMN_NAME_ROUTEEND).toString());
        		tx_RouteSampleNum.setText(String.valueOf(number));
    		
    		}
    		
    	}
    }
}  
