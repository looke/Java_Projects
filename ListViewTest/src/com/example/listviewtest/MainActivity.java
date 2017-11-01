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

	// ����һ��String����������ʾListView������
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
	int 						ArduinoMessageLength = 11;  //Arduino����ͨ����Ϣ����ֵ(����/r/n)
    int 						ArduinoMessageEndSign = 10; //Arduino����ͨ�Ž�β�ַ���ASCII��ֵ
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
		
		/* �������д������ */
		ArrayList<HashMap<String, Object>> listItem = new ArrayList<HashMap<String, Object>>();

		// �õ�BluetoothAdapter����
		BluetoothAdapter blAdapter = BluetoothAdapter.getDefaultAdapter();

		// �ж�BluetoothAdapter�����Ƿ�Ϊ�գ����Ϊ�գ����������û�������豸
		if (blAdapter != null)
		{
			// ����isEnabled()�����жϵ�ǰ�����豸�Ƿ����
			if (!blAdapter.isEnabled())
			{
				// ��������豸�����õĻ�,����һ��intent����,�ö�����������һ��Activity,��ʾ�û���������������
				Intent intent = new Intent(
						BluetoothAdapter.ACTION_REQUEST_ENABLE);
				startActivity(intent);
			}

			// �õ������Ѿ���Ե���������������
			Set<BluetoothDevice> devices = blAdapter.getBondedDevices();
			// strs = new String[devices.size()];
			// int i=0;
			for (Iterator iterator = devices.iterator(); iterator.hasNext();)
			{
				// �õ�BluetoothDevice����,Ҳ����˵�õ���Ե�����������
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
		// ������Ҫע����ǵ���������arg2�����Ǵ������ڼ���ѡ��
		public void onItemClick(AdapterView arg0, View arg1, int arg2, long arg3)
		{
			// ͨ�������¼�����õ���ѡ�������
			final HashMap<String, Object> map = (HashMap<String, Object>) lv
					.getItemAtPosition(arg2);
			final BluetoothDevice device = (BluetoothDevice) map.get("device");
			final ProgressDialog myDialog = ProgressDialog.show(
					arg0.getContext(), "����������ʾ", "�����豸������...", false);

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
						// ж����������myDialog����
						myDialog.dismiss();
					}
				}
			}.start(); /* ��ʼ���������߳� */

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
        //�����¶ȱ�  
        db.execSQL("CREATE TABLE IF NOT EXISTS slope (_id INTEGER PRIMARY KEY AUTOINCREMENT, name VARCHAR, degree VARCHAR)");  
        //������·��Ϣ�� 
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
				// �������費�ϼ���
				while (true)
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
							if (newSTR.contains("\n"))
							{
								newSTR = newSTR.split("\n")[0];
								// ��ӡ��������
								Log.d("ArduinoMessage", "Arduino:" + newSTR);
								// ��UI�̷߳�message
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
    		//�ж��Ƿ��Ѿ����ڽ�����socket
    		if(socketInUse != null && socketInUse.isConnected())
    		{
    			dataCollectionThread = new CollectionThread(handler, db);
    			dataCollectionThread.setBlueToothSocket(socketInUse);
    			dataCollectionThread.start();
    			bt_startCollect.setEnabled(false);
    			bt_stopCollect.setEnabled(true);
    			Toast.makeText(getApplicationContext(), "�������¶Ȳɼ�", 1)
				.show();
    		}
    		else //�����ھ���ʾ�û��ȶ������豸����
    		{
    			Toast.makeText(getApplicationContext(), "��������δ���������Ƚ�����������", 1)
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
    		Toast.makeText(getApplicationContext(), "�ѹر��¶Ȳɼ�", 1)
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
					Toast.makeText(getApplicationContext(), "�������ӳɹ�", 1).show();
					break;
				case 1:
					Toast.makeText(getApplicationContext(), "���������ѶϿ�", 1)
							.show();
					break;
				case 3:
					Toast.makeText(getApplicationContext(), "��������ʧ�ܣ�������", 1)
							.show();
					break;
				case 4:
					int value = Integer.parseInt((String)msg.obj);
					double degree = DegreeCalc.calcDegree(value);
					double percent = DegreeCalc.calcPercentage(value);
					setDisplayColor(degree);
					//����ȡ��������ͨ����ɫȷ���������������ֵ����ȡ��
					if(degree < 0)
					{
						degree = 0-degree;
						percent = 0-percent;
					}
					degreeNum.setText(String.format("%.1f",degree));
					percentNum.setText(String.format("%.1f",percent) + "%");
					
					break;
				case 5:
					//���������쳣�жϴ�������map�е�����״̬�����½���
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
    		//�������ʾ���£���ʾ��ɫ
    		degreeNum.setTextColor(Color.rgb(255,97,0));
    		percentNum.setTextColor(Color.rgb(255,97,0));
    	}
    	else if(degree < 0)
    	{
    		//С�����ʾ���£���ʾ����ɫ
    		degreeNum.setTextColor(Color.rgb(46,139,87));
    		percentNum.setTextColor(Color.rgb(46,139,87));
    	}
    }
}
