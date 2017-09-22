package com.example.nfc_library.module;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
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
import android.os.Parcelable;
import android.widget.Toast;

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
    private WritableMap mTag;


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
                    Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
                    parseTag(tag);
                    Toast.makeText(mReactContext, "this is ndef", Toast.LENGTH_SHORT).show();

                } else if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(intent.getAction())) {
                    Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

                    Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
                    if (rawMsgs != null && rawMsgs.length > 0) {
                        Toast.makeText(mReactContext, "this is tech "+((NdefMessage)rawMsgs[0]).getRecords()[0].toMimeType(), Toast.LENGTH_SHORT).show();
                    }
                    parseTag(tag);

                } else if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())) {
                    Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

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
            Toast.makeText(mReactContext, filter, Toast.LENGTH_SHORT).show();
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

    private void parseTag(Tag tag) {
        mTag = Arguments.createMap();
        mTag.putInt("id", ByteBuffer.wrap(tag.getId()).getInt());
        mReactContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class).emit(EVENT_TAG_DETECTED, mTag);
        Toast.makeText(mReactContext, "parseTag", Toast.LENGTH_SHORT).show();
    }
}
