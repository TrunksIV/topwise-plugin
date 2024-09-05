import 'dart:async';
import 'dart:typed_data';
import 'package:flutter/services.dart';

class CPUCard {
  static const MethodChannel _channel = const MethodChannel('cpucard');

  static Future<bool> open(int cardType) async {
    return await _channel.invokeMethod('open', {'cardType': cardType});
  }

  static Future<void> close() async {
    return await _channel.invokeMethod('close');
  }

  static Future<bool> verifyPwd(int cardType, Uint8List psw) async {
    return await _channel.invokeMethod('verifyPwd', {'cardType': cardType, 'psw': psw});
  }

  static Future<bool> changePassword(int cardType, Uint8List oldpsw, Uint8List newpsw) async {
    return await _channel.invokeMethod('changePassword', {'cardType': cardType, 'oldpsw': oldpsw, 'newpsw': newpsw});
  }

  static Future<Uint8List> read(int cardType, int offset, int length) async {
    return await _channel.invokeMethod('read', {'cardType': cardType, 'offset': offset, 'length': length});
  }

  static Future<bool> write(int cardType, int offset, Uint8List data) async {
    return await _channel.invokeMethod('write', {'cardType': cardType, 'offset': offset, 'data': data});
  }

  static Future<int> openWithATRVerification(int cardType, Uint8List atrData) async {
    return await _channel.invokeMethod('openWithATRVerification', {'cardType': cardType, 'atrData': atrData});
  }

  static Future<bool> writeAt24c(int cardType, int offset, Uint8List data) async {
    return await _channel.invokeMethod('writeAt24c', {'cardType': cardType, 'offset': offset, 'data': data});
  }

  static Future<Uint8List> readAt24c(int cardType, int offset, int len) async {
    return await _channel.invokeMethod('readAt24c', {'cardType': cardType, 'offset': offset, 'len': len});
  }

  static Future<bool> verifyAT88SCPwd(int cardType, int pwdType, int pwdGroup, Uint8List pwd) async {
    return await _channel.invokeMethod('verifyAT88SCPwd', {'cardType': cardType,'pwdType': pwdType,'pwdGroup': pwdGroup,'pwd': pwd});
  }

  static Future<int> getAT88SCPwdCheckNum(int cardType) async {
    return await _channel.invokeMethod('getAT88SCPwdCheckNum', {'cardType': cardType});
  }

  static Future<bool> initAT88SCAuth(int cardType, Uint8List authData) async {
    return await _channel.invokeMethod('initAT88SCAuth', {'cardType': cardType, 'authData': authData});
  }

  static Future<bool> verifyAT88SCAuth(int cardType, Uint8List authData) async {
    return await _channel.invokeMethod('verifyAT88SCAuth', {'cardType': cardType, 'authData': authData});
  }

  static Future<Uint8List> readAT88SCDomainData(int cardType, int zoneNum, int offset, int len) async {
    return await _channel.invokeMethod('readAT88SCDomainData', {'cardType': cardType,'zoneNum': zoneNum,'offset': offset,'len': len});
  }

  static Future<bool> writeAT88SCDomainData(int cardType, int zoneNum, int offset, Uint8List data) async {
    return await _channel.invokeMethod('writeAt24c', {'cardType': cardType,'zoneNum': zoneNum,'offset': offset,'data': data});
  }

  static Future<int> readAT88SCFuseMark(int cardType) async {
    return await _channel.invokeMethod('readAT88SCFuseMark', {'cardType': cardType});
  }

  static Future<bool> writeAT88SCFuseMark(int cardType, int fuseMark) async {
    return await _channel.invokeMethod('writeAT88SCFuseMark', {'cardType': cardType, 'fuseMark': fuseMark});
  }

}

class AT88SCZoneNumber {
  static final int ZONE_NUM_USER0 = 0;
  static final int ZONE_NUM_USER1 = 1;
  static final int ZONE_NUM_USER2 = 2;
  static final int ZONE_NUM_USER3 = 3;
  static final int ZONE_NUM_USER4 = 4;
  static final int ZONE_NUM_USER5 = 5;
  static final int ZONE_NUM_USER6 = 6;
  static final int ZONE_NUM_USER7 = 7;
  static final int ZONE_NUM_CONFIG = 15;
}

class AT88SCCardType {
  static final int AT88SC102 = 21;
  static final int AT88SC1604 = 22;
  static final int AT88SC153 = 23;
  static final int AT88SC1608 = 24;
}

class AT24CCardType {
  static final int AT24C01 = 1;
  static final int AT24C02 = 2;
  static final int AT24C04 = 4;
  static final int AT24C08 = 8;
  static final int AT24C16 = 16;
  static final int AT24C32 = 32;
  static final int AT24C64 = 64;
  static final int AT24C128 = 128;
  static final int AT24C256 = 256;
  static final int AT24C512 = 512;
  static final int AT24C1024 = 1024;
}

class CPUCardType {
  static final int AT24CXX = 1;
  static final int AT45D041 = 2;
  static final int SLE44X8 = 3;
  static final int AT93C46 = 7;
  static final int AT93C46A = 8;
  static final int SLE4404 = 9;
  static final int SLE4406 = 10;
  static final int SLE4418 = 11;
  static final int SLE4428 = 12;
  static final int SLE4432 = 13;
  static final int SLE4442 = 14;
  static final int SSF1101 = 15;
}