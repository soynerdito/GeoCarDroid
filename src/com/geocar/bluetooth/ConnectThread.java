package com.geocar.bluetooth;

import java.io.IOException;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

public class ConnectThread extends Thread {
    private BluetoothSocket mmSocket;
    private BluetoothDevice mmDevice;
    private SocketHandler mHandler;
    
    public ConnectThread(BluetoothDevice device, String uuid, SocketHandler handler ) {
        // Use a temporary object that is later assigned to mmSocket,
        // because mmSocket is final
        //BluetoothSocket tmp = null;
        mmDevice = device;
        mHandler = handler;
        // Get a BluetoothSocket to connect with the given BluetoothDevice
        try {
            // 
        	mmSocket = mmDevice.createRfcommSocketToServiceRecord(UUID.fromString(uuid));
        } catch (IOException e) {
        	mmSocket = null;
        }
    }
 
    public void run() {
    	BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        // Cancel discovery because it will slow down the connection
    	adapter.cancelDiscovery();
 
        try {
            // Connect the device through the socket. This will block
            // until it succeeds or throws an exception
            mmSocket.connect();
            
            onConnected(mmSocket);
        } catch (IOException connectException) {
            // Unable to connect; close the socket and get out
            try {
                mmSocket.close();
                mmSocket = null;
            } catch (IOException closeException) { }
            return;
        }
 
       
    }
 
    /** Will cancel an in-progress connection, and close the socket */
    public void cancel() {
        try {
            mmSocket.close();
            mmSocket = null;
        } catch (IOException e) { }
    }
    
    protected void onConnected(BluetoothSocket socket){
    	 // Do work to manage the connection (in a separate thread)
        mHandler.onBthConnect(socket);
    }
}
