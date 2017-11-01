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
	int ArduinoMessageLength = 11;  //Arduino����ͨ����Ϣ����ֵ(����/r/n)
    int ArduinoMessageEndSign = 10; //Arduino����ͨ�Ž�β�ַ���ASCII��ֵ
    
	private BluetoothSocket socketInUse;
	private Handler handler;

	private InputStream inStream;
	
	private boolean isRunning = true;
	
	
	//�̰߳�ȫ���������У���mainActivity�ڴ������ݲɼ��߳�ʱ��ֵ
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

						//���newSTR������measureResult������Ӷ���
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
