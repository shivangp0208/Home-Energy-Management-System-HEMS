package com.project.hems.hems_api_contracts.contract.email;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttachmentDto {
    private String fileName;      // "aadhar.png"
    private String contentType;   // "image/png"
    private String url;           // signed URL from Supabase
}
