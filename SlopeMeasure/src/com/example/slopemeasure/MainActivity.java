package com.example.slopemeasure;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.LinkedBlockingQueue;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity implements OnClickListener
{
	// 得到BluetoothAdapter对象
	public BluetoothAdapter 	blAdapter;
	public Spinner 			sp_blueList;
	public UUID				uuid	= UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
	public BluetoothSocket socketInUse;
	public BluetoothDevice deviceInUse;
	public Handler handler;
	private CollectionThread cThd;
	private DatashowThread dsThd;
	public DisplayDataOnScreen data = new DisplayDataOnScreen();
	
	LinkedBlockingQueue<MeasureResult> dataQueue = new LinkedBlockingQueue<MeasureResult>();
	
	Button btn_connect;
	Button btn_disconnect;
	
	Button btn_test_start;
	Button btn_test_stop;
	
	EditText text_target_slope;
	double target_slope;
	
	TextView degree;
	TextView degree_min;
	TextView degree_max;
	
	TextView rate;
	TextView rate_min;
	TextView rate_max;
	
	TextView dt;
	TextView dt_min;
	TextView dt_max;
	
	TextView bias;
	TextView bias_min;
	TextView bias_max;
	
	TextView K0;
	TextView K0_min;
	TextView K0_max;
	
	TextView pitch;
	TextView pitch_min;
	TextView pitch_max;
	
	
	public ProgressDialog myDialog;
	
	private void initView()
	{
		degree = (TextView) this.findViewById(R.id.text_datashow_degree_value);
		degree_min = (TextView)this.findViewById(R.id.text_datashow_degree_minvalue);
		degree_max = (TextView)this.findViewById(R.id.text_datashow_degree_maxvalue);
		
		
		rate = (TextView) this.findViewById(R.id.text_datashow_rate_value);
		rate_min = (TextView)this.findViewById(R.id.text_datashow_rate_minvalue);
		rate_max = (TextView)this.findViewById(R.id.text_datashow_rate_maxvalue);
		
		dt = (TextView) this.findViewById(R.id.text_datashow_dt_value);
		dt_min = (TextView)this.findViewById(R.id.text_datashow_dt_minvalue);
		dt_max = (TextView)this.findViewById(R.id.text_datashow_dt_maxvalue);
		
		bias = (TextView) this.findViewById(R.id.text_datashow_bias_value);
		bias_min = (TextView)this.findViewById(R.id.text_datashow_bias_minvalue);
		bias_max = (TextView)this.findViewById(R.id.text_datashow_bias_maxvalue);
		
		K0 = (TextView) this.findViewById(R.id.text_datashow_k_value);
		K0_min = (TextView)this.findViewById(R.id.text_datashow_k_minvalue);
		K0_max = (TextView)this.findViewById(R.id.text_datashow_k_maxvalue);
		
		pitch = (TextView) this.findViewById(R.id.text_datashow_pitch_value);
		pitch_min = (TextView)this.findViewById(R.id.text_datashow_pitch_minvalue);
		pitch_max = (TextView)this.findViewById(R.id.text_datashow_pitch_maxvalue);
		
	}
	
	private void updateDisplayData()
	{
		degree.setText(String.valueOf(data.degree_old));
		degree_min.setText(String.valueOf(data.degree_min_old));
		degree_max.setText(String.valueOf(data.degree_max_old));
		
		rate.setText(String.valueOf(data.rate_old));
		rate_min.setText(String.valueOf(data.rate_min_old));
		rate_max.setText(String.valueOf(data.rate_max_old));
		
		dt.setText(String.valueOf(data.dt_old));
		dt_min.setText(String.valueOf(data.dt_min_old));
		dt_max.setText(String.valueOf(data.dt_max_old));
		
		bias.setText(String.valueOf(data.bias_old));
		bias_min.setText(String.valueOf(data.bias_min_old));
		bias_max.setText(String.valueOf(data.bias_max_old));
		
		K0.setText(String.valueOf(data.K0_old));
		K0_min.setText(String.valueOf(data.K0_min_old));
		K0_max.setText(String.valueOf(data.K0_max_old)) ;
		
		pitch.setText(String.valueOf(data.pitch_old));
		pitch_min.setText(String.valueOf(data.pitch_min_old));
		pitch_max.setText(String.valueOf(data.pitch_max_old));
		
	}
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		sp_blueList = (Spinner) this.findViewById(R.id.spinner_bluetooth_list);
        btn_connect = (Button)this.findViewById(R.id.btn_bluetooth_connect);
        btn_disconnect = (Button)this.findViewById(R.id.btn_bluetooth_disconnect);
        
        btn_test_start = (Button)this.findViewById(R.id.btn_test_start);
    	btn_test_stop = (Button)this.findViewById(R.id.btn_test_stop);
    	
    	text_target_slope = (EditText)this.findViewById(R.id.edit_text_target_slope);
    	
        initView();
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
    			ArrayAdapter<String> arr_adapter= new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, spinner_list);
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
			ArrayAdapter<String> arr_adapter= new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, spinner_list);
	        //设置样式
	        arr_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	        //加载适配器
	        sp_blueList.setAdapter(arr_adapter);
	        btn_connect.setEnabled(false);
	        btn_disconnect.setEnabled(true);
        }
        
        btn_test_start.setEnabled(false);
    	btn_test_stop.setEnabled(false);

        btn_connect.setOnClickListener(this);
        btn_disconnect.setOnClickListener(this);
        btn_test_start.setOnClickListener(this);
    	btn_test_stop.setOnClickListener(this);
        setHandler();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings)
		{
			return true;
		}
		return super.onOptionsItemSelected(item);
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
    		
    		case R.id.btn_test_start:
    			cThd = new CollectionThread(handler, dataQueue);
    			dsThd = new DatashowThread(handler, dataQueue, data);
    			cThd.setBlueToothSocket(socketInUse);
    			
    			btn_test_start.setEnabled(false);
    	    	btn_test_stop.setEnabled(true);
    	    	btn_disconnect.setEnabled(false);
    	    	text_target_slope.setEnabled(false);
    	    	cThd.start();
    	    	dsThd.start();
    	    	Toast.makeText(this, "测试已启动", 1).show();
    			break;
    		
    		case R.id.btn_test_stop:
    			cThd.setStop();
    			dsThd.setStop();
    			btn_test_start.setEnabled(true);
    	    	btn_test_stop.setEnabled(false);
    	    	btn_disconnect.setEnabled(true);
    	    	text_target_slope.setEnabled(true);
    	    	Toast.makeText(this, "测试已停止", 1).show();
    			break;
    	}
	}
	
	private void connect()
    {
    	myDialog = ProgressDialog.show(
				this, "蓝牙连接提示", "蓝牙设备处理中...", false);
    	
    	new BluetoothConnThread(this).start();
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
	
	private void setHandler()
    {
    	final Context ct = this.getApplicationContext();
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
						
						btn_disconnect.setEnabled(true);
						btn_test_start.setEnabled(true);
						
						Toast.makeText(ct, "蓝牙连接成功", 1).show();
						break;
					case 1:
						Toast.makeText(ct, "蓝牙操作失败，请重试", 1)
								.show();
						break;
					case 2:
						Toast.makeText(ct, "无蓝牙适配器，请重启应用", 1)
								.show();
						break;
					case 4:
						btn_connect.setEnabled(true);
						btn_disconnect.setEnabled(false);
						sp_blueList.setClickable(true);
						Toast.makeText(ct, "蓝牙连接已断开", 1).show();
						break;
					case 5:
						Toast.makeText(ct, "下拉菜单取值错误", 1).show();
						break;
					
					case 6: //使用新值刷新界面
						updateDisplayData();
						break;
				}
			}
		};
    }
	
	@Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) 
    {
		//打开蓝牙成功以后
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
        	ArrayAdapter<String> arr_adapter= new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, spinner_list);
        	//设置样式
        	arr_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        	//加载适配器
        	sp_blueList.setAdapter(arr_adapter);
        	arr_adapter.notifyDataSetChanged();
        }
    }
}
