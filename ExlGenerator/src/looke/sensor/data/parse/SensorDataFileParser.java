package looke.sensor.data.parse;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SensorDataFileParser
{
	static String	dataFileLocation	= "D:\\SensorDataFile.dat";

	static public List<SensorData> parseDataFile()
	{
		File file = new File(dataFileLocation);
		BufferedReader reader = null;

		List<SensorData> dataList = new ArrayList<SensorData>();
		try
		{
			reader = new BufferedReader(new FileReader(file));
			String readline;
			String[] elementDataPair = null;
			int line = 0;
			// 一次读入一行，直到读入null为文件结束
			while ((readline = reader.readLine()) != null)
			{
				// 解析滤波前后数据
				elementDataPair = readline.split(";");
				SensorData currentData = new SensorData();
				for(int i=0; i<elementDataPair.length; i++)
				{
					String[] temp = elementDataPair[i].split("=");
					String dataName = temp[0];
					String dataValue = temp[1];
					DataPair pair = new DataPair(dataName, dataValue);
					currentData.addDataPair(pair);
				}
				
				dataList.add(currentData);

				line++;
			}
			reader.close();
			System.out.print("Total Line:" + line);
		} 
		catch (IOException e)
		{
			e.printStackTrace();
		} 
		finally
		{
			if (reader != null)
			{
				try
				{
					reader.close();
				} catch (IOException e1)
				{
				}
			}
		}
		
		return dataList;
	}

}
