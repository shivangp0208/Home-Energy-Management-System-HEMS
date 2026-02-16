package com.project.hems.SiteManagerService.service.impl;

import com.project.hems.hems_api_contracts.contract.site.OwnerDto;
import java.util.List;
import java.util.UUID;

public interface OwnerServiceImpl {

     OwnerDto createOwner(OwnerDto ownerDto, String userSub, String email);

     OwnerDto getOwnerDetail(UUID id) ;

     OwnerDto updateOwnerDetail(UUID ownerId, OwnerDto ownerDto);

     void deleteOwner(UUID ownerId);

     List<OwnerDto> getAllOwnerDetail();
}
