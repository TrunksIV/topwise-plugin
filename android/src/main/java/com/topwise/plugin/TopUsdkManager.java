package com.topwise.plugin;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.topwise.cloudpos.aidl.AidlDeviceService;
import com.topwise.cloudpos.aidl.buzzer.AidlBuzzer;
import com.topwise.cloudpos.aidl.camera.AidlCameraScanCode;
import com.topwise.cloudpos.aidl.card.AidlCheckCard;
import com.topwise.cloudpos.aidl.cpucard.AidlCPUCard;
import com.topwise.cloudpos.aidl.decoder.AidlDecoderManager;
import com.topwise.cloudpos.aidl.emv.level2.AidlAmex;
import com.topwise.cloudpos.aidl.emv.level2.AidlDpas;
import com.topwise.cloudpos.aidl.emv.level2.AidlEmvL2;
import com.topwise.cloudpos.aidl.emv.level2.AidlEntry;
import com.topwise.cloudpos.aidl.emv.level2.AidlJcb;
import com.topwise.cloudpos.aidl.emv.level2.AidlMir;
import com.topwise.cloudpos.aidl.emv.level2.AidlPaypass;
import com.topwise.cloudpos.aidl.emv.level2.AidlPaywave;
import com.topwise.cloudpos.aidl.emv.level2.AidlPure;
import com.topwise.cloudpos.aidl.emv.level2.AidlQpboc;
import com.topwise.cloudpos.aidl.emv.level2.AidlRupay;
import com.topwise.cloudpos.aidl.fingerprint.AidlFingerprint;
import com.topwise.cloudpos.aidl.iccard.AidlICCard;
import com.topwise.cloudpos.aidl.led.AidlLed;
import com.topwise.cloudpos.aidl.magcard.AidlMagCard;
import com.topwise.cloudpos.aidl.pedestal.AidlPedestal;
import com.topwise.cloudpos.aidl.pinpad.AidlPinpad;
import com.topwise.cloudpos.aidl.pm.AidlPM;
import com.topwise.cloudpos.aidl.printer.AidlPrinter;
import com.topwise.cloudpos.aidl.printer.PrintTemplate;
import com.topwise.cloudpos.aidl.psam.AidlPsam;
import com.topwise.cloudpos.aidl.rfcard.AidlRFCard;
import com.topwise.cloudpos.aidl.serialport.AidlSerialport;
import com.topwise.cloudpos.aidl.shellmonitor.AidlShellMonitor;
import com.topwise.cloudpos.aidl.system.AidlSystem;

/**
 * @author caixh
 */
public class TopUsdkManager {
    private static final String TAG = "TopUsdkManager";

    private static final String DEVICE_SERVICE_PACKAGE_NAME = "com.android.topwise.topusdkservice";
    private static final String DEVICE_SERVICE_CLASS_NAME = "com.android.topwise.topusdkservice.service.DeviceService";
    private static final String ACTION_DEVICE_SERVICE = "topwise_cloudpos_device_service";

    private static TopUsdkManager instance;
    private Context mContext = null;
    private boolean stopBind = false;
    private AidlDeviceService mDeviceService;
    public static AidlSystem system;
    public static AidlLed led;
    public static AidlPinpad pinpad;
    public static AidlBuzzer buzzer;
    public static AidlPrinter printer;
    public static AidlShellMonitor shell;
    public static AidlCPUCard cpuCard;
    public static AidlICCard icCard;
    public static AidlRFCard rfCard;
    public static AidlMagCard magCard;
    public static AidlPedestal pedestal;
    public static AidlDecoderManager decoder;
    public static AidlCameraScanCode camera;
    public static AidlEmvL2 emvL2;
    public static AidlPure pure;
    public static AidlPaypass paypass;
    public static AidlPaywave paywave;
    public static AidlEntry entry;
    public static AidlAmex amex;
    public static AidlQpboc qpboc;
    public static AidlRupay rupay;
    public static AidlMir mir;
    public static AidlJcb jcb;
    public static AidlDpas dpas;
    public static AidlCheckCard checkCard;
    public static AidlPM pm;
    public AidlFingerprint fingerprint;

    public static TopUsdkManager getInstance() {
        Log.d(TAG,"getInstance()");
        if (null == instance) {
            synchronized (TopUsdkManager.class) {
                instance = new TopUsdkManager();
            }
        }
        return instance;
    }

    public void bindDeviceService(Context context) {
        Log.i(TAG,"bindDeviceService");
        PrintTemplate.getInstance().init(context);
        this.mContext = context;
        Intent intent = new Intent();
        intent.setAction(ACTION_DEVICE_SERVICE);
        intent.setClassName(DEVICE_SERVICE_PACKAGE_NAME, DEVICE_SERVICE_CLASS_NAME);

        try {
            boolean bindResult = mContext.bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
            Log.i(TAG,"bindResult = " + bindResult);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Context getContext() {
        return mContext;
    }

    public void unBindDeviceService() {
        Log.i(TAG,"unBindDeviceService");
        try {
            stopBind = true;
            mContext.unbindService(mConnection);
        } catch (Exception e) {
            Log.i(TAG,"unbind DeviceService service failed : " + e);
        }
    }

    private final ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mDeviceService = AidlDeviceService.Stub.asInterface(service);
            Log.d(TAG,"mDeviceService:" + mDeviceService);
            try {
                if(mDeviceService!=null) {
                    if(mDeviceService.getSystemService()!=null){
                        system = AidlSystem.Stub.asInterface(mDeviceService.getSystemService());
                    }
                    if(mDeviceService.getLed()!=null){
                        led = AidlLed.Stub.asInterface(mDeviceService.getLed());
                    }
                    if(mDeviceService.getPinPad(0)!=null){
                        pinpad = AidlPinpad.Stub.asInterface(mDeviceService.getPinPad(0));
                    }
                    if(mDeviceService.getBuzzer()!=null){
                        buzzer = AidlBuzzer.Stub.asInterface(mDeviceService.getBuzzer());
                    }
                    if(mDeviceService.getPrinter()!=null){
                        printer = AidlPrinter.Stub.asInterface(mDeviceService.getPrinter());
                    }
                    if(mDeviceService.getShellMonitor()!=null){
                        shell = AidlShellMonitor.Stub.asInterface(mDeviceService.getShellMonitor());
                    }
                    if (mDeviceService.getCPUCard()!=null) {
                        cpuCard = AidlCPUCard.Stub.asInterface(mDeviceService.getCPUCard());
                    }
                    if (mDeviceService.getInsertCardReader()!=null) {
                        icCard = AidlICCard.Stub.asInterface(mDeviceService.getInsertCardReader());
                    }
                    if (mDeviceService.getRFIDReader()!=null) {
                        rfCard = AidlRFCard.Stub.asInterface(mDeviceService.getRFIDReader());
                    }
                    if (mDeviceService.getMagCardReader()!=null) {
                        magCard = AidlMagCard.Stub.asInterface(mDeviceService.getMagCardReader());
                    }
                    if (mDeviceService.getPedestal()!=null) {
                        pedestal = AidlPedestal.Stub.asInterface(mDeviceService.getPedestal());
                    }
                    if (mDeviceService.getDecoder()!=null) {
                        decoder = AidlDecoderManager.Stub.asInterface(mDeviceService.getDecoder());
                    }
                    if (mDeviceService.getCameraManager()!=null) {
                        camera = AidlCameraScanCode.Stub.asInterface(mDeviceService.getCameraManager());
                    }
                    if(mDeviceService.getL2Emv()!=null){
                        emvL2 = AidlEmvL2.Stub.asInterface(mDeviceService.getL2Emv());
                        pure = AidlPure.Stub.asInterface(mDeviceService.getL2Pure());
                        paypass = AidlPaypass.Stub.asInterface(mDeviceService.getL2Paypass());
                        paywave = AidlPaywave.Stub.asInterface(mDeviceService.getL2Paywave());
                        entry = AidlEntry.Stub.asInterface(mDeviceService.getL2Entry());
                        amex = AidlAmex.Stub.asInterface(mDeviceService.getL2Amex());
                        qpboc = AidlQpboc.Stub.asInterface(mDeviceService.getL2Qpboc());
                        rupay = AidlRupay.Stub.asInterface(mDeviceService.getL2Rupay());
                        jcb = AidlJcb.Stub.asInterface(mDeviceService.getL2JCB());
                        mir = AidlMir.Stub.asInterface(mDeviceService.getL2Mir());
                        dpas = AidlDpas.Stub.asInterface(mDeviceService.getL2Dpas());
                    }
                    if(mDeviceService.getCheckCard()!=null){
                        checkCard = AidlCheckCard.Stub.asInterface(mDeviceService.getCheckCard());
                    }
                    if(mDeviceService.getPM()!=null){
                        pm = AidlPM.Stub.asInterface(mDeviceService.getPM());
                    }
                    if(mDeviceService.getFingerprint()!=null){
                        fingerprint = AidlFingerprint.Stub.asInterface(mDeviceService.getFingerprint());
                    }
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.i(TAG,"onServiceDisconnected");
            mDeviceService = null;
            if(!stopBind){
                bindDeviceService(mContext);
            }
        }
    };

    public AidlPsam getPsamCardReader(int devid) throws RemoteException {
        if (mDeviceService != null) {
            return AidlPsam.Stub.asInterface(mDeviceService.getPSAMReader(devid));
        }
        return null;
    }

    public AidlSerialport getSerialPort(int port) throws RemoteException {
        if (mDeviceService != null) {
            return AidlSerialport.Stub.asInterface(mDeviceService.getSerialPort(port));
        }
        return null;
    }

    public Bundle expandFunction(Bundle param) throws RemoteException {
        if (mDeviceService != null) {
            return mDeviceService.expandFunction(param);
        }
        return null;
    }
}