package com.topwise.plugin;

import android.os.RemoteException;
import androidx.annotation.NonNull;
import com.topwise.cloudpos.aidl.shellmonitor.InstructionSendDataCallback;
import java.util.concurrent.CountDownLatch;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;

/**
 * @author caixh
 * @description
 * @date 2023/4/7 9:58
 */
public class ShellMonitorPlugin extends BasePlugin {
    private byte mResultCode;
    private byte[] mResultData;

    @Override
    public void onMethodCall(@NonNull MethodCall call, @NonNull MethodChannel.Result result) {
        try {
            if(TopUsdkManager.shell==null){
                result.error("USDK Remote Exception","USDK service is not connected",null);
                return;
            }
            if (call.method.equals("executeCmd")) {
                String cmd = call.argument("cmd");
                result.success(TopUsdkManager.shell.executeCmd(cmd));
            } else if(call.method.equals("recovery")) {
                TopUsdkManager.shell.recovery();
                result.success(null);
            } else if(call.method.equals("canRecovery")) {
                result.success(TopUsdkManager.shell.canRecovery());
            } else if(call.method.equals("getRootAuth")) {
                String rootAuth = call.argument("rootAuth");
                result.success(TopUsdkManager.shell.getRootAuth(rootAuth));
            } else if(call.method.equals("executeRootCMD")) {
                String rootkey = call.argument("rootkey");
                String authToken = call.argument("authToken");
                String cmdParams = call.argument("cmdParams");
                result.success(TopUsdkManager.shell.executeRootCMD(rootkey,authToken,cmdParams));
            } else if(call.method.equals("getHardwareSNPlaintext")) {
                result.success(TopUsdkManager.shell.getHardwareSNPlaintext());
            } else if(call.method.equals("getHardwareSNCiphertext")) {
                byte[] b = call.argument("b");
                result.success(TopUsdkManager.shell.getHardwareSNCiphertext(b));
            } else if(call.method.equals("getSM4Ncryption")) {
                byte[] key = call.argument("key");
                byte[] b = call.argument("b");
                result.success(TopUsdkManager.shell.getSM4Ncryption(key,b));
            } else if(call.method.equals("sendIns")) {
                int timeOutMs = call.argument("timeOutMs");
                byte type = call.argument("type");
                byte cmd = call.argument("cmd");
                byte len = call.argument("len");
                byte[] insData = call.argument("insData");
                final CountDownLatch countDownLatch = new CountDownLatch(1);
                TopUsdkManager.shell.sendIns(timeOutMs,type,cmd,len,insData,new InstructionSendDataCallback.Stub(){
                    @Override
                    public void onReceiveData(byte resultCode, byte[] tlvArray) throws RemoteException {
                        mResultCode = resultCode;
                        mResultData = tlvArray;
                        countDownLatch.countDown();
                    }
                });
                try{
                    countDownLatch.await();
                } catch(InterruptedException e){
                    e.printStackTrace();
                }
                result.success(mResultCode==0?mResultData:null);
            } else if(call.method.equals("getSM4Ncryption")) {
                byte[] key = call.argument("key");
                byte[] b = call.argument("b");
                result.success(TopUsdkManager.shell.getSM4Ncryption(key,b));
            } else {
                result.notImplemented();
            }
        } catch (RemoteException e) {
            e.printStackTrace();
            result.error("USDK Remote Exception",e.getMessage(),e.getStackTrace());
        }
    }

    @Override
    public String getPluginName() {
        return "shellmonitor";
    }
}
