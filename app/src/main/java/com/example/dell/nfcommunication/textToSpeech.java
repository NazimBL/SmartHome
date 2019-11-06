package com.example.dell.nfcommunication;

import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.speech.tts.TextToSpeech;
import android.widget.EditText;

import java.util.Locale;

public class textToSpeech extends AppCompatActivity {

    Button b;
    TextToSpeech textToSpeech;
    EditText editText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_to_speech);
        init();
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textToSpeech.speak(editText.getText().toString(),TextToSpeech.QUEUE_FLUSH,null,null);
            }
        });


    }
    void init(){
        editText=(EditText)findViewById(R.id.edit);
        b=(Button)findViewById(R.id.ok);
        textToSpeech=new TextToSpeech(textToSpeech.this, new android.speech.tts.TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != android.speech.tts.TextToSpeech.ERROR) {

                    textToSpeech.setLanguage(Locale.ENGLISH);
                }
            }
        });
    }
}
/*
*
new Thread(new Runnable() {
    @Override
    public void run() {

        BufferedReader in;
        String serverIp="192.168.8.112";

        try{

            Socket socket=new Socket(serverIp,9999);
            OutputStream out = socket.getOutputStream();
            PrintWriter output = new PrintWriter(out);

//            String val="21";
//            byte[] buff=val.getBytes();
//            String value=new String(buff,"UTF-8");
            output.write(21);

           // out.flush();
           // out.close();


        }catch(Exception e){

            Log.d("Nazim",e.toString());
        }
    }
}).start();
* */