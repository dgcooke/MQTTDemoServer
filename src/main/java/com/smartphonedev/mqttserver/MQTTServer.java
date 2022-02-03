package com.smartphonedev.mqttserver;

import com.smartphonedev.mqttserver.notifications.Agency;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import javax.net.ssl.SSLSocketFactory;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

public class MQTTServer extends Application
{
    private static final int MQTT_QOS = 2;
    static final String MQTT_TOPIIC = "macdonalds/adelaide/hindley/44";
    private MqttAsyncClient mqttAsyncClient = null;
    private Agency notificationObservable = new Agency();

    private static MQTTServer applicationInstance;

    public MQTTServer()
    {
        applicationInstance = this;
    }


    @Override
    public void start(Stage stage) throws IOException
    {
        FXMLLoader fxmlLoader = new FXMLLoader(MQTTServer.class.getResource("/views/MQTTServerController.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 480, 600);
        stage.setTitle("MQTT Server Demo");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args)
    {
        launch();
        System.out.println("\nDamien's MQTT Server Demo");
    }

    protected static MQTTServer getApplicationInstance()
    {
        return applicationInstance;
    }

    protected Agency getNotificationObservable()
    {
        return notificationObservable;
    }

    protected void publishMessage(final String messageToSend, final boolean retained)
    {
        if (mqttAsyncClient == null)
        {
            connectToMQTTServer();
        }

        if (mqttAsyncClient.isConnected())
        {
            try
            {
                mqttAsyncClient.publish(MQTT_TOPIIC, messageToSend.getBytes(), MQTT_QOS, retained, null, new IMqttActionListener()
                {

                    @Override
                    public void onSuccess(IMqttToken iMqttToken)
                    {
                        getNotificationObservable().setInformation("Message sent successfully");
                    }

                    @Override
                    public void onFailure(IMqttToken iMqttToken, Throwable throwable)
                    {
                        getNotificationObservable().setInformation("Message not sent");
                    }
                });
            } catch (MqttPersistenceException pe)
            {
                getNotificationObservable().setInformation("Message not sent: PersistenceException");
            } catch (MqttException me)
            {
                getNotificationObservable().setInformation("Message not sent: " + me.getMessage());
            }

        }

    }

    protected boolean connectToMQTTServer()
    {
        final AtomicBoolean didConnect = new AtomicBoolean(false);
        final AtomicBoolean hasUpdated = new AtomicBoolean(false);
        var resourceReader = new ResourceReader();
        Optional<String> hostname;
        try
        {
            var ip = InetAddress.getLocalHost();
            hostname = Optional.of(ip.getHostName());
        } catch (UnknownHostException ukh)
        {
            hostname = Optional.empty();
        }

        try
        {
            final MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
            mqttConnectOptions.setMqttVersion(MqttConnectOptions.MQTT_VERSION_3_1_1);
            mqttConnectOptions.setKeepAliveInterval(MqttConnectOptions.KEEP_ALIVE_INTERVAL_DEFAULT);
            mqttConnectOptions.setUserName(resourceReader.getUserName());
            mqttConnectOptions.setPassword(resourceReader.getPassword().toCharArray());

            MemoryPersistence memoryPersistence = new MemoryPersistence();

            final String mqttServerHost = resourceReader.getServerHostname();
            final int mqttServerPort = 8883;
            final String mqttServerURI = String.format("ssl://%s:%d", mqttServerHost, mqttServerPort);

            final String mqttClientId = hostname + "-mqtt-server";
            mqttAsyncClient = new MqttAsyncClient(mqttServerURI, mqttClientId, memoryPersistence);


            final String caCertificateFileName = resourceReader.getCACertificateFilePath();
            final String clientCertificateFileName = resourceReader.getClientCertificatePath();
            final String clientKeyFileName = resourceReader.getClientKeyPath();


            SSLSocketFactory socketFactory;
            try {
                socketFactory = SecurityHelper.createSocketFactory(caCertificateFileName, clientCertificateFileName, clientKeyFileName);
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println(String.format("Failed to successfully connect, due to SSL exception"));
                return false;
            }

            mqttConnectOptions.setSocketFactory(socketFactory);


            // In this case, we don't use the token
            IMqttToken mqttConnectToken = mqttAsyncClient.connect(
                    mqttConnectOptions,
                    null,
                    new IMqttActionListener() {
                        @Override
                        public void onSuccess(IMqttToken asyncActionToken)
                        {
                            getNotificationObservable().setInformation("Connected");
                            System.out.println(String.format("Successfully connected"));
                            didConnect.set(true);
                            hasUpdated.set(true);
                        }
                        @Override
                        public void onFailure(IMqttToken asyncActionToken, Throwable exception)
                        {
                            exception.printStackTrace();
                            System.out.println(String.format("Failed to successfully connect"));
                            didConnect.set(false);
                            hasUpdated.set(true);
                        }
                    });

        } catch (MqttException e)
        {
            e.printStackTrace();
        }

        //wait for the connect operation to finish
        while (!hasUpdated.get())
        {
            System.out.println("Attempting to connect....");
            try
            {
                Thread.sleep(250);
            } catch (InterruptedException e)
            {
                //nothing to see here
            }

        }
        return didConnect.get();
    }


}
