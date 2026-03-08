package com.hems.project.ADMIN_SERVICE.dto;

import com.hems.project.ADMIN_SERVICE.dto.CasePriority;
import com.hems.project.ADMIN_SERVICE.dto.CaseStatus;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record CaseUpdateRequestDto(

        // any field can be nullable = "no change"
        CaseStatus status,
        CasePriority priority,

        // assignment
        String assignedTo,

        // optional note that becomes a CaseEvent (COMMENT_ADDED / STATUS_CHANGED reason)
        @Size(max = 2000)
        String note
) {}