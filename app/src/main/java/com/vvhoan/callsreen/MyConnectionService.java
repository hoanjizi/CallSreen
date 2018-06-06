package com.vvhoan.callsreen;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.PixelFormat;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.os.PowerManager;
import android.provider.ContactsContract;
import android.telecom.Call;
import android.telecom.InCallService;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.vvhoan.callsreen.call.CallActivity;
import com.vvhoan.callsreen.call.OnGoingCall;

/**
 * Created by vvhoan on 5/28/2018.
 */

@SuppressWarnings("deprecation")
@SuppressLint("NewApi")
public class MyConnectionService extends InCallService {
    private final String TAG = MyConnectionService.class.getSimpleName();
    private Cursor cursor;
    private View mView;
    private WindowManager manager;
    private PowerManager pm;
    private String number;
    private CountDownTimer time;
    private int tam = 0;

    @Override
    public void onCallAdded(Call call) {
        OnGoingCall.call = call;
        number = call.getDetails().getHandle().getSchemeSpecificPart();
        createUI(number, call.getState());
 /*       Intent intent = new Intent(getBaseContext(), MyService.class);
        phoneNumber = call.getDetails().getHandle().toString();
        intent.setData(call.getDetails().getHandle());
        //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        startService(intent);*/
    }

    @Override
    public void onCallRemoved(Call call) {
        OnGoingCall.call = call;
        if (manager != null) {
            manager.removeView(mView);
            mView = null;
            manager = null;
            cursor = null;
            time.cancel();
            tam = 0;
        }
        pm = null;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private Cursor getCusor() {
        return getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                new String[]{ContactsContract.CommonDataKinds.Phone._ID, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER}, null, null, null);
    }

    @SuppressLint({"RtlHardcoded", "InflateParams"})
    private void createUI(String number, int state) {
        mView = LayoutInflater.from(this).inflate(R.layout.activity_call_activity, null);
        manager = (WindowManager) getSystemService(WINDOW_SERVICE);
        final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT
        );
        DisplayMetrics displayMetrics = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        params.gravity = height / 2 | Gravity.CENTER;

        time = new CountDownTimer(8000*60*60, 1000) {

            public void onTick(long millisUntilFinished) {
                if(mView!=null)
                ((TextView) mView.findViewById(R.id.time)).setText(""+(tam = tam + 1000) / 1000);
            }

            public void onFinish() {

            }
        };

        if (mView != null && state == Call.STATE_CONNECTING)
            (mView.findViewById(R.id.answer)).setVisibility(View.INVISIBLE);
        if (manager != null) {
            manager.addView(mView, params);
        }
        light();
        updateUI(number);
    }

    private void updateUI(final String number) {
        ((TextView) mView.findViewById(R.id.callInfo)).setText(number);
        mView.findViewById(R.id.answer).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OnGoingCall.answer();
                mView.findViewById(R.id.answer).setVisibility(View.INVISIBLE);
            }
        });
        mView.findViewById(R.id.hangup).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OnGoingCall.hangup();
                time.cancel();
                tam = 0;
                manager.removeView(mView);
                mView = null;
                manager = null;
                cursor = null;
            }
        });
        cursor = getCusor();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                if (cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)).trim().equals(number.trim())) {
                    String e = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                    ((TextView) mView.findViewById(R.id.contact)).setText(e);
                }
            }
        }
    }

    private void light() {
        pm = (PowerManager) getBaseContext().getSystemService(Context.POWER_SERVICE);
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

    }
    class MyPhoneStateListener extends PhoneStateListener {
        public void onCallStateChanged(final int state, final String incomingNumber) {
            Log.e(TAG, "onCallStateChanged: "+state );
            if(state == 2&&time!=null)
            {
                time.start();
            }
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
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
}
