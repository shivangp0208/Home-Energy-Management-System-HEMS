package com.project.hems.SiteManagerService.service;

import com.project.hems.SiteManagerService.dto.CursorSiteResponse;
import com.project.hems.SiteManagerService.entity.Battery;
import com.project.hems.SiteManagerService.entity.Owner;
import com.project.hems.SiteManagerService.entity.Site;
import com.project.hems.hems_api_contracts.contract.program.ProgramFeignDto;
import com.project.hems.hems_api_contracts.contract.site.*;

import jakarta.transaction.Transactional;

import com.project.hems.SiteManagerService.exception.ProgramNotValidException;
import com.project.hems.SiteManagerService.exception.ResourceNotFoundException;
import com.project.hems.SiteManagerService.repository.OwnerRepo;
import com.project.hems.SiteManagerService.repository.SiteRepo;
import com.project.hems.SiteManagerService.service.impl.SiteService;
import com.project.hems.SiteManagerService.util.SiteHelperMethods;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class SiteServiceImpl implements SiteService {

        private final SiteRepo siteRepo;
        private final OwnerRepo ownerRepo;
        private final KafkaTemplate<String, SiteCreationEvent> kafkaTemplate;
        private final ModelMapper mapper;
        private final SiteHelperMethods siteHelperMethods;

        @Value("${property.config.kafka.site-creation-topic}")
        public String siteCreationTopic;

        @Transactional
        public SiteDto createSite(SiteDto siteDto, String userSub) {
                log.info("createSite: Creating site for ownerId={} by userSub={}", siteDto, userSub);

                // in dto apde id store kariee chiee owner entity ni so apde ema thi fetch
                // karine obj banavsu
                log.info("creating site start ownerId={} userSub={}", siteDto, userSub);
                log.info("fetch owner is exists or not with ownerId = {}", siteDto);
                Owner owner = ownerRepo.findById(siteDto.getOwnerId())
                                .orElseThrow(() -> {
                                        log.warn("createSite: Owner not found, ownerId={}",
                                                        siteDto.getOwnerId());
                                        return new ResourceNotFoundException(
                                                        "Owner not found first add Owner then add Site");
                                });

                // Save owner first in new created site
                log.debug("createSite: Owner fetched with ownerId={}", owner.getOwnerId());

                Site siteEnity = mapper.map(siteDto, Site.class);

                log.debug("createSite: after mapping site dto to entity = {}", siteEnity);

                siteEnity.setOwner(owner);
                siteEnity.getOwner().getSites().add(siteEnity);
                siteEnity.getSolar().forEach(solarEn -> solarEn.setSite(siteEnity));
                siteEnity.getAddress().setSite(siteEnity);
                siteEnity.getBatteries().forEach(battery -> battery.setSite(siteEnity));

                log.debug("createSite: after setting site entity to each dto in site entity = {}", siteEnity);

                Site savedSite = siteRepo.save(siteEnity);
                log.info("Creating site success siteId={} ownerId={} solarCount={} batteryIncluded={} addressIncluded={}",
                                savedSite.getSiteId(),
                                savedSite.getOwner(),
                                savedSite.getSolar() != null ? savedSite.getSolar().size() : 0,
                                savedSite.getBatteries() != null,
                                savedSite.getAddress() != null);

                owner.getSites().add(savedSite);
                ownerRepo.save(owner);

                log.info("create kafka SiteCreationEvent");
                SiteCreationEvent siteCreationEvent = SiteCreationEvent.builder()
                                .siteId(savedSite.getSiteId())
                                .batteryCapacityW(savedSite.getBatteries()
                                                .stream()
                                                .map(Battery::getCapacityWh)
                                                .reduce((res, curr) -> res + curr)
                                                .orElse(0.0))
                                .build();

                kafkaTemplate.send(siteCreationTopic, siteCreationEvent);
                log.info("createSite: Kafka event sent, topic={}, siteId={}", siteCreationTopic, savedSite.getSiteId());

                return mapper.map(savedSite, SiteDto.class);
        }

        public SiteDto fetchSiteById(UUID siteId, boolean includeProgram) {
                log.info("fetchSiteById: Fetching site asynchronously, siteId={}", siteId);
                return siteHelperMethods.getSiteBySiteId(siteId, includeProgram);
        }

        //
        public List<SiteDto> fetchAllSite() {
                log.info("fetchAllSite: Fetching all sites");

                List<Site> sites = siteRepo.findAll();

                List<SiteDto> siteDtos = sites.stream()
                                .map(entity -> mapper.map(entity, SiteDto.class))
                                .toList();
                return siteDtos;
        }

        public List<SiteDto> fetchAllSiteV2() {
                log.info("fetchAllSiteV2: Fetching all sites as response DTO");

                List<Site> sites = siteRepo.findAll();
                List<SiteDto> siteDto = sites.stream()
                                .map(entity -> mapper.map(entity, SiteDto.class))
                                .toList();

                log.debug("fetchAllSiteV2: Total sites fetched={}", siteDto.size());
                return siteDto;
        }

        // pagination in fetchAllSiteV2 this api
        public Page<SiteDto> findAllSiteV2WithPagination(int offset, int pageSize) {
                return siteRepo.findAll(PageRequest.of(offset, pageSize))
                                .map(entity -> mapper.map(entity, SiteDto.class));
        }

        // pagination in fetchAllSiteV2 this api with sorting based on input key
        public Page<SiteDto> findAllSiteV2WithPaginationAndSorting(int offset, int pageSize, String field) {
                return siteRepo.findAll(PageRequest.of(offset, pageSize)
                                .withSort(Sort.by(field)))
                                .map(entity -> mapper.map(entity, SiteDto.class));
        }

        // cursor based paginaton in fetchAllSiteV2
        public CursorSiteResponse<SiteDto> getSites(UUID cursor, int size) {
                // default page=0 and size=10
                Pageable pageable = PageRequest.of(0, size);

                // fetch next page record
                List<SiteDto> sites = siteRepo.fetchNextPage(cursor, pageable)
                                .stream()
                                .map(entity -> mapper.map(entity, SiteDto.class))
                                .toList();

                // check if we have more record
                boolean hasNext = sites.size() == size;

                // define the next cursor
                UUID nextCursor = hasNext ? sites.get(sites.size() - 1).getSiteId() : null;

                return new CursorSiteResponse<>(
                                sites,
                                size,
                                nextCursor,
                                hasNext);
        }

        public List<SiteDto> fetchSiteByRegion(String city) {
                log.info("fetchSiteByRegion: Fetching sites for city={}", city);

                List<Site> sites = siteRepo.findByAddress_City(city);
                List<SiteDto> SiteDtos = sites.stream()
                                .map(entity -> mapper.map(entity,
                                                SiteDto.class))
                                .toList();

                log.debug("fetchSiteByRegion: Found {} sites for city={}", SiteDtos.size(), city);
                return SiteDtos;
        }

        public List<SiteDto> fetchSiteByProgram(UUID programId, boolean includeProgram) {
                log.info("fetchSiteByProgram: Fetching sites with program id ={}", programId);

                List<Site> sites = siteRepo.findAllSitesByEnrollProgramIds(programId);
                log.debug("fetchSiteByProgram: Found {} sites enrolled in program id ={}", sites.size(), programId);
                List<SiteDto> resultSites = sites
                                .stream()
                                .map(en -> siteHelperMethods.getSiteByEntity(en, includeProgram))
                                .toList();
                return resultSites;
        }

        public List<String> fetchAllRegion() {
                List<String> allRegion = siteRepo.findAllRegion();
                return allRegion;
        }

        public SiteDto enrollSiteInProgram(UUID siteId, ProgramFeignDto program) {
                log.info("enrollSiteInProgram: enrolling site with site id {} in program {}", siteId, program);

                Site siteEntity = siteRepo.findById(siteId).orElseThrow(() -> new ResourceNotFoundException(
                                "unable to find site detail for site id = " + siteId));

                if (program == null || program.getProgramId() == null) {
                        throw new ProgramNotValidException(
                                        "invalid program detail for enrollment in site, program id is not provided" +
                                                        program);
                }
                siteEntity.getEnrollProgramIds().add(program.getProgramId());

                Site updatedSite = siteRepo.save(siteEntity);

                return mapper.map(updatedSite, SiteDto.class);
        }
}