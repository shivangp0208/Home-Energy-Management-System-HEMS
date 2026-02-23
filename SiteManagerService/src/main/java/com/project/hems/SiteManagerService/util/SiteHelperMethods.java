package com.project.hems.SiteManagerService.util;

import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import com.project.hems.SiteManagerService.entity.Site;
import com.project.hems.SiteManagerService.exception.ResourceNotFoundException;
import com.project.hems.SiteManagerService.external.ProgramFeignClientService;
import com.project.hems.SiteManagerService.repository.SiteRepo;
import com.project.hems.hems_api_contracts.contract.site.SiteDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class SiteHelperMethods {

    private final ModelMapper mapper;
    private final ProgramFeignClientService programFeignClientService;
    private final SiteRepo siteRepo;

    public SiteDto getSiteByEntity(Site entity, boolean includeProgram) {
        log.debug("getSiteByEntity: fetching site dto using site entity");
        SiteDto siteDto = mapper.map(entity, SiteDto.class);
        if (includeProgram) {
            log.debug("getSiteByEntity: includeProgram is " + includeProgram
                    + " so fetching all program detials from feign client");
            siteDto.setEnrollProgramIds(
                    programFeignClientService.getAllProgramBySiteId(false, entity.getSiteId()));
        }

        return siteDto;
    }

    public SiteDto getSiteBySiteId(UUID siteId, boolean includeProgram) {
        log.debug("getSiteBySiteId: fetching site dto using site id");
        Site site = siteRepo.findById(siteId)
                .orElseThrow(() -> {
                    log.error("getSiteBySiteId: Site not found, siteId={}", siteId);
                    return new ResourceNotFoundException(
                            "site is not found with site id :- " + siteId);
                });

        SiteDto siteDto = mapper.map(site, SiteDto.class);
        log.debug("getSiteBySiteId: success mapping the site entity to site dto");
        if (includeProgram) {
            log.debug("getSiteByEntity: includeProgram is " + includeProgram
                    + " so fetching all program detials from feign client");
            siteDto.setEnrollProgramIds(programFeignClientService.getAllProgramBySiteId(false,
                    siteId));
        }

        return siteDto;
    }
}
