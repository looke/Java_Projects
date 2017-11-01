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
		// ��һ��������һ��webbook����Ӧһ��Excel�ļ�  
        HSSFWorkbook wb = new HSSFWorkbook();  
        // �ڶ�������webbook�����һ��sheet,��ӦExcel�ļ��е�sheet  
        HSSFSheet sheet = wb.createSheet("SensorDataLPFTest");  
        // ����������sheet����ӱ�ͷ��0��,ע���ϰ汾poi��Excel����������������short  
        HSSFRow row = sheet.createRow((int) 0);  
        // ���Ĳ���������Ԫ�񣬲�����ֵ��ͷ ���ñ�ͷ����  
        HSSFCellStyle style = wb.createCellStyle();  
        style.setAlignment(HSSFCellStyle.ALIGN_CENTER); // ����һ�����и�ʽ  
  
        HSSFCell cell;
        //��ӡ��ͷ
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
  
        // ���岽��д��ʵ������ ʵ��Ӧ������Щ���ݴ����ݿ�õ���  
        //List list = SensorDataFileParser.parseDataFile();  
  
        for (int i = 0; i < dataList.size(); i++)  
        {  
            row = sheet.createRow((int) i + 1);  
            SensorData sdata = (SensorData) dataList.get(i);  
            //������Ԫ�񣬲�����ֵ  
            List<DataPair> dPair = sdata.getDataPair();
        	for(int j=0; j<dPair.size(); j++)
        	{	
        		DataPair temp = dPair.get(j);
        		row.createCell((short) j).setCellValue(Float.parseFloat(temp.getValue()));  
        	}
        }  
        
        // �����������ļ��浽ָ��λ��  
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
