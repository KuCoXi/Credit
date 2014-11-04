package com.mega.credit;


import guoTeng.IC2ReadCard.BloothComm;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.os.Message;

public class GTReadCardThread extends Thread
{
	private MyHandler handler;
	private BloothComm com = null;
	private boolean mTerminate = false;
	private boolean mDeviceOk = false;
	private Timer timer;
	public GTReadCardThread(BluetoothDevice device,MyHandler handler) throws SecurityException, NoSuchMethodException, IOException
	{
		// TODO Auto-generated constructor stub
		this.handler = handler;
		com = new BloothComm();
		com.InitBloothComm(device);
		timer = new Timer();
	};
	
	@Override
	public void run()
	{
		// TODO Auto-generated method stub
		TimerTask task = new TimerTask()
		{
			
			@Override
			public void run()
			{
				// TODO Auto-generated method stub
				SendMsg("读卡超时", 21);
			}
		};
		try
		{
			timer.schedule(task, 15000);
			while (!mTerminate)
			{
				if (!com.LinkOk())
				{
					SendMsg("设备连接失败",21);
					break;
				}

				if (!mDeviceOk)
				{
					String szSamId = com.ReadSamidCmd();
					if (szSamId.length() <= 10)
					{
						SendMsg("正在查找设备",20);
						continue;
					}

					mDeviceOk = true;
					SendMsg("设备连接成功...",20);
				}

				if (com.FindCardCmd() == 0x9e)
				{
					mDeviceOk = false;
					SendMsg("正在查找设备",20);
					Thread.sleep(100);
					continue;
				}

				if (mTerminate)
					break;

				if (com.SelCardCmd() != 0x90)
				{
					;
				}

				if (mTerminate)
					break;

				if (com.ReadCardCmd() != 0x90)
				{
					SendMsg("请重新放卡",21);
//					Thread.sleep(200);
					break;
				}

				if (mTerminate)
					break;

				Bundle bundle = new Bundle();
				Person person = new Person();
				person.setPeopleAddress(com.mAddress);
//				String szTmp = com.mBirth.substring(0, 4) + "年" + com.mBirth.substring(4, 6) + "月" + com.mBirth.substring(6, 8) + "日";
				person.setPeopleBirthday(com.mBirth);
				person.setPeopleIDCode(com.mIdNo);
				person.setMsg("读卡成功");
				person.setPeopleName(com.mName);
				person.setPeopleNation(com.mNation);
				person.setDepartment(com.mPolice);
				person.setPeopleSex(com.mSex);
				person.setStartDate(com.mStart);
				person.setEndDate(com.mEnd);
				person.setBase64(com.mBase64data);
				bundle.putSerializable("person", person);
				Message msg = new Message();
				msg.setData(bundle);
				msg.what = 22;
				handler.sendMessage(msg);
				mTerminate = true;
				timer.cancel();
			}
		} catch (InterruptedException e)
		{
			// TODO Auto-generated catch block
			timer.cancel();
			e.printStackTrace();
		}
		com.ClearBloothComm();

		SendMsg("设备未连接，设备打开后请点击读卡",21);
	}
	
	private void SendMsg(String szMsg,int mWhat)
	{
		Bundle bundle = new Bundle();
		Person person = new Person(szMsg);
		bundle.putSerializable("person", person);
		Message msg = new Message();
		msg.setData(bundle);
		msg.what = mWhat;
		handler.sendMessage(msg);
	}
	
}
