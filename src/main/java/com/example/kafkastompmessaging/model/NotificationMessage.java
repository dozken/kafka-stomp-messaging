package com.example.kafkastompmessaging.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class NotificationMessage implements Serializable {

    public static final String TOPIC = "notification";

    private String orderNumber;
    private String[] recipients;
    private String message;

}
