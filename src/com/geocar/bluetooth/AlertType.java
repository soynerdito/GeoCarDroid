package com.geocar.bluetooth;

public enum AlertType { 
	TURN_OFF(new char[]{'T',10,13} );
	
	private char mMessage[];
	AlertType(char[] msg){
		mMessage =  msg;
	}
	
	public char[] getMsg(){
		return mMessage;
	}
	
}