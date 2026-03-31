package com.hems.project.Virtual_Power_Plant.dto;

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