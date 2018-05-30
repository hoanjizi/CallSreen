package com.vvhoan.callsreen.call;

import android.os.Build;
import android.support.annotation.RequiresApi;
import android.telecom.Call;
import android.telecom.VideoProfile;

import java.util.Timer;

import io.reactivex.subjects.BehaviorSubject;
import timber.log.Timber;

/**
 * Created by vvhoan on 5/29/2018.
 */
@RequiresApi(api = Build.VERSION_CODES.M)
public class OnGoingCall {
    public static BehaviorSubject<Integer> myState = BehaviorSubject.create() ;
    public static Call call;
    static Call.Callback callBack=new Call.Callback(){
        @Override
        public void onStateChanged(Call call, int state) {
            super.onStateChanged(call, state);
            Timber.d(call.toString());
            myState.onNext(state);
        }
    };

    public static Call getCall() {
        return call;
    }

    public static void setCall(Call call) {
        if (OnGoingCall.call !=null){
        OnGoingCall.call.unregisterCallback(callBack);
        }
        if (call!=null){
            call.registerCallback(callBack);
            myState.onNext(Integer.valueOf(call.getState()));
        }
        OnGoingCall.call =call;
    }
    public static void answer(){
        if (call!=null){
            call.answer(VideoProfile.STATE_AUDIO_ONLY);
        }
    }

    public static void hangup(){
        if (call!=null){
            call.disconnect();
        }
    }
}
