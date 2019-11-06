package com.example.dell.nfcommunication;

import android.bluetooth.BluetoothDevice;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;

public class Lighting extends AppCompatActivity {

    Button ch1,ch2,salon,kitchen,garage,gym,on,off;
    BluetoothDevice btdevice;
    boolean s1=false,s2=false,s3=false,s4=false,s5=false,s6=false;
    public final  byte ROOM1=2,ROOM2=3,SALON=5,KITCHEN=4,GYM=6,GARAGE=7;
    SeekBar seekBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lighting);
        init();
        ch1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if(!s1){
                    ch1.setBackgroundColor(Color.GREEN);
                }
                else {
                    ch1.setBackgroundColor(Color.RED);
                }
                s1=!s1;

            }
        });

        ch2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!s2){
                    ch2.setBackgroundColor(Color.GREEN);
                }
                else {
                    ch2.setBackgroundColor(Color.RED);
                }
                s2=!s2;
            }
        });
        salon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!s4){
                    salon.setBackgroundColor(Color.GREEN);
                }
                else {
                    salon.setBackgroundColor(Color.RED);
                }
                s4=!s4;
            }
        });

        kitchen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!s3){
                    kitchen.setBackgroundColor(Color.GREEN);
                }
                else {
                    kitchen.setBackgroundColor(Color.RED);
                }
                s3=!s3;
            }
        });
        gym.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!s5){
                    gym.setBackgroundColor(Color.GREEN);
                }
                else {
                    gym.setBackgroundColor(Color.RED);
                }
                s5=!s5;
            }
        });
        garage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!s6){
                    garage.setBackgroundColor(Color.GREEN);
                }
                else {
                    garage.setBackgroundColor(Color.RED);
                }
                s6=!s6;
            }
        });
        on.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendLightOn();
            }
        });
        off.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendLightOff();
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
               int i= seekBar.getProgress();
                if(s4) MainActivity.bluetoothOperation(fetchSeekbarProgress(i,'5'), btdevice);
                if(s5) MainActivity.bluetoothOperation(fetchSeekbarProgress(i,'6'), btdevice);



            }
        });
    }
    String fetchSeekbarProgress(int p,char pin){
        String send = "";
        if (p < 100) send = "0" + p;
        else send=""+p;
        send=pin+send;
        send='a'+send;
        return send;

    }
    void init(){

        ch1=(Button)findViewById(R.id.ch1);
        ch1.setBackgroundColor(Color.RED);
        ch2=(Button)findViewById(R.id.ch2);
        ch2.setBackgroundColor(Color.RED);
        salon=(Button)findViewById(R.id.salon);
        salon.setBackgroundColor(Color.RED);
        kitchen=(Button)findViewById(R.id.kitchen);
        kitchen.setBackgroundColor(Color.RED);
        garage=(Button)findViewById(R.id.garage);
        garage.setBackgroundColor(Color.RED);
        gym=(Button)findViewById(R.id.gym);
        gym.setBackgroundColor(Color.RED);
        on=(Button)findViewById(R.id.on);
        off=(Button)findViewById(R.id.off);
        seekBar=(SeekBar)findViewById(R.id.seekbar);
        seekBar.setMax(255);
        seekBar.setProgress(255);

        if(MainActivity.btdevice!=null)btdevice=MainActivity.btdevice;
    }
    void sendLightOff(){
        if(MainActivity.connected){

            //room1
            if(s1)MainActivity.bluetoothOperation('d'+"0400",btdevice);
           // if(s2)MainActivity.bluetoothOperation('d'+"0200",btdevice);
            //kitchen
            if(s3) MainActivity.bluetoothOperation('d'+"0300",btdevice);
            //salon
            if(s4)MainActivity.bluetoothOperation('d'+"0500",btdevice);
            //gym
            if(s5)MainActivity.bluetoothOperation('d'+"0600",btdevice);
            //garage
            if(s6)MainActivity.bluetoothOperation('d'+"0700",btdevice);

        }else MainActivity.myToast(Lighting.this,"Not connected");

    }

    void sendLightOn(){
        if(MainActivity.connected ){

            //msb is the state , lsb is the pin number
            //pin 7 is room one


            if(s1)MainActivity.bluetoothOperation('d'+"1400",btdevice);
            // if(s2)MainActivity.bluetoothOperation('d'+"0200",btdevice);
            if(s3) MainActivity.bluetoothOperation('d'+"1300",btdevice);
            if(s4)MainActivity.bluetoothOperation('d'+"1500",btdevice);
            if(s5)MainActivity.bluetoothOperation('d'+"1600",btdevice);
            if(s6)MainActivity.bluetoothOperation('d'+"1700",btdevice);

        }
        else MainActivity.myToast(Lighting.this,"Not Connected");


    }

}
