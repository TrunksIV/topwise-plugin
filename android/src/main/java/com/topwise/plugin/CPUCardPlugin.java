package com.topwise.plugin;

import android.os.RemoteException;
import android.util.Log;
import androidx.annotation.NonNull;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;

/**
 * @author caixh
 * @description
 * @date 2023/3/23 15:50
 */
public class CPUCardPlugin extends BasePlugin {

    @Override
    public void onMethodCall(@NonNull MethodCall call, @NonNull MethodChannel.Result result) {
        try {
            if(TopUsdkManager.cpuCard==null){
                result.error("USDK Remote Exception","USDK service is not connected",null);
                return;
            }
            if (call.method.equals("open")) {
                int cardType = call.argument("cardType");
                result.success(TopUsdkManager.cpuCard.open(cardType)==0);
            } else if(call.method.equals("close")) {
                TopUsdkManager.cpuCard.close();
                result.success(null);
            } else if(call.method.equals("verifyPwd")) {
                int cardType = call.argument("cardType");
                byte[] psw = call.argument("psw");
                result.success(TopUsdkManager.cpuCard.verifyPwd(cardType,psw));
            } else if(call.method.equals("changePassword")) {
                int cardType = call.argument("cardType");
                byte[] oldpsw = call.argument("oldpsw");
                byte[] newpsw = call.argument("newpsw");
                result.success(TopUsdkManager.cpuCard.changePassword(cardType,oldpsw,newpsw));
            } else if(call.method.equals("read")) {
                int cardType = call.argument("cardType");
                int offset = call.argument("offset");
                int length = call.argument("length");
                result.success(TopUsdkManager.cpuCard.read(cardType,offset,length));
            } else if(call.method.equals("write")) {
                int cardType = call.argument("cardType");
                int offset = call.argument("offset");
                byte[] data = call.argument("data");
                result.success(TopUsdkManager.cpuCard.write(cardType,offset,data));
            } else if(call.method.equals("openWithATRVerification")) {
                int cardType = call.argument("cardType");
                byte[] atrData = call.argument("atrData");
                result.success(TopUsdkManager.cpuCard.openWithATRVerification(cardType,atrData));
            } else if(call.method.equals("writeAt24c")) {
                int cardType = call.argument("cardType");
                int offset = call.argument("offset");
                byte[] data = call.argument("data");
                result.success(TopUsdkManager.cpuCard.writeAt24c(cardType,offset,data));
            } else if(call.method.equals("readAt24c")) {
                int cardType = call.argument("cardType");
                int offset = call.argument("offset");
                int length = call.argument("length");
                result.success(TopUsdkManager.cpuCard.readAt24c(cardType,offset,length));
            } else if(call.method.equals("verifyAT88SCPwd")) {
                int cardType = call.argument("cardType");
                int pwdType = call.argument("pwdType");
                int pwdGroup = call.argument("pwdGroup");
                byte[] pwd = call.argument("pwd");
                result.success(TopUsdkManager.cpuCard.verifyAT88SCPwd(cardType,pwdType,pwdGroup,pwd));
            } else if (call.method.equals("getAT88SCPwdCheckNum")) {
                int cardType = call.argument("cardType");
                result.success(TopUsdkManager.cpuCard.getAT88SCPwdCheckNum(cardType));
            } else if(call.method.equals("initAT88SCAuth")) {
                int cardType = call.argument("cardType");
                byte[] authData = call.argument("authData");
                result.success(TopUsdkManager.cpuCard.initAT88SCAuth(cardType,authData));
            } else if(call.method.equals("verifyAT88SCAuth")) {
                int cardType = call.argument("cardType");
                byte[] authData = call.argument("authData");
                result.success(TopUsdkManager.cpuCard.verifyAT88SCAuth(cardType,authData));
            } else if(call.method.equals("readAT88SCDomainData")) {
                int cardType = call.argument("cardType");
                int zoneNum = call.argument("zoneNum");
                int offset = call.argument("offset");
                int len = call.argument("len");
                result.success(TopUsdkManager.cpuCard.readAT88SCDomainData(cardType,zoneNum,offset,len));
            } else if(call.method.equals("writeAT88SCDomainData")) {
                int cardType = call.argument("cardType");
                int zoneNum = call.argument("zoneNum");
                int offset = call.argument("offset");
                byte[] data = call.argument("data");
                result.success(TopUsdkManager.cpuCard.writeAT88SCDomainData(cardType,zoneNum,offset,data));
            } else if (call.method.equals("readAT88SCFuseMark")) {
                int cardType = call.argument("cardType");
                result.success(TopUsdkManager.cpuCard.readAT88SCFuseMark(cardType));
            } else if(call.method.equals("writeAT88SCFuseMark")) {
                int cardType = call.argument("cardType");
                int fuseMark = call.argument("fuseMark");
                result.success(TopUsdkManager.cpuCard.writeAT88SCFuseMark(cardType,fuseMark));
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
        return "cpucard";
    }
}
