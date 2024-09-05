package com.topwise.plugin.emv;

import android.os.Bundle;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;

import com.topwise.plugin.TopUsdkManager;
import com.topwise.plugin.emv.database.table.Aid;
import com.topwise.plugin.emv.database.table.DBManager;
import com.topwise.cloudpos.aidl.emv.level2.AidlAmex;
import com.topwise.cloudpos.aidl.emv.level2.AidlDpas;
import com.topwise.cloudpos.aidl.emv.level2.AidlEntry;
import com.topwise.cloudpos.aidl.emv.level2.AidlMir;
import com.topwise.cloudpos.aidl.emv.level2.AidlPaypass;
import com.topwise.cloudpos.aidl.emv.level2.AidlPaywave;
import com.topwise.cloudpos.aidl.emv.level2.AidlPure;
import com.topwise.cloudpos.aidl.emv.level2.AidlQpboc;
import com.topwise.cloudpos.aidl.emv.level2.AidlRupay;
import com.topwise.cloudpos.aidl.emv.level2.Combination;
import com.topwise.cloudpos.aidl.emv.level2.PreProcResult;
import com.topwise.cloudpos.aidl.emv.level2.TransParam;
import com.topwise.cloudpos.data.PinpadConstant;
import com.topwise.cloudpos.struct.BytesUtil;
import com.topwise.cloudpos.struct.Tlv;
import com.topwise.cloudpos.struct.TlvList;
import com.topwise.plugin.emv.PayDataUtil.*;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;


/**
 * 外卡处理类
 *
 * @author topwise
 * @version 1.0.0
 * @date 20-7-9
 */

public class ClsCardProcess {

    private static final String TAG = ClsCardProcess.class.getSimpleName();
    private volatile static ClsCardProcess process = null;

    private static final int LATCH_COUNT = 1;

//    private AidlPinpad pinPad = DeviceServiceManager.getInstance().getPinpadManager(PinpadConstant.PinpadId.BUILTIN);
//    private AidlEntry entryL2 = DeviceServiceManager.getInstance().getL2Entry();
//    private AidlPaywave paywave = DeviceServiceManager.getInstance().getL2Paywave();
//    private AidlPaypass paypass = DeviceServiceManager.getInstance().getL2Paypass();
//    private AidlPure pure = DeviceServiceManager.getInstance().getL2Pure();
//    private AidlAmex amex = DeviceServiceManager.getInstance().getL2Amex();
//    private AidlRupay rupay = DeviceServiceManager.getInstance().getL2Rupay();
//    private AidlQpboc qpboc = DeviceServiceManager.getInstance().getL2Qpboc();
//    private AidlMir mir = DeviceServiceManager.getInstance().getL2MirPay();
//    private AidlDpas dpas = DeviceServiceManager.getInstance().getL2DpasPay();

    private CountDownLatch mDownLatch;

    private long mAmount = 0;
    private long mCashAmount = 0;
    private byte[] mCardNo = null;
    private static int aidCount = 0;
    private boolean isEndEmv = false;
    private boolean mImportAmt = false;
    private CallbackSort mCallbackSort = CallbackSort.DEFAULT_MENU;
    private TransParam mTransParam = null;
    private byte cardPayType = -1;

    private OnEmvProcessListener mEmvProcessListener;
    private EmvTransData mEmvTransData;
    private StartPayThread mPayThread = null;
    private TlvList mTlvList = null;
    //private Map<String, byte[]> mTlvMap = null;
    private DBManager db = DBManager.getInstance();

    private ClsCardProcess() {
        //mTlvMap = new HashMap<>();
        mTlvList = new TlvList();
    }

    public static ClsCardProcess getInstance() {
        if (process == null) {
            synchronized (ClsCardProcess.class) {
                if (process == null) {
                    process = new ClsCardProcess();
                }
            }
        }
        return process;
    }

    public int processEntryLib(EmvTransData transData, OnEmvProcessListener listener) throws RemoteException {
        Log.d(TAG, "processEntryLib");
        isEndEmv = false;
        mImportAmt = false;
        cardPayType = -1;
        mEmvProcessListener = listener;
        mEmvTransData = transData;
        if (mPayThread != null && mPayThread.isAlive()) {
            mPayThread.interrupt();
            mPayThread = null;
        }
        byte[] version = new byte[64];
        int i = 0;
        int ret = TopUsdkManager.entry.getVersion(version, i);
        if (ret == PayDataUtil.EMV_OK) {
            try {
                String buffer = BytesUtil.bytes2HexString(version);
                if (buffer.contains("00")) {
                    version = BytesUtil.hexString2Bytes(buffer.split("00")[0]);
                }
                Log.d(TAG, "entryLib version: " + new String(version, "gbk"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return buildCandidate();
    }

    public boolean importAmount(long amt,long otherAmt) {
        Log.d(TAG, "importAmount amt: " + amt);
        if (amt >= 0 && mEmvTransData != null &&
                mCallbackSort == CallbackSort.REQUEST_IMPORT_AMT && mDownLatch != null) {
            mImportAmt = true;
            mAmount = amt;
            mCashAmount = otherAmt;
            Log.d(TAG, "amount in data: " + mAmount);
            Log.d(TAG, "otherAmt in data: " + mCashAmount);
            mDownLatch.countDown();
            return true;
        }
        isEndEmv = true;
        return false;
    }

    public boolean importFinalAidSelectRes(boolean res) {
        Log.d(TAG, "importFinalAidSelectRes" +
                " res: " + res);
        if (mDownLatch != null && mCallbackSort == CallbackSort.REQUEST_FINAL_AID_SELECT) {
            mDownLatch.countDown();
            return true;
        }
        isEndEmv = true;
        return false;
    }

    public boolean importAidSelectRes(int index) {
        Log.d(TAG, "importAidSelectRes" +
                " index: " + index);
        if (mDownLatch != null && mCallbackSort == CallbackSort.REQUEST_AID_SELECT) {
            mDownLatch.countDown();
            return true;
        }
        isEndEmv = true;
        return false;
    }

    public boolean importPin(String pin) {
        Log.d(TAG, "importPin" +
                " pin: " + pin);
        if (mDownLatch != null && mCallbackSort == CallbackSort.REQUEST_IMPORT_PIN) {
            mDownLatch.countDown();
            if (TextUtils.isEmpty(pin)) {
                isEndEmv = true;
            }
            return true;
        }
        isEndEmv = true;
        return false;
    }

    public boolean confirmOfflinePinEntry(boolean isContinue) {
        Log.d(TAG, "confirmOfflinePinEntry" +
                " isContinue: " + isContinue);
        if (mDownLatch != null && mCallbackSort == CallbackSort.REQUEST_OFFPINTIMES_CONFIRM) {
            mDownLatch.countDown();
            return true;
        }
        return false;
    }

    public boolean importUserAuthRes(boolean res) {
        Log.d(TAG, "importUserAuthRes" +
                " res: " + res);
        return false;
    }

    public boolean importMsgConfirmRes(boolean confirm) {
        Log.d(TAG, "importMsgConfirmRes" +
                " confirm: " + confirm);
        return false;
    }

    public boolean importECashTipConfirmRes(boolean confirm) {
        Log.d(TAG, "importECashTipConfirmRes" +
                " confirm: " + confirm);
        return false;
    }

    public boolean importOnlineResp(boolean onlineRes, String respCode, String icc55) {
        Log.d(TAG, "importOnlineResp" +
                " onlineRes: " + onlineRes + ";respCode: " + respCode + ";icc55: " + icc55);
        if (mCallbackSort == CallbackSort.REQUEST_ONLINE) {
            if (cardPayType == PayDataUtil.KERNTYPE_VISA) {
                return new PayWaveProcess().scriptProcess(onlineRes, respCode, icc55, mResultListener);
            }
        }
        return false;
    }

    public boolean importConfirmCardInfoRes(boolean res) {
        Log.d(TAG, "importConfirmCardInfoRes" +
                " res: " + res);
        if (mDownLatch != null && res && mCallbackSort == CallbackSort.REQUEST_CARDINFO_CONFIRM) {
            mDownLatch.countDown();
            return true;
        }
        isEndEmv = true;
        return false;
    }

    public void endEmv() {
        Log.d(TAG, "endEmv");
        isEndEmv = true;
        if (mPayThread != null && mPayThread.isAlive()) {
            mPayThread.interrupt();
            mPayThread = null;
        }
    }

    public boolean isEndEmv() {
        Log.d(TAG, "isEndEmv");
        return isEndEmv;
    }

    /**
     * 是否已经请求导入金额
     *
     * @return isRequestAmt
     */
    public boolean hasImportAmt() {
        Log.d(TAG, "hasImportAmt: " + mImportAmt);
        return mImportAmt;
    }

    private int buildCandidate() throws RemoteException {
        //init
        initData();
        List<Aid> mList = db.getAidDao().findAllAid();
        if (mList == null || mList.size() == 0) {
            Log.d(TAG, "aid is null!");
            selectCallback(CallbackSort.ON_ERROR, null, PayDataUtil.CardCode.NO_AID_ERROR);
        }
        mAmount = 0;
        if (mEmvTransData.getTransType() != PayDataUtil.CardCode.BALANCE) {
            selectCallback(CallbackSort.REQUEST_IMPORT_AMT, null, 0);
        }
        //pre processing
        mTransParam = new TransParam();
        if (mAmount > 0) {
            mTransParam.setAucAmount(BytesUtil.hexString2Bytes(String.format("%012d",mAmount)));
        } else {
            mTransParam.setAucAmount(BytesUtil.hexString2Bytes("000000000001"));
        }
        mTransParam.setAucAmountOther(null);
        PayDataUtil dataUtil = new PayDataUtil();
        mTransParam.setAucTransDate(BytesUtil.hexString2Bytes(dataUtil.getTransDateTime(0)));
        mTransParam.setAucTransTime(BytesUtil.hexString2Bytes(dataUtil.getTransDateTime(1)));
        mTransParam.setAucRFU(null);
        mTransParam.setAucUnNumber(PayDataUtil.getHexRandom(4));
        mTransParam.setUlTransNo(PayDataUtil.getSerialNumber());
        //TO DO 货币代码需要设置内核数据
        byte[] transCurCode = EmvManager.getInstance().getEmvTerminalInfo().getAucTransCurrencyCode();
        Log.d(TAG, "transCurCode: " + BytesUtil.bytes2HexString(transCurCode));
        mTransParam.setAucTransCurCode(transCurCode);
        mTransParam.setUcTransType(mEmvTransData.getTransType());
        int res = TopUsdkManager.entry.preProcessing(mTransParam);
        if (res != PayDataUtil.EMV_OK) {
            Log.d(TAG, "preProcessing fail, ret: " + res);
            if (res == PayDataUtil.CLSS_USE_CONTACT) {
                Bundle param = new Bundle();
                param.putInt(PayDataUtil.CardCode.TRANS_RESULT, PayDataUtil.CardCode.TRANS_USE_OTHER_INTERFACE);
                selectCallback(CallbackSort.ON_TRANS_RESULT, param, -1);
            } else {
                int errorCode = TopUsdkManager.entry.getErrorCode();
                Log.d(TAG, "getErrorCode: " + errorCode);
                Bundle param = new Bundle();
                param.putInt(PayDataUtil.CardCode.TRANS_RESULT, PayDataUtil.CardCode.TRANS_STOP);
                selectCallback(CallbackSort.ON_TRANS_RESULT, null, errorCode);
            }
            return PayDataUtil.DEFAULT_RETURN_CODE;
        }
        res = TopUsdkManager.entry.buildCandidate(0, 0);
        if (res != PayDataUtil.EMV_OK) {
            Log.d(TAG, "buildCandidate fail, error code: " + res);
            // need to handle 6A82 error
            if (res == PayDataUtil.ENTRY_KERNEL_6A82_ERR) {

            }
            int errorCode = TopUsdkManager.entry.getErrorCode();
            Log.d(TAG, "getErrorCode: " + errorCode);

            Bundle param = new Bundle();
            if (res == PayDataUtil.ICC_CMD_ERR) {
                param.putInt(PayDataUtil.CardCode.TRANS_RESULT, PayDataUtil.CardCode.TRANS_APDU_EXCHANGE_ERROR);
            } else {
                param.putInt(PayDataUtil.CardCode.TRANS_RESULT, PayDataUtil.CardCode.TRANS_STOP);
            }
            selectCallback(CallbackSort.ON_TRANS_RESULT, param, errorCode);
            return PayDataUtil.DEFAULT_RETURN_CODE;
        }

        return startFinalSelect();
    }

    private int startFinalSelect() throws RemoteException {
        Log.d(TAG, "startFinalSelect");
        byte[] ucKernType = new byte[1];
        byte[] outData = new byte[300];
        byte[] data = null;
        int[] len = new int[1];
        boolean isSelectOk = false;
        int count = 0;
        int res = 0;

        while (count++ < aidCount) {
            Arrays.fill(outData, (byte) 0x00);
            res = TopUsdkManager.entry.finalSelect(ucKernType, outData, len);
            Log.d(TAG, "finalSelect res: " + res);

            if (res != PayDataUtil.EMV_OK) {
                if (res == PayDataUtil.CLSS_USE_CONTACT || res == PayDataUtil.ICC_CMD_ERR) {
                    break;
                } else {
                    Log.d(TAG, "finalSelect fail, error code: " + res);
                    int errorCode = TopUsdkManager.entry.getErrorCode();
                    Log.d(TAG, "getErrorCode: " + errorCode);
                    TopUsdkManager.entry.delCandListCurApp();
                }
            } else {
                isSelectOk = true;
                if (len[0] > 0) {
                    data = new byte[len[0]];
                    System.arraycopy(outData, 0, data, 0, len[0]);
                }
                break;
            }
        }
        if (!isSelectOk) {
            Log.d(TAG, "finalSelect fail!");
            Bundle param = new Bundle();

            if (res == PayDataUtil.ICC_CMD_ERR) {
                param.putInt(PayDataUtil.CardCode.TRANS_RESULT, PayDataUtil.CardCode.TRANS_APDU_EXCHANGE_ERROR);
            } else {
                param.putInt(PayDataUtil.CardCode.TRANS_RESULT, PayDataUtil.CardCode.TRANS_USE_OTHER_INTERFACE);
            }
            selectCallback(CallbackSort.ON_TRANS_RESULT, param, -1);

            return PayDataUtil.DEFAULT_RETURN_CODE;
        }

        PreProcResult preProcResult = new PreProcResult();
        int ret = TopUsdkManager.entry.getPreProcResult(preProcResult);
        Log.d(TAG, "getPreProcResult ret: " + ret);
        Log.d(TAG, "preProcResult.getAucReaderTTQ(): " + BytesUtil.bytes2HexString(preProcResult.getAucReaderTTQ()));
        if (ret != PayDataUtil.EMV_OK) {
            Bundle param = new Bundle();
            param.putInt(PayDataUtil.CardCode.TRANS_RESULT, PayDataUtil.CardCode.TRANS_STOP);
            selectCallback(CallbackSort.ON_TRANS_RESULT, param, ret);
            return PayDataUtil.DEFAULT_RETURN_CODE;
        }

        Log.d(TAG, "ucKernType: " + ucKernType[0]);
        Log.d(TAG, "outData: " + BytesUtil.bytes2HexString(data));

        cardPayType = ucKernType[0];
        Bundle bundle = new Bundle();
        bundle.putInt(PayDataUtil.CardCode.FINAL_SELECT_LEN, len[0]);
        bundle.putByteArray(PayDataUtil.CardCode.FINAL_SELECT_DATA, data);
        bundle.putParcelable(PayDataUtil.CardCode.PREPROC_RESULT, preProcResult);
        bundle.putParcelable(PayDataUtil.CardCode.TRANS_PARAM, mTransParam);

        mPayThread = new StartPayThread();
        mPayThread.setAucType(cardPayType, bundle);
        mPayThread.start();
        return cardPayType;

    }

    private class StartPayThread extends Thread {

        private byte aucType = -1;
        private Bundle bundle = null;

        @Override
        public void run() {
            BasePayProcess payProcess = null;
            switch (aucType) {
                case PayDataUtil.KERNTYPE_MC:
                    payProcess = new PayPassProcess();
                    payProcess.startPay(bundle, mResultListener);
                    break;
                case PayDataUtil.KERNTYPE_VISA:
                    payProcess = new PayWaveProcess();
                    payProcess.startPay(bundle, mResultListener);
                    break;
                case PayDataUtil.KERNTYPE_PURE:
                    payProcess = new PureProcess();
                    payProcess.startPay(bundle, mResultListener);
                    break;
                case PayDataUtil.KERNTYPE_AMEX:
                    payProcess = new AMexPayProcess();
                    payProcess.startPay(bundle, mResultListener);
                    break;
                case PayDataUtil.KERNTYPE_QPBOC:
                    payProcess = new QpbocProcess();
                    payProcess.startPay(bundle, mResultListener);
                    break;
                case PayDataUtil.KERNTYPE_RUPAY:
                    payProcess = new RupayProcess();
                    payProcess.startPay(bundle, mResultListener);
                    break;
                case PayDataUtil.KERNTYPE_MIR:
                    payProcess = new MirProcess();
                    payProcess.startPay(bundle, mResultListener);
                    break;
                case PayDataUtil.KERNTYPE_DPAS:
                    payProcess = new DpasProcess();
                    payProcess.startPay(bundle, mResultListener);
                    break;
                default:
                    mResultListener.onProcessResult(false, true, PayDataUtil.CardCode.TRANS_STOP);
                    break;
            }
        }

        void setAucType(byte aucType, Bundle param) {
            Log.d(TAG, "setAucType aucType: " + aucType);
            this.aucType = aucType;
            this.bundle = param;
        }

        void finalSelectAgain() throws RemoteException {
            Log.d(TAG, "startFinalSelect again");
            byte[] ucKernType = new byte[1];
            byte[] outData = new byte[300];
            byte[] data = null;
            int[] len = new int[1];
            boolean isSelectOk = false;
            int count = 0;
            while (count++ < aidCount) {
                Arrays.fill(outData, (byte) 0x00);
                int res = TopUsdkManager.entry.finalSelect(ucKernType, outData, len);
                Log.d(TAG, "finalSelect res: " + res);

                if (res != PayDataUtil.EMV_OK) {
                    if (res == PayDataUtil.CLSS_USE_CONTACT || res == PayDataUtil.ICC_CMD_ERR) {
                        break;
                    } else {
                        Log.d(TAG, "finalSelect fail, error code: " + res);
                        int errorCode = TopUsdkManager.entry.getErrorCode();
                        Log.d(TAG, "getErrorCode: " + errorCode);
                        TopUsdkManager.entry.delCandListCurApp();
                    }
                } else {
                    isSelectOk = true;
                    if (len[0] > 0) {
                        data = new byte[len[0]];
                        System.arraycopy(outData, 0, data, 0, len[0]);
                    }
                    break;
                }
            }
            if (!isSelectOk) {
                Log.d(TAG, "finalSelect fail!");
                Bundle param = new Bundle();
                param.putInt(PayDataUtil.CardCode.TRANS_RESULT, PayDataUtil.CardCode.TRANS_USE_OTHER_INTERFACE);
                selectCallback(CallbackSort.ON_TRANS_RESULT, param, -1);
            } else {
                PreProcResult preProcResult = new PreProcResult();
                int ret = TopUsdkManager.entry.getPreProcResult(preProcResult);
                Log.d(TAG, "getPreProcResult ret: " + ret);
                if (ret != PayDataUtil.EMV_OK) {
                    Bundle param = new Bundle();
                    param.putInt(PayDataUtil.CardCode.TRANS_RESULT, PayDataUtil.CardCode.TRANS_STOP);
                    selectCallback(CallbackSort.ON_TRANS_RESULT, param, ret);
                    return;
                }
                Log.d(TAG, "ucKernType: " + ucKernType[0]);
                Log.d(TAG, "outData: " + BytesUtil.bytes2HexString(data));
                Bundle bundle = new Bundle();
                bundle.putInt(PayDataUtil.CardCode.FINAL_SELECT_LEN, len[0]);
                bundle.putByteArray(PayDataUtil.CardCode.FINAL_SELECT_DATA, data);
                bundle.putParcelable(PayDataUtil.CardCode.PREPROC_RESULT, preProcResult);
                bundle.putParcelable(PayDataUtil.CardCode.TRANS_PARAM, mTransParam);
                BasePayProcess payProcess = null;
                switch (ucKernType[0]) {
                    case PayDataUtil.KERNTYPE_MC:
                        payProcess = new PayPassProcess();
                        payProcess.startPay(bundle, mResultListener);
                        break;
                    case PayDataUtil.KERNTYPE_VISA:
                        payProcess = new PayWaveProcess();
                        payProcess.startPay(bundle, mResultListener);
                        break;
                    case PayDataUtil.KERNTYPE_PURE:
                        payProcess = new PureProcess();
                        payProcess.startPay(bundle, mResultListener);
                        break;
                    case PayDataUtil.KERNTYPE_AMEX:
                        payProcess = new AMexPayProcess();
                        payProcess.startPay(bundle, mResultListener);
                        break;
                    case PayDataUtil.KERNTYPE_QPBOC:
                        payProcess = new QpbocProcess();
                        payProcess.startPay(bundle, mResultListener);
                        break;
                    case PayDataUtil.KERNTYPE_RUPAY:
                        payProcess = new RupayProcess();
                        payProcess.startPay(bundle, mResultListener);
                        break;
                    default:
                        mResultListener.onProcessResult(false, true, PayDataUtil.CardCode.TRANS_STOP);
                        break;
                }
            }
        }
    }

    private TransResultListener mResultListener = new TransResultListener() {
        @Override
        public void onFail(int errorCode) {
            Log.d(TAG, "fail error code: " + errorCode);
            mCallbackSort = CallbackSort.ON_ERROR;
            try {
                mEmvProcessListener.onError(errorCode);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onProcessResult(boolean isOnline, boolean isTransResult, int resultValue) {
            Log.d(TAG, "onProcessResult isTransResult: " + isTransResult +
                    ";resultValue: " + resultValue);
            try {
                if (isTransResult) {
                    mCallbackSort = CallbackSort.ON_TRANS_RESULT;
                    mEmvProcessListener.onTransResult(resultValue);
                } else {
                    if (isOnline) {
                        mCallbackSort = CallbackSort.REQUEST_ONLINE;
                        mEmvProcessListener.onRequestOnline();
                    } else {
                        //TO DO
                    }
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void nextTransStep(CallbackSort sort, Bundle data) {
            Log.d(TAG, "nextTransStep CallbackSort: " + sort);
            selectCallback(sort, data, -1);
        }

        @Override
        public void finalSelectAgain() {
            Log.d(TAG, "finalSelectAgain");
            if (mPayThread != null && mPayThread.isAlive()) {
                try {
                    mPayThread.finalSelectAgain();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void setKernalData(int aucType, byte[] aid) {
            Log.d(TAG, "setKernalData aucType: " + aucType);
            setTransKernelData(aucType, aid);
        }

        @Override
        public void setCardNo(byte[] cardNo) {
            Log.d(TAG, "setCardNo cardNo: " + BytesUtil.bytes2HexString(cardNo));
            mCardNo = cardNo;
        }
    };

    /**
     * 获取tlv数据
     *
     * @param tag tlv的tag
     * @return tlv数据的value值
     */
    public byte[] getTlvData(String tag) {
        Log.d(TAG, "getTlvData() tag: " + tag);
        if (("5A".equals(tag)||"5a".equals(tag))&&mCardNo!=null) {
            Tlv tlv = new Tlv(tag,mCardNo);
            return tlv.getBytes();
        }

        try {
            int res = -1;
            byte[] data = new byte[512];
            int[] dataLen = new int[1];
            byte[] tagByte = BytesUtil.hexString2Bytes(tag);

            switch (cardPayType) {
                case PayDataUtil.KERNTYPE_MC:
                    res = TopUsdkManager.paypass.getTLVDataList(tagByte, tagByte.length, data.length, data, dataLen);
                    break;
                case PayDataUtil.KERNTYPE_VISA:
                    res = TopUsdkManager.paywave.getTLVDataList(tagByte, tagByte.length, data.length, data, dataLen);
                    break;
                case PayDataUtil.KERNTYPE_PURE:
                    res = TopUsdkManager.pure.getTLVDataList(tagByte, tagByte.length, data.length, data, dataLen);
                    break;
                case PayDataUtil.KERNTYPE_AMEX:
                    res = TopUsdkManager.amex.getTLVDataList(tagByte, tagByte.length, data.length, data, dataLen);
                    break;
                case PayDataUtil.KERNTYPE_QPBOC: {
                        byte[] bTag4Bytes = new byte[4];
                        int iTag = 0;

                        Arrays.fill(bTag4Bytes, (byte)0);
                        System.arraycopy(tagByte, 0, bTag4Bytes, bTag4Bytes.length - tagByte.length, tagByte.length);
                        Log.d(TAG, "bTag4Bytes: " + BytesUtil.bytes2HexString(bTag4Bytes));

                        //The first parameter of 'BytesUtil.bytes2Int' must be 4 bytes
                        iTag = BytesUtil.bytes2Int(bTag4Bytes, true);
                        Log.d(TAG, "iTag: " + iTag);

                        data = TopUsdkManager.qpboc.getTLVData(iTag);
                        if (data != null) {
                            res = PayDataUtil.EMV_OK;
                            dataLen[0] = data.length;
                        }
                        Log.d(TAG, "data: " + BytesUtil.bytes2HexString(data));
                    }
                    break;
                case PayDataUtil.KERNTYPE_RUPAY:
                    //TODO RUPAY
                    res = TopUsdkManager.rupay.getTLVDataList(tagByte, tagByte.length, data.length, data, dataLen);
                    break;
                default:
                    break;
            }
            Log.d(TAG, "getTlvData res: " + res);
            Log.d(TAG, "getTlvData dataLen: " + dataLen[0]);
            Log.d(TAG, "getTlvData data: " + BytesUtil.bytes2HexString(data));
            if (res == PayDataUtil.EMV_OK && dataLen[0] > 0) {
                byte[] outData = new byte[dataLen[0]];
                System.arraycopy(data, 0, outData, 0, dataLen[0]);
                Tlv tlv = new Tlv(tag,outData);
                return tlv.getBytes();
            } else {
                return null;
            }
        }catch(RemoteException e){
            Log.d(TAG, "RemoteException: " + e.getMessage());
            return null;
        }
    }

    /**
     * 设置tlv数据
     *
     * @param tag   tlv的tag
     * @param value tlv的value值
     */
    public void setTlvData(String tag, byte[] value) {
        if (tag != null && value != null) {
            mTlvList.addTlv(tag, value);
            setTransKernelData(cardPayType,new Tlv(tag,value).getBytes());
        }
    }

    private void setTransKernelData(int ucType, byte[] aid) {
        try {
            TlvList mList = handleKernalData(aid);
            byte[] data = mList.getBytes();
            if (ucType == PayDataUtil.KERNTYPE_MC) {
                TopUsdkManager.paypass.setTLVDataList(data, data.length);
            } else if (ucType == PayDataUtil.KERNTYPE_VISA) {
                TopUsdkManager.paywave.setTLVDataList(data, data.length);
            } else if (ucType == PayDataUtil.KERNTYPE_PURE) {
                TopUsdkManager.pure.setTLVDataList(data, data.length);
            } else if (ucType == PayDataUtil.KERNTYPE_AMEX) {
                TopUsdkManager.amex.setTLVDataList(data, data.length);
            } else if (ucType == PayDataUtil.KERNTYPE_QPBOC) {
                QpbocProcess.setTLVDataList(data, data.length);
            } else if (ucType == PayDataUtil.KERNTYPE_RUPAY) {
                TopUsdkManager.rupay.setTLVDataList(data, data.length);
            } else if (ucType == PayDataUtil.KERNTYPE_MIR) {
                TopUsdkManager.mir.setTLVDataList(data, data.length);
            } else if (ucType == PayDataUtil.KERNTYPE_DPAS) {
                TopUsdkManager.dpas.setTLVDataList(data, data.length);
            }
        }catch(RemoteException e){
            e.printStackTrace();
        }
    }

    /**
     * 初始化交易库数据
     */
    public void initData() throws RemoteException {
        TopUsdkManager.entry.initialize();
        TopUsdkManager.entry.delAllCombination();

        List<Aid> mList = db.getAidDao().findAllAid();
        if (mList != null && mList.size() > 0) {
            aidCount = mList.size();
            for (Aid aid : mList) {
                Combination combination = new Combination();
                combination.setUcAidLen(aid.getAid().length()/2);
                combination.setAucAID(BytesUtil.hexString2Bytes(aid.getAid()));
                combination.setUcPartMatch(1);
                combination.setUcKernIDLen(1);
                byte kernelId = aid.getKernelType();
                combination.setAucKernelID(new byte[]{kernelId});

                //Byte 1
                //bit 6: 1 = EMV mode supported
                //bit 5: 1 = EMV contact chip supported
                //bit 3: 1 = Online PIN supported
                //bit 2: 1 = Signature supported
                //Byte 3
                //bit 8: 1 = Issuer Update Processing supported
                //bit 7: 1 = Consumer Device CVM supported
                byte[] TTQ = new byte[]{0x36, 0x00, (byte) 0xC0, 0x00}; ;
                combination.setAucReaderTTQ(TTQ);

                if (aid.isFloorLimitFlg()) {
                    combination.setUcTermFLmtFlg(PayDataUtil.CLSS_TAG_EXIST_WITHVAL);
                    combination.setUlTermFLmt(aid.getFloorLimit());
                } else {
                    combination.setUcTermFLmtFlg(PayDataUtil.CLSS_TAG_NOT_EXIST);
                }

                //if (aid.isRdCVMLimitFlg()) {
                if (true) {
                    combination.setUcRdCVMLmtFlg(PayDataUtil.CLSS_TAG_EXIST_WITHVAL);
                    combination.setAucRdCVMLmt(BytesUtil.hexString2Bytes(String.format("%012d", aid.getRdCVMLimit())));
                    Log.d(TAG, "aid.getRdCVMLimit(): " + String.format("%012d", aid.getRdCVMLimit()));
                } else {
                    combination.setUcRdCVMLmtFlg(PayDataUtil.CLSS_TAG_NOT_EXIST);
                }

                //if (aid.isRdClssTxnLimitFlg()) {
                if (true) {
                    combination.setUcRdClssTxnLmtFlg(PayDataUtil.CLSS_TAG_EXIST_WITHVAL);
                    combination.setAucRdClssTxnLmt(BytesUtil.hexString2Bytes(String.format("%012d", aid.getRdClssTxnLimit())));
                    Log.d(TAG, "aid.getRdClssTxnLimit(): " + String.format("%012d", aid.getRdClssTxnLimit()));
                } else {
                    combination.setUcRdClssTxnLmtFlg(PayDataUtil.CLSS_TAG_NOT_EXIST);
                }

                //if (aid.isRdClssFloorLimitFlg()) {
                if (true) {
                    combination.setUcRdClssFLmtFlg(PayDataUtil.CLSS_TAG_EXIST_WITHVAL);
                    combination.setAucRdClssFLmt(BytesUtil.hexString2Bytes(String.format("%012d", aid.getRdClssFloorLimit())));
                    Log.d(TAG, "aid.getRdClssFloorLimit(): " + String.format("%012d", aid.getRdClssFloorLimit()));
                } else {
                    combination.setUcRdClssFLmtFlg(PayDataUtil.CLSS_TAG_NOT_EXIST);
                }
                combination.setUcZeroAmtNoAllowed(0);
                combination.setUcStatusCheckFlg(0);
                combination.setUcCrypto17Flg(1);
                combination.setUcExSelectSuppFlg(0);


                Log.d(TAG, "combination.getAucRdClssTxnLmt(): " + BytesUtil.bytes2HexString(combination.getAucRdClssTxnLmt()));
                Log.d(TAG, "combination.getAucRdClssFLmt(): " + BytesUtil.bytes2HexString(combination.getAucRdClssFLmt()));
                Log.d(TAG, "combination.getAucRdCVMLmt(): " + BytesUtil.bytes2HexString(combination.getAucRdCVMLmt()));
                TopUsdkManager.entry.addCombination(combination);
            }
        }
    }

    /**
     * 选择需要回调的方法
     *
     * @param index  方法序号
     * @param bundle 传入数据
     */
    private void selectCallback(CallbackSort index, Bundle bundle, int errorCode) {
        Log.d(TAG, "selectCallback: " + index);
        try {
            if (isEndEmv) {
                mCallbackSort = CallbackSort.ON_TRANS_RESULT;
                mEmvProcessListener.onTransResult(PayDataUtil.CardCode.TRANS_STOP);
                return;
            }
            mDownLatch = new CountDownLatch(LATCH_COUNT);
            switch (index) {
                case REQUEST_IMPORT_AMT:
                    //默认金额类型为授权金额(1)
                    mCallbackSort = CallbackSort.REQUEST_IMPORT_AMT;
                    mEmvProcessListener.requestImportAmount(1);
                    mDownLatch.await();
                    break;
                case REQUEST_FINAL_AID_SELECT:
                    mCallbackSort = CallbackSort.REQUEST_FINAL_AID_SELECT;
                    mEmvProcessListener.finalAidSelect();
                    mDownLatch.await();
                    break;
                case REQUEST_AID_SELECT:
                    int times = bundle.getInt(PayDataUtil.CardCode.IMPORT_AMT_TIMES, 1);
                    String[] aids = bundle.getStringArray(PayDataUtil.CardCode.IMPORT_AMT_AIDS);
                    mCallbackSort = CallbackSort.REQUEST_AID_SELECT;
                    mEmvProcessListener.requestAidSelect(times, aids);
                    mDownLatch.await();
                    break;
                case REQUEST_TIPS_CONFIRM:
                    //暂时没用
                    mCallbackSort = CallbackSort.REQUEST_TIPS_CONFIRM;
                    mEmvProcessListener.requestTipsConfirm(null);
                    mDownLatch.await();
                    break;
                case REQUEST_ECASHTIPS_CONFIRM:
                    //暂时没用
                    mCallbackSort = CallbackSort.REQUEST_ECASHTIPS_CONFIRM;
                    mEmvProcessListener.requestEcashTipsConfirm();
                    mDownLatch.await();
                    break;
                case REQUEST_CARDINFO_CONFIRM:
                    mCallbackSort = CallbackSort.REQUEST_CARDINFO_CONFIRM;
                    String cardNo = bundle.getString(PayDataUtil.CardCode.CARDINFO_CARDNO);
                    mEmvProcessListener.onConfirmCardInfo(cardNo);
                    mDownLatch.await();
                    break;
                case REQUEST_OFFPINTIMES_CONFIRM:
                    mCallbackSort = CallbackSort.REQUEST_CARDINFO_CONFIRM;
                    int offpin_times = bundle.getInt(PayDataUtil.CardCode.OFFPIN_TIMES);
                    mEmvProcessListener.onConfirmOfflinePinEntry(offpin_times);
                    mDownLatch.await();
                    break;
                case REQUEST_IMPORT_PIN:
                    mCallbackSort = CallbackSort.REQUEST_IMPORT_PIN;
                    int pinType = bundle.getInt(PayDataUtil.CardCode.IMPORT_PIN_TYPE);
                    mEmvProcessListener.requestImportPin(pinType, true, mAmount);
                    mDownLatch.await();
                    break;
                case REQUEST_USER_AUTH:
                    //暂时没用
                    mCallbackSort = CallbackSort.REQUEST_USER_AUTH;
                    mEmvProcessListener.requestUserAuth(0, null);
                    mDownLatch.await();
                    break;
                case REQUEST_ONLINE:
                    mCallbackSort = CallbackSort.REQUEST_ONLINE;
                    mEmvProcessListener.onRequestOnline();
                    break;
                case ON_OFFLINE_BALANCE:
                    mCallbackSort = CallbackSort.ON_OFFLINE_BALANCE;
                    mEmvProcessListener.onReadCardOffLineBalance(null,
                            null, null, null);
                    break;
                case ON_CARD_TRANSLOG:
                    mCallbackSort = CallbackSort.ON_CARD_TRANSLOG;
                    mEmvProcessListener.onReadCardTransLog(null);
                    break;
                case ON_CARD_LOADLOG:
                    mCallbackSort = CallbackSort.ON_CARD_LOADLOG;
                    mEmvProcessListener.onReadCardLoadLog(null, null, null);
                    break;
                case ON_TRANS_RESULT:
                    mCallbackSort = CallbackSort.ON_TRANS_RESULT;
                    int transRes = bundle.getInt(PayDataUtil.CardCode.TRANS_RESULT);
                    mEmvProcessListener.onTransResult(transRes);
                    break;
                case ON_ERROR:
                    mCallbackSort = CallbackSort.ON_ERROR;
                    mEmvProcessListener.onError(errorCode);
                    break;
                default:
                    break;
            }
        } catch (RemoteException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * assets 文件保存
     */
    private TlvList handleKernalData(byte[] aid) {
        PayDataUtil dataUtil = new PayDataUtil();
        TlvList list = dataUtil.getDefaultKernal();

        //set kernel data
        if (mTlvList.getList() != null && mTlvList.getList().size() > 0) {
            for (Map.Entry<String, Tlv> entry : mTlvList.getList().entrySet()) {
                Log.d(TAG, "settlv out: " + entry.getValue().toHex());
                list.addTlv(entry.getValue());
            }
            mTlvList.clear();
        }

        //Amount, Authorised (Numeric)
        Log.d(TAG, "txn amount: " + mAmount);
        if (mAmount > 0) {
            list.addTlv("9F02",String.format("%012d",mAmount));
        }

        //Transaction Type
        String tradeType = BytesUtil.bytes2HexString(new byte[]{mEmvTransData.getTransType()});
        Log.d(TAG, "txn type: " + tradeType);
        list.addTlv("9C",tradeType);

        //Transaction Sequence Counter
        list.addTlv("9F41",dataUtil.getSequenceCounter());

        //Transaction Date
        list.addTlv("9A",dataUtil.getTransDateTime(PayDataUtil.TRANS_DATE_YYMMDD));

        //Transaction Time
        list.addTlv("9F21",dataUtil.getTransDateTime(PayDataUtil.TRANS_TIME_HHMMSS));

        //The getRandom function returns a fixed 8 byte random number
        byte[] random = new byte[0];
        try {
            random = TopUsdkManager.pinpad.getRandom();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        byte[] unpredictableNum = new byte[4];
        System.arraycopy(random, 0, unpredictableNum, 0, 4);
        list.addTlv("9F37", unpredictableNum);

        //aid parameters
        if (aid == null) {
            return list;
        }
        TlvList aidList = new TlvList();
        aidList.fromBytes(aid);

        //Reader Contactless Floor Limit
        String data = null;
        if(aidList.getTlv("DF19")!=null)
            data = aidList.getTlv("DF19").getHexValue();
        Log.d(TAG, "DF8123(DF19) data: " + data);
        if (!TextUtils.isEmpty(data)) {
            list.addTlv("DF8123",data);
        } else {
            list.addTlv("DF8123","000000030000");
        }

        //Reader Contactless Transaction Limit (No On-device CVM)
        data = null;
        if(aidList.getTlv("DF20")!=null)
            data = aidList.getTlv("DF20").getHexValue();
        Log.d(TAG, "DF8124(DF20) data: " + data);
        if (!TextUtils.isEmpty(data)) {
            list.addTlv("DF8124",data);
        } else {
            list.addTlv("DF8124","000099999999");
        }

        //Reader Contactless Transaction Limit (On-device CVM)
        data = null;
        if(aidList.getTlv("DF20")!=null)
            data = aidList.getTlv("DF20").getHexValue();
        Log.d(TAG, "DF8125(DF20) data: " + data);
        if (!TextUtils.isEmpty(data)) {
            list.addTlv("DF8125",data);
        } else {
            list.addTlv("DF8125","000099999999");
        }

        //Reader CVM Required Limit
        data = null;
        if(aidList.getTlv("DF21")!=null)
            data = aidList.getTlv("DF21").getHexValue();
        Log.d(TAG, "DF8126(DF21) data: " + data);
        if (!TextUtils.isEmpty(data)) {
            list.addTlv("DF8126",data);
        } else {
            list.addTlv("DF8126","000000030000");
        }

        //TAC-Default
        data = null;
        if(aidList.getTlv("DF11")!=null)
            data = aidList.getTlv("DF11").getHexValue();
        Log.d(TAG, "DF8120(DF11) data: " + data);
        if (!TextUtils.isEmpty(data)) {
            list.addTlv("DF8120",data);
        } else {
            list.addTlv("DF8120","0000000000");
        }

        //TAC-Online
        data = null;
        if(aidList.getTlv("DF12")!=null)
            data = aidList.getTlv("DF12").getHexValue();
        Log.d(TAG, "DF8122(DF12) data: " + data);
        if (!TextUtils.isEmpty(data)) {
            list.addTlv("DF8122",data);
        } else {
            list.addTlv("DF8122","0000000000");
        }

        //TAC-Denial
        data = null;
        if(aidList.getTlv("DF13")!=null)
            data = aidList.getTlv("DF13").getHexValue();
        Log.d(TAG, "DF8121(DF13) data: " + data);
        if (!TextUtils.isEmpty(data)) {
            list.addTlv("DF8121",data);
        } else {
            list.addTlv("DF8121","0000000000");
        }

        return list;
    }
    private TlvList setRupayKernalData(byte[] aid){
        PayDataUtil dataUtil = new PayDataUtil();
        TlvList list = new TlvList();

        //Transaction Date
        list.addTlv("9A",dataUtil.getTransDateTime(PayDataUtil.TRANS_DATE_YYMMDD));
        //Transaction Time
        list.addTlv("9F21",dataUtil.getTransDateTime(PayDataUtil.TRANS_TIME_HHMMSS));
        //
        list.addTlv("DF16","02");

        //((unsigned char*)"\xDF\x81\x0C", 3, (unsigned char*)"\x0D", 1)
        list.addTlv("DF810C","0D");
        //((unsigned char*)"\xDF\x51", 2, aucAmount, 6);//Terminal Floor Limit.
        if (mImportAmt) {
            list.addTlv("DF51",String.format("%012d",mAmount));
        }
        //((unsigned char*)"\x9F\x1B", 2, (unsigned char*)"\x00\x00\x27\x10", 4);//Terminal Floor Limit.
        list.addTlv("9F1B","00002710");
        //((unsigned char*)"\xDF\x81\x23", 3, gl_stCurAppParam.aucRdClssFLmt, 6);//Reader Contactless Floor Limit
        TlvList aidList = new TlvList();
        aidList.fromBytes(aid);
        //Reader Contactless Floor Limit
        String data = null;
        if(aidList.getTlv("DF19")!=null)
            data = aidList.getTlv("DF19").getHexValue();
        Log.d(TAG, "DF8123(DF19) data: " + data);
        if (!TextUtils.isEmpty(data)) {
            list.addTlv("DF8123",data);
        } else {
            list.addTlv("DF8123","000000030000"); //300
        }
        //==========
        //((unsigned char*)"\xDF\x4D", 2, gl_stCurAppParam.aucRdCVMLmt, 6);//Terminal CVM Limit
        list.addTlv("DF4D","000000100000"); //getAucRdCVMLmt //暂时写死，应该要用 aid 记录的
        // ((unsigned char*)"\xDF\x4C", 2, gl_stCurAppParam.aucRdClssTxnLmt, 6);//Terminal Contactless Transaction Limit
        list.addTlv("DF4C","000999999999"); //aucRdClssTxnLmt //暂时写死，应该要用 aid 记录的
        //((unsigned char*)"\xDF\x81\x21", 3, (unsigned char*)"\x00\x00\x00\x00\x00", 5);//TACDenail
        list.addTlv("DF8121","0000000000");
        //     ((unsigned char*)"\xDF\x81\x22", 3, (unsigned char*)"\x00\x00\x00\x00\x00", 5);//TACOnline
        list.addTlv("DF8122","0000000000");
        //     ((unsigned char*)"\xDF\x81\x20", 3, (unsigned char*)"\x00\x00\x00\x00\x00", 5);//TACDefault
        list.addTlv("DF8120","0000000000");
        //  ((unsigned char*)"\x9F\x09", 2, (unsigned char*)"\x00\x02", 2);//Application Version Number
        list.addTlv("9F09","0002");
        //  ((unsigned char*)"\x9F\x15", 2, (unsigned char*)"\x41\x31", 2);//Merchant Category Code
        list.addTlv("9F15","4131");
        //     ((unsigned char*)"\x9F\x1A", 2, (unsigned char*)"\x03\x56", 2);//Terminal Country Code
        list.addTlv("9F1A","0356");
        //  ((unsigned char*)"\x9F\x1C", 2, (unsigned char*)"\x31\x32\x33\x34\x35\x36\x37\x38", 8);//Terminal ID
        list.addTlv("9F1C","3132333435363738");
        //     ((unsigned char*)"\x9F\x33", 2, (unsigned char*)"\xE0\x68\xC8", 3);//Terminal Capabilities
        list.addTlv("9F33","E068C8");
        //((unsigned char*)"\x9F\x40", 2, (unsigned char*)"\xFF\xC0\xF0\xA0\x01", 5);//DV_122_00_02 ask B2b7=1 and DF3A B2b7=0
        list.addTlv("9F40","FFC0F0A001");
        //((unsigned char*)"\xDF\x3A", 2, (unsigned char*)"\x00\x40\x00\x00\x00", 5);
        list.addTlv("DF3A","0040000000");
        //((unsigned char*)"\x9F\x35", 2, (unsigned char*)"\x22", 1);
        list.addTlv("9F35","22");
        //((unsigned char*)"\x5F\x2A", 2, (unsigned char*)"\x03\x56", 2);//Transaction Currency Code
        list.addTlv("5F2A","0356");
        //((unsigned char*)"\x5F\x36", 2, (unsigned char*)"\x02", 1);//Transaction Currency Exponent
        list.addTlv("5F36","02");
        //((unsigned char*)"\xDF\x81\x31", 3, (unsigned char*)"\x05", 1);//Max Target Percentage. Tag defined by self
        list.addTlv("DF8131","05");
        //(unsigned char*)"\xDF\x81\x32", 3, (unsigned char*)"\x00", 1);//Target Percentage
        list.addTlv("DF8132","00");
        //((unsigned char*)"\xDF\x81\x33", 3, (unsigned char*)"\x00\x00\x00\x00\x05\x00", 6);// Threshold Value
        list.addTlv("DF8133","000000000500");

        return list;
    }
}
