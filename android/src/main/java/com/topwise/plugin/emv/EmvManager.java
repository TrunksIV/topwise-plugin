package com.topwise.plugin.emv;

import android.os.RemoteException;
import android.util.Log;
import com.topwise.cloudpos.aidl.emv.level2.EmvKernelConfig;
import com.topwise.cloudpos.aidl.emv.level2.EmvTerminalInfo;
import com.topwise.cloudpos.struct.BytesUtil;
import com.topwise.plugin.emv.database.table.DBManager;

public class EmvManager {
    private static final String TAG = "EmvManager";
    private static EmvManager instance = new EmvManager();
    private static final int PARAM_ERROR = -1;
    private EmvTransData emvTransData;
    private OnEmvProcessListener listener;
    private volatile EmvProcessThread emvProcessThread = null;
    private DBManager db = DBManager.getInstance();
    private int cardType = CardType.NONE;
    private boolean isProcessEmv = false;
    private EmvKernelConfig emvKernelConfig = null;
    private EmvTerminalInfo emvTerminalInfo = null;

    /**
     * process PBOC thread
     */
    private class EmvProcessThread extends Thread {

        private volatile OnEmvProcessListener emvProcessListener = null;
        private EmvProcessThread() {
        }

        @Override
        public void run() {
            if(cardType == CardType.IC){
                try {
                    ContactCardProcess.getInstance().EmvProcess(emvTransData, listener);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            } else if(cardType==CardType.RF){
                int entryLibRes = 0;
                try {
                    entryLibRes = ClsCardProcess.getInstance().processEntryLib(emvTransData, listener);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                Log.d(TAG, "processEntryLib res: " + entryLibRes);
            }
        }
    }

    public static EmvManager getInstance() {
        return instance;
    }

    public synchronized void startEmvProcess(EmvTransData emvTransData, OnEmvProcessListener listener){
        if (emvTransData == null || listener == null) {
            Log.d(TAG, "input param is null");
            return;
        }
        Log.d(TAG, emvTransData.toString());
        this.emvTransData = emvTransData;
        this.listener = listener;
        cardType = emvTransData.getCardType();
        if (emvTransData == null) {
            Log.d(TAG, "emvTransData == null");
            return;
        }
        if (listener == null) {
            Log.d(TAG, "listener == null");
            return;
        }

        if (emvProcessThread != null && emvProcessThread.isAlive()) {
            Log.e(TAG, "pbocThread is alive");
            try {
                abortEMV();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            //return;
        }
        isProcessEmv = true;
        Log.d(TAG, "EmvTransData: " + emvTransData.toString());
        emvProcessThread = new EmvProcessThread();
        emvProcessThread.start();
    }

    public void setEmvTerminalInfo(EmvTerminalInfo info){
        emvTerminalInfo = info;
    }

    public EmvTerminalInfo getEmvTerminalInfo() {
        if(emvTerminalInfo==null){
            emvTerminalInfo = new EmvTerminalInfo();
            emvTerminalInfo.setUnTerminalFloorLimit(20000);
            emvTerminalInfo.setUnThresholdValue(10000);
            emvTerminalInfo.setAucTerminalID("00000001");
            emvTerminalInfo.setAucIFDSerialNumber("12345678");
            emvTerminalInfo.setAucTerminalCountryCode(new byte[] {0x01, 0x56});
            emvTerminalInfo.setAucMerchantID("000000010000000");
            emvTerminalInfo.setAucMerchantCategoryCode(new byte[] {0x00, 0x01});
            emvTerminalInfo.setAucMerchantNameLocation(new byte[] {0x30, 0x30, 0x30, 0x31}); //"0001"
            emvTerminalInfo.setAucTransCurrencyCode(new byte[] {0x01, 0x56});
            emvTerminalInfo.setUcTransCurrencyExp((byte) 2);
            emvTerminalInfo.setAucTransRefCurrencyCode(new byte[] {0x01, 0x56});
            emvTerminalInfo.setUcTransRefCurrencyExp((byte) 2);
            emvTerminalInfo.setUcTerminalEntryMode((byte) 0x05);

            emvTerminalInfo.setAucTerminalAcquireID("123456");
            emvTerminalInfo.setAucAppVersion(new byte[] {0x00, 0x030});
            emvTerminalInfo.setAucDefaultDDOL(new byte[] {(byte)0x9F, 0x37, 0x04});
            emvTerminalInfo.setAucDefaultTDOL(new byte[] {(byte)0x9F, 0x37, 0x04});
            emvTerminalInfo.setAucTACDenial(new byte[] {0x00, 0x00, 0x00, 0x00, 0x00});
            emvTerminalInfo.setAucTACOnline(new byte[] {0x00, 0x00, 0x00, 0x00, 0x00});
            emvTerminalInfo.setAucTACDefault(new byte[] {0x00, 0x00, 0x00, 0x00, 0x00});

            emvTerminalInfo.setUcTerminalType((byte)0x22);
            emvTerminalInfo.setAucTerminalCapabilities(new byte[] {(byte)0xE0, (byte)0xF8, (byte)0xC8});
            emvTerminalInfo.setAucAddtionalTerminalCapabilities(new byte[] {(byte)0xFF, (byte)0x00, (byte)0xF0, (byte)0xA0, 0x01});

            emvTerminalInfo.setUcTargetPercentage((byte) 20);
            emvTerminalInfo.setUcMaxTargetPercentage((byte) 50);
            emvTerminalInfo.setUcAccountType((byte) 0);
            emvTerminalInfo.setUcIssuerCodeTableIndex((byte) 0);
        }
        return emvTerminalInfo;
    }

    public void setKernelConfig(EmvKernelConfig config){
        emvKernelConfig = config;
    }

    public EmvKernelConfig getKernelConfig() {
        if(emvKernelConfig == null){
            emvKernelConfig= new EmvKernelConfig();
            emvKernelConfig.setbPSE((byte) 1);
            emvKernelConfig.setbCardHolderConfirm((byte) 1);
            emvKernelConfig.setbPreferredDisplayOrder((byte) 0);
            emvKernelConfig.setbLanguateSelect((byte) 1);
            emvKernelConfig.setbRevocationOfIssuerPublicKey((byte) 1);
            emvKernelConfig.setbDefaultDDOL((byte) 1);
            emvKernelConfig.setbBypassPINEntry((byte) 1);
            emvKernelConfig.setbSubBypassPINEntry((byte) 1);
            emvKernelConfig.setbGetdataForPINTryCounter((byte) 1);
            emvKernelConfig.setbFloorLimitCheck((byte) 1);
            emvKernelConfig.setbRandomTransSelection((byte) 1);
            emvKernelConfig.setbVelocityCheck((byte) 1);
            emvKernelConfig.setbTransactionLog((byte) 1);
            emvKernelConfig.setbExceptionFile((byte) 1);
            emvKernelConfig.setbTerminalActionCode((byte) 1);
            emvKernelConfig.setbDefaultActionCodeMethod((byte) EmvDefinition.EMV_DEFAULT_ACTION_CODE_AFTER_GAC1);
            emvKernelConfig.setbTACIACDefaultSkipedWhenUnableToGoOnline((byte) 0);
            emvKernelConfig.setbCDAFailureDetectedPriorTerminalActionAnalysis((byte) 1);
            emvKernelConfig.setbCDAMethod((byte) EmvDefinition.EMV_CDA_MODE1);
            emvKernelConfig.setbForcedOnline((byte) 0);
            emvKernelConfig.setbForcedAcceptance((byte) 0);
            emvKernelConfig.setbAdvices((byte) 0);
            emvKernelConfig.setbIssuerReferral((byte) 1);
            emvKernelConfig.setbBatchDataCapture((byte) 0);
            emvKernelConfig.setbOnlineDataCapture((byte) 1);
            emvKernelConfig.setbDefaultTDOL((byte) 1);
            emvKernelConfig.setbTerminalSupportAccountTypeSelection((byte) 1);
        }
        return emvKernelConfig;
    }

    /**
     * End EMV process
     *
     * @throws RemoteException RemoteException
     */
    public void endEMV() throws RemoteException {
        Log.d(TAG, "endEMV");

        if (cardType == CardType.RF) {
            ClsCardProcess.getInstance().endEmv();
        } else {
            ContactCardProcess.getInstance().endEmv();
        }

        if (emvProcessThread != null) {
            emvProcessThread.interrupt();
        }
    }

    /**
     * abort EMV process, equivalent to the endPBOC method
     *
     * @throws RemoteException RemoteException
     */
    public void abortEMV() throws RemoteException {
        Log.d(TAG, "abortPBOC");
        endEMV();
    }

    /**
     * Read the kernel log
     *
     * @param taglist Needed to output the taglist data such as {“9F26”,“5A”}
     * @param buffer  Read data [output param]
     * @return <0 means read failed, >0 means the number of bytes read
     * @throws RemoteException RemoteException
     */
    public synchronized int readKernelData(String[] taglist, byte[] buffer) throws RemoteException {
        if (taglist == null) {
            Log.d(TAG, "readKernelData taglist == null");
            return PARAM_ERROR;
        }
        if (buffer == null) {
            Log.d(TAG, "readKernelData buffer == null");
            return PARAM_ERROR;
        }
        Log.d(TAG, "readKernelData start");
        int bufferLen = 0;
        int bufferInLen = buffer.length;
        for (String tag : taglist) {
            int tagInt = Integer.parseInt(tag, 16);
            byte[] value = new byte[500];
            int retTlv = 0;
            if (cardType == CardType.IC) {
                value = ContactCardProcess.getInstance().getTlvData(tag);
                if (value != null) {
                    retTlv = value.length;
                }
            } else {
                value = ClsCardProcess.getInstance().getTlvData(tag);
                if (value != null) {
                    retTlv = value.length;
                }
            }
            Log.d(TAG, "readKernelData retTlv: " + retTlv + "; tag: " + tag + "; tagInt: " + tagInt);
            if (retTlv > 0) {
                if (bufferLen <= bufferInLen) {
                    System.arraycopy(value, 0, buffer, bufferLen, retTlv);
                    bufferLen += retTlv;
                }
            }
        }
        Log.d(TAG, "readKernelData end bufferLen: " + bufferLen + "; buffer: " + BytesUtil.bytes2HexString(buffer));
        return bufferLen;
    }

    /**
     * Set the TLV data
     *
     * @param tag   tlv data
     * @param value tlv result [output param]
     * @throws RemoteException RemoteException
     */
    public void setTlv(int tag, byte[] value) throws RemoteException {
        String tagStr = Integer.toString(tag,16);
        Log.d(TAG, "setTlv tag: " + tagStr + "; value: " + BytesUtil.bytes2HexString(value));
        ContactCardProcess.getInstance().setTlvData(tagStr, value);
        ClsCardProcess.getInstance().setTlvData(tagStr, value);
    }

    /**
     * Get the TLV data
     *
     * @param tag   tlv tag
     * @return value tlv data
     * @throws RemoteException RemoteException
     */
    public byte[] getTlv(int tag) throws RemoteException {
        Log.d(TAG, "tag: " + tag);
        String tagStr = Integer.toString(tag,16);
        if (cardType == CardType.IC) {
            return ContactCardProcess.getInstance().getTlvData(tagStr);
        } else {
            return ClsCardProcess.getInstance().getTlvData(tagStr);
        }
    }

    /**
     * import amount
     *
     * @param amt amount
     * @return whether succeed
     * @throws RemoteException RemoteException
     */
    public boolean importAmount(long amt,long otherAmt) throws RemoteException {
        Log.d(TAG, "importAmount amt: " + amt);
        boolean isImportAmount;
        if (cardType == CardType.RF) {
            isImportAmount = ClsCardProcess.getInstance().importAmount(amt,otherAmt);
        } else {
            isImportAmount = ContactCardProcess.getInstance().importAmount(amt,otherAmt);
        }
        return isImportAmount;
    }

    /**
     * Import the application selection results
     *
     * @param index app index
     * @return whether succeed
     * @throws RemoteException RemoteException
     */
    public boolean importAidSelectRes(int index) throws RemoteException {
        Log.d(TAG, "importAidSelectRes");
        boolean isImportAidRes;
        if (cardType == CardType.RF) {
            isImportAidRes = ClsCardProcess.getInstance().importAidSelectRes(index);
        } else {
            isImportAidRes = ContactCardProcess.getInstance().importAidSelectRes(index);
        }
        return isImportAidRes;
    }

    /**
     * import result of select aid res
     *
     * @param res true:confirm success, false: confirm failed
     * @return whether succeed
     * @throws RemoteException
     */
    public boolean importFinalAidSelectRes(boolean res) throws RemoteException {
        Log.d(TAG, "importFinalAidSelectRes res: " + res);
        boolean isimportFinalAidSelectRes;
        if (cardType == CardType.RF) {
            isimportFinalAidSelectRes = ClsCardProcess.getInstance().importFinalAidSelectRes(res);
        } else {
            isimportFinalAidSelectRes = ContactCardProcess.getInstance().importFinalAidSelectRes(res);
        }
        Log.d(TAG, "importFinalAidSelectRes: " + isimportFinalAidSelectRes);
        return isimportFinalAidSelectRes;
    }

    /**
     * import result of confirm card info
     *
     * @param res true: confirm success, false: confirm failed
     * @return whether succeed
     * @throws RemoteException RemoteException
     */
    public boolean importConfirmCardInfoRes(boolean res) throws RemoteException  {
        Log.d(TAG, "importMsgConfirmRes res: " + res);
        boolean isImportMsgConfirmRes;
        if (cardType == CardType.RF) {
            isImportMsgConfirmRes = ClsCardProcess.getInstance().importConfirmCardInfoRes(res);
        } else {
            isImportMsgConfirmRes = ContactCardProcess.getInstance().importConfirmCardInfoRes(res);
        }
        return isImportMsgConfirmRes;
    }

    /**
     * import pin
     *
     * @param pin pin
     * @return whether succeed
     * @throws RemoteException RemoteException
     */
    public boolean importPin(String pin) throws RemoteException {
        Log.d(TAG, "importPin pin: " + pin);
        boolean isImportPin;
        if (cardType == CardType.RF) {
            isImportPin = ClsCardProcess.getInstance().importPin(pin);
        } else {
            isImportPin = ContactCardProcess.getInstance().importPin(pin);
        }
        Log.d(TAG, "isimportPin: " + isImportPin);
        return isImportPin;
    }

    /**
     * confirm OfflinePin Entry continue or not
     *
     * @param isContinue true: continue
     * @return whether succeed
     * @throws RemoteException RemoteException
     */
    public boolean confirmOfflinePinEntry(boolean isContinue) throws RemoteException {
        Log.d(TAG, "confirmOfflinePinEntry isContinue: " + isContinue);
        boolean result;
        if (cardType == CardType.RF) {
            result = ClsCardProcess.getInstance().confirmOfflinePinEntry(isContinue);
        } else {
            result = ContactCardProcess.getInstance().confirmOfflinePinEntry(isContinue);
        }
        Log.d(TAG, "result: " + result);
        return result;
    }

    /**
     * import certification result
     *
     * @param res Authentication result, true: authentication successful, false: authentication failed
     * @return whether succeed
     * @throws RemoteException RemoteException
     */
    public boolean importUserAuthRes(boolean res) throws RemoteException {
        Log.d(TAG, "importUserAuthRes");
        boolean isImportAuthRes;
        if (cardType == CardType.RF) {
            isImportAuthRes = ClsCardProcess.getInstance().importUserAuthRes(res);
        } else {
            isImportAuthRes = ContactCardProcess.getInstance().importUserAuthRes(res);
        }
        Log.d(TAG, "importUserAuthRes: " + isImportAuthRes);
        return isImportAuthRes;
    }

    /**
     * Import the message of confirm the results
     *
     * @param confirm Confirm the result, true: confirm, false: cancel
     * @return whether succeed
     * @throws RemoteException RemoteException
     */
    public boolean importMsgConfirmRes(boolean confirm) throws RemoteException {
        Log.d(TAG, "importMsgConfirmRes confirm: " + confirm);
        boolean isImportMsgConfirmRes;
        if (cardType == CardType.RF) {
            isImportMsgConfirmRes = ClsCardProcess.getInstance().importMsgConfirmRes(confirm);
        } else {
            isImportMsgConfirmRes = ContactCardProcess.getInstance().importMsgConfirmRes(confirm);
        }
        Log.d(TAG, "isImportMsgConfirmRes: " + isImportMsgConfirmRes);
        return isImportMsgConfirmRes;
    }

    /**
     * import electronic cash prompt information confirm result
     *
     * @param confirm Confirm the result, true: confirm, false: cancel
     * @return whether succeed
     * @throws RemoteException RemoteException
     */
    public boolean importECashTipConfirmRes(boolean confirm) throws RemoteException {
        Log.d(TAG, "importECashTipConfirmRes confirm: " + confirm);
        boolean isImportECashTipRes;
        if (cardType == CardType.RF) {
            isImportECashTipRes = ClsCardProcess.getInstance().importECashTipConfirmRes(confirm);
        } else {
            isImportECashTipRes = ContactCardProcess.getInstance().importECashTipConfirmRes(confirm);
        }
        Log.d(TAG, "isimportECashTipConfirmRes: " + isImportECashTipRes);
        return isImportECashTipRes;
    }

    /**
     * import online result
     *
     * @param onlineRes whether online success
     * @param respCode  Background response code
     * @param icc55     The 55 field data returned by the card
     * @return whether succeed
     * @throws RemoteException RemoteException
     */
    public boolean importOnlineResp(boolean onlineRes, String respCode, String icc55) throws RemoteException {
        Log.d(TAG, "importOnlineResp onlineRes: " + onlineRes + "; respCode: " + respCode + "; icc55: " + icc55);
        boolean isImportOnlineResp;
        if (cardType == CardType.RF) {
            isImportOnlineResp = ClsCardProcess.getInstance().importOnlineResp(onlineRes, respCode, icc55);
        } else {
            isImportOnlineResp = ContactCardProcess.getInstance().importOnlineResp(onlineRes, respCode, icc55);
        }
        Log.d(TAG, "isImportOnlineResp: " + isImportOnlineResp);
        return isImportOnlineResp;
    }
}
