package com.topwise.plugin;

import androidx.annotation.NonNull;
import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;

/**
 * @author caixh
 * @description
 * @date 2023/6/20 10:17
 */
public class TopwisePlugin implements FlutterPlugin, MethodChannel.MethodCallHandler {
    private MethodChannel mainChannel,systemChannel,buzzerChannel,ledChannel,extendChannel,icCardChannel,pinpadChannel,magCardChannel,
            psamChannel,cpuCardChannel,rfCardChannel,serialportChannel,printerChannel,scannerChannel,cardReaderChannel,emvChannel;
    private static BasePlugin systemPlugin,buzzerPlugin,ledPlugin,extendPlugin,icCardPlugin,pinpadPlugin,magCardPlugin,
            psamPlugin,cpuCardPlugin,rfCardPlugin,serialportPlugin,printerPlugin,scannerPlugin,cardReaderPlugin,emvPlugin;
    public BinaryMessenger messenger;

    @Override
    public void onAttachedToEngine(@NonNull FlutterPluginBinding binding) {
        messenger = binding.getBinaryMessenger();
        mainChannel = new MethodChannel(messenger, "topwiseplugin");
        mainChannel.setMethodCallHandler(this);
        systemPlugin = new SystemPlugin().setMessenger(messenger);
        systemChannel = new MethodChannel(messenger, systemPlugin.getPluginName());
        systemChannel.setMethodCallHandler(systemPlugin);
        buzzerPlugin = new BuzzerPlugin().setMessenger(messenger);
        buzzerChannel = new MethodChannel(messenger, buzzerPlugin.getPluginName());
        buzzerChannel.setMethodCallHandler(buzzerPlugin);
        ledPlugin = new LedPlugin().setMessenger(messenger);
        ledChannel = new MethodChannel(messenger, ledPlugin.getPluginName());
        ledChannel.setMethodCallHandler(ledPlugin);
        extendPlugin = new ExtendPlugin().setMessenger(messenger);
        extendChannel = new MethodChannel(messenger, extendPlugin.getPluginName());
        extendChannel.setMethodCallHandler(extendPlugin);
        icCardPlugin = new ICCardPlugin().setMessenger(messenger);
        icCardChannel = new MethodChannel(messenger, icCardPlugin.getPluginName());
        icCardChannel.setMethodCallHandler(icCardPlugin);
        pinpadPlugin = new PinpadPlugin().setMessenger(messenger);
        pinpadChannel = new MethodChannel(messenger, pinpadPlugin.getPluginName());
        pinpadChannel.setMethodCallHandler(pinpadPlugin);
        magCardPlugin = new MagCardPlugin().setMessenger(messenger);
        magCardChannel = new MethodChannel(messenger, magCardPlugin.getPluginName());
        magCardChannel.setMethodCallHandler(magCardPlugin);
        psamPlugin = new PsamPlugin().setMessenger(messenger);
        psamChannel = new MethodChannel(messenger, psamPlugin.getPluginName());
        psamChannel.setMethodCallHandler(psamPlugin);
        cpuCardPlugin = new CPUCardPlugin().setMessenger(messenger);
        cpuCardChannel = new MethodChannel(messenger, cpuCardPlugin.getPluginName());
        cpuCardChannel.setMethodCallHandler(cpuCardPlugin);
        rfCardPlugin = new RfCardPlugin().setMessenger(messenger);
        rfCardChannel = new MethodChannel(messenger, rfCardPlugin.getPluginName());
        rfCardChannel.setMethodCallHandler(rfCardPlugin);
        serialportPlugin = new SerialportPlugin().setMessenger(messenger);
        serialportChannel = new MethodChannel(messenger, serialportPlugin.getPluginName());
        serialportChannel.setMethodCallHandler(serialportPlugin);
        printerPlugin = new PrinterPlugin().setMessenger(messenger);
        printerChannel = new MethodChannel(messenger, printerPlugin.getPluginName());
        printerChannel.setMethodCallHandler(printerPlugin);
        scannerPlugin = new ScannerPlugin().setMessenger(messenger);
        scannerChannel = new MethodChannel(messenger, scannerPlugin.getPluginName());
        scannerChannel.setMethodCallHandler(scannerPlugin);
        cardReaderPlugin = new CardReaderPlugin().setMessenger(messenger);
        cardReaderChannel = new MethodChannel(messenger, cardReaderPlugin.getPluginName());
        cardReaderChannel.setMethodCallHandler(cardReaderPlugin);
        emvPlugin = new EmvPlugin().setMessenger(messenger);
        emvChannel = new MethodChannel(messenger, emvPlugin.getPluginName());
        emvChannel.setMethodCallHandler(emvPlugin);
    }

    @Override
    public void onMethodCall(@NonNull MethodCall call, @NonNull MethodChannel.Result result) {
        if (call.method.equals("getVersion")) {
            result.success("V1.0");
        } else {
            result.notImplemented();
        }
    }

    @Override
    public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
        mainChannel.setMethodCallHandler(null);
        systemChannel.setMethodCallHandler(null);
        buzzerChannel.setMethodCallHandler(null);
        ledChannel.setMethodCallHandler(null);
        extendChannel.setMethodCallHandler(null);
        icCardChannel.setMethodCallHandler(null);
        pinpadChannel.setMethodCallHandler(null);
        magCardChannel.setMethodCallHandler(null);
        psamChannel.setMethodCallHandler(null);
        cpuCardChannel.setMethodCallHandler(null);
        rfCardChannel.setMethodCallHandler(null);
        serialportChannel.setMethodCallHandler(null);
        printerChannel.setMethodCallHandler(null);
        scannerChannel.setMethodCallHandler(null);
        cardReaderChannel.setMethodCallHandler(null);
        emvChannel.setMethodCallHandler(null);
    }


}
