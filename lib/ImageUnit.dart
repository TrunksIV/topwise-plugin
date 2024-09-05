
import 'package:json_annotation/json_annotation.dart';
import 'package:topwiseplugin/ImageType.dart';

import 'align.dart';

@JsonSerializable()
class ImageUnit extends Object {

  String content = "";
  ImageType type = ImageType.QRCODE;
  int left = 0;
  int top = 0;
  int width = 0;
  int height = 0;
  PrintAlign align = PrintAlign.LEFT;

  ImageUnit(this.content,{this.type = ImageType.QRCODE, this.left = 0, this.top = 0, this.width = 0, this.height = 0, align = PrintAlign.LEFT});

  fromMap(Map<String, dynamic> json) {
    if(json.containsKey('content'))
      this.content = json['content'];
    if(json.containsKey('type')) {
      int al = json['type'];
      this.type = ImageType.QRCODE;
      switch(al){
        case 0:
          this.type = ImageType.ASSET;
          break;
        case 1:
          this.type = ImageType.SDCARD;
          break;
        case 3:
          this.type = ImageType.BARCODE;
          break;
        case 4:
          this.type = ImageType.NET;
          break;
      }
    }
    if(json.containsKey('left'))
      this.left = json['left'];
    if(json.containsKey('top'))
      this.top = json['top'];
    if(json.containsKey('width'))
      this.width = json['width'];
    if(json.containsKey('height'))
      this.height = json['height'];
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
  }

  Map<String, dynamic> toJson() => <String, dynamic>{
    'content': this.content,
    'type': this.type.index,
    'left': this.left,
    'top': this.top,
    'width': this.width,
    'height': this.height,
    'align': this.align.index,
  };
}
