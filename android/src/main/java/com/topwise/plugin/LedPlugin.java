package com.topwise.plugin;

import android.os.RemoteException;
import android.util.Log;

import androidx.annotation.NonNull;

import com.topwise.cloudpos.aidl.led.AidlLed;

import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel.Result;

/**
 * @author caixh
 * @description
 * @date 2023/3/13 10:07
 */
public class LedPlugin extends BasePlugin {

    @Override
    public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
        try {
            if(TopUsdkManager.led==null){
                result.error("USDK Remote Exception","USDK service is not connected",null);
                return;
            }
            if (call.method.equals("setLed")) {
                int light = call.argument("light");
                boolean isOn = call.argument("isOn");
                result.success(TopUsdkManager.led.setLed(light,isOn));
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
        return "led";
    }
}
