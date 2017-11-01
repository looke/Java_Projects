package com.example.listviewtest;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import android.database.Cursor;
import android.os.Environment;
import android.util.Log;

public class DBExporter
{
	public void ExportToCSV(Cursor c, String fileName) 
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
				// д���ͷ
				for (int i = 0; i < colCount; i++) 
				{
					if (i != colCount - 1)
					   bfw.write(c.getColumnName(i) + ',');
					else
					   bfw.write(c.getColumnName(i));
				}
				// д�ñ�ͷ����
				bfw.newLine();
				// д������
				for (int i = 0; i < rowCount; i++) 
				{
					c.moveToPosition(i);
					// Toast.makeText(mContext, "���ڵ�����"+(i+1)+"��",
					// Toast.LENGTH_SHORT).show();
					Log.v("��������", "���ڵ�����" + (i + 1) + "��");
					for (int j = 0; j < colCount; j++) 
					{
						if (j != colCount - 1)
						    bfw.write(c.getString(j) + ',');
						else
						   bfw.write(c.getString(j));
					}
					// д��ÿ����¼����
					bfw.newLine();
				}
			}
			// ����������д���ļ�
			bfw.flush();
			// �ͷŻ���
			bfw.close();
			// Toast.makeText(mContext, "������ϣ�", Toast.LENGTH_SHORT).show();
			Log.v("��������", "������ϣ�");
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
