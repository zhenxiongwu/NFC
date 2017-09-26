package com.example.nfc_library.module.tech.ndef.text;

import android.content.Context;

import java.nio.charset.Charset;
import java.util.Locale;

/**
 * Created by wuzhenxiong on 2017/9/13.
 */

public class TextEncoder {

    private Context mContext;
    private String mText;
    private boolean mEncodeInUtf8;
    private Charset mCharset;

    /**
     * If encodeInUtf8 is true, the text is encoded in UTF-8, else in UTF-16
     * @param context
     * @param text
     * @param encodeInUtf8
     */
    public TextEncoder(Context context, String text, boolean encodeInUtf8){
        mContext = context;
        mText = text;
        mEncodeInUtf8 = encodeInUtf8;
        if(mEncodeInUtf8){
            mCharset = Charset.forName("UTF-8");
        }else{
            mCharset = Charset.forName("UTF-16");
        }
    }

    public byte[] getBytes(){
        Locale locale = mContext.getResources().getConfiguration().locale;
        byte[] langBytes = locale.getLanguage().getBytes(Charset.forName("US-ASCII"));
        byte[] textBytes = mText.getBytes(mCharset);
        int utfBit = mEncodeInUtf8? 0:(1<<7);
        byte[] data = new byte[1+langBytes.length+textBytes.length];
        data[0] = (byte)(utfBit + langBytes.length);
        System.arraycopy(langBytes,0,data,1,langBytes.length);
        System.arraycopy(textBytes,0,data,1+langBytes.length,textBytes.length);
        return data;
    }

    public Charset getCharset(){
        return mCharset;
    }

}
