package com.topwise.plugin;

import android.os.Handler;
import android.os.Looper;
import android.os.RemoteException;
import android.util.Log;
import androidx.annotation.NonNull;
import com.topwise.cloudpos.aidl.emv.AidlCheckCardListener;
import com.topwise.cloudpos.aidl.magcard.AidlMagCard;
import com.topwise.cloudpos.aidl.magcard.TrackData;
import com.topwise.cloudpos.aidl.rfcard.AidlRFCard;
import com.topwise.cloudpos.aidl.shellmonitor.AidlShellMonitor;
import java.util.HashMap;
import java.util.Map;
import io.flutter.plugin.common.EventChannel;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;

/**
 * @author caixh
 * @description
 * @date 2023/4/11 16:41
 */
public class CardReaderPlugin extends BasePlugin {
    private static final String TAG = "CardReaderPlugin";
    private boolean isRunging = false;
    private CardTimer cardTimer;    private CardData cardData;
    //private SearchCardThread searchCardThread;
    private boolean isMag;
    private boolean isIcc;
    private boolean isRf;
    private byte mResultCode;
    private byte[] mResultData;
    private boolean bCloseAll;

    private AidlMagCard magCard;
    private AidlRFCard rfCard;
    private AidlShellMonitor aidlShellMonitor;

    EventChannel.EventSink eventSink;


    @Override
    public void onMethodCall(@NonNull MethodCall call, @NonNull MethodChannel.Result result) {
        try {
            if (TopUsdkManager.emvL2 == null) {
                result.error("USDK Remote Exception", "USDK service is not connected", null);
                return;
            }
            if (call.method.equals("startSearchCard")) {
                boolean isMag = call.argument("isMag");
                boolean isIcc = call.argument("isIcc");
                boolean isRf = call.argument("isRf");
                int timeout = call.argument("timeout");
                new EventChannel(messenger, "startSearchCard").setStreamHandler(new EventChannel.StreamHandler() {
                    @Override
                    public void onListen(Object arguments, EventChannel.EventSink events) {
                        eventSink = events;
                        Handler handler = new Handler(Looper.getMainLooper());
                        Log.d(getPluginName(), "cardreader.startSearchCard");
                        try {
                            TopUsdkManager.emvL2.checkCard(isMag,isIcc,isRf,timeout,new AidlCheckCardListener.Stub(){

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
                                public void onFindMagCard(TrackData trackData) throws RemoteException {
                                    Map<String,Object> map = new HashMap<String,Object>();
                                    map.put("name","onFindMagCard");
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
                                public void onSwipeCardFail() throws RemoteException {
                                    Map<String,Object> map = new HashMap<String,Object>();
                                    map.put("name","onSwipeCardFail");
                                    handler.post(()->{
                                        eventSink.success(map);
                                    });
                                }

                                @Override
                                public void onFindICCard() throws RemoteException {
                                    Map<String,Object> map = new HashMap<String,Object>();
                                    map.put("name","onFindICCard");
                                    handler.post(()->{
                                        eventSink.success(map);
                                    });
                                }

                                @Override
                                public void onFindRFCard() throws RemoteException {
                                    Map<String,Object> map = new HashMap<String,Object>();
                                    map.put("name","onFindRFCard");
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

                                @Override
                                public void onCanceled() throws RemoteException {
                                    Map<String,Object> map = new HashMap<String,Object>();
                                    map.put("name","onCanceled");
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
            } else if (call.method.equals("cancelCheckCard")) {
                TopUsdkManager.emvL2.cancelCheckCard();
                result.success(null);
            } else {
                result.notImplemented();
            }
        } catch (RemoteException e) {
            e.printStackTrace();
            result.error("USDK Remote Exception", e.getMessage(), e.getStackTrace());
        }
    }

    @Override
    public String getPluginName() {
        return "cardreader";
    }
}