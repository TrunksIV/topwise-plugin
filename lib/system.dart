import 'dart:async';
import 'dart:typed_data';

import 'package:flutter/services.dart';
import 'dart:convert';

import 'package:json_annotation/json_annotation.dart';

import 'baseListener.dart';

class System {
  static const MethodChannel _channel = const MethodChannel('system');

  static Future<String> get getSerialNo async {
    return await _channel.invokeMethod('getSerialNo');
  }

  static Future<void> installApp(String filePath, BaseListener listener) async {
    // TODO conn 回调监听
    await _channel.invokeMethod('installApp', {'filePath': filePath});

    String name = 'installApp';
    EventChannel(name).receiveBroadcastStream().listen((data) {
      if (data is int) {
        listener.onError(data);
      } else {
        listener.onSuccess();
      }
    });
  }

  static Future<void> uninstallApp(String packageName, BaseListener listener) async {
    // TODO conn 回调监听
    await _channel.invokeMethod('uninstallApp', {'packageName': packageName});

    String name = 'uninstallApp';
    EventChannel(name).receiveBroadcastStream().listen((data) {
      if (data is int) {
        listener.onError(data);
      } else {
        listener.onSuccess();
      }
    });
  }

  static Future<String> get getKsn async {
    return await _channel.invokeMethod('getKsn');
  }

  static Future<String> get getDriverVersion async {
    return await _channel.invokeMethod('getDriverVersion');
  }

  static Future<String> get getCurSdkVersion async {
    return await _channel.invokeMethod('getCurSdkVersion');
  }

  static Future<String> get getPinServiceVersion async {
    return await _channel.invokeMethod('getPinServiceVersion');
  }

  static Future<String> get getStoragePath async {
    return await _channel.invokeMethod('getStoragePath');
  }

  static Future<String> get getIMSI async {
    return await _channel.invokeMethod('getIMSI');
  }

  static Future<String> get getIMEI async {
    return await _channel.invokeMethod('getIMEI');
  }

  static Future<String> get getHardWireVersion async {
    return await _channel.invokeMethod('getHardWireVersion');
  }

  static Future<String> get getSecurityDriverVersion async {
    return await _channel.invokeMethod('getStoragePath');
  }

  static Future<String> get getManufacture async {
    return await _channel.invokeMethod('getManufacture');
  }

  static Future<String> get getModel async {
    return await _channel.invokeMethod('getModel');
  }

  static Future<String> get getAndroidOsVersion async {
    return await _channel.invokeMethod('getAndroidOsVersion');
  }

  static Future<String> get getRomVersion async {
    return await _channel.invokeMethod('getRomVersion');
  }

  static Future<String> get getAndroidKernelVersion async {
    return await _channel.invokeMethod('getAndroidKernelVersion');
  }

  static Future<String> get getICCID async {
    return await _channel.invokeMethod('getICCID');
  }

  static Future<String> get getLKLOSSpecsVersion async {
    return await _channel.invokeMethod('getLKLOSSpecsVersion');
  }

}
