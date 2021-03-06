package com.example.nfc.module.tech.ndef.creat;

import android.content.Context;
import android.nfc.NdefRecord;

import com.example.nfc.module.tech.ndef.NdefRecordModule;
import com.example.nfc.module.tech.ndef.text.TextEncoder;
import com.facebook.react.bridge.ReadableMap;

/**
 * Created by wuzhenxiong on 2017/9/28.
 */

public class RtdTextCreater extends NdefRecordCreater {

    private Context mContext;

    public RtdTextCreater(Context context) {
        mContext = context;
    }

    @Override
    public boolean checkRecordMap(ReadableMap readableMap) {
        String tnf = null;
        String rtd = null;
        try {
            tnf = readableMap.getString("TNF");
            rtd = readableMap.getString("RTD");
        } catch (Exception e) {
            return false;
        }
        return tnf != null && tnf.equals(NdefRecordModule.TNF_WELL_KNOWN)
                && rtd != null && rtd.equals(NdefRecordModule.RTD_TEXT);
    }

    @Override
    public NdefRecord createRecord(ReadableMap readableMap) {
        String content;
        try{
            content = readableMap.getString("content");
        }catch (Exception e){
            content = "";
        }
        return createRecord(content);
    }

    private NdefRecord createRecord(String text) {
        if (text == null) text = "";
        TextEncoder textEncoder = new TextEncoder(mContext, text, true);
        NdefRecord ndefRecord = new NdefRecord(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_TEXT, new byte[0], textEncoder.getBytes());
        return ndefRecord;
    }
}
