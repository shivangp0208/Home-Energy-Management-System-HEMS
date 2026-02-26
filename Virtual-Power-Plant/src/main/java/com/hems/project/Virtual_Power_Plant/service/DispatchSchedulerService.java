package com.hems.project.Virtual_Power_Plant.service;

import com.hems.project.Virtual_Power_Plant.entity.DispatchEvent;
import com.project.hems.hems_api_contracts.contract.vpp.DispatchMode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class DispatchSchedulerService {

    private final Scheduler scheduler;

    public void scheduleDispatchEvent(List<UUID> siteId, UUID eventId, DispatchMode mode, Long targetPower,Integer targetSoc,LocalDateTime scheduledTime) {


        JobDetail jobDetail = buildJobDetail(siteId,eventId,mode,targetPower,targetSoc);
        Trigger trigger = buildTrigger(jobDetail,scheduledTime);

        try {
            scheduler.scheduleJob(jobDetail, trigger);
            log.info("dispatch scheduled at {}", scheduledTime);
        } catch (SchedulerException e) {
            throw new RuntimeException("failed to schedule job", e);
        }
    }

    private JobDetail buildJobDetail(
                List<UUID> siteIds,
                UUID eventId,
                DispatchMode mode,
                Long targetPower,
                Integer targetSoc) {

        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put("eventId", eventId.toString());
        jobDataMap.put("eventMode", mode.name());
        jobDataMap.put("targetPowerW", targetPower);
        jobDataMap.put("targetSoc", targetSoc);

        //convert uuid to string to safe seralization
        if (siteIds != null && !siteIds.isEmpty()) {
            List<String> siteIdStrings = siteIds.stream()
                    .map(UUID::toString)
                    .toList();

            jobDataMap.put("siteIds", siteIdStrings);
        }
        return JobBuilder.newJob(DispatchEvent.class)
                .withIdentity("dispatch-" + eventId, "dispatch-jobs")
                .usingJobData(jobDataMap)
                .storeDurably()
                .build();
    }

    private Trigger buildTrigger(JobDetail jobDetail,
                                 LocalDateTime dateTime) {

        return TriggerBuilder.newTrigger()
                .forJob(jobDetail)
                .withIdentity(
                        jobDetail.getKey().getName(),
                        "dispatch-triggers")
                .startAt(
                        Date.from(dateTime.atZone(
                                ZoneId.systemDefault()).toInstant()))
                .withSchedule(
                        SimpleScheduleBuilder.simpleSchedule())
                .build();
    }
}