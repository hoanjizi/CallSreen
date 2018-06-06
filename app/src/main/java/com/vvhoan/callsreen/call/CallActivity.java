package com.vvhoan.callsreen.call;

import android.content.Context;
import android.database.Cursor;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.telecom.Call;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.vvhoan.callsreen.R;

/**
 * Created by vvhoan on 5/29/2018.
 */

@SuppressWarnings("ALL")
public class CallActivity extends AppCompatActivity {
    private static final String TAG = "acb" ;
    private String number;
    private Cursor cursor;
    private View mView;
    private WindowManager manager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mView = LayoutInflater.from(this).inflate(R.layout.activity_call_activity,null);
        final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT
        );
        params.gravity = Gravity.TOP | Gravity.LEFT;
        manager = (WindowManager) getSystemService(WINDOW_SERVICE);
        manager.addView(mView,params);



        PowerManager pm = (PowerManager) getBaseContext().getSystemService(Context.POWER_SERVICE);
        boolean isScreenOn = false;
        if (pm != null) {
            isScreenOn = pm.isScreenOn();
        }
        if (!isScreenOn) {
            PowerManager.WakeLock wl = null;
            if (pm != null) {
                wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.ON_AFTER_RELEASE, "MyLock");
            }
            if (wl != null) {
                wl.acquire(10000);
            }
            PowerManager.WakeLock wl_cpu = null;
            if (pm != null) {
                wl_cpu = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MyCpuLock");
            }

            if (wl_cpu != null) {
                wl_cpu.acquire(10000);
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
        if (getIntent() != null) {
            number = getIntent().getData().getSchemeSpecificPart();
            ((TextView) mView.findViewById(R.id.callInfo)).setText(number);
        }
        cursor = getCusor();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                if (cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)).trim().equals(number.trim())) {
                    String e = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                }
            }
        }
        try {
            TelephonyManager tamar = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            MyPhoneStateListener PhoneListener = new MyPhoneStateListener();
            if (tamar != null) {
                tamar.listen(PhoneListener, PhoneStateListener.LISTEN_CALL_STATE);
            }
        } catch (Exception e) {
            Log.e("Phone Receive Error", " " + e);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cursor != null) {
            cursor.close();
        }
        OnGoingCall.hangup();
        OnGoingCall.call = null;
        manager.removeView(mView);
        manager = null;
        mView = null;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onStart() {
        super.onStart();
        mView.findViewById(R.id.answer).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OnGoingCall.answer();
            }
        });
        mView.findViewById(R.id.hangup).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OnGoingCall.hangup();
                finishAndRemoveTask();
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    class MyPhoneStateListener extends PhoneStateListener {
        public void onCallStateChanged(final int state, final String incomingNumber) {
            switch (state) {
                case 0:
                    if (OnGoingCall.call != null) {
                        if (OnGoingCall.call.getState() == Call.STATE_RINGING) {
                            CallActivity.this.finishAndRemoveTask();
                        }
                        if (OnGoingCall.call.getState() == Call.STATE_DISCONNECTED) {
                            CallActivity.this.finishAndRemoveTask();
                        }
                    }
                    break;
                case 1:
                    break;
                case 2:
                    (mView.findViewById(R.id.answer)).setVisibility(View.INVISIBLE);
                    break;
            }
        }
    }

    private Cursor getCusor() {
        return getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                new String[]{ContactsContract.CommonDataKinds.Phone._ID, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER}, null, null, null);
    }
}

