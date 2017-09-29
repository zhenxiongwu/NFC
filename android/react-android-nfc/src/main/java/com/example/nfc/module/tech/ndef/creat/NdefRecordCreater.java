package com.example.nfc.module.tech.ndef.creat;

import android.nfc.NdefRecord;

import com.facebook.react.bridge.ReadableMap;

/**
 * Created by wuzhenxiong on 2017/9/28.
 */

public abstract class NdefRecordCreater {

    public abstract boolean checkRecordMap(ReadableMap readableMap);

    public abstract NdefRecord createRecord(ReadableMap readableMap);
}
