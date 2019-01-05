package com.twtech.fleetviewapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;

/**
 * Created by twtech on 3/8/18.
 */

public class IncommingSMS extends BroadcastReceiver {
    final SmsManager sms = SmsManager.getDefault();
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("OnRecieve Called","***");
        final Bundle bundle = intent.getExtras();
        try {
            if (bundle != null) {
                final Object[] pdusObj = (Object[]) bundle.get("pdus");
                for (int i = 0; i < pdusObj.length; i++) {
                    SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
                    String phoneNumber = currentMessage.getDisplayOriginatingAddress();
                    Log.e("phone Number"," : "+phoneNumber);
                    String senderNum = phoneNumber;
                    String message = currentMessage.getDisplayMessageBody().split(":")[i];
                    Log.e("Message "," : "+message);
                    message = message.substring(0, message.length()-99);
                    Log.e("SmsReceiver", "senderNum: " + senderNum + "; message: " +","+ message+",");
                    new MyLogger().storeMassage(senderNum,message);
                    Intent myIntent = new Intent("otp");

                    myIntent.putExtra("message",message);
                    new MyLogger().storeMassage(message,"");
                    LocalBroadcastManager.getInstance(context).sendBroadcast(myIntent);
                } // end for loop
            } // bundle is null
        } catch (Exception e) {
            Log.e("SmsReceiver", "Exception smsReceiver" +e);
        }
    }
}
