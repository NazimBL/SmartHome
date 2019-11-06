package com.example.dell.nfcommunication;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import org.w3c.dom.Text;

public class PowerMonitor extends AppCompatActivity {


    public static TextView solar_E,gym_E,light_E,E_Hvac,E_house;
    //we suppose gym generates 0.32 kwh per month
    public static float s_E=0,g_E=32/100,l_E=0;

    public static  char[] codes={'s','l'};
    byte index=0;
    public static double hvac_energy=0,house_energy=0;
    public static final double HVAC_POWER=1.44;
    boolean tag=true,thread_tag=true;
    double E_battery=48;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_power_monitor);
        ini();
        if(MainActivity.connected && thread_tag)monitor();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id=item.getItemId();
        if(id==R.id.forecast){

           myDialog();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.power_menu,menu);
        return true;
    }

    void ini(){

        solar_E=(TextView)findViewById(R.id.solar_panel_text);
        gym_E=(TextView)findViewById(R.id.gym_text);
        light_E=(TextView)findViewById(R.id.energy_light);
        E_Hvac=(TextView)findViewById(R.id.hvac);
        E_house=(TextView)findViewById(R.id.house_hold_text);
        thread_tag=true;
        tag=true;
    }
    void monitor(){
        new Thread(new Runnable() {
            @Override
            public void run() {

                while(true){
                    if(MainActivity.connected){

                       if(MainActivity.read_code!=codes[index]) {

                           MainActivity.read_code = codes[index];
                           MainActivity.bluetoothOperation("" + MainActivity.read_code, MainActivity.btdevice);
                       }
                        if(tag) {

                            ThreadCommunication communication = new ThreadCommunication(MainActivity.socket, "read");
                            communication.start();
                            tag=false;
                        }
                        try {
                            Thread.sleep(100);
                            hvac_energy=hvac_energy+(HVAC_POWER/36000);
                            house_energy+=0.0333;
                            MainActivity.myHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    E_Hvac.setText("HVAC \n \n" + (float)hvac_energy + "kwh");
                                    E_house.setText("House Appliances \n \n "+(float)house_energy+"kwh");
                                }
                            });

                        }catch (Exception e){

                        }
                        E_battery=E_battery+s_E+g_E-l_E/3600-hvac_energy-house_energy;
                        index++;
                        if(index>1)index=0;
                    }else MainActivity.myToast(PowerMonitor.this,"Not Connected");

                }

            }
        }).start();
    }
    @Override
    protected void onPause() {
        super.onPause();
        thread_tag=false;

    }
    float energyCostCalculator(float v){
        float cost;
        if(v>125) {

            //see facture first 125 are paid 1.1 rest is paid 4.1
            cost = (float) ((v- 125) * 4.17 + 125 * 1.77);
        }else cost=(float)(v*1.77);
        return cost;
    }

    void myDialog(){

        //10hours
        //30*10*3600=
        float total_consumed=(float)(l_E+hvac_energy+house_energy)*(3600*10*30);
        //9 hour sunlight , 4horus of sport
        //the 10 we didn't devide in arduino code
        float energy_gain=(s_E*360*9*30)+(g_E*3600*4*30);
        float factured=(float)(total_consumed-E_battery-energy_gain);
        //in dinard
        float paid;
        float gain_fac;

paid=energyCostCalculator(factured);
gain_fac=energyCostCalculator(energy_gain);



        AlertDialog.Builder builder=new AlertDialog.Builder(PowerMonitor.this);
        builder.setTitle("Energy Forecast").setMessage("(Monthly)\nTotal Energy Consumed="+total_consumed+
                                                                    "\nEnergy Gain="+energy_gain+
                                                                     "\nFactured Energy="+factured+
                                                                       "\nTotal Facture="+paid+"DA\n"+
                                                                         "Money Saved="+gain_fac+"DA").setIcon(R.drawable.info)
                .setCancelable(true).setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
builder.show();

    }


    @Override
    public void onBackPressed() {
        Intent i=new Intent(PowerMonitor.this,MainActivity.class);
        i.putExtra("battery",(float)E_battery*100/48);
        startActivity(i);
    }
}
