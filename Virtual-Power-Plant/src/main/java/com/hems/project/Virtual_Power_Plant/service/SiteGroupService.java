package com.hems.project.Virtual_Power_Plant.service;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.hems.project.Virtual_Power_Plant.entity.SiteGroup;
import com.hems.project.Virtual_Power_Plant.exception.GroupAlreadyPresentException;
import com.hems.project.Virtual_Power_Plant.exception.SiteGroupNotFoundException;
import com.hems.project.Virtual_Power_Plant.exception.SiteGroupStateConflictException;
import com.hems.project.Virtual_Power_Plant.repository.SiteGroupRepository;
import com.project.hems.hems_api_contracts.contract.vpp.SiteGroupDto;
import com.project.hems.hems_api_contracts.contract.vpp.SiteGroupReqDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class SiteGroupService {

    private final SiteGroupRepository siteGroupRepository;
    private final ModelMapper mapper;

    public SiteGroupDto createSiteGroup(SiteGroupReqDto siteGroupDto) {

        if (siteGroupRepository.existsByGroupName(siteGroupDto.getGroupName())) {
            throw new GroupAlreadyPresentException("unable to create group with name " + siteGroupDto.getGroupName()
                    + " as group with this name already exists");
        }

        SiteGroup newSiteGroup = mapper.map(siteGroupDto, SiteGroup.class);

        log.info("created site group entity successfuly + " + newSiteGroup);

        SiteGroup savedGroup = siteGroupRepository.save(newSiteGroup);

        return mapper.map(savedGroup, SiteGroupDto.class);
    }

    public SiteGroupDto getSiteGroupByGroupId(UUID groupId, boolean includeSites) {

        SiteGroup fetchedSiteGroup = siteGroupRepository.findById(groupId)
                .orElseThrow(
                        () -> new SiteGroupNotFoundException("unable to find site group with group id " + groupId));

        log.info("created site group entity successfuly + " + fetchedSiteGroup);

        return mapper.map(fetchedSiteGroup, SiteGroupDto.class);
    }

    public List<SiteGroupDto> getAllSiteGroups(boolean includeSites) {

        List<SiteGroup> groups = siteGroupRepository.findAll();

        return groups.stream()
                .map(group -> mapper.map(group, SiteGroupDto.class))
                .toList();
    }

    // UPDATE
    public SiteGroupDto updateSiteGroup(UUID groupId, SiteGroupDto siteGroupDto, boolean includeSites) {

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
        Set<UUID> updatedSites = siteGroupDto.getSitesInGroup()
                .stream()
                .map(siteDto -> siteDto.getSiteId())
                .collect(Collectors.toSet());
        existingGroup.setSitesInGroup(updatedSites);

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
