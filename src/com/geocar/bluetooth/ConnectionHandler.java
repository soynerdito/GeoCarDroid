package com.geocar.bluetooth;

import android.os.Handler;
import android.os.Message;

public abstract class ConnectionHandler extends Handler{
	
	public ConnectionHandler(){
		super();
	}

	@Override
	public void handleMessage(Message msg) {
		super.handleMessage(msg);
	}
	
	protected abstract void onDisconnect();
	protected abstract void onDataReceived(String line);
	
}
