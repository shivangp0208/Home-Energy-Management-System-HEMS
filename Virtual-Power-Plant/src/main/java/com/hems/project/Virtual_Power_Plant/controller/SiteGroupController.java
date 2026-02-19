package com.hems.project.Virtual_Power_Plant.controller;

import org.springframework.web.bind.annotation.RestController;

import com.hems.project.Virtual_Power_Plant.service.SiteGroupService;
import com.project.hems.hems_api_contracts.contract.vpp.SiteGroupDto;
import com.project.hems.hems_api_contracts.contract.vpp.SiteGroupReqDto;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/site-groups")
public class SiteGroupController {

    private final SiteGroupService siteGroupService;

    // TODO: for fetching the admin detail we have to first create a AdminIdentites
    // to store thier token's sub claim to identify them and then at the time of
    // creating from the token we can know who created this group
    @PostMapping("/create-site-group")
    public SiteGroupDto createSiteGroup(
            @RequestBody @Valid SiteGroupReqDto siteGroup,
            @RequestParam(name = "includeSites", required = false, defaultValue = "false") boolean includeSites) {
        log.info("POST req to create a site group ");
        return siteGroupService.createSiteGroup(siteGroup);
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
    public SiteGroupDto updateSiteGroup(
            @PathVariable(name = "groupId", required = true) UUID groupId,
            @RequestBody @Valid SiteGroupDto siteGroupDto,
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

}
