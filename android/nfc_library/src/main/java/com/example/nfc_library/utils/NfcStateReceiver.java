package com.example.nfc_library.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wuzhenxiong on 2017/9/19.
 */

public class NfcStateReceiver extends BroadcastReceiver {

    private static NfcStateReceiver sNfcStateReceiver = null;

    public static NfcStateReceiver getDefaultReceiver() {
        if (sNfcStateReceiver == null) {
            sNfcStateReceiver = new NfcStateReceiver();
        }
        return sNfcStateReceiver;
    }

    private List<OnNfcStateChangeListener> mListeners;

    private NfcStateReceiver() {
        mListeners = new ArrayList<>();
    }

    /**
     * Add instance of OnNfcStateChangeListener as the listener on the changing of the Nfc adapter state
     *
     * @param listener
     */
    public void addListener(OnNfcStateChangeListener listener) {
        if (!mListeners.contains(listener))
            mListeners.add(listener);
    }

    public void removeListener(OnNfcStateChangeListener listener){
        if(mListeners.contains(listener)){
            mListeners.remove(listener);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        //Toast.makeText(context,""+intent.getIntExtra(NfcAdapter.EXTRA_ADAPTER_STATE,-1),Toast.LENGTH_SHORT).show();
        int state = intent.getIntExtra(NfcAdapter.EXTRA_ADAPTER_STATE, -1);
        for (OnNfcStateChangeListener listener : mListeners) {
            listener.onNfcStateChange(state);
        }
    }
}
