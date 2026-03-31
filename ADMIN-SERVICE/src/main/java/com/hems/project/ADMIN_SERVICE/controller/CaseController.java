package com.hems.project.ADMIN_SERVICE.controller;

import com.hems.ExcelModule.service.ExcelExportService;
import com.hems.project.ADMIN_SERVICE.dto.*;
import com.hems.project.ADMIN_SERVICE.entity.CaseEvent;
import com.hems.project.ADMIN_SERVICE.service.CaseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/admin/case")
public class CaseController {

    private final CaseService caseService;
    private final ExcelExportService excelExportService;

    //raise ticket and when ticket is created
    //1.automatic using site/vpp heartbeat here we use grpc.
    //2. manual by using controller..
    @Operation(
            summary = "Create manual case",
            description = "Creates a new case manually by admin"
    )
    @ApiResponse(responseCode = "200", description = "Case created successfully")
    @PreAuthorize("hasAuthority('admin:write')")
    @PostMapping("/create-case-ticket/manual")
    public ResponseEntity<CaseCreatedResponseDto> createManualCase(
            @RequestBody CaseRaisedEventDto caseRaisedEventDto) {

        log.info("Received request to create manual case: {}", caseRaisedEventDto);

        try {
            CaseCreatedResponseDto response = caseService.createCaseManully(caseRaisedEventDto);
            log.info("Manual case created successfully with id: {}", response.getId());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error while creating manual case: {}", e.getMessage(), e);
            throw e;
        }
    }

    //current check case status
    @Operation(
            summary = "Get case status",
            description = "Fetch current status of a case using caseId"
    )
    @ApiResponse(responseCode = "200", description = "Case status fetched successfully")
    @PreAuthorize("hasAuthority('admin:read')")
    @GetMapping("/{caseId}/status")
    public ResponseEntity<CaseStatus> getCaseStatus(@PathVariable UUID caseId) {

        log.info("Fetching status for caseId: {}", caseId);

        try {
            CaseStatus status = caseService.getCaseStatus(caseId);
            log.info("Status fetched for caseId: {}", caseId);
            return ResponseEntity.ok(status);

        } catch (Exception e) {
            log.error("Error fetching case status for caseId {}: {}", caseId, e.getMessage(), e);
            throw e;
        }
    }


    //check all ticket status
    @Operation(
            summary = "Get all cases",
            description = "Fetch all cases with optional filters"
    )
    @ApiResponse(responseCode = "200", description = "Cases fetched successfully")
    @PreAuthorize("hasAuthority('admin:read')")
    @GetMapping("/all")
    public ResponseEntity<List<CaseCreatedResponseDto>> getAllCases(
            @RequestParam(required = false) CaseStatus status,
            @RequestParam(required = false) UUID siteId,
            @RequestParam(required = false) UUID vppId
    ) {
        log.info("Fetching all cases with filters status={}, siteId={}, vppId={}", status, siteId, vppId);
        return ResponseEntity.ok(caseService.getAllCases(status, siteId, vppId));
    }

    // check particular ticket timeline (status travel)
    @Operation(
            summary = "get case timeline",
            description = "fetch complete timeline of a case including all status changes"
    )
    @ApiResponse(responseCode = "200", description = "case timeline fetched successfully")
    @PreAuthorize("hasAuthority('admin:read')")
    @GetMapping("/{caseId}/timeline")
    public ResponseEntity<List<CaseEvent>> getCaseTimeline(@PathVariable UUID caseId) {

        log.info("fetching timeline for case id: {}", caseId);

        try {
            List<CaseEvent> timeline = caseService.getCaseTimeline(caseId);
            log.info("timeline fetched successfully for case id: {}", caseId);
            return ResponseEntity.ok(timeline);

        } catch (Exception e) {
            log.error("error fetching timeline for case id {}: {}", caseId, e.getMessage(), e);
            throw e;
        }
    }


    //mark ticket status
    // mark ticket status
    @Operation(
            summary = "update case status",
            description = "update the status of a case"
    )
    @ApiResponse(responseCode = "200", description = "case status updated successfully")
    @PreAuthorize("hasAuthority('admin:write')")
    @PatchMapping("/{caseId}/status")
    public ResponseEntity<CaseCreatedResponseDto> updateStatus(
            @PathVariable UUID caseId,
            @RequestBody UpdateCaseStatusRequest request
    ) {

        log.info("updating case status for case id: {}", caseId);

        try {
            CaseCreatedResponseDto response = caseService.updateCaseStatus(caseId, request);
            log.info("case status updated successfully for case id: {}", caseId);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("error updating case status for case id {}: {}", caseId, e.getMessage(), e);
            throw e;
        }
    }


    // add comment in ticket by admin
    @Operation(
            summary = "add comment to case",
            description = "add a comment to a specific case"
    )
    @ApiResponse(responseCode = "200", description = "comment added successfully")
    @PreAuthorize("hasAuthority('admin:write')")
    @PostMapping("/{caseId}/comment")
    public ResponseEntity<String> addComment(
            @PathVariable UUID caseId,
            @RequestBody AddCommentRequestDto request
    ) {

        log.info("adding comment for case id: {}", caseId);

        try {
            caseService.addComment(caseId, request);
            log.info("comment added successfully for case id: {}", caseId);
            return ResponseEntity.ok("comment added successfully");

        } catch (Exception e) {
            log.error("error adding comment for case id {}: {}", caseId, e.getMessage(), e);
            throw e;
        }
    }

    //transfer ticket to one to another admin
    @Operation(
            summary = "transfer case",
            description = "transfer case from one admin to another"
    )
    @ApiResponse(responseCode = "200", description = "case transferred successfully")
    @PreAuthorize("hasAuthority('admin:write')")
    @PatchMapping("/{caseId}/transfer")
    public ResponseEntity<CaseCreatedResponseDto> transferCase(
            @PathVariable UUID caseId,
            @RequestBody TransferCaseRequestDto request
    ) {

        log.info("transferring case id: {}", caseId);

        try {
            CaseCreatedResponseDto response = caseService.transferCase(caseId, request);
            log.info("case transferred successfully for case id: {}", caseId);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("error transferring case id {}: {}", caseId, e.getMessage(), e);
            throw e;
        }
    }


    @Operation(
            summary = "reopen case",
            description = "reopen a closed case"
    )
    @ApiResponse(responseCode = "200", description = "case reopened successfully")
    @PreAuthorize("hasAuthority('admin:write')")
    @PatchMapping("/{caseId}/reopen")
    public ResponseEntity<CaseCreatedResponseDto> reopen(
            @PathVariable UUID caseId,
            @RequestParam(required = false) String actor,
            @RequestParam(required = false) String reason
    ) {

        log.info("reopening case id: {}", caseId);

        try {
            CaseCreatedResponseDto response = caseService.reopenCase(caseId, actor, reason);
            log.info("case reopened successfully for case id: {}", caseId);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("error reopening case id {}: {}", caseId, e.getMessage(), e);
            throw e;
        }
    }

    @Operation(
            summary = "close case",
            description = "close a case with provided details"
    )
    @ApiResponse(responseCode = "200", description = "case closed successfully")
    @PreAuthorize("hasAuthority('admin:write')")
    @PatchMapping("/{caseId}/close")
    public ResponseEntity<CaseCreatedResponseDto> close(
            @PathVariable UUID caseId,
            @Valid @RequestBody CloseCaseRequestDto req
    ) {

        log.info("closing case id: {}", caseId);

        try {
            CaseCreatedResponseDto response = caseService.closeCase(caseId, req);
            log.info("case closed successfully for case id: {}", caseId);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("error closing case id {}: {}", caseId, e.getMessage(), e);
            throw e;
        }
    }

    @Operation(
            summary = "get cases with pagination",
            description = "fetch cases with filters and pagination"
    )
    @ApiResponse(responseCode = "200", description = "cases fetched successfully")
    @PreAuthorize("hasAuthority('admin:read')")
    @GetMapping("/all-with-pagination")
    public ResponseEntity<Page<CaseCreatedResponseDto>> list(
            @RequestParam(required = false) CaseStatus status,
            @RequestParam(required = false) CasePriority priority,
            @RequestParam(required = false) UUID siteId,
            @RequestParam(required = false) UUID vppId,
            @RequestParam(required = false) String assignedTo,
            Pageable pageable
    ) {

        log.info("fetching paginated cases");

        try {
            Page<CaseCreatedResponseDto> response =
                    caseService.listCases(status, priority, siteId, vppId, assignedTo, pageable);

            log.info("paginated cases fetched successfully");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("error fetching paginated cases: {}", e.getMessage(), e);
            throw e;
        }
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

    @Operation(
            summary = "get my cases",
            description = "fetch cases assigned to a specific user"
    )
    @ApiResponse(responseCode = "200", description = "my cases fetched successfully")
    @PreAuthorize("hasAuthority('admin:read')")
    @GetMapping("/my-cases")
    public ResponseEntity<Page<CaseCreatedResponseDto>> myCases(
            @RequestParam(required = false) String assignedTo,
            Pageable pageable
    ) {

        log.info("fetching my cases for user: {}", assignedTo);

        try {
            Page<CaseCreatedResponseDto> response = caseService.myCases(assignedTo, pageable);
            log.info("my cases fetched successfully");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("error fetching my cases: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Operation(
            summary = "get dashboard summary",
            description = "fetch dashboard summary of cases"
    )
    @ApiResponse(responseCode = "200", description = "dashboard summary fetched successfully")
    @PreAuthorize("hasAuthority('admin:read')")
    @GetMapping("/dashboard-summary")
    public ResponseEntity<CaseDashboardSummaryDto> dashboard() {

        log.info("fetching dashboard summary");

        try {
            CaseDashboardSummaryDto response = caseService.dashboardSummary();
            log.info("dashboard summary fetched successfully");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("error fetching dashboard summary: {}", e.getMessage(), e);
            throw e;
        }
    }

    @PreAuthorize("hasAuthority('admin:read')")
    @GetMapping("/export-cases")
    public ResponseEntity<byte[]> exportCases() {

        try {
            List<CaseCreatedResponseDto> cases =
                    caseService.getAllCases(null, null, null);

            if (cases == null || cases.isEmpty()) {
                return ResponseEntity.noContent().build();
            }

            List<Map<String, Object>> data = cases.stream()
                    .map(c -> {
                        Map<String, Object> map = new HashMap<>();

                        map.put("Case ID",
                                c.getId() != null ? c.getId().toString() : "");

                        map.put("Status",
                                c.getStatus() != null ? c.getStatus().toString() : "");

                        map.put("Priority",
                                c.getPriority() != null ? c.getPriority().toString() : "");

                        return map;
                    })
                    .toList();

            log.info("CASE DATA SIZE: " + data.size());

            byte[] excel = excelExportService.export("Cases", data);

            if (excel == null || excel.length == 0) {
                throw new RuntimeException("Excel generation failed");
            }

            return ResponseEntity.ok()
                    .header("Content-Disposition", "attachment; filename=cases.xlsx")
                    .header("Content-Type", "application/octet-stream")
                    .body(excel);

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error exporting cases: " + e.getMessage());
        }
    }

    @PreAuthorize("hasAuthority('admin:access')")
    @GetMapping("/test/api1")
    public ResponseEntity<String> api1() {
        return ResponseEntity.ok("API 1 response");
    }

    @PreAuthorize("hasAuthority('admin:access')")
    @GetMapping("/test/api2")
    public ResponseEntity<String> api2() {
        return ResponseEntity.ok("API 2 response");
    }
}
