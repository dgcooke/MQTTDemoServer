package com.smartphonedev.mqttserver;

import com.smartphonedev.mqttserver.notifications.Notification;
import com.smartphonedev.protobuf.PunchIn;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import net.jcip.annotations.NotThreadSafe;

import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;
import java.util.ResourceBundle;

@NotThreadSafe
public class MQTTServerController implements Notification, Initializable
{
    private static final String PUNCH_IN_TOPIC = "macdonalds/adelaide/hindley/44/punchin";

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
    private Button connectButton;

    @FXML
    private Button sendBinaryMessageButton;

    @FXML
    protected void onSendBinaryMessageButtonClick()
    {
        var utcTime = LocalDateTime.now().atZone(ZoneId.systemDefault()).withZoneSameInstant(ZoneId.of("UTC"));

        var punchin = PunchIn.newBuilder()
                .setFirstName("Damien")
                .setLastName("Cooke")
                .setStaffID("a2801efe-b95b-476d-ab41-cf0c94282adc")
                .setStoreID("6cf15568-debd-4ac9-a3c1-a674714f89fe")
                .setUtcTimestamp(utcTime.toLocalDateTime().toString()).build();
        if (punchin.isInitialized())
        {
            MQTTServer.getApplicationInstance().publishBinaryMessage(punchin.toByteArray(), true, PUNCH_IN_TOPIC);
        }
    }

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
                connectButton.setVisible(false);
            });
        } else
        {
            System.out.println("connectToMQTTServer failed");
        }

    }

    @FXML
    protected void onSendMessageButtonClick()
    {
        final String messageToSend = messageToSendText.getText();
        if (messageToSend.length() > 0)
        {
            MQTTServer.getApplicationInstance().publishStringMessage(messageToSend, true);
        }
    }

    @FXML
    public void exitApplication(ActionEvent event) {
        MQTTServer.getApplicationInstance().closeMQTTConnection();
    }

    @Override
    public void update(String message)
    {
        Platform.runLater(() -> {
            statusLabel.setText(message);
        });
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle)
    {
        MQTTServer.getApplicationInstance().getNotificationObservable().addObserver(this);
    }
}
