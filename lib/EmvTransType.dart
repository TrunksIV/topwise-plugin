import 'dart:ffi';

class EmvTransType {
  static final int GOODS = 0x00;
  static final int SERVICE = 0x00;
  static final int CASH = 0x01;
  static final int AUTH = 0x00;
  static final int CASHBACK = 0x09;
  static final int REFUND = 0x20;
  static final int DEPOSIT = 0x21;
  static final int INQUIRY = 0x31;
  static final int TRANSFER = 0x40;
  static final int PAYMENT = 0x50;
  static final int ADMIN = 0x60;
}