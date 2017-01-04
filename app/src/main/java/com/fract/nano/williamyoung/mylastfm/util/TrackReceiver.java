package com.fract.nano.williamyoung.mylastfm.util;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.os.ResultReceiver;

@SuppressLint("ParcelCreator")
public class TrackReceiver extends ResultReceiver {
    private Receiver receiver;

    public TrackReceiver(Handler handler) { super(handler); }

    public void setReceiver(Receiver receiver) { this.receiver = receiver; }

    public interface Receiver {
        void onReceiveResult(int resultCode, Bundle resultData);
    }

    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData) {
        if (receiver != null) { receiver.onReceiveResult(resultCode, resultData); }
    }
}