package com.project.hems.site_manager_service.service;

import com.project.hems.site_manager_service.dto.CursorSiteResponse;
import com.project.hems.hems_api_contracts.contract.program.ProgramFeignDto;
import com.project.hems.hems_api_contracts.contract.site.SiteDto;
import com.project.hems.hems_api_contracts.contract.site.SiteReqDto;

import org.springframework.data.domain.Page;
import java.util.List;
import java.util.UUID;

public interface SiteService {

    SiteDto createSite(SiteReqDto siteDto, String userSub);

    SiteDto fetchSiteById(UUID siteId, boolean includeProgram);

    List<SiteDto> fetchAllSite();

    List<SiteDto> fetchAllSiteV2();

    // pagination in fetchAllSiteV2 this api
    Page<SiteDto> findAllSiteV2WithPagination(int offset, int pageSize);

    // pagination in fetchAllSiteV2 this api with sorting based on input key
    Page<SiteDto> findAllSiteV2WithPaginationAndSorting(int offset, int pageSize, String field);

    // cursor based paginaton in fetchAllSiteV2
    CursorSiteResponse<SiteDto> getSites(UUID cursor, int size);

    List<SiteDto> fetchSiteByRegion(String city);

    // EnrollSiteInVppResponse assignSiteToVpp(UUID siteId, AssignVppRequest
    // request);

    SiteDto enrollSiteInProgram(UUID siteId, ProgramFeignDto program);

    Boolean checkSiteAvailable(UUID siteId);

    List<UUID> fetchAllSiteIds();
}
