package com.project.hems.SiteManagerService.service;

import com.project.hems.SiteManagerService.dto.SiteCreationEvent;
import com.project.hems.SiteManagerService.dto.SiteRequestDto;
import com.project.hems.SiteManagerService.dto.SiteResponseDto;
import com.project.hems.SiteManagerService.entity.*;
import com.project.hems.SiteManagerService.exception.ResourceNotFoundException;
import com.project.hems.SiteManagerService.repository.OwnerRepo;
import com.project.hems.SiteManagerService.repository.SiteRepo;
import com.project.hems.SiteManagerService.util.ValueMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

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

        // in dto apde id store kariee chiee owner entity ni so apde ema thi fetch
        // karine obj banavsu
        Owner owner = ownerRepo.findById(dto.getOwnerId())
                .orElseThrow(() -> new ResourceNotFoundException("Owner not found first add Owner then add Site"));

        // Save owner first in new created site
        Owner savedOwner = ownerRepo.save(owner);

        // now we create new Site obj and eni under badhu set karsu
        Site site = new Site();
        site.setOwner(savedOwner);
        site.setActive(true);
        site.setEnrollProgramIds(dto.getProgramId());

        // now have child na table jode thi badhu laisu save karsu then site ma save
        // karsu
        // solar
        if (dto.getSolars() != null) {
            List<Solar> solarList = dto.getSolars().stream()
                    .map(solarDto -> {
                        Solar solar = valueMapper.solarDtoToModel(solarDto, site);
                        return solar;
                    }).toList();
            site.setSolar(solarList);
        }

        // battery
        if (dto.getBattery() != null) {
            Battery battery = valueMapper.batteryDtoToModel(dto.getBattery(), site);
            site.setBattery(battery);
        }

        // address
        if (dto.getAddress() != null) {
            Address address = valueMapper.addressDtoToModel(dto.getAddress(), site);
            site.setAddress(address);
        }

        Site savedSite = siteRepo.save(site);

        // todo:-
        // site ni pan dto banavine work karvu siteResponseDto che toh e pass karvo
        UUID id = savedSite.getId();
        SiteCreationEvent siteCreationEvent = SiteCreationEvent.builder()
                .siteId(id)
                .batteryCapacityW(savedSite.getBattery().getCapacity())
                .build();
        kafkaTemplate.send(siteCreationTopic, siteCreationEvent);
        log.info("kafka event send to site creation topic body is " + id);
        return savedSite;

    }

    @Async
    public Site fetchSiteById(UUID siteId) {
        Site site = siteRepo.findById(siteId)
                .orElseThrow(() -> new ResourceNotFoundException("site is not found with site id :- " + siteId));
        System.out.println("running on thread :- " + Thread.currentThread());
        return site;

    }

    //
    public List<Site> fetchAllSite() {
        List<Site> sites = siteRepo.findAll();
        System.out.println("running on thread :- " + Thread.currentThread());
        CompletableFuture<Integer> firstTask = CompletableFuture.supplyAsync(() -> {
            return 42;
        });

        CompletableFuture<String> secondTask = firstTask.thenApply(result -> {
            return "Result based on Task 1: " + result;
        });
        return sites;
    }

    public List<SiteResponseDto> fetchAllSiteV2() {
        List<Site> sites = siteRepo.findAll();
        List<SiteResponseDto> siteResponseDtos = sites.stream().map(valueMapper::siteModelToResponseDto).toList();
        return siteResponseDtos;
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


    public List<SiteResponseDto> fetchSiteByRegion(String city){
        List<Site> sites=siteRepo.findByAddress_City(city);
        List<SiteResponseDto> siteResponseDtos = sites.stream().map(valueMapper::siteModelToResponseDto).toList();
        return siteResponseDtos;
    }

}