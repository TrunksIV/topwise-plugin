import 'dart:async';
import 'package:flutter/services.dart';

class Scanner {
  static const MethodChannel _channel = const MethodChannel('scanner');

  static Future<void> scanCode(int cameraId,int timeout,String title,String reminder,String amt,ScanCodeListener listener) async {
    // TODO conn 回调监听
    await _channel.invokeMethod('scanCode', {'cameraId': cameraId,'timeout': timeout,'title': title,'reminder': reminder,'amt': amt});

    String name = 'scanCode';
    EventChannel(name).receiveBroadcastStream().listen((data) {
      if (data is Map) {
        String name = data['name'];
        if(name=='onResult'){
          String result = data['result'];
          print(result);
          listener.onResult(result);
        }else if(name=='onTimeout'){
          listener.onTimeout();
        }else if(name=='onError'){
          int errorCode = data['errorCode'];
          listener.onError(errorCode);
        }else if(name=='onCancel'){
          listener.onCancel();
        }else{
          listener.onTimeout();
        }
      } else{

      }
    });
  }

  static Future<bool> stopScan() async {
    return await _channel.invokeMethod('stopScan');
  }

}

typedef void OnResult(String result);
typedef void OnCancel();
typedef void OnError(int errorCode);
typedef void OnTimeout();

class ScanCodeListener{
  OnResult onResult;
  OnCancel onCancel;
  OnError onError;
  OnTimeout onTimeout;

  ScanCodeListener(this.onResult,this.onCancel,this.onError,this.onTimeout);
}
