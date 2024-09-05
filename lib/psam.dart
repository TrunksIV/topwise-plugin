import 'dart:async';
import 'dart:ffi';
import 'dart:typed_data';
import 'package:flutter/services.dart';

class Psam {
  static const MethodChannel _channel = const MethodChannel('psam');

  static Future<bool> init(int devid) async {
    return await _channel.invokeMethod('init', {'devid': devid});
  }

  static Future<bool> open() async {
    return await _channel.invokeMethod('open');
  }

  static Future<bool> close() async {
    return await _channel.invokeMethod('close');
  }

  static Future<Uint8List?> reset(int cardType) async {
    return await _channel.invokeMethod('reset', {'cardType': cardType});
  }

  static Future<Uint8List?> apduComm(Uint8List apdu) async {
    return await _channel.invokeMethod('apduComm', {'apdu': apdu});
  }

  static Future<bool> setETU(Uint8 etuVal) async {
    return await _channel.invokeMethod('setETU', {'etuVal': etuVal});
  }

}
