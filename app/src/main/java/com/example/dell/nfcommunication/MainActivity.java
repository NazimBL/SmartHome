package com.example.dell.nfcommunication;

import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.nfc.NfcAdapter;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

import cz.msebera.android.httpclient.Header;


public class MainActivity extends AppCompatActivity {


    public final static String POMPIER="911";
    LinearLayout templayout,humlayout,lightlayout,batterylayout,garagelayout,fanlayout;
    FrameLayout  jarvislayout,powerlayout;
    Button connect_b;
    static boolean garage_tag=true;

    private BluetoothAdapter BA;
    static ThreadCommunication com;
    private Set<BluetoothDevice> pairedDevices;

    public static String op;
    public static TextView humtext,temptext,battext,fantext,garagetext;
    public static Handler myHandler=new Handler();
    public static BluetoothSocket socket;
    public static char read_code='t';
    static BluetoothDevice btdevice;
    public static Context context;
    public static String dataRead="";
    public static  char[] codes={'t','h'};
    public static boolean connected=false;
    public static float temperature=0,humidity=0;
    public static float battery=0;
    public boolean thread_tag=true;
    byte index=0;
    boolean tag=true;
    boolean read_tag=true;
    static boolean  write_tag=false;
    public static final  UUID MY_UUID_SECURE =
            UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initialize();

        connect_b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BA=BluetoothAdapter.getDefaultAdapter();
                if(!connected){
                    if(!BA.isEnabled())turnOnBt();
                    connectToDevice();
                }else turnOffBt();
            }
        });

        humlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(connected && thread_tag) Dht11monitor();
            }
        });
      templayout.setOnClickListener(new View.OnClickListener() {

          //displays both hum and temp , using code arduino dht11_read
          @Override
          public void onClick(View v) {
              myDialog();
          }
      });
        lightlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,Lighting.class));
            }
        });


        jarvislayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,SpeechToText.class));
            }
        });

        powerlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,PowerMonitor.class));
            }
        });
        garagelayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                write_tag=true;
                if(connected){

                    if(garage_tag){
                        bluetoothOperation("0",btdevice);
                        garagetext.setText("Open");
                    }
                    else{
                        bluetoothOperation("1",btdevice);
                        garagetext.setText("Closed");
                    }
                    garage_tag=!garage_tag;
                    write_tag=false;

                }else myToast(MainActivity.this,"Not Connected");
            }
        });
        fanlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                write_tag=true;
                if(connected){

                    if(garage_tag) {
                        bluetoothOperation("2",btdevice);
                        fantext.setText("On");

                    }
                    else {
                        bluetoothOperation("3",btdevice);
                        fantext.setText("Off");
                    }
                    garage_tag=!garage_tag;
                    write_tag=false;
                }else myToast(MainActivity.this,"Not Connected");

            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        thread_tag=false;

    }
    void initialize() {

        humlayout=(LinearLayout)findViewById(R.id.humidity_layout);
        fanlayout=(LinearLayout)findViewById(R.id.fan_img);
        garagelayout=(LinearLayout)findViewById(R.id.garage_img);
        templayout=(LinearLayout)findViewById(R.id.temp_layout);
        lightlayout=(LinearLayout)findViewById(R.id.light_layout);
        jarvislayout=(FrameLayout)findViewById(R.id.jarvis);
        batterylayout=(LinearLayout)findViewById(R.id.batlayout);
        powerlayout=(FrameLayout)findViewById(R.id.power_layout);

        fantext=(TextView)findViewById(R.id.fan_text);
        garagetext=(TextView)findViewById(R.id.garage_text);

        humtext=(TextView)findViewById(R.id.hum_text);
        humtext.setText("40%");
        temptext=(TextView)findViewById(R.id.temp_text);
        temptext.setText("21°");
        battext=(TextView)findViewById(R.id.bat_text);
        battery=getIntent().getFloatExtra("battery",0);
        battext.setText(battery+"%");
        connect_b=(Button)findViewById(R.id.connect);
        context=MainActivity.this;
        thread_tag=true;
        read_tag=true;
        tag=true;

    }
    void Dht11monitor(){

        new Thread(new Runnable() {
            @Override
            public void run() {
                while(true){
                    if(connected){

                        if(read_code!=codes[index]) {
                            read_code = codes[index];
                            bluetoothOperation(""+read_code,btdevice);
                        }

                        if(tag) {

                            ThreadCommunication communication = new ThreadCommunication(MainActivity.socket, "read");
                            communication.start();
                            read_tag=false;
                        }
                        try {
                            Thread.sleep(1000);

                        }catch (Exception e){

                            if(temperature>28)call(POMPIER);
                        }
                        index++;
                        if(index>1)index=0;
                    }else myToast(MainActivity.this,"Not Connected");
                }
            }
        }).start();
    }

    void myDialog(){

        AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Temperature Setting").setMessage("Set Desired Temperature");
        final EditText input = new EditText(MainActivity.this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        builder.setView(input);
        builder.setNeutralButton("Validate", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

               if(!input.getText().toString().equals("")) temperature=Float.parseFloat(input.getText().toString());

                if(temperature<24){
                    garage_tag=false;
                    bluetoothOperation("2",btdevice);
                    fantext.setText("On");
                }
            }
        }).show();

    }

    public static void bluetoothOperation(String op,BluetoothDevice bt){

        Log.d("Nazim",""+connected);
        if(connected){

            ThreadCommunication comm=new ThreadCommunication(socket,op);
            comm.write(op);
            com=comm;
        }

        else{
            ThreadBtConnection connection=new ThreadBtConnection(bt,op);
            connection.start();
        }

    }

    void connectToDevice(){
        pairedDevices = BA.getBondedDevices();
        ArrayList list = new ArrayList();

        for (BluetoothDevice bt : pairedDevices) {
            list.add(bt.getName());
            if (bt.getName().equals("HC-06")) {
                btdevice = bt;
                Log.d("Nazim", "detected");
            }

        }
        if (btdevice != null) {
            ThreadBtConnection connection = new ThreadBtConnection(btdevice, op);
            connection.start();

        }
    }
    public void turnOnBt(){
        if (!BA.isEnabled()) {
            Intent turnOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(turnOn, 0);
            Toast.makeText(getApplicationContext(), "Turning On Bluetooth",Toast.LENGTH_LONG).show();
        }
    }
    void turnOffBt(){

        if(BA.isEnabled() && connected){
            connected=false;
            try{
                socket.close();
            }catch (Exception e){
            }
        }
        connect_b.setText("Connect");
    }
    public static void myToast(Context context,String msg){

        Toast.makeText(context,msg,Toast.LENGTH_SHORT).show();

    }
    public void call(String number) {
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" + number));

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        startActivity(callIntent);

    }
    public void sendSms(String phone,String text){
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(phone, null, text, null, null);

    }

    @Override
    public void onBackPressed() {
        finishAffinity();
        System.exit(0);
    }
}
