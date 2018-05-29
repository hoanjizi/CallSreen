package com.vvhoan.callsreen.call;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.vvhoan.callsreen.R;

import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by vvhoan on 5/29/2018.
 */

public class CallActivity extends AppCompatActivity {
    private String number;
    private CompositeDisposable disposables = new CompositeDisposable();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_activity);
        if (getIntent() != null) {
            number = getIntent().getStringExtra("string");
            ((TextView) findViewById(R.id.callInfo)).setText(number);
        }
        try {
            TelephonyManager tmgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            MyPhoneStateListener PhoneListener = new MyPhoneStateListener();
            if (tmgr != null) {
                tmgr.listen(PhoneListener, PhoneStateListener.LISTEN_CALL_STATE);
            }
        } catch (Exception e) {
            Log.e("Phone Receive Error", " " + e);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        OnGoingCall.hangup();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onStart() {
        super.onStart();
        findViewById(R.id.answer).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OnGoingCall.answer();
            }
        });
        findViewById(R.id.hangup).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OnGoingCall.hangup();
                finish();
            }
        });

    }

    @Override
    protected void onStop() {
        disposables.clear();
        super.onStop();
    }

    class MyPhoneStateListener extends PhoneStateListener {
        public void onCallStateChanged(final int state, final String incomingNumber) {
            Log.e("state change=[===", String.valueOf(state));
            switch (state)
            {
                case 0:
                    CallActivity.this.finish();
                    break;
                case 1:
                    break;
                case 2:
                    (findViewById(R.id.answer)).setVisibility(View.INVISIBLE);
                    break;
            }
        }
    }
}

