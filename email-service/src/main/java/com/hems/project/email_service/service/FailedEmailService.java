package com.hems.project.email_service.service;

import com.hems.project.email_service.entity.FailedEmail;

public interface FailedEmailService {
     FailedEmail addFailedInDatabase(FailedEmail failedEmail);
}
