// package com.hems.project.Vpp_Manager.controller;

// import java.net.http.HttpClient;

// import org.apache.hc.core5.http.HttpStatus;
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.PostMapping;
// import org.springframework.web.bind.annotation.RequestBody;
// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RestController;

// import com.hems.project.Vpp_Manager.entity.VirtualPowerPlant;
// import com.hems.project.Vpp_Manager.service.VirtualPowerPlantService;

// import lombok.RequiredArgsConstructor;

// @RequiredArgsConstructor
// @RestController
// @RequestMapping("/test")
// public class VirtualPowerPlantController {

//     private final VirtualPowerPlantService virtualPowerPlantService;
    
//     @PostMapping
//     public ResponseEntity<String> check(@RequestBody VirtualPowerPlant virtualPowerPlant){
//     String saveVppData = virtualPowerPlantService.saveVppData(virtualPowerPlant);
//         return ResponseEntity.ok(saveVppData);
//     }

// }
