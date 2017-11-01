package com.example.test;

import android.os.Environment;

public class SDStorageHandler
{

	
	/**
	 * �ж�SDCard�Ƿ���� [��û�����SD��ʱ������ROMҲ��ʶ��Ϊ����sd��]
	 * 
	 * @return
	 */
	public static boolean isSdCardExist() 
	{
		return Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED);
	}	
	
	
	/**
	 * ��ȡSD����Ŀ¼·��
	 * 
	 * @return
	 */
	public static String getSdCardPath() 
	{
		String sdpath;
		boolean exist = isSdCardExist();
		
		if (exist) 
		{
			sdpath = Environment.getExternalStorageDirectory()
					.getAbsolutePath();
		}
		else
		{
			sdpath = "";
		}
		return sdpath;
	}
	
}
