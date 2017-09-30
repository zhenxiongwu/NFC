package com.example.nfc.module;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import android.nfc.tech.MifareClassic;
import android.nfc.tech.MifareUltralight;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.nfc.tech.NfcA;
import android.nfc.tech.NfcB;
import android.nfc.tech.NfcF;
import android.nfc.tech.NfcV;
import android.support.annotation.NonNull;

import com.example.nfc.module.tech.ndef.NdefReader;
import com.example.nfc.module.tech.ndef.WriteNdefMessageTask;
import com.example.nfc.utils.NfcStateReceiver;
import com.example.nfc.utils.OnNfcStateChangeListener;
import com.facebook.react.bridge.ActivityEventListener;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.BaseActivityEventListener;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;


/**
 * Created by wuzhenxiong on 2017/9/19.
 */

public class NfcAdapterModule extends ReactContextBaseJavaModule implements OnNfcStateChangeListener {

    public final static String EVENT_STATE_CHANGED = "NFC_ADAPTER_STATE_CHANGED";
    public final static String EVENT_TAG_DETECTED = "A_TAG_IS_DETECTED";

    public final static String ILLEGAL_OPERATION_EXCEPTION = "IllegalOperationException";
    public final static String IO_EXCEPTION = "IOException";
    public final static String NULL_OBJECT_EXCEPTION = "NullObjectException";
    public final static String READ_ONLY_EXCEPTION = "ReadOnlyException";
    public final static String OVERFLOW_EXCEPTION = "OverflowException";
    public final static String FORMAT_EXCEPTION = "FormatException";

    private final ReactApplicationContext mReactContext;
    private final NfcAdapter mNfcAdapter;
//    private boolean isListeningOnAdapterState;
    private boolean toReadTag;
    private boolean toWriteTag;

    private IntentFilter[] mIntentFilters;
    private String[][] mTechLists;

    /**
     * isEnableForegroundDispatch is controlled by the module caller
     */
    private boolean isEnableForegroundDispatch;

    /**
     * isForegroundDispatchEnabled is controlled by this module
     */
    private boolean isForegroundDispatchEnabled;

    private Map<String, Object> mConstants;

    /**
     * core object
     */
    private Tag mTag;
    private Ndef mNdef;

    private WriteNdefMessageTask mWriteNdefMessageTask;
    private NdefWriteRequsetHandler mNdefWriteRequestHandler;


    public NfcAdapterModule(ReactApplicationContext reactContext) {
        super(reactContext);
        mReactContext = reactContext;
        mNfcAdapter = NfcAdapter.getDefaultAdapter(mReactContext);
//        isListeningOnAdapterState = false;
        isEnableForegroundDispatch = false;
        isForegroundDispatchEnabled = false;
        toReadTag = false;
        toWriteTag = false;

        mConstants = new HashMap<>();


        /**
         * Listen on nfc state changing.
         */
        IntentFilter filter = new IntentFilter(NfcAdapter.ACTION_ADAPTER_STATE_CHANGED);
        mReactContext.registerReceiver(NfcStateReceiver.getInstance(), filter);
        NfcStateReceiver.getInstance().addListener(this);

        /**
         * Listen on new intent
         */
        ActivityEventListener mActivityEventListener = new BaseActivityEventListener() {
            @Override
            public void onNewIntent(Intent intent) {
                super.onNewIntent(intent);

                if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction()) ||
                        NfcAdapter.ACTION_TECH_DISCOVERED.equals(intent.getAction()) ||
                        NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())) {
                    mTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
                    notifyTagDetected(mTag);
                }
            }
        };

        /**
         * Listen on events of host activity's lifecycle
         */
        LifecycleEventListener mLifecycleEventListener = new LifecycleEventListener() {
            @Override
            public void onHostResume() {
                if (isEnableForegroundDispatch && !isForegroundDispatchEnabled) {
                    _enableForegroundDispatch();
                }
            }

            @Override
            public void onHostPause() {
                if (isForegroundDispatchEnabled) {
                    _disableForegroundDispatch();
                }
            }

            @Override
            public void onHostDestroy() {
            }
        };

        mReactContext.addActivityEventListener(mActivityEventListener);
        mReactContext.addLifecycleEventListener(mLifecycleEventListener);
    }

    /**
     * The module name witch will be used by the js caller
     *
     * @return
     */
    @Override
    public String getName() {
        return "NfcAdapter";
    }

    @Nullable
    @Override
    public Map<String, Object> getConstants() {
        mConstants.put("isAvailable", mNfcAdapter != null);

        mConstants.put("EVENT_STATE_CHANGED", EVENT_STATE_CHANGED);
        mConstants.put("EVENT_TAG_DETECTED", EVENT_TAG_DETECTED);

        mConstants.put("STATE_OFF", NfcAdapter.STATE_OFF);
        mConstants.put("STATE_ON", NfcAdapter.STATE_ON);
        mConstants.put("STATE_TURNING_OFF", NfcAdapter.STATE_TURNING_OFF);
        mConstants.put("STATE_TURNING_ON", NfcAdapter.STATE_TURNING_ON);

        mConstants.put("FILTER_NDEF_DISCOVERED", NfcAdapter.ACTION_NDEF_DISCOVERED);
        mConstants.put("FILTER_TECH_DISCOVERED", NfcAdapter.ACTION_TECH_DISCOVERED);
        mConstants.put("FILTER_TAG_DISCOVERED", NfcAdapter.ACTION_TAG_DISCOVERED);

        mConstants.put("TECH_NFC_A", NfcA.class.getName());
        mConstants.put("TECH_NFC_B", NfcB.class.getName());
        mConstants.put("TECH_NFC_F", NfcF.class.getName());
        mConstants.put("TECH_NFC_V", NfcV.class.getName());
        mConstants.put("TECH_NDEF", Ndef.class.getName());
        mConstants.put("TECH_ISO_DEP", IsoDep.class.getName());
        mConstants.put("TECH_NDEF_FORMATABLE", NdefFormatable.class.getName());
        mConstants.put("TECH_MIFARE_CLASSIC", MifareClassic.class.getName());
        mConstants.put("TECH_MIFARE_ULTRALIGHT", MifareUltralight.class.getName());


        mConstants.put("IO_EXCEPTION",IO_EXCEPTION);
        mConstants.put("NULL_OBJECT_EXCEPTION",NULL_OBJECT_EXCEPTION);
        mConstants.put("READ_ONLY_EXCEPTION",READ_ONLY_EXCEPTION);
        mConstants.put("OVERFLOW_EXCEPTION",OVERFLOW_EXCEPTION);
        mConstants.put("FORMAT_EXCEPTION",FORMAT_EXCEPTION);
        return mConstants;
    }

    /**
     * Callback the function to return the result of whether the Nfc is enable
     *
     * @param callback is the function that set by JavaScript caller
     */
    @ReactMethod
    public void isEnabled(Callback callback) {
        callback.invoke(mNfcAdapter.isEnabled());
    }


    /**
     * Enable the module to listen on the changing of the nfc adapter state
     */
/*    @ReactMethod
    public void enableStateListener() {
        if (isListeningOnAdapterState) return;
        IntentFilter filter = new IntentFilter(NfcAdapter.ACTION_ADAPTER_STATE_CHANGED);
        mReactContext.registerReceiver(NfcStateReceiver.getInstance(), filter);
        NfcStateReceiver.getInstance().addListener(this);
        isListeningOnAdapterState = true;
    }*/

    /**
     * Disable the module to listen on the changing of the nfc adapter state
     */
/*    @ReactMethod
    public void disableStateListener() {
        if (isListeningOnAdapterState) {
            NfcStateReceiver.getInstance().removeListener(this);
            mReactContext.unregisterReceiver(NfcStateReceiver.getInstance());
            isListeningOnAdapterState = false;
        }
    }*/

    @Override
    public void onNfcStateChange(int state) {
        if(state==NfcAdapter.STATE_ON && isEnableForegroundDispatch && !isForegroundDispatchEnabled){
            _enableForegroundDispatch();
        }else if(state == NfcAdapter.STATE_TURNING_OFF && isForegroundDispatchEnabled) {
            _disableForegroundDispatch();
        }
        mReactContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class).emit(EVENT_STATE_CHANGED, state);
    }

    /**
     * @param filters is a array composited by the String elements , as each element is the name of the action filter
     * @param techs   is a two-dimension array composited by tech array.
     */
    @ReactMethod
    public void enableForegroundDispatch(ReadableArray filters, ReadableArray techs) {
        if (isEnableForegroundDispatch) return;
        isEnableForegroundDispatch = true;

        mIntentFilters = new IntentFilter[filters.size()];

        for (int i = 0; i < filters.size(); i++) {
            String filter = filters.getString(i);
            mIntentFilters[i] = new IntentFilter(filter);
            if (filter.equals(NfcAdapter.ACTION_NDEF_DISCOVERED)) {
                try {
                    mIntentFilters[i].addDataType("*/*");
                } catch (IntentFilter.MalformedMimeTypeException e) {
                    e.printStackTrace();
                }
            }
        }

        mTechLists = new String[techs.size()][];
        for (int i = 0; i < techs.size(); i++) {
            ReadableArray techList = techs.getArray(i);
            mTechLists[i] = new String[techList.size()];
            for (int j = 0; j < techList.size(); j++) {
                mTechLists[i][j] = techList.getString(j);
            }
        }
        _enableForegroundDispatch();
    }

    private void _enableForegroundDispatch() {
        Activity currentActivity = getCurrentActivity();
        Intent intent = new Intent(mReactContext, currentActivity.getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(mReactContext, 0, intent, 0);
        mNfcAdapter.enableForegroundDispatch(currentActivity, pendingIntent, mIntentFilters, mTechLists);
        isForegroundDispatchEnabled = true;
    }

    @ReactMethod
    public void disableForegroundDispatch() {
        if (!isEnableForegroundDispatch) return;
        isEnableForegroundDispatch = false;
        _disableForegroundDispatch();
    }

    private void _disableForegroundDispatch() {
        mNfcAdapter.disableForegroundDispatch(getCurrentActivity());
        isForegroundDispatchEnabled = false;
    }

    private WritableMap parseTag(@NonNull Tag tag) {
        WritableMap tagMap = Arguments.createMap();
        tagMap.putInt("id", ByteBuffer.wrap(mTag.getId()).getInt());
        tagMap.putArray("techList", parseTechList(tag));
        if (mNdef != null) {
            NdefReader ndefReader = new NdefReader(mReactContext, mNdef);
            tagMap.putMap("ndef", ndefReader.getTagMap());
        }
        return tagMap;
    }

    private WritableArray parseTechList(@NonNull Tag tag) {
        WritableArray techList = Arguments.createArray();
        for (String tech : tag.getTechList()) {
            techList.pushString(tech);
        }
        return techList;
    }

    private boolean checkSupportTechs(@NonNull Tag tag, @NonNull String[] checkTechs) {
        String[] techList = tag.getTechList();
        if (techList == null || techList.length == 0) return false;
        for (String checkTech : checkTechs) {
            if (!isTechSupported(tag, checkTech)) {
                return false;
            }
        }
        return true;
    }

    private boolean isTechSupported(Tag tag, String checkTech) {
        for (String tech : tag.getTechList()) {
            if (tech.equals(checkTech)) return true;
        }
        return false;
    }

    @ReactMethod
    public void readTagData() {
        toReadTag = true;
    }

    @ReactMethod
    public void ignoreTagData() {
        toReadTag = false;
    }


    @ReactMethod
    public void writeNdefMessage(ReadableArray message, Promise promise) {
        if(mNfcAdapter==null || !mNfcAdapter.isEnabled()){
            promise.reject(ILLEGAL_OPERATION_EXCEPTION,"Nfc feature of the device is unusable");
            return;
        }
        toWriteTag = true;
        mNdefWriteRequestHandler = new NdefWriteRequsetHandler(message, promise);
    }

    @ReactMethod
    public void cancelWriteOption() {
        toWriteTag = false;
        mNdefWriteRequestHandler = null;
    }


    private void notifyTagDetected(Tag tag) {
        mNdef = Ndef.get(tag);

        WritableMap tagMap = null;
        if (toReadTag) {
            tagMap = parseTag(tag);
        }
        mReactContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class).emit(EVENT_TAG_DETECTED, tagMap);

        if (toWriteTag) {
            if(mNdefWriteRequestHandler!=null){
                if (mNdef != null) {
                    new WriteNdefMessageTask(mReactContext,mNdef,mNdefWriteRequestHandler.promise).execute(mNdefWriteRequestHandler.message);
                }else{
                    mNdefWriteRequestHandler.promise.reject(FORMAT_EXCEPTION,"The tag does not support Ndef");
                }
                mNdefWriteRequestHandler = null;
            }
            toWriteTag=false;
        }

        mTag = null;
        mNdef = null;

    }

    private class NdefWriteRequsetHandler {
        ReadableArray message;
        Promise promise;

        NdefWriteRequsetHandler(ReadableArray msg, Promise prm) {
            message = msg;
            promise = prm;
        }
    }

}