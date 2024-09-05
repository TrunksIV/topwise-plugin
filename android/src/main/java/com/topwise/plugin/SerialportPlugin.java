package com.topwise.plugin;

import android.os.RemoteException;
import androidx.annotation.NonNull;
import com.topwise.cloudpos.aidl.serialport.AidlSerialport;

import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;

/**
 * @author caixh
 * @description
 * @date 2023/4/7 9:07
 */
public class SerialportPlugin extends BasePlugin {
    private int port = -1;
    private AidlSerialport serialport;

    @Override
    public void onMethodCall(@NonNull MethodCall call, @NonNull MethodChannel.Result result) {
        try {

            if (call.method.equals("open")) {
                int p = call.argument("port");
                serialport = TopUsdkManager.getInstance().getSerialPort(port);
                if(serialport==null){
                    result.error("USDK Remote Exception","USDK service is not connected",null);
                    return;
                }
                if(serialport.open()){
                    port = p;
                    result.success(true);
                }else{
                    result.success(false);
                }
                return;
            }
            if(port==-1){
                result.error("not open","SerialPort not opened",null);
                return;
            }
            serialport = TopUsdkManager.getInstance().getSerialPort(port);
            if(serialport==null){
                result.error("USDK Remote Exception","USDK service is not connected",null);
                return;
            }
            if(call.method.equals("init")) {
                int botratebyte = call.argument("botratebyte");
                byte dataBits = call.argument("dataBits");
                byte parity = call.argument("parity");
                byte StopBits = call.argument("StopBits");
                result.success(serialport.init(botratebyte,dataBits,parity,StopBits));
            } else if(call.method.equals("sendData")) {
                int timeout = call.argument("timeout");
                byte[] data = call.argument("data");
                result.success(serialport.sendData(data,timeout));
            } else if(call.method.equals("readData")) {
                int timeout = call.argument("timeout");
                result.success(serialport.readData(timeout));
            } else if(call.method.equals("close")) {
                result.success(serialport.close());
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
        return "serialport";
    }
}
