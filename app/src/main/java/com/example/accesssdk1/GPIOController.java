package com.example.accesssdk1;

import android.serialport.SerialPort;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

public class GPIOController {
    private SerialPort serialPort;
    private OutputStream outputStream;

    public void setGPIOLevel(int pin, boolean isHigh) {
        try {
            serialPort = new SerialPort(new File("/dev/ttyUSB0"), 9600);
            outputStream = serialPort.getOutputStream();

            // 构造控制GPIO引脚的命令
            String command = "set_zysj_gpio_value(" + pin + "," + (isHigh ? "1" : "0") + ")\n";
            Log.d("SerialPortDebug", "Sending command: " + command);

            // 发送命令到串口
            outputStream.write(command.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
        }
        }

