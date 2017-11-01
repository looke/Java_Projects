package com.example.slopemeasure;

import java.util.concurrent.LinkedBlockingQueue;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class DatashowThread extends Thread
{
	private Handler handler;

	private boolean isRunning = true;

	//线程安全的阻塞队列，由mainActivity在创建数据采集线程时赋值
	private LinkedBlockingQueue<MeasureResult> dataQueue;
	private DisplayDataOnScreen data;
	
	public DatashowThread(Handler hd, LinkedBlockingQueue<MeasureResult> dataQueue, DisplayDataOnScreen data)
	{
		this.handler = hd;
		this.dataQueue = dataQueue;
		this.data=data;
	}
	
	public void run()
	{
		isRunning = true;
		// 读数据需不断监听
		while (isRunning)
		{
			if(!dataQueue.isEmpty())
			{
				try
				{
					MeasureResult result = dataQueue.take();
					
					double degree_new = Double.parseDouble(result.degree);
					double rate_new = Double.parseDouble(result.rate);
					double dt_new = Double.parseDouble(result.dt);
					double bias_new = Double.parseDouble(result.bias);
					double K0_new = Double.parseDouble(result.K0);
					double pitch_new = Double.parseDouble(result.pitch);
					
					data.degree_old = degree_new;
					data.degree_max_old = (degree_new>data.degree_max_old)?degree_new:data.degree_max_old;
					data.degree_min_old = (degree_new<data.degree_min_old)?degree_new:data.degree_min_old;
					
					data.rate_old = rate_new;
					data.rate_max_old = (rate_new>data.rate_max_old)?rate_new:data.rate_max_old;
					data.rate_min_old = (rate_new<data.rate_min_old)?rate_new:data.rate_min_old;
					
					data.dt_old = dt_new;
					data.dt_max_old = (dt_new>data.dt_max_old)?dt_new:data.dt_max_old;
					data.dt_min_old = (dt_new<data.dt_min_old)?dt_new:data.dt_min_old;
					
					data.bias_old = bias_new;
					data.bias_max_old = (bias_new>data.bias_max_old)?bias_new:data.bias_max_old;
					data.bias_min_old = (bias_new<data.bias_min_old)?bias_new:data.bias_min_old;
					
					data.K0_old = K0_new;
					data.K0_max_old = (K0_new>data.K0_max_old)?K0_new:data.K0_max_old;
					data.K0_min_old = (K0_new<data.K0_min_old)?K0_new:data.K0_min_old;
					
					data.pitch_old = pitch_new;
					data.pitch_max_old = (pitch_new>data.pitch_max_old)?pitch_new:data.pitch_max_old;
					data.pitch_min_old = (pitch_new<data.pitch_min_old)?pitch_new:data.pitch_min_old;
					
					Message message = new Message();
					message.what = 6;
					handler.sendMessage(message);

				} 
				catch (InterruptedException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
					Log.w("DatashowThreadError", "Take message from queue failed!");
				}
			}
		}
	}
	
	public void setStop()
	{
		this.isRunning = false;
	}
	
}
