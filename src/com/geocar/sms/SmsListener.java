package com.geocar.sms;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;

import com.geocar.bluetooth.AlertType;
import com.geocar.geocarconnect.GeoCarAlert;
import com.geocar.geocarconnect.Settings;

public class SmsListener extends BroadcastReceiver{

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
    	if(!Settings.enableSMSSecurity(context)){
    		return;
    	}
        if(intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")){
            Bundle bundle = intent.getExtras();           //---get the SMS message passed in---
            SmsMessage[] msgs = null;
            String msgFrom;
            if (bundle != null){
                //---retrieve the SMS message received---
                try{                	
                	GeoCarAlert alertHandler = new GeoCarAlert(context);
                    Object[] pdus = (Object[]) bundle.get("pdus");
                    msgs = new SmsMessage[pdus.length];
                    for(int i=0; i<msgs.length; i++){
                        msgs[i] = SmsMessage.createFromPdu((byte[])pdus[i]);
                        msgFrom = msgs[i].getOriginatingAddress();
                        String msgBody = msgs[i].getMessageBody();
                        if( msgBody.equals(Settings.getSMSSecurityCode(context)) ){
                        	//Trigger Alarm
                        	alertHandler.onMessageReceived(msgFrom, AlertType.TURN_OFF);
                        }
                        
                    }
                }catch(Exception e){
                	//Log.d("Exception caught",e.getMessage());
                }
            }
        }
    }
}
