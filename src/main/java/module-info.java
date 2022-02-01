module MQTTDemoServer {
    requires javafx.fxml;
    requires javafx.controls;
    requires javafx.graphics;
    requires javafx.web;
    requires java.logging;
    requires java.desktop;
    requires org.bouncycastle.provider;

    opens com.smartphonedev.mqttserver;
    //opens com.smartphonedev.mqttserver.controller;
    //opens org.bouncycastle.jce.provider;
}
