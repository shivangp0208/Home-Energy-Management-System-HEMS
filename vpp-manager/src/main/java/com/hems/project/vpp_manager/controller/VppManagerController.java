package com.hems.project.vpp_manager.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/vpp-manager")
public class VppManagerController {


/*
    2) Document Verification APIs
✅ Pending documents

    GET /api/v1/vpp/manager/documents/pending

    returns list: documentId, userId, type, uploadedAt, status

✅ Update document status (your current one)

    PATCH /api/v1/vpp/manager/documents/{documentId}/status

    body: { "status": "APPROVED", "note": "..." }

✅ Document history/audit (very useful)

    GET /api/v1/vpp/manager/documents/{documentId}/history

    who changed, when, oldStatus → newStatus, note

3) Enrollment / Powerhouse APIs (manager operations)
✅ Stats

    GET /api/v1/vpp/manager/enrollments/stats

    total enrolled, today enrolled, pending approvals, etc.

            ✅ List powerhouses / customers in region

    GET /api/v1/vpp/manager/powerhouses

    with paging + search

✅ Block/Unblock (operational control)

    PATCH /api/v1/vpp/manager/powerhouses/{id}/block

    PATCH /api/v1/vpp/manager/powerhouses/{id}/unblock we implement this three..  ok

 */


    @PatchMapping("/block-vpp/{vppId}")
    public ResponseEntity<?> blockVpp(@PathVariable UUID vppId){
        //fetch vpp and set accessStatus to BLOCK

        return null;

    }

    @PatchMapping("/unblock-vpp/{vppId}")
    public ResponseEntity<?> unBlockVpp(@PathVariable UUID vppId){
        //fetch vpp and set accessStatus to ACTIVE

        return null;
    }


}
