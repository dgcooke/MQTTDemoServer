package com.smartphonedev.mqttserver;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MQTTServer extends Application
{
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


}
