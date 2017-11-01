package com.example.test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends ActionBarActivity {

	UUID uuid= UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
	private Thread dataThread;
    private Handler handler;
    private BluetoothSocket socket;  
    private InputStream inStream;  
    int ArduinoMessageLength = 11;  //Arduino串口通信消息长度值(包含/r/n)
    int ArduinoMessageEndSign = 10; //Arduino串口通信结尾字符的ASCII码值
    private SQLiteDatabase db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        
        db = openOrCreateDatabase("/data/data/com.example.test/databases/test.db", Context.MODE_PRIVATE, null);  
        //db.execSQL("DROP TABLE IF EXISTS person");  
        //创建person表  
        db.execSQL("CREATE TABLE IF NOT EXISTS person (_id INTEGER PRIMARY KEY AUTOINCREMENT, name VARCHAR, age SMALLINT)");  
        
        //Log.d("SQLiteTimeTest","Start Insert");
        //db.execSQL("INSERT INTO person VALUES (NULL, 123, 123)");
        //Log.d("SQLiteTimeTest","End Insert");
        
        //listen for button clicks
        Button button = (Button)this.findViewById(R.id.submit);
        button.setOnClickListener(Calc);
        
        //listen for button clicks
        Button button_exp = (Button)this.findViewById(R.id.export);
        button_exp.setOnClickListener(export);
        
        
        //TextView
        final TextView showValue = (TextView)this.findViewById(R.id.height);
        
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                case 0:
                	showValue.setText("msg:"+msg.obj);
                }
            }
        };
        
        dataThread = new Thread(new Runnable()
    	{
        	public void run()
            {  
                byte[] buff = new byte[1];
                StringBuffer sb = new StringBuffer();
                //读数据需不断监听
                while(true)
                {  
                    try 
                    {  
                    	inStream = socket.getInputStream();
                    	while(inStream.available()>0)
                    	{
                    		buff[0] = (byte)inStream.read();//强制转换截取Int中的低字节，Arduino发送端发送的数据流中不会出现大于255的值。
                    		
                    		String str = new String(buff);
                    		sb.append(str);
                    		if(buff[0]== ArduinoMessageEndSign)
                    		{
                    			break;
                    		}
                    	}
                    	
                    	//根据String Buffer中的结束符打印结束符之前的内容，保留之后的内容待下一个结束符到来后打印
                    	//if(sb.length()==this.ArduinoMessageLength) 
                    	{
                    		String newSTR = sb.toString();
                    		if(newSTR.contains("\n"))
                    		{
                    			newSTR = newSTR.split("\n")[0];
                    			
                    			//打印数据内容
                    			Log.d("ArduinoMessage","Arduino:"+ newSTR);
                    			//给UI线程发message
                    			Message message = new Message();
                                message.obj = newSTR;
                                message.what = 0;
                                handler.sendMessage(message);
                                Log.d("SQLiteTimeTest","Start Insert");
                                db.execSQL("INSERT INTO person VALUES (NULL," + newSTR + ", 123)");
                                Log.d("SQLiteTimeTest","End Insert");
                                sb.delete(0, sb.indexOf("\n")+1);
                    			
                    		}

                    	}
                    	Thread.currentThread().sleep(50);
                    } 
                    catch (IOException e) 
                    {  
                        Log.e("app", "disconnected", e);
                        
                        break;
                    } 
                    catch (InterruptedException e) 
                    {
        				// TODO Auto-generated catch block
        				e.printStackTrace();
        				break;
        			}
                    
                }
            }  
    	});
    }

    
    private OnClickListener Calc = new OnClickListener()
    {
    	public void onClick(View v)
    	{
    		TextView height = (TextView) findViewById(R.id.height); 
    		height.setText("Click!");
    		
    		//得到BluetoothAdapter对象
    		BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
    		
    		//判断BluetoothAdapter对象是否为空，如果为空，则表明本机没有蓝牙设备
    		if(adapter != null)
    		{
    			System.out.println("本机拥有蓝牙设备");
    			//调用isEnabled()方法判断当前蓝牙设备是否可用
    			if(!adapter.isEnabled())
    			{     
    				//如果蓝牙设备不可用的话,创建一个intent对象,该对象用于启动一个Activity,提示用户启动蓝牙适配器
    				Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
    				startActivity(intent);
    			}
    			
    			
    			//得到所有已经配对的蓝牙适配器对象
    			Set<BluetoothDevice> devices = adapter.getBondedDevices();
    			if(devices.size()>0)
    			{
    				//BluetoothSocket socket;
    				//用迭代
    				for(Iterator iterator = devices.iterator();iterator.hasNext();)
    				{
    					//得到BluetoothDevice对象,也就是说得到配对的蓝牙适配器
    					BluetoothDevice device = (BluetoothDevice)iterator.next();
    					//得到远程蓝牙设备的地址
    					Log.d("mytag","BlueTooth Device --"+device.getName()+"--"+device.getAddress());
    					
    					try 
    					{
    						socket = device.createRfcommSocketToServiceRecord(uuid);
    						socket.connect();
    						//InputStream in = socket.getInputStream();
    						
    						//ReceiverThread receiver = new ReceiverThread(socket);
    						//receiver.start();
    						
    						dataThread.start();
						} 
    					catch (IOException e) 
    					{
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
    					
    				}     
    			}
    		}
    	}
    };
    
    
    private OnClickListener export = new OnClickListener()
    {
    	public void onClick(View v)
    	{

    			Cursor queryResult = db.rawQuery("select * from person", null);
    			
    			String fileName="testForDB.csv";
    			
    			ExportToCSV(queryResult, fileName);
    			
    	}
    };
    
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
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
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    private void ExportToCSV(Cursor c, String fileName) 
    {

		int rowCount = 0;
		int colCount = 0;
		FileWriter fw;
		BufferedWriter bfw;
		File sdCardDir = Environment.getExternalStorageDirectory();
		File saveFile = new File(sdCardDir, fileName);
		try 
		{

			rowCount = c.getCount();
			colCount = c.getColumnCount();
			fw = new FileWriter(saveFile);
			bfw = new BufferedWriter(fw);
			if (rowCount > 0) 
			{
				c.moveToFirst();
				// 写入表头
				for (int i = 0; i < colCount; i++) 
				{
					if (i != colCount - 1)
					   bfw.write(c.getColumnName(i) + ',');
					else
					   bfw.write(c.getColumnName(i));
				}
				// 写好表头后换行
				bfw.newLine();
				// 写入数据
				for (int i = 0; i < rowCount; i++) 
				{
					c.moveToPosition(i);
					// Toast.makeText(mContext, "正在导出第"+(i+1)+"条",
					// Toast.LENGTH_SHORT).show();
					Log.v("导出数据", "正在导出第" + (i + 1) + "条");
					for (int j = 0; j < colCount; j++) 
					{
						if (j != colCount - 1)
						    bfw.write(c.getString(j) + ',');
						else
						   bfw.write(c.getString(j));
					}
					// 写好每条记录后换行
					bfw.newLine();
				}
			}
			// 将缓存数据写入文件
			bfw.flush();
			// 释放缓存
			bfw.close();
			// Toast.makeText(mContext, "导出完毕！", Toast.LENGTH_SHORT).show();
			Log.v("导出数据", "导出完毕！");
		} 
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		finally 
		{
			c.close();
		}
	}
}



