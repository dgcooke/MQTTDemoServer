package com.smartphonedev.mqttserver.notifications;

import java.util.ArrayList;
import java.util.List;

public class Agency
{
    private String information;
    private List<Notification> notificationList = new ArrayList<>();

    public void addObserver(Notification notification)
    {
        this.notificationList.add(notification);
    }

    public void removeObserver(Notification notification)
    {
        this.notificationList.remove(notification);
    }

    public void setInformation(String notificationPayload)
    {
        this.information = notificationPayload;
        notificationList.forEach(notification -> {
            notification.update(this.information);
        });
    }
}
