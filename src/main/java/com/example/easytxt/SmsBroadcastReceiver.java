package com.example.easytxt;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.util.Log;

/**
 * A broadcast receiver who listens for incoming SMS
 */

public class SmsBroadcastReceiver extends BroadcastReceiver {

    private static final String TAG = "SmsBroadcastReceiver";

    private Listener listener;

    public SmsBroadcastReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Telephony.Sms.Intents.SMS_RECEIVED_ACTION)) {
            String smsSender = "";
            String smsBody = "";
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                for (SmsMessage smsMessage : Telephony.Sms.Intents.getMessagesFromIntent(intent)) {
                    smsSender = smsMessage.getDisplayOriginatingAddress();
                    smsBody += smsMessage.getMessageBody();
                }
            } else {
                Bundle smsBundle = intent.getExtras();
                if (smsBundle != null) {
                    Object[] pdus = (Object[]) smsBundle.get("pdus");
                    if (pdus == null) {
                        // Display some error to the user
                        Log.e(TAG, "SmsBundle had no pdus key");
                        return;
                    }
                    SmsMessage[] messages = new SmsMessage[pdus.length];
                    for (int i = 0; i < messages.length; i++) {
                        messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                        smsBody += messages[i].getMessageBody();
                    }
                    smsSender = messages[0].getOriginatingAddress();
                }
            }

            if (listener != null) {
                listener.onTextReceived(smsSender);
            }
        }
    }

    void setListener(Listener listener) {
        this.listener = listener;
    }

    interface Listener {
        void onTextReceived(String number);
    }
}
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.Intent;
//import android.telephony.SmsManager;
//import android.widget.Toast;
//
//
//public class SmsListener extends BroadcastReceiver {
//
//
//    @Override
//    public void onReceive(Context context, Intent intent) {
//        if(intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")) {
//
//            Toast.makeText(context, "SMS Received", Toast.LENGTH_SHORT).show();
//
//            SmsManager.getDefault().sendTextMessage(
//                    "+447787279203", null,
//                    "Hello, my name is EasyTXT. I'm an app that's currently under development" +
//                    " " + "and being tested.",
//                    null, null);
//        }
//    }
//
//
//}
