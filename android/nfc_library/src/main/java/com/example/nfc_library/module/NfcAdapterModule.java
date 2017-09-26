package com.example.nfc_library.module;

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
import android.widget.Toast;

import com.example.nfc_library.module.tech.ndef.NdefWrapper;
import com.example.nfc_library.utils.NfcStateReceiver;
import com.example.nfc_library.utils.OnNfcStateChangeListener;
import com.facebook.react.bridge.ActivityEventListener;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.BaseActivityEventListener;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.LifecycleEventListener;
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

    private final static String EVENT_STATE_CHANGED = "NFC_ADAPTER_STATE_CHANGED";
    private final static String EVENT_TAG_DETECTED = "A_TAG_IS_DETECTED";

    private final ReactApplicationContext mReactContext;
    private final NfcAdapter mNfcAdapter;
    private boolean isListeningOnAdapterState;

    private IntentFilter[] mIntentFilters;
    private String[][] mTechLists;

    /**
     * isEnableForegroundDispatch is controlled by the module caller
     */
    private boolean isEnableForegroundDispatch;

    private Map<String, Object> mConstants;

    /**
     * core object
     */
    private Tag mTag;


    public NfcAdapterModule(ReactApplicationContext reactContext) {
        super(reactContext);
        mReactContext = reactContext;
        mNfcAdapter = NfcAdapter.getDefaultAdapter(mReactContext);
        isListeningOnAdapterState = false;
        isEnableForegroundDispatch = false;

        mConstants = new HashMap<>();

        ActivityEventListener mActivityEventListener = new BaseActivityEventListener() {
            @Override
            public void onNewIntent(Intent intent) {
                super.onNewIntent(intent);
                Toast.makeText(mReactContext, intent.getAction(), Toast.LENGTH_SHORT).show();

                if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())) {
                    Toast.makeText(mReactContext, "this is ndef", Toast.LENGTH_SHORT).show();

                    mTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
                    WritableMap tagMap = parseTag(mTag);
                    notifyTagDetected(tagMap);

                } else if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(intent.getAction())) {
                    Toast.makeText(mReactContext, "this is tech ", Toast.LENGTH_SHORT).show();

                    mTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

                    WritableMap tagMap = parseTag(mTag);
                    notifyTagDetected(tagMap);

                } else if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())) {
                    mTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

                }
            }
        };

        LifecycleEventListener mLifecycleEventListener = new LifecycleEventListener() {
            @Override
            public void onHostResume() {
                if (isEnableForegroundDispatch) {
                    _enableForegroundDispatch();
                }
            }

            @Override
            public void onHostPause() {
                if (isEnableForegroundDispatch) {
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
        return mConstants;
    }

    /**
     * Callback the function to return the result of whether the Nfc is enable
     *
     * @param callback is the function that set by JavaScript caller
     */
    @ReactMethod
    public void isEnable(Callback callback) {
        callback.invoke(mNfcAdapter.isEnabled());
    }


    /**
     * Enable the module to listen on the changing of the nfc adapter state
     */
    @ReactMethod
    public void enableStateListener() {
        if (isListeningOnAdapterState) return;
        IntentFilter filter = new IntentFilter(NfcAdapter.ACTION_ADAPTER_STATE_CHANGED);
        mReactContext.registerReceiver(NfcStateReceiver.getDefaultReceiver(), filter);
        NfcStateReceiver.getDefaultReceiver().addListener(this);
        isListeningOnAdapterState = true;
    }

    /**
     * Disable the module to listen on the changing of the nfc adapter state
     */
    @ReactMethod
    public void disableStateListener() {
        if (isListeningOnAdapterState) {
            NfcStateReceiver.getDefaultReceiver().removeListener(this);
            mReactContext.unregisterReceiver(NfcStateReceiver.getDefaultReceiver());
            isListeningOnAdapterState = false;
        }
    }

    @Override
    public void onNfcStateChange(int state) {
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
    }

    @ReactMethod
    public void disableForegroundDispatch() {
        if (!isEnableForegroundDispatch) return;
        isEnableForegroundDispatch = false;
        _disableForegroundDispatch();
    }

    private void _disableForegroundDispatch() {
        mNfcAdapter.disableForegroundDispatch(getCurrentActivity());
    }

    private WritableMap parseTag(@NonNull Tag tag) {
        WritableMap tagMap = Arguments.createMap();
        tagMap.putInt("id", ByteBuffer.wrap(mTag.getId()).getInt());
        tagMap.putArray("tech-list", parseTechList(tag));
        if (isTechSupported(tag,Ndef.class.getName())) {
            NdefWrapper ndefWrapper = new NdefWrapper(mReactContext,tag);
            tagMap.putMap("ndef",ndefWrapper.getTagMap());
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
            if(!isTechSupported(tag,checkTech)){
                return false;
            }
        }
        return true;
    }

    private boolean isTechSupported(Tag tag,String checkTech){
        for(String tech: tag.getTechList()){
            if(tech.equals(checkTech)) return true;
        }
        return false;
    }

    private void notifyTagDetected(WritableMap writableMap) {
        mReactContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class).emit(EVENT_TAG_DETECTED, writableMap);
    }

}
