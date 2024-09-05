import 'dart:ffi';
import 'dart:typed_data';

class EmvKernelConfig extends Object {
  EmvKernelConfig();

  int bPSE = 0;
  int bCardHolderConfirm = 0;
  int bPreferredDisplayOrder = 0;
  int bLanguateSelect = 0;
  int bRevocationOfIssuerPublicKey = 0;
  int bDefaultDDOL = 0;
  int bBypassPINEntry = 0;
  int bSubBypassPINEntry = 0;
  int bGetdataForPINTryCounter = 0;
  int bFloorLimitCheck = 0;
  int bRandomTransSelection = 0;
  int bVelocityCheck = 0;
  int bTransactionLog = 0;
  int bExceptionFile = 0;
  int bTerminalActionCode = 0;
  int bDefaultActionCodeMethod = 0;
  int bTACIACDefaultSkipedWhenUnableToGoOnline = 0;
  int bCDAFailureDetectedPriorTerminalActionAnalysis = 0;
  int bCDAMethod = 0;
  int bForcedOnline = 0;
  int bForcedAcceptance = 0;
  int bAdvices = 0;
  int bIssuerReferral = 0;
  int bBatchDataCapture = 0;
  int bOnlineDataCapture = 0;
  int bDefaultTDOL = 0;
  int bTerminalSupportAccountTypeSelection = 0;
  int bPCIPINEntry = 0;
  Uint8List aucRFU = new Uint8List(12);

  fromBytes(Uint8List data){
    bPSE = data[0];
    bCardHolderConfirm = data[1];
    bPreferredDisplayOrder = data[2];
    bLanguateSelect = data[3];
    bRevocationOfIssuerPublicKey = data[4];
    bDefaultDDOL = data[5];
    bBypassPINEntry = data[6];
    bSubBypassPINEntry = data[7];
    bGetdataForPINTryCounter = data[8];
    bFloorLimitCheck = data[9];
    bRandomTransSelection = data[10];
    bVelocityCheck = data[11];
    bTransactionLog = data[12];
    bExceptionFile = data[13];
    bTerminalActionCode = data[14];
    bDefaultActionCodeMethod = data[15];
    bTACIACDefaultSkipedWhenUnableToGoOnline = data[16];
    bCDAFailureDetectedPriorTerminalActionAnalysis = data[17];
    bCDAMethod = data[18];
    bForcedOnline = data[19];
    bForcedAcceptance = data[20];
    bAdvices = data[21];
    bIssuerReferral = data[22];
    bBatchDataCapture = data[23];
    bOnlineDataCapture = data[24];
    bDefaultTDOL = data[25];
    bTerminalSupportAccountTypeSelection = data[26];
    bPCIPINEntry = data[27];
    aucRFU = data.sublist(28);
  }

  Uint8List? getBytes(){
    List<int> data = [];
    data.add(bPSE);
    data.add(bCardHolderConfirm);
    data.add(bPreferredDisplayOrder);
    data.add(bLanguateSelect);
    data.add(bRevocationOfIssuerPublicKey);
    data.add(bDefaultDDOL);
    data.add(bBypassPINEntry);
    data.add(bSubBypassPINEntry);
    data.add(bGetdataForPINTryCounter);
    data.add(bFloorLimitCheck);
    data.add(bRandomTransSelection);
    data.add(bVelocityCheck);
    data.add(bTransactionLog);
    data.add(bExceptionFile);
    data.add(bTerminalActionCode);
    data.add(bDefaultActionCodeMethod);
    data.add(bTACIACDefaultSkipedWhenUnableToGoOnline);
    data.add(bCDAFailureDetectedPriorTerminalActionAnalysis);
    data.add(bCDAMethod);
    data.add(bForcedOnline);
    data.add(bForcedAcceptance);
    data.add(bAdvices);
    data.add(bIssuerReferral);
    data.add(bBatchDataCapture);
    data.add(bOnlineDataCapture);
    data.add(bDefaultTDOL);
    data.add(bTerminalSupportAccountTypeSelection);
    data.add(bPCIPINEntry);
    data.addAll(aucRFU);
    return Uint8List.fromList(data);
  }
}