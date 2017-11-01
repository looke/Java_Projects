package com.example.spiride.route;

import com.example.spiride.R;
import com.example.spiride.R.id;
import com.example.spiride.R.layout;
import com.example.spiride.util.SpirideDBHelper;

import android.app.Activity;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class RouteAddActivity extends Activity
{
	SQLiteDatabase 				db;
	SpirideDBHelper 			dbHelper;
	
	EditText name;
	EditText start;
	EditText end;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_addroute);
		name = (EditText)findViewById(R.id.route_add_name_edit);
		start = (EditText)findViewById(R.id.route_add_startplace_edit);
		end = (EditText)findViewById(R.id.route_add_endplace_edit);
		
		dbHelper = new SpirideDBHelper(this);
		db = dbHelper.getWritableDatabase();
		
		Button bte_submit = (Button)findViewById(R.id.btn_route_add_submit);
		Button bte_cencel = (Button)findViewById(R.id.btn_route_add_cancel);
		
		bte_submit.setOnClickListener(new OnClickListener(){
			
			public void onClick(View v) 
			{
				
				if(TextUtils.isEmpty(name.getText()))
				{
					Toast.makeText(getApplicationContext(),"请输入线路名称", Toast.LENGTH_SHORT).show();
					return;
				}
				if(TextUtils.isEmpty(start.getText()))
				{
					Toast.makeText(getApplicationContext(),"请输入线路起点", Toast.LENGTH_SHORT).show();
					return;
				}
				if(TextUtils.isEmpty(start.getText()))
				{
					Toast.makeText(getApplicationContext(),"请输入线路终点", Toast.LENGTH_SHORT).show();
					return;
				}
				
				String routeName = name.getText().toString();
				String routeStart = start.getText().toString();
				String routeEnd = end.getText().toString();
				
				String sql = "INSERT INTO route VALUES (NULL, '"+ routeName + "','" + routeStart + "','"+routeEnd +"','0')";
				try
				{
					db.execSQL(sql);
				}
				catch (SQLException e)
				{
					Toast.makeText(getApplicationContext(), "数据插入失败，请重试", 1).show();
					
					finish();
				}
				Toast.makeText(getApplicationContext(), "数据插入成功", 1)
				.show();
				finish();
			}
		});
		
		bte_cencel.setOnClickListener(new OnClickListener(){
			
			public void onClick(View v) 
			{
				finish();
			}
		});
		
	}
	
	
}
