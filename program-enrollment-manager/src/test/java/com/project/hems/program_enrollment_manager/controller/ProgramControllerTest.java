package com.project.hems.program_enrollment_manager.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.hems.hems_api_contracts.contract.EnergyPriority;
import com.project.hems.hems_api_contracts.contract.envoy.BatteryControl;
import com.project.hems.hems_api_contracts.contract.envoy.GridControl;
import com.project.hems.hems_api_contracts.contract.program.Program;
import com.project.hems.hems_api_contracts.contract.program.ProgramDescription;
import com.project.hems.hems_api_contracts.contract.program.ProgramPriority;
import com.project.hems.hems_api_contracts.contract.program.ProgramStatus;
import com.project.hems.program_enrollment_manager.entity.ProgramDescEntity;
import com.project.hems.program_enrollment_manager.entity.ProgramEntity;
import com.project.hems.program_enrollment_manager.external.SiteFeignClientService;
import com.project.hems.program_enrollment_manager.repository.ProgramRepository;
import com.project.hems.program_enrollment_manager.service.ProgramService;
import com.project.hems.program_enrollment_manager.service.ProgramServiceImpl;
import com.project.hems.program_enrollment_manager.util.ProgramHelperMethods;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
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