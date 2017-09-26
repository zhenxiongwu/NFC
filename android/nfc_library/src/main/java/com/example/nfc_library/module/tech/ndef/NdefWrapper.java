package com.example.nfc_library.module.tech.ndef;

import android.content.Context;
import android.net.Uri;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.widget.Toast;

import com.example.nfc_library.module.tech.TagParser;
import com.example.nfc_library.module.tech.ndef.text.TextDecoder;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;

import java.nio.charset.Charset;
import java.util.Arrays;

/**
 * Created by wuzhenxiong on 2017/9/26.
 */

public class NdefWrapper implements TagParser {

    private Context mContext;

    private Tag mTag;
    private Ndef mNdef;

    public NdefWrapper(Context context, Tag tag) {
        mContext = context;
        mTag = tag;
        mNdef = Ndef.get(mTag);
    }

    @Override
    public WritableMap getTagMap() {
        if (mNdef == null) return null;
        WritableMap tagMap = Arguments.createMap();
        tagMap.putString("type", mNdef.getType());
        tagMap.putBoolean("isWritable", mNdef.isWritable());
        tagMap.putBoolean("canMakeReadOnly", mNdef.canMakeReadOnly());
        tagMap.putArray("records", getRecords());
        return tagMap;
    }

    private WritableArray getRecords() {

        WritableArray records = Arguments.createArray();

        NdefMessage ndefMessage = mNdef.getCachedNdefMessage();

        NdefRecord[] ndefRecords = null;
        if (ndefMessage == null) {
            Toast.makeText(mContext, "ndefMessage is null", Toast.LENGTH_SHORT).show();
            return records;
        }
        ndefRecords = ndefMessage.getRecords();

        if (ndefRecords != null) {
            for (NdefRecord ndefRecord : ndefRecords) {
                records.pushMap(parseRecord(ndefRecord));
            }
        }

        return records;
    }

    private WritableMap parseRecord(NdefRecord ndefRecord) {
        WritableMap recordMap = Arguments.createMap();
        /*short tnf = ndefRecord.getTnf();
        recordMap.putString("TNF",getTNF(tnf));*/
        Uri uri = ndefRecord.toUri();
        if (uri != null) {
            recordMap.putBoolean("isURI", true);
            recordMap.putString("URI", uri.toString());
        } else {
            recordMap.putBoolean("isURI", false);
            recordMap.putString("type", ndefRecord.toMimeType());
            recordMap.putString("payload", parsePayload(ndefRecord));
        }
        return recordMap;
    }

    private String parsePayload(NdefRecord ndefRecord) {
        short tnf = ndefRecord.getTnf();
        if (tnf == NdefRecord.TNF_WELL_KNOWN) {
            byte[] type = ndefRecord.getType();
            if (Arrays.equals(type, NdefRecord.RTD_TEXT)) {
                TextDecoder textDecoder = new TextDecoder(mContext, ndefRecord.getPayload());
                return textDecoder.getDisplayText();
            }
        }
        return new String(ndefRecord.getPayload(), Charset.forName("US-ASCII"));

    }

    public static String getTNF(short tnf_byte) {
        switch (tnf_byte) {
            case NdefRecord.TNF_EMPTY:
                return "TNF_EMPTY";
            case NdefRecord.TNF_WELL_KNOWN:
                return "TNF_WELL_KNOWN";
            case NdefRecord.TNF_MIME_MEDIA:
                return "TNF_MIME_MEDIA";
            case NdefRecord.TNF_ABSOLUTE_URI:
                return "TNF_ABSOLUTE_URI";
            case NdefRecord.TNF_EXTERNAL_TYPE:
                return "TNF_EXTERNAL_TYPE";
            case NdefRecord.TNF_UNKNOWN:
                return "TNF_UNKNOWN";
            case NdefRecord.TNF_UNCHANGED:
                return "TNF_UNCHANGED";
            default:
                return "";
        }
    }


}
