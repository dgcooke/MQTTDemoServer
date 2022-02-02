package com.smartphonedev.mqttserver;

import com.smartphonedev.mqttserver.notifications.Notification;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Optional;
import java.util.ResourceBundle;

public class MQTTServerController implements Notification, Initializable
{

    @FXML
    private Label statusLabel;
    @FXML
    private TextField serverHostNameText;
    @FXML
    private Label messageToSendLabel;
    @FXML
    private TextField messageToSendText;
    @FXML
    private Button sendMessageButton;

    @FXML
    protected void onConnectButtonClick()
    {
        Optional<String> hostname;
        try
        {
            var ip = InetAddress.getLocalHost();
            hostname = Optional.of(ip.getHostName());
        } catch (UnknownHostException ukh)
        {
            hostname = Optional.empty();
        }

        hostname.ifPresent(connectedHostname -> {
            serverHostNameText.setText(connectedHostname);
        });

        if (MQTTServer.getApplicationInstance().connectToMQTTServer())
        {
            Platform.runLater(() -> {
                sendMessageButton.setVisible(true);
                messageToSendText.setVisible(true);
                messageToSendLabel.setVisible(true);
            });
        }
    }

    @FXML
    protected void onSendMessageButtonClick()
    {
        final String messageToSend = messageToSendText.getText();
        if (messageToSend.length() > 0)
        {
            MQTTServer.getApplicationInstance().publishMessage(messageToSend, false);
        }
    }

    @Override
    public void update(String message)
    {
        Runnable runnable = () -> {
            Platform.runLater(() -> {
                statusLabel.setText(message);
            });
        };

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle)
    {
        MQTTServer.getApplicationInstance().getNotificationObservable().addObserver(this);
    }
}
