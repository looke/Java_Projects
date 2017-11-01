import java.util.List;

import looke.sensor.data.parse.SensorData;
import looke.sensor.data.parse.SensorDataExport;
import looke.sensor.data.parse.SensorDataFileParser;

public class test
{
	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		
		// TODO Auto-generated method stub
		
    	
		SensorDataFileParser myParser = new SensorDataFileParser();
		List<SensorData> dataList = myParser.parseDataFile();
		
		SensorDataExport myExport = new SensorDataExport(dataList);
		myExport.exportToXLS();
	}

}
