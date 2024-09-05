import 'dart:async';
import 'dart:ffi';
import 'dart:typed_data';
import 'package:flutter/services.dart';

import 'EmvKernelConfig.dart';
import 'EmvResult.dart';
import 'EmvTerminalInfo.dart';
import 'EmvTransData.dart';
import 'MagData.dart';

class Emv {
  static const MethodChannel _channel = const MethodChannel('emv');

  static Future<String> get getVersion async {
    return await _channel.invokeMethod('getVersion');
  }

  static Future<void> setKernelConfig(EmvKernelConfig config) async {
    return await _channel.invokeMethod('setKernelConfig',{'config':config.getBytes()});
  }

  static Future<EmvKernelConfig> get getKernelConfig async {
    Uint8List data = await _channel.invokeMethod('getKernelConfig');
    EmvKernelConfig config = new EmvKernelConfig();
    config.fromBytes(data);
    return config;
  }

  static Future<void> setEmvTerminalInfo(EmvTerminalInfo info) async {
    return await _channel.invokeMethod('setTerminalInfo',{'info':info.getBytes()});
  }

  static Future<EmvTerminalInfo> get getEmvTerminalInfo async {
    Uint8List? data = await _channel.invokeMethod('getTerminalInfo');
    EmvTerminalInfo info = new EmvTerminalInfo();
    info.fromBytes(data);
    return info;
  }

  static Future<Uint8List?> readKernelData(List<String> taglist) async {
    return await _channel.invokeMethod('readKernelData',{'taglist':taglist});
  }

  static Future<Uint8List?> getTlv(int tag) async {
    return await _channel.invokeMethod('getTlv',{'tag':tag});
  }

  static Future<void> setTlv(int tag, Uint8List value) async {
    return await _channel.invokeMethod('setTlv',{'tag':tag, 'value':value});
  }

  static Future<void> importAmount(int amt, int otherAmt) async {
    return await _channel.invokeMethod('importAmount',{'amt':amt,'otherAmt':otherAmt});
  }

  static Future<bool> importAidSelectRes(int index) async {
    return await _channel.invokeMethod('importAidSelectRes',{'index':index});
  }

  static Future<bool> importFinalAidSelectRes(bool res) async {
    return await _channel.invokeMethod('importFinalAidSelectRes',{'res':res});
  }

  static Future<bool> importConfirmCardInfoRes(bool res) async {
    return await _channel.invokeMethod('importConfirmCardInfoRes',{'res':res});
  }

  static Future<bool> confirmOfflinePinEntry(bool isContinue) async {
    return await _channel.invokeMethod('confirmOfflinePinEntry',{'isContinue':isContinue});
  }

  static Future<bool> importPin(String? pin) async {
    return await _channel.invokeMethod('importPin',{'pin':pin});
  }

  static Future<bool> importUserAuthRes(bool res) async {
    return await _channel.invokeMethod('importUserAuthRes',{'res':res});
  }

  static Future<bool> importMsgConfirmRes(bool confirm) async {
    return await _channel.invokeMethod('importMsgConfirmRes',{'confirm':confirm});
  }

  static Future<bool> importECashTipConfirmRes(bool confirm) async {
    return await _channel.invokeMethod('importECashTipConfirmRes',{'confirm':confirm});
  }

  static Future<bool> importOnlineResp(bool onlineRes,String respCode,String? icc55) async {
    return await _channel.invokeMethod('importOnlineResp',{'onlineRes':onlineRes,'respCode':respCode,'icc55':icc55});
  }

  static Future<void> endEMV() async {
    return await _channel.invokeMethod('endEMV');
  }

  static Future<void> abortEMV() async {
    return await _channel.invokeMethod('abortEMV');
  }

  static Future<void> startEmvProcess(EmvTransData emvTransData, OnEmvProcessListener listener) async {
    // TODO conn 回调监听
    await _channel.invokeMethod('startEmvProcess', {'emvTransData': emvTransData.toJson()});

    String name = 'startEmvProcess';
    EventChannel(name).receiveBroadcastStream().listen((data) {
      if (data is Map) {
        String name = data['name'];
        if(name=='finalAidSelect'){
          listener.finalAidSelect();
        }else if(name=='requestImportAmount'){
          int type = data['type'];
          listener.requestImportAmount(type);
        }else if(name=='requestTipsConfirm'){
          String msg = data['msg'];
          listener.requestTipsConfirm(msg);
        }else if(name=='requestAidSelect'){
          int times = data['times'];
          List<String> aids = data['aids'];
          listener.requestAidSelect(times,aids);
        }else if(name=='requestEcashTipsConfirm'){
          listener.requestEcashTipsConfirm();
        }else if(name=='onConfirmCardInfo'){
          String cardNo = data['cardNo'];
          listener.onConfirmCardInfo(cardNo);
        }else if(name=='onConfirmOfflinePinEntry'){
          int times = data['times'];
          listener.onConfirmOfflinePinEntry(times);
        }else if(name=='requestImportPin'){
          int type = data['type'];
          bool lasttimeFlag = data['lasttimeFlag'];
          int amt = data['amt'];
          listener.requestImportPin(type, lasttimeFlag, amt);
        }else if(name=='requestUserAuth'){
          int certype = data['certype'];
          String certnumber = data['certnumber'];
          listener.requestUserAuth(certype,certnumber);
        }else if(name=='onRequestOnline'){
          listener.onRequestOnline();
        }else if(name=='onReadCardOffLineBalance'){
          String moneyCode = data['moneyCode'];
          String balance = data['balance'];
          String secondMoneyCode = data['secondMoneyCode'];
          String secondBalance = data['secondBalance'];
          listener.onReadCardOffLineBalance(moneyCode,balance,secondMoneyCode,secondBalance);
        }else if(name=='onReadCardTransLog'){
          List<Uint8List>? logs = data['logs'];
          listener.onReadCardTransLog(logs);
        }else if(name=='onReadCardLoadLog'){
          String atc = data['atc'];
          String checkCode = data['checkCode'];
          List<Uint8List>? logs = data['logs'];
          listener.onReadCardLoadLog(atc,checkCode,logs);
        }else if(name=='onTransResult'){
          int result = data['result'];
          listener.onTransResult(result);
        }else if(name=='onError'){
          int errorCode = data['errorCode'];
          listener.onError(errorCode);
        }else if(name=='onEnd'){
          EmvResult? result = data['result'];
          listener.onEnd(result);
        }else{
          listener.onEnd(null);
        }
      } else{

      }
    });
  }

}

typedef void FinalAidSelect();
typedef void RequestImportAmount(int type);
typedef void RequestTipsConfirm(String msg);
typedef void RequestAidSelect(int times, List<String> aids);
typedef void RequestEcashTipsConfirm();
typedef void OnConfirmCardInfo(String cardNo);
typedef void OnConfirmOfflinePinEntry(int times);
typedef void RequestImportPin(int type, bool lasttimeFlag, int amt);
typedef void RequestUserAuth(int certype, String certnumber);
typedef void OnRequestOnline();
typedef void OnReadCardOffLineBalance(String moneyCode, String balance, String secondMoneyCode, String secondBalance);
typedef void OnReadCardTransLog(List<Uint8List>? logs);
typedef void OnReadCardLoadLog(String atc, String checkCode,List<Uint8List>? logs);
typedef void OnTransResult(int result);
typedef void OnError(int errorCode);
typedef void OnEnd(EmvResult? result);

class OnEmvProcessListener{
  FinalAidSelect finalAidSelect;
  RequestImportAmount requestImportAmount;
  RequestTipsConfirm requestTipsConfirm;
  RequestAidSelect requestAidSelect;
  RequestEcashTipsConfirm requestEcashTipsConfirm;
  OnConfirmCardInfo onConfirmCardInfo;
  OnConfirmOfflinePinEntry onConfirmOfflinePinEntry;
  RequestImportPin requestImportPin;
  RequestUserAuth requestUserAuth;
  OnRequestOnline onRequestOnline;
  OnReadCardOffLineBalance onReadCardOffLineBalance;
  OnReadCardTransLog onReadCardTransLog;
  OnReadCardLoadLog onReadCardLoadLog;
  OnTransResult onTransResult;
  OnError onError;
  OnEnd onEnd;

  OnEmvProcessListener(this.finalAidSelect,this.requestImportAmount,this.requestTipsConfirm,this.requestAidSelect,this.requestEcashTipsConfirm,
      this.onConfirmCardInfo,this.onConfirmOfflinePinEntry,this.requestImportPin,this.requestUserAuth,this.onRequestOnline,
      this.onReadCardOffLineBalance,this.onReadCardTransLog,this.onReadCardLoadLog,this.onTransResult,this.onError,this.onEnd);
}
