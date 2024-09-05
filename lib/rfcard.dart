import 'dart:async';
import 'dart:typed_data';
import 'package:flutter/services.dart';

class RfCard {
  static const MethodChannel _channel = const MethodChannel('rfcard');

  static Future<bool> open() async {
    return await _channel.invokeMethod('open');
  }

  static Future<bool> close() async {
    return await _channel.invokeMethod('close');
  }

  static Future<Uint8List> reset(int cardType) async {
    return await _channel.invokeMethod('reset', {'cardType': cardType});
  }

  static Future<bool> isExist() async {
    return await _channel.invokeMethod('isExist');
  }

  static Future<int> isExistMT() async {
    return await _channel.invokeMethod('isExistMT');
  }

  static Future<Uint8List?> apduComm(Uint8List apdu) async {
    return await _channel.invokeMethod('apduComm', {'apdu': apdu});
  }

  static Future<int> halt() async {
    return await _channel.invokeMethod('halt');
  }

  static Future<int> getCardType() async {
    return await _channel.invokeMethod('getCardType');
  }

  static Future<int> auth(int type,int blockaddr,Uint8List keydata,Uint8List resetRes) async {
    return await _channel.invokeMethod('auth', {'type': type,'blockaddr': blockaddr,'keydata': keydata,'resetRes': resetRes});
  }

  static Future<Uint8List?> readBlock(int blockaddr) async {
    return await _channel.invokeMethod('readBlock', {'blockaddr': blockaddr});
  }

  static Future<Uint8List?> readBlockMT(int blockaddr) async {
    return await _channel.invokeMethod('readBlockMT', {'blockaddr': blockaddr});
  }

  static Future<int> writeBlock(int blockaddr, Uint8List data) async {
    return await _channel.invokeMethod('writeBlock', {'blockaddr': blockaddr, 'data': data});
  }

  static Future<int> addValue(int blockaddr, Uint8List data) async {
    return await _channel.invokeMethod('addValue', {'blockaddr': blockaddr, 'data': data});
  }

  static Future<int> reduceValue(int blockaddr, Uint8List data) async {
    return await _channel.invokeMethod('reduceValue', {'blockaddr': blockaddr, 'data': data});
  }

  static Future<Uint8List?> readBlockX(int blockaddr) async {
    return await _channel.invokeMethod('readBlockX', {'blockaddr': blockaddr});
  }

  static Future<Uint8List?> getCardCode() async {
    return await _channel.invokeMethod('getCardCode');
  }

  static Future<Uint8List?> getATQA() async {
    return await _channel.invokeMethod('getATQA');
  }

  static Future<Uint8List?> activateTypeAOrIDCard(int cardType) async {
    return await _channel.invokeMethod('activateTypeAOrIDCard', {'cardType': cardType});
  }

  static Future<Uint8List?> getFelicaProtocolData() async {
    return await _channel.invokeMethod('getFelicaProtocolData');
  }

  static Future<Uint8List?> felicaTransceive(Uint8List apdu) async {
    return await _channel.invokeMethod('felicaTransceive', {'apdu': apdu});
  }

  static Future<Uint8List?> getUID() async {
    return await _channel.invokeMethod('getUID');
  }

}