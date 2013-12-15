package com.geocar.bluetooth;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import android.bluetooth.BluetoothSocket;

public class ConnectedThread extends Thread {
    private final BluetoothSocket mmSocket;
    private final InputStream mmInStream;
    private DataInputStream mDataInputStream;
    private InputStreamReader mmInStreamReader;
    private BufferedReader mBufferedReader;
    private final OutputStream mmOutStream;
    private ConnectionHandler mhandler;
    
    public final int MESSAGE_READ = 4;
 
    public ConnectedThread(BluetoothSocket socket, ConnectionHandler handler ) {
        mmSocket = socket;
        InputStream tmpIn = null;
        OutputStream tmpOut = null; 
        mhandler = handler;
        
        // Get the input and output streams, using temp objects because
        // member streams are final
        try {
            tmpIn = socket.getInputStream();
            tmpOut = socket.getOutputStream();
        } catch (IOException e) { }
        
        mmInStream = tmpIn;
        mmOutStream = tmpOut;
        if( mmInStream !=null ){
        	try {
        		mDataInputStream = new DataInputStream(mmInStream);
        		mmInStreamReader = new InputStreamReader( mDataInputStream, "UTF-8" );
				mBufferedReader = new BufferedReader( mmInStreamReader );
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
				mBufferedReader = null;
				mmInStreamReader = null;
				mDataInputStream = null;
			}
        } 
    }
 
    private void mySleep(long mil ){
    	try {
			Thread.sleep(mil);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    public void run() {
    	byte[] buffer = new byte[128];  // buffer store for the stream
        int bytes; // bytes returned from read()
        int endOfLineIndex;
        StringBuilder sb = new StringBuilder();
        // Keep listening to the InputStream until an exception occurs
        while (true) {
            try {
            	if( mmInStream.available() >0 ){ 
	            	bytes = mmInStream.read(buffer);
	                byte[] readBuf = (byte[]) buffer;
	                String strIncom = new String(readBuf, 0, bytes, "UTF-8");                 // create string from bytes array
	                //Remove null characters that get filter (maybe because of some delays)
	                strIncom = strIncom.replace(String.valueOf(((char)0)), "" );
	                sb.append(strIncom);// append string	                
	                endOfLineIndex = sb.indexOf("\n");	// determine the end-of-line
	                if (endOfLineIndex > 0){
	                    // add the current string to EOL to a local string
	                    String sbprint = sb.substring(0, endOfLineIndex);
	                    mhandler.onDataReceived( sbprint );
	                    sb = new StringBuilder( sb.substring(endOfLineIndex+1) );
	                }
            	}
            }catch (IOException e) {
                break;
            }
        }
        if( mhandler!=null ){
        	mhandler.onDisconnect();	
        }        
    }
 
    /* Call this from the main activity to send data to the remote device */
    public void write(byte[] bytes) {
        try {
            mmOutStream.write(bytes);
        } catch (IOException e) { }
    }
 
    /* Call this from the main activity to shutdown the connection */
    public void cancel() {
        try {
            mmSocket.close();
        } catch (IOException e) { }
    }
}
