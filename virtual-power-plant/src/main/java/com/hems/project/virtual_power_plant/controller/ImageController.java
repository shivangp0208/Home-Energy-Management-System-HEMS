package com.hems.project.virtual_power_plant.controller;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.hems.project.virtual_power_plant.dto.ImageResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.hems.project.virtual_power_plant.service.SupabaseStorageService;

import lombok.RequiredArgsConstructor;

@Slf4j
@Tag(name = "image controller")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/images")
public class ImageController {

    private final SupabaseStorageService supabaseStorageService;

    @PreAuthorize("hasAuthority('vpp:write')")
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
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body("File is empty");
            }

            if (file.getContentType() == null || !file.getContentType().startsWith("image/")) {
                return ResponseEntity.badRequest().body("Only image files allowed");
            }

            if (file.getSize() > 5 * 1024 * 1024) {
                return ResponseEntity.badRequest().body("File too large");
            }
            String imageUrl = supabaseStorageService.uploadImage(vppId,file);
            return ResponseEntity.ok(Map.of(
                    "imageUrl", imageUrl
            ));
        }catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "error", "Image upload failed",
                            "message", e.getMessage()
                    ));
        }
    }


    @PreAuthorize("hasAuthority('vpp:read')")
    @Operation(
            summary = "get image url",
            description = "get the public url of a specific image for a vpp"
    )
    @ApiResponse(responseCode = "200", description = "image url fetched successfully")
    @ApiResponse(responseCode = "500", description = "internal server error")
    @GetMapping("/vpp/{vppId}")
    public ResponseEntity<Map<String, String>> getVppImage(
            @PathVariable UUID vppId,
            @RequestParam String fileName) {

        log.info("fetching image for vppId {} with fileName {}", vppId, fileName);

        try {
            String imageUrl = supabaseStorageService.getPublicImageUrl(vppId, fileName);

            log.info("image fetched successfully for vppId {}", vppId);

            return ResponseEntity.ok(Map.of("imageUrl", imageUrl));

        } catch (Exception e) {
            log.error("error fetching image for vppId {}: {}", vppId, e.getMessage(), e);
            throw e;
        }
    }

    @Operation(
            summary = "get all images",
            description = "get all image urls associated with a specific vpp"
    )
    @ApiResponse(responseCode = "200", description = "all images fetched successfully")
    @ApiResponse(responseCode = "500", description = "internal server error")
    @GetMapping("/vpp/{vppId}/all")
    public ResponseEntity<List<ImageResponseDto>> getAllVppImages(
            @PathVariable UUID vppId) {

        log.info("fetching all images for vppId {}", vppId);

        try {
            List<ImageResponseDto> images = supabaseStorageService.getAllImagesFromVppId(vppId);

            log.info("fetched {} images for vppId {}", images.size(), vppId);

            return ResponseEntity.ok(images);

        } catch (Exception e) {
            log.error("error fetching images for vppId {}: {}", vppId, e.getMessage(), e);
            throw e;
        }
    }
    
    

    
}
