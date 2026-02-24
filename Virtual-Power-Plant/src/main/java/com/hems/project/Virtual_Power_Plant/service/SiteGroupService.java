package com.hems.project.Virtual_Power_Plant.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hems.project.Virtual_Power_Plant.entity.SiteGroup;
import com.hems.project.Virtual_Power_Plant.exception.GroupAlreadyPresentException;
import com.hems.project.Virtual_Power_Plant.exception.ResourceNotFoundException;
import com.hems.project.Virtual_Power_Plant.exception.SiteGroupNotFoundException;
import com.hems.project.Virtual_Power_Plant.exception.SiteGroupStateConflictException;
import com.hems.project.Virtual_Power_Plant.external.SiteFeignClientService;
import com.hems.project.Virtual_Power_Plant.repository.SiteGroupRepository;
import com.project.hems.hems_api_contracts.contract.site.SiteDto;
import com.project.hems.hems_api_contracts.contract.vpp.SiteGroupDto;
import com.project.hems.hems_api_contracts.contract.vpp.SiteGroupReqDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class SiteGroupService {

    private final SiteGroupRepository siteGroupRepository;
    private final SiteFeignClientService siteFeignClientService;
    private final ModelMapper mapper;

    // Note: we have to check for sited id by validating them using feign client as
    // some point in frontend if admin is creating a new group and at the same time
    // that site got deleted then it will create a unexpedted error
    @Transactional
    public SiteGroupDto createSiteGroup(SiteGroupReqDto siteGroupDto) {

        if (siteGroupRepository.existsByGroupName(siteGroupDto.getGroupName())) {
            throw new GroupAlreadyPresentException("unable to create group with name " + siteGroupDto.getGroupName()
                    + " as group with this name already exists");
        }
        log.debug("createSiteGroup: no duplicate group found");

        List<UUID> nonValidSites = siteFeignClientService.verifyAllSites(siteGroupDto.getSitesInGroup());
        if (!nonValidSites.isEmpty()) {
            log.error("createSiteGroup: invalid site id " + nonValidSites.get(0));
            throw new ResourceNotFoundException(
                    "unable to get the site detail, invalid site id " + nonValidSites.get(0));
        }
        log.debug("createSiteGroup: site detail found successfully invalid sites found = " + nonValidSites.size());

        SiteGroup newSiteGroup = mapper.map(siteGroupDto, SiteGroup.class);
        log.debug("createSiteGroup: created site group entity successfuly + " + newSiteGroup);

        SiteGroup savedGroup = siteGroupRepository.save(newSiteGroup);
        log.debug("createSiteGroup: saved site group entity successfuly + " + savedGroup);

        return mapper.map(savedGroup, SiteGroupDto.class);
    }

    public SiteGroupDto getSiteGroupByGroupId(UUID groupId, boolean includeSites) {

        SiteGroup fetchedSiteGroup = siteGroupRepository.findById(groupId)
                .orElseThrow(
                        () -> new SiteGroupNotFoundException("unable to find site group with group id " + groupId));

        log.info("created site group entity successfuly + " + fetchedSiteGroup);

        SiteGroupDto siteGroupDto = mapper.map(fetchedSiteGroup, SiteGroupDto.class);
        if (includeSites) {
            Set<SiteDto> siteDetails = siteFeignClientService.getAllSiteDetail(fetchedSiteGroup.getSitesInGroup(),
                    false);
            siteGroupDto.setSitesInGroup(siteDetails);
        }

        return siteGroupDto;
    }

    public List<SiteGroupDto> getAllSiteGroups(boolean includeSites) {

        List<SiteGroup> groups = siteGroupRepository.findAll();
        List<SiteGroupDto> siteGroupDtos = new ArrayList<>();

        for (SiteGroup siteGroup : groups) {

            SiteGroupDto siteGroupDto = mapper.map(siteGroup, SiteGroupDto.class);

            if (includeSites) {
                Set<UUID> sitesInGroup = siteGroup.getSitesInGroup();
                Set<SiteDto> fetchedSites = siteFeignClientService.getAllSiteDetail(sitesInGroup, false);
                siteGroupDto.setSitesInGroup(fetchedSites);
            }

            siteGroupDtos.add(siteGroupDto);
        }

        return siteGroupDtos;
    }

    // UPDATE
    public SiteGroupDto updateSiteGroup(UUID groupId, SiteGroupReqDto siteGroupDto, boolean includeSites) {

        SiteGroup existingGroup = siteGroupRepository.findById(groupId)
                .orElseThrow(
                        () -> new SiteGroupNotFoundException("Unable to update. Group not found with id " + groupId));

        // Prevent duplicate name change
        if (!existingGroup.getGroupName().equals(siteGroupDto.getGroupName())
                && siteGroupRepository.existsByGroupName(siteGroupDto.getGroupName())) {

            throw new GroupAlreadyPresentException(
                    "Unable to update. Group with name " + siteGroupDto.getGroupName() + " already exists");
        }

        // Update mutable fields
        existingGroup.setGroupName(siteGroupDto.getGroupName());
        existingGroup.setDescription(siteGroupDto.getDescription());
        existingGroup.setGroupType(siteGroupDto.getGroupType());
        existingGroup.setGroupStatus(siteGroupDto.isGroupStatus());
        existingGroup.setCreatedBy(siteGroupDto.getCreatedBy());
        existingGroup.setSitesInGroup(siteGroupDto.getSitesInGroup());

        SiteGroup updatedGroup = siteGroupRepository.save(existingGroup);

        log.info("Updated site group successfully: {}", groupId);

        return mapper.map(updatedGroup, SiteGroupDto.class);
    }

    public void deleteSiteGroup(UUID groupId) {

        SiteGroup existingGroup = siteGroupRepository.findById(groupId)
                .orElseThrow(
                        () -> new SiteGroupNotFoundException("Unable to delete. Group not found with id " + groupId));

        siteGroupRepository.delete(existingGroup);

        log.info("Deleted site group successfully: {}", groupId);
    }

    public void deactivateSiteGroup(UUID groupId) {

        SiteGroup existingGroup = siteGroupRepository.findById(groupId)
                .orElseThrow(() -> new SiteGroupNotFoundException(
                        "Unable to deactivate. Group not found with id " + groupId));

        if (!existingGroup.isGroupStatus()) {
            throw new SiteGroupStateConflictException(
                    "site group with group id " + groupId + " is already deactivated, duplicate request");
        }

        existingGroup.setGroupStatus(false);

        siteGroupRepository.save(existingGroup);

        log.info("Deactivated site group successfully: {}", groupId);
    }

    public void activateSiteGroup(UUID groupId) {

        SiteGroup existingGroup = siteGroupRepository.findById(groupId)
                .orElseThrow(() -> new SiteGroupNotFoundException(
                        "Unable to deactivate. Group not found with id " + groupId));

        if (existingGroup.isGroupStatus()) {
            throw new SiteGroupStateConflictException(
                    "site group with group id " + groupId + " is already activated, duplicate request");
        }

        existingGroup.setGroupStatus(true);

        siteGroupRepository.save(existingGroup);

        log.info("Activated site group successfully: {}", groupId);
    }

}
