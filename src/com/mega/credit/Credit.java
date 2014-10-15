/*
       Licensed to the Apache Software Foundation (ASF) under one
       or more contributor license agreements.  See the NOTICE file
       distributed with this work for additional information
       regarding copyright ownership.  The ASF licenses this file
       to you under the Apache License, Version 2.0 (the
       "License"); you may not use this file except in compliance
       with the License.  You may obtain a copy of the License at

         http://www.apache.org/licenses/LICENSE-2.0

       Unless required by applicable law or agreed to in writing,
       software distributed under the License is distributed on an
       "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
       KIND, either express or implied.  See the License for the
       specific language governing permissions and limitations
       under the License.
 */

package com.mega.credit;

import java.io.IOException;
import java.lang.reflect.Field;
import org.apache.cordova.CordovaActivity;
import org.apache.cordova.plugin.HXiMateDeviceSDK;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

public class Credit extends CordovaActivity 
{
    private static int count = 0;
//    public static int cardreader_type;
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        super.init();
        View view = LayoutInflater.from(this).inflate(R.layout.urltext, null);
        final EditText eturl = (EditText) view.findViewById(R.id.et_url);
        eturl.setText(MyConstants.spf.getString("url", ""));
        final RadioButton rb_ureal = (RadioButton) view.findViewById(R.id.rb_ureal);
        final RadioButton rb_real = (RadioButton) view.findViewById(R.id.rb_real);
        RadioButton rb_bth = (RadioButton) view.findViewById(R.id.rb_bth);
        rb_bth.setButtonDrawable(R.drawable.checkbox_checked_style);
        rb_real.setButtonDrawable(R.drawable.checkbox_checked_style);
        rb_ureal.setButtonDrawable(R.drawable.checkbox_checked_style);
        MyConstants.setPowerOnSFZ();
        if (MyConstants.spf.getInt("cardreader_type", 0)==1)
		{
			rb_ureal.setChecked(true);
		}
        else if (MyConstants.spf.getInt("cardreader_type", 0)==2) 
        {
			rb_real.setChecked(true);
		}
        else {
			rb_bth.setChecked(true);
		}
        new AlertDialog.Builder(this).setTitle("选择").setView(view).setCancelable(false).setPositiveButton("退出", new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				Intent exit = new Intent(Intent.ACTION_MAIN);
				exit.addCategory(Intent.CATEGORY_HOME);
				exit.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(exit);
				try
				{
					if (HXiMateDeviceSDK.mBTHSocket!=null)
					{
						HXiMateDeviceSDK.mBTHSocket.close();
					}
					if (HXiMateDeviceSDK.mmInStream!=null)
					{
						HXiMateDeviceSDK.mmInStream.close();
					}
					if (HXiMateDeviceSDK.mmOutStream!=null)
					{
						HXiMateDeviceSDK.mmOutStream.close();
					}
				} catch (IOException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				System.exit(0);
			}
		}).setNegativeButton("确定", new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				String url = eturl.getText().toString();
				Field field;
				try
				{
					field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");
					field.setAccessible(true);
					if (rb_ureal.isChecked()==true)
					{
						MyConstants.editor.putInt("cardreader_type", 1);
					}
					else if (rb_real.isChecked()==true){
						MyConstants.editor.putInt("cardreader_type", 2);
					}else {
						MyConstants.editor.putInt("cardreader_type", 3);
					}
					MyConstants.editor.commit();
					if (url.equals(""))
					{
						Toast.makeText(Credit.this, "URL不能为空！", Toast.LENGTH_SHORT).show();
						field.set(dialog, false);// 点击dialog不消失
					}
					else {
						field.set(dialog, true);// 点击dialog消失
						loadUrl(url);
					}
					MyConstants.editor.putString("url", url);
					MyConstants.editor.commit();
					
				} catch (NoSuchFieldException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalArgumentException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}).setMessage("请选择二代证读卡器类型并输入主页URL：").show();
    }
    
    @Override
	public boolean dispatchKeyEvent(KeyEvent event) 
	{
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) 
	    {     
			count++;
			if (count==2)
			{
				
			}
			else {
				quit();
			}
			return true;
	    }
		return super.dispatchKeyEvent(event);
	}
    
    /**
     * 
     *  函数名称 : quit
     *  功能描述 : 退出程序 
     *  参数及返回值说明：
     *
     *  修改记录：
     *  	日期：2014-4-28 上午11:46:33	修改人：kcx
     *  	描述	：
     *
     */
    private void quit()
	{
		new AlertDialog.Builder(this).setTitle("退出").setPositiveButton("确定", new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				count=0;
				Intent exit = new Intent(Intent.ACTION_MAIN);
				exit.addCategory(Intent.CATEGORY_HOME);
				exit.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(exit);
				try
				{
					if (HXiMateDeviceSDK.mBTHSocket!=null)
					{
						HXiMateDeviceSDK.mBTHSocket.close();
					}
					if (HXiMateDeviceSDK.mmInStream!=null)
					{
						HXiMateDeviceSDK.mmInStream.close();
					}
					if (HXiMateDeviceSDK.mmOutStream!=null)
					{
						HXiMateDeviceSDK.mmOutStream.close();
					}
				} catch (IOException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				System.exit(0);
			}
		}).setNegativeButton("取消", new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				count=0;
				dialog.cancel();
			}
		}).setMessage("您确定要退出本系统?").show();
	}
}

