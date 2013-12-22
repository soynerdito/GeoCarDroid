package com.geocar.geocarconnect;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Set;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.geocar.bluetooth.AlertType;
import com.geocar.bluetooth.BthDeviceManager;
import com.geocar.bluetooth.ConnectThread;
import com.geocar.bluetooth.ConnectedThread;
import com.geocar.bluetooth.ConnectionHandler;
import com.geocar.bluetooth.SocketHandler;

public class StartMenu extends Activity implements OnClickListener, SocketHandler  {
	
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		if( item.getItemId() == R.id.action_settings ){
			//This is the settings			
			Intent intent = new Intent(this, PrefActivity.class);
			startActivity(intent);
			
		}
		return super.onMenuItemSelected(featureId, item);
	}
	private ImageButton mConnectButton;
	
	/*private static String BTH_DEVICE_NAME = "GeoCar";	
	private static String SPP_UUID = "00001101-0000-1000-8000-00805F9B34FB";
	
	// Get the default adapter
	private BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
	*/
	
	private int REQUEST_ENABLE_BT = 2;
	private ConnectedThread mConnThread;
	private ConnectThread mConnection;
	private ConnectionHandler mHandler;
	private static final String STATUS = "STATUS";
	
	private Handler mGUIUpdate = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			Bundle bundle = msg.getData();
			if( bundle != null ){
				if( bundle.getBoolean(STATUS) ){
					onStartLoading();
				}else{
					onStop();
				}
			}
		}
		
	};
	
	private void setStatus(boolean connected ){
		mConnectButton.setImageResource(connected?R.drawable.disconnect_button:R.drawable.connect_button);
		//mConnectButton.setText(connected?"Disconnect":"Connect");
		if( connected ){
			//Get Data from Device
			getDataFromGeoCar();
		}
	}
	
	private void onStartLoading(){
		View v =  findViewById( R.id.pbLoading );
		v.setVisibility(View.VISIBLE);
		
		((TextView)findViewById(R.id.tvStatus)).setText(R.string.loading);
	}
	
	private void onStopLoading(){
		View v =  findViewById( R.id.pbLoading );
		v.setVisibility(View.INVISIBLE);
		
		((TextView)findViewById(R.id.tvStatus)).setText(R.string.press_to_start);
	}
	
	public class ConnHandler extends ConnectionHandler{

		public ConnHandler(){
			super();
		}
		
		private BufferedWriter mOutput = null;
		
		public ConnectionHandler init(BufferedWriter output){
			mOutput = output;
			return this;
		}
		
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			Bundle b = msg.getData();
			setStatus( b.getInt(STATUS, 0)>0 );
			
		}

		@Override
		protected void onDisconnect() {
			postMessage( false );
			mConnThread = null;
		}

		@Override
		protected void onDataReceived(String line) {
			//Debug print to see what's in
			System.out.println(line);			
			//Write to file
			if( mOutput!=null ){
				try {
					mOutput.write(line);
					mOutput.write("\n");					
					if(line.contains(">")){
						//this is the end
						mOutput.flush();
						mOutput.close();
						mOutput = null;
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
		}
	} ;
	
	/*private BluetoothDevice getDevice(String name){
		Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
		// If there are paired devices
		if (pairedDevices.size() > 0) {
		    // Loop through paired devices
		    for (BluetoothDevice device : pairedDevices) {
		    	if( device.getName().equals(name)){
		    		return device;
		    	}
		    }
		}
		return null;
	}*/
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.start_menu);
		onStopLoading();
		
		BthDeviceManager manager = new BthDeviceManager();
		
		if (!manager.isEnabled()) {
		    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
		    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
		}
		
		mConnectButton = (ImageButton)findViewById(R.id.ibConnect);
		mConnectButton.setOnClickListener(this);
		
		((Button)findViewById(R.id.button2)).setOnClickListener(this);;
		
		setStatus(false);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public void onClick(View v) {
		if( v.getId() == R.id.ibConnect ){			
			if(mHandler == null ){
				mHandler = new ConnHandler().init(FileManager.getNewOutputFile());
			}
			if( mConnThread !=null ){
				mConnThread.cancel();
				mConnThread = null;
			}
			if( mConnection!=null ){
				mConnection.cancel();
				mConnection = null;
				setStatus(false);
			}else{
				BthDeviceManager manager = new BthDeviceManager();
				BluetoothDevice device = manager.getGeoCar();
				//BluetoothDevice device = getDevice(BTH_DEVICE_NAME);
				if( device != null ){
					 mConnection = new ConnectThread(device, BthDeviceManager.SPP_UUID, this);
					 mConnection.start();
				}
			}
		}else if( v.getId() == R.id.button2 ){
			getDataFromGeoCar();
		}
	}

	private void getDataFromGeoCar(){		
		//Send a B to the device
		sendMessage( 'B' );
		sendMessage( 10 );
		sendMessage( 13 );
	}
	
	//this is running on a thread on the background so be careful	
	@Override
	public void onBthConnect( BluetoothSocket socket ){		
		mConnThread = new ConnectedThread( socket, mHandler );		
		mConnThread.start();
		
		postMessage(true);
	}

	private void postMessage(boolean connected ){
		Message msg = new Message();
		Bundle b = new Bundle();
	    b.putInt(STATUS, (connected?1:0));
	    msg.setData(b);
	    mHandler.sendMessage(msg);
	    
	    Message msg2 = new Message();
	    Bundle bundle = new Bundle();
	    bundle.putBoolean(STATUS, connected);
	    msg2.setData(bundle);
	    mGUIUpdate.sendMessage(msg2);
	}

	private void sendMessage(int value) {
		if( mConnThread!=null ){
			mConnThread.write( new byte[]{(byte) value} );	
		}
	}
	
	private void sendMessage(char value) {
		// TODO Auto-generated method stub
		if( mConnThread!=null ){
			mConnThread.write( new byte[]{(byte) value} );	
		}
		
	}

}
