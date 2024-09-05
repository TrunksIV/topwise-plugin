import 'dart:async';
import 'package:flutter/services.dart';

class Buzzer {
  static const MethodChannel _channel = const MethodChannel('buzzer');

  static Future<bool> beep(int mode, int ms) async {
    return await _channel.invokeMethod('beep', {'mode': mode, 'ms': ms});
  }

  static Future<bool> stopBeep() async {
    return await _channel.invokeMethod('stopBeep');
  }

}