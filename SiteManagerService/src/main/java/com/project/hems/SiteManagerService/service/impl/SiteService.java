package com.project.hems.SiteManagerService.service.impl;

import com.project.hems.SiteManagerService.dto.CursorSiteResponse;
import com.project.hems.hems_api_contracts.contract.program.Program;
import com.project.hems.hems_api_contracts.contract.site.SiteDto;

import org.springframework.data.domain.Page;
import java.util.List;
import java.util.UUID;

public interface SiteService {

     SiteDto createSite(SiteDto siteDto, String userSub);

     SiteDto fetchSiteById(UUID siteId);
     List<SiteDto> fetchAllSite();
     List<SiteDto> fetchAllSiteV2();

    //pagination in fetchAllSiteV2 this api
     Page<SiteDto> findAllSiteV2WithPagination(int offset, int pageSize);

    //pagination in fetchAllSiteV2 this api with sorting based on input key
     Page<SiteDto> findAllSiteV2WithPaginationAndSorting(int offset,int pageSize,String field);

    //cursor based paginaton in fetchAllSiteV2
     CursorSiteResponse<SiteDto> getSites(UUID cursor, int size);


    List<SiteDto> fetchSiteByRegion(String city);

    // EnrollSiteInVppResponse assignSiteToVpp(UUID siteId, AssignVppRequest request);

    SiteDto enrollSiteInProgram(UUID siteId, Program program);

}
