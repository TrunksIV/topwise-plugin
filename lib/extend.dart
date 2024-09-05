import 'dart:async';
import 'package:flutter/services.dart';

class Extend {
  static const MethodChannel _channel = const MethodChannel('extend');

  static Future<bool> EnableMasterPosKey(bool enable) async {
    return await _channel.invokeMethod('EnableMasterPosKey', {'enable': enable});
  }

  static Future<void> goToSleep() async {
    await _channel.invokeMethod('goToSleep');
  }

  static Future<void> wakeUp() async {
    await _channel.invokeMethod('wakeUp');
  }

  static Future<void> lockStatusBar(bool isLock) async {
    await _channel.invokeMethod('lockStatusBar', {'isLock': isLock});
  }

  static Future<void> switchSimCard(int slotId) async {
    await _channel.invokeMethod('switchSimCard', {'slotId': slotId});
  }

  static Future<void> lockSimCard(int slotId, bool isLock) async {
    await _channel.invokeMethod('lockSimCard', {'slotId': slotId, 'isLock': isLock});
  }

}