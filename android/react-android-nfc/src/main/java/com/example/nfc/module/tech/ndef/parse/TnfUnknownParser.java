package com.example.nfc.module.tech.ndef.parse;

import android.nfc.NdefRecord;

import com.example.nfc.module.tech.ndef.NdefRecordModule;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableMap;

/**
 * Created by wuzhenxiong on 2017/9/30.
 */

public class TnfUnknownParser extends NdefRecordParser {

    @Override
    public boolean checkRecord(NdefRecord ndefRecord) {
        return ndefRecord.getTnf()==NdefRecord.TNF_UNKNOWN;
    }

    @Override
    public WritableMap parseRecord(NdefRecord ndefRecord) {
        WritableMap recordMap = Arguments.createMap();
        recordMap.putString("TNF", NdefRecordModule.TNF_UNKNOWN);
        recordMap.putString("id",new String(ndefRecord.getId()));
        recordMap.putBoolean("isURI",false);
        recordMap.putString("label","unknown type");
        recordMap.putString("content","unknown content");
        return null;
    }
}
