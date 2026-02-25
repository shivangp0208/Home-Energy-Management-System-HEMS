package com.hems.project.Email_Service.service;

public interface FcmSender {
    public void send(String deviceToken, String title, String body);
}
