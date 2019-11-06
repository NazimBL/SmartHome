package com.example.dell.nfcommunication;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.Toast;


public class ThreadBtConnection extends Thread {


    private BluetoothSocket bluetoothSocket = null;
    private BluetoothDevice bluetoothDev;
    private BluetoothAdapter BA;
    String op;

    public ThreadBtConnection(BluetoothDevice bD,String code) {
        this.bluetoothDev = bD;
        BluetoothSocket socket=null;
        op=code;

        try{
            BA=BluetoothAdapter.getDefaultAdapter();
            bluetoothDev=BA.getRemoteDevice(bluetoothDev.getAddress());
            Log.d("Nazim","bluetoothDev: "+bluetoothDev);
            socket=bluetoothDev.createInsecureRfcommSocketToServiceRecord(MainActivity.MY_UUID_SECURE);
            Log.d("Nazim","BluetoothSocket : "+socket);

        }catch (Exception e){
            e.printStackTrace();
        }
        bluetoothSocket=socket;
    }

    @Override
    public void run() {

       // BA.cancelDiscovery();

        try{
            bluetoothSocket.connect();
            MainActivity.socket=bluetoothSocket;
            MainActivity.connected=true;
            MainActivity.myHandler.post(new Runnable() {
                @Override
                public void run() {

                    MainActivity.myToast(MainActivity.context,"Connected");
                }
            });

        }catch (Exception e){
            MainActivity.connected=false;
            MainActivity.myHandler.post(new Runnable() {
                @Override
                public void run() {
                    MainActivity.myToast(MainActivity.context,"Connection Error Try again");

                }
            });
            Log.d("Nazim",e.toString());
            try{
                bluetoothSocket.close();
                Log.d("Nazim","Closing thread");
            }catch (Exception ex){
                Log.d("Nazim",ex.toString());
            }
        }

    }
}
