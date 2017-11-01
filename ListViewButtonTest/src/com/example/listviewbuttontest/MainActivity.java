package com.example.listviewbuttontest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends ListActivity
{
	//private ListView					lv;
	private List<Map<String, Object>>	mData;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		//this.lv = (ListView) findViewById(R.id.blueToothList);
		mData = getData();
		MyAdapter adapter = new MyAdapter(this);
		setListAdapter(adapter);

	}

	private List<Map<String, Object>> getData()
	{
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("itemTitle", "G1");
		map.put("itemInfo", "google 1");
		list.add(map);

		map = new HashMap<String, Object>();
		map.put("itemTitle", "G2");
		map.put("itemInfo", "google 2");
		list.add(map);

		map = new HashMap<String, Object>();
		map.put("itemTitle", "G3");
		map.put("itemInfo", "google 3");
		list.add(map);

		return list;
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

	/**
	 * listview中点击按键弹出对话框
	 */
	public void showInfo(View v)
	{
		Button viewBtn = (Button)v.findViewById(R.id.view_btn);
		String btnText = viewBtn.getText().toString();
		if(btnText.equalsIgnoreCase("Conn BlueTooth"))
		{
			viewBtn.setText("Disconn");
		}
		else
		{
			viewBtn.setText("Conn BlueTooth");
		}
		
		
		/*new AlertDialog.Builder(this).setTitle("我的listview")
				.setMessage("介绍...")
				.setPositiveButton("确定", new DialogInterface.OnClickListener()
				{
					@Override
					public void onClick(DialogInterface dialog, int which)
					{
					}
				}).show();
*/
	}

	public final class ViewHolder
	{

		public TextView	title;
		public TextView	info;
		public Button	viewBtn;
	}

	
	public class MyAdapter extends BaseAdapter
	{
		private LayoutInflater	mInflater;

		public MyAdapter(Context context)
		{
			this.mInflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount()
		{
			// TODO Auto-generated method stub
			return mData.size();
		}

		@Override
		public Object getItem(int arg0)
		{
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int arg0)
		{
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{

			ViewHolder holder = null;
			if (convertView == null)
			{

				holder = new ViewHolder();

				convertView = mInflater.inflate(R.layout.item, null);

				holder.title = (TextView) convertView.findViewById(R.id.itemTitle);
				holder.info = (TextView) convertView.findViewById(R.id.itemInfo);
				holder.viewBtn = (Button) convertView
						.findViewById(R.id.view_btn);
				convertView.setTag(holder);

			} 
			else
			{

				holder = (ViewHolder) convertView.getTag();
			}

			holder.title.setText((String) mData.get(position).get("itemTitle"));
			holder.info.setText((String) mData.get(position).get("itemInfo"));

			holder.viewBtn.setOnClickListener(new View.OnClickListener()
			{

				@Override
				public void onClick(View v)
				{
					showInfo(v);
				}
			});

			return convertView;
		}
	}
}
