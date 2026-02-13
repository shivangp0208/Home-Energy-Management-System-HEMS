package com.project.hems.SiteManagerService.service;

import com.project.hems.SiteManagerService.entity.Owner;
import com.project.hems.SiteManagerService.entity.OwnerIdentities;
import com.project.hems.SiteManagerService.exception.ResourceNotFoundException;
import com.project.hems.SiteManagerService.repository.OwnerIdentityRepo;
import com.project.hems.SiteManagerService.repository.OwnerRepo;
import com.project.hems.SiteManagerService.repository.SiteRepo;
import com.project.hems.hems_api_contracts.contract.site.OwnerDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class OwnerService {

    private final OwnerRepo ownerRepo;
    private final SiteRepo siteRepo;
    private final OwnerIdentityRepo ownerIdentityRepo;
    private final ModelMapper mapper;

    @Transactional
    public OwnerDto createOwner(OwnerDto ownerDto, String userSub, String email) {
        log.info("createOwner: Creating owner for email={} with authSub={}", email, userSub);

        // 1. Safe extraction of provider
        String sub = userSub; // e.g., auth0|695781256ccfb90819e1df58
        String provider = "";
        if (sub != null && sub.contains("|")) {
            String[] parts = sub.split("\\|");
            provider = parts[0];
        }
        log.info("owner request provider = {} and sub = {}", provider, sub);

        // 2. Get or Create Owner
        Owner savedOwner = ownerRepo.findByEmail(email).orElseGet(() -> {
            log.info("createOwner: Owner not found with email={}, creating new owner", email);
            // Map DTO to Entity and save
            Owner newOwner = mapper.map(ownerDto, Owner.class);
            return ownerRepo.save(newOwner);
        });

        // 3. Link Identity if it doesn't exist
        if (!ownerIdentityRepo.existsByAuthSub(userSub)) {
            log.info("createOwner: Linking auth identity to ownerId={}, provider={}",
                    savedOwner.getOwnerId(), provider);

            OwnerIdentities newIdentity = new OwnerIdentities();
            newIdentity.setAuthSub(userSub);
            newIdentity.setProvider(provider);
            newIdentity.setOwner(savedOwner); // Directly link the managed entity

            // Synchronize bidirectional relationship so the DTO return has the data
            savedOwner.getOwnerIdentities().add(newIdentity);

            ownerIdentityRepo.save(newIdentity);
        } else {
            log.debug("createOwner: Auth identity already exists for authSub={}", userSub);
        }

        log.info("createOwner: Owner creation completed for ownerId={}", savedOwner.getOwnerId());

        // 4. Return the fully populated DTO
        return mapper.map(savedOwner, OwnerDto.class);
    }

    public OwnerDto getOwnerDetail(UUID id) {
        log.info("getOwnerDetail: Fetching owner details for ownerId={}", id);

        Owner owner = ownerRepo.findById(id)
                .orElseThrow(() -> {
                    log.warn("getOwnerDetail: Owner not found for ownerId={}", id);
                    return new ResourceNotFoundException("getOwnerDetail: owner details not found for this id : " + id);
                });

        return mapper.map(owner, OwnerDto.class);
    }

    public OwnerDto updateOwnerDetail(UUID ownerId, OwnerDto ownerDto) {
        log.info("updateOwnerDetail: Updating owner details for ownerId={}", ownerDto.getOwnerId());

        Owner existingOwner = ownerRepo.findById(ownerId)
                .orElseThrow(() -> {
                    log.warn("updateOwnerDetail: Owner not found while updating ownerId={}", ownerDto.getOwnerId());
                    return new ResourceNotFoundException(
                            "updateOwnerDetail: owner is not found for this is : " + ownerDto.getOwnerId());
                });

        existingOwner.setOwnerName(ownerDto.getOwnerName());
        existingOwner.setEmail(ownerDto.getEmail());
        existingOwner.setPhoneNo(ownerDto.getPhoneNo());

        existingOwner.getSites().forEach(site -> site.setOwner(existingOwner));
        Owner savedUpdatedOwner = ownerRepo.save(existingOwner);

        log.debug("complete update owner details ownerId={} ", ownerDto.getOwnerId());
        log.debug("updateOwnerDetail: Owner updated successfully for ownerId={}", existingOwner.getOwnerId());
        OwnerDto updatedOwner = mapper.map(savedUpdatedOwner, OwnerDto.class);

        return updatedOwner;
    }

    @Transactional
    public void deleteOwner(UUID ownerId) {
        log.info("deleteOwner: start deleting owner and ownerId ={}", ownerId);

        log.debug("deleteOwner: fetch owner detail ownerId ={}", ownerId);
        Owner owner = ownerRepo.findById(ownerId)
                .orElseThrow(() -> {
                    log.warn("deleteOwner: Owner not found while deleting ownerId={}", ownerId);
                    return new ResourceNotFoundException(
                            "deleteOwner: owner details not found for this id : " + ownerId);
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
                    OwnerDto ownerDto = mapper.map(owner, OwnerDto.class);
                    return ownerDto;
                }).toList();

        log.debug("getAllOwnerDetail: Disptach from thared :- {}", Thread.currentThread());
        return allOwnerDto;
    }
}
