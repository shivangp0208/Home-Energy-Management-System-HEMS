package com.hems.project.admin_service.controller;

import com.hems.project.admin_service.dto.GroupDispatchRequestDto;
import com.hems.project.admin_service.dto.SiteDispatchRequestDto;
import com.hems.project.admin_service.external.SiteFeignClientService;
import com.hems.project.admin_service.service.SiteGroupService;
import com.project.hems.hems_api_contracts.contract.vpp.SiteGroupDto;
import com.project.hems.hems_api_contracts.contract.vpp.SiteGroupReqDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    @PostMapping("/create-site-group")
    public SiteGroupDto createSiteGroup(
            @RequestHeader("Authorization") String token,
            @RequestBody @Valid SiteGroupReqDto siteGroup,
            @RequestParam(name = "includeSites", required = false, defaultValue = "false") boolean includeSites) {
        log.info("POST req to create a site group ");
        return siteGroupService.createSiteGroup(siteGroup,token);
    }

    @GetMapping("groups/{groupId}")
    public SiteGroupDto getSiteGroupById(
            @PathVariable(name = "groupId", required = true) UUID groupId,
            @RequestParam(name = "includeSites", required = false, defaultValue = "false") boolean includeSites) {
        log.info("GET request to fetch site group with id {}", groupId);
        return siteGroupService.getSiteGroupByGroupId(groupId, includeSites);
    }

    @GetMapping("/groups")
    public List<SiteGroupDto> getAllSiteGroups(
            @RequestParam(name = "includeSites", required = false, defaultValue = "false") boolean includeSites) {
        log.info("GET request to fetch all site groups");
        return siteGroupService.getAllSiteGroups(includeSites);
    }

    @PutMapping("groups/{groupId}")
    @ResponseStatus(HttpStatus.OK)
    public SiteGroupDto updateSiteGroup(
            @PathVariable(name = "groupId", required = true) UUID groupId,
            @RequestBody @Valid SiteGroupReqDto siteGroupDto,
            @RequestParam(name = "includeSites", required = false, defaultValue = "false") boolean includeSites) {

        log.info("PUT request to update site group with id {}", groupId);
        return siteGroupService.updateSiteGroup(groupId, siteGroupDto, includeSites);
    }

    @DeleteMapping("groups/{groupId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteSiteGroup(@PathVariable(name = "groupId", required = true) UUID groupId) {
        log.info("DELETE request for site group with id {}", groupId);
        siteGroupService.deleteSiteGroup(groupId);
    }

    @PatchMapping("groups/{groupId}/deactivate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deactivateSiteGroup(@PathVariable(name = "groupId", required = true) UUID groupId) {
        log.info("PATCH request to deactivate site group with id {}", groupId);
        siteGroupService.deactivateSiteGroup(groupId);
    }

    @PatchMapping("groups/{groupId}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activateSiteGroup(@PathVariable(name = "groupId", required = true) UUID groupId) {
        log.info("PATCH request to activate site group with id {}", groupId);
        siteGroupService.activateSiteGroup(groupId);
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

    @PostMapping("/dispatch-power-group")
    public ResponseEntity<?> dispatchPowerFromGroup(
            @RequestHeader("Authorization") String token,
            @RequestBody @Valid GroupDispatchRequestDto request) {

        siteGroupService.handleGroupDispatch(request,token);
        return ResponseEntity.ok("dispatch scheduled successfully");
    }

    @PostMapping("/dispatch-power-site")
    public ResponseEntity<?> dispatchPowerFromSite(
            @RequestHeader("Authorization") String token,
            @RequestBody @Valid SiteDispatchRequestDto request) {

        siteGroupService.handleSiteDispatch(request,token);
        return ResponseEntity.ok("dispatch scheduled successfully");
    }





    // controller: dispatch command send by admin only for fetching power from one site / not from whole group
    //and in this only we implement the quartz system to scedule that event so we take sceduled time..

}
