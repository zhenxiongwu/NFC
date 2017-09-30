package com.example.nfc.module.tech.ndef.parse;

import android.net.Uri;
import android.nfc.NdefRecord;

import com.example.nfc.module.tech.ndef.NdefRecordModule;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableMap;

/**
 * Created by wuzhenxiong on 2017/9/27.
 */

public class TnfAbsoluteUriParser extends NdefRecordParser {

    @Override
    public boolean checkRecord(NdefRecord ndefRecord) {
        return ndefRecord.getTnf()==NdefRecord.TNF_ABSOLUTE_URI;
    }

    @Override
    public WritableMap parseRecord(NdefRecord ndefRecord) {
        Uri uri = ndefRecord.toUri();
        WritableMap recordMap = Arguments.createMap();
        recordMap.putString("TNF", NdefRecordModule.TNF_ABSOLUTE_URI);
        recordMap.putString("id",new String(ndefRecord.getId()));
        recordMap.putBoolean("isURI",true);
        recordMap.putString("URI",uri.toString());
        recordMap.putString("label",uri.getScheme());
        recordMap.putString("content",uri.toString());

        return recordMap;
    }
}
