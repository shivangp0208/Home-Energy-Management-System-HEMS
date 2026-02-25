package com.hems.project.Virtual_Power_Plant.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class DispatchContext {
    private UUID groupId;
    private UUID siteId;
    private Double power;
    private LocalDateTime scheduleTime;
}