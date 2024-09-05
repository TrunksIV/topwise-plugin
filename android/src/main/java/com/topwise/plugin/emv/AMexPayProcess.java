package com.topwise.plugin.emv;

import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import com.topwise.cloudpos.aidl.emv.level2.AidlAmex;
import com.topwise.cloudpos.aidl.emv.level2.EmvCapk;
import com.topwise.cloudpos.aidl.emv.level2.EmvTerminalInfo;
import com.topwise.cloudpos.aidl.emv.level2.PreProcResult;
import com.topwise.cloudpos.aidl.emv.level2.TransParam;
import com.topwise.cloudpos.struct.BytesUtil;
import com.topwise.cloudpos.struct.TlvList;
import com.topwise.plugin.TopUsdkManager;

/**
 * AMex(美运卡)交易
 * Created by topwise on 20-7-15.
 */
class AMexPayProcess extends BasePayProcess {

    private static final String TAG = AMexPayProcess.class.getSimpleName();

    @Override
    void startPay(Bundle param, TransResultListener listener) {
        Log.d(TAG, "startAMex");
        try {
            TopUsdkManager.amex.initialize();
            byte[] data = param.getByteArray(PayDataUtil.CardCode.FINAL_SELECT_DATA);
            int len = param.getInt(PayDataUtil.CardCode.FINAL_SELECT_LEN);
            int res = TopUsdkManager.amex.setFinalSelectData(data, len);
            Log.d(TAG, "setFinalSelectData res: " + res);
            if (!handleResult(res, listener)) {
                return;
            }
            String rid = getCurrentRid();
            byte[] aidData = getCurrentAidData(rid);
            listener.setKernalData(PayDataUtil.KERNTYPE_AMEX, aidData);

            /*******ljz add 20200709 *********/
            byte[] aucECRC = {(byte) 0x9F, 0x6E, 0x04, (byte)0x9C, (byte)0xA0, 0x00, 0x03};
            TopUsdkManager.amex.setTLVDataList(aucECRC, aucECRC.length);

            PreProcResult preProcResult = param.getParcelable(PayDataUtil.CardCode.PREPROC_RESULT);
            if (preProcResult != null) {
                String buffer = BytesUtil.bytes2HexString(preProcResult.getAucReaderTTQ());
                if (buffer.contains("00000000")) {
                    preProcResult.setAucReaderTTQ(BytesUtil.hexString2Bytes("3600C000"));
                }
            }
            TransParam transParam = param.getParcelable(PayDataUtil.CardCode.TRANS_PARAM);
            res = TopUsdkManager.amex.setTransData(transParam, preProcResult);
            Log.d(TAG, "setTransData res: " + res);

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
                    TopUsdkManager.amex.setTLVDataList(tlvData, tlvData.length);
                }
            }
            listener.nextTransStep(PayDataUtil.CallbackSort.REQUEST_FINAL_AID_SELECT, null);

            byte[] dataBuf = new byte[1];
            res = TopUsdkManager.amex.gpoProc(dataBuf);
            Log.d(TAG, "gpoProc res: " + res);
            if (!handleResult(res, listener)) {
                return;
            }

            res = TopUsdkManager.amex.readData();
            Log.d(TAG, "readData res: " + res);
            if (!handleResult(res, listener)) {
                return;
            }

            Log.d(TAG, "ucType: " + dataBuf[0]);
            if (dataBuf[0] == PayDataUtil.CLSS_TRANSPATH_EMV) {
                addCapk(rid);
                res = TopUsdkManager.amex.cardAuth();
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
            int res1 = TopUsdkManager.amex.getTLVDataList(BytesUtil.hexString2Bytes("57"),1, cardData.length, cardData, dataLen);
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

            byte[] ucAcType = new byte[1];
            byte[] ucAdviceFlag = new byte[1];
            byte[] ucDelayAuth = new byte[1];
            res = TopUsdkManager.amex.startTrans((byte) 0, ucAdviceFlag, ucAcType, ucDelayAuth);
            Log.d(TAG, "startTrans res: " + res);
            if (!handleResult(res, listener)) {
                return;
            }

            //DF8129 tag  --is need pwd
            byte[] outComeBuffer = new byte[17];
            int[] bufLen = new int[1];
            res = TopUsdkManager.amex.getTLVDataList(new byte[]{(byte) 0xDF, (byte) 0x81, 0x29},
                    3, outComeBuffer.length, outComeBuffer, bufLen);
            if (res == PayDataUtil.EMV_OK) {
                Log.d(TAG, "bufLen: " + bufLen[0]);
                byte[] outData = new byte[bufLen[0]];
                System.arraycopy(outComeBuffer, 0, outData, 0, bufLen[0]);
                Log.d(TAG, "real outcome data: " + BytesUtil.bytes2HexString(outData));
                //judge import pin
                if ((outData[3] & 0xF0) == PayDataUtil.CLSS_OC_ONLINE_PIN) {
                    Bundle bundle = new Bundle();
                    bundle.putInt(PayDataUtil.CardCode.IMPORT_PIN_TYPE, PayDataUtil.PINTYPE_ONLINE);
                    listener.nextTransStep(PayDataUtil.CallbackSort.REQUEST_IMPORT_PIN, bundle);
                    if (ClsCardProcess.getInstance().isEndEmv()) {
                        Log.d(TAG, "Cancel Pin input");
                        return;
                    }
                }
            }

            switch (ucAcType[0]) {
                case PayDataUtil.AC_TC:
                    //offline success
                    Log.d(TAG, "TC offline success");
                    listener.onProcessResult(false, true, PayDataUtil.CardCode.TRANS_APPROVAL);
                    break;
                case PayDataUtil.AC_ARQC:
                    //online success
                    Log.d(TAG, "ARQC online success");
                    listener.onProcessResult(true, false, PayDataUtil.CardCode.TRANS_APPROVAL);
                    break;
                case PayDataUtil.AC_AAC:
                    //transaction reject
                    Log.d(TAG, "AAC transaction reject");
                    listener.onProcessResult(true, true, PayDataUtil.CardCode.TRANS_REFUSE);
                    break;
                default:
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
            TopUsdkManager.amex.delAllRevocList();
            TopUsdkManager.amex.delAllCAPK();
            int[] realLen = new int[1];
            byte[] aucAid = new byte[17];
            int res = TopUsdkManager.amex.getTLVDataList(new byte[]{0x4F}, 1,aucAid.length, aucAid, realLen);
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
            TopUsdkManager.amex.delAllRevocList();
            TopUsdkManager.amex.delAllCAPK();
            byte[] index = new byte[1];
            int[] realLen = new int[1];
            Log.d(TAG, "aid: " + aid);
            int res = TopUsdkManager.amex.getTLVDataList(new byte[]{(byte) 0x8F},1, 1, index, realLen);
            if (res == PayDataUtil.EMV_OK) {
                Log.d(TAG, "capk index: " + index[0]);
                //import capk
                EmvCapk emvCapk = getCurrentCapk(BytesUtil.hexString2Bytes(aid), index);
                Log.d(TAG, "add capk: " + emvCapk);
                if (emvCapk != null) {
                    res = TopUsdkManager.amex.addCAPK(emvCapk);
                    Log.d(TAG, "add capk res: " + res);
                }
            }
        }catch(RemoteException e){
            e.printStackTrace();
        }
    }
}
