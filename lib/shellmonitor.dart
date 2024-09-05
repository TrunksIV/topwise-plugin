import 'dart:async';
import 'dart:ffi';
import 'dart:typed_data';
import 'package:flutter/services.dart';

class ShellMonitor {
  static const MethodChannel _channel = const MethodChannel('shellmonitor');

  static Future<bool> executeCmd(String cmd) async {
    return await _channel.invokeMethod('executeCmd', {'cmd': cmd});
  }

  static Future<void> recovery() async {
    return await _channel.invokeMethod('recovery');
  }

  static Future<bool> canRecovery() async {
    return await _channel.invokeMethod('canRecovery');
  }

  static Future<Uint8List> getRootAuth(String rootAuth) async {
    return await _channel.invokeMethod('getRootAuth', {'rootAuth': rootAuth});
  }

  static Future<bool> executeRootCMD(String rootkey,String authToken,String cmdParams) async {
    return await _channel.invokeMethod('executeRootCMD', {'rootkey': rootkey,'authToken': authToken,'cmdParams': cmdParams});
  }

  static Future<String> get getHardwareSNPlaintext async {
    return await _channel.invokeMethod('getHardwareSNPlaintext');
  }

  static Future<Uint8List> getHardwareSNCiphertext(Uint8List b) async {
    return await _channel.invokeMethod('getHardwareSNCiphertext', {'b': b});
  }

  static Future<Uint8List> getSM4Ncryption(Uint8List key,Uint8List b) async {
    return await _channel.invokeMethod('getSM4Ncryption',{'key': key, 'b': b});
  }

  static Future<Uint8List> sendIns(int timeOutMs,Uint8 type,Uint8 cmd,Uint8 len,Uint8List insData) async {
    return await _channel.invokeMethod('sendIns',{'timeOutMs': timeOutMs,'type': type,'cmd': cmd,'len': len,'insData': insData});
  }

}