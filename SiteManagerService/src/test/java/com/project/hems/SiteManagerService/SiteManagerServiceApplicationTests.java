package com.project.hems.SiteManagerService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.boot.test.context.SpringBootTest;

import com.project.hems.SiteManagerService.entity.Owner;
import com.project.hems.SiteManagerService.entity.Site;
import com.project.hems.hems_api_contracts.contract.site.OwnerDto;
import com.project.hems.hems_api_contracts.contract.site.SiteDto;
import com.project.hems.hems_api_contracts.contract.site.SolarDto;

import java.util.Arrays;
import java.util.UUID;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class SiteManagerServiceApplicationTests {
	private ModelMapper modelMapper;

	@BeforeEach
	void setUp() {
		modelMapper = new ModelMapper();
		// Crucial for circular references
		modelMapper.getConfiguration().setAmbiguityIgnored(true);
	}

	@Test
	void shouldMapOwnerEntityToDto() {
		// 1. Arrange: Create Entity with nested data
		Owner owner = new Owner();
		owner.setOwnerId(UUID.randomUUID());
		owner.setOwnerName("John Doe");
		owner.setEmail("john@example.com");

		Site site = new Site();
		site.setSiteId(UUID.randomUUID());
		site.setActive(true);
		site.setOwner(owner); // Circular ref

		owner.setSites(Arrays.asList(site));

		// 2. Act: Map to DTO
		OwnerDto dto = modelMapper.map(owner, OwnerDto.class);

		// 3. Assert: Verify values
		assertThat(dto.getOwnerId()).isEqualTo(owner.getOwnerId());
		assertThat(dto.getOwnerName()).isEqualTo(owner.getOwnerName());
		assertThat(dto.getSites()).hasSize(1);
		assertThat(dto.getSites().get(0).getSiteId()).isEqualTo(site.getSiteId());
	}

	@Test
	void shouldMapSiteDtoToEntity() {
		// 1. Arrange: Create DTO
		SiteDto dto = new SiteDto();
		dto.setSiteId(UUID.randomUUID());
		dto.setActive(true);

		SolarDto solarDto = new SolarDto();
		solarDto.setTotalPanelCapacityW(5000.0);
		dto.setSolar(Arrays.asList(solarDto));

		// 2. Act: Map to Entity
		Site entity = modelMapper.map(dto, Site.class);

		// 3. Assert
		assertThat(entity.getSiteId()).isEqualTo(dto.getSiteId());
		assertThat(entity.isActive()).isTrue();
		assertThat(entity.getSolar()).hasSize(1);
		assertThat(entity.getSolar().get(0).getTotalPanelCapacityW()).isEqualTo(5000.0);
	}
}
