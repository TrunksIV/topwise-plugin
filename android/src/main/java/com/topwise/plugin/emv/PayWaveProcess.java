package com.topwise.plugin.emv;

import android.os.Bundle;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;
import com.topwise.cloudpos.aidl.emv.level2.EmvCapk;
import com.topwise.cloudpos.aidl.emv.level2.EmvTerminalInfo;
import com.topwise.cloudpos.aidl.emv.level2.PreProcResult;
import com.topwise.cloudpos.aidl.emv.level2.TransParam;
import com.topwise.cloudpos.struct.BytesUtil;
import com.topwise.cloudpos.struct.Tlv;
import com.topwise.cloudpos.struct.TlvList;
import com.topwise.plugin.TopUsdkManager;

/**
 * @author xukun
 * @version 1.0.0
 * @date 19-9-17
 */

public class PayWaveProcess extends BasePayProcess {

    private static final String TAG = PayWaveProcess.class.getSimpleName();
    
    @Override
    public void startPay(Bundle param, TransResultListener listener){
        Log.d(TAG, "startPaywave");
        try {
            TopUsdkManager.paywave.initialize();
            byte[] data = param.getByteArray(PayDataUtil.CardCode.FINAL_SELECT_DATA);
            int len = param.getInt(PayDataUtil.CardCode.FINAL_SELECT_LEN);
            int res = TopUsdkManager.paywave.setFinalSelectData(data, len);
            Log.d(TAG, "setFinalSelectData res: " + res);
            if (!handleResult(res, listener)) {
                return;
            }

            String rid = getCurrentRid();
            byte[] aidData = getCurrentAidData(rid);
            listener.setKernalData(PayDataUtil.KERNTYPE_VISA, aidData);

            PreProcResult preProcResult = param.getParcelable(PayDataUtil.CardCode.PREPROC_RESULT);
            if (preProcResult != null) {
                String buffer = BytesUtil.bytes2HexString(preProcResult.getAucReaderTTQ());
                Log.d(TAG, "preProcResult.getAucReaderTTQ: " + buffer);
                if (buffer.contains("00000000")) {
                    preProcResult.setAucReaderTTQ(BytesUtil.hexString2Bytes("3600C000"));
                }
            }
            TransParam transParam = param.getParcelable(PayDataUtil.CardCode.TRANS_PARAM);
            res = TopUsdkManager.paywave.setTransData(transParam, preProcResult);
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
                    Log.d(TAG, "setTerminalInfo: " + BytesUtil.bytes2HexString(tlvData));
                    TopUsdkManager.paywave.setTLVDataList(tlvData, tlvData.length);
                }
            }
            listener.nextTransStep(PayDataUtil.CallbackSort.REQUEST_FINAL_AID_SELECT, null);

            byte[] TTQTlv = new byte[7];
            TTQTlv[0] = (byte)0x9F;
            TTQTlv[1] = (byte)0x66;
            TTQTlv[2] = (byte)0x04;
            System.arraycopy(preProcResult.getAucReaderTTQ(), 0, TTQTlv, 3, 4);
            Log.d(TAG, "TTQTlv: " + BytesUtil.bytes2HexString(TTQTlv));
            TopUsdkManager.paywave.setTLVDataList(TTQTlv, TTQTlv.length);

            byte[] dataBuf = new byte[1];
            res = TopUsdkManager.paywave.gpoProc(dataBuf);
            Log.d(TAG, "gpoProc res: " + res);
            if (!handleResult(res, listener)) {
                return;
            }

            byte[] ucAcType1 = new byte[1];
            res = TopUsdkManager.paywave.readData(ucAcType1);
            Log.d(TAG, "readData res: " + res);
            if (!handleResult(res, listener)) {
                return;
            }
            //ucAcType1 == PayDataUtil.AC_TC   ----TO DO

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
                byte[] ucODDAResultFlg = new byte[1];
                res = TopUsdkManager.paywave.cardAuth(ucAcType, ucODDAResultFlg);
            } else if (dataBuf[0] == PayDataUtil.CLSS_TRANSPATH_MAG) {
                Log.d(TAG, "ucType == PayDataUtil.CLSS_TRANSPATH_MAG");
            } else {
                res = PayDataUtil.CLSS_TERMINATE;
            }
            Log.d(TAG, "trans proc res: " + res + ";ucAcType: " + ucAcType[0]);
            if (!handleResult(res, listener)) {
                return;
            }
            //read card info
            byte[] cardData = new byte[32];
            int[] dataLen = new int[1];
            int res1 = TopUsdkManager.paywave.getTLVDataList(BytesUtil.hexString2Bytes("57"), 1, cardData.length, cardData, dataLen);
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
            //DF8129 tag  --is need pwd
            byte[] outComeBuffer = new byte[17];
            int[] bufLen = new int[1];
            res = TopUsdkManager.paywave.getTLVDataList(new byte[]{(byte) 0xDF, (byte) 0x81, 0x29},
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
    boolean scriptProcess(boolean onlineRes, String respCode, String icc55, TransResultListener listener) {
        Bundle param = new Bundle();
        if (!PayDataUtil.AUTH_RES_CODE.equals(respCode) || !onlineRes || TextUtils.isEmpty(icc55)) {
            param.putInt(PayDataUtil.CardCode.TRANS_RESULT, 2);
            listener.nextTransStep(PayDataUtil.CallbackSort.ON_TRANS_RESULT, param);
            return false;
        }
        TlvList list = new TlvList();
        list.fromHex(icc55);
        if (list.getList().isEmpty()) {
            param.putInt(PayDataUtil.CardCode.TRANS_RESULT, 2);
            listener.nextTransStep(PayDataUtil.CallbackSort.ON_TRANS_RESULT, param);
            return false;
        }
        try {
            Tlv tlv = list.getTlv("91");
            byte[] authData = null;
            if (tlv != null)
                authData = tlv.getBytes();
            Log.d(TAG, "auth data: " + BytesUtil.bytes2HexString(authData));
            if (authData != null && authData.length > 0) {
                int ret = TopUsdkManager.paywave.issuerAuth(authData, authData.length);
                Log.d(TAG, "issuerAuth ret: " + ret);
                if (ret == PayDataUtil.EMV_OK) {
                    Tlv tlv1 = list.getTlv("71");
                    Tlv tlv2 = list.getTlv("72");
                    byte[] procData = null;
                    if(tlv1!=null)
                        procData = BytesUtil.mergeBytes(procData,tlv1.getBytes());
                    if(tlv2!=null)
                        procData = BytesUtil.mergeBytes(procData,tlv2.getBytes());
                    Log.d(TAG, "proc data: " + BytesUtil.bytes2HexString(procData));
                    if (procData != null && procData.length > 0) {
                        ret = TopUsdkManager.paywave.issScriptProc(procData, procData.length);
                        Log.d(TAG, "issScriptProc ret: " + ret);
                        if (ret == PayDataUtil.EMV_OK) {
                            param.putInt(PayDataUtil.CardCode.TRANS_RESULT, 1);
                            listener.nextTransStep(PayDataUtil.CallbackSort.ON_TRANS_RESULT, param);
                            return true;
                        }
                    }
                }
            }
            param.putInt(PayDataUtil.CardCode.TRANS_RESULT, 2);
            listener.nextTransStep(PayDataUtil.CallbackSort.ON_TRANS_RESULT, param);
            return false;
        } catch(RemoteException e){
            e.printStackTrace();
            listener.onFail(e.hashCode());
        }
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
            TopUsdkManager.paywave.delAllRevocList();
            TopUsdkManager.paywave.delAllCAPK();
            int[] realLen = new int[1];
            byte[] aucAid = new byte[17];
            int res = TopUsdkManager.paywave.getTLVDataList(new byte[]{0x4F}, 1,
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
        Log.d(TAG, "addCapk(),  aid: " + aid);
        try {
            TopUsdkManager.paywave.delAllRevocList();
            TopUsdkManager.paywave.delAllCAPK();
            byte[] index = new byte[1];
            int[] realLen = new int[1];
            Log.d(TAG, "aid: " + aid);
            int res = TopUsdkManager.paywave.getTLVDataList(new byte[]{(byte) 0x8F},
                    1, 1, index, realLen);
            if (res == PayDataUtil.EMV_OK) {
                Log.d(TAG, "capk index: " + index[0]);
                //import capk
                EmvCapk emvCapk = getCurrentCapk(BytesUtil.hexString2Bytes(aid), index);
                Log.d(TAG, "add capk: " + emvCapk);
                if (emvCapk != null) {
                    res = TopUsdkManager.paywave.addCAPK(emvCapk);
                    Log.d(TAG, "add capk res: " + res);
                }
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
