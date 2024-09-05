package com.topwise.plugin;

import android.os.RemoteException;
import android.util.Log;
import androidx.annotation.NonNull;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;

/**
 * @author caixh
 * @description
 * @date 2023/3/13 11:13
 */
public class ICCardPlugin extends BasePlugin {

    @Override
    public void onMethodCall(@NonNull MethodCall call, @NonNull MethodChannel.Result result) {
        try {
            if(TopUsdkManager.icCard==null){
                result.error("USDK Remote Exception","USDK service is not connected",null);
                return;
            }
            if (call.method.equals("open")) {
                result.success(TopUsdkManager.icCard.open());
            } else if(call.method.equals("close")) {
                result.success(TopUsdkManager.icCard.close());
            } else if(call.method.equals("halt")) {
                result.success(TopUsdkManager.icCard.halt());
            } else if(call.method.equals("reset")) {
                int cardType = call.argument("cardType");
                result.success(TopUsdkManager.icCard.reset(cardType));
            } else if(call.method.equals("isExist")) {
                result.success(TopUsdkManager.icCard.isExist());
            } else if(call.method.equals("apduComm")) {
                byte[] apdu = call.argument("apdu");
                result.success(TopUsdkManager.icCard.apduComm(apdu));
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
        return "iccard";
    }
}
