package com.example.slopemeasure;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.LinkedBlockingQueue;

import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class CollectionThread extends Thread
{
	int ArduinoMessageLength = 11;  //Arduino串口通信消息长度值(包含/r/n)
    int ArduinoMessageEndSign = 10; //Arduino串口通信结尾字符的ASCII码值
    
	private BluetoothSocket socketInUse;
	private Handler handler;

	private InputStream inStream;
	
	private boolean isRunning = true;
	
	
	//线程安全的阻塞队列，由mainActivity在创建数据采集线程时赋值
	private LinkedBlockingQueue<MeasureResult> dataQueue;
	
	public CollectionThread(Handler hd, LinkedBlockingQueue<MeasureResult> dataQueue)
	{
		
		this.handler = hd;
		this.dataQueue = dataQueue;
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

						//拆分newSTR，生成measureResult对象，添加队列
						dataQueue.put(parseContent(newSTR));
						sb.delete(0, sb.indexOf("\n") + 1);
					}

				}
				Thread.currentThread().sleep(10);
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
	
	
	private MeasureResult parseContent(String newSTR)
	{
		MeasureResult result = null;
		if(!newSTR.isEmpty() && newSTR.contains(";"))
		{
			String[] elements = newSTR.split(";");
			if(elements.length == 6)
			{
				result = new MeasureResult();
				
				String[] degreePair = elements[0].split(":");
				String[] ratePair = elements[1].split(":");
				String[] dtPair = elements[2].split(":");
				String[] pitchPair = elements[3].split(":");
				String[] biasPair = elements[4].split(":");
				String[] K0Pair = elements[5].split(":");
				
				result.degree = degreePair[1];
				result.rate = ratePair[1];
				result.dt = dtPair[1];
				result.pitch = pitchPair[1];
				result.bias = biasPair[1];
				result.K0 = K0Pair[1];
				
			}
		}
		return result;
	}
}
