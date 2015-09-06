package com.example.fillmemory_beta;




import java.io.File;
import java.util.HashMap;






import android.support.v7.app.ActionBarActivity;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.ActivityManager.MemoryInfo;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends ActionBarActivity {
  
	ActivityManager.MemoryInfo outInfo = new ActivityManager.MemoryInfo();
	long totalFill = 0;
	long lastTotalFill = 0;
    long AvailMemorySize=0;
	final String myMessage = "";
	final int MY_NOTIFICATION_ID = 1;
    private long UsedMemorySize=0;
    String Init_text_EnterFillsize="";
	NotificationManager notificationManager;
	Notification myNotification;
	Memory_Info Memory;
	Button Start_Button;
	EditText editMemorySize;
	TextView text_EnterFillsize,text_Fillmemory,text_TotalMemory,text_AvailMemory,text_MemoryInfoAvail,text_memoryInfoTotal,text_UseMemorySize;
	SharedPreferences isRunningFlag;
	SharedPreferences SaveUsedMemorySize;
	HashMap<String,Object>  map=new HashMap<String,Object>() ;
	Message message;
	Thread tMonitor;
	
	private boolean MonitorFlag = true;
    
	public static Context MainActivityContext;
	public static Handler messageHandler = new Handler();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		 MainActivityContext=this.getApplicationContext();
		Memory=new Memory_Info();
		AvailMemorySize=(Memory.getAvailMemory()/(1024*1024));//Size MB
		Log.i(PathAndFlag.Tag,"系統可用記憶體:"+(AvailMemorySize)+"MB");
	
		
		editMemorySize= (EditText)findViewById(R.id.editMemorySize);
		text_EnterFillsize=(TextView)findViewById(R.id.text_EnterFillsize);
		text_Fillmemory=(TextView)findViewById(R.id.text_Fillmemory);
		Start_Button= (Button) findViewById(R.id.Start_Button);
		text_TotalMemory=(TextView)findViewById(R.id.text_TotalMemory);
		text_AvailMemory=(TextView)findViewById(R.id.text_AvailMemory);
		text_UseMemorySize=(TextView)findViewById(R.id.	text_UseMemorySize);
		long MemoryTotalSize=(Memory.getTotalMemory()/(1024*1024));
		Init_text_EnterFillsize=text_EnterFillsize.getText().toString();
		text_TotalMemory.setText(String.valueOf(MemoryTotalSize)+"MB");
		text_AvailMemory.setText(String.valueOf(AvailMemorySize)+"MB");
		
		SaveUsedMemorySize =this.getSharedPreferences(
				"MemoryInfo", 0);
		if(FillMempryServiceCall.RunnFirst ==false)
		{
			UsedMemorySize=SaveUsedMemorySize.getLong("Usedmemory", 0);
			Log.i(PathAndFlag.LogcatTAG,"FillMempryServiceCall.class=: "+FillMempryServiceCall.class);
		}
		
		text_UseMemorySize.setText(UsedMemorySize+"MB");
		messageHandler = new Handler()
		{
		    @Override
		    public void handleMessage(Message msg)
		    {
		        // TODO Auto-generated method stub
		        super.handleMessage(msg);
		        //Log.i(PathAndFlag.Tag,"進入handleMessage");
		        switch (msg.what)
		        {
		            case 1:
		            	text_AvailMemory.setText(String.valueOf((Memory.getAvailMemory()/(1024*1024)))+"MB");
		            
		                break;
		            case 2:
		            	Bundle GetDataMsg = msg.getData();
		            	long BefortMemory=GetDataMsg.getLong("Befort AvailMemory");
		            	long AfterMemory=GetDataMsg.getLong("After AvailMemory");
		            	UsedMemorySize=UsedMemorySize+(BefortMemory-AfterMemory);
		            	text_UseMemorySize.setText(String.valueOf((UsedMemorySize)+"MB"));
		            	
		            	Start_Button.setEnabled(true);
		            default:
		                break;
		        }
		    }
		};
		Start_Button.setOnClickListener(new OnClickListener() {
		
			@Override
			public void onClick(View v) {
				
				if(editMemorySize.getText().toString().isEmpty()==false)
				{
					
					long MemorySizelimit=UsedMemorySize+Integer.valueOf(editMemorySize.getText().toString());
				if(  Integer.valueOf(editMemorySize.getText().toString())>=10 && Integer.valueOf(editMemorySize.getText().toString()) <=80 && MemorySizelimit<=80)
				{
				// TODO Auto-generated method stub
			    text_EnterFillsize.setText(Init_text_EnterFillsize);
				Log.i(PathAndFlag.LogcatTAG,"FillupMemory Start fill up Memory");
				Thread tStorage = new Thread(new Runnable() {
					@Override
					public void run() {
					
						try {
							String FillMemorySize=editMemorySize.getText().toString();
							UpdateUI(1);
							Intent i = new Intent(MainActivity.this,FillMempryServiceCall.class);
							i.putExtra("FillupSize", FillMemorySize);
						    i.putExtra("Hash", map);
							startService(i);
						} catch (Exception ee) {
							Log.e(PathAndFlag.LogcatTAG,"FillupMemory Start_Button : "+ ee.getMessage());
						}
					}
				});
				tStorage.start();
				Start_Button.setEnabled(false);
				}
				else if(MemorySizelimit>80)
				{
					text_EnterFillsize.setText("已超過可塞Memroy極限");
			
				}
				else
				{
					text_EnterFillsize.setText("請重新輸入介於10-80數值");
				}
				}
				else
				{
					text_EnterFillsize.setText("請輸入介於10-80數值");
				}
			}
		
		 });
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {

	savedInstanceState.putLong("Usedmemory", UsedMemorySize);


	super.onSaveInstanceState(savedInstanceState);
	Log.i(PathAndFlag.LogcatTAG,"onSaveInstanceState()");

	}
	@Override


	public void onRestoreInstanceState(Bundle savedInstanceState) {


	super.onRestoreInstanceState(savedInstanceState);
	UsedMemorySize = savedInstanceState.getLong("Usedmemory");
	Log.i(PathAndFlag.LogcatTAG,"onRestoreInstanceState()");
	}
	protected void onResume() {
		// TODO Auto-generated method stub
		
		super.onResume();
		isRunningFlag = this.getSharedPreferences(
				"isFillupRunning", 0);
		isRunningFlag.edit().putBoolean("isRunning", false).commit();		
		 
		tMonitor = new Thread(new MemoryInfoMonitor());
		tMonitor.start();
		Log.i(PathAndFlag.LogcatTAG,"onResume()");
		
		
	}
	@Override
	protected void onStart(){
		super.onStart();
		Log.i(PathAndFlag.LogcatTAG,"onStart()");
		//SaveUsedMemorySize =this.getSharedPreferences(
		//		"MemoryInfo", 0);
		//UsedMemorySize=SaveUsedMemorySize.getLong("Usedmemory", 0);
		
	}
	@Override
	protected void onRestart(){
		super.onRestart();
		
		Log.i(PathAndFlag.LogcatTAG,"onRestart()");
	}
	@Override
	public void onPause()
    {
        super.onPause();
        SaveUsedMemorySize = this.getSharedPreferences(
				"MemoryInfo", 0);
        SaveUsedMemorySize.edit().putLong("Usedmemory", UsedMemorySize).commit();
        Log.i(PathAndFlag.LogcatTAG,"onPause()");
       
    }
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		
		super.onDestroy();
		Log.i(PathAndFlag.LogcatTAG,"onDestroy()");
	}
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		
		super.onStop();
		Log.i(PathAndFlag.LogcatTAG,"onStop()");
	}
	private void UpdateUI(int event) {
		message = new Message();
		message.what = event;
	
		MainActivity.this.messageHandler.sendMessage(message);
	}
	class MemoryInfoMonitor implements Runnable {

		Message message;
		@Override
		public void run() {
			// TODO Auto-generated method stub
		
			try {

				while (MonitorFlag) {
	
					UpdateUI(1);
					Thread.sleep(1000);
					if (!MonitorFlag)
						break;

				}
			} catch (Exception ee) {
				Log.e(PathAndFlag.LogcatTAG,
						"Fillup MemoryInfoMonitorthread Error : "
								+ ee.getMessage());
			}
		}

	}
	
}


