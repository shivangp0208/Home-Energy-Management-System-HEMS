package com.project.hems.hems_api_contracts.contract.email;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmailEventDto {
    private String to;
    private String subject;
    private String body;     // plain text or HTML
    private boolean html;

    // Attachments via URLs (Supabase signed/public URL)
    private List<AttachmentDto> attachments;

    // Inline images (for HTML templates)
    private List<InlineImageDto> inlineImages;

    // Optional metadata
    private String eventType;  // DOCUMENT_UPLOADED, SITE_CREATED
    private String correlationId;
}
