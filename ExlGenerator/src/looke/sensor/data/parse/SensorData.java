package looke.sensor.data.parse;

import java.util.ArrayList;
import java.util.List;

public class SensorData
{
	//int id = 0;
	//float beforeFilter = 0;
	//float afterFilter = 0;
	List<DataPair> data = new ArrayList<DataPair>();
	
	public SensorData()
	{
	
	}
	
	public void addDataPair(DataPair newData)
	{
		this.data.add(newData);
	}
	
	public List<DataPair> getDataPair()
	{
		return this.data;
	}
	/*
	public SensorData(int id, float before, float after)
	{
		this.id = id;
		this.beforeFilter = before;
		this.afterFilter = after;
	}
	
	public int getId()
	{
		return id;
	}
	public void setId(int id)
	{
		this.id = id;
	}
	public float getBeforeFilter()
	{
		return beforeFilter;
	}
	public void setBeforeFilter(float beforeFilter)
	{
		this.beforeFilter = beforeFilter;
	}
	public float getAfterFilter()
	{
		return afterFilter;
	}
	public void setAfterFilter(float afterFilter)
	{
		this.afterFilter = afterFilter;
	}
	*/
}
