package com.geocar.geocarconnect;

import android.bluetooth.BluetoothDevice;
import android.content.Context;

import com.geocar.bluetooth.AlertType;
import com.geocar.bluetooth.BthDeviceManager;
import com.geocar.bluetooth.GeoSendDataThread;

public class GeoCarAlert {
	private Context mAppContext;
	
	public GeoCarAlert(Context context){
		mAppContext = context;
	}
	
	GeoSendDataThread mconn;
	public void onMessageReceived(String from, AlertType alert ){
		BthDeviceManager manager = new BthDeviceManager();
		BluetoothDevice device = manager.getGeoCar();
		//BluetoothDevice device = getDevice(BTH_DEVICE_NAME);
		if( device != null ){
			mconn = new GeoSendDataThread(device, BthDeviceManager.SPP_UUID, alert.getMsg() );
			mconn.start();			
		}
	}
}
