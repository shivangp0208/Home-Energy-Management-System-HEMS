package com.hems.project.admin_service.service;

import com.hems.project.admin_service.dto.*;
import com.hems.project.admin_service.entity.CaseEvent;
import com.hems.project.admin_service.entity.CaseTicket;
import com.hems.project.admin_service.external.SiteFeignClientService;
import com.hems.project.admin_service.repository.CaseTicketRepository;
import com.project.hems.hems_api_contracts.contract.site.SiteDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Component
public class AutoCaseConsumerService {

    private final RedisTemplate<String,String> redisTemplate;
    private final CaseService caseService;
    private final CaseTicketRepository caseTicketRepository;
    private final SiteFeignClientService siteFeignClientService;
    private final Random random = new Random();



    @Scheduled(fixedRate = 60000)
    public void checkHeartbeats(){


        List<UUID> allSiteDetail = siteFeignClientService.getAllSiteIdByMeterStatus(true);
        log.info("all site id is {} ",allSiteDetail);

         List<String> list = allSiteDetail.stream().map(siteIds -> siteIds.toString()).toList();
        Set<String> aliveSites = redisTemplate.opsForSet().members("alive_sites");
        boolean allAlive = true;

        for(String site : list){

            if(aliveSites == null || !aliveSites.contains(site)){
                allAlive = false;

                log.warn("Heartbeat missing for site {}", site);

                // avoid duplicate case
                boolean alreadyOpen = caseTicketRepository
                        .existsBySiteIdAndStatusIn(UUID.fromString(site),
                                List.of(CaseStatus.NEW, CaseStatus.IN_PROGRESS));

                if(alreadyOpen){
                    log.info("Case already open for site {}", site);

                    CaseTicket existingCase = caseTicketRepository
                            .findFirstBySiteIdAndStatusIn(
                                    UUID.fromString(site),
                                    List.of(CaseStatus.NEW, CaseStatus.IN_PROGRESS)
                            )
                            .orElse(null);

                    if(existingCase != null){

                        CaseEvent event = CaseEvent.builder()
                                .caseId(existingCase.getId())
                                .eventType(CaseEventType.COMMENT_ADDED)
                                .message("Heartbeat still missing for site")
                                .actor("system")
                                .build();

                        caseService.addEvent(event);
                    }

                    continue;
                }

                CaseRaisedEventDto dto = CaseRaisedEventDto.builder()
                        .siteId(UUID.fromString(site))
                        .type(CaseType.SITE_OFFLINE)
                        .priority(CasePriority.HIGH)
                        .source(CaseSource.SYSTEM)
                        .sourceService("HEARTBEAT_MONITOR")
                        .title("Site Heartbeat Missing")
                        .description("Heartbeat not received from site for last 60 seconds")
                        .build();

                caseService.createCaseManully(dto);

                log.info("Case automatically created for site {}", site);
            }
        }
        if (allAlive) {
            log.info("All activated meters are sending heartbeat");
        }

        redisTemplate.delete("alive_sites");
    }

}

/*
redis format
KEY
alive_sites  -> SET
        VALUES
--------------------------------
        "11111111-1111-1111-1111-111111111111"
        "22222222-2222-2222-2222-222222222222"
        "33333333-3333-3333-3333-333333333333"

 */