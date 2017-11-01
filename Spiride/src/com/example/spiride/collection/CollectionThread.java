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
	int ArduinoMessageLength = 11;  //Arduino����ͨ����Ϣ����ֵ(����/r/n)
    int ArduinoMessageEndSign = 10; //Arduino����ͨ�Ž�β�ַ���ASCII��ֵ
    
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
		// �������費�ϼ���
		while (isRunning)
		{
			try
			{
				inStream = socketInUse.getInputStream();
				while (inStream.available() > 0)
				{
					buff[0] = (byte) inStream.read();// ǿ��ת����ȡInt�еĵ��ֽڣ�Arduino���Ͷ˷��͵��������в�����ִ���255��ֵ��

					String str = new String(buff);
					sb.append(str);
					if (buff[0] == ArduinoMessageEndSign)
					{
						break;
					}
				}
				// ����String Buffer�еĽ�������ӡ������֮ǰ�����ݣ�����֮������ݴ���һ���������������ӡ
				// if(sb.length()==this.ArduinoMessageLength)
				{
					String newSTR = sb.toString();
					if (newSTR.contains("\r\n"))
					{
						newSTR = newSTR.split("\r\n")[0];
						// ��ӡ��������
						Log.d("ArduinoMessage", "Arduino:" + newSTR);
						// ��UI�̷߳�message
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
