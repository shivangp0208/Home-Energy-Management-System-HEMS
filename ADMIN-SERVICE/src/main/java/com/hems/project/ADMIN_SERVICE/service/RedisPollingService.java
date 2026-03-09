package com.hems.project.ADMIN_SERVICE.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

//this service check every 5 sec from redis is there is any site is hearbeat missing
//if any site heartbaeat is missing then we create ticket and also event ..
/*
key->value
hb:missing:site:{siteId} → value = lastSeen timestamp or reason
hb:missing:vpp:{vppId} → value = lastSeen timestamp or reason
 */

@RequiredArgsConstructor
@Service
public class RedisPollingService {

    private final RedisTemplate<String,String> redisTemplate;

    @Scheduled(fixedRate = 5000)
    public void checkHeartBeatOfSitesAndVpp(){
        //go to redis
        //redisTemplate




        //and get keys list traves through anf make dedupeKey and create event

    }



}
