package com.topwise.plugin.emv;

import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;

import com.topwise.cloudpos.aidl.emv.level2.AidlEmvL2;
import com.topwise.cloudpos.aidl.emv.level2.AidlRupay;
import com.topwise.cloudpos.aidl.emv.level2.EmvCapk;
import com.topwise.cloudpos.aidl.emv.level2.EmvTerminalInfo;
import com.topwise.cloudpos.aidl.emv.level2.PreProcResult;
import com.topwise.cloudpos.struct.BytesUtil;
import com.topwise.cloudpos.struct.TlvList;
import com.topwise.plugin.TopUsdkManager;

/**
 * Pure Process
 * Created by topwise on 20-07-09.
 */
public class RupayProcess extends BasePayProcess {
    private static final String TAG = RupayProcess.class.getSimpleName();

    @Override
    public void startPay(Bundle param, TransResultListener listener) {
        Log.d(TAG, "startPay");
        try {
            //init
            TopUsdkManager.rupay.initialize();
            byte[] data = param.getByteArray(PayDataUtil.CardCode.FINAL_SELECT_DATA);
            int len = param.getInt(PayDataUtil.CardCode.FINAL_SELECT_LEN);
            int res = TopUsdkManager.rupay.setFinalSelectData(data, len);
            Log.d(TAG, "setFinalSelectData res: " + res);
            Log.d(TAG, "setFinalSelectData data: " + BytesUtil.bytes2HexString(data));
            if (!handleResult(res, listener)) {
                return;
            }

            String rid = getCurrentRid();
            byte[] aidData = getCurrentAidData(rid);
            //回调设置内核数据
            Log.d(TAG, "listener set aid KernalData ========= " );
            listener.setKernalData(PayDataUtil.KERNTYPE_RUPAY, aidData);
            //读取终端信息
            Log.d(TAG, "emv GetTerminalInfo ========= " );
            EmvTerminalInfo terminalInfo = TopUsdkManager.emvL2.EMV_GetTerminalInfo();
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
                    TopUsdkManager.rupay.setTLVDataList(tlvData, tlvData.length);
                }
            }
            Log.d(TAG, "listener emv REQUEST_FINAL_AID_SELECT ========= " );
            listener.nextTransStep(PayDataUtil.CallbackSort.REQUEST_FINAL_AID_SELECT, null);

            //set tag
            //===
            Log.d(TAG, "getParcelable=========: " );
            PreProcResult preProcResult = param.getParcelable(PayDataUtil.CardCode.PREPROC_RESULT);
            if (preProcResult != null) {
                String buffer = BytesUtil.bytes2HexString(preProcResult.getAucReaderTTQ());
                Log.d(TAG, "preProcResult.getAucReaderTTQ: " + buffer);
                if (buffer.contains("00000000")) {
                    preProcResult.setAucReaderTTQ(BytesUtil.hexString2Bytes("3600C000"));
                }
            }
            Log.d(TAG, "gpoProc=========: " );
            res = TopUsdkManager.rupay.gpoProc();
            Log.d(TAG, "gpoProc res: " + res);
            if (!handleResult(res, listener)) {
                return;
            }
            Log.d(TAG, "readData=========: " );
            res = TopUsdkManager.rupay.readData();
            Log.d(TAG, "readData res: " + res);
            if (!handleResult(res, listener)) {
                return;
            }
            Log.d(TAG, "read card info =========: " );
            //read card info
            byte[] cardData = new byte[32];
            int[] dataLen = new int[1];
            int res1 = TopUsdkManager.rupay.getTLVDataList(BytesUtil.hexString2Bytes("57"),
                    1, cardData.length, cardData, dataLen);
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
                Log.d(TAG, "listener emv setCardNo ========= " );
                listener.setCardNo(BytesUtil.hexString2Bytes(cardNo));
            }

            //Add Capk
            Log.d(TAG, "Add Capk =========: " );
            addCapk(rid);
            Log.d(TAG, "startTrans=========: " );
            res = TopUsdkManager.rupay.startTrans();
            Log.d(TAG, "startTrans res: " + res);
            if (!handleResult(res, listener)) {
                return;
            }
            Log.d(TAG, "cardAuth=========: " );
            res = TopUsdkManager.rupay.cardAuth();
            Log.d(TAG, "pure.cardAuth: res=" + res);
            if (!handleResult(res, listener)) {
                return;
            }
            //DF8129 tag  --is need pwd
            byte[] outComeBuffer = new byte[17];
            int[] bufLen = new int[1];
            Log.d(TAG, "getTLVDataList DF8129 =========: " );
            res = TopUsdkManager.rupay.getTLVDataList(new byte[]{(byte) 0xDF, (byte) 0x81, 0x29},
                    3, outComeBuffer.length, outComeBuffer, bufLen);
            if (res == PayDataUtil.EMV_OK) { //30 F0 F0 F0 A0F0FF00
                Log.d(TAG, "bufLen: " + bufLen[0]);
                byte[] outData = new byte[bufLen[0]];
                System.arraycopy(outComeBuffer, 0, outData, 0, bufLen[0]);
                Log.d(TAG, "real outcome data: " + BytesUtil.bytes2HexString(outData));
                //judge import pin
                if ((outData[3] & 0xF0) == PayDataUtil.CLSS_OC_ONLINE_PIN) {
                    Bundle bundle = new Bundle();
                    bundle.putInt(PayDataUtil.CardCode.IMPORT_PIN_TYPE, PayDataUtil.PINTYPE_ONLINE);
                    listener.nextTransStep(PayDataUtil.CallbackSort.REQUEST_IMPORT_PIN, bundle);
                }
            }
            Log.d(TAG, "switch outComeBuffer =========: " + outComeBuffer[0]);
            switch (outComeBuffer[0]) {
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
                    listener.onProcessResult(true, true, PayDataUtil.CardCode.TRANS_REFUSE);
                    break;
                default:
                    break;
            }

        } catch (RemoteException e) {
            e.printStackTrace();
        }


    }

    /**
     * 脚本处理
     * @param onlineRes 是否联机
     * @param respCode  联机结果
     * @param icc55     脚本信息
     * @param listener  交易监听
     * @return
     * @throws RemoteException
     */
    @Override
    public boolean scriptProcess(boolean onlineRes, String respCode, String icc55, TransResultListener listener) throws RemoteException {
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
            //delete
            TopUsdkManager.rupay.delAllRevocList();
            TopUsdkManager.rupay.delAllCAPK();
            int[] realLen = new int[1];
            byte[] aucAid = new byte[17];
            int res = TopUsdkManager.rupay.getTLVDataList(new byte[]{0x4F}, 1,
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
        }catch(RemoteException e){
            e.printStackTrace();
        }
        return aid;
    }
    /**
     * 添加CAPK到交易库
     */
    private void addCapk(String aid) {
        try {
            TopUsdkManager.rupay.delAllRevocList();
            TopUsdkManager.rupay.delAllCAPK();
            byte[] index = new byte[1];
            int[] realLen = new int[1];
            Log.d(TAG, "aid: " + aid);
            int res = TopUsdkManager.rupay.getTLVDataList(new byte[]{(byte) 0x8F},
                    1, 1, index, realLen);
            if (res == PayDataUtil.EMV_OK) {
                Log.d(TAG, "capk index: " + index[0]);
                //import capk
                EmvCapk emvCapk = getCurrentCapk(BytesUtil.hexString2Bytes(aid), index);
                Log.d(TAG, "add capk: " + emvCapk);
                if (emvCapk != null) {
                    res = TopUsdkManager.rupay.addCAPK(emvCapk);
                    Log.d(TAG, "add capk res: " + res);
                }
            }
        }catch(RemoteException e){
            e.printStackTrace();
        }
    }

    private byte [] setKernalDataTag(){
        TlvList list = new TlvList();
        //(unsigned char*)"\xDF\x81\x0C", 3, (unsigned char*)"\x0D", 1);
        list.addTlv("DF810C", "0D");
        //((unsigned char*)"\xDF\x51", 2, aucAmount, 6);//Terminal Floor Limit.
        list.addTlv("DF51", "0D");

        Log.d(TAG, "setKernalDataTag ========= " + list.toString() );
        return list.getBytes();
    }

}
