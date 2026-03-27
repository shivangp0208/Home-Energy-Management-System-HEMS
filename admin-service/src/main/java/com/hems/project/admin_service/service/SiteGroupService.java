package com.hems.project.admin_service.service;

import com.hems.project.admin_service.dto.GroupDispatchRequestDto;
import com.hems.project.admin_service.dto.SiteDispatchRequestDto;
import com.hems.project.admin_service.entity.SiteGroup;
import com.hems.project.admin_service.exception.GroupAlreadyPresentException;
import com.hems.project.admin_service.exception.ResourceNotFoundException;
import com.hems.project.admin_service.exception.SiteGroupNotFoundException;
import com.hems.project.admin_service.exception.SiteGroupStateConflictException;
import com.hems.project.admin_service.external.ProgramFeignClientService;
import com.hems.project.admin_service.external.SiteFeignClientService;
import com.hems.project.admin_service.repository.SiteGroupRepository;
import com.project.hems.hems_api_contracts.contract.site.SiteDto;
import com.project.hems.hems_api_contracts.contract.vpp.SiteGroupDto;
import com.project.hems.hems_api_contracts.contract.vpp.SiteGroupReqDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Service
@Slf4j
@RequiredArgsConstructor
public class SiteGroupService {

    private final SiteGroupRepository siteGroupRepository;
    private final SiteFeignClientService siteFeignClientService;
    private final ModelMapper mapper;
    private final DispatchSchedulerService quartzService;
    private final ProgramFeignClientService programFeignClientService;
    private final Executor contextAwareExecutor;

    // Note: we have to check for sited id by validating them using feign client as
    // some point in frontend if admin is creating a new group and at the same time
    // that site got deleted then it will create a unexpedted error
    @Transactional
    public SiteGroupDto createSiteGroup(SiteGroupReqDto siteGroupDto,String token) {

        if (siteGroupRepository.existsByGroupName(siteGroupDto.getGroupName())) {
            throw new GroupAlreadyPresentException("unable to create group with name " + siteGroupDto.getGroupName()
                    + " as group with this name already exists");
        }
        log.debug("createSiteGroup: no duplicate group found");

        List<UUID> nonValidSites = siteFeignClientService.verifyAllSites(token,siteGroupDto.getSitesInGroup());
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

    //note:-
    //todo:-
    //here do db call and external remote call(fegin call) so in transcational we cannot do this
    //in one place...because if we wrote
    public void handleGroupDispatch(GroupDispatchRequestDto request,String token) {

        List<UUID> validSiteIds = validateGroupDispatch(request,token);
        log.info("handle service is done 1");

        scheduleGroupDispatch(request, validSiteIds);
        log.info("handle service is done 2");
    }

    @Transactional
    public void scheduleGroupDispatch(
            GroupDispatchRequestDto request,
            List<UUID> validSiteIds) {

        quartzService.scheduleDispatchEvent(
                request.getProgramId(),
                validSiteIds,
                UUID.randomUUID(),
                request.getEventMode(),
                request.getTargetPowerW(),
                request.getTargetSoc(),
                request.getDurationMinutes(),
                request.getScheduleTime()
        );
    }

    public List<UUID> validateGroupDispatch(GroupDispatchRequestDto request,String token) {

        SiteGroup group = siteGroupRepository.findById(request.getGroupId())
                .orElseThrow(() -> new RuntimeException("group not found"));

        if (!group.isGroupStatus()) {
            throw new RuntimeException("group is inactive");
        }

        if (request.getScheduleTime().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("schedule time must be future");
        }

        Set<UUID> siteIds = group.getSitesInGroup();

        if (siteIds.isEmpty()) {
            throw new RuntimeException("no sites in group");
        }

        List<CompletableFuture<UUID>> futures = siteIds.stream()
                .map(siteId ->
                        CompletableFuture.supplyAsync(() -> {
                            Boolean available =
                                    siteFeignClientService
                                            .checkSiteIsAvailableOtNot(token,siteId)
                                            .getBody();

                            return Boolean.TRUE.equals(available) ? siteId : null;
                        },contextAwareExecutor)
                )
                .toList();

        List<UUID> validSiteIds = futures.stream()
                .map(CompletableFuture::join)
                .filter(Objects::nonNull)
                .toList();

        if (validSiteIds.isEmpty()) {
            throw new RuntimeException("no eligible sites found");
        }
        boolean available = programFeignClientService.checkProgramIdIsAvailable(request.getProgramId());
        if (!available) {
            throw new RuntimeException("program not available with id"+request.getProgramId());
        }

        return validSiteIds;
    }

    public void validateSiteDispatch(SiteDispatchRequestDto request,String token) {

        if (request.getScheduleTime().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("schedule time must be future");
        }

        ResponseEntity<Boolean> response =
                siteFeignClientService
                        .checkSiteIsAvailableOtNot(token,request.getSiteId());



        if (!Boolean.TRUE.equals(response.getBody())) {
            throw new RuntimeException("site not available");
        }

         boolean available = programFeignClientService.checkProgramIdIsAvailable(request.getProgramId());
        if (!available) {
            throw new RuntimeException("program not available with id"+request.getProgramId());
        }
    }
    @Transactional
    public void scheduleSiteDispatch(SiteDispatchRequestDto request) {

        quartzService.scheduleDispatchEvent(
                request.getProgramId(),
                List.of(request.getSiteId()),
                UUID.randomUUID(),
                request.getEventMode(),
                request.getTargetPowerW(),
                request.getTargetSoc(),
                request.getDurationMinutes(),
                request.getScheduleTime()
        );
    }

    public void handleSiteDispatch(SiteDispatchRequestDto request,String token) {

        validateSiteDispatch(request,token);

        scheduleSiteDispatch(request);
    }
}
