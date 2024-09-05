import 'package:json_annotation/json_annotation.dart';

import 'align.dart';

@JsonSerializable()
class TextUnit extends Object {

  String text = "";
  int fontSize = 24;
  bool isBold = false;
  PrintAlign align = PrintAlign.LEFT;
  bool isUnderline = false;
  bool isWordWrap = true;
  int lineSpacing = 0;
  int letterSpacing = 0;
  double scaleX = 1.0;
  double scaleY = 1.0;

  TextUnit(this.text,{this.fontSize = 24,this.isBold = false,this.align = PrintAlign.LEFT,this.isUnderline = false,
    this.isWordWrap = true,this.lineSpacing = 0,this.letterSpacing = 0,this.scaleX = 1.0,this.scaleY = 1.0});

  fromMap(Map<String, dynamic> json) {
    if(json.containsKey('text'))
      this.text = json['text'];
    if(json.containsKey('fontSize'))
      this.fontSize = json['fontSize'];
    if(json.containsKey('isBold'))
      this.isBold = json['isBold'];
    if(json.containsKey('align')) {
      int al = json['align'];
      this.align = PrintAlign.LEFT;
      switch(al){
        case 1:
          this.align = PrintAlign.CENTER;
          break;
        case 2:
          this.align = PrintAlign.RIGHT;
          break;
      }
    }
    if(json.containsKey('isUnderline'))
      this.isUnderline = json['isUnderline'];
    if(json.containsKey('isWordWrap'))
      this.isWordWrap = json['isWordWrap'];
    if(json.containsKey('lineSpacing'))
      this.lineSpacing = json['lineSpacing'];
    if(json.containsKey('letterSpacing'))
      this.lineSpacing = json['letterSpacing'];
    if(json.containsKey('scaleX'))
      this.lineSpacing = json['scaleX'];
    if(json.containsKey('scaleY'))
      this.lineSpacing = json['scaleY'];
  }

  Map<String, dynamic> toJson() => <String, dynamic>{
    'text': this.text,
    'fontSize': this.fontSize,
    'isBold': this.isBold,
    'align': this.align.index,
    'isUnderline': this.isUnderline,
    'isWordWrap': this.isWordWrap,
    'lineSpacing': this.lineSpacing,
    'letterSpacing': this.letterSpacing,
    'scaleX': this.scaleX,
    'scaleY': this.scaleY,
  };
}
