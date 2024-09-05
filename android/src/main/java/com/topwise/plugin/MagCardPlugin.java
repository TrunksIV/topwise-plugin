package com.topwise.plugin;

import android.os.Handler;
import android.os.Looper;
import android.os.RemoteException;
import android.util.Log;
import androidx.annotation.NonNull;

import com.topwise.cloudpos.aidl.magcard.EncryptMagCardListener;
import com.topwise.cloudpos.aidl.magcard.MagCardListener;
import com.topwise.cloudpos.aidl.magcard.TrackData;

import java.util.HashMap;
import java.util.Map;
import io.flutter.plugin.common.EventChannel;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;

/**
 * @author caixh
 * @description
 * @date 2023/3/22 11:23
 */
public class MagCardPlugin extends BasePlugin{
    EventChannel.EventSink eventSink;

    @Override
    public void onMethodCall(@NonNull MethodCall call, @NonNull MethodChannel.Result result) {
        try {
            if(TopUsdkManager.magCard==null){
                result.error("USDK Remote Exception","USDK service is not connected",null);
                return;
            }
            if (call.method.equals("open")) {
                result.success(TopUsdkManager.magCard.open());
            } else if(call.method.equals("close")) {
                result.success(TopUsdkManager.magCard.close());
            } else if(call.method.equals("stopSearch")) {
                TopUsdkManager.magCard.stopSearch();
                result.success(null);
            } else if(call.method.equals("searchCard")) {
                int timeout = call.argument("timeout");
                new EventChannel(messenger, "searchCard").setStreamHandler(new EventChannel.StreamHandler() {
                    @Override
                    public void onListen(Object arguments, EventChannel.EventSink events) {
                        eventSink = events;
                        Handler handler = new Handler(Looper.getMainLooper());
                        Log.d(getPluginName(),"TopUsdkManager.magCard.searchCard");
                        try {
                            TopUsdkManager.magCard.searchCard(timeout, new MagCardListener.Stub() {
                                @Override
                                public void onSuccess(TrackData trackData) throws RemoteException {
                                    Map<String,Object> map = new HashMap<String,Object>();
                                    map.put("name","onSuccess");
                                    MagData data = new MagData(trackData.getCardno(),
                                            trackData.getFirstTrackData(),trackData.getSecondTrackData(),trackData.getThirdTrackData(),
                                            trackData.getFormatTrackData(),trackData.getExpiryDate(),trackData.getServiceCode()
                                    );
                                    map.put("magData",data.toJson());
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
                                public void onCanceled() throws RemoteException {
                                    Map<String,Object> map = new HashMap<String,Object>();
                                    map.put("name","onCanceled");
                                    handler.post(()->{
                                        eventSink.success(map);
                                    });
                                }

                                @Override
                                public void onGetTrackFail() throws RemoteException {
                                    Map<String,Object> map = new HashMap<String,Object>();
                                    map.put("name","onGetTrackFail");
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
                            // TODO Auto-generated catch block
                            handler.post(()->{
                                eventSink.error("USDK Remote Exception",e.getMessage(),e.getStackTrace());
                            });
                        }
                    }

                    @Override
                    public void onCancel(Object arguments) {
                        eventSink.error("","onCancel",arguments);
                    }
                });
                result.success(null);
            } else if(call.method.equals("searchEncryptCard")) {
                int timeout = call.argument("timeout");
                byte keyIndex = PluginUtils.getByte(call.argument("keyIndex"));
                byte encryptFlag = call.argument("encryptFlag");
                byte[] random = call.argument("random");
                byte pinpadType = PluginUtils.getByte(call.argument("pinpadType"));
                new EventChannel(messenger, "searchEncryptCard").setStreamHandler(new EventChannel.StreamHandler() {
                    @Override
                    public void onListen(Object arguments, EventChannel.EventSink events) {
                        eventSink = events;
                        Handler handler = new Handler(Looper.getMainLooper());
                        Log.d(getPluginName(),"magCard.searchEncryptCard");
                        try {
                            TopUsdkManager.magCard.searchEncryptCard(timeout, keyIndex, encryptFlag, random, pinpadType, new EncryptMagCardListener.Stub() {
                                @Override
                                public void onSuccess(String[] track) throws RemoteException {
                                    Map<String,Object> map = new HashMap<String,Object>();
                                    map.put("name","onSuccess");
                                    MagData data = new MagData();
                                    if(track.length==2){
                                        data.setFormatTrack(track[0]);
                                        data.setCardno(track[1]);
                                    } else if(track.length==2){
                                        data.setTrack2(track[0]);
                                        data.setTrack3(track[1]);
                                        data.setCardno(track[2]);
                                        data.setExpiryDate(track[3]);
                                    }
                                    map.put("magData",data.toJson());
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
                                public void onCanceled() throws RemoteException {
                                    Map<String,Object> map = new HashMap<String,Object>();
                                    map.put("name","onCanceled");
                                    handler.post(()->{
                                        eventSink.success(map);
                                    });
                                }

                                @Override
                                public void onGetTrackFail() throws RemoteException {
                                    Map<String,Object> map = new HashMap<String,Object>();
                                    map.put("name","onGetTrackFail");
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
                            // TODO Auto-generated catch block
                            handler.post(()->{
                                eventSink.error("USDK Remote Exception",e.getMessage(),e.getStackTrace());
                            });
                        }
                    }

                    @Override
                    public void onCancel(Object arguments) {
                        eventSink.error("","onCancel",arguments);
                    }
                });
                result.success(null);
            } else if(call.method.equals("searchEncryptCardEx")) {
                int timeout = call.argument("timeout");
                byte keyIndex = PluginUtils.getByte(call.argument("keyIndex"));
                byte encryptFlag = call.argument("encryptFlag");
                byte[] random = call.argument("random");
                byte pinpadType = PluginUtils.getByte(call.argument("pinpadType"));
                new EventChannel(messenger, "searchEncryptCardEx").setStreamHandler(new EventChannel.StreamHandler() {
                    @Override
                    public void onListen(Object arguments, EventChannel.EventSink events) {
                        eventSink = events;
                        Handler handler = new Handler(Looper.getMainLooper());
                        Log.d(getPluginName(),"magCard.searchEncryptCardEx");
                        try {
                            TopUsdkManager.magCard.searchEncryptCardEx(timeout, keyIndex, encryptFlag, random, pinpadType, new EncryptMagCardListener.Stub() {
                                @Override
                                public void onSuccess(String[] track) throws RemoteException {
                                    Map<String,Object> map = new HashMap<String,Object>();
                                    map.put("name","onSuccess");
                                    MagData data = new MagData();
                                    if(track.length==2){
                                        data.setFormatTrack(track[0]);
                                        data.setCardno(track[1]);
                                    } else if(track.length==2){
                                        data.setTrack2(track[0]);
                                        data.setTrack3(track[1]);
                                        data.setCardno(track[2]);
                                        data.setExpiryDate(track[3]);
                                    }
                                    map.put("magData",data.toJson());
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
                                public void onCanceled() throws RemoteException {
                                    Map<String,Object> map = new HashMap<String,Object>();
                                    map.put("name","onCanceled");
                                    handler.post(()->{
                                        eventSink.success(map);
                                    });
                                }

                                @Override
                                public void onGetTrackFail() throws RemoteException {
                                    Map<String,Object> map = new HashMap<String,Object>();
                                    map.put("name","onGetTrackFail");
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
                            },true);
                        } catch (RemoteException e) {
                            // TODO Auto-generated catch block
                            handler.post(()->{
                                eventSink.error("USDK Remote Exception",e.getMessage(),e.getStackTrace());
                            });
                        }
                    }

                    @Override
                    public void onCancel(Object arguments) {
                        eventSink.error("","onCancel",arguments);
                    }
                });
                result.success(null);
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
        return "magcard";
    }
}
