package com.vvhoan.callsreen;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.telecom.Call;
import android.telecom.InCallService;

import com.vvhoan.callsreen.call.CallActivity;
import com.vvhoan.callsreen.call.OnGoingCall;

/**
 * Created by vvhoan on 5/28/2018.
 */

@SuppressLint("NewApi")
public class MyConnectionService extends InCallService {
    @Override
    public void onCallAdded(Call call) {
        OnGoingCall.call = call;
        Intent intent = new Intent(getBaseContext(), CallActivity.class);
        intent.putExtra("string", call.getDetails().getHandle());
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        startActivity(intent);
    }

    @Override
    public void onCallRemoved(Call call) {
        OnGoingCall.call = call;
    }
}
