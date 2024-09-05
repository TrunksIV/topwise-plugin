import 'dart:async';
import 'package:flutter/services.dart';
import 'LedEnum.dart';

class Led {
  static const MethodChannel _channel = const MethodChannel('led');

  static Future<bool> setLed(LedEnum light, bool isOn) async {
    return await _channel.invokeMethod('setLed', {'light': light.value, 'isOn': isOn});
  }

}