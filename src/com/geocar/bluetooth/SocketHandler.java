package com.geocar.bluetooth;

import android.bluetooth.BluetoothSocket;

public interface SocketHandler {
	void onBthConnect( BluetoothSocket socket );    	
}