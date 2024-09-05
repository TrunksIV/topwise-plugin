import 'dart:async';
import 'package:flutter/services.dart';
import 'ImageUnit.dart';
import 'TextUnit.dart';

class Printer {
  static const MethodChannel _channel = const MethodChannel('printer');

  static Future<void> init() async {
    await _channel.invokeMethod('init');
  }

  static Future<void> clear() async {
    await _channel.invokeMethod('clear');
  }

  static Future<void> startPrint(PrintListener listener) async {
    // TODO conn 回调监听
    await _channel.invokeMethod('startPrint');

    String name = 'startPrint';
    EventChannel(name).receiveBroadcastStream().listen((data) {
      if (data is int) {
        listener.onError(data);
      } else {
        listener.onSuccess();
      }
    });
  }

  static Future<void> addText(TextUnit textUnit) async {
    await _channel.invokeMethod('addText', {'textUnit': textUnit.toJson()});
  }

  static Future<void> addText2(int weight1, TextUnit textUnit1, int weight2, TextUnit textUnit2) async {
    await _channel.invokeMethod('addText2',
        {'weight1': weight1, 'textUnit1': textUnit1.toJson(), 'weight2': weight2, 'textUnit2': textUnit2.toJson()});
  }

  static Future<void> addText3(
      int weight1, TextUnit textUnit1, int weight2, TextUnit textUnit2, int weight3, TextUnit textUnit3) async {
    await _channel.invokeMethod('addText3', {
      'weight1': weight1,
      'textUnit1': textUnit1.toJson(),
      'weight2': weight2,
      'textUnit2': textUnit2.toJson(),
      'weight3': weight3,
      'textUnit3': textUnit3.toJson()
    });
  }

  static Future<void> addImage(ImageUnit imageUnit) async {
    //await _channel.invokeMethod('addImage', imageUnit.toJson());
    await _channel.invokeMethod('addImage', {'imageUnit':imageUnit.toJson()});
  }

  static Future<void> addTextImage(List<TextUnit> textUnits, ImageUnit imageUnit) async {
    List<Map<String, dynamic>> list = [];
    for (int i = 0; i < textUnits.length; i++) {
      list.add(textUnits.elementAt(i).toJson());
    }
    await _channel.invokeMethod('addTextImage', {'textUnits': list, 'imageUnit': imageUnit.toJson()});
  }

  static Future<void> addImageText(ImageUnit imageUnit, List<TextUnit> textUnits) async {
    List<Map<String, dynamic>> list = [];
    for (int i = 0; i < textUnits.length; i++) {
      list.add(textUnits.elementAt(i).toJson());
    }
    await _channel.invokeMethod('addImageText', {'textUnits': list, 'imageUnit': imageUnit.toJson()});
  }
}

typedef void OnSuccess();
typedef void OnError(int errorCode);

class PrintListener {
  OnSuccess onSuccess;
  OnError onError;

  PrintListener(this.onSuccess, this.onError);
}
