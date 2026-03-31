package com.hems.project.ADMIN_SERVICE.controller;

import com.hems.project.ADMIN_SERVICE.dto.GroupDispatchRequestDto;
import com.hems.project.ADMIN_SERVICE.dto.SiteDispatchRequestDto;
import com.hems.project.ADMIN_SERVICE.external.SiteFeignClientService;
import com.hems.project.ADMIN_SERVICE.service.SiteGroupService;
import com.project.hems.hems_api_contracts.contract.vpp.SiteGroupDto;
import com.project.hems.hems_api_contracts.contract.vpp.SiteGroupReqDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/site-groups")
public class SiteGroupController {

    private final SiteGroupService siteGroupService;
    private final SiteFeignClientService siteFeignClientService;

    // TODO: for fetching the admin detail we have to first create a AdminIdentites
    // to store thier token's sub claim to identify them and then at the time of
    // creating from the token we can know who created this group
    // create site group
    @Operation(
            summary = "create site group",
            description = "create a new site group with optional site inclusion"
    )
    @ApiResponse(responseCode = "200", description = "site group created successfully")
    @PreAuthorize("hasAuthority('site:write')")
    @PostMapping("/create-site-group")
    public ResponseEntity<SiteGroupDto> createSiteGroup(
            @RequestHeader("Authorization") String token,
            @RequestBody @Valid SiteGroupReqDto siteGroup,
            @RequestParam(name = "includeSites", required = false, defaultValue = "false") boolean includeSites) {

        log.info("creating site group");

        try {
            SiteGroupDto response = siteGroupService.createSiteGroup(siteGroup, token);
            log.info("site group created successfully");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("error creating site group: {}", e.getMessage(), e);
            throw e;
        }
    }


    // get site group by id
    @Operation(
            summary = "get site group by id",
            description = "fetch a site group using group id"
    )
    @ApiResponse(responseCode = "200", description = "site group fetched successfully")
    @PreAuthorize("hasAuthority('site:read')")
    @GetMapping("/groups/{groupId}")
    public ResponseEntity<SiteGroupDto> getSiteGroupById(
            @PathVariable UUID groupId,
            @RequestParam(name = "includeSites", required = false, defaultValue = "false") boolean includeSites) {

        log.info("fetching site group with id: {}", groupId);

        try {
            SiteGroupDto response = siteGroupService.getSiteGroupByGroupId(groupId, includeSites);
            log.info("site group fetched successfully for id: {}", groupId);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("error fetching site group for id {}: {}", groupId, e.getMessage(), e);
            throw e;
        }
    }

    // get all site groups
    @Operation(
            summary = "get all site groups",
            description = "fetch all site groups"
    )
    @ApiResponse(responseCode = "200", description = "site groups fetched successfully")
    @PreAuthorize("hasAuthority('site:read')")
    @GetMapping("/groups")
    public ResponseEntity<List<SiteGroupDto>> getAllSiteGroups(
            @RequestParam(name = "includeSites", required = false, defaultValue = "false") boolean includeSites) {

        log.info("fetching all site groups");

        try {
            List<SiteGroupDto> response = siteGroupService.getAllSiteGroups(includeSites);
            log.info("site groups fetched successfully");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("error fetching all site groups: {}", e.getMessage(), e);
            throw e;
        }
    }

    // update site group
    @Operation(
            summary = "update site group",
            description = "update an existing site group"
    )
    @ApiResponse(responseCode = "200", description = "site group updated successfully")
    @PreAuthorize("hasAuthority('site:write')")
    @PutMapping("/groups/{groupId}")
    public ResponseEntity<SiteGroupDto> updateSiteGroup(
            @PathVariable UUID groupId,
            @RequestBody @Valid SiteGroupReqDto siteGroupDto,
            @RequestParam(name = "includeSites", required = false, defaultValue = "false") boolean includeSites) {

        log.info("updating site group with id: {}", groupId);

        try {
            SiteGroupDto response = siteGroupService.updateSiteGroup(groupId, siteGroupDto, includeSites);
            log.info("site group updated successfully for id: {}", groupId);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("error updating site group for id {}: {}", groupId, e.getMessage(), e);
            throw e;
        }
    }

    // delete site group
    @Operation(
            summary = "delete site group",
            description = "delete a site group"
    )
    @ApiResponse(responseCode = "204", description = "site group deleted successfully")
    @PreAuthorize("hasAuthority('site:write')")
    @DeleteMapping("/groups/{groupId}")
    public ResponseEntity<Void> deleteSiteGroup(@PathVariable UUID groupId) {

        log.info("deleting site group with id: {}", groupId);

        try {
            siteGroupService.deleteSiteGroup(groupId);
            log.info("site group deleted successfully for id: {}", groupId);
            return ResponseEntity.noContent().build();

        } catch (Exception e) {
            log.error("error deleting site group for id {}: {}", groupId, e.getMessage(), e);
            throw e;
        }
    }

    // deactivate site group
    @Operation(
            summary = "deactivate site group",
            description = "deactivate a site group"
    )
    @ApiResponse(responseCode = "204", description = "site group deactivated successfully")
    @PreAuthorize("hasAuthority('site:write')")
    @PatchMapping("/groups/{groupId}/deactivate")
    public ResponseEntity<Void> deactivateSiteGroup(@PathVariable UUID groupId) {

        log.info("deactivating site group with id: {}", groupId);

        try {
            siteGroupService.deactivateSiteGroup(groupId);
            log.info("site group deactivated successfully for id: {}", groupId);
            return ResponseEntity.noContent().build();

        } catch (Exception e) {
            log.error("error deactivating site group for id {}: {}", groupId, e.getMessage(), e);
            throw e;
        }
    }

    // activate site group
    @Operation(
            summary = "activate site group",
            description = "activate a site group"
    )
    @ApiResponse(responseCode = "204", description = "site group activated successfully")
    @PreAuthorize("hasAuthority('site:write')")
    @PatchMapping("/groups/{groupId}/activate")
    public ResponseEntity<Void> activateSiteGroup(@PathVariable UUID groupId) {

        log.info("activating site group with id: {}", groupId);

        try {
            siteGroupService.activateSiteGroup(groupId);
            log.info("site group activated successfully for id: {}", groupId);
            return ResponseEntity.noContent().build();

        } catch (Exception e) {
            log.error("error activating site group for id {}: {}", groupId, e.getMessage(), e);
            throw e;
        }
    }

    // controller:- dispatch command send by admin so admin select group and then send command
    //and in this only we implement the quartz system to scedule that event so we take sceduled time..
//    @PostMapping("/dispatch-power-group")
//    public ResponseEntity<? extends Object> dispatchPowerFromGroup(){
//        //todo:-
//        //normal we send here string then change to DispatchEvent...
//
//        return null;
//
//    }

    @Operation(
            summary = "dispatch power to group",
            description = "schedule power dispatch for a site group"
    )
    @ApiResponse(responseCode = "200", description = "dispatch scheduled successfully")
    @PreAuthorize("hasAuthority('admin:write') or hasAuthority('vppm:write')")
    @PostMapping("/dispatch-power-group")
    public ResponseEntity<String> dispatchPowerFromGroup(
            @RequestHeader("Authorization") String token,
            @RequestBody @Valid GroupDispatchRequestDto request) {

        log.info("dispatching power for group");

        try {
            siteGroupService.handleGroupDispatch(request, token);
            log.info("group dispatch scheduled successfully");
            return ResponseEntity.ok("dispatch scheduled successfully");

        } catch (Exception e) {
            log.error("error dispatching power for group: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Operation(
            summary = "dispatch power to site",
            description = "schedule power dispatch for a specific site"
    )
    @ApiResponse(responseCode = "200", description = "dispatch scheduled successfully")
    @PreAuthorize("hasAuthority('admin:write') or hasAuthority('vppm:write')")
    @PostMapping("/dispatch-power-site")
    public ResponseEntity<String> dispatchPowerFromSite(
            @RequestHeader("Authorization") String token,
            @RequestBody @Valid SiteDispatchRequestDto request) {

        log.info("dispatching power for site");

        try {
            siteGroupService.handleSiteDispatch(request, token);
            log.info("site dispatch scheduled successfully");
            return ResponseEntity.ok("dispatch scheduled successfully");

        } catch (Exception e) {
            log.error("error dispatching power for site: {}", e.getMessage(), e);
            throw e;
        }
    }





    // controller: dispatch command send by admin only for fetching power from one site / not from whole group
    //and in this only we implement the quartz system to scedule that event so we take sceduled time..

}
