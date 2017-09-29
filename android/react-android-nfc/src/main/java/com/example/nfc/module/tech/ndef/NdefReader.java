package com.example.nfc.module.tech.ndef;

import android.content.Context;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.tech.Ndef;

import com.example.nfc.module.tech.TagDataReader;
import com.example.nfc.module.tech.ndef.parse.NdefRecordParser;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;

/**
 * Created by wuzhenxiong on 2017/9/26.
 */

public class NdefReader implements TagDataReader {

    private Context mContext;
    private Ndef mNdef;

    public NdefReader(Context context, Ndef ndef) {
        mContext = context;
        mNdef = ndef;
    }

    @Override
    public WritableMap getTagMap() {
        if (mNdef == null) return null;
        WritableMap tagMap = Arguments.createMap();
        tagMap.putString("type", mNdef.getType());
        tagMap.putBoolean("isWritable", mNdef.isWritable());
        tagMap.putBoolean("canMakeReadOnly", mNdef.canMakeReadOnly());
        tagMap.putInt("maxSize", mNdef.getMaxSize());
        NdefMessage ndefMessage = mNdef.getCachedNdefMessage();
        tagMap.putInt("UsedSize", ndefMessage.toByteArray().length);
        tagMap.putArray("records", parseMessage(ndefMessage));
        return tagMap;
    }

    private WritableArray parseMessage(NdefMessage ndefMessage) {
        if (ndefMessage == null) return null;
        WritableArray records = Arguments.createArray();
        NdefRecord[] ndefRecords = ndefMessage.getRecords();
        if (ndefRecords != null) {
            for (NdefRecord ndefRecord : ndefRecords) {
                records.pushMap(parseRecord(ndefRecord));
            }
        }
        return records;
    }

    private WritableMap parseRecord(NdefRecord ndefRecord) {
        NdefFormatManager ndefFormatManager = NdefFormatManager.getInstance(mContext);
        NdefRecordParser parser = ndefFormatManager.getBestParser(ndefRecord);
        if (parser == null)
            return null;
        return parser.parseRecord(ndefRecord);
    }


}
