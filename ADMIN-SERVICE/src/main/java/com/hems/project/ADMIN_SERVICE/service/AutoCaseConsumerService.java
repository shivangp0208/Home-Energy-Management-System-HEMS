package com.hems.project.ADMIN_SERVICE.service;

import com.hems.project.ADMIN_SERVICE.dto.*;
import com.hems.project.ADMIN_SERVICE.entity.CaseEvent;
import com.hems.project.ADMIN_SERVICE.entity.CaseTicket;
import com.hems.project.ADMIN_SERVICE.external.SiteFeignClientService;
import com.hems.project.ADMIN_SERVICE.repository.CaseTicketRepository;
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

        List<UUID> allSiteDetail = siteFeignClientService.getAllSiteDetail();
         List<String> list = allSiteDetail.stream().map(siteIds -> siteIds.toString()).toList();
        Set<String> aliveSites = redisTemplate.opsForSet().members("alive_sites");

        for(String site : list){

            if(aliveSites == null || !aliveSites.contains(site)){

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

        redisTemplate.delete("alive_sites");
    }

    @Scheduled(fixedRate = 30000)
    public void simulateHeartbeat(){

        List<SiteDto> sites = siteFeignClientService.getAllSites(false).getBody();

        if (sites == null) return;

        for (SiteDto site : sites) {

            UUID siteId = site.getSiteId();

            boolean heartbeat = random.nextInt(100) < 80;

            redisTemplate.opsForValue()
                    .set(siteId.toString(), String.valueOf(heartbeat));

            log.info("Simulated heartbeat for site {} -> {}", siteId, heartbeat);
        }
    }

}

/*
KEY
alive_sites  -> SET

        VALUES
--------------------------------
        "11111111-1111-1111-1111-111111111111"
        "22222222-2222-2222-2222-222222222222"
        "33333333-3333-3333-3333-333333333333"

 */