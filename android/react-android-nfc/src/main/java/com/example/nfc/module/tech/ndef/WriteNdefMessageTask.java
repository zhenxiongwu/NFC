package com.example.nfc.module.tech.ndef;

import android.content.Context;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.tech.Ndef;
import android.os.AsyncTask;

import com.example.nfc.module.NfcAdapterModule;
import com.example.nfc.module.tech.ndef.creat.NdefRecordCreater;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;

import java.io.IOException;

/**
 * Created by wuzhenxiong on 2017/9/28.
 */

public class WriteNdefMessageTask extends AsyncTask<ReadableArray, Integer, Integer> {

    private Context mContext;
    private Ndef mNdef;
    private Promise mPromise;

    public WriteNdefMessageTask(Context context,Ndef ndef, Promise promise) {
        mContext = context;
        mNdef = ndef;
        mPromise = promise;
    }

    @Override
    protected Integer doInBackground(ReadableArray... ndefMessages) {
        if (mNdef == null)
            return 0;
        try {
            mNdef.connect();
            NdefMessage ndefMessage = parseMessageMap(ndefMessages[0]);
            try{
                if(!mNdef.isWritable()){
                    return -2;
                }else if(mNdef.getMaxSize()<ndefMessage.toByteArray().length){
                    return -3;
                }
                mNdef.writeNdefMessage(ndefMessage);
            } catch (FormatException e) {
                e.printStackTrace();
                return -4;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }
        return 1;
    }

    @Override
    protected void onPostExecute(Integer b) {
        if(b==1){
            mPromise.resolve(true);
        }else if(b==0){
            mPromise.reject(NfcAdapterModule.NULL_OBJECT_EXCEPTION,"Illegal call, please check your codes");
        }else if(b==-1){
            mPromise.reject(NfcAdapterModule.IO_EXCEPTION,"Ndef connection failed");
        }else if(b==-2){
            mPromise.reject(NfcAdapterModule.READ_ONLY_EXCEPTION,"The tag is read-only");
        }else if(b==-3){
            mPromise.reject(NfcAdapterModule.OVERFLOW_EXCEPTION, "The message is too big for the tag");
        }else if(b==-4){
            mPromise.reject(NfcAdapterModule.FORMAT_EXCEPTION,"The format of the data is illegal for the tag");
        }
    }

    private NdefMessage parseMessageMap(ReadableArray message) {
        NdefRecord[] records = new NdefRecord[message.size()];
        NdefFormatManager ndefFormatManager = NdefFormatManager.getInstance(mContext);
        for(int i = 0; i<message.size(); i++){
            ReadableMap recordMap = message.getMap(i);
            NdefRecordCreater ndefRecordCreater = ndefFormatManager.getBestCreater(recordMap);
            records[i] = ndefRecordCreater.createRecord(recordMap);
        }
        return new NdefMessage(records);
    }
}
