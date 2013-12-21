package com.geocar.geocarconnect;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public abstract class Settings {
	public static String ENABLE_SMS_SECURITY = "parent_security_preference";
	public static String SMS_SECURITY_CODE = "sms_security_code";

	public static boolean enableSMSSecurity(Context context){
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
	    return preferences.getBoolean(ENABLE_SMS_SECURITY, false);
	}

	public static String getSMSSecurityCode(Context context){
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
	    return preferences.getString( SMS_SECURITY_CODE , "");
	}
}
