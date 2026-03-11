package com.project.hems.SiteManagerService.controller;

import com.project.hems.SiteManagerService.config.MessagingConfig;
import com.project.hems.SiteManagerService.dto.CursorSiteResponse;
import com.project.hems.SiteManagerService.service.impl.EmailServiceImpl;
import com.project.hems.SiteManagerService.service.impl.SiteServiceImpl;
import com.project.hems.SiteManagerService.util.EmailTemplateUtil;
import com.project.hems.hems_api_contracts.contract.email.EmailEventDto;
import com.project.hems.hems_api_contracts.contract.email.MailSuccessfullRequestDto;
import com.project.hems.hems_api_contracts.contract.email.MailSuccessfullResponseDto;
import com.project.hems.hems_api_contracts.contract.program.ProgramFeignDto;
import com.project.hems.hems_api_contracts.contract.site.SiteDto;
import com.project.hems.hems_api_contracts.contract.site.SiteReqDto;
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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
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
    public ResponseEntity<SiteDto> createSite(
            @RequestParam(name = "includeProgram", required = false, defaultValue = "false") boolean includeProgram,
            @RequestBody @Valid SiteReqDto siteRequestDto,
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

        System.out.println("email" + email);

        MailSuccessfullRequestDto oldDto = EmailTemplateUtil.buildSiteCreatedMail(
                email,
                String.valueOf(site.getSiteId()),
                userSub);

        EmailEventDto eventDto = EmailEventDto.builder()
                .to(oldDto.getTo())
                .subject(oldDto.getSubject())
                .body(oldDto.getBody())
                .html(false)
                .eventType("SITE_CREATED")
                .build();

        // System.out.println("site id"+String.valueOf(site.getSiteId()));

        // emailService.sendMail(dto);
        log.info("successfully send mail dto to Queue");
        rabbitTemplate.convertAndSend(MessagingConfig.EXCHANGE, MessagingConfig.ROUTING_KEY, eventDto);

        log.info("Site created successfully. siteId={}, userSub={}",
                site.getSiteId(), userSub);

        return new ResponseEntity<>(site, HttpStatus.CREATED);
    }

    @Operation(summary = "fetch site by id", description = "retrieve a single site by its id")
    @ApiResponse(responseCode = "200", description = "site fetched successfully")
    @ApiResponse(responseCode = "404", description = "site not found")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/fetch-site-by-id/{siteId}")
    public SiteDto getSite(
            @PathVariable(name = "siteId", required = true) UUID siteId,
            @RequestParam(name = "includeProgram", required = false, defaultValue = "false") boolean includeProgram) {

        log.info("GET req to fetch site by id with siteId={}", siteId);

        log.debug("Fetched site details. siteId={}", siteId);
        return siteService.fetchSiteById(siteId, includeProgram);
    }

    @Operation(summary = "fetch all sites", description = "retrieve a list of all sites")
    @ApiResponse(responseCode = "200", description = "sites fetched successfully")
    @GetMapping("/fetch-all-site")
    public ResponseEntity<List<SiteDto>> getAllSites(
            @RequestParam(name = "includeProgram", required = false, defaultValue = "false") boolean includeProgram) {

        log.info("GET req to fetch all site details");

        List<SiteDto> sites = siteService.fetchAllSite();

        log.debug("Fetched all sites. count={}", sites.size());

        return new ResponseEntity<>(sites, HttpStatus.OK);
    }

    @Operation(summary = "fetch all sites v2", description = "retrieve all sites in site response dto format")
    @ApiResponse(responseCode = "200", description = "sites fetched successfully")
    @GetMapping("/fetch-all-site/v2")
    public ResponseEntity<List<SiteDto>> getAllSitesV2(
            @RequestParam(name = "includeProgram", required = false, defaultValue = "false") boolean includeProgram) {

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
            @RequestParam(name = "includeProgram", required = false, defaultValue = "false") boolean includeProgram,
            @RequestParam(name = "offset", defaultValue = "0") int offset,
            @RequestParam(name = "pageSize", defaultValue = "2") int pageSize) {

        log.info("GET req to fetch all site details v2 with pagging");

        Page<SiteDto> allSiteV2WithPagination = siteService.findAllSiteV2WithPagination(offset, pageSize);
        return new ResponseEntity<>(allSiteV2WithPagination, HttpStatus.OK);
    }

    // this is normal offset based pagination
    @Operation(summary = "fetch all sites with pagination and sorting", description = "retrieve sites with offset-based pagination and sorting by field")
    @ApiResponse(responseCode = "200", description = "sites fetched successfully")
    @GetMapping("/fetch-all-site/v2/pagging-sorting")
    public ResponseEntity<Page<SiteDto>> getAllSitesV2WithPaggingAndSorting(
            @RequestParam(name = "includeProgram", required = false, defaultValue = "false") boolean includeProgram,
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
    @ResponseStatus(code = HttpStatus.OK)
    @GetMapping("/fetch-all-site/v2/cursor")
    public CursorSiteResponse<SiteDto> getAllSitesV2WithCursor(
            @RequestParam(name = "includeProgram", required = false, defaultValue = "false") boolean includeProgram,
            @RequestParam(name = "cursor", required = false) UUID cursor,
            @RequestParam(name = "size", required = false, defaultValue = "10") int size) {

        log.info("GET req to fetch all site details v2 with pagging and sorting");

        return siteService.getSites(cursor, size);
    }

    // @GetMapping("/fetch-all-site") // public
    // CompletableFuture<ResponseEntity<List<SiteDto>>> getAllSites() // {
    // // return siteService.fetchAllSites().thenApply(ResponseEntity::ok); // }
    @Operation(summary = "fetch site by region", description = "retrieve a list of sites filtered by city/region")
    @ApiResponse(responseCode = "200", description = "sites fetched successfully")
    @GetMapping("/fetch-site-by-region/{city}")
    public ResponseEntity<List<SiteDto>> getAllSiteByRegion(
            @RequestParam(name = "includeProgram", required = false, defaultValue = "false") boolean includeProgram,
            @PathVariable(name = "city", required = true) String city) {

        log.info("GET req to fetch site by region with city={}", city);

        List<SiteDto> sites = siteService.fetchSiteByRegion(city);

        log.debug("Fetched sites by region. city={}, count={}", city, sites.size());

        return new ResponseEntity<>(sites, HttpStatus.OK);
    }

    @GetMapping("/fetch-site-by-program/{programId}")
    public ResponseEntity<List<SiteDto>> getAllSitesInProgram(
            @PathVariable(name = "programId", required = true) UUID programId,
            @RequestParam(name = "includeProgram", required = false, defaultValue = "false") boolean includeProgram) {

        log.info("GET req to fetch site by programId={}", programId);

        List<SiteDto> sites = siteService.fetchSiteByProgram(programId, includeProgram);

        log.debug("Fetched sites enrolled in program with programId={}, count={}", programId, sites.size());

        return new ResponseEntity<>(sites, HttpStatus.OK);
    }

    @Operation(summary = "fetch all available regions", description = "retrieve a list of all distinct regions where sites are available")
    @ApiResponse(responseCode = "200", description = "regions fetched successfully")
    @GetMapping("/fetch-all-region")
    public ResponseEntity<List<String>> fethcAllAvailableRegion(
            @RequestParam(name = "includeProgram", required = false, defaultValue = "false") boolean includeProgram) {
        List<String> fetchAllRegion = siteService.fetchAllRegion();
        return new ResponseEntity(fetchAllRegion, HttpStatus.OK);
    }

    // prorgam enroll thayy tyare apde user ne e program ma nakhi daisu
    @Operation(summary = "add program to site", description = "add a program to the site after user enrollment")
    @ApiResponse(responseCode = "200", description = "program added successfully")
    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/{siteId}/add-program")
    public SiteDto addPrograminSite(
            @PathVariable(name = "siteId", required = true) UUID siteId,
            @RequestParam(name = "includeProgram", required = false, defaultValue = "false") boolean includeProgram,
            @RequestBody ProgramFeignDto program) {
        log.info("PUT req to add program = {} in site with siteId = {}", program, siteId);
        return siteService.enrollSiteInProgram(siteId, program);
    }

    // send email if site is created successfully
    @PutMapping("/send-email")
    public ResponseEntity<MailSuccessfullResponseDto> sendMail(@RequestBody MailSuccessfullRequestDto dto) {
        ResponseEntity<MailSuccessfullResponseDto> mailSuccessfullResponseDtoResponseEntity = emailService
                .sendMail(dto);
        return mailSuccessfullResponseDtoResponseEntity;
    }

    // check siteIs exists or not based on siteId
    @PostMapping("/check-site-available/{siteId}")
    public ResponseEntity<Boolean> checkSiteIsAvailableOtNot(
            @PathVariable(name = "siteId", required = true) UUID siteId) {
        Boolean flag = siteService.checkSiteAvailable(siteId);
        if (flag) {
            return new ResponseEntity<>(flag, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(flag, HttpStatus.NOT_FOUND);
        }

    }

    // this is a api to check for all site id given to it in a set and verify their
    // existence and returning the non-valid site id
    @PostMapping("/check-sites-available")
    @ResponseStatus(code = HttpStatus.OK)
    public List<UUID> verifyAllSites(@RequestBody Set<UUID> siteIds) {
        log.info("GET req to verify all site and give non-valid site as return type");
        List<UUID> siteList = siteIds.stream()
                .filter(siteId -> !siteService.checkSiteAvailable(siteId))
                .toList();
        System.out.println(siteList.size());
        return siteList;
    }

    @PostMapping("/sites/batch")
    public Set<SiteDto> getAllSiteDetail(
            @RequestBody List<UUID> siteIds,
            @RequestParam(name = "includeProgram", required = false, defaultValue = "false") boolean includeProgram) {

        System.out.println("printing all siteid received in this controller");
        for (UUID id : siteIds) {
            System.out.println("site id = " + id);
        }
        log.info("POST req to map the site id to their detail and return that siteDetail list");
        return siteService.getAllSiteFromBatch(siteIds, includeProgram);
    }

    @GetMapping("/fetch-all-site-id")
    public List<UUID> getAllSiteIds(){
        final List<UUID> uuids = siteService.fetchAllSiteIds();
        return uuids;
    }

    @PutMapping("/update-meter-status/{siteId}")
    public ResponseEntity<String> updateMeterStatus(@PathVariable("siteId") UUID siteId) {
        siteService.updateMeterStatus(siteId);
        return ResponseEntity.ok("meter status successfully updated");
    }

    @GetMapping("/get-all-siteId-by-meter-status")
    public ResponseEntity<List<UUID>> getAllSiteIdByMeterStatus(
            @RequestParam(name = "flag", required = false, defaultValue = "true") boolean flag) {

        List<UUID> allSiteIdByMeterStatus = siteService.findAllSiteIdByMeterStatus(flag);
        return ResponseEntity.ok(allSiteIdByMeterStatus);
    }


}
