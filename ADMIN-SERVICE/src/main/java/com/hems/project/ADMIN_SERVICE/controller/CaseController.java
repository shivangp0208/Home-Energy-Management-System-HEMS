package com.hems.project.ADMIN_SERVICE.controller;

import com.hems.project.ADMIN_SERVICE.dto.*;
import com.hems.project.ADMIN_SERVICE.entity.CaseEvent;
import com.hems.project.ADMIN_SERVICE.service.CaseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/admin/case")
public class CaseController {

    private final CaseService caseService;

    //raise ticket and when ticket is created
    //1.automatic using site/vpp heartbeat here we use grpc.
    //2. manual by using controller..
    @PostMapping("/create-case-ticket/manual")
    public ResponseEntity<?> createManualCase(@RequestBody CaseRaisedEventDto caseRaisedEventDto){
         CaseCreatedResponseDto caseManully = caseService.createCaseManully(caseRaisedEventDto);
        return new ResponseEntity<>(caseManully, HttpStatus.OK);
    }

    //current check case status
    @GetMapping("/{caseId}/status")
    public ResponseEntity<CaseStatus> getCaseStatus(@PathVariable UUID caseId) {
        return ResponseEntity.ok(caseService.getCaseStatus(caseId));
    }


    //check all ticket status
    @GetMapping("/all")
    public ResponseEntity<List<CaseCreatedResponseDto>> getAllCases(
            @RequestParam(required = false) CaseStatus status,
            @RequestParam(required = false) UUID siteId,
            @RequestParam(required = false) UUID vppId
    ) {
        return ResponseEntity.ok(caseService.getAllCases(status, siteId, vppId));
    }

    // check particular ticket timeline (status travel)
    @GetMapping("/{caseId}/timeline")
    public ResponseEntity<List<CaseEvent>> getCaseTimeline(@PathVariable UUID caseId) {
        return ResponseEntity.ok(caseService.getCaseTimeline(caseId));
    }
    //mark ticket status
    @PatchMapping("/{caseId}/status")
    public ResponseEntity<CaseCreatedResponseDto> updateStatus(
            @PathVariable UUID caseId,
            @RequestBody UpdateCaseStatusRequest request
    ) {
        return ResponseEntity.ok(caseService.updateCaseStatus(caseId, request));
    }

    //add comment in ticket by admin.
    @PostMapping("/{caseId}/comment")
    public ResponseEntity<String> addComment(
            @PathVariable UUID caseId,
            @RequestBody AddCommentRequestDto request
    ) {
        caseService.addComment(caseId, request);
        return ResponseEntity.ok("comment added successfully");
    }

    //transfer ticket to one to another admin
    @PatchMapping("/{caseId}/transfer")
    public ResponseEntity<CaseCreatedResponseDto> transferCase(
            @PathVariable UUID caseId,
            @RequestBody TransferCaseRequestDto request
    ) {
        return ResponseEntity.ok(caseService.transferCase(caseId, request));
    }

    @PatchMapping("/{caseId}/reopen")
    public ResponseEntity<CaseCreatedResponseDto> reopen(
            @PathVariable UUID caseId,
            @RequestParam(required = false) String actor,
            @RequestParam(required = false) String reason
    ) {
        return ResponseEntity.ok(caseService.reopenCase(caseId, actor, reason));
    }

    @PatchMapping("/{caseId}/close")
    public ResponseEntity<CaseCreatedResponseDto> close(
            @PathVariable UUID caseId,
            @Valid @RequestBody CloseCaseRequestDto req
    ) {
        return ResponseEntity.ok(caseService.closeCase(caseId, req));
    }
    @GetMapping("/all-with-pagging")
    public ResponseEntity<Page<CaseCreatedResponseDto>> list(
            @RequestParam(required = false) CaseStatus status,
            @RequestParam(required = false) CasePriority priority,
            @RequestParam(required = false) UUID siteId,
            @RequestParam(required = false) UUID vppId,
            @RequestParam(required = false) String assignedTo,
            Pageable pageable
    ) {
        return ResponseEntity.ok(caseService.listCases(status, priority, siteId, vppId, assignedTo, pageable));
    }


//    @GetMapping("/search")
//    public ResponseEntity<Page<CaseCreatedResponseDto>> search(
//            @RequestParam(required = false) String caseNumber,
//            @RequestParam(required = false) String title,
//            @RequestParam(required = false) String assignedTo,
//            @RequestParam(required = false) UUID siteId,
//            @RequestParam(required = false)
//            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
//            @RequestParam(required = false)
//            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
//            Pageable pageable
//    ) {
//        return ResponseEntity.ok(caseService.search(caseNumber, title, assignedTo, siteId, from, to, pageable));
//    }

    @GetMapping("/my-cases")
    public ResponseEntity<Page<CaseCreatedResponseDto>> myCases(
            @RequestParam(required = false) String assignedTo,
            Pageable pageable
    ) {
        return ResponseEntity.ok(caseService.myCases(assignedTo, pageable));
    }

    @GetMapping("/dashboard-summary")
    public ResponseEntity<CaseDashboardSummaryDto> dashboard() {
        return ResponseEntity.ok(caseService.dashboardSummary());
    }

}
