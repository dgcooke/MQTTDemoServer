package com.smartphonedev.mqttserver;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Optional;

public class MQTTServerController
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

        if (hostname.isPresent())
        {
            statusLabel.setText("Connected to " + hostname.get());
            serverHostNameText.setText(hostname.get());
        } else
        {
            statusLabel.setText("Connected");
        }

        sendMessageButton.setVisible(true);
        messageToSendText.setVisible(true);
        messageToSendLabel.setVisible(true);
    }

    @FXML
    protected void onSendMessageButtonClick()
    {

    }
}
