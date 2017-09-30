import React,{Component} from 'react';
import {
    View,
    StatusBar,
    StyleSheet,
    Platform,
} from 'react-native';
import App from './app';
import {createStore} from 'redux';
import {Provider} from 'react-redux';

import {NfcAdapter} from "./nfc/index.android";
import ActionType from './actions/actionTypes';


export default class Root extends Component{
    constructor(props){
        super(props);
        this.state ={
            store: createStore(()=>{})
        }
    }

    render(){
        return (
            <Provider store={this.state.store}>
                <App/>
            </Provider>
        )
    }

    componentDidMount(){
        this.state.store.dispatch({type:[ActionType.nfc_feature],isAvailable:NfcAdapter.isAvailable});
    }

}

