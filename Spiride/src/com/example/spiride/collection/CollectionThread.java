package com.example.spiride.collection;

import java.io.IOException;
import java.io.InputStream;

import com.example.spiride.util.SpirideDBHelper;

import android.bluetooth.BluetoothSocket;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class CollectionThread extends Thread
{
	int ArduinoMessageLength = 11;  //Arduino串口通信消息长度值(包含/r/n)
    int ArduinoMessageEndSign = 10; //Arduino串口通信结尾字符的ASCII码值
    
	private BluetoothSocket socketInUse;
	private Handler handler;
	private SpirideDBHelper sqDBhelper;
	
	private SQLiteDatabase 	sqDB;
	private InputStream inStream;
	
	private boolean isRunning = true;
	
	private String routeName;
	public CollectionThread(Handler hd, SpirideDBHelper hp, String name)
	{
		
		handler = hd;
		sqDBhelper = hp;
		sqDB = sqDBhelper.getWritableDatabase();
		routeName = name;
	}
	
	public void setBlueToothSocket(BluetoothSocket sk)
	{
		socketInUse = sk; 
	}
	
	public void run()
	{
		isRunning = true;
		byte[] buff = new byte[1];
		StringBuffer sb = new StringBuffer();
		// 读数据需不断监听
		while (isRunning)
		{
			try
			{
				inStream = socketInUse.getInputStream();
				while (inStream.available() > 0)
				{
					buff[0] = (byte) inStream.read();// 强制转换截取Int中的低字节，Arduino发送端发送的数据流中不会出现大于255的值。

					String str = new String(buff);
					sb.append(str);
					if (buff[0] == ArduinoMessageEndSign)
					{
						break;
					}
				}
				// 根据String Buffer中的结束符打印结束符之前的内容，保留之后的内容待下一个结束符到来后打印
				// if(sb.length()==this.ArduinoMessageLength)
				{
					String newSTR = sb.toString();
					if (newSTR.contains("\r\n"))
					{
						newSTR = newSTR.split("\r\n")[0];
						// 打印数据内容
						Log.d("ArduinoMessage", "Arduino:" + newSTR);
						// 给UI线程发message
						Message message = new Message();
						message.obj = newSTR;
						message.what = 0;
						handler.sendMessage(message);
						Log.d("SQLiteTimeTest", "Start Insert");
						String sql = "INSERT INTO slope VALUES (NULL, '"+ routeName +"','" + newSTR + "')";
						//String sql = "INSERT INTO slope VALUES (NULL, 123, 13)";
						sqDB.execSQL(sql);
						Log.d("SQLiteTimeTest", "End Insert");
						sb.delete(0, sb.indexOf("\n") + 1);
					}

				}
				Thread.currentThread().sleep(50);
			} 
			catch (IOException e)
			{
				//Log.e("app", "disconnected", e);
				e.printStackTrace();
				Message message = new Message();
				message.what = 1;
				handler.sendMessage(message);
				try
				{
					inStream.close();
				} 
				catch (IOException e1)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				isRunning = false;
				break;
			} 
			catch (InterruptedException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
				Message message = new Message();
				message.what = 1;
				handler.sendMessage(message);
				try
				{
					inStream.close();
				} 
				catch (IOException e2)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				isRunning = false;
				break;
			}
			catch(Exception sqle)
			{
				sqle.printStackTrace();
				isRunning = false;
			}
			finally
			{
				
			}
		}
	}
	
	public void setStop()
	{
		this.isRunning = false;
	}
}
