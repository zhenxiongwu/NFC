package com.example.nfc.module.tech.ndef.creat;

import android.content.Context;
import android.nfc.NdefRecord;

import com.example.nfc.module.tech.ndef.NdefRecordModule;
import com.facebook.react.bridge.ReadableMap;

import java.nio.charset.Charset;

/**
 * Created by wuzhenxiong on 2017/9/28.
 */

public class TnfExternalTypeCreater extends NdefRecordCreater {

    private Context mContext;

    public TnfExternalTypeCreater(Context context) {
        mContext = context;
    }

    @Override
    public boolean checkRecordMap(ReadableMap readableMap) {
        String tnf = null;
        try {
            tnf = readableMap.getString("TNF");
        } catch (Exception e) {
            return false;
        }
        return tnf != null && tnf.equals(NdefRecordModule.TNF_EXTERNAL_TYPE);
    }

    @Override
    public NdefRecord createRecord(ReadableMap readableMap) {
        String domain;
        String type;
        String content;
        boolean isTypeNull = false;
        try {
            domain = readableMap.getString("domain");
        } catch (Exception e) {
            domain = "[domain]";
        }
        try {
            type = readableMap.getString("type");
        } catch (Exception e) {
            type = "[type]";
            isTypeNull = true;
        }
        try {
            content = readableMap.getString("content");
        } catch (Exception e) {
            if (isTypeNull)
                content = "[content]";
            else
                content = mContext.getPackageName();
        }
        return NdefRecord.createExternal(domain, type, content.getBytes(Charset.forName("US-ASCII")));
    }
}
