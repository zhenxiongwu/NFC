import React, {Component} from 'react';
import {
    View,
    Text,
    ScrollView,
    Button,
    TouchableOpacity,
    StyleSheet,
} from 'react-native';
import {NfcAdapter,NdefRecord} from './nfc';

export default class App extends Component {

    constructor(props) {
        super(props);
        this.state = {
            isAvailable: NfcAdapter.isAvailable,
            writeState:false,
            result:'',
        };

    }

    componentWillMount() {
        NfcAdapter.isEnabled(
            (isEnabled) => {
                this.setState({NfcIsEnabled:isEnabled})
            }
        )
    }

    render() {
        return (
            <View style={styles.container}>
                <View style={{}}>
                    <Text style={{}}>
                        NFC状态：<Text style={{}}>该手机{this.state.isAvailable ? "" : "不"}支持NFC功能</Text>
                        <Text>，当前{this.state.NfcIsEnabled ? '' : '不'}可用</Text>
                    </Text>
                </View>
                {this.state.tag && this._renderTagInfo()}
                <Text>{this.state.writeState&&this.state.result}</Text>
                <Button onPress={this._writeNdefMessage}
                        style={{height:30}}
                        title="写标签"
                >
                </Button>
            </View>
        )
    }

    _writeNdefMessage=async ()=>{
        try {
            const result = await NfcAdapter.writeNdefMessage(
                [NdefRecord.createText('hello'), NdefRecord.createUri('http://www.baidu.com')]
            );
            this.setState({writeState:result,result:'写入成功'});
        }catch(e){
            this.setState({writeState:true,result:e.toString()})
        }

    }


    _renderTagInfo() {
        return (
            <ScrollView style={styles.scrollView}>
                <View style={{padding: 10}}>
                    <Text style={styles.id}>
                        ID:{JSON.stringify(this.state.tag.id)}
                    </Text>
                </View>
                <View style={{paddingHorizontal:10}}>
                    <Text style={styles.id}>
                        支持的格式:
                    </Text>
                    {this._renderTechList()}
                </View>
                <View>
                    <Text>
                        {JSON.stringify(this.state.tag)}
                    </Text>
                </View>
            </ScrollView>
        )
    }

    _renderTechList() {
        const techList = this.state.tag.techList;
        return (
            <View>
                {techList.map(
                    (tech, index) => {
                        return (
                            <Text key={index}>
                                {tech}
                            </Text>
                        )
                    }
                )}
            </View>
        )
    }

    componentDidMount() {
        this._listenOnNfcState();
        this._listenOnTagDetected()
        NfcAdapter.readTagData();
    }

    _listenOnNfcState() {
        NfcAdapter.enableStateListener();
        NfcAdapter.addStateListener((state) => {
            if (state == NfcAdapter.STATE_ON)
                this.setState({NfcIsEnabled:true});
            else
                this.setState({NfcIsEnabled:false})
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
        flex: 1,
    },

    id: {
        fontSize: 16,
        fontWeight: "500",
    }

})