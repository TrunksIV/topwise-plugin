import 'package:json_annotation/json_annotation.dart';

@JsonSerializable()
class EmvResult extends Object {
  EmvResult();

  int? ret;
  Object? data;

  fromJson(Map<String, dynamic> json) {
    if(json.containsKey('ret'))
      this.ret = json['ret'];
    if(json.containsKey('data'))
      this.data = json['data'];
  }

  Map<String, dynamic> toJson() => <String, dynamic>{
    'ret': this.ret,
    'data': this.data,
  };

}