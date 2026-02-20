package com.project.hems.hems_api_contracts.contract.email;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InlineImageDto {
    private String contentId;     // "logo" or "doc1"
    private String contentType;   // "image/png"
    private String url;           // signed URL
}
