import 'dart:async';
import 'dart:ffi';
import 'dart:typed_data';
import 'package:flutter/services.dart';

class Serialport {
  static const MethodChannel _channel = const MethodChannel('serialport');

  static Future<bool> open(int port) async {
    return await _channel.invokeMethod('open', {'port': port});
  }

  static Future<bool> init(int botratebyte,Uint8 dataBits,Uint8 parity,Uint8 StopBits) async {
    return await _channel.invokeMethod('init',{'botratebyte': botratebyte,'dataBits': dataBits,'parity': parity,'StopBits': StopBits});
  }

  static Future<bool> sendData(Uint8List data,int timeout) async {
    return await _channel.invokeMethod('sendData',{'data': data, 'timeout': timeout});
  }

  static Future<Uint8List> readData(int timeout) async {
    return await _channel.invokeMethod('readData',{'timeout': timeout});
  }

  static Future<bool> close() async {
    return await _channel.invokeMethod('close');
  }

}