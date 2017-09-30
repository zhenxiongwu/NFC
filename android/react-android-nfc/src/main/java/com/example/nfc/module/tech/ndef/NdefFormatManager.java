package com.example.nfc.module.tech.ndef;

import android.content.Context;
import android.nfc.NdefRecord;

import com.example.nfc.module.tech.ndef.creat.NdefRecordCreater;
import com.example.nfc.module.tech.ndef.creat.RtdTextCreater;
import com.example.nfc.module.tech.ndef.creat.RtdUriCreater;
import com.example.nfc.module.tech.ndef.creat.TnfExternalTypeCreater;
import com.example.nfc.module.tech.ndef.creat.TnfMimeMediaCreater;
import com.example.nfc.module.tech.ndef.parse.NdefRecordParser;
import com.example.nfc.module.tech.ndef.parse.RtdTextParser;
import com.example.nfc.module.tech.ndef.parse.RtdUriParser;
import com.example.nfc.module.tech.ndef.parse.TnfAbsoluteUriParser;
import com.example.nfc.module.tech.ndef.parse.TnfExternalTypeParser;
import com.example.nfc.module.tech.ndef.parse.TnfMimeMediaParser;
import com.facebook.react.bridge.ReadableMap;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wuzhenxiong on 2017/9/27.
 */

public class NdefFormatManager {

    private static NdefFormatManager sNdefFromatManager;

    public static NdefFormatManager getInstance(Context context) {
        if (sNdefFromatManager == null) sNdefFromatManager = new NdefFormatManager(context);
        return sNdefFromatManager;
    }

    private Context mContext;
    private List<NdefRecordParser> mParsers;
    private List<NdefRecordCreater> mCreaters;

    private NdefFormatManager(Context context) {
        mContext = context;
        mParsers = new ArrayList<>();
        mParsers.add(new RtdTextParser(mContext));
        mParsers.add(new RtdUriParser());
        mParsers.add(new TnfAbsoluteUriParser());
        mParsers.add(new TnfExternalTypeParser());
        mParsers.add(new TnfMimeMediaParser());

        mCreaters = new ArrayList<>();
        mCreaters.add(new RtdTextCreater(mContext));
        mCreaters.add(new RtdUriCreater());
        mCreaters.add(new TnfExternalTypeCreater(mContext));
        mCreaters.add(new TnfMimeMediaCreater());
    }

    public NdefRecordParser getBestParser(NdefRecord ndefRecord) {
        NdefRecordParser bestParser = null;
        for (NdefRecordParser parser : mParsers) {
            if (parser.checkRecord(ndefRecord)) {
                bestParser = parser;
            }
        }
        return bestParser;
    }

    public void addParser(NdefRecordParser parser) {
        if (parser != null) {
            mParsers.add(parser);
        }
    }

    public void removeParser(NdefRecordParser parser) {
        mParsers.remove(parser);
    }


    public NdefRecordCreater getBestCreater(ReadableMap recordMap){
        NdefRecordCreater bestCreater = null;
        for(NdefRecordCreater creater: mCreaters){
            if(creater.checkRecordMap(recordMap)){
                bestCreater = creater;
            }
        }
        return bestCreater;
    }

    public void addCreater(NdefRecordCreater creater){
        if(creater!=null)
            mCreaters.add(creater);
    }

    public void removeCreater(NdefRecordCreater creater){
        mCreaters.remove(creater);
    }

    public Context getmContext() {
        return mContext;
    }

}
