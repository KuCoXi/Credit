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
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

public class Credit extends CordovaActivity 
{
    private static int count = 0;
//    public static int cardreader_type;
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
//        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        super.init();
        View view = LayoutInflater.from(this).inflate(R.layout.urltext, null);
        final EditText eturl = (EditText) view.findViewById(R.id.et_url);
        eturl.setText(MyConstants.spf.getString("url", ""));
//        RadioGroup rg = (RadioGroup) view.findViewById(R.id.rg01);
        final RadioButton rb_yd = (RadioButton) view.findViewById(R.id.rb_yd);
        final RadioButton rb_zd = (RadioButton) view.findViewById(R.id.rb_zd);
        final RadioButton rb_bj = (RadioButton) view.findViewById(R.id.rb_bj);
        rb_bj.setButtonDrawable(R.drawable.checkbox_checked_style);
        rb_zd.setButtonDrawable(R.drawable.checkbox_checked_style);
        rb_yd.setButtonDrawable(R.drawable.checkbox_checked_style);
//        rb_yd.setOnCheckedChangeListener(new OnCheckedChangeListener()
//		{
//			
//			@Override
//			public void onCheckedChanged(CompoundButton arg0, boolean arg1)
//			{
//				// TODO Auto-generated method stub
//				
//			}
//		});
        MyConstants.setPowerOnSFZ();
        if (MyConstants.spf.getInt("cardreader_type", 0)==1)
		{
			rb_zd.setChecked(true);
		}
        else if (MyConstants.spf.getInt("cardreader_type", 0)==2) 
        {
			rb_bj.setChecked(true);
		}
        else if (MyConstants.spf.getInt("cardreader_type", 0)==3){
			rb_yd.setChecked(true);
		}
        new AlertDialog.Builder(this).setTitle("ѡ��").setView(view).setCancelable(false).setPositiveButton("�˳�", new DialogInterface.OnClickListener()
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
		}).setNegativeButton("ȷ��", new DialogInterface.OnClickListener()
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
					if (rb_zd.isChecked()==true)
					{
						MyConstants.editor.putInt("cardreader_type", 1);
					}
					else if (rb_bj.isChecked()==true){
						MyConstants.editor.putInt("cardreader_type", 2);
					}else if (rb_yd.isChecked()==true){
						MyConstants.editor.putInt("cardreader_type", 3);
					}
//					MyConstants.editor.putInt("cardreader_type", 100);//��������
					MyConstants.editor.commit();
					if (url.equals(""))
					{
						Toast.makeText(Credit.this, "URL����Ϊ�գ�", Toast.LENGTH_SHORT).show();
						field.set(dialog, false);// ���dialog����ʧ
					}
					else {
						field.set(dialog, true);// ���dialog��ʧ
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
		}).setMessage("��ѡ�����֤���������Ͳ�������ҳURL��").show();
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
     *  �������� : quit
     *  �������� : �˳����� 
     *  ����������ֵ˵����
     *
     *  �޸ļ�¼��
     *  	���ڣ�2014-4-28 ����11:46:33	�޸��ˣ�kcx
     *  	����	��
     *
     */
    private void quit()
	{
		new AlertDialog.Builder(this).setTitle("�˳�").setPositiveButton("ȷ��", new DialogInterface.OnClickListener()
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
		}).setNegativeButton("ȡ��", new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				count=0;
				dialog.cancel();
			}
		}).setMessage("��ȷ��Ҫ�˳���ϵͳ?").show();
	}
    
}

