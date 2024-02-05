package com.example.accesssdk1;

import android.os.Handler;
import android.os.Looper;
import android.serialport.SerialPort;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;


public class Serial {

    private final String TAG = "SerialPortUtils";
    private String path = "/dev/ttyUW1";
    private int baudrate = 115200;

    public boolean serialPortStatus = false; //是否打开串口标志
    public String data_;
    public boolean threadStatus; //线程状态，为了安全终止线程

    public SerialPort serialPort = null;
    public InputStream inputStream = null;//serialPort.getInputStream()
    public OutputStream outputStream = null;//serialPort.getOutputStream()
    //public ChangeTool changeTool = new ChangeTool();

    public static byte[] buffer = new byte[64];


    //这是写了一监听器来监听接收数据
    private OnDataReceiveListener onDataReceiveListener = null;
    public SerialPort openSerialPort(){
        try {
            serialPort = new SerialPort(new File(path),baudrate);
            // 创建一个 Handler
            Handler handler = new Handler(Looper.getMainLooper());
            // 在主线程外更新 serialPortStatus
            handler.post(() -> {
                this.serialPortStatus = true; // 更新状态变量
            });

            //this.serialPortStatus = true;
            threadStatus = false; //线程状态

            //获取打开的串口中的输入输出流，以便于串口数据的收发
            inputStream = serialPort.getInputStream();
            outputStream = serialPort.getOutputStream();

            new ReadThread().start(); //开始线程监控是否有数据要接收
        } catch (IOException e) {
            Log.e(TAG, "openSerialPort: 打开串口异常：" + e.toString());
            return serialPort;
        }
        Log.d(TAG, "openSerialPort: 打开串口");
        return serialPort;
    }

    /**
     * 关闭串口
     */
    public void closeSerialPort(){
        try {
            inputStream.close();
            outputStream.close();

            this.serialPortStatus = false;
            this.threadStatus = true; //线程状态
            serialPort.close();
        } catch (IOException e) {
            Log.e(TAG, "closeSerialPort: 关闭串口异常："+e.toString());
            return;
        }
        Log.d(TAG, "closeSerialPort: 关闭串口成功");
    }

    /**
     * 发送串口指令（字符串）
     * @param data String数据指令
     */
    public void sendSerialPort(byte[] data){
        Log.d(TAG, "sendSerialPort: 发送数据");

        try {

            this.data_ = new String(data); //byte[]转string
            if (data.length > 0) {
                outputStream.write(data);
                outputStream.write('\n');
                //outputStream.write('\r'+'\n');
                outputStream.flush();
                Log.d(TAG, "sendSerialPort: 串口数据发送成功");
            }
        } catch (IOException e) {
            Log.e(TAG, "sendSerialPort: 串口数据发送失败："+e.toString());
        }

    }

    /**
     * 单开一线程，来读数据
     */
    private class ReadThread extends Thread{
        @Override
        public void run() {
            super.run();
            //判断进程是否在运行，更安全的结束进程
            while (!threadStatus){
                //Log.d(TAG, "进入线程run");
                //64   1024
                int size; //读取数据的大小
                try {
                    inputStream = serialPort.getInputStream();
                    size = inputStream.read(buffer);
                    if (size > 0){
                        //Log.d(TAG, "run: 接收到了数据：" + changeTool.ByteArrToHex(buffer));
                       // Log.d(TAG, "run: 接收到了数据大小：" + String.valueOf(size));
                        onDataReceiveListener.onDataReceive(buffer,size);
                    }
                } catch (IOException e) {
                    Log.e(TAG, "run: 数据读取异常：" +e.toString());
                }
            }

        }
    }


    public static interface OnDataReceiveListener {
        public void onDataReceive(byte[] buffer, int size);

        void processGNGGAData(String gnggaLine);

        void processRMCData(String rmcLine);
    }
    public void setOnDataReceiveListener(OnDataReceiveListener dataReceiveListener) {
        onDataReceiveListener = dataReceiveListener;
    }
}
