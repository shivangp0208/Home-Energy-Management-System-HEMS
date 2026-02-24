//package com.project.hems.api_gateway_hems.config;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.data.redis.connection.RedisConnectionFactory;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.data.redis.serializer.StringRedisSerializer;
//
//
//@Configuration
//public class RedisConfig {
//
//    @Bean
//    public RedisTemplate redisTemplate(RedisConnectionFactory factory){
//
//        RedisTemplate redisTemplate=new RedisTemplate<>();
//
//        redisTemplate.setConnectionFactory(factory);
//
//        redisTemplate.setKeySerializer(new StringRedisSerializer());
//        redisTemplate.setValueSerializer(new StringRedisSerializer());
//
//        return redisTemplate;
//    }
//
//
//
//}
//
//
//// new device login
////session:{sub} -> {sid, deviceId, ip, userAgent, lastLoginAt}
///*
//
//{
//        "sid": "6f0b28a6-2fd0-4b4a-9e4b-4ac6d6a5d6b1",
//        "deviceId": "dvc_8a91c3f2-44de-4f90-aec1-11ab229933dd",
//        "ip": "49.36.182.14",
//        "userAgent": "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 Chrome/122.0.0.0 Safari/537.36",
//        "lastLoginAt": "2026-02-21T19:22:44.123Z"
//        }
//sid	Current     valid session id (UUID stored inside JWT custom claim)
//deviceId	    Stable id from frontend (stored in localStorage)
//IP               ip at login time (from request)
//userAgent	    Browser/device info
//lastLoginAt	    Timestamp of login
//
//
//KEY:
//session:auth0|abc123
//
//VALUE:
//{"sid":"6f0b28a6-2fd0-4b4a-9e4b-4ac6d6a5d6b1","deviceId":"dvc_8a91c3f2-44de-4f90-aec1-11ab229933dd","ip":"49.36.182.14","userAgent":"Mozilla/5.0...","lastLoginAt":"2026-02-21T19:22:44.123Z"}
// */