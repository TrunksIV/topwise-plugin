package com.topwise.plugin;

import androidx.annotation.NonNull;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
/**
 * @author caixh
 * @description
 * @date 2023/3/13 10:25
 */
public abstract class BasePlugin implements MethodCallHandler {
    public BinaryMessenger messenger;

    public BasePlugin setMessenger(BinaryMessenger messenger){
        this.messenger = messenger;
        return this;
    }

    @Override
    public abstract void onMethodCall(@NonNull MethodCall call, @NonNull Result result);


    public abstract String getPluginName();
}
