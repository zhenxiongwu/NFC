import {NativeModules,DeviceEventEmitter} from 'react-native';

export default {
    NfcAdapter:NativeModules.NfcAdapter,
}

const NfcAdapter = NativeModules.NfcAdapter;
NfcAdapter.addStateListener = function(listener){
    DeviceEventEmitter.addListener(NfcAdapter.EVENT_STATE_CHANGED,listener);
}
NfcAdapter.addTagDetectedListener = function(listener){
    DeviceEventEmitter.addListener(NfcAdapter.EVENT_TAG_DETECTED,listener);
}

export {NfcAdapter};

