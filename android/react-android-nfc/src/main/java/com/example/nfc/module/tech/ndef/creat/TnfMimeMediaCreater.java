package com.example.nfc.module.tech.ndef.creat;

import android.nfc.NdefRecord;

import com.example.nfc.module.tech.ndef.NdefRecordModule;
import com.facebook.react.bridge.ReadableMap;

import java.nio.charset.Charset;

/**
 * Created by wuzhenxiong on 2017/9/28.
 */

public class TnfMimeMediaCreater extends NdefRecordCreater {

    @Override
    public boolean checkRecordMap(ReadableMap readableMap) {
        String tnf = null;
        try {
            tnf = readableMap.getString("TNF");
        }catch (Exception e){
            return false;
        }
        return tnf != null && tnf.equals(NdefRecordModule.TNF_MIME_MEDIA);
    }

    @Override
    public NdefRecord createRecord(ReadableMap readableMap) {
        String type;
        String content;
        try{
            type = readableMap.getString("type");
        }catch (Exception e){
            type = "[type]/[subtype]";
        }
        try{
            content = readableMap.getString("content");
        }catch (Exception e){
            content = "[content]";
        }
        return NdefRecord.createMime(type,content.getBytes(Charset.forName("US-ASCII")));
    }
}
