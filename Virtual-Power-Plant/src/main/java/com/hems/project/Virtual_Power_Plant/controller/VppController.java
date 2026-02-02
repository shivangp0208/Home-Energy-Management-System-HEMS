package com.hems.project.Virtual_Power_Plant.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.hems.project.Virtual_Power_Plant.service.VppService;
import com.hems.project.hems_api_contracts.contract.site.SignalForImport;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/vpp")
public class VppController {

    private final VppService vppService;

    @PostMapping("/send-requirement")
    public String sendSignalForImport(@RequestBody SignalForImport signalForImport){
        vppService.importPower(signalForImport);
        return "send import details to vpp service";
    }
}
