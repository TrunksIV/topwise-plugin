
import 'dart:ffi';

import 'package:json_annotation/json_annotation.dart';

import 'CardType.dart';
import 'EmvTransType.dart';

@JsonSerializable()
class EmvTransData extends Object {
  EmvTransData();

  int cardType = CardType.NONE;
  int transType = EmvTransType.GOODS;
  bool isForceOnline = false;
  int amount = 0;
  int otherAmount = 0;

  fromJson(Map<String, dynamic> json) {
    if(json.containsKey('cardType'))
      this.cardType = json['cardType'];
    if(json.containsKey('transType'))
      this.transType = json['transType'];
    if(json.containsKey('isForceOnline'))
      this.isForceOnline = json['isForceOnline'];
    if(json.containsKey('amount'))
      this.amount = json['amount'];
    if(json.containsKey('otherAmount'))
      this.otherAmount = json['otherAmount'];
  }

  Map<String, dynamic> toJson() => <String, dynamic>{
    'cardType': this.cardType,
    'transType': this.transType,
    'isForceOnline': this.isForceOnline,
    'amount': this.amount,
    'otherAmount': this.otherAmount,
  };

}