import {NativeModules,DeviceEventEmitter} from 'react-native';


const NfcAdapter = NativeModules.NfcAdapter;
NfcAdapter.addStateListener = function(listener){
    DeviceEventEmitter.addListener(NfcAdapter.EVENT_STATE_CHANGED,listener);
}
NfcAdapter.addTagDetectedListener = function(listener){
    DeviceEventEmitter.addListener(NfcAdapter.EVENT_TAG_DETECTED,listener);
}


const NdefRecord = NativeModules.NdefRecord;
NdefRecord.createText= function(text){
    return {
        "TNF":NdefRecord.TNF_WELL_KNOWN,
        "RTD":NdefRecord.RTD_TEXT,
        "content":text
    }
}

NdefRecord.createUri = function(uri){
    return{
        "TNF":NdefRecord.TNF_WELL_KNOWN,
        "RTD":NdefRecord.RTD_URI,
        "content":uri
    }
}

NdefRecord.createExternal = function(domain,type,payload){
    return{
        "TNF":NdefRecord.TNF_EXTERNAL_TYPE,
        "domain":domain,
        "type":type,
        "content":payload
    }
}

NdefRecord.createMime = function(type,payload){
    return{
        "TNF":NdefRecord.TNF_MIME_MEDIA,
        "type":type,
        "content":payload
    }
}



export {NfcAdapter,NdefRecord};

