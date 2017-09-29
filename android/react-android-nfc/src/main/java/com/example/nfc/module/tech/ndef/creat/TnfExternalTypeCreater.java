package com.example.nfc.module.tech.ndef.creat;

import android.nfc.NdefRecord;

import com.example.nfc.module.tech.ndef.NdefRecordModule;
import com.facebook.react.bridge.ReadableMap;

import java.nio.charset.Charset;

/**
 * Created by wuzhenxiong on 2017/9/28.
 */

public class TnfExternalTypeCreater extends NdefRecordCreater {

    @Override
    public boolean checkRecordMap(ReadableMap readableMap) {
        String tnf = readableMap.getString("TNF");
        return tnf != null && tnf.equals(NdefRecordModule.TNF_EXTERNAL_TYPE);
    }

    @Override
    public NdefRecord createRecord(ReadableMap readableMap) {
        return NdefRecord.createExternal(readableMap.getString("domain"),
                readableMap.getString("type"),
                readableMap.getString("content").getBytes(Charset.forName("US-ASCII")));
    }
}
