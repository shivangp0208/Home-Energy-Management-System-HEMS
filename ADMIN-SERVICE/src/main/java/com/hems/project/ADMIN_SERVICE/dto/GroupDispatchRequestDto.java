package com.hems.project.ADMIN_SERVICE.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class GroupDispatchRequestDto {

    @NotNull
    private LocalDateTime scheduleTime;

    @NotNull
    private DispatchMode eventMode;

    @NotNull
    @PositiveOrZero
    private Long targetPowerW;

    @NotNull
    @Min(0)
    @Max(100)
    private Integer targetSoc;

    @NotNull
    private UUID groupId;
}