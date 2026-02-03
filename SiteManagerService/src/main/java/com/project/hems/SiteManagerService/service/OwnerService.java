package com.project.hems.SiteManagerService.service;

import com.project.hems.SiteManagerService.dto.OwnerDto;
import com.project.hems.SiteManagerService.entity.Owner;
import com.project.hems.SiteManagerService.entity.OwnerIdentities;
import com.project.hems.SiteManagerService.exception.ResourceNotFoundException;
import com.project.hems.SiteManagerService.repository.OwnerIdentityRepo;
import com.project.hems.SiteManagerService.repository.OwnerRepo;
import com.project.hems.SiteManagerService.repository.SiteRepo;
import com.project.hems.SiteManagerService.util.ValueMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Slf4j
public class OwnerService {

    private final OwnerRepo ownerRepo;
    private final SiteRepo siteRepo;
    private final ValueMapper valueMapper;
    private final OwnerIdentityRepo ownerIdentityRepo;

    @Transactional
    public OwnerDto createOwner(Owner owner, String userSub, String email) {
        log.info("createOwner: Creating owner for email={} with authSub={}", email, userSub);

        // extract provider
        String sub = userSub;// auth0|695781256ccfb90819e1df58
        String[] parts = sub.split("\\|");// [auth0,695781256ccfb90819e1df58]
        String provider = parts[0];
        log.info("owner request provider = {} and sub = {}",provider,sub);
        
        Owner savedOwner = ownerRepo.findByEmail(email).orElseGet(() -> {
            log.info("createOwner: Owner not found with email={}, creating new owner", email);
            owner.setEmail(email);
            return ownerRepo.save(owner);
        });


    
        if (!ownerIdentityRepo.existsByAuthSub(userSub)) {
            log.info("createOwner: Linking auth identity to ownerId={}, provider={}", savedOwner.getId(), provider);

            OwnerIdentities ownerIdentities = OwnerIdentities.builder()
                    .authSub(userSub)
                    .provider(provider)
                    .owner(savedOwner)
                    .build();
            ownerIdentityRepo.save(ownerIdentities);
        } else {
            log.debug("createOwner: Auth identity already exists for authSub={}", userSub);
        }

        log.info("createOwner: Owner creation completed for ownerId={}", savedOwner.getId());
        return valueMapper.ownerModelToDto(savedOwner);
    }

    public OwnerDto getOwnerDetail(UUID id) {
        log.info("getOwnerDetail: Fetching owner details for ownerId={}", id);

        Owner owner = ownerRepo.findById(id)
                .orElseThrow(() -> {
                    log.warn("getOwnerDetail: Owner not found for ownerId={}", id);
                    return new ResourceNotFoundException("getOwnerDetail: owner details not found for this id : " + id);
                });

        OwnerDto ownerDto = valueMapper.ownerModelToDto(owner);
        return ownerDto;
    }

    public OwnerDto updateOwnerDetail(Owner owner) {
        log.info("updateOwnerDetail: Updating owner details for ownerId={}", owner.getId());

        Owner existingOwner = ownerRepo.findById(owner.getId())
                .orElseThrow(() -> {
                    log.warn("updateOwnerDetail: Owner not found while updating ownerId={}", owner.getId());
                    return new ResourceNotFoundException("updateOwnerDetail: owner is not found for this is : " + owner.getId());
                });

        Optional.ofNullable(owner.getEmail()).ifPresent(existingOwner::setEmail);
        Optional.ofNullable(owner.getOwnerName()).ifPresent(existingOwner::setOwnerName);
        Optional.ofNullable(owner.getSites()).ifPresent(existingOwner::setSites);
        Optional.ofNullable(owner.getPhoneNo()).ifPresent(existingOwner::setPhoneNo);
        ownerRepo.save(existingOwner);

        log.debug("complete update owner details ownerId={} ",owner.getId());
        OwnerDto existingOwnerDto = valueMapper.ownerModelToDto(existingOwner);

        ownerRepo.save(existingOwner);
        log.debug("updateOwnerDetail: Owner updated successfully for ownerId={}", existingOwner.getId());

        return existingOwnerDto;
    }

    @Transactional
    public void deleteOwner(UUID ownerId) {
        log.info("deleteOwner: start deleting owner and ownerId ={}",ownerId);
        
        log.debug("deleteOwner: fetch owner detail ownerId ={}",ownerId);
        Owner owner = ownerRepo.findById(ownerId)
                .orElseThrow(() -> {
                    log.warn("deleteOwner: Owner not found while deleting ownerId={}", ownerId);
                    return new ResourceNotFoundException("deleteOwner: owner details not found for this id : " + ownerId);
                });

        siteRepo.deleteByOwner(owner);// because owner parent table che and site child table che so apde direct owner
        // delete kasu without deeting ownerSite so error apse ke first child table
        // mathi remove karo

        ownerRepo.deleteById(ownerId);
        log.info("deleteOwner: Owner deleted successfully for ownerId={}", ownerId);
    }

    public List<OwnerDto> getAllOwnerDetail() {
        log.info("getAllOwnerDetail: Fetching all owner details");

        List<Owner> allOwner = ownerRepo.findAll();
        log.info("convert owner Model to ownerDto");
        List<OwnerDto> allOwnerDto = allOwner.stream()
                .map(owner -> {
                    OwnerDto ownerDto = valueMapper.ownerModelToDto(owner);
                    return ownerDto;
                }).toList();

        log.debug("getAllOwnerDetail: Disptach from thared :- {}", Thread.currentThread());
        return allOwnerDto;
    }
}
