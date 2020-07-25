package vn.vais.demopubsub;

import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;
import android.widget.Toast;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.io.Console;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        String clientId = MqttClient.generateClientId();
        final MqttAndroidClient client = new MqttAndroidClient(getApplicationContext(), "tcp://10.1.8.113:1883", clientId);

        try {
            client.connect().setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Toast.makeText(getApplicationContext(), "Connect successfully", Toast.LENGTH_SHORT).show();
                    subscribeTopic("motor", client);
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Toast.makeText(getApplicationContext(), "Connect fail", Toast.LENGTH_SHORT).show();
                    Log.d("MQTT", exception.toString());
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
            Log.d("MQTT", e.toString());
        }


        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
                MqttMessage mqttMessage = new MqttMessage("message for testPubSub".getBytes());
                try {
                    client.publish("motor", mqttMessage);
                } catch (MqttException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    private void subscribeTopic(String topic, MqttAndroidClient client){
//        try {
//            client.subscribe("motor", 0,new IMqttMessageListener() {
//                @Override
//                public void messageArrived(String topic, MqttMessage message) throws Exception {
//                    Toast.makeText(getApplicationContext(), "MQTT " + topic + ": " + message, Toast.LENGTH_SHORT).show();
//                    Log.d("MQTT", "MQTT " + topic + ": " + message);
//                }
//            });
//        } catch (MqttException e) {
//            e.printStackTrace();
//            Log.d("MQTT", e.toString());
//        }
        try {
            client.subscribe("motor", 0);
            client.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {
                }

                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    Log.d("MQTT","message>>" + new String(message.getPayload()));
                    Log.d("MQTT","topic>>" + topic);
//                    parseMqttMessage(new String(message.getPayload()));

                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {

                }
            });
        } catch (Exception e){
            e.printStackTrace();
        }

    }
}