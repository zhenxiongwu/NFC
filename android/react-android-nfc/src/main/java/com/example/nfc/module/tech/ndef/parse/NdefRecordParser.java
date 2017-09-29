package com.example.nfc.module.tech.ndef.parse;

import android.nfc.NdefRecord;

import com.facebook.react.bridge.WritableMap;

/**
 * Created by wuzhenxiong on 2017/9/27.
 */

public abstract class NdefRecordParser {

    /**
     * 在调用parseRecord方法前，对NdefRecord对象进行格式检查
     * @return boolean
     */
    public abstract boolean checkRecord(NdefRecord ndefRecord);

    /**
     * 当正确设置checkRecord方法后，在parseRecord方法中则不需要对NdefRecord对象进行格式检查。
     * 此方法的关注点为解释NdefRecord对象,将其信息转化为JSON格式对象
     * @return WritableMap
     */
    public abstract WritableMap parseRecord(NdefRecord ndefRecord);


    public WritableMap getRecordMap(NdefRecord ndefRecord){
        if(checkRecord(ndefRecord))
            return parseRecord(ndefRecord);
        return null;
    }
}
