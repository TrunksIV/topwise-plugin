package com.topwise.plugin.emv;

import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;

import com.topwise.cloudpos.aidl.emv.level2.AidlDpas;
import com.topwise.cloudpos.aidl.emv.level2.EmvCapk;
import com.topwise.cloudpos.aidl.emv.level2.EmvTerminalInfo;
import com.topwise.cloudpos.aidl.emv.level2.PreProcResult;
import com.topwise.cloudpos.struct.BytesUtil;
import com.topwise.cloudpos.struct.TlvList;
import com.topwise.plugin.TopUsdkManager;

import java.util.Arrays;

/**
 * dpas(美运卡)交易
 * Created by topwise on 20-7-15.
 */
class DpasProcess extends BasePayProcess {

    private static final String TAG = DpasProcess.class.getSimpleName();

    @Override
    void startPay(Bundle param, TransResultListener listener) {
        Log.d(TAG, "startdpas");
        try {
            PreProcResult preProcResult = param.getParcelable(PayDataUtil.CardCode.PREPROC_RESULT);
            if (preProcResult == null) {
                Log.d(TAG, "preProcResult = null");
                listener.onProcessResult(false, true, PayDataUtil.CardCode.TRANS_STOP);
                return;
            }

            TopUsdkManager.dpas.initialize();
            byte[] data = param.getByteArray(PayDataUtil.CardCode.FINAL_SELECT_DATA);
            int len = param.getInt(PayDataUtil.CardCode.FINAL_SELECT_LEN);
            int res = TopUsdkManager.dpas.setFinalSelectData(data, len);
            Log.d(TAG, "setFinalSelectData res: " + res);
            if (!handleResult(res, listener)) {
                return;
            }
            String rid = getCurrentRid();
            byte[] aidData = getCurrentAidData(rid);
            listener.setKernalData(PayDataUtil.KERNTYPE_DPAS, aidData);

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
                    TopUsdkManager.dpas.setTLVDataList(tlvData, tlvData.length);
                }
            }
            listener.nextTransStep(PayDataUtil.CallbackSort.REQUEST_FINAL_AID_SELECT, null);

            byte[] ZIP_AID = {(byte)0xA0, 0x00, 0x00, 0x03, 0x24};
            if (Arrays.equals(preProcResult.getAucAID(), ZIP_AID)) {
                Log.d(TAG, "ZIP Transaction not update TTQ");
            } else {
                //Set TTQ
                byte[] getTTQ = preProcResult.getAucReaderTTQ();
                if (getTTQ == null) {
                    Log.d(TAG, "getAucReaderTTQ = null");
                    listener.onProcessResult(false, true, PayDataUtil.CardCode.TRANS_STOP);
                    return;
                }
                Log.d(TAG, "getAucReaderTTQ: " + BytesUtil.bytes2HexString(getTTQ));
                byte[] TTQTlv = new byte[7];
                TTQTlv[0] = (byte)0x9F;
                TTQTlv[1] = (byte)0x66;
                TTQTlv[2] = (byte)0x04;
                System.arraycopy(getTTQ, 0, TTQTlv, 3, 4);
                Log.d(TAG, "Set TTQTlv: " + BytesUtil.bytes2HexString(TTQTlv));
                TopUsdkManager.dpas.setTLVDataList(TTQTlv, 7);
            }

            if (preProcResult.getUcRdCLFLmtExceed() == 1 ||
                preProcResult.getUcTermFLmtExceed() == 1) {
                byte[] TVRTlv = {(byte)0x95, 0x05, 0x00, 0x00, 0x00, (byte)0x80, 0x00};
                Log.d(TAG, "Set TVRTlv: " + BytesUtil.bytes2HexString(TVRTlv));
                TopUsdkManager.dpas.setTLVDataList(TVRTlv, 7);
            }

            byte[] dataBuf = new byte[1];
            res = TopUsdkManager.dpas.gpoProc(dataBuf);
            Log.d(TAG, "gpoProc res: " + res);
            if (!handleResult(res, listener)) {
                return;
            }

            res = TopUsdkManager.dpas.readData();
            Log.d(TAG, "readData res: " + res);
            if (!handleResult(res, listener)) {
                return;
            }

            Log.d(TAG, "ucType: " + dataBuf[0]);
            if (dataBuf[0] == PayDataUtil.CLSS_TRANSPATH_EMV) {
                addCapk(rid);
            } else if (dataBuf[0] == PayDataUtil.CLSS_TRANSPATH_MAG) {
                Log.d(TAG, "ucType == PayDataUtil.CLSS_TRANSPATH_MAG");
            } else {
                res = PayDataUtil.CLSS_TERMINATE;
            }
            if (!handleResult(res, listener)) {
                return;
            }
            //read card info
            byte[] cardData = new byte[32];
            int[] dataLen = new int[1];
            int res1 = TopUsdkManager.dpas.getTLVDataList(BytesUtil.hexString2Bytes("57"),1, cardData.length, cardData, dataLen);
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

            res = TopUsdkManager.dpas.transProc(0);
            Log.d(TAG, "startTrans res: " + res);
            if (!handleResult(res, listener)) {
                return;
            }

            //DF8129 tag
            byte[] outComeBuffer = new byte[17];
            int[] bufLen = new int[1];
            res = TopUsdkManager.dpas.getTLVDataList(new byte[]{(byte) 0xDF, (byte) 0x81, 0x29},
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
            e.printStackTrace();
        }

    }

    @Override
    boolean scriptProcess(boolean onlineRes, String respCode, String icc55, TransResultListener listener) {
        // AE卡没有脚本处理
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
            TopUsdkManager.dpas.delAllRevocList();
            TopUsdkManager.dpas.delAllCAPK();
            int[] realLen = new int[1];
            byte[] aucAid = new byte[17];
            int res = TopUsdkManager.dpas.getTLVDataList(new byte[]{0x4F}, 1,aucAid.length, aucAid, realLen);
            Log.d(TAG, "getTLVDataList capk aid res: " + res);
            if (res == PayDataUtil.EMV_OK) {
                if (realLen[0] > 0) {
                    byte[] aidData = new byte[realLen[0]];
                    System.arraycopy(aucAid, 0, aidData, 0, realLen[0]);
                    aid = BytesUtil.bytes2HexString(aidData);
                    Log.d(TAG, "aid len: " + realLen[0] + ";aid: " + aid);
                }
            }
        }catch(RemoteException e){
            e.printStackTrace();
        }
        return aid;
    }

    /**
     * 添加CAPK到交易库
     */
    private void addCapk(String aid) {
        try{
            TopUsdkManager.dpas.delAllRevocList();
            TopUsdkManager.dpas.delAllCAPK();
            byte[] index = new byte[1];
            int[] realLen = new int[1];
            Log.d(TAG, "aid: " + aid);
            int res = TopUsdkManager.dpas.getTLVDataList(new byte[]{(byte) 0x8F},1, 1, index, realLen);
            if (res == PayDataUtil.EMV_OK) {
                Log.d(TAG, "capk index: " + index[0]);
                //import capk
                EmvCapk emvCapk = getCurrentCapk(BytesUtil.hexString2Bytes(aid), index);
                Log.d(TAG, "add capk: " + emvCapk);
                if (emvCapk != null) {
                    res = TopUsdkManager.dpas.addCAPK(emvCapk);
                    Log.d(TAG, "add capk res: " + res);
                }
            }
        }catch(RemoteException e){
            e.printStackTrace();
        }
    }
}
