package com.topwise.plugin.emv;

import android.os.RemoteException;

import com.topwise.cloudpos.aidl.emv.PCardLoadLog;
import com.topwise.cloudpos.aidl.emv.PCardTransLog;

public interface OnEmvProcessListener {
    /**请求设置EMV aid参数*/
    void finalAidSelect() throws RemoteException;
    /**请求输入金额 ，简易流程时不回调此方法*/
    void requestImportAmount(int type) throws RemoteException;
    /**请求提示信息*/
    void requestTipsConfirm(String msg) throws RemoteException;
    /**请求多应用选择*/
    void requestAidSelect(int times, String[] aids) throws RemoteException;
    /**请求确认是否使用电子现金*/
    void requestEcashTipsConfirm() throws RemoteException;
    /**请求确认卡信息*/
    void onConfirmCardInfo(String cardNo) throws RemoteException;
    /** 请求确认脱机Pin输入次数 */
    void onConfirmOfflinePinEntry(int times) throws RemoteException;
    /** 请求导入PIN */
    void requestImportPin(int type, boolean lasttimeFlag, long amt) throws RemoteException;
    /** 请求身份认证 */
    void requestUserAuth(int certype, String certnumber) throws RemoteException;
    /**请求联机*/
    void onRequestOnline() throws RemoteException;
    /**返回读取卡片脱机余额结果*/
    void onReadCardOffLineBalance(String moneyCode, String balance, String secondMoneyCode, String secondBalance) throws RemoteException;
    /**返回读取卡片交易日志结果*/
    void onReadCardTransLog(PCardTransLog[] log) throws RemoteException;
    /**返回读取卡片圈存日志结果*/
    void onReadCardLoadLog(String atc, String checkCode, PCardLoadLog[] logs) throws RemoteException;
    /**交易结果
     批准: 0x01
     拒绝: 0x02
     终止: 0x03
     FALLBACK: 0x04
     采用其他界面: 0x05
     其他：0x06
     EMV简易流程不回调此方法
     */
    void onTransResult(int result) throws RemoteException;
    /**出错*/
    void onError(int erroCode) throws RemoteException;
//    void onEnd(EmvResult erroCode) throws RemoteException;
}
