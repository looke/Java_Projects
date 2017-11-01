package com.example.spiride.collection;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.example.spiride.MainActivity;
import com.example.spiride.R;
import com.example.spiride.R.id;
import com.example.spiride.R.layout;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

public class BluetoothFragment extends Fragment implements OnClickListener
{  
	// 得到BluetoothAdapter对象
	public BluetoothAdapter 	blAdapter;
	public Spinner 			sp_blueList;
	public UUID				uuid	= UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
	public BluetoothSocket socketInUse;
	public BluetoothDevice deviceInUse;
	public Handler handler;
	Button btn_connect;
	Button btn_disconnect;
	
	public ProgressDialog myDialog;
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {  
        
		View view = inflater.inflate(R.layout.fragment_collect_bluetooth, null);  
		sp_blueList = (Spinner) view.findViewById(R.id.spinner_bluetooth_list);
        btn_connect = (Button)view.findViewById(R.id.btn_bluetooth_connect);
        btn_disconnect = (Button)view.findViewById(R.id.btn_bluetooth_disconnect);
        
        //handler = ((MainActivity)this.getActivity()).getHandler();
        setHandler();
        deviceInUse = ((MainActivity)this.getActivity()).getBluetoothDevice();
        socketInUse = ((MainActivity)this.getActivity()).getBluetoothSocket();
        if(deviceInUse == null)
        {
        	blAdapter = BluetoothAdapter.getDefaultAdapter();
            List<String> spinner_list = new ArrayList<String>();
            
            if (blAdapter != null)
    		{
    			// 调用isEnabled()方法判断当前蓝牙设备是否可用
    			if (!blAdapter.isEnabled())
    			{
    				// 如果蓝牙设备不可用的话,创建一个intent对象,该对象用于启动一个Activity,提示用户启动蓝牙适配器
    				Intent intent = new Intent(
    						BluetoothAdapter.ACTION_REQUEST_ENABLE);
    				startActivityForResult(intent,1);
    			}

    			// 得到所有已经配对的蓝牙适配器对象
    			Set<BluetoothDevice> devices = blAdapter.getBondedDevices();
    			// strs = new String[devices.size()];
    			// int i=0;
    			String spinner_content;
    			for (Iterator iterator = devices.iterator(); iterator.hasNext();)
    			{
    				// 得到BluetoothDevice对象,也就是说得到配对的蓝牙适配器
    				BluetoothDevice device = (BluetoothDevice) iterator.next();
    				spinner_content = device.getName() + " " + device.getAddress();
    				spinner_list.add(spinner_content);
    			}
    			
    			//适配器
    			ArrayAdapter<String> arr_adapter= new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_item, spinner_list);
    	        //设置样式
    	        arr_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    	        //加载适配器
    	        sp_blueList.setAdapter(arr_adapter);
    	        btn_disconnect.setEnabled(false);
    		}
        }
        else
        {
        	List<String> spinner_list = new ArrayList<String>();
        	
        	String spinner_content = deviceInUse.getName() + " " + deviceInUse.getAddress();
			spinner_list.add(spinner_content);
			
        	//适配器
			ArrayAdapter<String> arr_adapter= new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_item, spinner_list);
	        //设置样式
	        arr_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	        //加载适配器
	        sp_blueList.setAdapter(arr_adapter);
	        btn_connect.setEnabled(false);
	        btn_disconnect.setEnabled(true);
        }
        btn_connect.setOnClickListener(this);
        /*
        btn_connect.setOnClickListener(new OnClickListener(){
        	@Override
        	public void onClick(View v)
        	{
        		final ProgressDialog myDialog = ProgressDialog.show(
        				v.getContext(), "蓝牙连接提示", "蓝牙设备处理中...", false);
        		
        		new Thread()
    			{
    				public void run()
    				{
    					if (blAdapter != null)
    	        		{
    	        			String blueHDaddress = sp_blueList.getSelectedItem().toString();
    	        			blueHDaddress = blueHDaddress.split(" ")[1];
    	        			BluetoothDevice device = null;
    	        			BluetoothSocket socket;
    	        			// 得到所有已经配对的蓝牙适配器对象
    	        			Set<BluetoothDevice> devices = blAdapter.getBondedDevices();
    	        			// strs = new String[devices.size()];
    	        			// int i=0;
    	        			
    	        			for (Iterator iterator = devices.iterator(); iterator.hasNext();)
    	        			{
    	        				// 得到BluetoothDevice对象,也就是说得到配对的蓝牙适配器
    	        				device = (BluetoothDevice) iterator.next();
    	        				if(blueHDaddress.equalsIgnoreCase(device.getAddress()))
    	        				{
    	        					break;
    	        				}
    	        			}
    	        			
    	        			if(device == null)
    	        			{
    	        				Message msg = new Message();
    							msg.what = 2;
    							handler.sendMessage(msg);
    	        				return;
    	        			}
    	        			
    	        			try
    						{
    							socket = device.createRfcommSocketToServiceRecord(uuid);
    							socket.connect();
    							socketInUse = socket;
    							
    							Message msg = new Message();
    							msg.what = 0;
    							handler.sendMessage(msg);
    						} 
    	        			catch (IOException e)
    						{
    							// TODO Auto-generated catch block
    							e.printStackTrace();
    							Message msg = new Message();
    							msg.what = 1;
    							handler.sendMessage(msg);
    						}
    	        			finally
    	        			{
    	        				myDialog.dismiss();
    	        			}
    	        		}
    				}
    			}.start();
        	}
        });
        */
        btn_disconnect.setOnClickListener(this);
        /*
        btn_disconnect.setOnClickListener(new OnClickListener(){
        	@Override
        	public void onClick(View v)
        	{
        		try
				{
					socketInUse.close();
					socketInUse = null;
					Message msg = new Message();
					msg.what = 4;
					handler.sendMessage(msg);
					
				} 
        		catch (IOException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
					Message msg = new Message();
					msg.what = 1;
					handler.sendMessage(msg);
				}
        		
        	}
        });
        */
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
    	final MainActivity act = (MainActivity)this.getActivity();
    	handler = new Handler()
		{
			@Override
			public void handleMessage(Message msg)
			{
				
				switch (msg.what)
				{
				case 0:
					btn_connect.setEnabled(false);
					btn_disconnect.setEnabled(true);
					sp_blueList.setClickable(false);
					//BluetoothDevice device = (BluetoothDevice)msg.obj;
					act.setBluetoothDeviceAndSocket(deviceInUse, socketInUse);
					Toast.makeText(ct, "蓝牙连接成功", 1).show();
					break;
				case 1:
					Toast.makeText(ct, "蓝牙操作失败，请重试", 1)
							.show();
					break;
				case 3:
					Toast.makeText(ct, "无蓝牙适配器，请重启应用", 1)
							.show();
					break;
				case 4:
					btn_connect.setEnabled(true);
					btn_disconnect.setEnabled(false);
					sp_blueList.setClickable(true);
					Toast.makeText(ct, "蓝牙连接已断开", 1).show();
					act.cleanBluetoothDeviceAndSocket();
					break;
				case 5:
					
					break;
				}
				
			}
		};
    }
    
    @Override
	public void onClick(View v)
	{
    	int id = v.getId();
    	
    	switch (id)
    	{
    		case R.id.btn_bluetooth_connect:
    			connect();
    			break;
    		case R.id.btn_bluetooth_disconnect:
    			disconnect();
    			break;
    	}
	}
    
    private void disconnect()
    {
    	try
		{
			socketInUse.close();
			socketInUse = null;
			Message msg = new Message();
			msg.what = 4;
			handler.sendMessage(msg);
			((MainActivity)this.getActivity()).setBluetoothSocketInUse(null);
			//conn_result.setResult(false);
		} 
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			Message msg = new Message();
			msg.what = 1;
			handler.sendMessage(msg);
		}
    	
    }
    
    private void connect()
    {
    	myDialog = ProgressDialog.show(
				this.getActivity(), "蓝牙连接提示", "蓝牙设备处理中...", false);
    	
    	new BluetoothConnThread(this).start();
    	/*
    	new Thread()
		{
			public void run()
			{
				if (blAdapter != null)
        		{
        			String blueHDaddress = sp_blueList.getSelectedItem().toString();
        			blueHDaddress = blueHDaddress.split(" ")[1];
        			BluetoothDevice device = null;
        			BluetoothSocket socket;
        			// 得到所有已经配对的蓝牙适配器对象
        			Set<BluetoothDevice> devices = blAdapter.getBondedDevices();
        			// strs = new String[devices.size()];
        			// int i=0;
        			
        			for (Iterator iterator = devices.iterator(); iterator.hasNext();)
        			{
        				// 得到BluetoothDevice对象,也就是说得到配对的蓝牙适配器
        				device = (BluetoothDevice) iterator.next();
        				if(blueHDaddress.equalsIgnoreCase(device.getAddress()))
        				{
        					break;
        				}
        			}
        			
        			if(device == null)
        			{
        				Message msg = new Message();
						msg.what = 2;
						handler.sendMessage(msg);
        				return;
        			}
        			
        			try
					{
						socket = device.createRfcommSocketToServiceRecord(uuid);
						socket.connect();
						socketInUse = socket;
						synchronized (conn_result)
				    	{
							conn_result.setResult(true);
				    	}
						Message msg = new Message();
						msg.what = 0;
						handler.sendMessage(msg);
					} 
        			catch (IOException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
						Message msg = new Message();
						msg.what = 1;
						handler.sendMessage(msg);
					}
        			finally
        			{
        				myDialog.dismiss();
        				synchronized (conn_result)
				    	{
        					conn_result.notify();
				    	}
        			}
        		}
			}
		}.start();
		
    	synchronized (conn_result)
    	{
    		try
    		{
    			conn_result.wait();
    		} 
    		catch (InterruptedException e)
    		{
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
    	}

		if(conn_result.getResult())
		{
			((MainActivity)this.getActivity()).setBluetoothSocketInUse(socketInUse);
		}
		*/
    }
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) 
    {
        if(requestCode == 1 && resultCode == -1)
        {
        	//刷新Spinner
        	List<String> spinner_list = new ArrayList<String>();
        	// 得到所有已经配对的蓝牙适配器对象
        	Set<BluetoothDevice> devices = blAdapter.getBondedDevices();
        	// strs = new String[devices.size()];
        	// int i=0;
        	String spinner_content;
        	for (Iterator iterator = devices.iterator(); iterator.hasNext();)
        	{
        		// 得到BluetoothDevice对象,也就是说得到配对的蓝牙适配器
        		BluetoothDevice device = (BluetoothDevice) iterator.next();
        		spinner_content = device.getName() + " " + device.getAddress();
        		spinner_list.add(spinner_content);
        	}
        				
        	//适配器
        	ArrayAdapter<String> arr_adapter= new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_item, spinner_list);
        	//设置样式
        	arr_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        	//加载适配器
        	sp_blueList.setAdapter(arr_adapter);
        	arr_adapter.notifyDataSetChanged();
        }
    }
    
    public void setBluetoothDisconnectDisable()
    {
    	btn_disconnect.setEnabled(false);
    }
    
    
    public void setBluetoothDisconnectEnable()
    {
    	btn_disconnect.setEnabled(true);
    }
}  
