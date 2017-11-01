package com.example.spiride;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.spiride.collection.BluetoothFragment;
import com.example.spiride.collection.CollectionDisplayFragment;
import com.example.spiride.collection.CollectionThread;
import com.example.spiride.datalist.DataListFragment;
import com.example.spiride.datalist.RouteFilterFragment;
import com.example.spiride.route.RouteListFragment;
import com.example.spiride.route.RouteOpFragment;
import com.example.spiride.util.SpirideDBHelper;

public class MainActivity extends Activity
{
	private FragmentManager 	fragmentManager;  
    private RadioGroup 			radioGroup;  
	SQLiteDatabase 				db;
	SpirideDBHelper 			dbHelper;
	int selectedRouteID = -1;
	
	Handler handler;
	
	BluetoothSocket socketInUse = null;
	BluetoothDevice blueDeviceInUse = null;
	
	boolean isInCollection = false;
	
	private CollectionThread dataThread;
	
	RadioButton routeBtn;
	RadioButton collectBtn;
	RadioButton dataBtn;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		setupDB();
		
		
		routeBtn = (RadioButton)findViewById(R.id.rg_route_button);
		collectBtn = (RadioButton)findViewById(R.id.rg_collection_button);
		dataBtn = (RadioButton)findViewById(R.id.rg_data_button);
		final int routeBtnID = routeBtn.getId();
		final int collectBtnID = collectBtn.getId();
		final int dataBtnID = dataBtn.getId();
		
		fragmentManager = getFragmentManager();  
		
		//设置默认选中并加载对应的Fragment
		initRouteFragment();

        //设置监听
		radioGroup = (RadioGroup) findViewById(R.id.rg_tab);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {  
            @Override  
            public void onCheckedChanged(RadioGroup group, int checkedId) 
            {  
            	
            	if(checkedId == routeBtnID)
            	{
            		//if(isInCollection)
            		//{
            		//	Toast.makeText(MainActivity.this, "请先退出采集状态", 1).show();
            		//	return;
            		//}
            		initRouteFragment();
            		//isInCollection = false;
            	}
            	if(checkedId == collectBtnID)
            	{
            		initCollectFragment();
            	}
            	if(checkedId == dataBtnID)
            	{
            		//if(isInCollection)
            		//{
            		//	Toast.makeText(MainActivity.this, "请先退出采集状态", 1).show();
            		//	return;
            		//}
            		initDataListFragment();
            		//isInCollection = false;
            	}
            }  
        });
        
	}
	
	private void setupDB()
	{
		dbHelper = new SpirideDBHelper(this);
		db = dbHelper.getWritableDatabase();
		//db.execSQL("delete from slope where _id=359");
		//db =   openOrCreateDatabase("/data/data/com.example.spiride/databases/data.db", Context.MODE_PRIVATE, null);
	}
	
	public SpirideDBHelper getDBHelper()
	{
		return dbHelper;
	}
	
	private void initRouteFragment()
	{
		RadioButton rb = (RadioButton) findViewById(R.id.rg_route_button);
		rb.setChecked(true);
		FragmentTransaction transaction_list = fragmentManager.beginTransaction();
		Fragment fragment_list = new RouteListFragment();
		transaction_list.replace(R.id.content_3, fragment_list, "routeList");  
		transaction_list.commit();
        
		FragmentTransaction transaction_op = fragmentManager.beginTransaction();
		Fragment fragment_op = new RouteOpFragment();
		transaction_op.replace(R.id.content, fragment_op, "routeOP");  
		transaction_op.commit();
	}
	
	private void initCollectFragment()
	{
		FragmentTransaction transaction_blueTooth = fragmentManager.beginTransaction();
		Fragment fragment_blueTooth = new BluetoothFragment();
		transaction_blueTooth.replace(R.id.content, fragment_blueTooth, "bluetooth");  
		transaction_blueTooth.commit();
		
		FragmentTransaction transaction_dataDisplay = fragmentManager.beginTransaction();
		Fragment fragment_dataDisplay = new CollectionDisplayFragment();
		transaction_dataDisplay.replace(R.id.content_3, fragment_dataDisplay, "collection");  
		transaction_dataDisplay.commit();
	}

	private void initDataListFragment()
	{
		FragmentTransaction transaction_RouteFilter = fragmentManager.beginTransaction();
		RouteFilterFragment fragment_RouteFilter = new RouteFilterFragment();
		FragmentTransaction transaction_dataList = fragmentManager.beginTransaction();
		DataListFragment fragment_dataList = new DataListFragment();
		
		transaction_RouteFilter.replace(R.id.content, fragment_RouteFilter, "routefilter");  
		transaction_RouteFilter.commit();
		
		transaction_dataList.replace(R.id.content_3, fragment_dataList, "datalist");  
		transaction_dataList.commit();
		
	}
	public Handler getHandler()
	{
		return this.handler;
	}
	
	public void setBluetoothSocketInUse(BluetoothSocket socket)
	{
		this.socketInUse = socket;
	}
	
	public BluetoothSocket getBluetoothSocketInUse()
	{
		return this.socketInUse;
	}
	
	public void setSelectedRouteID(int id)
	{
		this.selectedRouteID = id;
	}
	
	public int getSelectedRouteID()
	{
		return selectedRouteID;
	}
	
	public void startCollection(CollectionThread dataThread)
	{
		isInCollection = true;
		this.dataThread = dataThread;
		//routeBtn.setEnabled(false);
	}
	
	public void stopCollection()
	{
		isInCollection = false;
		dataThread = null;
	}
	
	public boolean isInCollection()
	{
		return this.isInCollection;
	}
	
	public void setBluetoothDeviceAndSocket(BluetoothDevice device, BluetoothSocket socket)
	{
		this.blueDeviceInUse = device;
		this.socketInUse = socket;
	}
	
	public BluetoothDevice getBluetoothDevice()
	{
		return this.blueDeviceInUse;
	}
	
	public BluetoothSocket getBluetoothSocket()
	{
		return this.socketInUse;
	}
	
	public void cleanBluetoothDeviceAndSocket()
	{
		this.blueDeviceInUse = null;
		this.socketInUse = null;
	}
	
	public CollectionThread getCollectionThread()
	{
		return this.dataThread;
	}
}
