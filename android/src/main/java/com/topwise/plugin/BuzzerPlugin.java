package com.topwise.plugin;

import android.os.RemoteException;

import androidx.annotation.NonNull;

import com.topwise.cloudpos.aidl.buzzer.AidlBuzzer;

import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;

/**
 * @author caixh
 * @description
 * @date 2023/3/13 10:56
 */
public class BuzzerPlugin extends BasePlugin {

    @Override
    public void onMethodCall(@NonNull MethodCall call, @NonNull MethodChannel.Result result) {
        try {
            if(TopUsdkManager.buzzer==null){
                result.error("USDK Remote Exception","USDK service is not connected",null);
                return;
            }
            if (call.method.equals("beep")) {
                int mode = call.argument("mode");
                int ms = call.argument("ms");
                result.success(TopUsdkManager.buzzer.beep(mode,ms));
            } else if(call.method.equals("stopBeep")) {
                result.success(TopUsdkManager.buzzer.stopBeep());
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
        return "buzzer";
    }
}
