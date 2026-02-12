package com.project.hems.dispatch_manager_service.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.hems.dispatch_manager_service.service.DispatchEventProducer;
import com.project.hems.hems_api_contracts.contract.dispatch.DispatchEvent;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "dispatch manager", description = "endpoints to manage dispatch commands")
@Slf4j
@RestController
@RequestMapping("/api/v1/dispatch")
@RequiredArgsConstructor
public class DispatchController {

    private final DispatchEventProducer dispatchEventProducer;

    @Operation(
            summary = "send dispatch command",
            description = "send a dispatch event to the dispatch manager for processing"
    )
    @ApiResponse(responseCode = "200", description = "dispatch command sent successfully")
    @ApiResponse(responseCode = "400", description = "invalid dispatch event payload")
    @PostMapping("/send-command")
    public void sendDispatchCommand(@RequestBody @Valid DispatchEvent dispatchEvent) {
        log.info("sendDispatchCommand: sending post request to send the dispatch command");
        dispatchEventProducer.sendDispatchCommands(dispatchEvent);
    }

}
