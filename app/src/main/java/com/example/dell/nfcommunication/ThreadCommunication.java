package com.example.dell.nfcommunication;

import android.bluetooth.BluetoothSocket;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import cz.msebera.android.httpclient.entity.mime.content.InputStreamBody;

import static android.R.attr.data;
import static com.example.dell.nfcommunication.MainActivity.dataRead;
import static com.example.dell.nfcommunication.MainActivity.read_code;


public class ThreadCommunication extends Thread {

    InputStream connectedinputStream;
    OutputStream connectedoutputStream;
    BluetoothSocket bluetoothSocket;
    
    public ThreadCommunication(BluetoothSocket connectedbtsocket,String op) {
        //add String op as an argument

        InputStream inputStream = null;
        OutputStream outputStream = null;
        bluetoothSocket = connectedbtsocket;
        //op=code;
        try {
            inputStream = connectedbtsocket.getInputStream();
            outputStream = connectedbtsocket.getOutputStream();

        } catch (Exception e) {
            Log.d("Nazim", e.toString());
        }

        connectedinputStream = inputStream;
        connectedoutputStream = outputStream;

    }

    @Override
    public void run() {

        while (true) {
            try {

                InputStreamReader isr = new InputStreamReader(connectedinputStream);
                BufferedReader br = new BufferedReader(isr);
                String read="";
                String line="";

                if((line=br.readLine())!=""){
                    read=line;
                }

                final String readMessage=read;
                Log.d("dyabond","pre msg :"+readMessage);
                MainActivity.myHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    float float_value = Float.parseFloat(readMessage);
                                    if (MainActivity.read_code == 't') {

                                            MainActivity.temptext.setText(float_value + "°");
                                            MainActivity.temperature = float_value;
                                            Log.d("dyabond","t read :"+float_value);

                                    } else if (MainActivity.read_code == 'h') {
                                            MainActivity.humtext.setText(float_value + "%");
                                            MainActivity.humidity = float_value;
                                        Log.d("dyabond","h read :"+float_value);

                                    }  else if (MainActivity.read_code == 's') {

                                            PowerMonitor.solar_E.setText("Solar Panel \n \n" + float_value + "kwh");
                                            PowerMonitor.s_E = float_value;
                                            Log.d("Nazim","s read :"+float_value);


                                    }  else if (MainActivity.read_code == 'l') {

                                            PowerMonitor.light_E.setText("Lighting \n \n" + float_value + "kws");
                                            PowerMonitor.l_E = float_value;
                                            Log.d("Nazim","l read :"+float_value);
                                    }

                                } catch (Exception e) {
                                    Log.d("dyabond", "fuckoff exeption:");
                                }
                            }
                        });
            }
            catch (IOException e) {
                Log.d("dyabond", "Input stream was disconnected", e);
                break;
            }
        }

    }
    public void write(String op){
        try {
             connectedoutputStream.write(op.getBytes());

        } catch (IOException e) {
            Log.d("Nazim",e.toString());
        }
    }
}