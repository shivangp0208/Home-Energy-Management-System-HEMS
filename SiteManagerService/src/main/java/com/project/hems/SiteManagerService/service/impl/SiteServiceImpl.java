package com.project.hems.SiteManagerService.service.impl;

import com.project.hems.SiteManagerService.dto.AssignVppRequest;
import com.project.hems.SiteManagerService.dto.CursorSiteResponse;
import com.project.hems.SiteManagerService.dto.EnrollSiteInVppResponse;
import com.project.hems.SiteManagerService.dto.SiteRequestDto;
import com.project.hems.SiteManagerService.entity.*;
import com.project.hems.SiteManagerService.exception.ResourceNotFoundException;
import com.project.hems.hems_api_contracts.contract.program.AddProgramConfigInSite;
import com.project.hems.hems_api_contracts.contract.site.SiteCreationEvent;
import com.project.hems.hems_api_contracts.contract.site.SiteResponseDto;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Async;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface SiteServiceImpl {

     Site createSite(SiteRequestDto dto, String userSub);

     Site fetchSiteById(UUID siteId);
     List<Site> fetchAllSite();
     List<SiteResponseDto> fetchAllSiteV2();

    //pagination in fetchAllSiteV2 this api
     Page<SiteResponseDto> findAllSiteV2WithPagination(int offset, int pageSize);

    //pagination in fetchAllSiteV2 this api with sorting based on input key
     Page<SiteResponseDto> findAllSiteV2WithPaginationAndSorting(int offset,int pageSize,String field);

    //cursor based paginaton in fetchAllSiteV2
     CursorSiteResponse<SiteResponseDto> getSites(UUID cursor, int size);


    List<SiteResponseDto> fetchSiteByRegion(String city);

    EnrollSiteInVppResponse assignSiteToVpp(UUID siteId, AssignVppRequest request);

    Map<UUID,String> addProgramDetailInSite(UUID siteId, AddProgramConfigInSite dto);

}
