package com.campushare.apiLayer.model;

import lombok.Data;
import org.springframework.data.annotation.Id;

@Data
public class NotificationRecord {
    @Id
    private String notificationId;
    private String recipientId;
    private Object notification;
}
