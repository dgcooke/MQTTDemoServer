package com.smartphonedev.mqttserver.notifications;

public class StatusNotification implements Notification
{
    private String notificationString;

    @Override
    public void update(String information)
    {
        this.notificationString = information;
    }
}
