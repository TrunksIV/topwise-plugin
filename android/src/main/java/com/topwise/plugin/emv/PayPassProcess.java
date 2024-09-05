package com.topwise.plugin.emv;

import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;

import com.topwise.cloudpos.aidl.emv.level2.AidlEmvL2;
import com.topwise.cloudpos.aidl.emv.level2.AidlPaypass;
import com.topwise.cloudpos.aidl.emv.level2.ClssTornLogRecord;
import com.topwise.cloudpos.aidl.emv.level2.EmvCapk;
import com.topwise.cloudpos.aidl.emv.level2.EmvTerminalInfo;
import com.topwise.cloudpos.struct.BytesUtil;
import com.topwise.cloudpos.struct.TlvList;
import com.topwise.plugin.TopUsdkManager;

import java.util.Arrays;

/**
 * @author xukun
 * @version 1.0.0
 * @date 19-9-17
 */

class PayPassProcess extends BasePayProcess {

    private static final String TAG = PayPassProcess.class.getSimpleName();
    private int mSaveLogNum = 0;
    private ClssTornLogRecord[] mTornLogs;

    @Override
    void startPay(Bundle param, TransResultListener listener) {
        Log.d(TAG, "startPaypass");
        try {
            TopUsdkManager.paypass.initialize(1);
            byte[] data = param.getByteArray(PayDataUtil.CardCode.FINAL_SELECT_DATA);
            int len = param.getInt(PayDataUtil.CardCode.FINAL_SELECT_LEN);
            int res = TopUsdkManager.paypass.setFinalSelectData(data, len);
            Log.d(TAG, "setFinalSelectData res: " + res);
            if (!handleResult(res, listener)) {
                return;
            }

            String rid = getCurrentRid();
            byte[] aidData = getCurrentAidData(rid);
            listener.setKernalData(PayDataUtil.KERNTYPE_MC, aidData);

            EmvTerminalInfo terminalInfo = EmvManager.getInstance().getEmvTerminalInfo();
            if (terminalInfo != null) {
                TlvList list = new TlvList();
                if (terminalInfo.getAucIFDSerialNumber().length() == 8) {
                    list.addTlv("9F1E", terminalInfo.getAucIFDSerialNumber().getBytes());
                }
                if (terminalInfo.getUcTerminalType() != -1) {
                    list.addTlv("9F35", new byte[]{terminalInfo.getUcTerminalType()});
                }
                if (terminalInfo.getAucTerminalCountryCode().length == 2) {
                    list.addTlv("9F1A", terminalInfo.getAucTerminalCountryCode());
                }
                if (terminalInfo.getAucTransCurrencyCode().length == 2) {
                    list.addTlv("5F2A", terminalInfo.getAucTransCurrencyCode());
                }
                if (terminalInfo.getAucTerminalCapabilities().length == 3) {
                    list.addTlv("9F33", terminalInfo.getAucTerminalCapabilities());
                }
                if (terminalInfo.getAucAddtionalTerminalCapabilities().length == 5) {
                    list.addTlv("9F40", terminalInfo.getAucAddtionalTerminalCapabilities());
                }
                if (!list.getList().isEmpty()) {
                    byte[] tlvData = list.getBytes();
                    TopUsdkManager.paypass.setTLVDataList(tlvData, tlvData.length);
                }
            }
            listener.nextTransStep(PayDataUtil.CallbackSort.REQUEST_FINAL_AID_SELECT, null);

            byte[] dataBuf = new byte[1];
            res = TopUsdkManager.paypass.gpoProc(dataBuf);
            Log.d(TAG, "gpoProc res: " + res);
            if (!handleResult(res, listener)) {
                return;
            }

            res = TopUsdkManager.paypass.readData();
            Log.d(TAG, "readData res: " + res);
            if (!handleResult(res, listener)) {
                return;
            }

//            //IF Tag 5F28 i.e. Issuer Country Code NOT already available in EMV data
//            //THEN Obtain Tag 5F28 using GET DATA
//            byte[] value5F28 = ClsCardProcess.getInstance().getTlvData("5F28");
//            Log.d(TAG, "5F28 = " + BytesUtil.bytes2HexString(value5F28));
//            if (value5F28 == null || value5F28.length == 0) {
//                Log.d(TAG, "EMV_GetDataFromICC(0x5F28)");
//                TopUsdkManager.emvL2.EMV_GetDataFromICC(0x5F28);
//            }
//
//            //IF Tag 9F42 i.e. Application Currency Code NOT already available in EMV data
//            //THEN Obtain Tag 5F28 using GET DATA
//            byte[] value9F42 = ClsCardProcess.getInstance().getTlvData("9F42");
//            Log.d(TAG, "9F42 = " + BytesUtil.bytes2HexString(value9F42));
//            if (value9F42 == null || value9F42.length == 0) {
//                Log.d(TAG, "EMV_GetDataFromICC(0x9F42)");
//                TopUsdkManager.emvL2.EMV_GetDataFromICC(0x9F42);
//            }

            byte[] ucAcType = new byte[1];
            Log.d(TAG, "ucType: " + dataBuf[0]);
            if (dataBuf[0] == PayDataUtil.CLSS_TRANSPATH_EMV) {
                addCapk(rid);
                Log.d(TAG, "start transProcMChip");
                int[] tornUpdateFlag = {0};
                int tornLogNum[] = {0};
                Log.d(TAG, "mSaveLogNum: " + mSaveLogNum);
                if (mSaveLogNum > 0) {
                    TopUsdkManager.paypass.setTornLogMChip(mTornLogs, mSaveLogNum);
                }
                res = TopUsdkManager.paypass.transProcMChip(ucAcType);
                Log.d(TAG, "end transProcMChip");
                Arrays.fill(tornUpdateFlag, 0);
                Arrays.fill(tornLogNum, 0);
                mTornLogs = new ClssTornLogRecord[5];
                TopUsdkManager.paypass.getTornLogMChip(mTornLogs, tornLogNum, tornUpdateFlag);
                Log.d(TAG, "getTornLogMChip tornUpdateFlag: " + tornUpdateFlag[0]);
                if (tornUpdateFlag[0] == 1) {
                    if (tornLogNum[0] > mSaveLogNum) {
                        listener.onProcessResult(true, true,
                                PayDataUtil.CardCode.TRANS_SECOND_READ);
                    }
                    mSaveLogNum = tornLogNum[0];
                    return;
                }

            } else if (dataBuf[0] == PayDataUtil.CLSS_TRANSPATH_MAG) {
                res = TopUsdkManager.paypass.transProcMag(ucAcType);
            } else {
                res = PayDataUtil.CLSS_TERMINATE;
            }
            Log.d(TAG, "trans proc res: " + res + ";ucAcType: " + ucAcType[0]);

            if (res != PayDataUtil.EMV_OK) {
                Log.d(TAG, "trans fail!!");
                if (res == PayDataUtil.ICC_CMD_ERR) {
                    listener.onProcessResult(true, true, PayDataUtil.CardCode.TRANS_APDU_EXCHANGE_ERROR);
                } else {
                    listener.onProcessResult(true, true, PayDataUtil.CardCode.TRANS_REFUSE);
                }
                return;
            }

            //read card info
            byte[] cardData = new byte[32];
            int[] dataLen = new int[1];
            int res1 = TopUsdkManager.paypass.getTLVDataList(BytesUtil.hexString2Bytes("57"),1, cardData.length, cardData, dataLen);
            if (res1 == PayDataUtil.EMV_OK) {
                byte[] track2 = new byte[dataLen[0]];
                System.arraycopy(cardData, 0, track2, 0, track2.length);
                Log.d(TAG, "track2 data len: " + dataLen[0]);
                String track2Data = BytesUtil.bytes2HexString(track2);
                Log.d(TAG, "track2 data: " + track2Data);
                String cardNo = track2Data.split("D")[0];
                if (cardNo.length() % 2 != 0) {
                    cardNo = cardNo + "F";
                }
                listener.setCardNo(BytesUtil.hexString2Bytes(cardNo));
            }

            //DF8129 tag
            byte[] outComeBuffer = new byte[17];
            int[] bufLen = new int[1];
            TopUsdkManager.paypass.getTLVDataList(new byte[]{(byte) 0xDF, (byte) 0x81, 0x29},
                3, outComeBuffer.length, outComeBuffer, bufLen);
            Log.d(TAG, "bufLen: " + bufLen[0]);
            Log.d(TAG, "outComeBuffer: " + BytesUtil.bytes2HexString(outComeBuffer));

            //DF8129 Byte 4 : CVM
            if ((outComeBuffer[3] & 0xF0) == PayDataUtil.CLSS_OC_ONLINE_PIN) {
                Bundle bundle = new Bundle();
                bundle.putInt(PayDataUtil.CardCode.IMPORT_PIN_TYPE, PayDataUtil.PINTYPE_ONLINE);
                listener.nextTransStep(PayDataUtil.CallbackSort.REQUEST_IMPORT_PIN, bundle);
                if (ClsCardProcess.getInstance().isEndEmv()) {
                    Log.d(TAG, "Cancel Pin input");
                    return;
                }
            }

            //DF8129 Byte 1 : Status
            switch (outComeBuffer[0] & 0xF0) {
                case 0x10:
                    //offline success
                    Log.d(TAG, "TC offline success");
                    listener.onProcessResult(false, true, PayDataUtil.CardCode.TRANS_APPROVAL);
                    break;
                case 0x30:
                    //online success
                    Log.d(TAG, "ARQC online success");
                    listener.onProcessResult(true, false, PayDataUtil.CardCode.TRANS_APPROVAL);
                    break;
                case 0x20:
                    //transaction reject
                    Log.d(TAG, "AAC transaction reject");
                    listener.onProcessResult(false, true, PayDataUtil.CardCode.TRANS_REFUSE);
                    break;
                default:
                    Log.d(TAG, "Transaction termination");
                    listener.onProcessResult(false, true, PayDataUtil.CardCode.TRANS_STOP);
                    break;
            }
        }catch(RemoteException e){
            listener.onFail(e.hashCode());
        }
    }

    @Override
    boolean scriptProcess(boolean onlineRes, String respCode, String icc55, TransResultListener listener ) {
        // 没有脚本处理逻辑
        return false;
    }

    /**
     * 获取当前rid
     *
     * @return 当前应用rid
     */
    private String getCurrentRid() {
        String aid = null;
        try {
            TopUsdkManager.paypass.delAllRevocList();
            TopUsdkManager.paypass.delAllCAPK();
            int[] realLen = new int[1];
            byte[] aucAid = new byte[17];
            int res = TopUsdkManager.paypass.getTLVDataList(new byte[]{0x4F}, 1,
                    aucAid.length, aucAid, realLen);
            Log.d(TAG, "getTLVDataList capk aid res: " + res);
            if (res == PayDataUtil.EMV_OK) {
                if (realLen[0] > 0) {
                    byte[] aidData = new byte[realLen[0]];
                    System.arraycopy(aucAid, 0, aidData, 0, realLen[0]);
                    aid = BytesUtil.bytes2HexString(aidData);
                    Log.d(TAG, "aid len: " + realLen[0] + ";aid: " + aid);
                }
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return aid;
    }

    /**
     * 添加CAPK到交易库
     */
    private void addCapk(String aid) {
        try {
            TopUsdkManager.paypass.delAllRevocList();
            TopUsdkManager.paypass.delAllCAPK();
            byte[] index = new byte[1];
            int[] realLen = new int[1];
            Log.d(TAG, "aid: " + aid);
            int res = TopUsdkManager.paypass.getTLVDataList(new byte[]{(byte) 0x8F},
                    1, 1, index, realLen);
            if (res == PayDataUtil.EMV_OK) {
                Log.d(TAG, "capk index: " + index[0]);
                //import capk
                EmvCapk emvCapk = getCurrentCapk(BytesUtil.hexString2Bytes(aid), index);
                Log.d(TAG, "add capk: " + emvCapk);
                if (emvCapk != null) {
                    res = TopUsdkManager.paypass.addCAPK(emvCapk);
                    Log.d(TAG, "add capk res: " + res);
                }
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
