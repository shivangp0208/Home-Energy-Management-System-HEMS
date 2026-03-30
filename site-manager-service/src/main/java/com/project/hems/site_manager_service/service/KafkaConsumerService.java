package com.project.hems.site_manager_service.service;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Service;

import com.project.hems.hems_api_contracts.contract.site.SiteCreationEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@ConfigurationProperties(prefix = "property.config.kafka")
public class KafkaConsumerService {

    @RetryableTopic(attempts = "3", backoff = @Backoff(delay = 2000), dltTopicSuffix = ".DLT", autoStartDltHandler = "false")
    @KafkaListener(topics = "${property.config.kafka.site-creation-dlt-topic}", groupId = "${property.config.kafka.site-creation-dlt-group-id}")
    public void consumeSiteCreationDLTEvents(SiteCreationEvent siteCreationEvent) {
        // TODO: after failing of an event for any site we can send the error detail
        // from here to admin dashboard
        log.error("consumeSiteCreationDLTEvents: error for the site creation event " + siteCreationEvent);
    }

}
