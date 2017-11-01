package com.example.listviewtest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity
{

	// 定义一个String数组用来显示ListView的内容
	/** Called when the activity is first created. */
	// private static String[] strs;
	UUID					uuid	= UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
	// private static final ArrayList<String> strArrayList = new
	// ArrayList<String>();

	private ListView			lv;
	private Handler				handler;
	//private BluetoothSocket	socketInUse;
	private CollectionThread	dataCollectionThread;
	//private InputStream		inStream;
	int 						ArduinoMessageLength = 11;  //Arduino串口通信消息长度值(包含/r/n)
    int 						ArduinoMessageEndSign = 10; //Arduino串口通信结尾字符的ASCII码值
    SQLiteDatabase 				db;
    private TextView 			degreeNum;
    private TextView 			percentNum;
    
    Button bt_startCollect; 
	Button bt_stopCollect;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		this.lv = (ListView) findViewById(R.id.blueToothList);
		bt_startCollect = (Button) findViewById(R.id.startupcollect);
		bt_stopCollect = (Button) findViewById(R.id.stopcollect);
		degreeNum = (TextView) findViewById(R.id.slopeDegreeNum);
		percentNum = (TextView) findViewById(R.id.slopePercentNum);
		
		/* 在数组中存放数据 */
		ArrayList<HashMap<String, Object>> listItem = new ArrayList<HashMap<String, Object>>();

		// 得到BluetoothAdapter对象
		BluetoothAdapter blAdapter = BluetoothAdapter.getDefaultAdapter();

		// 判断BluetoothAdapter对象是否为空，如果为空，则表明本机没有蓝牙设备
		if (blAdapter != null)
		{
			// 调用isEnabled()方法判断当前蓝牙设备是否可用
			if (!blAdapter.isEnabled())
			{
				// 如果蓝牙设备不可用的话,创建一个intent对象,该对象用于启动一个Activity,提示用户启动蓝牙适配器
				Intent intent = new Intent(
						BluetoothAdapter.ACTION_REQUEST_ENABLE);
				startActivity(intent);
			}

			// 得到所有已经配对的蓝牙适配器对象
			Set<BluetoothDevice> devices = blAdapter.getBondedDevices();
			// strs = new String[devices.size()];
			// int i=0;
			for (Iterator iterator = devices.iterator(); iterator.hasNext();)
			{
				// 得到BluetoothDevice对象,也就是说得到配对的蓝牙适配器
				BluetoothDevice device = (BluetoothDevice) iterator.next();

				HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("itemTitle", device.getName());
				map.put("itemInfo", device.getAddress());
				map.put("socketInfo", "Disconnect");
				map.put("device", device);
				listItem.add(map);
				// strArrayList.add(device.getName()+"--"+device.getAddress());
				// strs[i] = device.getName()+"--"+device.getAddress();
				// i++;

			}
			// strs = (String[])strArrayList.toArray();

		}

		
		// this.lv.setAdapter(new ArrayAdapter<String>(this,
		// android.R.layout.simple_list_item_1, strs));

		SimpleAdapter smAdapter = new SimpleAdapter(this, listItem,
				R.layout.item, new String[] { "itemTitle", "itemInfo",
						"socketInfo" }, new int[] { R.id.itemTitle,
						R.id.itemInfo, R.id.socketInfo });
		lv.setAdapter(smAdapter);
		lv.setOnItemClickListener(new ItemClickEvent());

		bt_startCollect.setOnClickListener(startCollect);
		bt_stopCollect.setOnClickListener(stopCollect);
		
		setHandler();
		setupDB();
		bt_stopCollect.setEnabled(false);
		//setDataThread();
	}

	private final class ItemClickEvent implements OnItemClickListener
	{
		@Override
		// 这里需要注意的是第三个参数arg2，这是代表单击第几个选项
		public void onItemClick(AdapterView arg0, View arg1, int arg2, long arg3)
		{
			// 通过单击事件，获得单击选项的内容
			final HashMap<String, Object> map = (HashMap<String, Object>) lv
					.getItemAtPosition(arg2);
			final BluetoothDevice device = (BluetoothDevice) map.get("device");
			final ProgressDialog myDialog = ProgressDialog.show(
					arg0.getContext(), "蓝牙连接提示", "蓝牙设备处理中...", false);

			new Thread()
			{
				public void run()
				{
					try
					{
						if (map.get("Socket") == null)
						{
							BluetoothSocket socket = device
									.createRfcommSocketToServiceRecord(uuid);
							socket.connect();

							// InputStream in = socket.getInputStream();

							// ReceiverThread receiver = new
							// ReceiverThread(socket);
							// receiver.start();
							//socketInUse = socket;
							map.remove("socketInfo");
							map.put("socketInfo", "Connected");
							map.put("Socket", socket);

							Message message = new Message();
							message.what = 0;
							handler.sendMessage(message);

						} 
						else if (!((BluetoothSocket) map.get("Socket"))
								.isConnected())
						{
							BluetoothSocket socket = ((BluetoothSocket) map.get("Socket"));
							socket.connect();
							//socketInUse = socket;
							map.remove("socketInfo");
							map.put("socketInfo", "Connected");
							map.put("Socket", socket);

							Message message = new Message();
							message.what = 0;
							handler.sendMessage(message);
						}
						else 
						{

							((BluetoothSocket) map.get("Socket")).close();

							// InputStream in = socket.getInputStream();

							// ReceiverThread receiver = new
							// ReceiverThread(socket);
							// receiver.start();
							//socketInUse = null;
							map.remove("Socket");
							map.remove("socketInfo");
							map.put("socketInfo", "Disconnect");
							Message message = new Message();
							message.what = 1;
							handler.sendMessage(message);
						}
					} catch (IOException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
						Message message = new Message();
						message.what = 3;
						handler.sendMessage(message);
					} finally
					{
						// 卸载所创建的myDialog对象。
						myDialog.dismiss();
					}
				}
			}.start(); /* 开始运行运行线程 */

			// Toast.makeText(getApplicationContext(),
			// (String)text.get("itemTitle"), 1).show();
		}
	};

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

	private void setupDB()
	{
		db = openOrCreateDatabase("/data/data/com.example.listviewtest/databases/test.db", Context.MODE_PRIVATE, null);  
        //db.execSQL("DROP TABLE IF EXISTS person");  
        //创建坡度表  
        db.execSQL("CREATE TABLE IF NOT EXISTS slope (_id INTEGER PRIMARY KEY AUTOINCREMENT, name VARCHAR, degree VARCHAR)");  
        //创建线路信息表 
        //db.execSQL("CREATE TABLE IF NOT EXISTS route (_id INTEGER PRIMARY KEY AUTOINCREMENT, name VARCHAR, sample_number VARCHAR, start_point VARCHAR, end_point VARCHAR)");
        
	}
	
	//private void setDataThread()
	//{
	//	dataCollectionThread = new CollectionThread(handler, db);
				
				
		/*		new Thread(new Runnable()
		{
			private BluetoothSocket socketInUse;
			private
			public void setBlueToothSocket(BluetoothSocket sk)
			{
				socketInUse = sk;
			}
			
			public void run()
			{
				
				byte[] buff = new byte[1];
				StringBuffer sb = new StringBuffer();
				// 读数据需不断监听
				while (true)
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
							if (newSTR.contains("\n"))
							{
								newSTR = newSTR.split("\n")[0];
								// 打印数据内容
								Log.d("ArduinoMessage", "Arduino:" + newSTR);
								// 给UI线程发message
								Message message = new Message();
								message.obj = newSTR;
								message.what = 4;
								handler.sendMessage(message);
								Log.d("SQLiteTimeTest", "Start Insert");
								db.execSQL("INSERT INTO slope VALUES (NULL, G210,"
										+ newSTR + ")");
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
						message.what = 5;
						handler.sendMessage(message);
						break;
					} 
					catch (InterruptedException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
						Message message = new Message();
						message.what = 5;
						handler.sendMessage(message);
						break;
					}
					finally
					{
						try
						{
							inStream.close();
						} 
						catch (IOException e)
						{
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}
		});*/
	//}
	
	private OnClickListener startCollect = new OnClickListener()
    {
    	public void onClick(View v)
    	{
    		
    		BluetoothSocket socketInUse = getCurrentSocket();
    		//判断是否已经存在建链的socket
    		if(socketInUse != null && socketInUse.isConnected())
    		{
    			dataCollectionThread = new CollectionThread(handler, db);
    			dataCollectionThread.setBlueToothSocket(socketInUse);
    			dataCollectionThread.start();
    			bt_startCollect.setEnabled(false);
    			bt_stopCollect.setEnabled(true);
    			Toast.makeText(getApplicationContext(), "已启动坡度采集", 1)
				.show();
    		}
    		else //不存在就提示用户先对蓝牙设备建链
    		{
    			Toast.makeText(getApplicationContext(), "蓝牙连接未建立，请先建立蓝牙连接", 1)
				.show();
    		}
    	}
    };
    
    private OnClickListener stopCollect = new OnClickListener()
    {
    	public void onClick(View v)
    	{
    		dataCollectionThread.setStop();
    		bt_startCollect.setEnabled(true);
    		bt_stopCollect.setEnabled(false);
    		degreeNum.setText("--");
			percentNum.setText("--");
			degreeNum.setTextColor(Color.rgb(88, 88, 88));
			percentNum.setTextColor(Color.rgb(88, 88, 88));
    		Toast.makeText(getApplicationContext(), "已关闭坡度采集", 1)
			.show();
    	}
    };
    
    private void setHandler()
    {
    	handler = new Handler()
		{
			@Override
			public void handleMessage(Message msg)
			{
				SimpleAdapter ad = (SimpleAdapter) lv.getAdapter();
				switch (msg.what)
				{
				case 0:
					Toast.makeText(getApplicationContext(), "蓝牙连接成功", 1).show();
					break;
				case 1:
					Toast.makeText(getApplicationContext(), "蓝牙连接已断开", 1)
							.show();
					break;
				case 3:
					Toast.makeText(getApplicationContext(), "蓝牙操作失败，请重试", 1)
							.show();
					break;
				case 4:
					int value = Integer.parseInt((String)msg.obj);
					double degree = DegreeCalc.calcDegree(value);
					double percent = DegreeCalc.calcPercentage(value);
					setDisplayColor(degree);
					//负数取反，界面通过颜色确定正负，数据入库值不会取反
					if(degree < 0)
					{
						degree = 0-degree;
						percent = 0-percent;
					}
					degreeNum.setText(String.format("%.1f",degree));
					percentNum.setText(String.format("%.1f",percent) + "%");
					
					break;
				case 5:
					//蓝牙连接异常中断处理，更新map中的蓝牙状态，更新界面
					updateCurrentBluetoothToDisconn();
					break;
				}
				ad.notifyDataSetChanged();
			}
		};
    }
    
    private void updateCurrentBluetoothToDisconn()
    {
    	HashMap<String, Object> map;
    	BluetoothSocket tempSocket;
    	for(int i=0; i<lv.getCount(); i++)
    	{
    		map = (HashMap<String, Object>) lv
					.getItemAtPosition(i);
    		tempSocket = (BluetoothSocket)map.get("Socket");
    		
    		if(tempSocket!=null)
    		{
    			
				map.remove("Socket");
				map.remove("socketInfo");
				map.put("socketInfo", "Disconnect");
				break;
    		}
    	}
    }
    
    private BluetoothSocket getCurrentSocket()
    {
    	HashMap<String, Object> map;
    	BluetoothSocket tempSocket;
    	for(int i=0; i<lv.getCount(); i++)
    	{
    		map = (HashMap<String, Object>) lv
					.getItemAtPosition(i);
    		tempSocket = (BluetoothSocket)map.get("Socket");
    		
    		if(tempSocket!=null)
    		{
    			return tempSocket;
    		}
    	}
    	return null;
    }
    
    private void setDisplayColor(double degree)
    {
    	if(degree > 0)
    	{
    		//大于零表示上坡，显示橙色
    		degreeNum.setTextColor(Color.rgb(255,97,0));
    		percentNum.setTextColor(Color.rgb(255,97,0));
    	}
    	else if(degree < 0)
    	{
    		//小于零表示下坡，显示绿土色
    		degreeNum.setTextColor(Color.rgb(46,139,87));
    		percentNum.setTextColor(Color.rgb(46,139,87));
    	}
    }
}
