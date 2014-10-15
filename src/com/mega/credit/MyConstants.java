package com.mega.credit;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import com.authentication.utils.ContextUtils;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

public class MyConstants
{
	/** 配置文件 */
	public static SharedPreferences spf;
	/** 配置文件编辑器 */
	public static Editor editor;
	static
	{
		spf = PreferenceManager.getDefaultSharedPreferences(ContextUtils.getInstance().getApplicationContext());
		editor = spf.edit();
	}
	public static void setPowerOnSFZ()
	{
		File awakeTimeFile = new File("/proc/s706_power/s706_power_sfz");
        FileWriter fr;
        try
       {
             fr = new FileWriter(awakeTimeFile);
             fr.write("1"); 
             fr.close();
        } 
        catch (IOException e) 
        {
                    e.printStackTrace();
         }
	}
}
