package com.hems.project.Email_Service.service;

import com.hems.project.Email_Service.entity.FailedEmail;

public interface FailedEmailService {
     FailedEmail addFailedInDatabase(FailedEmail failedEmail);
}
