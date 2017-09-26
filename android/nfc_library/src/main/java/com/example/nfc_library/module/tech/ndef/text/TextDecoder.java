package com.example.nfc_library.module.tech.ndef.text;

import android.content.Context;

import java.nio.charset.Charset;

/**
 * Created by wuzhenxiong on 2017/9/13.
 */

public class TextDecoder {

    private Context mContext;
    private byte[] mCodes;
    private String mDisplayText;
    private String mExtraText;
    private String mEncodeLang;
    private Charset mCharset;

    public TextDecoder(Context context, byte[] codes){
        mContext = context;
        mCodes = codes;
    }

    public String getDisplayText(){
        if(mDisplayText!=null) return mDisplayText;
        Charset charset = getCharset();
        int index = getDisplayTextIndex();
        mDisplayText = new String(mCodes,index, mCodes.length-index,getCharset());
        return mDisplayText;
    }

    public String getExtraText(){
        if(mExtraText!=null) return mExtraText;
        mExtraText = new String(mCodes);
        return mExtraText;
    }

    public String getEncodeLang(){
        if(mEncodeLang!= null) return mEncodeLang;
        int firstCode = mCodes[0];
        int lang_length = firstCode & ~(1<<7);
        mEncodeLang = new String(mCodes,1,lang_length, Charset.forName("US-ASCII"));
        return mEncodeLang;
    }

    public Charset getCharset(){
        if(mCharset!=null) return mCharset;
        int firstCode = mCodes[0];
        if(firstCode>>7==0){
            mCharset = Charset.forName("UTF-8");
        }else{
            mCharset = Charset.forName("UTF-16");
        }
        return mCharset;
    }

    public int getDisplayTextIndex(){
        int index = mCodes[0];
        index = index & 0x7f;
        return index+1;
    }
}
