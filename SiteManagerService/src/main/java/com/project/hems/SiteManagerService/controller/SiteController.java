package com.project.hems.SiteManagerService.controller;

import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.PropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.project.hems.SiteManagerService.config.MessagingConfig;
import com.project.hems.SiteManagerService.dto.CursorSiteResponse;
import com.project.hems.SiteManagerService.service.EmailServiceImpl;
import com.project.hems.SiteManagerService.service.SiteServiceImpl;
import com.project.hems.SiteManagerService.util.EmailTemplateUtil;
import com.project.hems.hems_api_contracts.contract.email.MailSuccessfullRequestDto;
import com.project.hems.hems_api_contracts.contract.email.MailSuccessfullResponseDto;
import com.project.hems.hems_api_contracts.contract.program.Program;
import com.project.hems.hems_api_contracts.contract.site.SiteDto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/site")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "site apis", description = "read, create, update, delete, and manage site details")
public class SiteController {

    private final SiteServiceImpl siteService;
    private final EmailServiceImpl emailService;
    private final RabbitTemplate rabbitTemplate;

    @Transactional
    @Operation(summary = "create site", description = "create a new site with site details and return the created site")
    @ApiResponse(responseCode = "201", description = "site created successfully")
    @PostMapping("/create-site")
    public ResponseEntity<MappingJacksonValue> createSite(
            @RequestBody @Valid SiteDto siteRequestDto,
            @AuthenticationPrincipal Jwt jwt) {

        log.info("POST req to create site with site detail = {}", siteRequestDto);

        if (jwt != null) {
            log.debug("Authenticated JWT subject={}", jwt.getSubject());
            log.debug("Authenticated JWT claims={}", jwt.getClaims());
        }

        String userSub = jwt.getSubject();
        String email = jwt.getClaim("http://hems.com/email");

        log.debug("Creating site for userSub={}, email={}", userSub, email);

        SiteDto site = siteService.createSite(siteRequestDto, userSub);

        System.out.println("email"+email);

        MailSuccessfullRequestDto dto =
                EmailTemplateUtil.buildSiteCreatedMail(
                        email,
                        String.valueOf(site.getSiteId()),
                        userSub
                );

        //System.out.println("site id"+String.valueOf(site.getSiteId()));

        //emailService.sendMail(dto);
        log.info("successfully send mail dto to Queue");
        rabbitTemplate.convertAndSend(MessagingConfig.EXCHANGE,MessagingConfig.ROUTING_KEY,dto);


        log.info("Site created successfully. siteId={}, userSub={}",
                site.getSiteId(), userSub);

        PropertyFilter filter=SimpleBeanPropertyFilter.filterOutAllExcept(
                "siteId",
                "isActive"
        );

        //here we define filter je apdne joiee che response ma
        FilterProvider provider = new SimpleFilterProvider()
                .addFilter("siteFilter",filter);


        MappingJacksonValue mapping=new MappingJacksonValue(site);

        mapping.setFilters(provider);

        return new ResponseEntity<>(mapping, HttpStatus.CREATED);
    }

    @Operation(summary = "fetch site by id", description = "retrieve a single site by its id")
    @ApiResponse(responseCode = "200", description = "site fetched successfully")
    @ApiResponse(responseCode = "404", description = "site not found")
    @GetMapping("/fetch-site-by-id/{siteId}")
    public ResponseEntity<SiteDto> getSite(@PathVariable(name = "siteId", required = true) UUID siteId) {

        log.info("GET req to fetch site by id with siteId={}", siteId);

        SiteDto site = siteService.fetchSiteById(siteId);

        log.debug("Fetched site details. siteId={}", siteId);

        return new ResponseEntity<>(site, HttpStatus.OK);
    }

    @Operation(summary = "fetch all sites", description = "retrieve a list of all sites")
    @ApiResponse(responseCode = "200", description = "sites fetched successfully")
    @GetMapping("/fetch-all-site")
    public ResponseEntity<List<SiteDto>> getAllSites() {

        log.info("GET req to fetch all site details");

        List<SiteDto> sites = siteService.fetchAllSite();

        log.debug("Fetched all sites. count={}", sites.size());

        return new ResponseEntity<>(sites, HttpStatus.OK);
    }

    @Operation(summary = "fetch all sites v2", description = "retrieve all sites in site response dto format")
    @ApiResponse(responseCode = "200", description = "sites fetched successfully")
    @GetMapping("/fetch-all-site/v2")
    public ResponseEntity<List<SiteDto>> getAllSitesV2() {

        log.info("GET req to fetch all site details v2");

        List<SiteDto> sites = siteService.fetchAllSiteV2();

        log.debug("Fetched all sites v2. count={}", sites.size());

        return new ResponseEntity<>(sites, HttpStatus.OK);
    }

    // this is normal offset based pagination
    @Operation(summary = "fetch all sites with pagination", description = "retrieve sites with offset-based pagination")
    @ApiResponse(responseCode = "200", description = "sites fetched successfully")
    @GetMapping("/fetch-all-site/v2/pagging")
    public ResponseEntity<Page<SiteDto>> getAllSitesV2WithPagging(
            @RequestParam(name = "offset", defaultValue = "0") int offset,
            @RequestParam(name = "pageSize", defaultValue = "2") int pageSize) {

        log.info("GET req to fetch all site details v2 with pagging");

        Page<SiteDto> allSiteV2WithPagination = siteService.findAllSiteV2WithPagination(offset, pageSize);
        ;
        return new ResponseEntity<>(allSiteV2WithPagination, HttpStatus.OK);
    }

    // this is normal offset based pagination
    @Operation(summary = "fetch all sites with pagination and sorting", description = "retrieve sites with offset-based pagination and sorting by field")
    @ApiResponse(responseCode = "200", description = "sites fetched successfully")
    @GetMapping("/fetch-all-site/v2/pagging-sorting")
    public ResponseEntity<Page<SiteDto>> getAllSitesV2WithPaggingAndSorting(
            @RequestParam(name = "offset", defaultValue = "0") int offset,
            @RequestParam(name = "pageSize", defaultValue = "2") int pageSize,
            @RequestParam(name = "field") String field) {

        log.info("GET req to fetch all site details v2 with pagging and sorting");

        Page<SiteDto> allSiteV2WithPagination = siteService.findAllSiteV2WithPaginationAndSorting(offset,
                pageSize, field);
        return new ResponseEntity<>(allSiteV2WithPagination, HttpStatus.OK);
    }

    // here we implement cursor based pagination
    @Operation(summary = "fetch all sites with cursor-based pagination", description = "retrieve sites using cursor-based pagination for large data sets")
    @ApiResponse(responseCode = "200", description = "sites fetched successfully")
    @GetMapping("/fetch-all-site/v2/cursor")
    public ResponseEntity<CursorSiteResponse<SiteDto>> getAllSitesV2WithPaggingAndSorting(
            @RequestParam(required = false) UUID cursor,
            @RequestParam(defaultValue = "10") int size) {

        log.info("GET req to fetch all site details v2 with pagging and sorting");

        CursorSiteResponse<SiteDto> sites = siteService.getSites(cursor, size);
        ;
        return new ResponseEntity<>(sites, HttpStatus.OK);
    }

    // @GetMapping("/fetch-all-site") // public
    // CompletableFuture<ResponseEntity<List<SiteDto>>> getAllSites() // {
    // // return siteService.fetchAllSites().thenApply(ResponseEntity::ok); // }
    @Operation(summary = "fetch site by region", description = "retrieve a list of sites filtered by city/region")
    @ApiResponse(responseCode = "200", description = "sites fetched successfully")
    @GetMapping("/fetch-site-by-region/{city}")
    public ResponseEntity<List<SiteDto>> getAllSiteByRegion(
            @PathVariable String city) {

        log.info("GET req to fetch site by region with city={}", city);

        List<SiteDto> sites = siteService.fetchSiteByRegion(city);

        log.debug("Fetched sites by region. city={}, count={}", city, sites.size());

        return new ResponseEntity<>(sites, HttpStatus.OK);
    }

    @GetMapping("/fetch-site-by-program/{programId}")
    public ResponseEntity<List<SiteDto>> getAllSitesInProgram(
            @PathVariable(name = "programId", required = true) UUID programId) {

        log.info("GET req to fetch site by programId={}", programId);

        List<SiteDto> sites = siteService.fetchSiteByProgram(programId);

        log.debug("Fetched sites enrolled in program with programId={}, count={}", programId, sites.size());

        return new ResponseEntity<>(sites, HttpStatus.OK);
    }

    @Operation(summary = "fetch all available regions", description = "retrieve a list of all distinct regions where sites are available")
    @ApiResponse(responseCode = "200", description = "regions fetched successfully")
    @GetMapping("/fetch-all-region")
    public ResponseEntity<List<String>> fethcAllAvailableRegion() {
        List<String> fetchAllRegion = siteService.fetchAllRegion();
        return new ResponseEntity(fetchAllRegion, HttpStatus.OK);
    }

    @PatchMapping("/add-program/{siteId}")
    public SiteDto addProgramInSite(
            @PathVariable(name = "siteId", required = true) UUID siteId,
            @RequestBody @Valid Program program) {
        log.info("PATCH Req to add program in a site with site id = " + siteId);
        return null;
    }

    // jyare vpp approve kari dey tyare apde site ni under e vpp ni id nakhi daisu
    // @Operation(summary = "assign vpp to site", description = "assign a VPP to a
    // site after approval")
    // @ApiResponse(responseCode = "200", description = "vpp assigned successfully")
    // @PostMapping("/{siteId}/assign-vpp")
    // public ResponseEntity<EnrollSiteInVppResponse> assignVppToSite(
    // @PathVariable("siteId") UUID siteId,
    // @RequestBody @Valid AssignVppRequest request
    // ) {
    // EnrollSiteInVppResponse resp = siteService.assignSiteToVpp(siteId, request);
    // return ResponseEntity.ok(resp);
    // }

    // prorgam enroll thayy tyare apde user ne e program ma nakhi daisu
    @Operation(summary = "add program to site", description = "add a program to the site after user enrollment")
    @ApiResponse(responseCode = "200", description = "program added successfully")
    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/{site-id}/add-program")
    public SiteDto addPrograminSite(
            @PathVariable("site-id") UUID siteId,
            @RequestBody Program program) {
        log.info("PUT req to add program = {} in site with siteId = {}", program, siteId);
        return siteService.enrollSiteInProgram(siteId, program);
    }

    //send email if site is created successfully
    @PutMapping("/send-email")
    public ResponseEntity<MailSuccessfullResponseDto> sendMail(@RequestBody MailSuccessfullRequestDto dto){
         ResponseEntity<MailSuccessfullResponseDto> mailSuccessfullResponseDtoResponseEntity = emailService.sendMail(dto);
         return mailSuccessfullResponseDtoResponseEntity;
    }

    //check siteIs exists or not based on siteId
    @PostMapping("/check-site-available/{siteId}")
    public ResponseEntity<Boolean> checkSiteIsAvailableOtNot(@PathVariable UUID siteId){
         Boolean flag = siteService.checkSiteAvailable(siteId);
         if(flag) {
             return new ResponseEntity<>(flag, HttpStatus.OK);
         }else{
             return new ResponseEntity<>(flag, HttpStatus.NOT_FOUND);
         }

    }



}
