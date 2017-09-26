import React, {Component} from 'react';
import {
    View,
    Text,
    ScrollView,
    TouchableOpacity,
    StyleSheet,
} from 'react-native';
import {NfcAdapter} from './nfc/index.android';

export default class App extends Component {

    constructor(props) {
        super(props);
        this.state = {
            isAvailable: NfcAdapter.isAvailable,
        };

    }

    render() {
        return (
            <View style={styles.container}>
                <View style={{}}>
                    <Text style={{}}>
                        NFC状态：<Text style={{}}>该手机{this.state.isAvailable ? "" : "不"}支持NFC功能</Text>
                        <Text>，当前{this.state.NfcState == NfcAdapter.STATE_ON ? '' : '不'}可用</Text>
                    </Text>
                </View>
                <ScrollView style={styles.scrollView}>
                    <View>
                        <Text>
                            {JSON.stringify(this.state.tag)}
                        </Text>
                    </View>
                </ScrollView>
            </View>
        )
    }

    componentDidMount() {
        this._listenOnNfcState();
        this._listenOnTagDetected()
    }

    _listenOnNfcState() {
        NfcAdapter.enableStateListener();
        NfcAdapter.addStateListener((state) => {
            this.setState({NfcState: state});
        })
    }

    _listenOnTagDetected() {
        NfcAdapter.enableForegroundDispatch([NfcAdapter.FILTER_NDEF_DISCOVERED], [[NfcAdapter.TECH_NDEF]]);
        NfcAdapter.addTagDetectedListener(
            (tag) => {
                this.setState({"tag": tag});
            }
        )
    }
}

const styles = StyleSheet.create({
    container: {
        flex: 1,
    },

    scrollView: {
        flex:1,
    }

})