package com.example.accesssdk1;

import static com.qxwz.sdk.core.Constants.QXWZ_SDK_CAP_ID_NOSR;
import static com.qxwz.sdk.core.Constants.QXWZ_SDK_ERR_AUTHING;
import static com.qxwz.sdk.core.Constants.QXWZ_SDK_ERR_INVALID_CONFIG;
import static com.qxwz.sdk.core.Constants.QXWZ_SDK_STAT_AUTH_SUCC;
import static com.qxwz.sdk.core.Constants.QXWZ_SDK_STAT_OK;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.OnNmeaMessageListener;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;


import android.provider.ContactsContract;
import android.serialport.SerialPort;
import android.serialport.SerialPortFinder;
import android.util.Log;
import android.view.View;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.qxwz.sdk.configs.AccountInfo;
import com.qxwz.sdk.configs.SDKConfig;
import com.qxwz.sdk.core.CapInfo;
import com.qxwz.sdk.core.Constants;
import com.qxwz.sdk.core.CoordSysInfo;
import com.qxwz.sdk.core.IRtcmSDKCallback;
import com.qxwz.sdk.core.IRtcmSDKGetCoordSysCallback;
import com.qxwz.sdk.core.RtcmSDKManager;
import com.qxwz.sdk.types.KeyType;

import android.location.GpsStatus.NmeaListener;
import android.widget.TextView;
//import gnu.io.CommPortIdentifier;


import java.sql.SQLOutput;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/*implements IRtcmSDKCallback, View.OnClickListener,IRtcmSDKGetCoordSysCallback, Serial.OnDataReceiveListener, LocationListener, OnNmeaMessageListener*/

@RequiresApi(api = Build.VERSION_CODES.N)
public class MainActivity extends AppCompatActivity  {

    private MyService mMyService;
    private ServiceConnection mServiceConnection = new ServiceConnection()
    {
        @Override
        public void onServiceDisconnected(ComponentName name)
        {
            // TODO Auto-generated method stub
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service)
        {
            // bindService成功的时候返回service的引用
            MyService.MyBinder myBinder = (MyService.MyBinder)service;
            mMyService = myBinder.getService();
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {


//启动服务
        super.onCreate(savedInstanceState);
        Intent intentService = new Intent(MainActivity.this, MyService.class);
        //intentService.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intentService.setAction("scott");
//bindService用于和service进行交互

        MainActivity.this.bindService(intentService, mServiceConnection, BIND_AUTO_CREATE);
//startService用于启动service但是不和其交互
        startService(intentService);

    }

    /* private static final String TAG = "qxwz";

    private static final String AK = "A4ec4gb2fmpn";
    private static final String AS = "5c408b9cfb15bd31";
    private static final String DEVICE_ID = "D4d84gb2fnrh";
    private static final String DEVICE_TYPE = "183966c7b5d33684";

    private static final String GGA1 = "$GNGGA,024942.000,3908.2987830,N,11712.1153842,E,5,08,3.518,-8.601,M,-5.425,M,1,1679*48";
    //private static final String GGA2 = "$GNGGA,083507.007,0000.0000000,N,00000.0000000,E,0,00,4.833,0.000,M,0.000,M,,*71";

    private static String GGA = "";

    private static String DataPool = "";

    private static String time = "";
    private static String latitude = "";
    private static String longitude = "";
    private static String Positioning ="";
    private static String msl = "";
    private boolean isStart = false;

    private  Serial serial;

    Handler handler = null;

    @Override
    public void onResult(int var1, CoordSysInfo var2) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);


        *//*handler = new Handler(){
            @Override
            public void handleMessage(Message msg){
                // call update gui method.
                TextView textView = findViewById(R.id.textView);
                    textView.setText(GGA);
            }
        };*//*

     *//* TextView textView = findViewById(R.id.textView);
        textView.setText(GGA);*//*



       SerialPortFinder mSerialPortFinder = new SerialPortFinder();
        mSerialPortFinder.getAllDevices();


        serial = new Serial();
        serial.setOnDataReceiveListener(this);


        SerialPort serialPort = serial.openSerialPort();
        if (serialPort != null) {
            // 获取输出流
            serial.outputStream = serialPort.getOutputStream();
            serial.inputStream = serialPort.getInputStream();
        } else {
            System.out.println("Failed to open serial port");
        }



        GPIOController gpioController = new GPIOController();
        gpioController.setGPIOLevel(22,true);


        LocationManager locationManager = (LocationManager) getSystemService(this.LOCATION_SERVICE);

        boolean providerEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);


        String[] permissionsGroup = new String[]{

                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION

        };
       if (providerEnabled) {//开启了定位权限
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                //没开启权限申请权限

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(permissionsGroup,10);
                }

            } else {

                //有权限
                locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1,  this);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    locationManager.addNmeaListener((OnNmeaMessageListener) this);

                } else {
                    locationManager.addNmeaListener((NmeaListener) this);
                }
            }
        }
    }

    @Override
    public void onDataReceive(byte[] buffer, int size){

        int print = 0;
        String [] temps = DataPool.split("\\$",1000);

        if(temps.length > 18){

            DataPool = "";
            print = 1;
        }else{
            String data = new String(buffer, 0, size);
            DataPool += data;
            //GGA = DataPool;
        }
        //System.out.println("DataPool="+DataPool);

       // System.out.println("temps:"+ Arrays.toString(temps)+"完成");
        //System.out.println("temps[0]:"+ temps[0]+"完成");
        //System.out.println("temps[1]:"+ temps[1]+"完成");
        //System.out.println("temps[2]:"+ temps[2]+"完成");
        //System.out.println("temps[3]:"+ temps[3]+"完成");
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

                        for (int j = 0; j < Math.min(15, lines.length); j++) {
                            GGA += "," + lines[j];
                            //System.out.println("GGA= "+GGA);

                        }
                    }
                }
                i++;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // 在主线程中更新UI
                        TextView textView = findViewById(R.id.textView); // 获取 TextView
                        if(GGA.length()>=15){
                            textView.setText("经度为："+longitude+"\n"+"纬度为："+latitude+"\n"+"高程为:"+msl);
                        }
                    }
                });
                }
            }
        }



      *//*if(temps.length>=1)
      {
          for (String line : temps){
              if(line.contains("$GNGGA")){
                  if(!(line.length()<15)){
                      GGA = "";
                      GGA = line;
                      String[] parts = GGA.split(",");
                      // 获取经纬度等信息
                      time = parts[1];
                      latitude = parts[2];
                      //longitude = parts[4];
                      //Positioning = parts[6];
                      *//**//*if(parts.length>=8){
                          msl = parts[8];
                      }*//**//*

                  }else{
                      System.out.println("GNGGA无效");
                  }
                  //System.out.println("time:"+time+" latitude:"+latitude+" longitude："+longitude+" msl:"+msl);
                  //System.out.println("Found $GNGGA data: " + line+"\n");
                  //在APP上显示数据



                  } else if(line.contains("$GNRMC")) {
                  if(!(line.length()<14)){
                      String rmc = line;
                      String[] parts = rmc.split(",");
                      //System.out.println("$GNRMC data: " + line+"\n");
                  }else{
                      System.out.println("GNRMC无效");
                  }
                  }
              }
          //DataPool = "";
          }*//*
     *//*if (!DataPool.endsWith("\r\n")) {
              int lastBreakIndex = DataPool.lastIndexOf("\r\n");
              if (lastBreakIndex != -1) {
                  DataPool = DataPool.substring(lastBreakIndex + 2);
              }
          } else {
              DataPool = "";
          }*//*
     *//*if(!temps[0].contains("$GNGGA")){
              DataPool = temps[1];
          }else{
              GGA = temps[0];
              System.out.println(GGA);
          }
          //System.out.println("整体数据");
          //GGA.replace("$GNGGA","$GPGGA");
          DataPool = temps[1];*//*

          //GGA = data;
         *//* if (GGA.contains("$GNGGA")){
              String[] parts = GGA.split(",");
              // 获取经纬度等信息
              time = parts[1];
              latitude = parts[2];
              longitude = parts[4];
              //Positioning = parts[6];
              if(parts.length>=8){
                  msl = parts[8];
              }

              System.out.println("time:"+time+" latitude:"+latitude+" longitude："+longitude+" msl:"+msl);
          }else{
              System.out.println("没找到");
          }*//*
     *//* }*//*

    @Override
    public void processGNGGAData(String gnggaLine) {

    }

    @Override
    public void processRMCData(String rmcLine) {

    }


    @Override
    protected void onResume() {
        super.onResume();
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
        RtcmSDKManager.getInstance().init(builder.build());
        RtcmSDKManager.getInstance().auth();
    }
    

    @Override
    protected void onPause() {
        super.onPause();
        isStart = false;
        RtcmSDKManager.getInstance().stop(QXWZ_SDK_CAP_ID_NOSR);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RtcmSDKManager.getInstance().cleanup();
    }

    @Override
    public void onData(int type, byte[] bytes) {
        Log.d(TAG, "rtcm data received, data length is " + bytes.length);

                serial.sendSerialPort(bytes);
        


        // Convert the received data to a string
    }

    @Override
    public void onStatus(int status) {
        Log.d(TAG, "status changed to " + status);
    }

    @Override
    public void onAuth(int code, List<CapInfo> caps) {
        if (code == QXWZ_SDK_STAT_AUTH_SUCC) {

            Log.d(TAG, "auth successfully.");
            for (CapInfo capInfo : caps) {
                Log.d(TAG, "capInfo:" + capInfo.toString());
            }
            //我想启用能力
           //RtcmSDKManager.getInstance().start(QXWZ_SDK_CAP_ID_NOSR);

            *//* if you want to call the start api in the callback function, you must invoke it in a new thread. *//*
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


    public void onClick3(View view) {
        System.out.println("停止");
    }

    public void onClick1(View view) {

        try {
            int result = RtcmSDKManager.getInstance().start(1);
            System.out.println(result);
        }catch(Exception e){
            System.out.println(e.getMessage());
        }


    }
    //按键
    @Override
    public void onClick(View view) {
        SDKConfig.Builder builder = SDKConfig.builder()
                .setAccountInfo(
                        AccountInfo.builder()
                                .setKeyType(KeyType.QXWZ_SDK_KEY_TYPE_AK)
                                .setKey(AK)
                                .setSecret(AS)
                                .setDeviceId(DEVICE_ID)
                                .setDeviceType(DEVICE_TYPE)
                                .build()).setRtcmSDKCallback(this);
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
    public void onLocationChanged(@NonNull Location location) {

    }

    @Override
    public void onNmeaMessage(String message, long timestamp) {
        System.out.println(message);
        System.out.println("123");
        Log.i("log2", "onNmeaMessage: "+ message);
    }
}


*/
}