package com.mega.credit;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;

public class SplashActivity extends Activity
{

	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_splash);
		new CountDownTimer(3000, 1500)
		{

			@Override
			public void onTick(long millisUntilFinished)
			{
			}

			@Override
			public void onFinish()
			{
				Intent intent = new Intent();
				intent.setClass(SplashActivity.this, Credit.class);
				startActivity(intent);

				int VERSION = android.os.Build.VERSION.SDK_INT;
				if (VERSION >= 5)
				{
					SplashActivity.this.overridePendingTransition(R.anim.alpha_in, R.anim.alpha_out);
				}
				finish();
			}
		}.start();
		// Thread thread = new Thread(new Runnable()
		// {
		//
		// @Override
		// public void run()
		// {
		// // TODO Auto-generated method stub
		// try
		// {
		// Thread.sleep(3000);
		// } catch (InterruptedException e)
		// {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// Intent intent = new Intent();
		// intent.setClass(SplashActivity.this, Credit.class);
		// startActivity(intent);
		// finish();
		// }
		// });
		// thread.start();

	}

	/**
	 * 
	 * 函数名称 : onKeyDown 功能描述 : 屏蔽返回键 参数说明：
	 * 
	 * @param keyCode
	 * @param event
	 * @return 返回值：
	 * 
	 *         修改记录： 日期：2013-1-5 上午9:59:18 修改人：kcx 描述 ：
	 * 
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{

		switch (keyCode)
		{
		case KeyEvent.KEYCODE_BACK:
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onDestroy()
	{
		// TODO Auto-generated method stub
		super.onDestroy();
	}

}
