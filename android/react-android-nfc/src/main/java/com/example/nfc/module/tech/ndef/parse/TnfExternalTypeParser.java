package com.example.nfc.module.tech.ndef.parse;

import android.net.Uri;
import android.nfc.NdefRecord;

import com.example.nfc.module.tech.ndef.NdefRecordModule;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableMap;

/**
 * Created by wuzhenxiong on 2017/9/27.
 */

public class TnfExternalTypeParser extends NdefRecordParser {

    @Override
    public boolean checkRecord(NdefRecord ndefRecord) {
        return ndefRecord.getTnf() == NdefRecord.TNF_EXTERNAL_TYPE;
    }

    @Override
    public WritableMap parseRecord(NdefRecord ndefRecord) {
        Uri uri = ndefRecord.toUri();
        WritableMap recordMap = Arguments.createMap();
        recordMap.putString("TNF", NdefRecordModule.TNF_EXTERNAL_TYPE);
        recordMap.putString("id",new String(ndefRecord.getId()));
        recordMap.putBoolean("isURI", true);
        recordMap.putString("URI", uri.toString());
        String type = uri.getPath();
        if(type.startsWith("/")){
            type=type.substring(1);
        }
        if (type.equals("android.com:pkg")) {
            type = "ARR";
        }else{
            type="URN:("+type+")";
        }
        recordMap.putString("label", type);
        recordMap.putString("content", new String(ndefRecord.getPayload()));
        return recordMap;
    }
}
