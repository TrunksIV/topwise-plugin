import 'dart:async';
import 'dart:convert';
import 'dart:typed_data';
import 'package:flutter/services.dart';
import 'package:json_annotation/json_annotation.dart';

import 'MagData.dart';

class CardReader {
  static const MethodChannel _channel = const MethodChannel('cardreader');

  static Future<void> startSearchCard(bool isMag, bool isIcc, bool isRf, int timeout, OnSearchCardListener listener) async {
    // TODO conn 回调监听
    await _channel.invokeMethod('startSearchCard', {'isMag': isMag,'isIcc': isIcc,'isRf': isRf,'timeout': timeout});

    String name = 'startSearchCard';
    EventChannel(name).receiveBroadcastStream().listen((data) {
      if (data is Map) {
        String name = data['name'];
        if(name=='onFindMagCard'){
          Map<String, dynamic> map = Map<String, dynamic>.from(data['magData']);
          print(map);
          MagData magData = new MagData();
          magData.fromJson(map);
          listener.onFindMagCard(magData);
        }else if(name=='onFindICCard'){
          listener.onFindICCard();
        }else if(name=='onFindRFCard'){
          listener.onFindRFCard();
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

  static Future<void> cancelCheckCard() async {
    return await _channel.invokeMethod('cancelCheckCard');
  }

}

typedef void OnFindMagCard(MagData trackData);
typedef void OnFindICCard();
typedef void OnFindRFCard();
typedef void OnError(int errorCode);
typedef void OnTimeout();
typedef void OnCanceled();
typedef void OnGetTrackFail();

class OnSearchCardListener{
  OnFindMagCard onFindMagCard;
  OnFindICCard onFindICCard;
  OnFindRFCard onFindRFCard;
  OnError onError;
  OnTimeout onTimeout;
  OnCanceled onCanceled;
  OnGetTrackFail onGetTrackFail;

  OnSearchCardListener(this.onFindMagCard,this.onFindICCard,this.onFindRFCard,this.onError,this.onTimeout,this.onCanceled,this.onGetTrackFail);
}
