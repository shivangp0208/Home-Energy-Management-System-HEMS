package com.hems.project.virtual_power_plant.controller;

<<<<<<< HEAD:virtual-power-plant/src/main/java/com/hems/project/virtual_power_plant/controller/VppController.java
import com.hems.project.virtual_power_plant.Config.MessagingConfig;
import com.hems.project.virtual_power_plant.dto.DocumentVerificationDto;
import com.hems.project.virtual_power_plant.dto.VppUpdateRequestDto;
import com.hems.project.virtual_power_plant.dto.VppUpdateResponseDto;
import com.hems.project.virtual_power_plant.entity.Vpp;
import com.hems.project.virtual_power_plant.entity.VppDocumentType;
import com.hems.project.virtual_power_plant.service.SupabaseStorageService;
import com.hems.project.virtual_power_plant.service.VppDocumentService;
import com.project.hems.hems_api_contracts.contract.email.AttachmentDto;
=======
import com.hems.project.Virtual_Power_Plant.Config.MessagingConfig;
import com.hems.project.Virtual_Power_Plant.dto.DocumentVerificationDto;
import com.hems.project.Virtual_Power_Plant.dto.ImageResponseDto;
import com.hems.project.Virtual_Power_Plant.dto.VppUpdateRequestDto;
import com.hems.project.Virtual_Power_Plant.dto.VppUpdateResponseDto;
import com.hems.project.Virtual_Power_Plant.entity.Vpp;
import com.hems.project.Virtual_Power_Plant.entity.VppDocumentType;
import com.hems.project.Virtual_Power_Plant.service.SupabaseStorageService;
import com.hems.project.Virtual_Power_Plant.service.VppDocumentService;
>>>>>>> j-feature:Virtual-Power-Plant/src/main/java/com/hems/project/Virtual_Power_Plant/controller/VppController.java
import com.project.hems.hems_api_contracts.contract.email.EmailEventDto;
import com.project.hems.hems_api_contracts.contract.vpp.SignalForImport;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
<<<<<<< HEAD:virtual-power-plant/src/main/java/com/hems/project/virtual_power_plant/controller/VppController.java

import com.hems.project.virtual_power_plant.service.VppService;


=======
import com.hems.project.Virtual_Power_Plant.service.VppService;
>>>>>>> j-feature:Virtual-Power-Plant/src/main/java/com/hems/project/Virtual_Power_Plant/controller/VppController.java
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;
import java.nio.file.AccessDeniedException;
import java.util.*;
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

    @PreAuthorize("hasAuthority('vpp:write')")
    @PostMapping("/send-requirement")
    public ResponseEntity<Map<String, Object>> sendSignalForImport(
            @RequestBody SignalForImport signalForImport) {

        log.info("sending import signal for region {}", signalForImport.getRegionName());

        vppService.importPower(signalForImport);

        return ResponseEntity.ok(
                Map.of(
                        "message", "Signal sent successfully",
                        "data", "SUCCESS"
                )
        );
    }


    //TODO:-
    //jyare user signup and login thayy with role vpp then we make emtpy vpp entity and 
    //then ene dashboard mathi fill karvsu later ene jyare fill karvi hoy detail
    //and when vpp detail fill kare like location,location photo and all then we put this into ai 
    //and then we like send this to vpp manager.. and e verify karse 



    //find vpp by vppID
    @PreAuthorize("hasAuthority('vpp:read')")
    @GetMapping("/{vppId}")
    public ResponseEntity<Map<String, Object>> fetchVpp(@PathVariable UUID vppId) {

        log.info("fetching vpp with id {}", vppId);

        Vpp vpp = vppService.fetchVpp(vppId);

        return ResponseEntity.ok(
                Map.of(
                        "message", "Fetched successfully",
                        "data", vpp
                )
        );
    }

    //create vpp when use assign role to vpp then by default we create empty vpp raw in database
    //so aa eno endpoint che
    @PreAuthorize("hasAuthority('vpp:write')")
    @PostMapping("/create-vpp")
    public ResponseEntity<Map<String, Object>> createVpp(@AuthenticationPrincipal Jwt jwt) {

        String email = jwt.getClaimAsString("http://hems.com/email");
        String authId = jwt.getSubject();

        log.info("creating vpp for user {}", email);

        UUID vppId = vppService.createVpp(authId, email);

        return ResponseEntity.ok(
                Map.of(
                        "message", "VPP created successfully",
                        "data", vppId
                )
        );
    }

    @PreAuthorize("hasAuthority('vpp:write')")
    @PatchMapping("/update-vpp")
    public ResponseEntity<Map<String, Object>> updateVpp(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody VppUpdateRequestDto dto) {

        String email = jwt.getClaimAsString("http://hems.com/email");

        log.info("updating vpp for user {}", email);

        VppUpdateResponseDto response = vppService.updateVppV2(email, dto);

        return ResponseEntity.ok(
                Map.of(
                        "message", "VPP updated successfully",
                        "data", response
                )
        );
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
    public ResponseEntity<Map<String, Object>> deleteVpp(@PathVariable UUID vppId) {

        log.info("deleting vpp with id {}", vppId);

        boolean deleted = vppService.deleteVpp(vppId);

        if (deleted) {
            return ResponseEntity.ok(
                    Map.of(
                            "message", "VPP deleted successfully",
                            "data", vppId
                    )
            );
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    Map.of(
                            "message", "VPP not found",
                            "data", vppId
                    )
            );
        }
    }
    //todo:-
    //1.send verification document tyare verification submission time and e badhu update no call karbo

    //get all vpp details
    @PreAuthorize("hasAnyAuthority('admin:read','admin:write')")
    @GetMapping("/all")
    public ResponseEntity<Map<String, Object>> getAllVpps() {

        log.info("fetching all vpps");

        List<Vpp> vpps = vppService.getAllVpps();

        return ResponseEntity.ok(
                Map.of(
                        "message", "Fetched successfully",
                        "data", vpps,
                        "count", vpps.size()
                )
        );
    }

    //upload document to verify so we put in the s3 bucket and ek controller vpp manager ma banai daisu ke
    //vpp manager /list-all-pending document so vpp manager ne link badhi ayi jase je document verify karvana baki che e
    //and verify thai gaya hase ene Mark kari daisu

    //upload-document
    //RBAC karvu ..
    @PreAuthorize("hasAuthority('SCOPE_vpp:write')")
    @PostMapping(value = "/upload/{vppId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, Object>> uploadDocuments(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable UUID vppId,
            @RequestParam("documentType") VppDocumentType documentType,
            @RequestParam("file") List<MultipartFile> files
    ) {
        //ek check muleu che if user jode role vpp:write no hoy toh e pan aa eidpoint access kari sakse...
        //so we use here extra check in ownership

        if (files == null || files.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Files are required"));
        }
        String email = jwt.getClaimAsString("http://hems.com/email");

        try {
            vppService.validateOwnership(vppId, email);
        } catch (AccessDeniedException e) {
            throw new RuntimeException("You are not owner of this VPP");
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

        try {
            rabbitTemplate.convertAndSend(
                    MessagingConfig.MAIN_EXCHANGE,
                    MessagingConfig.ROUTING_KEY,
                    dto
            );
        } catch (Exception e) {
            log.error("Failed to send email event", e);
        }

        log.info("mail send successfully {} ",dto);



        return ResponseEntity.ok(resp);
    }


    //get all imageLink with vppId
    @PreAuthorize("hasAuthority('vpp:read')")
    @GetMapping("/fetch-image/{vppId}")
    public ResponseEntity<Map<String, Object>> getAllImages(@PathVariable UUID vppId){

        log.info("fetching images for vppId {}", vppId);

        List<ImageResponseDto> images =
                supabaseStorageService.getAllImagesFromVppId(vppId);

        Map<String, Object> response = Map.of(
                "message", "Fetch successfully",
                "count", images.size(),
                "images", images
        );

        return ResponseEntity.ok(response);
    }

    //delete all image form storage based on vppId
    @PreAuthorize("hasAuthority('vpp:write')")
    @DeleteMapping("/{vppId}/images")
    public ResponseEntity<Map<String, Object>> deleteAllImages(@PathVariable UUID vppId) {

        log.info("deleting all images for vppId {}", vppId);

        supabaseStorageService.deleteAllFilesByVppId(vppId);

        return ResponseEntity.ok(
                Map.of(
                        "message", "Deleted all images successfully",
                        "data", vppId
                )
        );
    }

    @PreAuthorize("hasAuthority('admin:write')")
    @PatchMapping("/update-status/{vppId}")
    public ResponseEntity<Map<String, Object>> updateStatus(
            @PathVariable UUID vppId,
            @RequestBody DocumentVerificationDto dto) {

        log.info("updating verification status for vppId {}", vppId);

        if (dto == null) {
            return ResponseEntity.badRequest().body(
                    Map.of("message", "Invalid request")
            );
        }

        vppDocumentService.updateVerificationStatus(vppId, dto);

        return ResponseEntity.ok(
                Map.of(
                        "message", "Verification status updated successfully",
                        "data", vppId
                )
        );
    }

    private String buildBody(UUID vppId, VppDocumentType type, Map<String,Object> resp) {
        Object urlsObj = resp.get("imageUrls");

        List<String> urls = urlsObj instanceof List<?>
                ? (List<String>) urlsObj
                : List.of();
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








//    @PostMapping("/create-dispatch-event/{groupId}")
//    public DispatchEvent createDispatchEvent(@RequestBody DispatchEvent dispatchEvent) {
//
//        return dispatchEvent;
//    }
    

}