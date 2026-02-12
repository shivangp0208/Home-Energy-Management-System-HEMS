package com.hems.project.Virtual_Power_Plant.controller;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.hems.project.Virtual_Power_Plant.service.SupabaseStorageService;

import lombok.RequiredArgsConstructor;

@Tag(name = "image controller")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/images")
public class ImageController {

    private final SupabaseStorageService supabaseStorageService;

    @Operation(
            summary = "upload image",
            description = "upload an image file for a specific vpp"
    )
    @ApiResponse(responseCode = "200", description = "image uploaded successfully")
    @ApiResponse(responseCode = "500", description = "internal server error")
    @PostMapping("/upload/{vppId}")
    public ResponseEntity<?> uploadImage(
            @PathVariable("vppId") UUID vppId,
            @RequestParam("file") MultipartFile file
    ) {
        try {
            String imageUrl = supabaseStorageService.uploadImage(vppId,file);
            return ResponseEntity.ok(Map.of(
                    "imageUrl", imageUrl
            ));
        }catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.getMessage());
        }
    }


    @Operation(
            summary = "get image url",
            description = "get the public url of a specific image for a vpp"
    )
    @ApiResponse(responseCode = "200", description = "image url fetched successfully")
    @GetMapping("/vpp/{vppId}")
    public ResponseEntity<?> getVppImage(
            @PathVariable("vppId") UUID vppId,
            @RequestParam("fileName") String fileName
    ) {
        String imageUrl = supabaseStorageService.getPublicImageUrl(vppId, fileName);

        return ResponseEntity.ok(
                Map.of("imageUrl", imageUrl)
        );
    }

    @Operation(
            summary = "get all images",
            description = "get all image urls associated with a specific vpp"
    )
    @ApiResponse(responseCode = "200", description = "all images fetched successfully")
   @GetMapping("/vpp/{vppId}/all")
    public ResponseEntity<?> getAllVppImages(
        @PathVariable("vppId") UUID vppId
) {
    return ResponseEntity.ok(
            Map.of(
                    "images",
                    supabaseStorageService.getAllImagesFromVppId(vppId)
            )
    );
}
    
    

    
}
