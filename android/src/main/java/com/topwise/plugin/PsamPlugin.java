package com.topwise.plugin;

import android.os.RemoteException;

import androidx.annotation.NonNull;
import com.topwise.cloudpos.aidl.psam.AidlPsam;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;

/**
 * @author caixh
 * @description
 * @date 2023/3/23 17:02
 */
public class PsamPlugin extends BasePlugin {
    public AidlPsam psam;

    @Override
    public void onMethodCall(@NonNull MethodCall call, @NonNull MethodChannel.Result result) {
        try {
            if (call.method.equals("init")) {
                int devid = call.argument("devid");
                psam = TopUsdkManager.getInstance().getPsamCardReader(devid);
                result.success(psam!=null);
                return;
            }
            if(psam==null){
                result.error("USDK Remote Exception","USDK service is not connected",null);
                return;
            }
            if (call.method.equals("open")) {
                result.success(psam.open());
            } else if(call.method.equals("close")) {
                result.success(psam.close());
            } else if(call.method.equals("reset")) {
                int cardType = call.argument("cardType");
                result.success(psam.reset(cardType));
            } else if(call.method.equals("apduComm")) {
                byte[] apdu = call.argument("apdu");
                result.success(psam.apduComm(apdu));
            } else if(call.method.equals("setETU")) {
                byte etuVal = call.argument("etuVal");
                result.success(psam.setETU(etuVal));
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
        return "psam";
    }
}
