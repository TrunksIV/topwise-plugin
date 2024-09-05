package com.topwise.plugin;

import android.os.Handler;
import android.os.Looper;
import android.os.RemoteException;
import android.util.Log;
import androidx.annotation.NonNull;

import com.topwise.cloudpos.aidl.printer.AidlPrinterListener;
import com.topwise.cloudpos.aidl.printer.ImageUnit;
import com.topwise.cloudpos.aidl.printer.PrintTemplate;
import com.topwise.cloudpos.aidl.printer.TextUnit;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import io.flutter.plugin.common.EventChannel;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;

/**
 * @author caixh
 * @description
 * @date 2023/4/13 15:18
 */
public class PrinterPlugin extends BasePlugin{
    PrintTemplate template = PrintTemplate.getInstance();
    EventChannel.EventSink eventSink;
    private boolean printRunning =  false;

    @Override
    public void onMethodCall(@NonNull MethodCall call, @NonNull MethodChannel.Result result) {
        if(TopUsdkManager.printer==null){
            result.error("USDK Remote Exception","USDK service is not connected",null);
            return;
        }
        if (call.method.equals("init")) {
            template.clear();
            result.success(null);
        } else if(call.method.equals("clear")) {
            template.clear();
            result.success(null);
        } else if(call.method.equals("addText")) {
            Map<String,Object> map = call.argument("textUnit");
            TextUnitJson json = new TextUnitJson();
            json.fromJson(map);
            template.add(json.toTextUnit());
            result.success(null);
        } else if(call.method.equals("addText2")) {
            Map<String,Object> map1 = call.argument("textUnit1");
            Map<String,Object> map2 = call.argument("textUnit2");
            int weight1 = call.argument("weight1");
            int weight2 = call.argument("weight2");
            TextUnitJson json1 = new TextUnitJson();
            json1.fromJson(map1);
            TextUnitJson json2 = new TextUnitJson();
            json2.fromJson(map2);
            template.add(weight1,json1.toTextUnit(),weight2,json2.toTextUnit());
            result.success(null);
        } else if(call.method.equals("addText3")) {
            Map<String,Object> map1 = call.argument("textUnit1");
            Map<String,Object> map2 = call.argument("textUnit2");
            Map<String,Object> map3 = call.argument("textUnit3");
            int weight1 = call.argument("weight1");
            int weight2 = call.argument("weight2");
            int weight3 = call.argument("weight3");
            TextUnitJson json1 = new TextUnitJson();
            json1.fromJson(map1);
            TextUnitJson json2 = new TextUnitJson();
            json2.fromJson(map2);
            TextUnitJson json3 = new TextUnitJson();
            json3.fromJson(map3);
            template.add(weight1,json1.toTextUnit(),weight2,json2.toTextUnit(),weight3,json3.toTextUnit());
            result.success(null);
        } else if(call.method.equals("addImage")) {
            Map<String,Object> map = call.argument("imageUnit");
            ImageUnitJson json = new ImageUnitJson();
            json.fromJson(map);
            ImageUnit imageUnit = json.toImageUnit();
            if(imageUnit!=null){
                template.add(imageUnit);
            }
            result.success(null);
        } else if(call.method.equals("addTextImage")) {
            List<Map<String,Object>> list = call.argument("textUnits");
            List<TextUnit> textUnitList = new ArrayList<>();
            for(int i=0;i < list.size();i++){
                Map<String,Object> map = list.get(i);
                TextUnitJson json = new TextUnitJson();
                json.fromJson(map);
                textUnitList.add(json.toTextUnit());
            }
            Map<String,Object> map = call.argument("imageUnit");
            ImageUnitJson json = new ImageUnitJson();
            json.fromJson(map);
            ImageUnit imageUnit = json.toImageUnit();
            if(imageUnit!=null){
                template.add(textUnitList,imageUnit);
            } else{
                for(int i=0;i < textUnitList.size();i++){
                    template.add(textUnitList.get(i));
                }
            }
            result.success(null);
        } else if(call.method.equals("addImageText")) {
            List<Map<String,Object>> list = call.argument("textUnits");
            List<TextUnit> textUnitList = new ArrayList<>();
            for(int i=0;i < list.size();i++){
                Map<String,Object> map = list.get(i);
                TextUnitJson json = new TextUnitJson();
                json.fromJson(map);
                textUnitList.add(json.toTextUnit());
            }
            Map<String,Object> map = call.argument("imageUnit");
            ImageUnitJson json = new ImageUnitJson();
            json.fromJson(map);
            ImageUnit imageUnit = json.toImageUnit();
            if(imageUnit!=null){
                template.add(imageUnit,textUnitList);
            } else{
                for(int i=0;i < textUnitList.size();i++){
                    template.add(textUnitList.get(i));
                }
            }
            result.success(null);
        } else if(call.method.equals("startPrint")) {
            new EventChannel(messenger, "startPrint").setStreamHandler(new EventChannel.StreamHandler() {
                @Override
                public void onListen(Object arguments, EventChannel.EventSink events) {
                    eventSink = events;
                    Handler handler = new Handler(Looper.getMainLooper());
                    Log.d(getPluginName(),"TopUsdkManager.printer.startPrint");
                    try {
                        TopUsdkManager.printer.addRuiImage(template.getPrintBitmap(),0);
                        printRunning = true;
                        TopUsdkManager.printer.printRuiQueue(new AidlPrinterListener.Stub() {
                            @Override
                            public void onError(int errorCode) throws RemoteException {
                                printRunning = false;
                                handler.post(()->{
                                    eventSink.success(errorCode);
                                });

                            }

                            @Override
                            public void onPrintFinish() throws RemoteException {
                                printRunning = false;
                                handler.post(()->{
                                    eventSink.success(null);
                                });
                            }
                        });
                    } catch (RemoteException e) {
                        // TODO Auto-generated catch block
                        printRunning = false;
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
    }

    @Override
    public String getPluginName() {
        return "printer";
    }
}
