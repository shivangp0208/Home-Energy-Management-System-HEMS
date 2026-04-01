package com.hems.project.virtual_power_plant.dto;

import lombok.Data;

@Data
public class ImageResponseDto {
    private String fileName;
    private String url;

    public ImageResponseDto(String fileName, String url) {
        this.fileName = fileName;
        this.url = url;
    }

}