package com.topwise.plugin;

import android.os.Bundle;
import android.os.RemoteException;

import androidx.annotation.NonNull;

import com.topwise.cloudpos.ExpandFunctionConstant;

import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;

/**
 * @author caixh
 * @description
 * @date 2023/3/13 11:32
 */
public class ExtendPlugin extends BasePlugin {

    @Override
    public void onMethodCall(@NonNull MethodCall call, @NonNull MethodChannel.Result result) {
        try {
            if (call.method.equals("EnableMasterPosKey")) {
                boolean enable = call.argument("enable");
                Bundle bundle = new Bundle();
                bundle.putString(ExpandFunctionConstant.Key.FUNCTION_NAME, "EnableMasterPosKey");
                bundle.putBoolean("enable", enable);
                Bundle ret = TopUsdkManager.getInstance().expandFunction(bundle);
                if(ret==null) {
                    result.error("USDK Remote Exception","USDK service is not connected",null);
                } else if(ret.getInt(ExpandFunctionConstant.Key.ERROR_CODE)==ExpandFunctionConstant.Error.SUCCESS) {
                    result.success(ret.getBoolean(ExpandFunctionConstant.Key.RESULT,false));
                } else {
                    result.error(Integer.toString(ret.getInt(ExpandFunctionConstant.Key.ERROR_CODE)),"NOT_SUPPORT",null);
                }
            } else if (call.method.equals("goToSleep")) {
                Bundle bundle = new Bundle();
                bundle.putString(ExpandFunctionConstant.Key.FUNCTION_NAME, "goToSleep");
                Bundle ret = TopUsdkManager.getInstance().expandFunction(bundle);
                if(ret==null) {
                    result.error("USDK Remote Exception","USDK service is not connected",null);
                } else if(ret.getInt(ExpandFunctionConstant.Key.ERROR_CODE)==ExpandFunctionConstant.Error.SUCCESS) {
                    result.success(null);
                } else {
                    result.error(Integer.toString(ret.getInt(ExpandFunctionConstant.Key.ERROR_CODE)),"NOT_SUPPORT",null);
                }
            } else if (call.method.equals("wakeUp")) {
                Bundle bundle = new Bundle();
                bundle.putString(ExpandFunctionConstant.Key.FUNCTION_NAME, "wakeUp");
                Bundle ret = TopUsdkManager.getInstance().expandFunction(bundle);
                if(ret==null) {
                    result.error("USDK Remote Exception","USDK service is not connected",null);
                } else if(ret.getInt(ExpandFunctionConstant.Key.ERROR_CODE)==ExpandFunctionConstant.Error.SUCCESS) {
                    result.success(null);
                } else {
                    result.error(Integer.toString(ret.getInt(ExpandFunctionConstant.Key.ERROR_CODE)),"NOT_SUPPORT",null);
                }
            } else if(call.method.equals("lockStatusBar")) {
                boolean isLock = call.argument("isLock");
                Bundle bundle = new Bundle();
                bundle.putString(ExpandFunctionConstant.Key.FUNCTION_NAME, "lockStatusBar");
                bundle.putBoolean("isLock", isLock);
                Bundle ret = TopUsdkManager.getInstance().expandFunction(bundle);
                if(ret==null) {
                    result.error("USDK Remote Exception","USDK service is not connected",null);
                } else if(ret.getInt(ExpandFunctionConstant.Key.ERROR_CODE)==ExpandFunctionConstant.Error.SUCCESS) {
                    result.success(null);
                } else {
                    result.error(Integer.toString(ret.getInt(ExpandFunctionConstant.Key.ERROR_CODE)),"NOT_SUPPORT",null);
                }
            } else if(call.method.equals("switchSimCard")) {
                int slotId = call.argument("slotId");
                Bundle bundle = new Bundle();
                bundle.putString(ExpandFunctionConstant.Key.FUNCTION_NAME, "switchSimCard");
                bundle.putInt("slotId", slotId);
                Bundle ret = TopUsdkManager.getInstance().expandFunction(bundle);
                if(ret==null) {
                    result.error("USDK Remote Exception","USDK service is not connected",null);
                } else if(ret.getInt(ExpandFunctionConstant.Key.ERROR_CODE)==ExpandFunctionConstant.Error.SUCCESS) {
                    result.success(null);
                } else {
                    result.error(Integer.toString(ret.getInt(ExpandFunctionConstant.Key.ERROR_CODE)),"NOT_SUPPORT",null);
                }
            } else if(call.method.equals("lockSimCard")) {
                int slotId = call.argument("slotId");
                boolean isLock = call.argument("isLock");
                Bundle bundle = new Bundle();
                bundle.putString(ExpandFunctionConstant.Key.FUNCTION_NAME, "lockStatusBar");
                bundle.putInt("slotId", slotId);
                bundle.putBoolean("isLock", isLock);
                Bundle ret = TopUsdkManager.getInstance().expandFunction(bundle);
                if(ret==null) {
                    result.error("USDK Remote Exception","USDK service is not connected",null);
                } else if(ret.getInt(ExpandFunctionConstant.Key.ERROR_CODE)==ExpandFunctionConstant.Error.SUCCESS) {
                    result.success(null);
                } else {
                    result.error(Integer.toString(ret.getInt(ExpandFunctionConstant.Key.ERROR_CODE)),"NOT_SUPPORT",null);
                }
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
        return "extend";
    }
}
