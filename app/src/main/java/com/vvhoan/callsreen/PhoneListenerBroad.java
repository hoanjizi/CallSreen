package com.vvhoan.callsreen;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.vvhoan.callsreen.call.AcceptCall;

import java.util.Objects;

/**
 * Created by vvhoan on 5/28/2018.
 */

public class PhoneListenerBroad extends BroadcastReceiver {
    Context c;

    @Override
    public void onReceive(Context context, Intent intent) {
        c = context;

        if (Objects.equals(intent.getAction(), "android.intent.action.NEW_OUTGOING_CALL")) {
            String outgoing = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
            int state = 2;
//            Intent intentPhoneCall = new Intent(c, AcceptCall.class);
//            intentPhoneCall.putExtra("incomingnumber", outgoing);
//            intentPhoneCall.putExtra("state", state);
//            intentPhoneCall.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
//            intentPhoneCall.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            c.startActivity(intentPhoneCall);
        }

        try {
            TelephonyManager tmgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            MyPhoneStateListener PhoneListener = new MyPhoneStateListener();
            if (tmgr != null) {
                tmgr.listen(PhoneListener, PhoneStateListener.LISTEN_CALL_STATE);
            }
        } catch (Exception e) {
            Log.e("Phone Receive Error", " " + e);
        }

    }

    private class MyPhoneStateListener extends PhoneStateListener {
        public void onCallStateChanged(final int state, final String incomingNumber) {
            Handler callActionHandler = new Handler();
            Runnable runRingingActivity = new Runnable() {
                @Override
                public void run() {
                    if (state == 1) {
                        Intent intentPhoneCall = new Intent(c, AcceptCall.class);
                        intentPhoneCall.putExtra("incomingnumber", incomingNumber);
                        intentPhoneCall.putExtra("state", state);
                        intentPhoneCall.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intentPhoneCall.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intentPhoneCall.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intentPhoneCall.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        c.startActivity(intentPhoneCall);
                    }
                }
            };

            if (state == 1) {
                callActionHandler.postDelayed(runRingingActivity, 100);
            }

            if (state == 0) {
                callActionHandler.removeCallbacks(runRingingActivity);
            }
        }
    }
}