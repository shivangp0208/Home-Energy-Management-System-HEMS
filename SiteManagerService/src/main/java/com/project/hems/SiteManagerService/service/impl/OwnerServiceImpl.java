package com.project.hems.SiteManagerService.service.impl;

import com.project.hems.SiteManagerService.entity.Owner;
import com.project.hems.SiteManagerService.entity.OwnerIdentities;
import com.project.hems.SiteManagerService.exception.ResourceNotFoundException;
import com.project.hems.hems_api_contracts.contract.site.OwnerDto;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OwnerServiceImpl {

     OwnerDto createOwner(Owner owner, String userSub, String email);

     OwnerDto getOwnerDetail(UUID id) ;

     OwnerDto updateOwnerDetail(Owner owner);

     void deleteOwner(UUID ownerId);

     List<OwnerDto> getAllOwnerDetail();
}
