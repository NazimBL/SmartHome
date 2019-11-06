package com.example.dell.nfcommunication;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.speech.RecognizerIntent;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.speech.tts.TextToSpeech;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.Set;

import static com.example.dell.nfcommunication.MainActivity.connected;
import static com.example.dell.nfcommunication.MainActivity.socket;

public class SpeechToText extends AppCompatActivity  {
    LinearLayout layout;
    TextView textView;
    private final int request_code=100;
    String output="";
    TextToSpeech textToSpeech;
    BluetoothDevice btdevice;
    private Set<BluetoothDevice> pairedDevices;
    MediaPlayer music1,music2;
    public final static String PHONE_NUMBER="0796770812";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speech_to_text);
        init();


        layout.setOnTouchListener(new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            try{
                promptSpeechToText();
            }catch (Exception e){
                Log.d("Nazim","Error");

            }
            return false;
        }
    });

    }
    public void list(){
        pairedDevices = BluetoothAdapter.getDefaultAdapter().getBondedDevices();
        ArrayList list = new ArrayList();

        for(BluetoothDevice bt : pairedDevices){
            list.add(bt.getName());
            if(bt.getName().equals("HC-06")){
                btdevice=bt;
                Log.d("Nazim","detected");
            }}}

    void init(){

        list();
        layout=(LinearLayout)findViewById(R.id.activity_speech_to_text);
        textView=(TextView)findViewById(R.id.text);
        textToSpeech=new TextToSpeech(SpeechToText.this, new android.speech.tts.TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != android.speech.tts.TextToSpeech.ERROR) {

                    textToSpeech.setLanguage(Locale.ENGLISH);
                }
            }
        });

        music1=MediaPlayer.create(SpeechToText.this,R.raw.lil_dicky);
        music2=MediaPlayer.create(SpeechToText.this,R.raw.tyga);
    }
    public void promptSpeechToText(){

        Intent intent=new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
       //free form =english
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                "Say Something !");
        try {
            startActivityForResult(intent, request_code);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    "ErrorLoadingActivity",
                    Toast.LENGTH_SHORT).show();
        }

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    if(requestCode==request_code){
        ArrayList<String> result = data
                .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
        textView.setText(result.get(0));
        output=result.get(0);

    }

        if (MainActivity.connected) {
            try{
                checkOrders();
            }catch (Exception e){
                MainActivity.myToast(SpeechToText.this,"Error");
            }
        }

    }

    void checkOrders(){

        if(output.contains("turn room light on") ||output.contains("light room on") || output.contains("room on") || output.contains("room light on")){

            jarvis("Light turned  on");
            MainActivity.bluetoothOperation('d'+"1400",btdevice);
        }
        else if(output.contains("turn room light off") || output.contains("light room off") || output.contains("room off") || output.contains("room light off")){
            jarvis("Light turned off");
            MainActivity.bluetoothOperation('d'+"0400",btdevice);
        }

        else if(output.contains("open the door")||output.contains("open garage") || output.contains("open the garage")){
            jarvis("Garage opened");

            MainActivity.bluetoothOperation("0",btdevice);

        }
        else if(output.contains("close the door") || output.contains("close garage") || output.contains("close the garage")){
            jarvis("Garage closed");

            MainActivity.bluetoothOperation("1",btdevice);

        }
        else if(output.contains("gym light on") || output.contains("jim light on") || output.contains("dim light on")){
            jarvis("Gym light turned on");

            MainActivity.bluetoothOperation('d'+"1600",btdevice);

        }
        else if(output.contains("gym light off") || output.contains("jim light off") || output.contains("dim light off")){
            jarvis("Gym light turned off");

            MainActivity.bluetoothOperation('d'+"0600",btdevice);

        }
        else if((output.contains("living room ") ||  output.contains("leaving room")) && output.contains("light on")){
            jarvis("Living room light on");

            //should be kitchen
            MainActivity.bluetoothOperation('d'+"1500",btdevice);

        } else if((output.contains("living room ") ||  output.contains("leaving room")) && output.contains("light off")){
            jarvis("Living room light off");

            //should be kitchen
            MainActivity.bluetoothOperation('d'+"0500",btdevice);

        }
        else if(output.contains("kitchen light on")){
            jarvis("kitchen light on");

            //should be kitchen
            MainActivity.bluetoothOperation('d'+"1300",btdevice);

        } else if(output.contains("kitchen light off")){
            jarvis("kitchen light off");

            //should be kitchen
            MainActivity.bluetoothOperation('d'+"0300",btdevice);

        }
        else if(output.contains("what time")){

            Calendar c=Calendar.getInstance();
            int hour=c.get(Calendar.HOUR_OF_DAY);
            int minute=c.get(Calendar.MINUTE);

            jarvis("it is "+hour+"and "+minute+" minutes");
        }
        else if(output.contains("today") || output.contains("date") || output.contains("which day") || output.contains("which week")){

            Calendar c=Calendar.getInstance();
            int day=c.get(Calendar.DAY_OF_MONTH);
            int month=c.get(Calendar.MONTH);
            int year=c.get(Calendar.MONTH);

            jarvis("today is "+day+" of "+month+" "+year);
        }
        else if(output.contains("auto")){

            jarvis("auto setting set");
        }

        else if(output.contains("good bye")){

            jarvis("at your service sir");
        }
        else if(output.contains("close the door")){


        }else if (output.contains("temperature")){

            jarvis(""+MainActivity.temperature);
        }

        else if(output.contains("humidity")){
            jarvis(""+MainActivity.humidity);
        }

        else if(output.contains("battery")){
            jarvis(""+MainActivity.battery);

        }
        else if(output.contains("music on") ||output.contains("turn on music") ||output.contains("play music")){

            music1.start();
        }
        else if(output.contains("screw you")){

           jarvis("screw you nigger");
        }
        else if(output.contains("fuck you")){

            jarvis("fuck you too nigger");
        }
        else if(output.contains("have a boyfriend")){

            jarvis("no,neither do you i suppose");
        }
        else if(output.contains("fan on") || output.contains("fun on")|| output.contains("sound on") || output.contains("come on")|| output.contains("find on")

                || output.contains("fine on") || output.contains("turn on")){

            MainActivity.bluetoothOperation("2",btdevice);

        }
        else if(output.contains("fan off") || output.contains("fun off")|| output.contains("sound off") || output.contains("come off")
                || output.contains("find off") || output.contains("fine off") || output.contains("turn off") ){

            MainActivity.bluetoothOperation("3",btdevice);

        }
        else if(output.contains("turn off") ||output.contains("turn lights off")||output.contains("turn light off") ){

            MainActivity.bluetoothOperation('d'+"0400",btdevice);

            //kitchen
            MainActivity.bluetoothOperation('d'+"0300",btdevice);
            //salon
            MainActivity.bluetoothOperation('d'+"0500",btdevice);
            //gym
            MainActivity.bluetoothOperation('d'+"0600",btdevice);
            //garage
            MainActivity.bluetoothOperation('d'+"0700",btdevice);
        }
        else if(output.contains("turn on") ||output.contains("turn lights on") ||output.contains("turn light on") ){

            MainActivity.bluetoothOperation('d'+"1400",btdevice);

            //kitchen
            MainActivity.bluetoothOperation('d'+"1300",btdevice);
            //salon
            MainActivity.bluetoothOperation('d'+"1500",btdevice);
            //gym
            MainActivity.bluetoothOperation('d'+"1600",btdevice);
            //garage
            MainActivity.bluetoothOperation('d'+"1700",btdevice);
        }


        else if(output.contains("music off") ||output.contains("stop music") ){

            if(music1.isPlaying())music1.pause();
            if(music2.isPlaying())music2.pause();
        }
        else if(output.contains("next")){

            if(music1.isPlaying()){
                music1.pause();
                music2.start();
            }
            else if(music2.isPlaying()){
                music2.pause();
                music1.start();
            }
        }
        else if(output.contains("call")){

            jarvis("Calling ..");
            call(PHONE_NUMBER);
        }
        else if(output.contains("picture") || output.contains("selfie") || output.contains("photo")){
            Log.d("Nazim","photo");
        }else if(output.contains("hello") || output.contains("good morning") || output.contains("hi")){

            jarvis("Hello Sir ! how are you today ? ");
        }else if(output.contains("good night")){

            jarvis("Good night Sir");
        }
        else if(output.contains("and you") || output.contains("how are you ?")|| output.contains("what about you ?")  ){

            jarvis("i'm fine thank you");
        }

        else{
                jarvis("Can you repeat please ?");
        }

    }
    void jarvis(String speech){

        textToSpeech.speak(speech,TextToSpeech.QUEUE_FLUSH,null);
    }
    public void call(String number) {
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" + number));

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        startActivity(callIntent);

    }

    @Override
    protected void onPause() {
        super.onPause();

    }

}
