package com.topwise.plugin;

import android.os.RemoteException;
import androidx.annotation.NonNull;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;

/**
 * @author caixh
 * @description
 * @date 2023/3/30 19:17
 */
public class RfCardPlugin extends BasePlugin {

    @Override
    public void onMethodCall(@NonNull MethodCall call, @NonNull MethodChannel.Result result) {
        try {
            if(TopUsdkManager.rfCard==null){
                result.error("USDK Remote Exception","USDK service is not connected",null);
                return;
            }
            if (call.method.equals("open")) {
                result.success(TopUsdkManager.rfCard.open());
            } else if(call.method.equals("close")) {
                result.success(TopUsdkManager.rfCard.close());
            } else if(call.method.equals("reset")) {
                int cardType = call.argument("cardType");
                result.success(TopUsdkManager.rfCard.reset(cardType));
            } else if(call.method.equals("isExist")) {
                    result.success(TopUsdkManager.rfCard.isExist());
            } else if(call.method.equals("isExistMT")) {
                result.success(TopUsdkManager.rfCard.isExistMT());
            } else if(call.method.equals("apduComm")) {
                byte[] apdu = call.argument("apdu");
                result.success(TopUsdkManager.rfCard.apduComm(apdu));
            } else if(call.method.equals("halt")) {
                result.success(TopUsdkManager.rfCard.halt());
            } else if(call.method.equals("getCardType")) {
                result.success(TopUsdkManager.rfCard.getCardType());
            } else if(call.method.equals("auth")) {
                int type = call.argument("type");
                byte blockaddr = PluginUtils.getByte(call.argument("blockaddr"));
                byte[] keydata = call.argument("keydata");
                byte[] resetRes = call.argument("resetRes");
                result.success(TopUsdkManager.rfCard.auth(type,blockaddr,keydata,resetRes));
            } else if(call.method.equals("readBlock")) {
                byte blockaddr = PluginUtils.getByte(call.argument("blockaddr"));
                byte[] blockdata = new byte[16];
                int ret = TopUsdkManager.rfCard.readBlock(blockaddr,blockdata);
                if(ret==0){
                    result.success(blockdata);
                } else{
                    result.success(null);
                }
            } else if(call.method.equals("readBlockMT")) {
                byte blockaddr = PluginUtils.getByte(call.argument("blockaddr"));
                byte[] blockdata = new byte[16];
                int ret = TopUsdkManager.rfCard.readBlockMT(blockaddr,blockdata);
                if(ret==0){
                    result.success(blockdata);
                } else{
                    result.success(null);
                }
            } else if(call.method.equals("writeBlock")) {
                byte blockaddr = PluginUtils.getByte(call.argument("blockaddr"));
                byte[] data = call.argument("data");
                result.success(TopUsdkManager.rfCard.writeBlock(blockaddr,data));
            } else if(call.method.equals("addValue")) {
                byte blockaddr = PluginUtils.getByte(call.argument("blockaddr"));
                byte[] data = call.argument("data");
                result.success(TopUsdkManager.rfCard.addValue(blockaddr,data));
            } else if(call.method.equals("reduceValue")) {
                byte blockaddr = PluginUtils.getByte(call.argument("blockaddr"));
                byte[] data = call.argument("data");
                result.success(TopUsdkManager.rfCard.reduceValue(blockaddr,data));
            } else if(call.method.equals("readBlockX")) {
                byte blockaddr = PluginUtils.getByte(call.argument("blockaddr"));
                result.success(TopUsdkManager.rfCard.readBlockX(blockaddr));
            } else if(call.method.equals("getCardCode")) {
                result.success(TopUsdkManager.rfCard.getCardCode());
            } else if(call.method.equals("getATQA")) {
                result.success(TopUsdkManager.rfCard.getATQA());
            } else if(call.method.equals("activateTypeAOrIDCard")) {
                int cardType = call.argument("cardType");
                result.success(TopUsdkManager.rfCard.activateTypeAOrIDCard(cardType));
            } else if(call.method.equals("getFelicaProtocolData")) {
                result.success(TopUsdkManager.rfCard.getFelicaProtocolData());
            } else if(call.method.equals("felicaTransceive")) {
                byte[] apdu = call.argument("apdu");
                result.success(TopUsdkManager.rfCard.felicaTransceive(apdu));
            } else if(call.method.equals("getUID")) {
                result.success(TopUsdkManager.rfCard.getUID());
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
        return "rfcard";
    }
}
