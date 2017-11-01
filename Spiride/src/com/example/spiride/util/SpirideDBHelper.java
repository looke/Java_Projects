package com.example.spiride.util;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SpirideDBHelper extends SQLiteOpenHelper
{
	
	public static final String DB_NAME = "data.db";
    public static final int VERSION = 1;
    
    public static final String TABLE_NAME_SLOPE = "slope";
    public static final String TABLE_NAME_ROUTE = "route";
    
    //private static final String DATABASE_CREATE = "这里是创建库的语句";
    private static final String TABLE_CREATE_SLOPE = "CREATE TABLE IF NOT EXISTS slope (_id INTEGER PRIMARY KEY AUTOINCREMENT, name VARCHAR, degree VARCHAR)";
    private static final String TABLE_CREATE_ROUTE = "CREATE TABLE IF NOT EXISTS route (_id INTEGER PRIMARY KEY AUTOINCREMENT, name VARCHAR, start_point VARCHAR, end_point VARCHAR, sample_number VARCHAR)";
    
    public static final String COLUMN_NAME_ROUTEID = "id";
    public static final String COLUMN_NAME_ROUTENAME = "name";
    public static final String COLUMN_NAME_ROUTESTART = "start";
    public static final String COLUMN_NAME_ROUTEEND = "end";
    public static final String COLUMN_NAME_ROUTESAMPLENUMBER = "number";
    
    public static final String COLUMN_NAME_SLOPEID = "id";
    public static final String COLUMN_NAME_SLOPENAME = "name";
    public static final String COLUMN_NAME_SLOPEDEGREE = "degree";

    
    
    //db.execSQL("DROP TABLE IF EXISTS person");  
    //创建坡度表  
    
    public SpirideDBHelper(Context context) 
    {
        super(context, DB_NAME, null, VERSION);
    }
 
    @Override
    public void onCreate(SQLiteDatabase db) 
    {
        db.execSQL(TABLE_CREATE_SLOPE);
        db.execSQL(TABLE_CREATE_ROUTE);
    }
 
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) 
    {
        //db.execSQL("drop table if exists " + TABLE_NAME);
        onCreate(db);
    }
    
    public void getAllRouteData(ArrayList<HashMap<String, Object>> listItem)
    {
    	Cursor c = this.getReadableDatabase().rawQuery("select * from " + TABLE_NAME_ROUTE, null);
    	//int columnsSize = c.getColumnCount(); 
    	
    	//int number = c.getCount();
    	int sample_num = -1;
    	while (c.moveToNext()) 
    	{  
            HashMap<String, Object> map = new HashMap<String, Object>();  
            
            //for (int i = 0; i < columnsSize; i++) 
            //{  
                map.put(COLUMN_NAME_ROUTEID, c.getString(0));  
                map.put(COLUMN_NAME_ROUTENAME, c.getString(1));  
                map.put(COLUMN_NAME_ROUTESTART, c.getString(2));  
                map.put(COLUMN_NAME_ROUTEEND, c.getString(3)); 
                sample_num = getSampleNumberByRouteName(c.getString(1));
                map.put(COLUMN_NAME_ROUTESAMPLENUMBER, String.valueOf(sample_num));
            //}  
            listItem.add(map);  
        }  
    	
    }
    
    public int getSampleNumberByRouteName(String routeName)
    {
    	Cursor c = this.getReadableDatabase().rawQuery("select * from " + TABLE_NAME_SLOPE + " where name='" + routeName + "'", null);
    	//int columnsSize = c.getColumnCount(); 
    	
    	int number = c.getCount();
    	return number;
    }
    
    public void getAllSlopeData(ArrayList<HashMap<String, Object>> listItem)
    {
    	Cursor c = this.getReadableDatabase().rawQuery("select * from " + TABLE_NAME_SLOPE, null);
    	//int columnsSize = c.getColumnCount(); 
    	
    	//int number = c.getCount();
    	//int sample_num = -1;
    	while (c.moveToNext()) 
    	{  
            HashMap<String, Object> map = new HashMap<String, Object>();  
            
            //for (int i = 0; i < columnsSize; i++) 
            //{  
                map.put(COLUMN_NAME_SLOPEID, c.getString(0));  
                map.put(COLUMN_NAME_SLOPENAME, c.getString(1));  
                map.put(COLUMN_NAME_SLOPEDEGREE, c.getString(2));      
            //}  
            listItem.add(map);  
        }  
    }
    
    public void getAllSlopeDataInDegree(ArrayList<HashMap<String, Object>> listItem)
    {
    	Cursor c = this.getReadableDatabase().rawQuery("select * from " + TABLE_NAME_SLOPE, null);
    	//int columnsSize = c.getColumnCount(); 
    	
    	//int number = c.getCount();
    	//int sample_num = -1;
    	while (c.moveToNext()) 
    	{  
            HashMap<String, Object> map = new HashMap<String, Object>();  
            String degree_str = c.getString(2);
            double degree = DegreeCalc.calcDegree(Integer.valueOf(degree_str));
            
            //for (int i = 0; i < columnsSize; i++) 
            //{  
                map.put(COLUMN_NAME_SLOPEID, c.getString(0));  
                map.put(COLUMN_NAME_SLOPENAME, c.getString(1));  
                map.put(COLUMN_NAME_SLOPEDEGREE, String.valueOf(degree));      
            //}  
            listItem.add(map);  
        }  
    }
    
    public void getSlopeDataByRouteName(ArrayList<HashMap<String, Object>> listItem, String routeName)
    {
    	Cursor c = this.getReadableDatabase().rawQuery("select * from " + TABLE_NAME_SLOPE + " where name='" +routeName+"'" , null);
    	//int columnsSize = c.getColumnCount(); 
    	
    	//int number = c.getCount();
    	//int sample_num = -1;
    	while (c.moveToNext()) 
    	{  
            HashMap<String, Object> map = new HashMap<String, Object>();  
            
            //for (int i = 0; i < columnsSize; i++) 
            //{  
                map.put(COLUMN_NAME_SLOPEID, c.getString(0));  
                map.put(COLUMN_NAME_SLOPENAME, c.getString(1));  
                map.put(COLUMN_NAME_SLOPEDEGREE, c.getString(2));      
            //}  
            listItem.add(map);  
        }  
    }
    
    public void getSlopeDataInDegreeByRouteName(ArrayList<HashMap<String, Object>> listItem, String routeName)
    {
    	Cursor c = this.getReadableDatabase().rawQuery("select * from " + TABLE_NAME_SLOPE + " where name='" +routeName+"'" , null);
    	//int columnsSize = c.getColumnCount(); 
    	
    	//int number = c.getCount();
    	//int sample_num = -1;
    	while (c.moveToNext()) 
    	{  
            HashMap<String, Object> map = new HashMap<String, Object>();  
            String degree_str = c.getString(2);
            double degree = DegreeCalc.calcDegree(Integer.valueOf(degree_str));
            //for (int i = 0; i < columnsSize; i++) 
            //{  
                map.put(COLUMN_NAME_SLOPEID, c.getString(0));  
                map.put(COLUMN_NAME_SLOPENAME, c.getString(1));  
                map.put(COLUMN_NAME_SLOPEDEGREE, String.valueOf(degree));      
            //}  
            listItem.add(map);  
        }  
    }
}
