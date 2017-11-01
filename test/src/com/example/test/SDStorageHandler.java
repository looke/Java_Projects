package com.example.test;

import android.os.Environment;

public class SDStorageHandler
{

	
	/**
	 * 判断SDCard是否存在 [当没有外挂SD卡时，内置ROM也被识别为存在sd卡]
	 * 
	 * @return
	 */
	public static boolean isSdCardExist() 
	{
		return Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED);
	}	
	
	
	/**
	 * 获取SD卡根目录路径
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
