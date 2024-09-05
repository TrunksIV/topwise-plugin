package com.topwise.plugin;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.RemoteException;
import android.util.Log;
import androidx.annotation.NonNull;
import com.topwise.cloudpos.aidl.pinpad.GetPinListener;
import com.topwise.cloudpos.struct.BytesUtil;
import java.util.HashMap;
import java.util.Map;
import io.flutter.plugin.common.EventChannel;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;

/**
 * @author caixh
 * @description
 * @date 2023/3/16 10:29
 */
public class PinpadPlugin extends BasePlugin {
    EventChannel.EventSink eventSink;

    @Override
    public void onMethodCall(@NonNull MethodCall call, @NonNull MethodChannel.Result result) {
        try {
            if(TopUsdkManager.pinpad==null){
                result.error("USDK Remote Exception","USDK service is not connected",null);
                return;
            }
            if (call.method.equals("getPin")) {
                final Bundle bundle = new Bundle();
                if(call.hasArgument("keyId")){
                    bundle.putInt("wkeyid", (int)call.argument("keyId"));
                }
                if(call.hasArgument("isOnlinePin")){
                    bundle.putInt("keytype", call.argument("isOnlinePin")?0x00:0x01);
                }
                if(call.hasArgument("keyType")){
                    bundle.putInt("key_type", (int)call.argument("keyType"));
                }
                if(call.hasArgument("pan")){
                    bundle.putString("pan", (String)call.argument("pan"));
                }
                if(call.hasArgument("random")){
                    bundle.putByteArray("random", (byte[])call.argument("random"));
                }
                if(call.hasArgument("algType")){
                    bundle.putInt("algorithm", (int)call.argument("algType"));
                }
                if(call.hasArgument("isUseSm4")){
                    bundle.putBoolean("is_use_sm4", (boolean)call.argument("isUseSm4"));
                }
                if(call.hasArgument("timeout")){
                    bundle.putInt("timeout", (int)call.argument("timeout"));
                }
                if(call.hasArgument("refreshPin")){
                    bundle.putBoolean("pin_refresh", (boolean)call.argument("refreshPin"));
                }
                if(call.hasArgument("keyLayout")){
                    bundle.putByteArray("pin_key_layout", (byte[])call.argument("keyLayout"));
                }
                if(call.hasArgument("buttonNum")){
                    bundle.putByteArray("pin_button_num", (byte[])call.argument("buttonNum"));
                }
                if(call.hasArgument("pinLenSet")){
                    bundle.putString("input_pin_mode", (String)call.argument("pinLenSet"));
                }
                if(call.hasArgument("minlength")){
                    bundle.putInt("minlength", (int)call.argument("minlength"));
                }
                if(call.hasArgument("maxlength")){
                    bundle.putInt("maxlength", (int)call.argument("maxlength"));
                }
                if(call.hasArgument("tips")){
                    bundle.putString("tips", (String)call.argument("tips"));
                }
                bundle.putBoolean("is_lkl", false);

                new EventChannel(messenger, "getPin").setStreamHandler(new EventChannel.StreamHandler() {
                    @Override
                    public void onListen(Object arguments, EventChannel.EventSink events) {
                        eventSink = events;
                        Handler handler = new Handler(Looper.getMainLooper());
                        Log.d(getPluginName(),"TopUsdkManager.pinpad.getPin");
                        try {
                            TopUsdkManager.pinpad.getPin(bundle, new GetPinListener.Stub() {
                                @Override
                                public void onInputKey(int len, String msg) throws RemoteException {
                                    Map<String,Object> map = new HashMap<String,Object>();
                                    map.put("name","onInputKey");
                                    map.put("len",len);
                                    map.put("msg",msg);
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
                                public void onConfirmInput(byte[] pin) throws RemoteException {
                                    Map<String,Object> map = new HashMap<String,Object>();
                                    map.put("name","onConfirmInput");
                                    map.put("pin",pin);
                                    handler.post(()->{
                                        eventSink.success(map);
                                    });
                                }

                                @Override
                                public void onCancelKeyPress() throws RemoteException {
                                    Map<String,Object> map = new HashMap<String,Object>();
                                    map.put("name","onCancelKeyPress");
                                    handler.post(()->{
                                        eventSink.success(map);
                                    });
                                }

                                @Override
                                public void onStopGetPin() throws RemoteException {
                                    Map<String,Object> map = new HashMap<String,Object>();
                                    map.put("name","onStopGetPin");
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
            } else if(call.method.equals("stopGetPin")){
                TopUsdkManager.pinpad.stopGetPin();
                result.success(null);
            } else if(call.method.equals("confirmGetPin")){
                TopUsdkManager.pinpad.confirmGetPin();
                result.success(null);
            } else if(call.method.equals("loadMainkey")){
                int keyId = call.argument("keyId");
                byte[] key = call.argument("key");
                byte[] kcv = call.argument("kcv");
                result.success(TopUsdkManager.pinpad.loadMainkey(keyId, key,kcv));
            } else if(call.method.equals("loadWorkKey")){
                int keyType = call.argument("keyType");
                int keyId = call.argument("keyId");
                int masterKeyId = call.argument("masterKeyId");
                byte[] key = call.argument("key");
                byte[] kcv = call.argument("kcv");
                result.success(TopUsdkManager.pinpad.loadWorkKey(keyType,masterKeyId,keyId,key,kcv));
            } else if(call.method.equals("getMac")){
                Bundle bundle = new Bundle();
                if(call.hasArgument("keyId")) {
                    bundle.putInt("wkeyid", (int)call.argument("keyId"));
                }
                if(call.hasArgument("keyType")) {
                    bundle.putInt("key_type", (int)call.argument("keyType"));
                }
                if(call.hasArgument("macAlg")) {
                    bundle.putInt("type", (int)call.argument("macAlg"));
                }
                if(call.hasArgument("isUseSm4")) {
                    bundle.putBoolean("is_use_sm4", (boolean)call.argument("isUseSm4"));
                }
                if(call.hasArgument("random")) {
                    bundle.putByteArray("random", (byte[])call.argument("random"));
                }
                if(call.hasArgument("data")) {
                    bundle.putByteArray("data", (byte[])call.argument("data"));
                }
                byte[] encryptdata = new byte[8];

                int ret = TopUsdkManager.pinpad.getMac(bundle,encryptdata);
                if(ret==0){
                    result.success(encryptdata);
                } else{
                    result.success(null);
                }
            } else if(call.method.equals("encryptByTdk")){
                int keyId = call.argument("keyId");
                int mode = call.argument("mode");
                byte[] random = call.argument("random");
                byte[] data = call.argument("data");
                byte[] encryptdata = new byte[data.length];
                int ret = TopUsdkManager.pinpad.encryptByTdk(keyId,(byte)mode,random,data,encryptdata);
                if(ret==0){
                    result.success(encryptdata);
                } else{
                    result.success(null);
                }
            } else if(call.method.equals("getRandom")) {
                result.success(TopUsdkManager.pinpad.getRandom());
            } else if(call.method.equals("display")) {
                String line1 = call.argument("line1");
                String line2 = call.argument("line2");
                result.success(TopUsdkManager.pinpad.display(line1,line2));
            } else if(call.method.equals("loadTEK")) {
                int keyId = call.argument("keyId");
                byte[] key = call.argument("key");
                byte[] kcv = call.argument("kcv");
                result.success(TopUsdkManager.pinpad.loadTEK(keyId,key,kcv));
            } else if(call.method.equals("loadTWK")) {
                int keyId = call.argument("keyId");
                int tmkID = call.argument("tmkID");
                int keyType = call.argument("keyType");
                byte[] key = call.argument("key");
                byte[] kcv = call.argument("kcv");
                result.success(TopUsdkManager.pinpad.loadTWK(keyType,tmkID,keyId,key,kcv));
            } else if(call.method.equals("loadEncryptMainkey")) {
                int keyId = call.argument("keyId");
                int tmkID = call.argument("tmkID");
                byte[] key = call.argument("key");
                byte[] kcv = call.argument("kcv");
                result.success(TopUsdkManager.pinpad.loadEncryptMainkey(tmkID,keyId,key,kcv));
            } else if(call.method.equals("setPinKeyboardMode")) {
                int mode = call.argument("mode");
                result.success(TopUsdkManager.pinpad.setPinKeyboardMode(mode));
            } else if(call.method.equals("getKeyState")) {
                int keyId = call.argument("keyId");
                int keyType = call.argument("keyType");
                result.success(TopUsdkManager.pinpad.getKeyState(keyType,keyId));
            } else if(call.method.equals("encryptByRandomWk")) {
                /*int keyId = call.argument("keyId");
                boolean flag = call.argument("flag");
                String random = call.argument("random");
                String data = call.argument("data");
                result.success(null);*/
                result.notImplemented();
            } else if(call.method.equals("loadDuKPTkey")) {
                int keyId = call.argument("keyId");
                int keyType = call.argument("keyType");
                byte[] key = call.argument("key");
                byte[] ksn = call.argument("ksn");
                result.success(TopUsdkManager.pinpad.loadDuKPTkey(keyType,keyId,key,ksn));
            } else if(call.method.equals("getDUKPTKsn")) {
                int keyId = call.argument("keyId");
                boolean isIncrease = call.argument("isIncrease");
                result.success(TopUsdkManager.pinpad.getDUKPTKsn(keyId,isIncrease));
            } else if(call.method.equals("algorithmCal")) {
                int keyId = call.argument("keyId");
                int keyType = call.argument("keyType");
                byte[] data = call.argument("data");
                byte[] cbcData = call.argument("cbcData");
                result.success(TopUsdkManager.pinpad.algorithmCal(keyType,keyId,data,cbcData));
            } else if(call.method.equals("algorithDecrypt")) {
                int keyId = call.argument("keyId");
                byte[] data = call.argument("data");
                result.success(TopUsdkManager.pinpad.algorithDecrypt(keyId,data));
            } else if(call.method.equals("loadKeyCommonMethod")) {
                int encryptKeyType = call.argument("encryptKeyType");
                int encryptKeyId = call.argument("encryptKeyId");
                int decryptKeyType = call.argument("decryptKeyType");
                int decryptKeyId = call.argument("decryptKeyId");
                int decryptAlg = call.argument("decryptAlg");
                byte[] vectorData = call.argument("vectorData");
                byte[] encryptData = call.argument("encryptData");
                byte[] checkValue = call.argument("checkValue");
                result.success(TopUsdkManager.pinpad.loadKeyCommonMethod(encryptKeyType,encryptKeyId,decryptKeyType,decryptKeyId,decryptAlg,
                   vectorData,encryptData,checkValue));
                result.success(null);
            } else if(call.method.equals("getButtonNum")) {
                result.success(TopUsdkManager.pinpad.getButtonNum());
            } else if(call.method.equals("deletePedKey")) {
                int keyId = call.argument("keyId");
                int keyType = call.argument("keyType");
                int mode = call.argument("mode");
                result.success(TopUsdkManager.pinpad.deletePedKey(keyType,keyId,mode));
            } else if(call.method.equals("loadDukptIPEK")) {
                int keyId = call.argument("keyId");
                byte[] key = call.argument("key");
                byte[] ksn = call.argument("ksn");
                result.success(TopUsdkManager.pinpad.loadDukptIPEK(keyId,key,ksn));
            } else if(call.method.equals("loadDukptBDK")) {
                int keyId = call.argument("keyId");
                byte[] key = call.argument("key");
                byte[] ksn = call.argument("ksn");
                result.success(TopUsdkManager.pinpad.loadDukptBDK(keyId,key,ksn));
            } else if(call.method.equals("cryptByTdk")) {
                int keyId = call.argument("keyId");
                int mode = call.argument("mode");
                byte[] data = call.argument("data");
                byte[] iv = call.argument("iv");
                byte[] cryptdata = new byte[data.length];
                int ret = TopUsdkManager.pinpad.cryptByTdk(keyId,(byte)mode,data,iv,cryptdata);
                Log.d(getPluginName(),"ret="+ret+",cryptdata="+BytesUtil.bytes2HexString(cryptdata));
                if(ret==0){
                    result.success(cryptdata);
                } else{
                    result.success(null);
                }
            } else if(call.method.equals("cryptByDukptDataKey")) {
                int keyId = call.argument("keyId");
                int mode = call.argument("mode");
                byte[] data = call.argument("data");
                byte[] iv = call.argument("iv");
                byte[] cryptdata = new byte[data.length];
                int ret = TopUsdkManager.pinpad.cryptByDukptDataKey(keyId,(byte)mode,data,iv,cryptdata);
                if(ret==0){
                    result.success(cryptdata);
                } else{
                    result.success(null);
                }
            } else if(call.method.equals("getKeyCheckValue")) {
                int keyId = call.argument("keyId");
                int keyType = call.argument("keyType");
                result.success(TopUsdkManager.pinpad.getKeyCheckValue(keyType,keyId));
            } else if(call.method.equals("loadTr31EncryptMainkey")) {
                int tekID = call.argument("tekID");
                int tmkID = call.argument("tmkID");
                byte[] key = call.argument("key");
                byte[] kcv = call.argument("kcv");
                result.success(TopUsdkManager.pinpad.loadTr31EncryptMainkey(tekID,tmkID,key,kcv));
            } else if(call.method.equals("dukptKeyBdkToIpek")) {
                int keyId = call.argument("keyId");
                result.success(TopUsdkManager.pinpad.dukptKeyBdkToIpek(keyId));
            } else if(call.method.equals("getKeyKin")) {
                int recordIndex = call.argument("recordIndex");
                int keyId = call.argument("keyId");
                int keyType = call.argument("keyType");
                result.success(TopUsdkManager.pinpad.getKeyKin(recordIndex,keyType,keyId));
            } else if(call.method.equals("cryptByFixedTdk")) {
                int keyId = call.argument("keyId");
                int mode = call.argument("mode");
                byte[] data = call.argument("data");
                byte[] iv = call.argument("iv");
                byte[] encryptdata = new byte[data.length];
                int ret = TopUsdkManager.pinpad.cryptByFixedTdk(keyId,(byte)mode,data,iv,encryptdata);
                if(ret==0){
                    result.success(encryptdata);
                } else{
                    result.success(null);
                }
            } else if(call.method.equals("genRandomTekEncryptByTek")) {
                int masterIndex = call.argument("masterIndex");
                int subIndex = call.argument("subIndex");
                int keyLen = call.argument("keyLen");
                result.success(TopUsdkManager.pinpad.genRandomTekEncryptByTek(masterIndex,subIndex,keyLen));
            } else if(call.method.equals("loadKey")) {
                int keyId = call.argument("keyId");
                int keyType = call.argument("keyType");
                byte[] key = call.argument("key");
                byte[] kcv = call.argument("kcv");
                result.success(TopUsdkManager.pinpad.loadKey(keyId,keyType,key,kcv));
            } else if(call.method.equals("setPinPadLayout")) {
                byte[] layout = call.argument("layout");
                TopUsdkManager.pinpad.setPinPadLayout(layout);
                result.success(null);
            } else if(call.method.equals("loadMainkeyEx")) {
                result.notImplemented();
            } else if(call.method.equals("loadWorkKeyEx")) {
                result.notImplemented();
            } else if(call.method.equals("encryptByTdkEx")) {
                result.notImplemented();
            } else if(call.method.equals("loadTEKEx")) {
                result.notImplemented();
            } else if(call.method.equals("loadEncryptMainkeyEx")) {
                result.notImplemented();
            } else if(call.method.equals("loadTWKEx")) {
                result.notImplemented();
            } else if(call.method.equals("checkkey")) {
                int keyId = call.argument("keyId");
                int keyType = call.argument("keyType");
                boolean isSM4 = call.argument("isSM4");
                result.success(TopUsdkManager.pinpad.checkkey(keyId,keyType,isSM4));
            } else if(call.method.equals("getDUKPTKsnExt")) {
                result.notImplemented();
            } else if(call.method.equals("storedRecord")) {
                int actionType = call.argument("actionType");
                int recordNumber = call.argument("recordNumber");
                byte[] data = call.argument("data");
                result.success(TopUsdkManager.pinpad.storedRecord(actionType,recordNumber,data));
            } else{
                result.notImplemented();
            }
        } catch (RemoteException e) {
            e.printStackTrace();
            result.error("USDK Remote Exception",e.getMessage(),e.getStackTrace());
        }
    }

    @Override
    public String getPluginName() {
        return "pinpad";
    }
}
