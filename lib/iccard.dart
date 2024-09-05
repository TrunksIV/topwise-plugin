import 'dart:async';
import 'dart:typed_data';
import 'package:flutter/services.dart';

class ICCard {
  static const MethodChannel _channel = const MethodChannel('iccard');

  static Future<bool> open() async {
    return await _channel.invokeMethod('open');
  }

  static Future<bool> close() async {
    return await _channel.invokeMethod('close');
  }

  static Future<Uint8List?> reset(int cardType) async {
    return await _channel.invokeMethod('reset', {'cardType': cardType});
  }

  static Future<bool> isExist() async {
    return await _channel.invokeMethod('isExist');
  }

  static Future<Uint8List?> apduComm(Uint8List apdu) async {
    return await _channel.invokeMethod('apduComm', {'apdu': apdu});
  }

  static Future<int> halt() async {
    return await _channel.invokeMethod('halt');
  }

}
