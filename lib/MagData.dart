import 'package:json_annotation/json_annotation.dart';

@JsonSerializable()
class MagData extends Object {
  MagData();

  String serviceCode = '';
  String expiryDate = '';
  String formatTrack = '';
  String cardno = '';
  String track3 = '';
  String track2 = '';
  String track1 = '';

  fromJson(Map<String, dynamic> json) {
    if(json.containsKey('serviceCode'))
      this.serviceCode = json['serviceCode'];
    if(json.containsKey('expiryDate'))
      this.expiryDate = json['expiryDate'];
    if(json.containsKey('formatTrack'))
      this.formatTrack = json['formatTrack'];
    if(json.containsKey('cardno'))
      this.cardno = json['cardno'];
    if(json.containsKey('track3'))
      this.track3 = json['track3'];
    if(json.containsKey('track2'))
      this.track2 = json['track2'];
    if(json.containsKey('track1'))
      this.track1 = json['track1'];
  }

  Map<String, dynamic> toJson() => <String, dynamic>{
    'serviceCode': this.serviceCode,
    'expiryDate': this.expiryDate,
    'formatTrack': this.formatTrack,
    'cardno': this.cardno,
    'track3': this.track3,
    'track2': this.track2,
    'track1': this.track1,
  };
}
