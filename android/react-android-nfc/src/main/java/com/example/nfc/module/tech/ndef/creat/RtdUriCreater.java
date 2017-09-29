package com.example.nfc.module.tech.ndef.creat;

import android.nfc.NdefRecord;

import com.example.nfc.module.tech.ndef.NdefRecordModule;
import com.facebook.react.bridge.ReadableMap;

/**
 * Created by wuzhenxiong on 2017/9/28.
 */

public class RtdUriCreater extends NdefRecordCreater {
    @Override
    public boolean checkRecordMap(ReadableMap readableMap) {
        String tnf = readableMap.getString("TNF");
        String rtd = readableMap.getString("RTD");
        return tnf != null && tnf.equals(NdefRecordModule.TNF_WELL_KNOWN)
                && rtd != null && rtd.equals(NdefRecordModule.RTD_URI);
    }

    @Override
    public NdefRecord createRecord(ReadableMap readableMap) {
        return NdefRecord.createUri(readableMap.getString("content"));
    }
}
