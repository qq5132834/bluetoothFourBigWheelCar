 
package com.beyond.testbt;

 
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class ScaleActivity extends Activity {
	// Debugging
	private static final String TAG = "OtherActivity";
	private static final boolean D = true;
	// Message types sent from the BluetoothChatService Handler
	public static final int MESSAGE_STATE_CHANGE = 1;
	public static final int MESSAGE_READ = 2;
	public static final int MESSAGE_WRITE = 3;
	public static final int MESSAGE_DEVICE_NAME = 4;
	public static final int MESSAGE_TOAST = 5;

	// Key names received from the BluetoothChatService Handler
	public static final String DEVICE_NAME = "device_name";
	public static final String TOAST = "toast";

	// Intent request codes
	private static final int REQUEST_CONNECT_DEVICE = 1;
	private static final int REQUEST_ENABLE_BT = 2;

	// Layout Views
	private TextView mTitle;
	private ListView mConversationView;
	// Name of the connected device
	private String mConnectedDeviceName = null;
	// Array adapter for the conversation thread
	private ArrayAdapter<String> mConversationArrayAdapter;
	 
	private BluetoothAdapter mBluetoothAdapter = null;
 
	private BluetoothService bluetoothService = null;

	private String numcode = "";
	private Object lock = new Object();
	
	
	private Button button_up = null;
	private Button button_stop = null;
	private Button button_left = null;
	private Button button_right = null;
	private Button button_back = null;
	private RadioGroup radio_speed = null;//速度选择
	
	private static String DONOTHING = "N"; //do nothing 
	private String fontOrBack = DONOTHING; //N表示在前进和后退方向上没有作任何操作
	private String leftOrRight = DONOTHING; //N表示在左右方向上没有作任何操作
	private String speed = "M";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (D)
			Log.e(TAG, "+++ ON CREATE +++");

		// Set up the window layout
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.other);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,R.layout.custom_title);

		// Set up the custom title
		mTitle = (TextView) findViewById(R.id.title_left_text);
		mTitle.setText(R.string.app_name);
		mTitle = (TextView) findViewById(R.id.title_right_text);
		
		this.button_up = (Button) this.findViewById(R.id.buttton_UP);
		this.button_stop = (Button) this.findViewById(R.id.buttton_STOP);
		this.button_left = (Button) this.findViewById(R.id.buttton_LEFT);
		this.button_right = (Button) this.findViewById(R.id.buttton_RIGHT);
		this.button_back = (Button) this.findViewById(R.id.buttton_BACK);
		this.radio_speed = (RadioGroup) this.findViewById(R.id.car_speed);
		
		// Get local Bluetooth adapter
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

		// If the adapter is null, then Bluetooth is not supported
		if (mBluetoothAdapter == null) {
			Toast.makeText(this, "Bluetooth is not available",Toast.LENGTH_LONG).show();
			finish();
			return;
		}
		
		/**
		 * click "向前" button
		 * */
		this.button_up.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Toast.makeText(getApplicationContext(), "font", Toast.LENGTH_LONG).show();
				if (null != bluetoothService) {
					
					//获取速度
					RadioButton radioButton =  (RadioButton) findViewById(radio_speed.getCheckedRadioButtonId());
					String radioText = radioButton.getText().toString();
					if(radioText.indexOf("低")!=-1){
						speed = "L";
					}else if(radioText.indexOf("中")!=-1){
						speed = "M";
					}else if(radioText.indexOf("高")!=-1){
						speed = "H";
					}
					
					
					
					fontOrBack = "F";
					String opt = fontOrBack.trim()+DONOTHING+speed;
					bluetoothService.write(StringHexUtils.hexStr2Bytes(StringHexUtils.encode(opt.trim())));  
				}
			}
		});
		/**
		 * click "停止" button
		 * */
		this.button_stop.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Toast.makeText(getApplicationContext(), "stop", Toast.LENGTH_LONG).show();
				if (null != bluetoothService) {
					
					//获取速度
					RadioButton radioButton =  (RadioButton) findViewById(radio_speed.getCheckedRadioButtonId());
					String radioText = radioButton.getText().toString();
					if(radioText.indexOf("低")!=-1){
						speed = "L";
					}else if(radioText.indexOf("中")!=-1){
						speed = "M";
					}else if(radioText.indexOf("高")!=-1){
						speed = "H";
					}
					
					fontOrBack = "S";
					leftOrRight = "S";
					String opt = fontOrBack.trim()+leftOrRight.trim()+speed;
					bluetoothService.write(StringHexUtils.hexStr2Bytes(StringHexUtils.encode(opt.trim()))); 
				}
				
			}
		});
		/**
		 * click "向左" button
		 * */
		this.button_left.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
			    Toast.makeText(getApplicationContext(), "left", Toast.LENGTH_LONG).show();
				if (null != bluetoothService) {
					
					//获取速度
					RadioButton radioButton =  (RadioButton) findViewById(radio_speed.getCheckedRadioButtonId());
					String radioText = radioButton.getText().toString();
					if(radioText.indexOf("低")!=-1){
						speed = "L";
					}else if(radioText.indexOf("中")!=-1){
						speed = "M";
					}else if(radioText.indexOf("高")!=-1){
						speed = "H";
					}
					
					leftOrRight = "L";
					String opt = fontOrBack.trim()+leftOrRight.trim()+speed;
					bluetoothService.write(StringHexUtils.hexStr2Bytes(StringHexUtils.encode(opt.trim())));   
				}
			}
		});
		/**
		 * click "向右" button
		 * */
		this.button_right.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Toast.makeText(getApplicationContext(), "right", Toast.LENGTH_LONG).show();
				if (null != bluetoothService) {
					
					//获取速度
					RadioButton radioButton =  (RadioButton) findViewById(radio_speed.getCheckedRadioButtonId());
					String radioText = radioButton.getText().toString();
					if(radioText.indexOf("低")!=-1){
						speed = "L";
					}else if(radioText.indexOf("中")!=-1){
						speed = "M";
					}else if(radioText.indexOf("高")!=-1){
						speed = "H";
					}
					
					leftOrRight = "R";
					String opt = fontOrBack.trim()+leftOrRight.trim()+speed;
					bluetoothService.write(StringHexUtils.hexStr2Bytes(StringHexUtils.encode(opt.trim())));    
				}
			}
		});
		 
		this.button_back.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Toast.makeText(getApplicationContext(), "back", Toast.LENGTH_LONG).show();
				if (null != bluetoothService) {
					
					//获取速度
					RadioButton radioButton =  (RadioButton) findViewById(radio_speed.getCheckedRadioButtonId());
					String radioText = radioButton.getText().toString();
					if(radioText.indexOf("低")!=-1){
						speed = "L";
					}else if(radioText.indexOf("中")!=-1){
						speed = "M";
					}else if(radioText.indexOf("高")!=-1){
						speed = "H";
					}
					
					fontOrBack = "B";
					String opt = fontOrBack.trim()+DONOTHING+speed;
					bluetoothService.write(StringHexUtils.hexStr2Bytes(StringHexUtils.encode(opt.trim())));    
				}
			}
		});
		
	 
	}

 
	@Override
	public void onStart() {
		super.onStart();
		if (D)
			Log.e(TAG, "++ ON START ++");
		
		if (!mBluetoothAdapter.isEnabled()) {
			//采用询问的方式，请求用户开启蓝牙
			Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
		} else {
			if (bluetoothService == null)
				setupConnect();
		}
	}

	@Override
	public synchronized void onResume() {
		super.onResume();
		if (D)
			Log.e(TAG, "+ ON RESUME +");
 
		if (bluetoothService != null) {
			if (bluetoothService.getState() == BluetoothService.STATE_NONE) {
				// Start the Bluetooth chat services
				bluetoothService.start();
			}
		}
	}

	private synchronized void setupConnect() {
		Log.d(TAG, "setupChat()");
		mConversationArrayAdapter = new ArrayAdapter<String>(this,R.layout.message);
		mConversationView = (ListView) findViewById(R.id.in);
		mConversationView.setAdapter(mConversationArrayAdapter);
		bluetoothService = new BluetoothService(this, mHandler);//建立以个蓝牙通信链接
	}

	@Override
	public synchronized void onPause() {
		super.onPause();
		if (D)
			Log.e(TAG, "- ON PAUSE -");
	}

	@Override
	public void onStop() {
		super.onStop();
		if (D) Log.e(TAG, "-- ON STOP --");
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		if (bluetoothService != null){
			bluetoothService.stop();  
		}
		
	 
		if (D) Log.e(TAG, "--- ON DESTROY ---");
	}

	private void ensureDiscoverable() {
		if (D)
			Log.d(TAG, "ensure discoverable");
		if (mBluetoothAdapter.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
			Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
			discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
			startActivity(discoverableIntent);
		}
	}

	// The Handler that gets information back from the BluetoothChatService
	private final Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MESSAGE_STATE_CHANGE:
				if (D)
					Log.i(TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1);
				switch (msg.arg1) {
				case BluetoothService.STATE_CONNECTED:
					mTitle.setText(R.string.title_connected_to);
					mTitle.append(mConnectedDeviceName);
					mConversationArrayAdapter.clear();
					break;
				case BluetoothService.STATE_CONNECTING:
					mTitle.setText(R.string.title_connecting);
					break;
				case BluetoothService.STATE_LISTEN:
				case BluetoothService.STATE_NONE:
					mTitle.setText(R.string.title_not_connected);
					break;
				}
				break;
			case MESSAGE_WRITE:
				byte[] writeBuf = (byte[]) msg.obj;
				// construct a string from the buffer
				String writeMessage = new String(writeBuf);
				mConversationArrayAdapter.add("Sended:  " + writeMessage);
				break;
			case MESSAGE_READ:
				 
				/**
				 * 异步接受Arduino向Android发送回来的数据，然后数据摘要在这里处理
				 * */
				synchronized (lock) {
					byte[] readBuf = (byte[]) msg.obj;
					String str = new String(readBuf);//当接受到的byte[]转为字符串
					Log.i("从arduino接受到的数据", str); 	 
						mConversationArrayAdapter.add("received:  " + str);				
				}
				
				break;
			case MESSAGE_DEVICE_NAME:
				// save the connected device's name
				mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
				// Toast.makeText(getApplicationContext(),"Connected to " + mConnectedDeviceName,Toast.LENGTH_SHORT).show(); 
				//if (null != bluetoothService) {
				//	bluetoothService.write(StringHexUtils.hexStr2Bytes(StringHexUtils.encode("F")));  //发送g字符
				//}
				
				break;
			case MESSAGE_TOAST:
				Toast.makeText(getApplicationContext(),msg.getData().getString(TOAST), Toast.LENGTH_SHORT).show();
				break;
			}
		}
	};
	
	
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (D)
			Log.d(TAG, "onActivityResult " + resultCode);
		switch (requestCode) {
		case REQUEST_CONNECT_DEVICE:
			if (resultCode == Activity.RESULT_OK) {
				String address = data.getExtras().getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
				BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
				bluetoothService.connect(device);
			}
			break;
		case REQUEST_ENABLE_BT:
			// When the request to enable Bluetooth returns
			if (resultCode == Activity.RESULT_OK) {
				setupConnect();// 建立蓝天通信链接 
			} else {
				Log.d(TAG, "BT not enabled");
				Toast.makeText(this, R.string.bt_not_enabled_leaving,Toast.LENGTH_SHORT).show();
				finish();
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.option_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.scan:
			Intent serverIntent = new Intent(this, DeviceListActivity.class);
			startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
			return true;
		}
		return false;
	}
	 
  
}