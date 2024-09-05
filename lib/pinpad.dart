import 'dart:async';
import 'dart:typed_data';
import 'package:flutter/services.dart';

class Pinpad {
  static const MethodChannel _channel = const MethodChannel('pinpad');

  static Future<void> getPin(dynamic arguments, GetPinListener listener) async {
    // TODO conn 回调监听
    await _channel.invokeMethod('getPin', arguments);

    String name = 'getPin';
    EventChannel(name).receiveBroadcastStream().listen((data) {
      if (data is Map) {
        String name = data['name'];
        if (name == 'onInputKey') {
          int len = data['len'];
          String msg = data['msg'];
          listener.onInputKey(len, msg);
        } else if (name == 'onError') {
          int errorCode = data['errorCode'];
          listener.onError(errorCode);
        }else if(name=='onConfirmInput'){
          Uint8List? pin = data['pin'];
          listener.onConfirmInput(pin);
        } else if (name == 'onCancelKeyPress') {
          listener.onCancelKeyPress();
        } else if (name == 'onStopGetPin') {
          listener.onStopGetPin();
        } else if (name == 'onTimeout') {
          listener.onTimeout();
        }
      } else {}
    });
  }

  static Future<void> get stopGetPin async {
    return await _channel.invokeMethod('stopGetPin');
  }

  static Future<void> get confirmGetPin async {
    return await _channel.invokeMethod('confirmGetPin');
  }

  static Future<bool> loadMainkey(int keyId, Uint8List? key, Uint8List? kcv) async {
    return await _channel.invokeMethod('loadMainkey', {'keyId': keyId, 'key': key, 'kcv': kcv});
  }

  static Future<bool> loadWorkKey(int keyType, int keyId, int masterKeyId, Uint8List? key, Uint8List? kcv) async {
    return await _channel.invokeMethod(
        'loadWorkKey', {'keyType': keyType, 'keyId': keyId, 'masterKeyId': masterKeyId, 'key': key, 'kcv': kcv});
  }

  static Future<Uint8List> getMac(dynamic arguments) async {
    return await _channel.invokeMethod('getMac', arguments);
  }

  static Future<Uint8List?> encryptByTdk(int keyId, int mode, Uint8List? random, Uint8List data) async {
    return await _channel.invokeMethod('encryptByTdk', {'keyId': keyId, 'mode': mode, 'random': random, 'data': data});
  }

  static Future<Uint8List> get getRandom async {
    return await _channel.invokeMethod('getRandom');
  }

  static Future<bool> display(String line1, String line2) async {
    return await _channel.invokeMethod('display', {'line1': line1, 'line2': line2});
  }

  static Future<bool> loadTEK(int keyId, Uint8List key, Uint8List? kcv) async {
    return await _channel.invokeMethod('loadTEK', {'keyId': keyId, 'key': key, 'kcv': kcv});
  }

  static Future<bool> loadTWK(int keyType, int keyId, int tmkID, Uint8List key, Uint8List? kcv) async {
    return await _channel
        .invokeMethod('loadTWK', {'keyType': keyType, 'keyId': keyId, 'tmkID': tmkID, 'key': key, 'kcv': kcv});
  }

  static Future<bool> loadEncryptMainkey(int keyId, int tmkID, Uint8List key, Uint8List? kcv) async {
    return await _channel.invokeMethod('loadEncryptMainkey', {'keyId': keyId, 'tmkID': tmkID, 'key': key, 'kcv': kcv});
  }

  static Future<bool> setPinKeyboardMode(int mode) async {
    return await _channel.invokeMethod('setPinKeyboardMode', {'mode': mode});
  }

  static Future<bool> getKeyState(int keyType, int keyId) async {
    return await _channel.invokeMethod('getKeyState', {'keyType': keyType, 'keyId': keyId});
  }

  static Future<bool> loadDuKPTkey(int keyType, int keyId, Uint8List key, Uint8List? ksn) async {
    return await _channel.invokeMethod('loadDuKPTkey', {'keyType': keyType, 'keyId': keyId, 'key': key, 'ksn': ksn});
  }

  static Future<Uint8List>? getDUKPTKsn(int keyId, bool isIncrease) async {
    return await _channel.invokeMethod('getDUKPTKsn', {'keyId': keyId, 'isIncrease': isIncrease});
  }

  static Future<Uint8List> algorithmCal(int keyType, int keyId, Uint8List data, Uint8List? cbcData) async {
    return await _channel
        .invokeMethod('algorithmCal', {'keyType': keyType, 'keyId': keyId, 'data': data, 'cbcData': cbcData});
  }

  static Future<bool> algorithDecrypt(int keyId, Uint8List data) async {
    return await _channel.invokeMethod('algorithDecrypt', {'keyId': keyId, 'data': data});
  }

  static Future<bool> loadKeyCommonMethod(int encryptKeyType, int encryptKeyId, int decryptKeyType, int decryptKeyId,
      int decryptAlg, Uint8List vectorData, Uint8List encryptData, Uint8List? checkValue) async {
    return await _channel.invokeMethod('loadKeyCommonMethod', {
      'encryptKeyType': encryptKeyType,
      'encryptKeyId': encryptKeyId,
      'decryptKeyType': decryptKeyType,
      'decryptKeyId': decryptKeyId,
      'decryptAlg': decryptAlg,
      'vectorData': vectorData,
      'encryptData': encryptData,
      'checkValue': checkValue
    });
  }

  static Future<Uint8List> get getButtonNum async {
    return await _channel.invokeMethod('getButtonNum');
  }

  static Future<bool> deletePedKey(int keyType, int keyId, int mode) async {
    return await _channel.invokeMethod('deletePedKey', {'keyType': keyType, 'keyId': keyId, 'mode': mode});
  }

  static Future<bool> loadDukptIPEK(int keyId, Uint8List key, Uint8List? ksn) async {
    return await _channel.invokeMethod('loadDukptIPEK', {'keyId': keyId, 'key': key, 'ksn': ksn});
  }

  static Future<bool> loadDukptBDK(int keyId, Uint8List? key, Uint8List? ksn) async {
    return await _channel.invokeMethod('loadDukptBDK', {'keyId': keyId, 'key': key, 'ksn': ksn});
  }

  static Future<Uint8List> cryptByTdk(int keyId, int mode, Uint8List data, Uint8List? iv) async {
    return await _channel.invokeMethod('cryptByTdk', {'keyId': keyId, 'mode': mode, 'data': data, 'iv': iv});
  }

  static Future<Uint8List> cryptByDukptDataKey(int keyId, int mode, Uint8List data, Uint8List? iv) async {
    return await _channel.invokeMethod('cryptByDukptDataKey', {'keyId': keyId, 'mode': mode, 'data': data, 'iv': iv});
  }

  static Future<Uint8List> getKeyCheckValue(int keyType, int keyId) async {
    return await _channel.invokeMethod('getKeyCheckValue', {'keyType': keyType, 'keyId': keyId});
  }

  static Future<bool> loadTr31EncryptMainkey(int tekID, int tmkID, Uint8List key, Uint8List? kcv) async {
    return await _channel
        .invokeMethod('loadTr31EncryptMainkey', {'tekID': tekID, 'tmkID': tmkID, 'key': key, 'kcv': kcv});
  }

  static Future<int> dukptKeyBdkToIpek(int keyId) async {
    return await _channel.invokeMethod('dukptKeyBdkToIpek', {'keyId': keyId});
  }

  static Future<int> getKeyKin(int recordIndex, int keyType, int keyId) async {
    return await _channel.invokeMethod('getKeyKin', {'recordIndex': recordIndex, 'keyType': keyType, 'keyId': keyId});
  }

  static Future<Uint8List> cryptByFixedTdk(int keyId, int mode, Uint8List data, Uint8List? iv) async {
    return await _channel.invokeMethod('cryptByFixedTdk', {'keyId': keyId, 'mode': mode, 'data': data, 'iv': iv});
  }

  static Future<Uint8List> genRandomTekEncryptByTek(int masterIndex, int subIndex, int keyLen) async {
    return await _channel
        .invokeMethod('genRandomTekEncryptByTek', {'masterIndex': masterIndex, 'subIndex': subIndex, 'keyLen': keyLen});
  }

  static Future<bool> loadKey(int keyId, int keyType, Uint8List key, Uint8List? kcv) async {
    return await _channel.invokeMethod('loadKey', {'keyId': keyId, 'keyType': keyType, 'key': key, 'kcv': kcv});
  }

  static Future<void> setPinPadLayout(Uint8List layout) async {
    return await _channel.invokeMethod('setPinPadLayout', {'layout': layout});
  }

  static Future<Uint8List> checkkey(int keyId, int keyType, bool isSM4) async {
    return await _channel.invokeMethod('checkkey', {'keyId': keyId, 'keyType': keyType, 'isSM4': isSM4});
  }

  static Future<Uint8List> storedRecord(int actionType, int recordNumber, Uint8List data) async {
    return await _channel
        .invokeMethod('storedRecord', {'actionType': actionType, 'recordNumber': recordNumber, 'data': data});
  }
}

typedef void OnInputKey(int len, String msg);
typedef void OnError(int errorCode);
typedef void OnConfirmInput(Uint8List? pin);
typedef void OnCancelKeyPress();
typedef void OnStopGetPin();
typedef void OnTimeout();

class GetPinListener {
  OnInputKey onInputKey;
  OnError onError;
  OnConfirmInput onConfirmInput;
  OnCancelKeyPress onCancelKeyPress;
  OnStopGetPin onStopGetPin;
  OnTimeout onTimeout;

  GetPinListener(
      this.onInputKey, this.onError, this.onConfirmInput, this.onCancelKeyPress, this.onStopGetPin, this.onTimeout);
}
