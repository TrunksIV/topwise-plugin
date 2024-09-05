package com.topwise.plugin;

import android.os.RemoteException;
import android.util.Log;
import androidx.annotation.NonNull;

import com.topwise.cloudpos.aidl.system.InstallAppObserver;
import com.topwise.cloudpos.aidl.system.UninstallAppObserver;

import io.flutter.plugin.common.EventChannel;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;

/**
 * @author caixh
 * @description
 * @date 2023/3/13 15:13
 */
public class SystemPlugin extends BasePlugin {
    EventChannel.EventSink eventSink;

    @Override
    public void onMethodCall(@NonNull MethodCall call, @NonNull MethodChannel.Result result) {
        try {
            if(TopUsdkManager.system==null){
                result.error("USDK Remote Exception","USDK service is not connected",null);
                return;
            }
            if(call.method.equals("getSerialNo")) {
                result.success(TopUsdkManager.system.getSerialNo());
            } else if (call.method.equals("installApp")) {
                final String filePath = call.argument("filePath");
                Log.d(getPluginName(),"installApp,filePath="+filePath);
                new EventChannel(messenger, "installApp").setStreamHandler(new EventChannel.StreamHandler() {
                    @Override
                    public void onListen(Object arguments, EventChannel.EventSink events) {
                        eventSink = events;
                        try {
                            Log.d(getPluginName(),"TopUsdkManager.system.installApp,filePath="+filePath);
                            TopUsdkManager.system.installApp(filePath, new InstallAppObserver.Stub() {
                                @Override
                                public void onInstallFinished() throws RemoteException {
                                    Log.d(getPluginName(),"onUninstallFinished");
                                    eventSink.success(null);
                                }

                                @Override
                                public void onInstallError(int i) throws RemoteException {
                                    Log.d(getPluginName(),"onInstallError i="+i);
                                    eventSink.success(i);
                                }
                            });
                        } catch (RemoteException e) {
                            e.printStackTrace();
                            eventSink.error("USDK Remote Exception",e.getMessage(),e.getStackTrace());
                        }
                    }

                    @Override
                    public void onCancel(Object arguments) {
                        eventSink.error("","onCancel",arguments);
                    }
                });
                result.success(null);
            } else if(call.method.equals("uninstallApp")) {
                final String packageName = call.argument("packageName");
                Log.d(getPluginName(),"uninstallApp,packageName="+packageName);
                new EventChannel(messenger, "uninstallApp").setStreamHandler(new EventChannel.StreamHandler() {
                    @Override
                    public void onListen(Object arguments, EventChannel.EventSink events) {
                        eventSink = events;
                        try {
                            Log.d(getPluginName(),"TopUsdkManager.system.uninstallApp,packageName="+packageName);
                            TopUsdkManager.system.uninstallApp(packageName, new UninstallAppObserver.Stub() {
                                @Override
                                public void onUninstallFinished() throws RemoteException {
                                    Log.d(getPluginName(),"onUninstallFinished");
                                    eventSink.success(null);
                                }

                                @Override
                                public void onUninstallError(int i) throws RemoteException {
                                    Log.d(getPluginName(),"onUninstallError i="+i);
                                    eventSink.success(i);
                                }
                            });
                        } catch (RemoteException e) {
                            e.printStackTrace();
                            eventSink.error("","",null);
                        }
                    }

                    @Override
                    public void onCancel(Object arguments) {
                        eventSink.error("","onCancel",arguments);
                    }
                });
                result.success(null);
            } else if(call.method.equals("getKsn")) {
                result.success(TopUsdkManager.system.getKsn());
            } else if(call.method.equals("getDriverVersion")) {
                result.success(TopUsdkManager.system.getDriverVersion());
            } else if(call.method.equals("getCurSdkVersion")) {
                result.success(TopUsdkManager.system.getCurSdkVersion());
            } else if(call.method.equals("getPinServiceVersion")) {
                result.success(TopUsdkManager.system.getPinServiceVersion());
            } else if(call.method.equals("getStoragePath")) {
                result.success(TopUsdkManager.system.getStoragePath());
            } else if(call.method.equals("getIMSI")) {
                result.success(TopUsdkManager.system.getIMSI());
            } else if(call.method.equals("getIMEI")) {
                result.success(TopUsdkManager.system.getIMEI());
            } else if(call.method.equals("getHardWireVersion")) {
                result.success(TopUsdkManager.system.getHardWireVersion());
            } else if(call.method.equals("getSecurityDriverVersion")) {
                result.success(TopUsdkManager.system.getSecurityDriverVersion());
            } else if(call.method.equals("getManufacture")) {
                result.success(TopUsdkManager.system.getManufacture());
            } else if(call.method.equals("getModel")) {
                result.success(TopUsdkManager.system.getModel());
            } else if(call.method.equals("getAndroidOsVersion")) {
                result.success(TopUsdkManager.system.getAndroidOsVersion());
            } else if(call.method.equals("getRomVersion")) {
                result.success(TopUsdkManager.system.getRomVersion());
            } else if(call.method.equals("getAndroidKernelVersion")) {
                result.success(TopUsdkManager.system.getAndroidKernelVersion());
            } else if(call.method.equals("getICCID")) {
                result.success(TopUsdkManager.system.getICCID());
            } else if(call.method.equals("getLKLOSSpecsVersion")) {
                result.success(TopUsdkManager.system.getLKLOSSpecsVersion());
            } else if(call.method.equals("getKsn")) {
                result.success(TopUsdkManager.system.getKsn());
            } else if(call.method.equals("getKsn")) {
                result.success(TopUsdkManager.system.getKsn());
            } else if(call.method.equals("getIMSIExt")) {
                int subId = call.argument("subId");
                result.success(TopUsdkManager.system.getIMSIExt(subId));
            } else if(call.method.equals("getIMEIExt")) {
                int slotId = call.argument("slotId");
                result.success(TopUsdkManager.system.getIMEIExt(slotId));
            } else if(call.method.equals("getKsn")) {
                result.success(TopUsdkManager.system.getKsn());
            } else if(call.method.equals("getKsn")) {
                result.success(TopUsdkManager.system.getKsn());
            } else if(call.method.equals("getKsn")) {
                result.success(TopUsdkManager.system.getKsn());
            } else if(call.method.equals("getKsn")) {
                result.success(TopUsdkManager.system.getKsn());
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
        return "system";
    }
}
