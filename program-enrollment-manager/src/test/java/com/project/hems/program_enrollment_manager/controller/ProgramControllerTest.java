package com.project.hems.program_enrollment_manager.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.hems.hems_api_contracts.contract.EnergyPriority;
import com.project.hems.hems_api_contracts.contract.envoy.BatteryControl;
import com.project.hems.hems_api_contracts.contract.envoy.GridControl;
import com.project.hems.hems_api_contracts.contract.program.ProgramStatus;
import com.project.hems.hems_api_contracts.contract.program.ProgramType;
import com.project.hems.program_enrollment_manager.entity.ProgramDescEntity;
import com.project.hems.program_enrollment_manager.entity.ProgramEntity;
import com.project.hems.program_enrollment_manager.entity.SiteProgramEnrollmentEntity;
import com.project.hems.program_enrollment_manager.exception.ProgramStateConflictException;
import com.project.hems.program_enrollment_manager.repository.ProgramRepository;
import com.project.hems.program_enrollment_manager.repository.SiteProgramEnrollmentRepo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ProgramControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mvc;

    @MockBean
    private ProgramRepository programRepository;

    @MockBean
    private SiteProgramEnrollmentRepo siteProgramEnrollmentRepo;

    @Test
    void getAllPrograms() {
    }

    @Test
    void getOneProgram() throws Exception {
        UUID programId = UUID.randomUUID();
        ProgramEntity p1 = new ProgramEntity();
        p1.setProgramId(programId);
        p1.setProgramStatus(ProgramStatus.ACTIVE);
        p1.setStartDate(LocalDate.now().plusDays(20));
        p1.setEndDate(LocalDate.now().plusMonths(2));
        ProgramDescEntity p1Desc = new ProgramDescEntity();
        p1Desc.setDescriptionId(101L);
        p1Desc.setProgram(p1);
        p1Desc.setBatteryControl(BatteryControl.builder().build());
        p1Desc.setGridControl(GridControl.builder().build());
        p1Desc.setLoadEnergyOrder(List.of(EnergyPriority.SOLAR, EnergyPriority.BATTERY));
        p1Desc.setSurplusEnergyOrder(List.of(EnergyPriority.BATTERY));
        p1.setProgramDescription(p1Desc);

        when(programRepository.findById(programId)).thenReturn(Optional.of(p1));

        mvc.perform(get("/api/v1/program/programs/" + programId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.programId").value(programId.toString()));
    }

    @Test
    void createNewProgram() throws Exception {
        UUID programId = UUID.randomUUID();
        ProgramEntity p1 = new ProgramEntity();
        p1.setProgramStatus(ProgramStatus.ACTIVE);
        p1.setStartDate(LocalDate.now().plusDays(20));
        p1.setEndDate(LocalDate.now().plusMonths(2));
        ProgramDescEntity p1Desc = new ProgramDescEntity();
        p1Desc.setDescriptionId(101L);
        p1Desc.setProgram(p1);
        p1Desc.setBatteryControl(BatteryControl.builder().build());
        p1Desc.setGridControl(GridControl.builder().build());
        p1Desc.setLoadEnergyOrder(List.of(EnergyPriority.SOLAR, EnergyPriority.BATTERY));
        p1Desc.setSurplusEnergyOrder(List.of(EnergyPriority.BATTERY));
        p1.setProgramDescription(p1Desc);

        p1.setProgramId(programId);
        when(programRepository.save(any())).thenReturn(p1);

        mvc.perform(post("/api/v1/program//create-program")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(p1)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.programId").value(programId.toString()));
    }

    @Test
    void updateProgram() {
    }

    @Test
    void enrollSiteinProgram() {
        ProgramEntity programEntity = new ProgramEntity();
        programEntity.setProgramId(UUID.randomUUID());
        programEntity.setProgramName("Test Program 1");
        programEntity.setStartDate(LocalDate.of(2026, 3, 11));
        programEntity.setEndDate(LocalDate.of(2026, 8, 11));
        programEntity.setProgramType(ProgramType.EMERGENCY_BACKUP);
        ProgramDescEntity programDescEntity = new ProgramDescEntity();
        programDescEntity.setDescriptionId(101L);
        programDescEntity.setProgram(programEntity);
        programDescEntity.setBatteryControl(BatteryControl.builder().build());
        programDescEntity.setGridControl(GridControl.builder().build());
        programDescEntity.setLoadEnergyOrder(List.of(EnergyPriority.GRID, EnergyPriority.SOLAR));
        programDescEntity.setSurplusEnergyOrder(List.of(EnergyPriority.BATTERY));
        programEntity.setProgramDescription(programDescEntity);

        ProgramEntity programEntity2 = new ProgramEntity();
        programEntity2.setProgramId(UUID.randomUUID());
        programEntity2.setProgramName("Test Program 2");
        programEntity2.setStartDate(LocalDate.of(2026, 8, 10));
        programEntity2.setEndDate(LocalDate.of(2026, 12, 12));
        programEntity2.setProgramType(ProgramType.EMERGENCY_BACKUP);
        ProgramDescEntity programDescEntity2 = new ProgramDescEntity();
        programDescEntity2.setDescriptionId(102L);
        programDescEntity2.setProgram(programEntity2);
        programDescEntity2.setBatteryControl(BatteryControl.builder().build());
        programDescEntity2.setGridControl(GridControl.builder().build());
        programDescEntity2.setLoadEnergyOrder(List.of(EnergyPriority.GRID, EnergyPriority.SOLAR));
        programDescEntity2.setSurplusEnergyOrder(List.of(EnergyPriority.BATTERY));
        programEntity2.setProgramDescription(programDescEntity2);

        UUID siteId = UUID.randomUUID();

        SiteProgramEnrollmentEntity siteProgramEnrollmentEntity = new SiteProgramEnrollmentEntity();
        siteProgramEnrollmentEntity.setEnrollmentId(UUID.randomUUID());
        siteProgramEnrollmentEntity.setSite(siteId);
        siteProgramEnrollmentEntity.setEnrollmentTime(LocalDateTime.of(LocalDate.of(2026, 04, 12), LocalTime.now()));
        siteProgramEnrollmentEntity.setProgram(programEntity);

        List<SiteProgramEnrollmentEntity> stubbedEnrollment = List.of(siteProgramEnrollmentEntity);

        when(programRepository.findById(any())).thenReturn(Optional.of(programEntity));
        when(siteProgramEnrollmentRepo.findBySite(siteId)).thenReturn(stubbedEnrollment);

        MultiValueMap<String, String> reqParams = new LinkedMultiValueMap<>();
        reqParams.add("siteId", siteId.toString());
        reqParams.add("programId", programEntity2.getProgramId().toString());

        try {
            mvc.perform(post("/api/v1/program/enroll-site-in-program").params(reqParams)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isConflict())
                    .andExpect(result -> assertTrue(
                            result.getResolvedException() instanceof ProgramStateConflictException));
        } catch (Exception e) {
            System.out.println("Exception occurent dueing testing of enrollSiteinProgram controller" + e.getMessage());
        }

    }

    @Test
    void activateProgram() {
    }

    @Test
    void deactivateProgram() {
    }

    @Test
    void getAllProgramBySiteId() {
    }
}