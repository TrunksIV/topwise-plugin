import 'dart:typed_data';

T? getEnumFromString<T>(Iterable<T?> values, String? value) {
  return values.firstWhere((type) => type.toString().split(".").last == value,
      orElse: () => null);
}

String enumToString<T>(T enumValue) {
  return enumValue.toString().split('.').last;
}


Uint8List? hex2Bytes(String? value,bool leanRight) {
  if(value==null){
    return null;
  }
  if(value.length==0){
    return Uint8List(0);
  }
  if(value.length%2==1){
    if(leanRight) {
      value = '0' + value;
    } else{
      value += '0';
    }
  }
  int length = (value.length/2).toInt();
  Uint8List res = Uint8List(length);
  for(int i=0;i<length;i++){
    String hex = value.substring(i*2,i*2+2);
    res[i] = int.parse(hex,radix: 16);
  }
  return res;
}

String? uint8ListToHexString(Uint8List? value) {
  if(value==null){
    return null;
  }
  if(value.length==0){
    return "";
  }
  String res = "";
  for(int i=0;i<value.length;i++){
    String hex = value[i].toRadixString(16);
    res += hex.length<2?'0$hex':hex;
  }
  return res.toUpperCase();
}

Uint8List? hexStringToUint8List(String? value) {
  if(value==null){
    return null;
  }
  if(value.length==0){
    return Uint8List(0);
  }
  if(value.length%2==1){
    value += '0';
  }
  int length = (value.length/2).toInt();
  Uint8List res = Uint8List(length);
  for(int i=0;i<length;i++){
    String hex = value.substring(i*2,i*2+2);
    res[i] = int.parse(hex,radix: 16);
  }
  return res;
}

Uint8List int2Uint8List(int n, bool highFirst) {
  Uint8List data = new Uint8List(4);

  for(int i = 0; i < 4; ++i) {
    if (highFirst) {
      data[i] = (n >> 24 - i * 8 & 255);
    } else {
      data[3 - i] = (n >> 24 - i * 8 & 255);
    }
  }

  return data;
}

int uint8List2Int(Uint8List b, bool highFirst) {
  int value = 0;

  for(int i = 0; i < 4; ++i) {
    int shift;
    if (highFirst) {
      shift = (3 - i) * 8;
    } else {
      shift = i * 8;
    }
    value += (b[i] & 255) << shift;
  }

  return value;
}

Uint8List uint8Listfill(Uint8List data,int len, int c) {
  int addlen = 0;
  List<int> ret = [];
  if(data==null){
    addlen = len;
  }else if(data.length<len){
    addlen = len-data.length;
    ret.addAll(data);
  }else{
    return data.sublist(0,len);
  }
  for(int i=0;i<addlen;i++){
    ret.add(c);
  }
  return Uint8List.fromList(ret);
}