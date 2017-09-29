package com.example.nfc.module.tech.ndef.parse;

import android.net.Uri;
import android.nfc.NdefRecord;

import com.example.nfc.module.tech.ndef.NdefRecordModule;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableMap;

import java.util.Arrays;

/**
 * Created by wuzhenxiong on 2017/9/27.
 */

public class RtdUriParser extends NdefRecordParser {

    @Override
    public boolean checkRecord(NdefRecord ndefRecord) {
        return ndefRecord.getTnf()==NdefRecord.TNF_WELL_KNOWN && Arrays.equals(ndefRecord.getType(),NdefRecord.RTD_URI);
    }

    @Override
    public WritableMap parseRecord(NdefRecord ndefRecord) {
        Uri uri = ndefRecord.toUri();
        WritableMap recordMap = Arguments.createMap();
        recordMap.putString("TNF", NdefRecordModule.TNF_WELL_KNOWN);
        recordMap.putString("RCT",NdefRecordModule.RTD_URI);
        recordMap.putBoolean("isURI",true);
        recordMap.putString("URI",uri.toString());
        recordMap.putString("label",uri.getScheme());
        recordMap.putString("content",uri.toString());
        return recordMap;
    }


}
