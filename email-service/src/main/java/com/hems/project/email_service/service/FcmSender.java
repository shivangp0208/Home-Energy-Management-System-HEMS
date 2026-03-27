package com.hems.project.email_service.service;

public interface FcmSender {
    public void send(String deviceToken, String title, String body);
}
