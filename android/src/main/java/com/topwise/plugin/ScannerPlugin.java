package com.topwise.plugin;

import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.RemoteException;
import android.util.Log;
import androidx.annotation.NonNull;
import com.topwise.cloudpos.aidl.camera.AidlCameraScanCodeListener;
import com.topwise.cloudpos.data.AidlScanParam;
import java.util.HashMap;
import java.util.Map;
import io.flutter.plugin.common.EventChannel;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;

/**
 * @author caixh
 * @description
 * @date 2023/4/21 10:16
 */
public class ScannerPlugin extends BasePlugin {
    EventChannel.EventSink eventSink;

    @Override
    public void onMethodCall(@NonNull MethodCall call, @NonNull MethodChannel.Result result) {
        try {
            if(TopUsdkManager.camera==null){
                Log.d(getPluginName(), "TopUsdkManager.camera==null");
                result.error("USDK Remote Exception","USDK service is not connected",null);
                return;
            }
            if (call.method.equals("scanCode")) {
                final Bundle bundle = new Bundle();
                int cameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
                int timeout = 60;
                String title = "SALE";
                String reminder = "Please scan code:";
                String amt = "123.45";
                if(call.hasArgument("cameraId")){
                    cameraId = call.argument("cameraId");
                }
                if(call.hasArgument("timeout")){
                    timeout = call.argument("timeout");
                }
                if(call.hasArgument("title")){
                    title = call.argument("title");
                }
                if(call.hasArgument("reminder")){
                    reminder = call.argument("reminder");
                }
                if(call.hasArgument("amt")){
                    amt = call.argument("amt");
                }
                AidlScanParam param = new AidlScanParam(cameraId,timeout,title,reminder,amt);
                bundle.putSerializable(AidlScanParam.SCAN_CODE, param);
                new EventChannel(messenger, "scanCode").setStreamHandler(new EventChannel.StreamHandler() {
                    @Override
                    public void onListen(Object arguments, EventChannel.EventSink events) {
                        eventSink = events;
                        Handler handler = new Handler(Looper.getMainLooper());
                        Log.d(getPluginName(), "Scanner.scanCode");
                        try {
                            TopUsdkManager.camera.scanCode(bundle,new AidlCameraScanCodeListener.Stub(){
                                @Override
                                public void onResult(String result) throws RemoteException {
                                    Map<String,Object> map = new HashMap<String,Object>();
                                    map.put("name","onResult");
                                    map.put("result",result);
                                    handler.post(()->{
                                        eventSink.success(map);
                                    });
                                }

                                @Override
                                public void onCancel() throws RemoteException {
                                    Map<String,Object> map = new HashMap<String,Object>();
                                    map.put("name","onCancel");
                                    handler.post(()->{
                                        eventSink.success(map);
                                    });
                                }

                                @Override
                                public void onError(int errorCode) throws RemoteException {
                                    Map<String,Object> map = new HashMap<String,Object>();
                                    map.put("name","onError");
                                    map.put("errorCode",errorCode);
                                    handler.post(()->{
                                        eventSink.success(map);
                                    });
                                }

                                @Override
                                public void onTimeout() throws RemoteException {
                                    Map<String,Object> map = new HashMap<String,Object>();
                                    map.put("name","onTimeout");
                                    handler.post(()->{
                                        eventSink.success(map);
                                    });
                                }
                            });
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }

                    }
                    @Override
                    public void onCancel(Object arguments) {
                        eventSink.error("","onCancel",arguments);
                    }
                });
                result.success(null);
            } else if (call.method.equals("stopScan")) {
                TopUsdkManager.camera.stopScan();
                result.success(null);
            } else {
                result.notImplemented();
            }
        } catch (RemoteException e) {
            e.printStackTrace();
            Log.d(getPluginName(), "Remote Exception"+e.getStackTrace());
            result.error("USDK Remote Exception",e.getMessage(),e.getStackTrace());
        }
    }

    @Override
    public String getPluginName() {
        return "scanner";
    }
}

