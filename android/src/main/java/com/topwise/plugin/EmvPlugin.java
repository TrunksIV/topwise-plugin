package com.topwise.plugin;

import android.os.Handler;
import android.os.Looper;
import android.os.Parcelable;
import android.os.RemoteException;
import android.util.Log;
import androidx.annotation.NonNull;
import com.topwise.cloudpos.aidl.emv.PCardLoadLog;
import com.topwise.cloudpos.aidl.emv.PCardTransLog;
import com.topwise.cloudpos.aidl.emv.level2.EmvKernelConfig;
import com.topwise.cloudpos.aidl.emv.level2.EmvTerminalInfo;
import com.topwise.cloudpos.struct.BytesUtil;
import com.topwise.plugin.emv.EmvManager;
import com.topwise.plugin.emv.EmvTransData;
import com.topwise.plugin.emv.OnEmvProcessListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import io.flutter.plugin.common.EventChannel;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;

/**
 * @author caixh
 * @description
 * @date 2023/4/27 16:16
 */
public class EmvPlugin extends BasePlugin {
    private static final String TAG = "EmvPlugin";
    private EmvManager emvManager;
    EventChannel.EventSink eventSink;


    @Override
    public void onMethodCall(@NonNull MethodCall call, @NonNull MethodChannel.Result result) {
        try {
            emvManager = EmvManager.getInstance();
            if (call.method.equals("getVersion")) {
                result.success(null);
            } else if(call.method.equals("setKernelConfig")) {
                byte[] config = call.argument("config");
                EmvKernelConfig emvKernelConfig = new EmvKernelConfig();
                emvKernelConfig.fromBytes(config);
                emvManager.setKernelConfig(emvKernelConfig);
                result.success(null);
            } else if(call.method.equals("getKernelConfig")) {
                EmvKernelConfig emvKernelConfig = emvManager.getKernelConfig();
                byte[] config = null;
                if(emvKernelConfig!=null){
                    config = emvKernelConfig.getBytes();
                }
                result.success(config);
            } else if(call.method.equals("setTerminalInfo")) {
                byte[] terminalInfo = call.argument("info");
                EmvTerminalInfo emvTerminalInfo = new EmvTerminalInfo();
                emvTerminalInfo.fromBytes(terminalInfo);
                emvManager.setEmvTerminalInfo(emvTerminalInfo);
                result.success(null);
            } else if(call.method.equals("getTerminalInfo")) {
                EmvTerminalInfo emvTerminalInfo = emvManager.getEmvTerminalInfo();
                byte[] info = null;
                if(emvTerminalInfo!=null){
                    info = emvTerminalInfo.getBytes();
                }
                result.success(info);
            } else if(call.method.equals("readKernelData")) {
                List<String> taglist = call.argument("taglist");
                if(taglist==null){
                    result.success(null);
                    return;
                }
                byte[] data = new byte[500];
                int len = emvManager.readKernelData(taglist.toArray(new String[taglist.size()]),data);
                if(len>0){
                    result.success(BytesUtil.subBytes(data,0,len));
                }else{
                    result.success(null);
                }
            } else if(call.method.equals("getTlv")) {
                int tag = call.argument("tag");
                result.success(emvManager.getTlv(tag));
            } else if(call.method.equals("setTlv")) {
                int tag = call.argument("tag");
                byte[] value = call.argument("value");
                emvManager.setTlv(tag,value);
                result.success(null);
            } else if(call.method.equals("importAmount")) {
                long amt = PluginUtils.getLong(call.argument("amt"));
                long otherAmt = PluginUtils.getLong(call.argument("otherAmt"));
                Log.d("caixh","amt="+amt);
                Log.d("caixh","otherAmt="+otherAmt);
                result.success(emvManager.importAmount(amt,otherAmt));
            } else if(call.method.equals("importAidSelectRes")) {
                int index = call.argument("index");
                result.success(emvManager.importAidSelectRes(index));
            } else if(call.method.equals("importFinalAidSelectRes")) {
                boolean res = call.argument("res");
                result.success(emvManager.importFinalAidSelectRes(res));
            } else if(call.method.equals("importConfirmCardInfoRes")) {
                boolean res = call.argument("res");
                result.success(emvManager.importConfirmCardInfoRes(res));
            } else if(call.method.equals("confirmOfflinePinEntry")) {
                boolean isContinue = call.argument("isContinue");
                result.success(emvManager.confirmOfflinePinEntry(isContinue));
            } else if(call.method.equals("importPin")) {
                String pin = call.argument("pin");
                result.success(emvManager.importPin(pin));
            } else if(call.method.equals("importUserAuthRes")) {
                boolean res = call.argument("res");
                result.success(emvManager.importUserAuthRes(res));
            } else if(call.method.equals("importMsgConfirmRes")) {
                boolean confirm = call.argument("confirm");
                result.success(emvManager.importMsgConfirmRes(confirm));
            } else if(call.method.equals("importECashTipConfirmRes")) {
                boolean confirm = call.argument("confirm");
                result.success(emvManager.importECashTipConfirmRes(confirm));
            } else if(call.method.equals("importOnlineResp")) {
                boolean onlineRes = call.argument("onlineRes");
                String respCode = call.argument("respCode");
                String icc55 = call.argument("icc55");
                result.success(emvManager.importOnlineResp(onlineRes,respCode,icc55));
            } else if(call.method.equals("endEMV")) {
                emvManager.endEMV();
                result.success(null);
            } else if(call.method.equals("abortEMV")) {
                emvManager.abortEMV();
                result.success(null);
            } else if(call.method.equals("startEmvProcess")) {
                Map<String, Object> json = call.argument("emvTransData");
                Log.d(getPluginName(), "emvTransData:"+json.toString());
                EmvTransData emvTransData = new EmvTransData();
                emvTransData.fromJson(json);
                new EventChannel(messenger, "startEmvProcess").setStreamHandler(new EventChannel.StreamHandler() {
                    @Override
                    public void onListen(Object arguments, EventChannel.EventSink events) {
                        eventSink = events;
                        Handler handler = new Handler(Looper.getMainLooper());
                        Log.d(getPluginName(), "emv.startEmvProcess");
                        emvManager.startEmvProcess(emvTransData,new OnEmvProcessListener(){

                            @Override
                            public void finalAidSelect(){
                                Map<String,Object> map = new HashMap<String,Object>();
                                map.put("name","finalAidSelect");
                                handler.post(()->{
                                    eventSink.success(map);
                                });
                            }

                            @Override
                            public void requestImportAmount(int type) {
                                Map<String,Object> map = new HashMap<String,Object>();
                                map.put("name","requestImportAmount");
                                map.put("type",type);
                                handler.post(()->{
                                    eventSink.success(map);
                                });
                            }

                            @Override
                            public void requestTipsConfirm(String msg) {
                                Map<String,Object> map = new HashMap<String,Object>();
                                map.put("name","requestTipsConfirm");
                                map.put("msg",msg);
                                handler.post(()->{
                                    eventSink.success(map);
                                });
                            }

                            @Override
                            public void requestAidSelect(int times, String[] aids) {
                                Map<String,Object> map = new HashMap<String,Object>();
                                map.put("name","requestAidSelect");
                                map.put("times",times);
                                map.put("aids",Arrays.asList(aids));
                                handler.post(()->{
                                    eventSink.success(map);
                                });
                            }

                            @Override
                            public void requestEcashTipsConfirm() {
                                Map<String,Object> map = new HashMap<String,Object>();
                                map.put("name","requestEcashTipsConfirm");
                                handler.post(()->{
                                    eventSink.success(map);
                                });
                            }

                            @Override
                            public void onConfirmCardInfo(String cardNo) {
                                Map<String,Object> map = new HashMap<String,Object>();
                                map.put("name","onConfirmCardInfo");
                                map.put("cardNo",cardNo);
                                handler.post(()->{
                                    eventSink.success(map);
                                });
                            }

                            @Override
                            public void onConfirmOfflinePinEntry(int times) throws RemoteException {
                                Map<String,Object> map = new HashMap<String,Object>();
                                map.put("name","onConfirmOfflinePinEntry");
                                map.put("times",times);
                                handler.post(()->{
                                    eventSink.success(map);
                                });
                            }

                            @Override
                            public void requestImportPin(int type, boolean lasttimeFlag, long amt) {
                                Map<String,Object> map = new HashMap<String,Object>();
                                map.put("name","requestImportPin");
                                map.put("type",type);
                                map.put("lasttimeFlag",lasttimeFlag);
                                map.put("amt",amt);
                                handler.post(()->{
                                    eventSink.success(map);
                                });
                            }

                            @Override
                            public void requestUserAuth(int certype, String certnumber) {
                                Map<String,Object> map = new HashMap<String,Object>();
                                map.put("name","requestUserAuth");
                                map.put("certype",certype);
                                map.put("certnumber",certnumber);
                                handler.post(()->{
                                    eventSink.success(map);
                                });
                            }

                            @Override
                            public void onRequestOnline() {
                                Map<String,Object> map = new HashMap<String,Object>();
                                map.put("name","onRequestOnline");
                                handler.post(()->{
                                    eventSink.success(map);
                                });
                            }

                            @Override
                            public void onReadCardOffLineBalance(String moneyCode, String balance, String secondMoneyCode, String secondBalance) {
                                Map<String,Object> map = new HashMap<String,Object>();
                                map.put("name","onReadCardOffLineBalance");
                                map.put("moneyCode",moneyCode);
                                map.put("balance",balance);
                                map.put("secondMoneyCode",secondMoneyCode);
                                map.put("secondBalance",secondBalance);
                                handler.post(()->{
                                    eventSink.success(map);
                                });
                            }

                            @Override
                            public void onReadCardTransLog(PCardTransLog[] logs) {
                                Map<String,Object> map = new HashMap<String,Object>();
                                map.put("name","onReadCardTransLog");
                                List<byte[]> loglist = new ArrayList<>();
                                map.put("logs",loglist);
                                handler.post(()->{
                                    eventSink.success(map);
                                });
                            }

                            @Override
                            public void onReadCardLoadLog(String atc, String checkCode, PCardLoadLog[] logs) {
                                Map<String,Object> map = new HashMap<String,Object>();
                                map.put("name","onReadCardLoadLog");
                                map.put("atc",atc);
                                map.put("checkCode",checkCode);
                                map.put("logs",logs);
                                handler.post(()->{
                                    eventSink.success(map);
                                });
                            }

                            @Override
                            public void onTransResult(int result) {
                                Map<String,Object> map = new HashMap<String,Object>();
                                map.put("name","onTransResult");
                                map.put("result",result);
                                handler.post(()->{
                                    eventSink.success(map);
                                });
                            }

                            @Override
                            public void onError(int erroCode) {
                                Map<String,Object> map = new HashMap<String,Object>();
                                map.put("name","onReadCardTransLog");
                                map.put("erroCode",erroCode);
                                handler.post(()->{
                                    eventSink.success(map);
                                });
                            }

                        });

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
            result.error("USDK Remote Exception", e.getMessage(), e.getStackTrace());
        }
    }

    @Override
    public String getPluginName() {
        return "emv";
    }
}
