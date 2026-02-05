package com.project.hems.SiteManagerService.service;

import com.project.hems.SiteManagerService.dto.CursorSiteResponse;
import com.project.hems.SiteManagerService.dto.SiteRequestDto;
import com.project.hems.SiteManagerService.entity.*;
import com.project.hems.SiteManagerService.exception.ResourceNotFoundException;
import com.project.hems.SiteManagerService.repository.OwnerRepo;
import com.project.hems.SiteManagerService.repository.SiteRepo;
import com.project.hems.SiteManagerService.util.ValueMapper;
import com.project.hems.hems_api_contracts.contract.site.SiteCreationEvent;
import com.project.hems.hems_api_contracts.contract.site.SiteResponseDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class SiteService {

    private final SiteRepo siteRepo;
    private final OwnerRepo ownerRepo;
    private final ValueMapper valueMapper;
    private final KafkaTemplate<String, SiteCreationEvent> kafkaTemplate;

    @Value("${property.config.kafka.site-creation-topic}")
    public String siteCreationTopic;

    public Site createSite(SiteRequestDto dto, String userSub) {
        log.info("createSite: Creating site for ownerId={} by userSub={}", dto.getOwnerId(), userSub);

        // in dto apde id store kariee chiee owner entity ni so apde ema thi fetch
        // karine obj banavsu
    log.info("creating site start ownerId={} userSub={}", dto.getOwnerId(), userSub);
        log.info("fetch owner is exists or not with ownerId = {}",dto.getOwnerId());
        Owner owner = ownerRepo.findById(dto.getOwnerId())
                .orElseThrow(() -> {
                    log.warn("createSite: Owner not found, ownerId={}", dto.getOwnerId());
                    return new ResourceNotFoundException("Owner not found first add Owner then add Site");
                });

        // Save owner first in new created site
        Owner savedOwner = ownerRepo.save(owner);
        log.debug("createSite: Owner fetched and saved, ownerId={}", savedOwner.getId());

        // now we create new Site obj and eni under badhu set karsu

        Site site = new Site();
        site.setOwner(savedOwner);
        site.setActive(true);
        site.setEnrollProgramIds(dto.getProgramId());

        // now have child na table jode thi badhu laisu save karsu then site ma save
        // karsu
        // solar
        if (dto.getSolars() != null) {
            log.debug("createSite: Mapping {} solar units", dto.getSolars().size());
            List<Solar> solarList = dto.getSolars().stream()
                    .map(solarDto -> {
                        Solar solar = valueMapper.solarDtoToModel(solarDto, site);
                        return solar;
                    }).toList();
            site.setSolar(solarList);
        }

        // battery
        if (dto.getBattery() != null) {
            log.debug("createSite: Mapping battery details");
            Battery battery = valueMapper.batteryDtoToModel(dto.getBattery(), site);
            site.setBattery(battery);
        }

        // address
        if (dto.getAddress() != null) {
            log.debug("createSite: Mapping address details");
            Address address = valueMapper.addressDtoToModel(dto.getAddress(), site);
            site.setAddress(address);

        }

        Site savedSite = siteRepo.save(site);
        log.info("Creating site success siteId={} ownerId={} solarCount={} batteryIncluded={} addressIncluded={}",
            savedSite.getId(),
            dto.getOwnerId(),
            savedSite.getSolar() != null ? savedSite.getSolar().size() : 0,
            savedSite.getBattery() != null,
            savedSite.getAddress() != null
        );
        // todo:-
        // site ni pan dto banavine work karvu siteResponseDto che toh e pass karvo
        log.info("create kafka SiteCreationEvent");
        UUID id = savedSite.getId();
        SiteCreationEvent siteCreationEvent = SiteCreationEvent.builder()
                .siteId(id)
                .batteryCapacityW(savedSite.getBattery().getCapacityWh())
                .build();

        kafkaTemplate.send(siteCreationTopic, siteCreationEvent);
        log.info("createSite: Kafka event sent, topic={}, siteId={}", siteCreationTopic, id);

        return savedSite;
    }

    @Async
    public Site fetchSiteById(UUID siteId) {
        log.info("fetchSiteById: Fetching site asynchronously, siteId={}", siteId);

        Site site = siteRepo.findById(siteId)
                .orElseThrow(() -> {
                    log.warn("fetchSiteById: Site not found, siteId={}", siteId);
                    return new ResourceNotFoundException("site is not found with site id :- " + siteId);
                });

        log.trace("fetchSiteById: running on thread :- " + Thread.currentThread());
        log.debug("fetchSiteById: Running on thread={}", Thread.currentThread());

        return site;
    }

    //
    public List<Site> fetchAllSite() {
        log.info("fetchAllSite: Fetching all sites");

        List<Site> sites = siteRepo.findAll();
        log.trace("fetchAllSite: running on thread :- " + Thread.currentThread());
        log.debug("fetchAllSite: Running on thread={}", Thread.currentThread());

        // CompletableFuture<Integer> firstTask = CompletableFuture.supplyAsync(() -> {
        //     return 42;
        // });

        // CompletableFuture<String> secondTask = firstTask.thenApply(result -> {
        //     return "Result based on Task 1: " + result;
        // });

        log.debug("fetchAllSite: Dummy async tasks triggered");
        return sites;
    }

    public List<SiteResponseDto> fetchAllSiteV2() {
        log.info("fetchAllSiteV2: Fetching all sites as response DTO");

        List<Site> sites = siteRepo.findAll();
        List<SiteResponseDto> siteResponseDtos = sites.stream()
                .map(valueMapper::siteModelToResponseDto)
                .toList();

        log.debug("fetchAllSiteV2: Total sites fetched={}", siteResponseDtos.size());
        return siteResponseDtos;
    }

    //pagination in fetchAllSiteV2 this api 
    public Page<SiteResponseDto> findAllSiteV2WithPagination(int offset,int pageSize){
       return siteRepo.findAll(PageRequest.of(offset, pageSize)).map(valueMapper::siteModelToResponseDto);
    }

    //pagination in fetchAllSiteV2 this api with sorting based on input key
    public Page<SiteResponseDto> findAllSiteV2WithPaginationAndSorting(int offset,int pageSize,String field){
       return siteRepo.findAll(PageRequest.of(offset, pageSize).withSort(Sort.by(field))).map(valueMapper::siteModelToResponseDto);
    }

    //cursor based paginaton in fetchAllSiteV2
    public CursorSiteResponse<SiteResponseDto> getSites(UUID cursor,int size){
        //default page=0 and size=10
         Pageable pageable=PageRequest.of(0, size);

         //fetch next page record
         List<SiteResponseDto> sites = siteRepo.fetchNextPage(cursor, pageable).stream().map(valueMapper::siteModelToResponseDto).toList();

         //check if we have more record
         boolean hasNext = sites.size() == size;

         //define the next cursor
         UUID nextCursor= hasNext?sites.get(sites.size()-1).getSiteId():null;

         
        return new CursorSiteResponse<>(
            sites,
            size,
            nextCursor,
            hasNext
        );
    }



    // @Async
    // @Transactional(readOnly = true)
    // public CompletableFuture<List<SiteResponseDto>> fetchAllSites() {
    //
    // List<SiteResponseDto> dtoList = siteRepo.findAll()
    // .stream()
    // .map(valueMapper::siteModelToResponseDto)
    // .toList();
    //
    // return CompletableFuture.completedFuture(dtoList);
    // }

    public List<SiteResponseDto> fetchSiteByRegion(String city) {
        log.info("fetchSiteByRegion: Fetching sites for city={}", city);

        List<Site> sites = siteRepo.findByAddress_City(city);
        List<SiteResponseDto> siteResponseDtos = sites.stream()
                .map(valueMapper::siteModelToResponseDto)
                .toList();

        log.debug("fetchSiteByRegion: Found {} sites for city={}", siteResponseDtos.size(), city);
        return siteResponseDtos;
    }

    public List<String> fetchAllRegion(){
        List<String> allRegion = siteRepo.findAllRegion();
        return allRegion;
    }
}
