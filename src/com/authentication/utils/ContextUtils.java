package com.authentication.utils;

import android.app.Application;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.HandlerThread;
import android.util.Log;

public class ContextUtils extends Application {
    private static ContextUtils instance;
    private HandlerThread handlerThread;
    private String rootPath;
    public static ContextUtils getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        instance = this;
        handlerThread = new HandlerThread("handlerThread");
		handlerThread.start();
		setRootPath();
    }
    
    public HandlerThread getHandlerThread() {
		return handlerThread;
	}
    
    public String getRootPath() {
		return rootPath;
	}
	private void setRootPath() {
		PackageManager manager = this.getPackageManager();
		try {
			PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);
			rootPath = info.applicationInfo.dataDir;
			Log.i("rootPath", "################rootPath=" + rootPath);
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
