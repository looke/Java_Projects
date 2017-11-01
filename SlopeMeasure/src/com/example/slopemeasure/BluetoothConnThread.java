package com.example.slopemeasure;

import java.io.IOException;
import java.util.Iterator;
import java.util.Set;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Message;

public class BluetoothConnThread extends Thread
{
	MainActivity act;
	
	public BluetoothConnThread(MainActivity act)
	{
		this.act = act;
	}
	
	private BluetoothDevice findBluetoothDevice(String blueHDaddress)
	{
		Set<BluetoothDevice> devices = act.blAdapter.getBondedDevices();
		// strs = new String[devices.size()];
		// int i=0;
		BluetoothDevice device = null;
		for (Iterator iterator = devices.iterator(); iterator.hasNext();)
		{
			// 得到BluetoothDevice对象,也就是说得到配对的蓝牙适配器
			device = (BluetoothDevice) iterator.next();
			if(blueHDaddress.equalsIgnoreCase(device.getAddress()))
			{
				return device;
			}
		}
		return null;
	}
	
	public void run()
	{
		if (act.blAdapter != null)
		{
			if(act.sp_blueList.getSelectedItem() == null)
			{
				Message msg = new Message();
				msg.what = 5;
				act.handler.sendMessage(msg);
				act.myDialog.dismiss();
				return;
			}
			String blueHDaddress = act.sp_blueList.getSelectedItem().toString();
			
			
			blueHDaddress = blueHDaddress.split(" ")[1];
			BluetoothDevice device = findBluetoothDevice(blueHDaddress);
			BluetoothSocket socket;
			
			if(device == null)
			{
				Message msg = new Message();
				msg.what = 2;
				act.handler.sendMessage(msg);
				return;
			}
			
			try
			{
				socket = device.createRfcommSocketToServiceRecord(act.uuid);
				socket.connect();
				act.socketInUse = socket;
				act.deviceInUse = device;
				
				Message msg = new Message();
				msg.what = 0;
				//msg.obj = device;
				act.handler.sendMessage(msg);
			} 
			catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
				Message msg = new Message();
				msg.what = 1;
				act.handler.sendMessage(msg);
			}
			finally
			{
				act.myDialog.dismiss();
			}
		}
	}
	
}
