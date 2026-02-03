package com.project.hems.dispatch_manager_service.web.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.hems.dispatch_manager_service.model.DispatchEvent;
import com.project.hems.dispatch_manager_service.service.DispatchEventProducer;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Slf4j
@RestController
@RequestMapping("/api/v1/dispatch")
@RequiredArgsConstructor
public class DispatchController {

    private final DispatchEventProducer dispatchEventProducer;

    @PostMapping("/send-command")
    public void sendDispatchCommand(@RequestBody @Valid DispatchEvent dispatchEvent) {
        log.info("sendDispatchCommand: sending post request to send the dispatch command");
        dispatchEventProducer.sendDispatchCommands(dispatchEvent);
    }

}
