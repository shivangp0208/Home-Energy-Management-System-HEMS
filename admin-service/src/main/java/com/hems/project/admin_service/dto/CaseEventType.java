package com.hems.project.admin_service.dto;

public enum CaseEventType {
    CREATED,
    STATUS_CHANGED,
    PRIORITY_CHANGED,
    ASSIGNED,
    COMMENT_ADDED,
    AUTO_ESCALATED,
    RESOLVED,
    CLOSED
}