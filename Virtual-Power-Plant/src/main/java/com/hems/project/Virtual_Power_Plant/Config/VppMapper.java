package com.hems.project.Virtual_Power_Plant.Config;

import com.hems.project.Virtual_Power_Plant.dto.VppUpdateRequestDto;
import com.hems.project.Virtual_Power_Plant.dto.VppUpdateResponseDto;
import com.hems.project.Virtual_Power_Plant.entity.Vpp;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface VppMapper {

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateVppFromDto(VppUpdateRequestDto dto, @MappingTarget Vpp entity);

    VppUpdateResponseDto toDto(Vpp entity);

}

/*
 note:-
 mapStruct is a compile time code generator so insted of wrote mnaull line we use MapStruct
 @Mapper(componentModel = "spring")
This tells MapStruct:
“Generate an implementation of this interface”
componentModel = "spring" → Register it as a Spring Bean

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
 and this means apde avu karta ke Optional.ofNullable(check karta che ).ifPresent(then set)
 so aa e j che ke null hoy dto ma user apde toh ene entity ma set na karta agad vadhi jajo

 @MappingTarget aa navi nai banave likde Vpp entity=new Vpp() am nai kare je fetch kareli hase
 apde databas mathi ema j change karse direct..


 */
