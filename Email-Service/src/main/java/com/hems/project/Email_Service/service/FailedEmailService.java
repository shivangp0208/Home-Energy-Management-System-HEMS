package com.hems.project.Email_Service.service;

import com.hems.project.Email_Service.entity.FailedEmail;
import com.hems.project.Email_Service.exception.FailedEmailSaveException;
import com.hems.project.Email_Service.repository.FailedEmailRepo;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class FailedEmailService {

    private final FailedEmailRepo failedEmailRepo;

    public FailedEmail addFailedInDatabase(FailedEmail failedEmail){

        FailedEmail save;
        try {
            save = failedEmailRepo.save(failedEmail);
        } catch (Exception e) {
            throw new FailedEmailSaveException("failed to save FailedEmail into database", e);
        }
        return save;
    }
}
