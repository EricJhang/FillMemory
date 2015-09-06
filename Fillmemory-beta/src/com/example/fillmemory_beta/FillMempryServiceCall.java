package com.example.fillmemory_beta;

import java.io.File;




import java.util.ArrayList;
import java.util.HashMap;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Environment;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

public class FillMempryServiceCall extends Service {

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	SharedPreferences FillupRunning;
	SharedPreferences FillupThread;
	SharedPreferences SaveUsedMemorySize;
	boolean isRunning = false;
	long currentThreadID;
	long lastThreadID;

	final static int myID = 5;
	//用來判斷此APK是否第一次運行
    public static boolean RunnFirst=true;
	public static Context MainServiceContext;
	private Thread Internalthread;

	
	public static ArrayList<ByteArrayWrapper> mAllocations = new ArrayList<ByteArrayWrapper>();

	public  ArrayList<ByteArrayWrapper> mAllocations_9 = new ArrayList<ByteArrayWrapper>();
	
	
	Message message;
	Thread tMonitor;
	Memory_Info Memory;
	
	public FillMempryServiceCall() {
		// super("FillupStorage_ServiceCall");
		FileIO.WriteLogcat("FillMempryServiceCall SERVICE Constructor");
		
		
	}

	@Override
	public void onLowMemory() {
		// TODO Auto-generated method stub
		FileIO.WriteLogcat("FillMempryServiceCall onLowMemory()");
		super.onLowMemory();
	}

	@Override
	public void onTrimMemory(int level) {
		// TODO Auto-generated method stub

		FileIO.WriteLogcat("FillMempryServiceCall onTrimMemory()");
		super.onTrimMemory(level);
	}

	@Override
	public void onDestroy() {
		FileIO.WriteLogcat("FillMempryServiceCall onDestroy()");		
		super.onDestroy();
		
		
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		FileIO.WriteLogcat("FillMempryServiceCall onStartCommand");
		super.onStartCommand(intent, flags, startId);

		try {
			Memory=new Memory_Info();
			String name = intent.getStringExtra("FillupSize"); 
			long MemorySize=Integer.valueOf(name);
			FillupRunning = FillMempryServiceCall.this.getSharedPreferences("isFillupRunning", 0);
			FillupThread = FillMempryServiceCall.this.getSharedPreferences("FillupThreadID", 0);
			lastThreadID = FillupThread.getLong("lastFillupThreadID", -1);			
			HashMap< String,Object> Map=(HashMap< String,Object>)intent.getSerializableExtra("Hash");
			isRunning = FillupRunning.getBoolean("isRunning", false);
			if (isRunning) {
				FileIO.WriteLogcat("isRunning is true ,do nothing return");
				FileIO.WriteLogcat("isRunningTest(should be true) = "+ isRunning);
				FileIO.WriteLogcat("return START_NOT_STICKY");
				return START_NOT_STICKY;

			} else {
				FileIO.WriteLogcat("isRunning is false ,do fillup and set isRunning is true");
				FillupRunning.edit().putBoolean("isRunning", true).commit();
			}

		
			MainServiceContext = this.getApplicationContext();


			// 提高Service的優先權不被系統殺掉
			Intent intent2 = new Intent(this, FillMempryServiceCall.class);
			intent2.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
					| Intent.FLAG_ACTIVITY_SINGLE_TOP);
		
			FileIO.WriteLogcat("new Internalthread and start");
			Internalthread = new FillupThread(getApplicationContext(),intent,Memory,Map,mAllocations_9, MemorySize);
			
			Internalthread.setPriority(Thread.MAX_PRIORITY);
			Internalthread.start();
			RunnFirst=false;
			
		} catch (Exception e) {
			Log.w(PathAndFlag.Tag, "FillMempryServiceCall onStartCommand Error" + e.getMessage());
			FileIO.WriteLogcat("set Running flag is false");
			FillupRunning.edit().putBoolean("isRunning", false).commit();
		}
		
		
		
		FileIO.WriteLogcat("return START_NOT_STICKY");
		return START_NOT_STICKY;
	}	
	
	
}
