//package com.hems.project.email_service.service.impl;
//
//import com.google.firebase.messaging.FirebaseMessaging;
//import com.google.firebase.messaging.FirebaseMessagingException;
//import com.google.firebase.messaging.Message;
//import com.google.firebase.messaging.Notification;
//import com.hems.project.email_service.exception.FcmNotificationException;
//import com.hems.project.email_service.service.FcmSender;
//import org.springframework.stereotype.Service;
//
//@Service
//public class FcmSenderImpl implements FcmSender {
//
//    public void send(String deviceToken, String title, String body) {
//        Message message = Message.builder()
//                .setToken(deviceToken)
//                .setNotification(Notification.builder()
//                        .setTitle(title)
//                        .setBody(body)
//                        .build())
//                .build();
//
//        try {
//            String response = FirebaseMessaging.getInstance().send(message);
//            System.out.println("message sent successfully: " + response);
//        } catch (FirebaseMessagingException e) {
//            throw new FcmNotificationException("failed to send FCM notification to token: " + deviceToken, e);
//        }
//    }
//}