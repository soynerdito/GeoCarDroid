package com.geocar.bluetooth;

import java.io.IOException;
import java.io.OutputStream;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

public class GeoSendDataThread extends ConnectThread {
	private char[] mMessages;
	
	@Override
	protected void onConnected(BluetoothSocket socket) {
		// Do work to manage the connection (in a separate thread)
		try {
			// flush out message
			OutputStream outStream = socket.getOutputStream();
			for (char msg : mMessages) {
				outStream.write(new byte[] { (byte) msg });
			}			
			sleep(200);
			outStream.flush();
			sleep(1000);
			outStream.close();
			sleep(3000);			
			outStream = null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// exception for sleep
			e.printStackTrace();
		}

		cancel();
	}

	public GeoSendDataThread(BluetoothDevice device, String uuid, char[] msgs) {
		super(device, uuid, null);
		mMessages = msgs;
		
	}

	

}
