package com.example.nfc.module.tech.ndef.parse;

import android.content.Context;
import android.nfc.NdefRecord;

import com.example.nfc.module.tech.ndef.NdefRecordModule;
import com.example.nfc.module.tech.ndef.text.TextDecoder;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableMap;

import java.util.Arrays;

/**
 * Created by wuzhenxiong on 2017/9/27.
 */

public class RtdTextParser extends NdefRecordParser{

    private Context mContext;

    public RtdTextParser(Context context) {
        mContext = context;
    }

    @Override
    public boolean checkRecord(NdefRecord ndefRecord) {
        return ndefRecord.getTnf() == NdefRecord.TNF_WELL_KNOWN && Arrays.equals(ndefRecord.getType(),NdefRecord.RTD_TEXT);
    }

    @Override
    public WritableMap parseRecord(NdefRecord ndefRecord) {
        WritableMap recordMap = Arguments.createMap();
        recordMap.putString("TNF", NdefRecordModule.TNF_WELL_KNOWN);
        recordMap.putString("RTD",NdefRecordModule.RTD_TEXT);
        recordMap.putString("id",new String(ndefRecord.getId()));
        recordMap.putBoolean("isURI", false);
        TextDecoder textDecoder = new TextDecoder(mContext, ndefRecord.getPayload());
        recordMap.putString("label", textDecoder.getCharset().displayName()+"("+
                textDecoder.getEncodeLang()+"):"+ndefRecord.toMimeType());
        recordMap.putString("content",textDecoder.getDisplayText());
        return recordMap;
    }
}
