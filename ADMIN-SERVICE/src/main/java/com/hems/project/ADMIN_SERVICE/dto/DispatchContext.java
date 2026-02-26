package com.hems.project.ADMIN_SERVICE.dto;

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