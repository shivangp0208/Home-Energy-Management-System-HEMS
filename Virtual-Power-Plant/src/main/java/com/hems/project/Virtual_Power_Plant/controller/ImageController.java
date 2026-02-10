package com.hems.project.Virtual_Power_Plant.controller;

import java.util.List;
import java.util.Map;
import java.util.UUID;

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

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/images")
public class ImageController {

    private final SupabaseStorageService supabaseStorageService;

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
            e.printStackTrace(); // 👈 ADD THIS
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.getMessage());
        }
    }


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
