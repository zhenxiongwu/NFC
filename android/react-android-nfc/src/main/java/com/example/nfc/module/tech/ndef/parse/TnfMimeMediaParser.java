package com.example.nfc.module.tech.ndef.parse;

import android.nfc.NdefRecord;

import com.example.nfc.module.tech.ndef.NdefRecordModule;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableMap;

/**
 * Created by wuzhenxiong on 2017/9/27.
 */

public class TnfMimeMediaParser extends NdefRecordParser {

    @Override
    public boolean checkRecord(NdefRecord ndefRecord) {
        return ndefRecord.getTnf()==NdefRecord.TNF_MIME_MEDIA;
    }

    @Override
    public WritableMap parseRecord(NdefRecord ndefRecord) {
        WritableMap recordMap = Arguments.createMap();
        recordMap.putString("TNF", NdefRecordModule.TNF_MIME_MEDIA);
        recordMap.putBoolean("isURI",false);
        recordMap.putString("label",ndefRecord.toMimeType());
        recordMap.putString("content",new String(ndefRecord.getPayload()));
        return recordMap;
    }
}
