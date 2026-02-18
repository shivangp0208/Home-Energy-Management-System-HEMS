package com.project.hems.SiteManagerService.util;

import com.project.hems.hems_api_contracts.contract.email.MailSuccessfullRequestDto;

public final class EmailTemplateUtil {

    private EmailTemplateUtil() {
        //koi pan aa class no obj na banave etle constructor ne private kari dedhu che
    }

    public static MailSuccessfullRequestDto buildSiteCreatedMail(
            String to,
            String siteId,
            String userSub
    ) {

        String subject = String.format(
                "[HEMS] Site Created Successfully | Site ID: %s",
                siteId
        );

        String body = String.format("""
                Hello,

                Your site has been created successfully in the HEMS system.

                ───────────────────────────────
                Site Details:
                Site ID      : %s
                Created By   : %s
                Status       : Under Review
                ───────────────────────────────

                Our team is currently reviewing your site details.
                You will receive another notification once the verification process is completed.

                If you have any questions, please contact our support team.

                Thank you,
                HEMS Support Team
                hems07293@gmail.com
                """,
                siteId,
                userSub
        );

        return MailSuccessfullRequestDto.builder()
                .to(to)
                .subject(subject)
                .body(body)
                .build();
    }
}
