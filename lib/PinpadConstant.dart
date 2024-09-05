
  class KCVVerifyMode
  {
  static final int KCV_ENCRYPT_ZERO = 0;
  static final int KCV_ENCRYPT_SN = 1;
  static final int KCV_ENCRYPT_CMAC = 2;
  }

  class StoredRecord
  {
  static final int ACTION_READ = 0;
  static final int ACTION_WRITE = 1;
  }

  class DelPedMode
  {
  static final int MODE_SINGLE = 0;
  static final int MODE_WHOLE = 1;
  static final int MODE_TYPE = 2;
  }

  class BasicAlg
  {
  static final int ALG_NULL = 0;
  static final int ALG_ENCRYPT_DES_CBC = 1;
  static final int ALG_DECRYPT_DES_CBC = 2;
  static final int ALG_ENCRYPT_DES_ECB = 3;
  static final int ALG_DECRYPT_DES_ECB = 4;
  static final int ALG_ENCRYPT_AES_CBC = 5;
  static final int ALG_DECRYPT_AES_CBC = 6;
  static final int ALG_ENCRYPT_AES_ECB = 7;
  static final int ALG_DECRYPT_AES_ECB = 8;
  static final int ALG_ENCRYPT_SM4_CBC = 9;
  static final int ALG_DECRYPT_SM4_CBC = 10;
  static final int ALG_ENCRYPT_SM4_ECB = 11;
  static final int ALG_DECRYPT_SM4_ECB = 12;
  }

  class KeyType
  {
  static final int KEYTYPE_TMK = 0;
  static final int KEYTYPE_FIXED_PEK = 1;
  static final int KEYTYPE_FIXED_MAK = 2;
  static final int KEYTYPE_FIXED_TDK = 3;
  static final int KEYTYPE_PEK = 4;
  static final int KEYTYPE_MAK = 5;
  static final int KEYTYPE_TDK = 6;
  static final int KEYTYPE_FIXED_AESPEK = 7;
  static final int KEYTYPE_TEK = 14;
  static final int KEYTYPE_DKEY = 15;
  static final int KEYTYPE_DUKPT_DES = 22;
  static final int KEYTYPE_DUKPT_DES_IPEK = 22;
  static final int KEYTYPE_DUKPT_DES_BDK = 23;
  static final int KEYTYPE_DUKPT_DES_KSN = 24;
  static final int KEYTYPE_DUKPT_AES_KEY = 25;
  static final int KEYTYPE_DUKPT_AES_IPEK = 26;
  static final int KEYTYPE_DUKPT_AES_BDK = 27;
  static final int KEYTYPE_DUKPT_AES_KSN = 28;
  static final int KEYTYPE_DUKPT_AES_128 = 25;
  static final int KEYTYPE_DUKPT_AES_192 = 29;
  static final int KEYTYPE_DUKPT_AES_256 = 30;
  static final int KEYTYPE_DUKPT_AES_2TDES = 31;
  static final int KEYTYPE_DUKPT_AES_3TDES = 32;
  static final int KEYTYPE_ROOT = 100;
  }

  class MacAlg
  {
  static final int PIN_BLOCK_0 = 13;
  static final int PIN_BLOCK_1 = 14;
  static final int PIN_BLOCK_3 = 16;
  static final int PIN_BLOCK_4 = 17;
  static final int ANSIX99CBC = 18;
  static final int ANSIX99ECB = 19;
  static final int EMV2000 = 20;
  static final int CUP = 21;
  static final int ANSIX919 = 22;
  }

  class WKeyType
  {
  static final int WKEY_TYPE_PIK = 1;
  static final int WKEY_TYPE_TDK = 2;
  static final int WKEY_TYPE_MAK = 3;
  }

  class PinType
  {
  static final int INLINE_TYPE = 0;
  static final int OUTLINE_TYPE = 1;
  }

  class PinpadId
  {
  static final int BUILTIN = 0;
  static final int EXTERNAL = 1;
  }