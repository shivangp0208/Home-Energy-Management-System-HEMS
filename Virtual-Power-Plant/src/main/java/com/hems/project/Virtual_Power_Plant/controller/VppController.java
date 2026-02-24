package com.hems.project.Virtual_Power_Plant.controller;

import com.hems.project.Virtual_Power_Plant.Config.MessagingConfig;
import com.hems.project.Virtual_Power_Plant.dto.DocumentVerificationDto;
import com.hems.project.Virtual_Power_Plant.dto.VppUpdateRequestDto;
import com.hems.project.Virtual_Power_Plant.dto.VppUpdateResponseDto;
import com.hems.project.Virtual_Power_Plant.entity.Vpp;
import com.hems.project.Virtual_Power_Plant.entity.VppDocumentType;
import com.hems.project.Virtual_Power_Plant.service.SupabaseStorageService;
import com.hems.project.Virtual_Power_Plant.service.VppDocumentService;
import com.project.hems.hems_api_contracts.contract.email.AttachmentDto;
import com.project.hems.hems_api_contracts.contract.email.EmailEventDto;
import com.project.hems.hems_api_contracts.contract.dispatch.DispatchEvent;
import com.project.hems.hems_api_contracts.contract.vpp.SignalForImport;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.PostConstruct;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.hems.project.Virtual_Power_Plant.service.VppService;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


//aa frontend mate che hamda emj lakhi rakhyu che frontend diff port per run kare and backend diff port per so ena mate..
@CrossOrigin("*")
@Tag(name = "Vpp controller",description = "api for send vpp requirement signal")
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/vpp")
@RefreshScope
public class VppController {

    private final VppService vppService;
    private final SupabaseStorageService supabaseStorageService;
    private final VppDocumentService vppDocumentService;
    private final RabbitTemplate rabbitTemplate;

    @Value("${jills.patel}")
    public String role;

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

    @GetMapping("/bus")
    public void check(){
        System.out.println("keyyyy is "+role);
    }

    //find vpp by vppID
    @GetMapping("/{vppId}")
    public ResponseEntity<Vpp> fetchVpp(@PathVariable UUID vppId){
         Vpp vpp = vppService.fetchVpp(vppId);
         return new ResponseEntity<>(vpp,HttpStatus.OK);
    }

    //create vpp when use assign role to vpp then by default we create empty vpp raw in database
    //so aa eno endpoint che
    @PreAuthorize("hasAuthority('vpp:write')")
    @PostMapping("/create-vpp")
    public ResponseEntity<Map<String, Object>> createVpp(@AuthenticationPrincipal Jwt jwt){
        if (jwt == null) {
            throw new RuntimeException("JWT not found. Make sure you send an Authorization header.");
        }
        String email=jwt.getClaimAsString("http://hems.com/email");
        String authId= jwt.getSubject();
         UUID vppId = vppService.createVpp(authId,email);
         Map<String,Object> response=new HashMap<>();
         response.put("vppId",vppId);
         response.put("message","blank raw is created for this vpp");
         return new ResponseEntity<>(response, HttpStatus.OK);
    }

    //@PreAuthorize("hasAuthority('vpp:write')")
    @PatchMapping("/update-vpp")
    public ResponseEntity<VppUpdateResponseDto> updateVpp(@AuthenticationPrincipal Jwt jwt,@RequestBody VppUpdateRequestDto dto){
        String email=jwt.getClaimAsString("http://hems.com/email");
         //VppUpdateResponseDto vppUpdateResponseDto = vppService.updateVpp(email,dto);
         VppUpdateResponseDto vppUpdateResponseDto = vppService.updateVppV2(email, dto);
        //todo:-
        //mail send kari sakiee ke aa vastu tame update kari che em..
         return new ResponseEntity<>(vppUpdateResponseDto,HttpStatus.OK);
    }

    //@PreAuthorize("hasAuthority('vpp:write')")
    /*
    @PutMapping("/update-vpp-put")
    public ResponseEntity<VppUpdateResponseDto> updateVppPut(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody VppUpdateRequestDto dto) {

        String email = jwt.getClaimAsString("http://hems.com/email");
        VppUpdateResponseDto vppUpdateResponseDto = vppService.updateVpp(email, dto);
        return new ResponseEntity<>(vppUpdateResponseDto, HttpStatus.OK);
    }

     */


    //todo:-
    //1.delete vpp so e controller mate role SUPER_ADMIN OR ADMIN hovo joiee toh j e delete kari sakse..
    @PreAuthorize("hasAuthority('admin:write')")
    @DeleteMapping("/delete-vpp/{vppId}")
    public ResponseEntity<Map<String,Object>> deleteVpp(
            @PathVariable UUID vppId
            ){
         boolean flag = vppService.deleteVpp(vppId);
         //todo:-
        //ahiya bhi mail send kari sakiee ke tame delete thai gaya cho and ane delete karyu che so
        //and we find kone delete karyu based on the Jwt token
        //contact them if any further enquiry...
        Map<String,Object> response = new HashMap<>();
        response.put("vppId", vppId);

        if (flag) {
            response.put("message", "VPP is deleted successfully");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            response.put("message", "VPP not found or already deleted");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }
    //todo:-
    //1.send verification document tyare verification submission time and e badhu update no call karbo

    //get all vpp details
    @PreAuthorize("hasAnyAuthority('admin:read','admin:write')")
    @GetMapping("/all")
    public ResponseEntity<List<Vpp>> getAllVpps() {
        List<Vpp> vpps = vppService.getAllVpps();
        if (vpps.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(vpps, HttpStatus.OK);
    }

    //upload document to verify so we put in the s3 bucket and ek controller vpp manager ma banai daisu ke
    //vpp manager /list-all-pending document so vpp manager ne link badhi ayi jase je document verify karvana baki che e
    //and verify thai gaya hase ene Mark kari daisu

    //upload-document
    //RBAC karvu ..
    @PostMapping(value = "/upload/{vppId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, Object>> uploadDocuments(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable UUID vppId,
            @RequestParam("documentType") VppDocumentType documentType,
            @RequestParam("file") List<MultipartFile> files
    ) {
        //ek check muleu che if user jode role vpp:write no hoy toh e pan aa eidpoint access kari sakse...
        //so we use here extra check in ownership
        String email = jwt.getClaimAsString("http://hems.com/email");

        try {
            vppService.validateOwnership(vppId, email);
        } catch (AccessDeniedException e) {
            throw new RuntimeException(e);
        }

        Map<String, Object> resp = vppDocumentService.uploadDocuments(vppId, files, documentType);


        //TODO:-
        //mail send karvo through rabbitMq..
        EmailEventDto dto = EmailEventDto.builder()
                .to(email)
                .subject("[HEMS] Documents uploaded successfully")
                .body(buildBody(vppId, documentType, resp))  // resp has URLs
                .html(false)
                .eventType("DOCUMENT_UPLOADED")
                .build();

        rabbitTemplate.convertAndSend(
                MessagingConfig.MAIN_EXCHANGE,
                MessagingConfig.ROUTING_KEY,
                dto
        );

        log.info("mail send successfully {} ",dto);



        return ResponseEntity.ok(resp);
    }


    //get all imageLink with vppId
    @GetMapping("/fetch-image/{vppId}")
    public ResponseEntity<Map<String,Object>> getAllImages(@PathVariable UUID vppId){
         List<Map<String, String>> allImagesFromVppId = supabaseStorageService.getAllImagesFromVppId(vppId);

         Map<String, Object> response = Map.of(
                "message", "Fetch successfully",
                "count", allImagesFromVppId.size(),
                "images", allImagesFromVppId
        );
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    //delete all image form storage based on vppId
    @DeleteMapping("/{vppId}/images")
    public ResponseEntity<Map<String, Object>> deleteAllImages(@PathVariable UUID vppId) {
        supabaseStorageService.deleteAllFilesByVppId(vppId);
        return ResponseEntity.ok(Map.of("message", "Deleted all files for vppId", "vppId", vppId));
    }

    @PreAuthorize("hasAuthority('admin:write')")
    @PatchMapping("/update-status/{vppId}")
    public ResponseEntity<Map<String, Object>> updateStatus(
            @PathVariable UUID vppId,
            @RequestBody DocumentVerificationDto dto) {

        vppDocumentService.updateVerificationStatus(vppId, dto);

        return ResponseEntity.ok(Map.of(
                "message", "Verification status updated successfully"
        ));
    }

    private String buildBody(UUID vppId, VppDocumentType type, Map<String,Object> resp) {
        List<String> urls = (List<String>) resp.getOrDefault("imageUrls", List.of());

        StringBuilder sb = new StringBuilder();
        sb.append("Your documents are uploaded and are under review.\n\n");
        sb.append("VPP ID: ").append(vppId).append("\n");
        sb.append("Document Type: ").append(type).append("\n\n");
        sb.append("Uploaded files:\n");

        for (int i = 0; i < urls.size(); i++) {
            sb.append(i + 1).append(". ").append(urls.get(i)).append("\n");
        }
        return sb.toString();
    }








    @PostMapping("/create-dispatch-event/{groupId}")
    public DispatchEvent createDispatchEvent(@RequestBody DispatchEvent dispatchEvent) {
        
        return dispatchEvent;
    }
    

}