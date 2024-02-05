package com.example.accesssdk1;

import static android.content.ContentValues.TAG;
import static com.qxwz.sdk.core.Constants.QXWZ_SDK_CAP_ID_NOSR;
import static com.qxwz.sdk.core.Constants.QXWZ_SDK_ERR_AUTHING;
import static com.qxwz.sdk.core.Constants.QXWZ_SDK_ERR_INVALID_CONFIG;
import static com.qxwz.sdk.core.Constants.QXWZ_SDK_STAT_AUTH_SUCC;
import static com.qxwz.sdk.core.Constants.QXWZ_SDK_STAT_OK;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.SystemClock;
import android.serialport.SerialPort;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.qxwz.sdk.configs.AccountInfo;
import com.qxwz.sdk.configs.SDKConfig;
import com.qxwz.sdk.core.CapInfo;
import com.qxwz.sdk.core.Constants;
import com.qxwz.sdk.core.IRtcmSDKCallback;
import com.qxwz.sdk.core.RtcmSDKManager;
import com.qxwz.sdk.types.KeyType;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MyService extends Service implements IRtcmSDKCallback {


    private static final String TAG = "qxwz";

    private static final String AK = "A4ec4gb2fmpn";
    private static final String AS = "5c408b9cfb15bd31";
    private static final String DEVICE_ID = "D4d84gb2fnrh";
    private static final String DEVICE_TYPE = "183966c7b5d33684";
    private static String GGA = "";

    private static String DataPool = "";

    private static String time = "";
    private static String latitude = "";
    private static String longitude = "";
    private static String Positioning ="";
    private static String msl = "";
    private boolean isStart = false;

    private Serial serial;

    private final IBinder binder = new MyBinder();

    
    private NotificationManager notificationMgr;
    private boolean canRun =true;
    private String retString = null;



    public class MyBinder extends Binder{
        MyService getService(){
            return MyService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent)
    {
        Log.d(TAG, String.format("on bind,intent = %s", intent.toString()));
        notificationMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        return binder;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        // 在服务创建时初始化串口对象等
        serial = new Serial();
        serial.setOnDataReceiveListener(new Serial.OnDataReceiveListener() {
            @Override
            public void onDataReceive(byte[] buffer, int size) {
             /*   String strData = new String((buffer));
                String regex = "\\$GNGGA.*";
                Pattern pattern = Pattern.compile(regex);
                Matcher matcher = pattern.matcher(strData);
                if(matcher.find()){
                    String ggaMessage = matcher.group();
                    System.out.println(ggaMessage+"wancheng");
                }*/
                // 处理接收到的数据
                int print = 0;
                String [] temps = DataPool.split("\\$",1000);
                //System.out.println(temps);

                if(temps.length > 18){

                    DataPool = "";
                    print = 1;
                }else{
                    String data = new String(buffer, 0, size);
                    DataPool += data;
                    //GGA = DataPool;
                }

                if(print == 1){
                    System.out.println("temp长度："+temps.length);
                    int i = 0;
                    for(String string : temps){
                        if(i == 0){
                            System.out.print(1+"\n");
                        }else{
                            //System.out.println("temps[" + i + "]:"+string);
                            String[] lines = string.split(",");
                            //GGA = String.join(",",lines);

                            if(lines[0].equals("GNGGA")&&lines.length>=15){
                                GGA = "";
                                System.out.println("456789");
                                longitude = lines[2];
                                latitude = lines[4];
                                msl = lines[9];
                                System.out.println("经度为："+longitude);
                                System.out.println("纬度为："+latitude);
                                System.out.println("高程为："+msl);

                                GGA="$GNGGA";
                                for (int j = 1; j < Math.min(15, lines.length); j++) {
                                    GGA += "," + lines[j];
                                    System.out.println("GGA= "+GGA);

                                }
                            }
                        }
                        i++;
                    }
                }
            }

            @Override
            public void processGNGGAData(String gnggaLine) {

            }

            @Override
            public void processRMCData(String rmcLine) {

            }
        });
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // 在服务启动时执行串口打开等操作
        SerialPort serialPort = serial.openSerialPort();
        if (serialPort != null) {
            // 获取输出流
            serial.outputStream = serialPort.getOutputStream();
            serial.inputStream = serialPort.getInputStream();
        } else {
            Log.e(TAG, "Failed to open serial port");
        }

        // 初始化千寻SDK服务
        initQianXunSDK();

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // 在服务销毁时执行关闭串口等操作
        serial.closeSerialPort();
        // 清理千寻SDK服务
        RtcmSDKManager.getInstance().cleanup();
    }

    private void initQianXunSDK(){
        SDKConfig.Builder builder = SDKConfig.builder()
                .setAccountInfo(
                        AccountInfo.builder()
                                .setKeyType(KeyType.QXWZ_SDK_KEY_TYPE_AK)
                                .setKey(AK)
                                .setSecret(AS)
                                .setDeviceId(DEVICE_ID)
                                .setDeviceType(DEVICE_TYPE)
                                .build())
                .setRtcmSDKCallback(this);

        RtcmSDKManager rtc=RtcmSDKManager.getInstance();
        SDKConfig conf = builder.build();

        //初始化
        int init = RtcmSDKManager.getInstance().init(conf);
        if(init==QXWZ_SDK_STAT_OK)
        {
            System.out.println("初始化成功");

        }
        else if(init==QXWZ_SDK_ERR_INVALID_CONFIG)
        {
            System.out.println(" config⾮法,建议检查参数是否正\n" +
                    "确");
        }

        //鉴权
        // Call the auth() method
        int authStatus = RtcmSDKManager.getInstance().auth();

        // Handle the authentication status
        //处理验证身份状态
        if (authStatus == QXWZ_SDK_STAT_OK) {
            // Authentication successful
            //身份验证成功
            System.out.println("Authentication successful");
            // Access the granted capabilities
            //访问授予的功能
            List<CapInfo> grantedCaps = rtc.getCapsInfo();
        } else if (authStatus == QXWZ_SDK_ERR_AUTHING) {
            // Authentication in progress
            //身份验证正在进行
            System.out.println("Authentication in progress");
            // Implement asynchronous handling or callbacks for authentication completion
            //实现异步处理或回调以完成身份验证
        } else {
            // Handle other authentication status codes as needed
            //根据需要处理其他身份验证状态代码
            System.out.println("Authentication status: " + authStatus);
        }
    }

    @Override
    public void onData(int type, byte[] bytes) {
        Log.d(TAG, "rtcm data received, data length is " + bytes.length);

        serial.sendSerialPort(bytes);
    }

    @Override
    public void onStatus(int status) {
        Log.d(TAG, "status changed to " + status);
    }

    @Override
    public void onAuth(int code, List<CapInfo> caps) {
        // 处理千寻SDK的认证结果
        if (code == QXWZ_SDK_STAT_AUTH_SUCC) {

            Log.d(TAG, "auth successfully.");
            for (CapInfo capInfo : caps) {
                Log.d(TAG, "capInfo:" + capInfo.toString());
            }
            //我想启用能力
            //RtcmSDKManager.getInstance().start(QXWZ_SDK_CAP_ID_NOSR);

            /* if you want to call the start api in the callback function, you must invoke it in a new thread. */
            new Thread() {
                public void run() {
                    RtcmSDKManager.getInstance().start(QXWZ_SDK_CAP_ID_NOSR);
                }
            }.start();

        } else {
            Log.d(TAG, "failed to auth, code is " + code);
        }
    }

    @Override
    public void onStart(int code, int capId) {
        // 处理千寻SDK的启动结果
        if (code == Constants.QXWZ_SDK_STAT_CAP_START_SUCC) {
            Log.d(TAG, "start successfully.");
            isStart = true;
            new Thread() {
                public void run() {
                    while (isStart) {
                        if(GGA!=""&&GGA.length()>=15){
                            int a = RtcmSDKManager.getInstance().sendGga(GGA);
                            if(a == QXWZ_SDK_STAT_OK)
                            {
                                System.out.println("成功");
                            }
                        }
                        SystemClock.sleep(100);
                    }
                }
            }.start();
        } else {
            Log.d(TAG, "failed to start, code is " + code);
        }
    }
}

