package com.example.nfc.module.tech.ndef;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

/**
 * Created by wuzhenxiong on 2017/9/19.
 */

public class NdefRecordModule extends ReactContextBaseJavaModule{

    public static final String TNF_EMPTY = "EMPTY";
    public static final String TNF_WELL_KNOWN = "WELL_KNOWN";
    public static final String TNF_MIME_MEDIA = "MIME_MEDIA";
    public static final String TNF_ABSOLUTE_URI = "ABSOLUTE_URI";
    public static final String TNF_EXTERNAL_TYPE = "EXTERNAL_TYPE";
    public static final String TNF_UNKNOWN = "UNKNOWN";
    public static final String TNF_UNCHANGED = "UNCHANGED";

    public static final String RTD_TEXT = "TEXT";
    public static final String RTD_URI = "URI";
    public static final String RTD_SMART_POSTER = "SMART_POSTER";
    public static final String RTD_ALTERNATIVE_CARRIER = "ALTERNATIVE_CARRIER";
    public static final String RTD_HANDOVER_CARRIER = "HANDOVER_CARRIER";
    public static final String RTD_HANDOVER_REQUEST = "HANDOVER_REQUEST";
    public static final String RTD_HANDOVER_SELECT = "HANDOVER_SELECT";

    public NdefRecordModule(ReactApplicationContext reactContext) {
        super(reactContext);
    }

    @Override
    public String getName() {
        return "NdefRecord";
    }

    @Nullable
    @Override
    public Map<String, Object> getConstants() {
        Map<String,Object> constants = new HashMap<>();
        constants.put("TNF_EMPTY",TNF_EMPTY);
        constants.put("TNF_WELL_KNOWN",TNF_WELL_KNOWN);
        constants.put("TNF_MIME_MEDIA",TNF_MIME_MEDIA);
        constants.put("TNF_ABSOLUTE_URI",TNF_ABSOLUTE_URI);
        constants.put("TNF_EXTERNAL_TYPE",TNF_EXTERNAL_TYPE);
        constants.put("TNF_UNKNOWN",TNF_UNKNOWN);
        constants.put("TNF_UNCHANGED",TNF_UNCHANGED);
        constants.put("RTD_TEXT",RTD_TEXT);
        constants.put("RTD_URI",RTD_URI);
        constants.put("RTD_SMART_POSTER",RTD_SMART_POSTER);
        constants.put("RTD_ALTERNATIVE_CARRIER",RTD_ALTERNATIVE_CARRIER);
        constants.put("RTD_HANDOVER_CARRIER",RTD_HANDOVER_CARRIER);
        constants.put("RTD_HANDOVER_REQUEST",RTD_HANDOVER_REQUEST);
        constants.put("RTD_HANDOVER_SELECT",RTD_HANDOVER_SELECT);
        return constants;
    }
}
