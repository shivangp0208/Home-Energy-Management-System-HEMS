package com.hems.project.virtual_power_plant;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.boot.test.context.SpringBootTest;

import com.project.hems.hems_api_contracts.contract.site.OwnerDto;
import com.project.hems.hems_api_contracts.contract.site.SiteDto;

@SpringBootTest
class VirtualPowerPlantApplicationTests {

    private ModelMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new ModelMapper();
        // Crucial for circular references
        mapper.getConfiguration().setAmbiguityIgnored(true);
    }

    @Test
    public void checkUUIDtoSiteDto() {
        UUID id = UUID.randomUUID();
        SiteDto siteDto = mapper.map(id, SiteDto.class);
        assertEquals(id, siteDto.getSiteId());
    }

    @Test
    public void checkSiteDtotoUUID() {
        SiteDto siteDto = new SiteDto();
        siteDto.setSiteId(UUID.randomUUID());
        UUID siteId = mapper.map(siteDto, UUID.class);
        assertEquals(siteDto.getSiteId(), siteId);
    }
}
