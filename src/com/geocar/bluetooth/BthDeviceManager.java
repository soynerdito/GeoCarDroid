package com.geocar.bluetooth;

import java.util.Set;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;

public class BthDeviceManager {
	private static String BTH_GEOCAR = "GeoCar";	
	public static String SPP_UUID = "00001101-0000-1000-8000-00805F9B34FB";
	// Get the default adapter
	private BluetoothAdapter mBluetoothAdapter;// = BluetoothAdapter.getDefaultAdapter();
	
	public BthDeviceManager(){
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
	}
	

	public boolean isEnabled(){
		return (mBluetoothAdapter!=null)? mBluetoothAdapter.isEnabled():false;
	}
	
	public BluetoothDevice getGeoCar(){
		return getDevice(BTH_GEOCAR);
	}
	
	
	private BluetoothDevice getDevice(String name){
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
	}
}
