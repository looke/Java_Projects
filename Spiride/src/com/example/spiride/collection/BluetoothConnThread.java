package com.example.spiride.collection;

import java.io.IOException;
import java.util.Iterator;
import java.util.Set;

import com.example.spiride.MainActivity;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Message;

public class BluetoothConnThread extends Thread
{
	BluetoothFragment frag;
	
	public BluetoothConnThread(BluetoothFragment f)
	{
		this.frag = f;
	}
	
	private BluetoothDevice findBluetoothDevice(String blueHDaddress)
	{
		Set<BluetoothDevice> devices = frag.blAdapter.getBondedDevices();
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
		if (frag.blAdapter != null)
		{
			if(frag.sp_blueList.getSelectedItem() == null)
			{
				Message msg = new Message();
				msg.what = 5;
				frag.handler.sendMessage(msg);
				frag.myDialog.dismiss();
				return;
			}
			String blueHDaddress = frag.sp_blueList.getSelectedItem().toString();
			/*
			if(blueHDaddress.isEmpty())
			{
				Message msg = new Message();
				msg.what = 4;
				frag.handler.sendMessage(msg);
				return;
			}
			*/
			
			blueHDaddress = blueHDaddress.split(" ")[1];
			BluetoothDevice device = findBluetoothDevice(blueHDaddress);
			BluetoothSocket socket;
			/*
			// 得到所有已经配对的蓝牙适配器对象
			Set<BluetoothDevice> devices = frag.blAdapter.getBondedDevices();
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
			*/
			if(device == null)
			{
				Message msg = new Message();
				msg.what = 2;
				frag.handler.sendMessage(msg);
				return;
			}
			
			try
			{
				socket = device.createRfcommSocketToServiceRecord(frag.uuid);
				socket.connect();
				frag.socketInUse = socket;
				frag.deviceInUse = device;
				((MainActivity)frag.getActivity()).setBluetoothSocketInUse(socket);
				Message msg = new Message();
				msg.what = 0;
				//msg.obj = device;
				frag.handler.sendMessage(msg);
			} 
			catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
				Message msg = new Message();
				msg.what = 1;
				frag.handler.sendMessage(msg);
			}
			finally
			{
				frag.myDialog.dismiss();
			}
		}
	}
	
}
