package com.hems.project.email_service.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@ConfigurationProperties(prefix = "hems.admin")
@Getter
@Setter
public class AdminNotificationProps {
    private List<String> deviceTokens = new ArrayList<>();
}