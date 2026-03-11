package com.project.hems.program_enrollment_manager;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.boot.test.context.SpringBootTest;

import com.project.hems.hems_api_contracts.contract.EnergyPriority;
import com.project.hems.hems_api_contracts.contract.envoy.BatteryControl;
import com.project.hems.hems_api_contracts.contract.envoy.GridControl;
import com.project.hems.hems_api_contracts.contract.program.Program;
import com.project.hems.hems_api_contracts.contract.program.ProgramFeignDto;
import com.project.hems.hems_api_contracts.contract.program.ProgramStatus;
import com.project.hems.hems_api_contracts.contract.program.ProgramType;
import com.project.hems.hems_api_contracts.contract.simulator.BatteryMode;
import com.project.hems.hems_api_contracts.contract.site.OwnerDto;
import com.project.hems.program_enrollment_manager.entity.ProgramDescEntity;
import com.project.hems.program_enrollment_manager.entity.ProgramEntity;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ProgramEnrollmentManagerApplicationTests {

	private ModelMapper modelMapper;

	@BeforeEach
	void setUp() {
		modelMapper = new ModelMapper();
		modelMapper.getConfiguration().setAmbiguityIgnored(true);
	}

	@Test
	void shouldMapOwnerEntityToFeignDto() {
		ProgramEntity programEntity = new ProgramEntity();
		UUID programId = UUID.randomUUID();

		programEntity.setProgramId(programId);
		programEntity.setProgramName("Green Energy Program");
		programEntity.setStartDate(LocalDate.now());
		programEntity.setEndDate(LocalDate.now().plusDays(30));
		programEntity.setProgramType(ProgramType.EMERGENCY_BACKUP);
		programEntity.setProgramStatus(ProgramStatus.ACTIVE);

		ProgramDescEntity descEntity = new ProgramDescEntity();
		descEntity.setDescriptionId(1l);
		descEntity.setProgram(programEntity);
		descEntity.setLoadEnergyOrder(List.of(EnergyPriority.SOLAR));
		descEntity.setSurplusEnergyOrder(List.of(EnergyPriority.BATTERY));
		descEntity.setGridControl(new GridControl(true, true, 900, 900));
		descEntity.setBatteryControl(new BatteryControl(BatteryMode.AUTO, 900l, 900l, 900l, 89.00, 90.00));
		programEntity.setProgramDescription(descEntity);
		programEntity.setSites(List.of(UUID.randomUUID()));

		ProgramFeignDto dto = modelMapper.map(programEntity, ProgramFeignDto.class);

		assertThat(dto).isNotNull();
		assertThat(dto.getProgramId()).isEqualTo(programId);
		assertThat(dto.getProgramName()).isEqualTo("Green Energy Program");
		assertThat(dto.getStartDate()).isEqualTo(programEntity.getStartDate());
		assertThat(dto.getEndDate()).isEqualTo(programEntity.getEndDate());
		assertThat(dto.getProgramType()).isEqualTo(ProgramType.EMERGENCY_BACKUP);
		assertThat(dto.getProgramStatus()).isEqualTo(ProgramStatus.ACTIVE);

		assertThat(dto.getProgramDescription()).isNotNull();

		assertThat(dto.getProgramPriority()).isNull();
	}

	@Test
	void shouldMapOwnerEntityToDto() {
		ProgramEntity programEntity = new ProgramEntity();
		UUID programId = UUID.randomUUID();

		programEntity.setProgramId(programId);
		programEntity.setProgramName("Green Energy Program");
		programEntity.setStartDate(LocalDate.now());
		programEntity.setEndDate(LocalDate.now().plusDays(30));
		programEntity.setProgramType(ProgramType.EMERGENCY_BACKUP);
		programEntity.setProgramStatus(ProgramStatus.ACTIVE);

		ProgramDescEntity descEntity = new ProgramDescEntity();
		descEntity.setDescriptionId(1l);
		descEntity.setProgram(programEntity);
		descEntity.setLoadEnergyOrder(List.of(EnergyPriority.SOLAR));
		descEntity.setSurplusEnergyOrder(List.of(EnergyPriority.BATTERY));
		descEntity.setGridControl(new GridControl(true, true, 900, 900));
		descEntity.setBatteryControl(new BatteryControl(BatteryMode.AUTO, 900l, 900l, 900l, 89.00, 90.00));
		programEntity.setProgramDescription(descEntity);
		programEntity.setSites(List.of(UUID.randomUUID()));

		Program dto = modelMapper.map(programEntity, Program.class);

		assertThat(dto).isNotNull();
		assertThat(dto.getProgramId()).isEqualTo(programId);
		assertThat(dto.getProgramName()).isEqualTo("Green Energy Program");
		assertThat(dto.getStartDate()).isEqualTo(programEntity.getStartDate());
		assertThat(dto.getEndDate()).isEqualTo(programEntity.getEndDate());
		assertThat(dto.getProgramType()).isEqualTo(ProgramType.EMERGENCY_BACKUP);
		assertThat(dto.getProgramStatus()).isEqualTo(ProgramStatus.ACTIVE);

		assertThat(dto.getProgramDescription()).isNotNull();

		assertThat(dto.getProgramPriority()).isNull();
	}

}
