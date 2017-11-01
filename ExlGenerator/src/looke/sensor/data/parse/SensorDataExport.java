package looke.sensor.data.parse;

import java.io.FileOutputStream;
import java.util.List;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
public class SensorDataExport
{
	List<SensorData> dataList;
	
	public SensorDataExport(List<SensorData> data)
	{
		this.dataList = data;
	}
	
	
	public void exportToXLS()
	{
		// 第一步，创建一个webbook，对应一个Excel文件  
        HSSFWorkbook wb = new HSSFWorkbook();  
        // 第二步，在webbook中添加一个sheet,对应Excel文件中的sheet  
        HSSFSheet sheet = wb.createSheet("SensorDataLPFTest");  
        // 第三步，在sheet中添加表头第0行,注意老版本poi对Excel的行数列数有限制short  
        HSSFRow row = sheet.createRow((int) 0);  
        // 第四步，创建单元格，并设置值表头 设置表头居中  
        HSSFCellStyle style = wb.createCellStyle();  
        style.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 创建一个居中格式  
  
        HSSFCell cell;
        //打印表头
        if(dataList.size() > 0)
        {
        	SensorData sdata = dataList.get(0);
        	List<DataPair> dPair = sdata.getDataPair();
        	for(int i=0; i<dPair.size(); i++)
        	{	
        		DataPair temp = dPair.get(i);
        		cell= row.createCell((short) i);  
                cell.setCellValue(temp.name);  
                cell.setCellStyle(style);  
        	}
        }
  
        // 第五步，写入实体数据 实际应用中这些数据从数据库得到，  
        //List list = SensorDataFileParser.parseDataFile();  
  
        for (int i = 0; i < dataList.size(); i++)  
        {  
            row = sheet.createRow((int) i + 1);  
            SensorData sdata = (SensorData) dataList.get(i);  
            //创建单元格，并设置值  
            List<DataPair> dPair = sdata.getDataPair();
        	for(int j=0; j<dPair.size(); j++)
        	{	
        		DataPair temp = dPair.get(j);
        		row.createCell((short) j).setCellValue(Float.parseFloat(temp.getValue()));  
        	}
        }  
        
        // 第六步，将文件存到指定位置  
        try  
        {  
            FileOutputStream fout = new FileOutputStream("D:/SensorData.xls");  
            wb.write(fout);  
            fout.close();  
        }  
        catch (Exception e)  
        {  
            e.printStackTrace();  
        }  
	}
}
