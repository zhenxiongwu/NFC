import {NfcAdapter} from '../nfc';
import ActionType from '../actions/actionTypes';


export type NfcState = {
    isAvailable:boolean
}

const initialState = {
    isAvailable:true
}

export default function(state: NfcState = initialState, action):NfcState{
    switch (action.type){
        case ActionType.nfc_feature:
            if(action.isAvailable){
                return {
                    ...state,
                    isAvailable:true
                }
            }else{
                return {
                    ...state,
                    isAvailable:false
                }
            }
        case ActionType.nfc_state:
            switch (action.nfc_state){
                case NfcAdapter.STATE_ON:
                    return {
                        ...state,
                        isEnabled:true
                    };
                default:
                    return {
                        ...state,
                        isEnabled:false
                    }

            }
    }

}