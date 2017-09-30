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
        String tnf = null;
        String rtd = null;
        try {
            tnf = readableMap.getString("TNF");
            rtd = readableMap.getString("RTD");
        }catch (Exception e){
            return false;
        }
        return tnf != null && tnf.equals(NdefRecordModule.TNF_WELL_KNOWN)
                && rtd != null && rtd.equals(NdefRecordModule.RTD_URI);
    }

    @Override
    public NdefRecord createRecord(ReadableMap readableMap) {
        String content = null;
        try{
            content = readableMap.getString("content");
            if(content==null || content.equals(""))
                content = "[scheme]:[content]";
        }catch (Exception e){
            content = "[scheme]:[content]";
        }
        return NdefRecord.createUri(content);
    }
}
