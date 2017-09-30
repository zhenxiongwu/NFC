package com.example.nfc.module.tech.ndef.creat;

import android.nfc.NdefRecord;

import com.example.nfc.module.tech.ndef.NdefRecordModule;
import com.facebook.react.bridge.ReadableMap;

/**
 * Created by wuzhenxiong on 2017/9/30.
 */

public class TnfUnknownCreater extends NdefRecordCreater {

    @Override
    public boolean checkRecordMap(ReadableMap readableMap) {
        String tnf = null;
        try{
            tnf = readableMap.getString("TNF");
        }catch (Exception e){
            return false;
        }
        return tnf!=null && tnf.equals(NdefRecordModule.TNF_UNKNOWN);
    }

    @Override
    public NdefRecord createRecord(ReadableMap readableMap) {
        return new NdefRecord(NdefRecord.TNF_UNKNOWN,new byte[0],new byte[0],new byte[0]);
    }
}
