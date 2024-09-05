import 'dart:async';
import 'dart:convert';
import 'dart:typed_data';
import 'package:flutter/services.dart';
import 'package:json_annotation/json_annotation.dart';

import 'MagData.dart';

class MagCard {
  static const MethodChannel _channel = const MethodChannel('magcard');

  static Future<void> searchCard(int timeout, MagCardListener listener) async {
    // TODO conn 回调监听
    await _channel.invokeMethod('searchCard', {'timeout': timeout});

    String name = 'searchCard';
    EventChannel(name).receiveBroadcastStream().listen((data) {
      if (data is Map) {
        String name = data['name'];
        if(name=='onSuccess'){
          Map<String, dynamic> map = Map<String, dynamic>.from(data['magData']);
          print(map);
          MagData magData = new MagData();
          magData.fromJson(map);
          listener.onSuccess(magData);
        }else if(name=='onTimeout'){
          listener.onTimeout();
        }else if(name=='onError'){
          int errorCode = data['errorCode'];
          listener.onError(errorCode);
        }else if(name=='onCanceled'){
          listener.onCanceled();
        }else if(name=='onGetTrackFail'){
          listener.onGetTrackFail();
        }else{
          listener.onGetTrackFail();
        }
      } else{

      }
    });
  }

  static Future<void> searchEncryptCard(int timeout, int keyIndex, int encryptFlag, Uint8List? random, int pinpadType, MagCardListener listener) async {
    // TODO conn 回调监听
    await _channel.invokeMethod('searchEncryptCard', {'timeout': timeout,'keyIndex': keyIndex,'encryptFlag': encryptFlag,'random': random,'pinpadType': pinpadType});

    String name = 'searchEncryptCard';
    EventChannel(name).receiveBroadcastStream().listen((data) {
      if (data is Map) {
        String name = data['name'];
        if(name=='onSuccess'){
          Map<String, dynamic> map = Map<String, dynamic>.from(data['magData']);
          print(map);
          MagData magData = new MagData();
          magData.fromJson(map);
          listener.onSuccess(magData);
        }else if(name=='onTimeout'){
          listener.onTimeout();
        }else if(name=='onError'){
          int errorCode = data['errorCode'];
          listener.onError(errorCode);
        }else if(name=='onCanceled'){
          listener.onCanceled();
        }else if(name=='onGetTrackFail'){
          listener.onGetTrackFail();
        }else{
          listener.onGetTrackFail();
        }
      } else{

      }
    });
  }

  static Future<void> searchEncryptCardEx(int timeout, int keyIndex, int encryptFlag, Uint8List? random, int pinpadType, MagCardListener listener) async {
    // TODO conn 回调监听
    await _channel.invokeMethod('searchEncryptCardEx', {'timeout': timeout,'keyIndex': keyIndex,'encryptFlag': encryptFlag,'random': random,'pinpadType': pinpadType});

    String name = 'searchEncryptCardEx';
    EventChannel(name).receiveBroadcastStream().listen((data) {
      if (data is Map) {
        String name = data['name'];
        if(name=='onSuccess'){
          Map<String, dynamic> map = Map<String, dynamic>.from(data['magData']);
          print(map);
          MagData magData = new MagData();
          magData.fromJson(map);
          listener.onSuccess(magData);
        }else if(name=='onTimeout'){
          listener.onTimeout();
        }else if(name=='onError'){
          int errorCode = data['errorCode'];
          listener.onError(errorCode);
        }else if(name=='onCanceled'){
          listener.onCanceled();
        }else if(name=='onGetTrackFail'){
          listener.onGetTrackFail();
        }else{
          listener.onGetTrackFail();
        }
      } else{

      }
    });
  }

  static Future<bool> open() async {
    return await _channel.invokeMethod('open');
  }

  static Future<bool> close() async {
    return await _channel.invokeMethod('close');
  }

  static Future<void> get stopSearch async {
    return await _channel.invokeMethod('stopSearch');
  }

}

typedef void OnSuccess(MagData trackData);
typedef void OnError(int errorCode);
typedef void OnTimeout();
typedef void OnCanceled();
typedef void OnGetTrackFail();

class MagCardListener{
  OnSuccess onSuccess;
  OnError onError;
  OnTimeout onTimeout;
  OnCanceled onCanceled;
  OnGetTrackFail onGetTrackFail;

  MagCardListener(this.onSuccess,this.onError,this.onTimeout,this.onCanceled,this.onGetTrackFail);
}
