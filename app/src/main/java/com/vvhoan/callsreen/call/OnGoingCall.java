package com.vvhoan.callsreen.call;

import android.os.Build;
import android.support.annotation.RequiresApi;
import android.telecom.Call;
import android.telecom.VideoProfile;

/**
 * Created by vvhoan on 5/29/2018.
 */
@RequiresApi(api = Build.VERSION_CODES.M)
public class OnGoingCall {
    public static Call call;

    static Call getCall() {
        return call;
    }

    public static void setCall(Call call) {
        if (OnGoingCall.call != null)
            OnGoingCall.call = call;
    }

    public static void answer() {
        if (call != null) {
            call.answer(VideoProfile.STATE_AUDIO_ONLY);
        }
    }

    public static void hangup() {
        if (call != null) {
            call.disconnect();
        }
    }
}
