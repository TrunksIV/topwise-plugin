import 'dart:convert';
import 'dart:ffi';
import 'dart:typed_data';

import 'package:topwiseplugin/type_util.dart';

class EmvTerminalInfo extends Object {
  EmvTerminalInfo();

  int unTerminalFloorLimit = 0;
  int unThresholdValue = 0;
  String aucTerminalID = "";
  String aucIFDSerialNumber = "";
  Uint8List aucTerminalCountryCode = new Uint8List(2);
  String aucMerchantID = "";
  Uint8List aucMerchantCategoryCode = new Uint8List(2);
  Uint8List aucMerchantNameLocation = new Uint8List(0);
  Uint8List aucTransCurrencyCode = new Uint8List(2);
  int ucTransCurrencyExp = 0;
  Uint8List aucTransRefCurrencyCode = new Uint8List(2);
  int ucTransRefCurrencyExp = 0;
  int ucTerminalEntryMode = 0;
  String aucTerminalAcquireID = "";
  Uint8List aucAppVersion = new Uint8List(2);
  Uint8List aucDefaultDDOL = new Uint8List(0);
  Uint8List aucDefaultTDOL = new Uint8List(0);
  Uint8List aucTACDenial = new Uint8List(5);
  Uint8List aucTACOnline = new Uint8List(5);
  Uint8List aucTACDefault = new Uint8List(5);
  int ucTerminalType = 0;
  Uint8List aucTerminalCapabilities = new Uint8List(3);
  Uint8List aucAddtionalTerminalCapabilities = new Uint8List(5);
  int ucTargetPercentage = 0;
  int ucMaxTargetPercentage = 0;
  int ucAccountType = 0;
  int ucIssuerCodeTableIndex = 0;
  Uint8List aucRFU = new Uint8List(30);

  fromBytes(Uint8List? data){
    if(data==null)
      return;
    if(data.length>=858){
      unTerminalFloorLimit = uint8List2Int(data.sublist(0, 4), false);
      unThresholdValue = uint8List2Int(data.sublist(4, 8), false);
      aucTerminalID = utf8.decode(data.sublist(8, 16));
      aucIFDSerialNumber = utf8.decode(data.sublist(17, 25));
      aucTerminalCountryCode = data.sublist(26, 28);
      aucMerchantID = utf8.decode(data.sublist(28, 43));
      aucMerchantCategoryCode = data.sublist(44, 46);
      aucMerchantNameLocation = data.sublist(46, 301);
      aucTransCurrencyCode = data.sublist(302, 304);
      ucTransCurrencyExp = data[304];
      aucTransRefCurrencyCode = data.sublist(305, 307);
      ucTransRefCurrencyExp = data[307];
      ucTerminalEntryMode = data[308];
      aucTerminalAcquireID = utf8.decode(data.sublist(309, 315));
      aucAppVersion = data.sublist(316, 318);
      int ucDefaultDDOLLen = data[573] & 0xFF;
      aucDefaultDDOL = data.sublist(318, 318+ucDefaultDDOLLen);
      int ucDefaultTDOLLen = data[829] & 0xFF;
      aucDefaultTDOL = data.sublist(574, 574+ucDefaultTDOLLen);
      aucTACDenial = data.sublist(830, 835);
      aucTACOnline = data.sublist(835, 840);
      aucTACDefault = data.sublist(840, 845);
      ucTerminalType = data[845];
      aucTerminalCapabilities = data.sublist(846, 849);
      aucAddtionalTerminalCapabilities = data.sublist(849, 854);
      ucTargetPercentage = data[854];
      ucMaxTargetPercentage = data[855];
      ucAccountType = data[856];
      ucIssuerCodeTableIndex = data[857];
      int length = data.length >= 888 ? 30 : data.length - 858;
      aucRFU = data.sublist(858, 858+length);
    }
  }

  Uint8List? getBytes(){
    List<int> data = [];
    data.addAll(int2Uint8List(unTerminalFloorLimit, false));
    data.addAll(int2Uint8List(unThresholdValue, false));
    data.addAll(uint8Listfill(utf8.encoder.convert(aucTerminalID),8,0));
    data.add(0);
    data.addAll(uint8Listfill(utf8.encoder.convert(aucIFDSerialNumber),8,0));
    data.add(0);
    data.addAll(uint8Listfill(aucTerminalCountryCode,2,0));
    data.addAll(uint8Listfill(utf8.encoder.convert(aucMerchantID),15,0));
    data.add(0);
    data.addAll(uint8Listfill(aucMerchantCategoryCode,2,0));
    data.addAll(uint8Listfill(aucMerchantNameLocation,255,0));
    data.add(0);
    data.addAll(uint8Listfill(aucTransCurrencyCode,2,0));
    data.add(ucTransCurrencyExp);
    data.addAll(uint8Listfill(aucTransRefCurrencyCode,2,0));
    data.add(ucTransRefCurrencyExp);
    data.add(ucTerminalEntryMode);
    data.addAll(uint8Listfill(utf8.encoder.convert(aucTerminalAcquireID),6,0));
    data.add(0);
    data.addAll(uint8Listfill(aucAppVersion,2,0));
    data.addAll(uint8Listfill(aucDefaultDDOL,255,0));
    data.add(aucDefaultDDOL.length);
    data.addAll(uint8Listfill(aucDefaultTDOL,255,0));
    data.add(aucDefaultTDOL.length);
    data.addAll(uint8Listfill(aucTACDenial,5,0));
    data.addAll(uint8Listfill(aucTACOnline,5,0));
    data.addAll(uint8Listfill(aucTACDefault,5,0));
    data.add(ucTerminalType);
    data.addAll(uint8Listfill(aucTerminalCapabilities,3,0));
    data.addAll(uint8Listfill(aucAddtionalTerminalCapabilities,5,0));
    data.add(ucTargetPercentage);
    data.add(ucMaxTargetPercentage);
    data.add(ucAccountType);
    data.add(ucIssuerCodeTableIndex);
    data.addAll(uint8Listfill(aucRFU,30,0));
    return Uint8List.fromList(data);
  }
}