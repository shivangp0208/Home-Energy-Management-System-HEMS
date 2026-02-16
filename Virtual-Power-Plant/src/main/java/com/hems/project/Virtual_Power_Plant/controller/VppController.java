package com.hems.project.Virtual_Power_Plant.controller;

import com.project.hems.hems_api_contracts.contract.vpp.SignalForImport;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import com.hems.project.Virtual_Power_Plant.service.VppService;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

//aa frontend mate che hamda emj lakhi rakhyu che frontend diff port per run kare and backend diff port per so ena mate..
@CrossOrigin("*")
@Tag(name = "Vpp controller",description = "api for send vpp requirement signal")
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/vpp")
public class VppController {

    private final VppService vppService;

    @Operation(
            summary = "send signal to import power",
            description = "receives a request to import power from a specific region and forwards the signal to the VPP service for processing."
    )
    @PostMapping("/send-requirement")
    public String sendSignalForImport(@RequestBody SignalForImport signalForImport){
    log.info("received request : send signal to import power from region = {} ",signalForImport.getRegionName());
        vppService.importPower(signalForImport);
        return "send import details to vpp service";
    }

    //TODO:-
    //jyare user signup and login thayy with role vpp then we make emtpy vpp entity and 
    //then ene dashboard mathi fill karvsu later ene jyare fill karvi hoy detail
    //and when vpp detail fill kare like location,location photo and all then we put this into ai 
    //and then we like send this to vpp manager.. and e verify karse 
    
   
}